plugins {
  id("org.ajoberstar.defaults.java-library")
  id("groovy")
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
  // groovy
  api("org.codehaus.groovy:groovy:[3.0.9, 4.0)")

  // jgit
  api("org.eclipse.jgit:org.eclipse.jgit:[6.0, 7.0)")
}

testing {
  suites {
    val test by getting(JvmTestSuite::class) {
      useSpock("2.3-groovy-3.0")

      dependencies {
        implementation("org.codehaus.groovy:groovy:[3.0.9, 4.0)")
        implementation("org.junit.jupiter:junit-jupiter-api:latest.release")

        // logging
        implementation("org.slf4j:slf4j-api:latest.release")
        runtimeOnly("org.slf4j:slf4j-simple:latest.release")
      }
    }
  }
}

tasks.named<Jar>("jar") {
  manifest {
    attributes.put("Automatic-Module-Name", "org.ajoberstar.grgit")
  }
}
