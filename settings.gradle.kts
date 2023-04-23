pluginManagement {
  plugins {
    id("org.ajoberstar.defaults.java-library") version "0.17.5"
    id("org.ajoberstar.defaults.gradle-plugin") version "0.17.5"

    id("org.ajoberstar.reckon.settings") version "0.18.0"
    id("org.ajoberstar.stutter") version "0.7.2"

    id("com.diffplug.spotless") version "6.18.0"
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
