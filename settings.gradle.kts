pluginManagement {
  plugins {
    id("org.gradle.test-retry") version "1.3.1"
    id("com.gradle.plugin-publish") version "0.18.0"

    id("org.ajoberstar.grgit") version "4.0.2"
    id("org.ajoberstar.reckon") version "0.11.0"
    id("org.ajoberstar.git-publish") version "3.0.0"
    id("org.ajoberstar.stutter") version "0.6.0"

    id("com.diffplug.spotless") version "6.0.0"
    id("org.jbake.site") version "5.5.0"
  }
}

rootProject.name = "grgit"

include("grgit-core")
include("grgit-gradle")
