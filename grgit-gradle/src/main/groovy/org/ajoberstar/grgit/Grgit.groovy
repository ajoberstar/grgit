package org.ajoberstar.grgit

import org.ajoberstar.grgit.internal.WithGradleOperations
import org.ajoberstar.grgit.operation.AddOp
import org.ajoberstar.grgit.operation.ApplyOp
import org.ajoberstar.grgit.operation.CheckoutOp
import org.ajoberstar.grgit.operation.CleanOp
import org.ajoberstar.grgit.operation.CloneOp
import org.ajoberstar.grgit.operation.CommitOp
import org.ajoberstar.grgit.operation.DescribeOp
import org.ajoberstar.grgit.operation.FetchOp
import org.ajoberstar.grgit.operation.InitOp
import org.ajoberstar.grgit.operation.LogOp
import org.ajoberstar.grgit.operation.LsRemoteOp
import org.ajoberstar.grgit.operation.MergeOp
import org.ajoberstar.grgit.operation.OpenOp
import org.ajoberstar.grgit.operation.PullOp
import org.ajoberstar.grgit.operation.PushOp
import org.ajoberstar.grgit.operation.ResetOp
import org.ajoberstar.grgit.operation.RevertOp
import org.ajoberstar.grgit.operation.RmOp
import org.ajoberstar.grgit.operation.ShowOp
import org.ajoberstar.grgit.operation.StatusOp

@WithGradleOperations(staticOperations = [InitOp, CloneOp, OpenOp], instanceOperations = [CleanOp, StatusOp, AddOp, RmOp, ResetOp, ApplyOp, PullOp, PushOp, FetchOp, LsRemoteOp, CheckoutOp, LogOp, CommitOp, RevertOp, MergeOp, DescribeOp, ShowOp])
class Grgit extends GrgitBase {
  private Grgit(Repository repository) {
    super(repository)
  }
}
