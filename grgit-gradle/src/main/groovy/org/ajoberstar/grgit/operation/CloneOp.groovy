package org.ajoberstar.grgit.operation

import org.ajoberstar.grgit.Grgit

class CloneOp extends CloneOpBase<Grgit> {
  @Override
  Grgit call() {
    return _call()
  }
}
