package org.ajoberstar.grgit.internal

import java.util.concurrent.Callable
import java.util.function.Consumer

class JavaOpSyntax {
  static def consumerOperation(Class<Callable> opClass, Object[] classArgs, Consumer arg) {
    def op = opClass.newInstance(classArgs)
    arg.accept(op)
    return op.call()
  }
}
