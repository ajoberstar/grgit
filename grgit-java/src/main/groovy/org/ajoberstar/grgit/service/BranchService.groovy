package org.ajoberstar.grgit.service

import org.ajoberstar.grgit.Repository
import org.ajoberstar.grgit.internal.WithJavaOperations
import org.ajoberstar.grgit.operation.*

@WithJavaOperations(instanceOperations = [BranchListOp, BranchAddOp, BranchRemoveOp, BranchChangeOp, BranchStatusOp])
class BranchService extends BranchServiceBase {
  private BranchService(Repository repository) {
    super(repository)
  }
}
