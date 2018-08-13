package org.ajoberstar.grgit.service

import org.ajoberstar.grgit.Repository
import org.ajoberstar.grgit.internal.WithJavaOperations
import org.ajoberstar.grgit.operation.RemoteAddOp
import org.ajoberstar.grgit.operation.RemoteListOp

@WithJavaOperations(instanceOperations = [RemoteListOp, RemoteAddOp])
class RemoteService extends RemoteServiceBase {
  private RemoteService(Repository repository) {
    super(repository)
  }
}
