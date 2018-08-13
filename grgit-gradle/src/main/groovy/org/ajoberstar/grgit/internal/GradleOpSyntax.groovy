package org.ajoberstar.grgit.internal

import org.gradle.api.Action

import java.util.concurrent.Callable

class GradleOpSyntax {
  static def actionOperation(Class<Callable> opClass, Object[] classArgs, Action arg) {
    def op = opClass.newInstance(classArgs)
    arg.execute(op)
    return op.call()
  }
}
