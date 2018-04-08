# grgit

[![Download](https://api.bintray.com/packages/ajoberstar/maven/grgit/images/download.svg)](https://bintray.com/ajoberstar/maven/grgit/_latestVersion)
[![CircleCI](https://circleci.com/gh/ajoberstar/grgit.svg?style=svg)](https://circleci.com/gh/ajoberstar/grgit)

## Why do you care?

[JGit](https://eclipse.org/jgit/) provides a powerful Java API for interacting with Git repositories. However,
in a Groovy context it feels very cumbersome, making it harder to express the operations you want to perform
without being surrounded by a lot of cruft.

## What is it?

Grgit is a wrapper over JGit that provides a fluent API for interacting with Git repositories in Groovy-based
tooling.

With the tooling focus, "porcelain" commands are the primary scope of what is included. Features that require
more user interaction (such as resolving merge conflicts) are intentionally excluded.

It also provides a Gradle plugin to easily get a Grgit instance for the build's repository.

## Documentation

**NOTE:** grgit is available from JCenter or the Gradle Plugin Portal. It is not published to Maven Central.

* [Documentation Site](http://ajoberstar.org/grgit/index.html)
* [Release Notes](https://github.com/ajoberstar/grgit/releases)

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
