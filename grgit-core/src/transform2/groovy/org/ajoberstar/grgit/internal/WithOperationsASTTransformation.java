package org.ajoberstar.grgit.internal;

import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import groovy.lang.Closure;
import org.ajoberstar.grgit.Configurable;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.AnnotatedNode;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.GenericsType;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.ArrayExpression;
import org.codehaus.groovy.ast.expr.ClassExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.FieldExpression;
import org.codehaus.groovy.ast.expr.StaticMethodCallExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.transform.AbstractASTTransformation;
import org.codehaus.groovy.transform.GroovyASTTransformation;

@GroovyASTTransformation(phase = CompilePhase.CANONICALIZATION)
public class WithOperationsASTTransformation extends AbstractASTTransformation {

  @Override
  public void visit(ASTNode[] nodes, SourceUnit source) {
    AnnotationNode annotation = (AnnotationNode) nodes[0];
    AnnotatedNode parent = (AnnotatedNode) nodes[1];

    if (parent instanceof ClassNode) {
      ClassNode clazz = (ClassNode) parent;
      List<ClassNode> staticOps = getClassList(annotation, "staticOperations");
      List<ClassNode> instanceOps = getClassList(annotation, "instanceOperations");

      staticOps.forEach(op -> makeMethods(clazz, op, true));
      instanceOps.forEach(op -> makeMethods(clazz, op, false));
    }
  }

  private void makeMethods(ClassNode targetClass, ClassNode opClass, boolean isStatic) {
    AnnotationNode annotation = opClass.getAnnotations(classFromType(Operation.class)).stream()
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Class is not annotated with @Operation: " + opClass));
    String opName = getMemberStringValue(annotation, "value");
    ClassNode opReturn = opClass.getDeclaredMethod("call", new Parameter[] {}).getReturnType();

    targetClass.addMethod(makeNoArgMethod(targetClass, opName, opClass, opReturn, isStatic));
    targetClass.addMethod(makeMapMethod(targetClass, opName, opClass, opReturn, isStatic));
    targetClass.addMethod(makeSamMethod(targetClass, opName, opClass, opReturn, isStatic));
    targetClass.addMethod(makeClosureMethod(targetClass, opName, opClass, opReturn, isStatic));
  }

  private MethodNode makeNoArgMethod(ClassNode targetClass, String opName, ClassNode opClass, ClassNode opReturn, boolean isStatic) {
    Parameter[] parms = new Parameter[] {};

    Statement code = new ExpressionStatement(
        new StaticMethodCallExpression(
            classFromType(OpSyntax.class),
            "noArgOperation",
            new ArgumentListExpression(
                new ClassExpression(opClass),
                new ArrayExpression(
                    classFromType(Object.class),
                    opConstructorParms(targetClass, isStatic)))));

    return new MethodNode(opName, modifiers(isStatic), opReturn, parms, new ClassNode[] {}, code);
  }

  private MethodNode makeMapMethod(ClassNode targetClass, String opName, ClassNode opClass, ClassNode opReturn, boolean isStatic) {
    ClassNode parmType = classFromType(Map.class);
    GenericsType[] generics = genericsFromTypes(String.class, Object.class);
    parmType.setGenericsTypes(generics);
    Parameter[] parms = new Parameter[] {new Parameter(parmType, "args")};

    Statement code = new ExpressionStatement(
        new StaticMethodCallExpression(
            classFromType(OpSyntax.class),
            "mapOperation",
            new ArgumentListExpression(
                new ClassExpression(opClass),
                new ArrayExpression(
                    classFromType(Object.class),
                    opConstructorParms(targetClass, isStatic)),
                new VariableExpression("args"))));

    return new MethodNode(opName, modifiers(isStatic), opReturn, parms, new ClassNode[] {}, code);
  }

  private MethodNode makeSamMethod(ClassNode targetClass, String opName, ClassNode opClass, ClassNode opReturn, boolean isStatic) {
    ClassNode parmType = classFromType(Configurable.class);
    GenericsType[] generics = new GenericsType[] {new GenericsType(opClass)};
    parmType.setGenericsTypes(generics);
    Parameter[] parms = new Parameter[] {new Parameter(parmType, "arg")};

    Statement code = new ExpressionStatement(
        new StaticMethodCallExpression(
            classFromType(OpSyntax.class),
            "samOperation",
            new ArgumentListExpression(
                new ClassExpression(opClass),
                new ArrayExpression(
                    classFromType(Object.class), opConstructorParms(targetClass, isStatic)),
                new VariableExpression("arg"))));

    return new MethodNode(opName, modifiers(isStatic), opReturn, parms, new ClassNode[] {}, code);
  }

  private MethodNode makeClosureMethod(ClassNode targetClass, String opName, ClassNode opClass, ClassNode opReturn, boolean isStatic) {
    ClassNode parmType = classFromType(Closure.class);
    Parameter[] parms = new Parameter[] {new Parameter(parmType, "arg")};

    Statement code = new ExpressionStatement(
        new StaticMethodCallExpression(
            classFromType(OpSyntax.class),
            "closureOperation",
            new ArgumentListExpression(
                new ClassExpression(opClass),
                new ArrayExpression(
                    classFromType(Object.class),
                    opConstructorParms(targetClass, isStatic)),
                new VariableExpression("arg"))));

    return new MethodNode(opName, modifiers(isStatic), opReturn, parms, new ClassNode[] {}, code);
  }

  public ClassNode classFromType(Type type) {
    if (type instanceof Class) {
      Class<?> clazz = (Class<?>) type;
      if (clazz.isPrimitive()) {
        return ClassHelper.make(clazz);
      } else {
        return ClassHelper.makeWithoutCaching(clazz, false);
      }
    } else if (type instanceof ParameterizedType) {
      ParameterizedType ptype = (ParameterizedType) type;
      ClassNode base = classFromType(ptype.getRawType());
      GenericsType[] generics = genericsFromTypes(ptype.getActualTypeArguments());
      base.setGenericsTypes(generics);
      return base;
    } else {
      throw new IllegalArgumentException("Unsupported type: " + type.getClass());
    }
  }

  public GenericsType[] genericsFromTypes(Type... types) {
    return Arrays.stream(types)
        .map(this::classFromType)
        .map(GenericsType::new)
        .toArray(size -> new GenericsType[size]);
  }

  public List<Expression> opConstructorParms(ClassNode targetClass, boolean isStatic) {
    if (isStatic) {
      return Collections.emptyList();
    } else {
      FieldNode repo = targetClass.getField("repository");
      return Arrays.asList(new FieldExpression(repo));
    }
  }

  public int modifiers(boolean isStatic) {
    int modifiers = Modifier.PUBLIC | Modifier.FINAL;
    if (isStatic) {
      modifiers |= Modifier.STATIC;
    }
    return modifiers;
  }
}
