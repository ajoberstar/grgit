package org.ajoberstar.grgit.internal;

import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.GenericsType;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.expr.*;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.transform.GroovyASTTransformation;
import org.gradle.api.Action;

@GroovyASTTransformation
public class WithGradleOperationsASTTransformation extends WithOperationsASTTransformationBase {
  @Override
  protected MethodNode makeSamMethod(ClassNode targetClass, String opName, ClassNode opClass, ClassNode opReturn, boolean isStatic) {
    ClassNode parmType = classFromType(Action.class);
    GenericsType[] generics = new GenericsType[] {new GenericsType(opClass)};
    parmType.setGenericsTypes(generics);
    Parameter[] parms = new Parameter[] {new Parameter(parmType, "arg")};

    Statement code = new ExpressionStatement(
        new StaticMethodCallExpression(
            classFromType(GradleOpSyntax.class),
            "actionOperation",
            new ArgumentListExpression(
                new ClassExpression(opClass),
                new ArrayExpression(
                    classFromType(Object.class),
                    opConstructorParms(targetClass, isStatic)),
                new VariableExpression("arg"))));

    return new MethodNode(opName, modifiers(isStatic), opReturn, parms, new ClassNode[] {}, code);
  }
}
