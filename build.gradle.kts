plugins {
  id("org.ajoberstar.grgit")
  id("org.ajoberstar.reckon")
}

reckon {
  scopeFromProp()
  stageFromProp("beta", "rc", "final")
}
