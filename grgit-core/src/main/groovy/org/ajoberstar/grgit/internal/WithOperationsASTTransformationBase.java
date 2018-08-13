package org.ajoberstar.grgit.internal;

import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

import groovy.lang.Closure;
import org.codehaus.groovy.ast.*;
import org.codehaus.groovy.ast.expr.*;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.transform.AbstractASTTransformation;

public abstract class WithOperationsASTTransformationBase extends AbstractASTTransformation {
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
    String opName = getMemberStringValue(findAnnotationNode(opClass, classFromType(Operation.class)), "value");
    ClassNode opReturn = findCorrectCallMethod(opClass).getReturnType();

    targetClass.addMethod(makeNoArgMethod(targetClass, opName, opClass, opReturn, isStatic));
    targetClass.addMethod(makeMapMethod(targetClass, opName, opClass, opReturn, isStatic));
    targetClass.addMethod(makeSamMethod(targetClass, opName, opClass, opReturn, isStatic));
    targetClass.addMethod(makeClosureMethod(targetClass, opName, opClass, opReturn, isStatic));
  }

  private AnnotationNode findAnnotationNode(ClassNode target, ClassNode find) {
    Optional<AnnotationNode> annotation = target.getAnnotations(find).stream().findFirst();
    if (annotation.isPresent()) {
      return annotation.get();
    } else {
      ClassNode superClass = target.getSuperClass();
      if (superClass == null) {
        throw new IllegalArgumentException("Class is not annotated with " + find + ": " + target);
      } else {
        return findAnnotationNode(superClass, find);
      }
    }
  }

  private MethodNode findCorrectCallMethod(ClassNode target) {
    return target.getDeclaredMethods("call").stream()
        .filter(methodNode -> !methodNode.isSynthetic())
        .filter(methodNode -> methodNode.getParameters().length == 0)
        .findFirst()
        .orElseThrow(() -> new IllegalStateException("No actual call methods are present."));
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

  protected abstract MethodNode makeSamMethod(ClassNode targetClass, String opName, ClassNode opClass, ClassNode opReturn, boolean isStatic);

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

  protected ClassNode classFromType(Type type) {
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

  private GenericsType[] genericsFromTypes(Type... types) {
    return Arrays.stream(types)
        .map(this::classFromType)
        .map(GenericsType::new)
        .toArray(GenericsType[]::new);
  }

  protected List<Expression> opConstructorParms(ClassNode targetClass, boolean isStatic) {
    if (isStatic) {
      return Collections.emptyList();
    } else {
      FieldNode repo = targetClass.getField("repository");
      return Collections.singletonList(new FieldExpression(repo));
    }
  }

  protected int modifiers(boolean isStatic) {
    int modifiers = Modifier.PUBLIC | Modifier.FINAL;
    if (isStatic) {
      modifiers |= Modifier.STATIC;
    }
    return modifiers;
  }
}
