# grgit

![Build](https://github.com/ajoberstar/grgit/workflows/Build/badge.svg)

## Project News

### Retirement of Bintray/JCenter

This project was previously uploaded to JCenter, which is being retired by JFrog on May 1st 2021.

To allow continued acess to past versions, I've made a Maven repo available in [bintray-backup](https://github.com/ajoberstar/bintray-backup). Add the following to your repositories to use it.

```groovy
maven {
  name = 'ajoberstar-backup'
  url = 'https://ajoberstar.github.io/bintray-backup/'
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

**NOTE:** grgit is available from JCenter or the Gradle Plugin Portal. It is not published to Maven Central.

- [Documentation Site](http://ajoberstar.org/grgit/index.html)
- [Release Notes](https://github.com/ajoberstar/grgit/releases)

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
