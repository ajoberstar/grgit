pluginManagement {
  plugins {
    id("org.ajoberstar.defaults.java-library") version "0.17.6"
    id("org.ajoberstar.defaults.gradle-plugin") version "0.17.6"

    id("org.ajoberstar.reckon.settings") version "0.18.3"
    id("org.ajoberstar.stutter") version "1.0.0"

    id("com.diffplug.spotless") version "6.25.0"
  }

  repositories {
    mavenCentral()
  }
}

plugins {
  id("org.ajoberstar.reckon.settings")
}

extensions.configure<org.ajoberstar.reckon.gradle.ReckonExtension> {
  setDefaultInferredScope("patch")
  stages("beta", "rc", "final")
  setScopeCalc(calcScopeFromProp().or(calcScopeFromCommitMessages()))
  setStageCalc(calcStageFromProp())
}

dependencyResolutionManagement {
  repositories {
    mavenCentral()
  }
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
}

rootProject.name = "grgit"

include("grgit-core")
include("grgit-gradle")
