plugins {
  id("java-library-convention")
  groovy
  id("org.gradle.test-retry")
}

dependencies {
  // groovy
  compileOnly("org.codehaus.groovy:groovy:[3.0, 4.0)")

  // jgit
  api("org.eclipse.jgit:org.eclipse.jgit:[6.0, 7.0)")

  // logging
  testImplementation("org.slf4j:slf4j-api:latest.release")
  testRuntimeOnly("org.slf4j:slf4j-simple:latest.release")

  // testing
  testImplementation("org.junit.jupiter:junit-jupiter-api:latest.release")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:latest.release")
  testImplementation("org.spockframework:spock-core:2.0-groovy-3.0")

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
