package org.ajoberstar.grgit

import org.ajoberstar.grgit.internal.WithJavaOperations
import org.ajoberstar.grgit.operation.*

@WithJavaOperations(staticOperations = [InitOp, CloneOp, OpenOp], instanceOperations = [CleanOp, StatusOp, AddOp, RmOp, ResetOp, ApplyOp, PullOp, PushOp, FetchOp, LsRemoteOp, CheckoutOp, LogOp, CommitOp, RevertOp, MergeOp, DescribeOp, ShowOp])
class Grgit extends GrgitBase {
  private Grgit(Repository repository) {
    super(repository)
  }
}
