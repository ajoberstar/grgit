package org.ajoberstar.grgit.service

import org.ajoberstar.grgit.Repository
import org.ajoberstar.grgit.internal.WithOperations
import org.ajoberstar.grgit.operation.RemoteAddOp
import org.ajoberstar.grgit.operation.RemoteListOp
import org.ajoberstar.grgit.operation.RemoteRemoveOp

/**
 * Provides support for remote-related operations on a Git repository.
 *
 * <p>
 *   Details of each operation's properties and methods are available on the
 *   doc page for the class. The following operations are supported directly on
 *   this service instance.
 * </p>
 *
 * <ul>
 *   <li>{@link org.ajoberstar.grgit.operation.RemoteAddOp add}</li>
 *   <li>{@link org.ajoberstar.grgit.operation.RemoteListOp list}</li>
 *   <li>{@link org.ajoberstar.grgit.operation.RemoteRemoveOp remove}</li>
 * </ul>
 */
@WithOperations(instanceOperations=[RemoteListOp, RemoteAddOp, RemoteRemoveOp])
class RemoteService {
  private final Repository repository

  RemoteService(Repository repository) {
    this.repository = repository
  }
}
