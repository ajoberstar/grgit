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
package org.ajoberstar.grgit.util

import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime

import org.ajoberstar.grgit.Branch
import org.ajoberstar.grgit.Commit
import org.ajoberstar.grgit.Person
import org.ajoberstar.grgit.Remote
import org.ajoberstar.grgit.Repository
import org.ajoberstar.grgit.Status
import org.ajoberstar.grgit.Tag
import org.eclipse.jgit.errors.IncorrectObjectTypeException
import org.eclipse.jgit.lib.BranchConfig
import org.eclipse.jgit.lib.Config
import org.eclipse.jgit.lib.ObjectId
import org.eclipse.jgit.lib.PersonIdent
import org.eclipse.jgit.lib.Ref
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.revwalk.RevObject
import org.eclipse.jgit.revwalk.RevTag
import org.eclipse.jgit.revwalk.RevWalk
import org.eclipse.jgit.transport.RemoteConfig

/**
 * Utility class to perform operations against JGit objects.
 * @since 0.1.0
 */
class JGitUtil {
  private JGitUtil() {
    throw new AssertionError('Cannot instantiate this class.')
  }

  /**
   * Resolves a JGit {@code ObjectId} using the given revision string.
   * @param repo the Grgit repository to resolve the object from
   * @param revstr the revision string to use
   * @return the resolved object
   */
  static ObjectId resolveObject(Repository repo, String revstr) {
    ObjectId object = repo.jgit.repository.resolve(revstr)
    return object
  }

  /**
   * Resolves a JGit {@code RevObject} using the given revision string.
   * @param repo the Grgit repository to resolve the object from
   * @param revstr the revision string to use
   * @param peel whether or not to peel the resolved object
   * @return the resolved object
   */
  static RevObject resolveRevObject(Repository repo, String revstr, boolean peel = false) {
    ObjectId id = resolveObject(repo, revstr)
    RevWalk walk = new RevWalk(repo.jgit.repository)
    RevObject rev = walk.parseAny(id)
    return peel ? walk.peel(rev) : rev
  }

  /**
   * Resolves the parents of an object.
   * @param repo the Grgit repository to resolve the parents from
   * @param id the object to get the parents of
   * @return the parents of the commit
   */
  static Set<ObjectId> resolveParents(Repository repo, ObjectId id) {
    RevWalk walk = new RevWalk(repo.jgit.repository)
    RevCommit rev = walk.parseCommit(id)
    return rev.parents.collect {
      walk.parseCommit(it)
    }
  }

  /**
   * Resolves a Grgit {@code Commit} using the given revision string.
   * @param repo the Grgit repository to resolve the commit from
   * @param revstr the revision string to use
   * @return the resolved commit
   */
  static Commit resolveCommit(Repository repo, String revstr) {
    ObjectId id = resolveObject(repo, revstr)
    return resolveCommit(repo, id)
  }

  /**
   * Resolves a Grgit {@code Commit} using the given object.
   * @param repo the Grgit repository to resolve the commit from
   * @param id the object id of the commit to resolve
   * @return the resolved commit
   */
  static Commit resolveCommit(Repository repo, ObjectId id) {
    RevWalk walk = new RevWalk(repo.jgit.repository)
    return convertCommit(walk.parseCommit(id))
  }

  /**
   * Converts a JGit commit to a Grgit commit.
   * @param rev the JGit commit to convert
   * @return a corresponding Grgit commit
   */
  static Commit convertCommit(RevCommit rev) {
    Map props = [:]
    props.id = ObjectId.toString(rev)
    PersonIdent committer = rev.committerIdent
    props.committer = new Person(committer.name, committer.emailAddress)
    PersonIdent author = rev.authorIdent
    props.author = new Person(author.name, author.emailAddress)

    Instant instant = Instant.ofEpochSecond(rev.commitTime)
    ZoneId zone = Optional.ofNullable(rev.committerIdent.timeZone)
      .map { it.toZoneId() }
      .orElse(ZoneOffset.UTC)
    props.dateTime = ZonedDateTime.ofInstant(instant, zone)

    props.fullMessage = rev.fullMessage
    props.shortMessage = rev.shortMessage
    props.parentIds = rev.parents.collect { ObjectId.toString(it) }
    return new Commit(props)
  }

