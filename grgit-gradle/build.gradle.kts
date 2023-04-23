plugins {
  id("org.ajoberstar.defaults.gradle-plugin")
  id("groovy")

  id("org.ajoberstar.stutter")
}

group = "org.ajoberstar.grgit"
description = "The Groovy way to use Git"

mavenCentral {
  developerName.set("Andrew Oberstar")
  developerEmail.set("andrew@ajoberstar.org")
  githubOwner.set("ajoberstar")
  githubRepository.set("grgit")
}

java {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(11))
  }
}

dependencies {
  api(project(":grgit-core")) {
    exclude(group = "org.codehaus.groovy")
  }
  compatTestImplementation(project(":grgit-core"))

  compatTestImplementation("org.spockframework:spock-core:2.3-groovy-3.0")
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
      id = "org.ajoberstar.grgit.service"
      displayName = "The Groovy way to use Git (BuildService edition)"
      description = "The Groovy way to use Git (BuildService edition)"
      implementationClass = "org.ajoberstar.grgit.gradle.GrgitServicePlugin"
    }
  }
}

tasks.withType<Test> {
  if (name.startsWith("compatTest")) {
    dependsOn(tasks.named("publishToMavenLocal"))
    dependsOn(project(":grgit-core").tasks.named("publishToMavenLocal"))
    systemProperty("compat.plugin.version", project.version.toString())
  }
}
