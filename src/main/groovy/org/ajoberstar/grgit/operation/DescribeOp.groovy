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
package org.ajoberstar.grgit.operation

import java.util.concurrent.Callable

import org.ajoberstar.grgit.Repository
import org.ajoberstar.grgit.exception.GrgitException

import org.eclipse.jgit.api.DescribeCommand
import org.eclipse.jgit.api.errors.GitAPIException

/**
 * Find the nearest tag reachable from HEAD. Returns an {@link String}}.
 *
 * <p>Find the most recent tag that is reachable from HEAD.  If the tag points to the commit, then only the tag is
 * shown. Otherwise, it suffixes the tag name with the number of additional commits on top of the tagged object and the
 * abbreviated object name of the most recent commit.</p>
 *
 * <pre>
 * def description = grgit.describe()
 * </pre>
 *
 * See <a href="http://git-scm.com/docs/git-describe">git-describe Manual Page</a>.
 *
 * @see <a href="http://git-scm.com/docs/git-describe">git-describe Manual Page</a>
 */
class DescribeOp implements Callable<String> {
  private final Repository repo

  DescribeOp(Repository repo){
    this.repo = repo
  }

  String call(){
    DescribeCommand cmd = repo.jgit.describe()
    try {
      return cmd.call()
    } catch (GitAPIException e) {
      throw new GrgitException('Problem retrieving description.', e)
    }

  }
}
