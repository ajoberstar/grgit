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
package org.ajoberstar.grgit

import groovy.transform.Immutable

import org.eclipse.jgit.lib.Repository

/**
 * A branch.
 * @since 0.2.0
 */
@Immutable
class Branch {
  /**
   * The fully qualified name of this branch.
   */
  String fullName

  /**
   * This branch's upstream branch. {@code null} if this branch isn't
   * tracking an upstream.
   */
  Branch trackingBranch

  /**
   * The simple name of the branch.
   * @return the simple name
   */
  String getName() {
    return Repository.shortenRefName(fullName)
  }
}
