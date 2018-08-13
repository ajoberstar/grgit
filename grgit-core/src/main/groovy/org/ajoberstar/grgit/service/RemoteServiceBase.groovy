package org.ajoberstar.grgit.service

import groovy.transform.PackageScope
import org.ajoberstar.grgit.Repository

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
 * </ul>
 */
abstract class RemoteServiceBase {
  @PackageScope
  final Repository repository

  RemoteServiceBase(Repository repository) {
    this.repository = repository
  }

  static RemoteServiceBase newInstance(Repository repository) {
    Class.forName("org.ajoberstar.grgit.service.RemoteService").newInstance(repository)
  }
}
