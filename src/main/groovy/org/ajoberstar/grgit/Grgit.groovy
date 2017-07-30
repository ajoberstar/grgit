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

import org.ajoberstar.grgit.internal.WithOperations
import org.ajoberstar.grgit.operation.*
import org.ajoberstar.grgit.service.*
import org.ajoberstar.grgit.util.JGitUtil

/**
 * Provides support for performing operations on and getting information about
 * a Git repository.
 *
 * <p>A Grgit instance can be obtained via 3 methods.</p>
 *
 * <ul>
 *   <li>
 *	 <p>{@link org.ajoberstar.grgit.operation.OpenOp Open} an existing repository.</p>
 *	 <pre>def grgit = Grgit.open(dir: 'path/to/my/repo')</pre>
 *   </li>
 *   <li>
 *	 <p>{@link org.ajoberstar.grgit.operation.InitOp Initialize} a new repository.</p>
 *	 <pre>def grgit = Grgit.init(dir: 'path/to/my/repo')</pre>
 *   </li>
 *   <li>
 *	 <p>{@link org.ajoberstar.grgit.operation.CloneOp Clone} an existing repository.</p>
 *	 <pre>def grgit = Grgit.clone(dir: 'path/to/my/repo', uri: 'git@github.com:ajoberstar/grgit.git')</pre>
 *   </li>
 * </ul>
 *
 * <p>
 *   Once obtained, operations can be called with two syntaxes.
 * </p>
 *
 * <ul>
 *   <li>
 *	 <p>Map syntax. Any public property on the {@code *Op} class can be provided as a Map entry.</p>
 *	 <pre>grgit.commit(message: 'Committing my code.', amend: true)</pre>
 *   </li>
 *   <li>
 *	 <p>Closure syntax. Any public property or method on the {@code *Op} class can be used.</p>
 *	 <pre>
 * grgit.log {
 *   range 'master', 'my-new-branch'
 *   maxCommits = 5
 * }
 *	 </pre>
 *   </li>
 * </ul>
 *
 * <p>
 *   Details of each operation's properties and methods are available on the
 *   doc page for the class. The following operations are supported directly on a
 *   Grgit instance.
 * </p>
 *
 * <ul>
 *   <li>{@link org.ajoberstar.grgit.operation.AddOp add}</li>
 *   <li>{@link org.ajoberstar.grgit.operation.ApplyOp apply}</li>
 *   <li>{@link org.ajoberstar.grgit.operation.CheckoutOp checkout}</li>
 *   <li>{@link org.ajoberstar.grgit.operation.CleanOp clean}</li>
 *   <li>{@link org.ajoberstar.grgit.operation.CommitOp commit}</li>
 *   <li>{@link org.ajoberstar.grgit.operation.DescribeOp describe}</li>
 *   <li>{@link org.ajoberstar.grgit.operation.FetchOp fetch}</li>
 *   <li>{@link org.ajoberstar.grgit.operation.LogOp log}</li>
 *   <li>{@link org.ajoberstar.grgit.operation.LsRemoteOp lsremote}</li>
 *   <li>{@link org.ajoberstar.grgit.operation.MergeOp merge}</li>
 *   <li>{@link org.ajoberstar.grgit.operation.PullOp pull}</li>
 *   <li>{@link org.ajoberstar.grgit.operation.PushOp push}</li>
 *   <li>{@link org.ajoberstar.grgit.operation.RmOp remove}</li>
 *   <li>{@link org.ajoberstar.grgit.operation.ResetOp reset}</li>
 *   <li>{@link org.ajoberstar.grgit.operation.RevertOp revert}</li>
 *   <li>{@link org.ajoberstar.grgit.operation.ShowOp show}</li>
 *   <li>{@link org.ajoberstar.grgit.operation.StatusOp status}</li>
 * </ul>
 *
 * <p>
 *   And the following operations are supported statically on the Grgit class.
 * </p>
 *
 * <ul>
 *   <li>{@link org.ajoberstar.grgit.operation.CloneOp clone}</li>
 *   <li>{@link org.ajoberstar.grgit.operation.InitOp init}</li>
 *   <li>{@link org.ajoberstar.grgit.operation.OpenOp open}</li>
 * </ul>
 *
 * <p>
 *   Further operations are available on the following services.
 * </p>
 *
 * <ul>
 *   <li>{@link org.ajoberstar.grgit.service.BranchService branch}</li>
 *   <li>{@link org.ajoberstar.grgit.service.RemoteService remote}</li>
 *   <li>{@link org.ajoberstar.grgit.service.ResolveService resolve}</li>
 *   <li>{@link org.ajoberstar.grgit.service.TagService tag}</li>
 * </ul>
 *
 * @since 0.1.0
 */
@WithOperations(staticOperations=[InitOp, CloneOp, OpenOp], instanceOperations=[CleanOp, StatusOp, AddOp, RmOp, ResetOp, ApplyOp, PullOp, PushOp, FetchOp, LsRemoteOp, CheckoutOp, LogOp, CommitOp, RevertOp, MergeOp, DescribeOp, ShowOp])
class Grgit implements AutoCloseable {
  /**
   * The repository opened by this object.
   */
  final Repository repository

  /**
   * Supports operations on branches.
   */
  final BranchService branch

  /**
   * Supports operations on remotes.
   */
  final RemoteService remote

  /**
   * Convenience methods for resolving various objects.
   */
  final ResolveService resolve

  /**
   * Supports operations on tags.
   */
  final TagService tag

  private Grgit(Repository repository) {
    this.repository = repository
    this.branch = new BranchService(repository)
    this.remote = new RemoteService(repository)
    this.tag = new TagService(repository)
    this.resolve = new ResolveService(repository)
  }

  /**
   * Returns the commit located at the current HEAD of the repository.
   * @return the current HEAD commit
   */
  Commit head() {
    return resolve.toCommit('HEAD')
  }

  /**
   * Checks if {@code base} is an ancestor of {@code tip}.
   * @param base the version that might be an ancestor
   * @param tip the tip version
   * @since 0.2.2
   */
  boolean isAncestorOf(Object base, Object tip) {
    Commit baseCommit = resolve.toCommit(base)
    Commit tipCommit = resolve.toCommit(tip)
    return JGitUtil.isAncestorOf(repository, baseCommit, tipCommit)
  }

  /**
   * Release underlying resources used by this instance. After calling close
   * you should not use this instance anymore.
   */
  @Override
  void close() {
    repository.jgit.close()
  }
}
