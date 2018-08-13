package org.ajoberstar.grgit.operation

import org.ajoberstar.grgit.Grgit

class InitOp extends InitOpBase<Grgit> {
  @Override
  Grgit call() {
    return _call()
  }
}
