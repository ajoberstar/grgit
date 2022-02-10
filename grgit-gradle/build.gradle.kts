plugins {
  id("org.ajoberstar.defaults.gradle-plugin")
  groovy

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

  compatTestImplementation("org.spockframework:spock-core:2.0-groovy-3.0")
}

tasks.withType<Test>() {
  useJUnitPlatform()
}

tasks.named<Jar>("jar") {
  manifest {
    attributes.put("Automatic-Module-Name", "org.ajoberstar.grgit.gradle")
  }
}

stutter {
  val java11 by matrices.creating {
    javaToolchain {
      languageVersion.set(JavaLanguageVersion.of(11))
    }
    gradleVersions {
      compatibleRange("7.0")
    }
  }
  val java17 by matrices.creating {
    javaToolchain {
      languageVersion.set(JavaLanguageVersion.of(17))
    }
    gradleVersions {
      compatibleRange("7.3")
    }
  }
}

tasks.named("check") {
  dependsOn(tasks.named("compatTest"))
}

gradlePlugin {
  plugins {
    create("grgitPlugin") {
      id = "org.ajoberstar.grgit"
      displayName = "The Groovy way to use Git"
      description = "The Groovy way to use Git"
      implementationClass = "org.ajoberstar.grgit.gradle.GrgitPlugin"
    }
    create("grgitServicePlugin") {
      id = "org.ajoberstar.grgit-service"
      displayName = "The Groovy way to use Git (BuildService edition)"
      description = "The Groovy way to use Git (BuildService edition)"
      implementationClass = "org.ajoberstar.grgit.gradle.GrgitServicePlugin"
    }
  }
}
