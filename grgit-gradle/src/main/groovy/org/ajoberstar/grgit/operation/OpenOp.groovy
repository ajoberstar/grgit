package org.ajoberstar.grgit.operation

import org.ajoberstar.grgit.Grgit

class OpenOp extends OpenOpBase<Grgit> {
  @Override
  Grgit call() {
    return _call()
  }
}
