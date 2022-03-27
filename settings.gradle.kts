pluginManagement {
  plugins {
    id("org.ajoberstar.defaults.java-library") version "0.17.1"
    id("org.ajoberstar.defaults.gradle-plugin") version "0.17.1"

    id("org.ajoberstar.grgit") version "5.0.0"
    id("org.ajoberstar.reckon") version "0.16.1"
    id("org.ajoberstar.stutter") version "0.7.1"

    id("com.diffplug.spotless") version "6.3.0"
  }

  repositories {
    mavenCentral()
  }
}

dependencyResolutionManagement {
  repositories {
    mavenCentral()
  }
}

rootProject.name = "grgit"

include("grgit-core")
include("grgit-gradle")
