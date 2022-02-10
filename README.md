# grgit

![CI](https://github.com/ajoberstar/grgit/workflows/CI/badge.svg)

## Project News

### Newest versions are on Maven Central

As of 4.1.1, grgit is published to Maven Central and the Gradle Plugin Portal.

### Retirement of Bintray/JCenter

This project was previously uploaded to JCenter, which is being retired by JFrog on May 1st 2021.

To allow continued acess to past versions (4.1.0 and earlier), I've made a Maven repo available in [bintray-backup](https://github.com/ajoberstar/bintray-backup). Add the following to your repositories to use it.

```groovy
maven {
  name = 'ajoberstar-backup'
  url = 'https://ajoberstar.org/bintray-backup/'
}
```

Made possible by [lacasseio/bintray-helper](https://github.com/lacasseio/bintray-helper) in case you have a similar need to pull your old Bintray artifacts.

### Maintenance

I'm not spending much time on this project anymore. If you're interested in taking over maintenance, please open an issue.

## Why do you care?

[JGit](https://eclipse.org/jgit/) provides a powerful Java API for interacting with Git repositories. However,
in a Groovy context it feels very cumbersome, making it harder to express the operations you want to perform
without being surrounded by a lot of cruft.

## What is it?

Grgit is a wrapper over JGit that provides a fluent API for interacting with Git repositories in Groovy-based
tooling.

"porcelain" commands are the primary scope of what is included. Features that require
more user interaction (such as resolving merge conflicts) are intentionally excluded.

It also provides a Gradle plugin to easily get a Grgit instance for the build's repository.

## Documentation

**NOTE:** grgit is available from Maven Central or the Gradle Plugin Portal

- [Documentation Site](http://ajoberstar.org/grgit/index.html)
- [Release Notes](https://github.com/ajoberstar/grgit/releases)

## Simple Usage in Gradle

Apply the `org.ajoberstar.grgit` plugin in any project that needs to access a `Grgit` instance.

NOTE: This plugin eagerly opens a Grgit instance, which may not be needed depending on the tasks you want to run. If this is not desired, see the next section.

```
plugins {
  id 'org.ajoberstar.grgit' version '<version>'
}

// adds a grgit property to the project (will silently be null if there's no git repo)
tasks.register("describe") {
  doFirst {
    println grgit.describe()
  }
}
```

## More Performant Usage in Gradle

Apply the `org.ajoberstar.grgit.service` plugin instead of `org.ajoberstar.grgit` to avoid eagerly resolving the `Grgit` instance. This works best with custom tasks that accept a `Property<GrgitService>`.

This approach ensures you only open a `Grgit` instance when a task is run that uses it.

```
import org.ajoberstar.grgit.gradle.GrgitService

plugins {
  id 'org.ajoberstar.grgit.service' version '<version>'
}

tasks.register("describe", DescribeTask) {
  service = grgitService.service
}

class DescribeTask extends DefaultTask {
    @Input
    final Property<GrgitService> service

    @Inject
    DoStuffTask(ObjectFactory objectFactory) {
        this.service = objectFactory.property(GrgitService.class);
    }

    @TaskAction
    void execute() {
        println service.get().grgit.describe()
    }
}
```

### Custom Gradle Plugins

If you are writing a custom Gradle plugin, you'll want to use one or both of the following approaches:

- If you need a `Grgit` instance representing the repository the project is in, use `org.ajoberstar.grgit.service` and use the `GrgitServiceExtension` to access the shared `GrgitService`. Wire this into any tasks or whatever needs to use the service via `Property<GrgitService>` for full lazy evaluation benefits.
- If you need a `Grgit` instance that's separate from the project's repository, declare your own `GrgitService` naming it something _not_ prefixed with `grgit*`.

  ```
  Provider<GrgitService> serviceProvider = project.getGradle().getSharedServices().registerIfAbsent("grgit", GrgitService.class, spec -> {
      // use getCurrentDirectory() if you need to search upwards from the provided directory
      spec.getParameters().getCurrentDirectory().set(project.getLayout().getProjectDirectory());
      // or use getDirectory() if you want to specify a specific directory and not search
      spec.getParameters().getDirectory().set(project.getLayout().getBuildDirectory().dir("my-custom-repo"));
      // generally, this should be false, unless you're using getDirectory() choose to have the repo initialized if the directory does not exist
      spec.getParameters().getInitIfNotExists().set(false);
      // I recommend setting this to 1 unless you know better, this will avoid multiple parallel tasks editing the repo at the same time
      spec.getMaxParallelUsages().set(1);
    });
  ```

## Questions, Bugs, and Features

Please use the repo's [issues](https://github.com/ajoberstar/grgit/issues)
for all questions, bug reports, and feature requests.

## Contributing

Contributions are very welcome and are accepted through pull requests.

Smaller changes can come directly as a PR, but larger or more complex
ones should be discussed in an issue first to flesh out the approach.

If you're interested in implementing a feature on the
[issues backlog](https://github.com/ajoberstar/grgit/issues), add a comment
to make sure it's not already in progress and for any needed discussion.

## Acknowledgements

Thanks to [everyone](https://github.com/ajoberstar/grgit/graphs/contributors)
who has contributed to the library.
