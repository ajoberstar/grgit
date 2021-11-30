plugins {
  id("org.ajoberstar.grgit")
  id("org.ajoberstar.git-publish")
  id("org.ajoberstar.reckon")
  id("org.jbake.site")
}

reckon {
  scopeFromProp()
  stageFromProp("alpha", "beta", "rc", "final")
}

jbake {
  srcDirName = "docs"
}

gitPublish {
  branch.set("gh-pages")
  contents {
    from("${buildDir}/${jbake.destDirName}")
  }
}

tasks.named<Copy>("gitPublishCopy") {
  dependsOn(tasks.named("bake"))
}
