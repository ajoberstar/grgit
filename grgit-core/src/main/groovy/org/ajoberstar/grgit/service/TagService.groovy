package org.ajoberstar.grgit.service

import org.ajoberstar.grgit.Repository
import org.ajoberstar.grgit.internal.WithOperations
import org.ajoberstar.grgit.operation.TagAddOp
import org.ajoberstar.grgit.operation.TagListOp
import org.ajoberstar.grgit.operation.TagRemoveOp

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
@WithOperations(instanceOperations=[TagListOp, TagAddOp, TagRemoveOp])
class TagService {
  private final Repository repository

  TagService(Repository repository) {
    this.repository = repository
  }
}
