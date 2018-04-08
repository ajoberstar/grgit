package org.ajoberstar.grgit.internal

import java.util.concurrent.Callable

class OpSyntax {
  static def noArgOperation(Class<Callable> opClass, Object[] classArgs) {
    def op = opClass.newInstance(classArgs)
    return op.call()
  }

  static def mapOperation(Class<Callable> opClass, Object[] classArgs, Map args) {
    def op = opClass.newInstance(classArgs)

    args.forEach { key, value ->
      op[key] = value
    }

    return op.call()
  }

  static def closureOperation(Class<Callable> opClass, Object[] classArgs, Closure closure) {
    def op = opClass.newInstance(classArgs)

    Object originalDelegate = closure.delegate
    closure.delegate = op
    closure.resolveStrategy = Closure.DELEGATE_FIRST
    closure.call()
    closure.delegate = originalDelegate

    return op.call()
  }
}
