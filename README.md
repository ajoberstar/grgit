# grgit

![CI](https://github.com/ajoberstar/grgit/workflows/CI/badge.svg)

**NOTE:** As of 5.0.0, grgit is published to Maven Central

## Getting Help or Contributing

**IMPORANT:** I consider this plugin feature complete and don't spend a lot of time on maintenance due to other time commitments. While, I will eventually get to issues or PRs raised, **do not** expect a timely response. I'm not trying to be rude or dismissive, I only get back to this project periodically (on the order of _months_, in many cases). Please set your expectations appropriately as you file issues or open PRs.

Please use the repo's [issues](https://github.com/ajoberstar/grgit/issues) for all questions, bug reports, and feature requests.

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

- [Documentation Site](https://ajoberstar.org/grgit/main/index.html)
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

tasks.register("describe", DescribeTask, grgitService.service)

class DescribeTask extends DefaultTask {
    private final Provider<GrgitService> service

    @Inject
    DescribeTask(Provider<GrgitService> service) {
        this.service = service
        usesService(service)
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
      // This should be coupled with tasks that use the service calling "usesService()" to register their usage of the service
      spec.getMaxParallelUsages().set(1);
    });
  ```

## Finding versions of grgit

### Newest versions are on Maven Central

As of 4.1.1, grgit is published to Maven Central and the Gradle Plugin Portal.

As of 5.0.0, this project is no longer directly published to the Gradle Plugin Portal, but since the portal proxies Maven Central you can still access it through the portal. The only side effect is that [the portal](https://plugins.gradle.org/plugin/org.ajoberstar.grgit) will no longer list the latest version. Use this repo or [search.maven.org](https://search.maven.org/search?q=g:org.ajoberstar.grgit) to find the latest version.

### Old versions from Bintray/JCenter

This project was previously uploaded to JCenter, which was deprecated in 2021.

In the event that JCenter is unavailable and acess to past versions (4.1.0 and earlier) is needed, I've made a Maven repo available in [bintray-backup](https://github.com/ajoberstar/bintray-backup). Add the following to your repositories to use it.

```groovy
maven {
  name = 'ajoberstar-backup'
  url = 'https://ajoberstar.org/bintray-backup/'
}
```

Made possible by [lacasseio/bintray-helper](https://github.com/lacasseio/bintray-helper) in case you have a similar need to pull your old Bintray artifacts.

## Acknowledgements

Thanks to [everyone](https://github.com/ajoberstar/grgit/graphs/contributors)
who has contributed to the library.
