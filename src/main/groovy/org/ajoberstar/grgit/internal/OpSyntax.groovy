/*
 * Copyright 2012-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
