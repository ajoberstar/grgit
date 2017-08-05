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

import org.ajoberstar.grgit.Commit
import org.ajoberstar.grgit.Repository
import org.ajoberstar.grgit.internal.Operation
import org.ajoberstar.grgit.service.ResolveService
import org.ajoberstar.grgit.util.JGitUtil
import org.eclipse.jgit.api.LogCommand

/**
 * Gets a log of commits in the repository. Returns a list of {@link Commit}s.
 * Since a Git history is not necessarilly a line, these commits may not be in
 * a strict order.
 * @since 0.1.0
 * @see <a href="http://ajoberstar.org/grgit/grgit-log.html">grgit-log</a>
 * @see <a href="http://git-scm.com/docs/git-log">git-log Manual Page</a>
 */
@Operation('log')
class LogOp implements Callable<List<Commit>> {
  private final Repository repo

  /**
   * @see {@link ResolveService#toRevisionString(Object)}
   */
  List includes = []
  /**
   * @see {@link ResolveService#toRevisionString(Object)}
   */
  List excludes = []
  List paths = []
  int skipCommits = -1
  int maxCommits = -1

  LogOp(Repository repo) {
    this.repo = repo
  }

  void range(Object since, Object until) {
    excludes << since
    includes << until
  }

  List<Commit> call() {
    LogCommand cmd = repo.jgit.log()
    ResolveService resolve = new ResolveService(repo)
    def toObjectId = { rev ->
      String revstr = resolve.toRevisionString(rev)
      JGitUtil.resolveRevObject(repo, revstr, true).id
    }

    includes.collect(toObjectId).each { object ->
      cmd.add(object)
    }
    excludes.collect(toObjectId).each { object ->
      cmd.not(object)
    }
    paths.each { path ->
      cmd.addPath(path)
    }
    cmd.skip = skipCommits
    cmd.maxCount = maxCommits
    return cmd.call().collect { JGitUtil.convertCommit(it) }.asImmutable()
  }
}
