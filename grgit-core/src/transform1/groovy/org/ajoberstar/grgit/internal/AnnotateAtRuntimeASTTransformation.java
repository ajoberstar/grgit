package org.ajoberstar.grgit.internal;

import java.util.List;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.AnnotatedNode;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.transform.AbstractASTTransformation;
import org.codehaus.groovy.transform.GroovyASTTransformation;

@GroovyASTTransformation
public final class AnnotateAtRuntimeASTTransformation extends AbstractASTTransformation {
  @Override
  public void visit(ASTNode[] nodes, SourceUnit source) {
    AnnotationNode annotation = (AnnotationNode) nodes[0];
    AnnotatedNode parent = (AnnotatedNode) nodes[1];

    ClassNode clazz = (ClassNode) parent;
    List<String> annotations = getMemberList(annotation, "annotations");
    for (String name : annotations) {
      // !!! UGLY HACK !!!
      // Groovy won't think the class is an annotation when creating a ClassNode just based on the name.
      // Instead, we create a node based on an interface and then overwrite the name to get the interface
      // we actually want.
      ClassNode base = new ClassNode(FunctionalInterface.class);
      base.setName(name);

      clazz.addAnnotation(new AnnotationNode(base));
    }
  }
}
