package org.ajoberstar.grgit.service

import org.ajoberstar.grgit.Repository
import org.ajoberstar.grgit.internal.WithGradleOperations
import org.ajoberstar.grgit.operation.BranchAddOp
import org.ajoberstar.grgit.operation.BranchChangeOp
import org.ajoberstar.grgit.operation.BranchListOp
import org.ajoberstar.grgit.operation.BranchRemoveOp
import org.ajoberstar.grgit.operation.BranchStatusOp

@WithGradleOperations(instanceOperations = [BranchListOp, BranchAddOp, BranchRemoveOp, BranchChangeOp, BranchStatusOp])
class BranchService extends BranchServiceBase {
  private BranchService(Repository repository) {
    super(repository)
  }
}
