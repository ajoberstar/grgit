package org.ajoberstar.grgit.service

import org.ajoberstar.grgit.Repository
import org.ajoberstar.grgit.internal.WithGradleOperations
import org.ajoberstar.grgit.operation.TagAddOp
import org.ajoberstar.grgit.operation.TagListOp
import org.ajoberstar.grgit.operation.TagRemoveOp

@WithGradleOperations(instanceOperations = [TagListOp, TagAddOp, TagRemoveOp])
class TagService extends TagServiceBase {
  private TagService(Repository repository) {
    super(repository)
  }
}
