/*
 * Copyright 2012-2015 the original author or authors.
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
package org.ajoberstar.grgit.util

/**
 * Utility class to provide support for different operation syntaxes.
 * @since 0.1.0
 */
final class OpSyntaxUtil {
  private OpSyntaxUtil() {
    throw new AssertionError('Cannot instantiate this class.')
  }

  /**
   * Attempt to call an operation using the given parameters.
   * @param service the service class to call the method on
   * @param supportedOps a map of operation method names to operation
   * classes supported on the {@code service} class
   * @param classArgs the arguments to instantiate the operation class
   * @param methodName the method requested on the {@code service}
   * @param methodArgs the arguments requested on the method
   * @return the result of the operation
   * @throws GrgitException if the operation failed
   * @throws MissingMethodException if the requested method doesn't
   * correspond to a supported operation
   */
  static Object tryOp(Class service, Map supportedOps, Object[] classArgs, String methodName, Object[] methodArgs) {
    if (methodName in supportedOps && methodArgs.size() < 2) {
      def op = supportedOps[methodName].newInstance(classArgs)
      def config = methodArgs.size() == 0 ? [:] : methodArgs[0]
      ConfigureUtil.configure(op, config)
      return op.call()
    } else {
      throw new MissingMethodException(methodName, service, methodArgs)
    }
  }
}
