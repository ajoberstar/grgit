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
package org.ajoberstar.grgit.operation

import java.util.concurrent.Callable

import org.ajoberstar.grgit.Repository
import org.ajoberstar.grgit.internal.Operation
import org.ajoberstar.grgit.service.ResolveService
import org.eclipse.jgit.api.DescribeCommand

/**
 * Find the nearest tag reachable. Returns an {@link String}}.
 * @see <a href="http://ajoberstar.org/grgit/grgit-describe.html">grgit-describe</a>
 * @see <a href="http://git-scm.com/docs/git-describe">git-describe Manual Page</a>
 */
@Operation('describe')
class DescribeOp implements Callable<String> {
  private final Repository repo

  DescribeOp(Repository repo){
    this.repo = repo
  }

  /**
   * Sets the commit to be described. Defaults to HEAD.
   * @see {@link ResolveService#toRevisionString(Object)}
   */
  Object commit

  /**
   * Whether to always use long output format or not.
   */
  boolean longDescr

  /**
   * glob patterns to match tags against before they are considered
   */
  List<String> match = []

  String call(){
    DescribeCommand cmd = repo.jgit.describe()
    if (commit) {
      cmd.setTarget(new ResolveService(repo).toRevisionString(commit))
    }
    cmd.setLong(longDescr)
    if (match) {
      cmd.setMatch(match as String[])
    }
    return cmd.call()
  }
}