  /**
   * Resolves a Grgit tag from a name.
   * @param repo the Grgit repository to resolve from
   * @param name the name of the tag to resolve
   * @return the resolved tag
   */
  static Tag resolveTag(Repository repo, String name) {
    Ref ref = repo.jgit.repository.getRef(name)
    return resolveTag(repo, ref)
  }

  /**
   * Resolves a Grgit Tag from a JGit ref.
   * @param repo the Grgit repository to resolve from
   * @param ref the JGit ref to resolve
   * @return the resolved tag
   */
  static Tag resolveTag(Repository repo, Ref ref) {
    Map props = [:]
    props.fullName = ref.name
    try {
      RevWalk walk = new RevWalk(repo.jgit.repository)
      RevTag rev = walk.parseTag(ref.objectId)
      RevObject target = walk.peel(rev)
      walk.parseBody(rev.object)
      props.commit = convertCommit(target)
      PersonIdent tagger = rev.taggerIdent
      props.tagger = new Person(tagger.name, tagger.emailAddress)
      props.fullMessage = rev.fullMessage
      props.shortMessage = rev.shortMessage

      Instant instant = rev.taggerIdent.when.toInstant()
      ZoneId zone = Optional.ofNullable(rev.taggerIdent.timeZone)
        .map { it.toZoneId() }
        .orElse(ZoneOffset.UTC)
      props.dateTime = ZonedDateTime.ofInstant(instant, zone)
    } catch (IncorrectObjectTypeException e) {
      props.commit = resolveCommit(repo, ref.objectId)
    }
    return new Tag(props)
  }

  /**
   * Resolves a Grgit branch from a name.
   * @param repo the Grgit repository to resolve from
   * @param name the name of the branch to resolve
   * @return the resolved branch
   */
  static Branch resolveBranch(Repository repo, String name) {
    Ref ref = repo.jgit.repository.getRef(name)
    return resolveBranch(repo, ref)
  }

  /**
   * Resolves a Grgit branch from a JGit ref.
   * @param repo the Grgit repository to resolve from
   * @param ref the JGit ref to resolve
   * @return the resolved branch or {@code null} if the {@code ref} is
   * {@code null}
   */
  static Branch resolveBranch(Repository repo, Ref ref) {
    if (ref == null) {
      return null
    }
    Map props = [:]
    props.fullName = ref.name
    String shortName = org.eclipse.jgit.lib.Repository.shortenRefName(props.fullName)
    Config config = repo.jgit.repository.config
    BranchConfig branchConfig = new BranchConfig(config, shortName)
    if (branchConfig.trackingBranch) {
      props.trackingBranch = resolveBranch(repo, branchConfig.trackingBranch)
    }
    return new Branch(props)
  }

  /**
   * Converts a JGit status to a Grgit status.
   * @param jgitStatus the status to convert
   * @return the converted status
   */
  static Status convertStatus(org.eclipse.jgit.api.Status jgitStatus) {
    return new Status(
      staged: [
        added: jgitStatus.added,
        modified: jgitStatus.changed,
        removed: jgitStatus.removed
      ],
      unstaged: [
        added: jgitStatus.untracked,
        modified: jgitStatus.modified,
        removed: jgitStatus.missing
      ],
      conflicts: jgitStatus.conflicting
    )
  }

  /**
   * Converts a JGit remote to a Grgit remote.
   * @param rc the remote config to convert
   * @return the converted remote
   */
  static Remote convertRemote(RemoteConfig rc) {
    return new Remote(
      name: rc.name,
      url: rc.uris.find(),
      pushUrl: rc.pushURIs.find(),
      fetchRefSpecs: rc.fetchRefSpecs.collect { it.toString() },
      pushRefSpecs: rc.pushRefSpecs.collect { it.toString() },
      mirror: rc.mirror)
  }

  /**
   * Checks if {@code base} is an ancestor of {@code tip}.
   * @param repo the repository to look in
   * @param base the version that might be an ancestor
   * @param tip the tip version
   * @since 0.2.2
   */
  static boolean isAncestorOf(Repository repo, Commit base, Commit tip) {
    org.eclipse.jgit.lib.Repository jgit = repo.jgit.repo
    RevWalk revWalk = new RevWalk(jgit)
    RevCommit baseCommit = revWalk.lookupCommit(jgit.resolve(base.id))
    RevCommit tipCommit = revWalk.lookupCommit(jgit.resolve(tip.id))
    return revWalk.isMergedInto(baseCommit, tipCommit)
  }
}
