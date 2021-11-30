plugins {
  id("java-library-convention")
  id("java-gradle-plugin")
  groovy

  id("com.gradle.plugin-publish")
  id("org.ajoberstar.stutter")
}

// avoid conflict with localGroovy()
configurations.configureEach {
  exclude(group = "org.codehaus.groovy")
}

// compat tests use grgit to set up and verify the tests
sourceSets {
  compatTest {
    compileClasspath += sourceSets["main"].output
    runtimeClasspath += sourceSets["main"].output
  }
}

dependencies {
  compileOnly(gradleApi())

  api(project(":grgit-core"))
  compatTestImplementation(project(":grgit-core"))

  compatTestImplementation("org.spockframework:spock-core:2.0-groovy-2.5")
}

tasks.named<Jar>("jar") {
  manifest {
    attributes.put("Automatic-Module-Name", "org.ajoberstar.grgit.gradle")
  }
}

stutter {
  setSparse(true)
  java(8) {
    compatibleRange("4.0")
  }
  java(11) {
    compatibleRange("5.0")
  }
  java(14) {
    compatibleRange("6.3")
  }
}

pluginBundle {
  website = "https://github.com/ajoberstar/grgit"
  vcsUrl = "https://github.com/ajoberstar/grgit.git"
  description = "The Groovy way to use Git"
  plugins {
    create("grgitPlugin") {
      id = "org.ajoberstar.grgit"
      displayName = "The Groovy way to use Git"
      tags = listOf("git", "groovy")
    }
  }
  mavenCoordinates {
    groupId = project.group as String
    artifactId = project.name as String
    version = project.version.toString()
  }
}

// remove duplicate publication
gradlePlugin {
  setAutomatedPublishing(false)
}
