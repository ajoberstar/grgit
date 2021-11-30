plugins {
  `java-base`
}

java {
  sourceSets.configureEach {
    val confNames = listOf(compileClasspathConfigurationName, runtimeClasspathConfigurationName)
    confNames.forEach { confName ->
      val conf = configurations.named(confName)
      conf.resolutionStrategy.activateDependencyLocking()
    }
  }
}

tasks.register("lock") {
  doFirst {
    require(gradle.startParameter.isWriteDependencyLocks)
  }
  doLast {
    sourceSets.configureEach {
      val confNames = listOf(compileClasspathConfigurationName, runtimeClasspathConfigurationName)
      confNames.forEach { confName ->
        val conf = configurations.named(confName)
        conf.resolve()
      }
    }
  }
}
