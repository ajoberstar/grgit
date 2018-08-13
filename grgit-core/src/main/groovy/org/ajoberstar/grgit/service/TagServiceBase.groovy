package org.ajoberstar.grgit.service

import groovy.transform.PackageScope
import org.ajoberstar.grgit.Repository

/**
 * Provides support for performing tag-related operations on
 * a Git repository.
 *
 * <p>
 *   Details of each operation's properties and methods are available on the
 *   doc page for the class. The following operations are supported directly on
 *   this service instance.
 * </p>
 *
 * <ul>
 *   <li>{@link org.ajoberstar.grgit.operation.TagAddOp add}</li>
 *   <li>{@link org.ajoberstar.grgit.operation.TagListOp list}</li>
 *   <li>{@link org.ajoberstar.grgit.operation.TagRemoveOp remove}</li>
 * </ul>
 *
 * @since 0.2.0
 * @see <a href="http://ajoberstar.org/grgit/grgit-tag.html">grgit-tag</a>
 */
abstract class TagServiceBase {
  @PackageScope
  final Repository repository

  TagServiceBase(Repository repository) {
    this.repository = repository
  }

  static TagServiceBase newInstance(Repository repository) {
    Class.forName("org.ajoberstar.grgit.service.TagService").newInstance(repository)
  }
}
