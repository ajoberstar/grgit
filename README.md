# grgit

[![Maintainer Status](http://stillmaintained.com/ajoberstar/grgit.svg)](http://stillmaintained.com/ajoberstar/grgit)
[![Travis](https://img.shields.io/travis/ajoberstar/grgit.svg?style=flat-square)](https://travis-ci.org/ajoberstar/grgit)
[![GitHub license](https://img.shields.io/github/license/ajoberstar/grgit.svg?style=flat-square)](https://github.com/ajoberstar/grgit/blob/master/LICENSE)
[![Bintray](https://img.shields.io/bintray/v/ajoberstar/libraries/org.ajoberstar%3Agrgit.svg?style=flat-square)](https://bintray.com/ajoberstar/libraries/org.ajoberstar%3Agrgit/_latestVersion)

## Why do you care?

[JGit](https://eclipse.org/jgit/) provides a powerful Java API for interacting with Git repositories. However,
in a Groovy context it feels very cumbersome, making it harder to express the operations you want to perform
without being surrounded by a lot of cruft.

## What is it?

Grgit is a wrapper over JGit that provides a fluent API for interacting with Git repositories in Groovy-based
tooling.

With the tooling focus, "porcelain" commands are the primary scope of what is included. Features that require
more user interaction (such as resolving merge conflicts) are intentionally excluded.

## Usage

**NOTE:** Grgit requires Java 7 (or higher).

* [Release Notes](https://github.com/ajoberstar/grgit/releases)
* [Usage examples and API documentation](http://ajoberstar.org/grgit/docs/groovydoc/index.html?org/ajoberstar/grgit/Grgit.html)
* [Authentication](http://ajoberstar.org/grgit/docs/groovydoc/index.html?org/ajoberstar/grgit/auth/AuthConfig.html)

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
