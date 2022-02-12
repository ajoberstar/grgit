pluginManagement {
  plugins {
    id("org.ajoberstar.defaults.java-library") version "0.17.1"
    id("org.ajoberstar.defaults.gradle-plugin") version "0.17.1"

    id("org.ajoberstar.grgit") version "5.0.0"
    id("org.ajoberstar.reckon") version "0.14.0"
    id("org.ajoberstar.git-publish") version "4.0.0"
    id("org.ajoberstar.stutter") version "0.7.0"

    id("org.gradle.test-retry") version "1.3.1"
    id("com.diffplug.spotless") version "6.2.1"
    id("org.jbake.site") version "5.5.0"
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
