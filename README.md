# grgit

Groovy wrapper around JGit.

[![Build Status](https://travis-ci.org/ajoberstar/grgit.png?branch=master)](https://travis-ci.org/ajoberstar/grgit)

## What is this?

Grgit is a way to interact with Git repositories with a fairly fluent Groovy API. JGit can get pretty tedious to use, especially in a scripting context.

Grgit will also be the base for a rewrite of the [gradle-git plugin](https://github.com/ajoberstar/gradle-git).

## Where do I get it from?

Grgit is published on Bintray. The [package](https://bintray.com/ajoberstar/libraries/grgit) is available on JCenter, though some of the dependencies are on Maven Central.

```groovy
repositories { 
	jcenter()
	mavenCentral()
}

dependencies {
	compile 'org.ajoberstar:grgit:<version>'
}

```

## How do I use it?

### Getting a repository

The main entry point is the `Grgit` class, which provides three options to get a `RepositoryService`:

```
// new local repository
myrepo = Grgit.init(dir: '/home/me/repos/cool-proj')

// clone existing repository
myrepo = Grgit.clone(dir: '/home/me/repos/grgit', uri: 'git@github.com:ajoberstar/grgit.git')

// open an existing local repository
myrepo = Grgit.open('/home/me/repos/cool-proj')
```

More information about the options for these operations can be found in the Groovydoc:

* [init](http://ajoberstar.org/grgit/docs/groovydoc/org/ajoberstar/grgit/operation/InitOp.html)
* [clone](http://ajoberstar.org/grgit/docs/groovydoc/org/ajoberstar/grgit/operation/CloneOp.html)
* [open](http://ajoberstar.org/grgit/docs/groovydoc/org/ajoberstar/grgit/Grgit.html)

### Authentication

Authentication is still a work in progress, but SSH authentication should be supported if you have `ssh-agent` or `Pageant` running on your machine.

Tests and credentials support will be added in a future version.

### Interacting with a repository:

After getting a repository (`RepositoryService`) in one of the ways described above, you can interact with it in a few ways:

* General methods on the [RepositoryService](http://ajoberstar.org/grgit/docs/groovydoc/org/ajoberstar/grgit/service/RepositoryService.html), for example `head()` or `resolveCommit()`.
* An operation.

In general, any operation in Grgit provides two ways to call it:

```groovy
// map syntax
repo.checkout(branch: 'issue-123', createBranch: true)

// closure syntax
def commits = repo.log {
	skipCommits = 4
	maxCommits = 3
}
```

The following operations are currently implemented:

* [add](http://ajoberstar.org/grgit/docs/groovydoc/org/ajoberstar/grgit/operation/AddOp.html)
* [apply](http://ajoberstar.org/grgit/docs/groovydoc/org/ajoberstar/grgit/operation/ApplyOp.html)
* [checkout](http://ajoberstar.org/grgit/docs/groovydoc/org/ajoberstar/grgit/operation/CheckoutOp.html)
* [commit](http://ajoberstar.org/grgit/docs/groovydoc/org/ajoberstar/grgit/operation/CommitOp.html)
* [fetch](http://ajoberstar.org/grgit/docs/groovydoc/org/ajoberstar/grgit/operation/FetchOp.html)
* [log](http://ajoberstar.org/grgit/docs/groovydoc/org/ajoberstar/grgit/operation/LogOp.html)
* [push](http://ajoberstar.org/grgit/docs/groovydoc/org/ajoberstar/grgit/operation/PushOp.html)
* [reset](http://ajoberstar.org/grgit/docs/groovydoc/org/ajoberstar/grgit/operation/ResetOp.html)
* [revert](http://ajoberstar.org/grgit/docs/groovydoc/org/ajoberstar/grgit/operation/RevertOp.html)
* [remove](http://ajoberstar.org/grgit/docs/groovydoc/org/ajoberstar/grgit/operation/RemoveOp.html)
* [status](http://ajoberstar.org/grgit/docs/groovydoc/org/ajoberstar/grgit/operation/StatusOp.html)

## Where's feature X?

I've only added some base operations so far. There is plenty more to go, and you can see the [issue tracker](https://github.com/ajoberstar/grgit/issues) for planned features.

If an existing operation is missing an option, it means one of the following things:

- I don't have a use case for it.
- I wasn't able to put together a test that could describe/validate the behavior of the option.
- JGit doesn't support it, my hands are tied.

If you provide a pull request for a feature you want, with tests that describe the features behavior, I am more than happy to merge it. (And thank you in advance!) Feel free to just submit an issue, if you don't want or aren't ready to implement it yourself.

## Release Notes

### 0.1.0

* Initial release!
