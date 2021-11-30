plugins {
  id("java-library-convention")
  groovy
  id("org.gradle.test-retry")
}

dependencies {
  // groovy
  compileOnly("org.codehaus.groovy:groovy:[2.5.0, 2.6.0-alpha)")

  // jgit
  api("org.eclipse.jgit:org.eclipse.jgit:latest.release")

  // logging
  testImplementation("org.slf4j:slf4j-api:latest.release")
  testRuntime("org.slf4j:slf4j-simple:latest.release")

  // testing
  testImplementation("junit:junit:latest.release")
  testImplementation("org.spockframework:spock-core:1.3-groovy-2.5")

  constraints {
    testImplementation("org.codehaus.groovy:groovy:2.5.10") {
      because("Needed to support Java 14")
    }
  }
}

tasks.named<Jar>("jar") {
  manifest {
    attributes.put("Automatic-Module-Name", "org.ajoberstar.grgit")
  }
}

tasks.named<Test>("test") {
  retry {
    maxFailures.set(1)
    maxRetries.set(1)
  }
}
