# Contributing Guide

We'd love your help!

### Env Prerequisites

Make sure you have the following env versions:

```shell
gradle --version # v7.4.2
java --verson # openjdk 11.0.2 2019-01-15
```

### Fork

In the interest of keeping this repository clean and manageable, you should work from a fork. To create a fork, click the 'Fork' button at the top of the repository, then clone the fork locally using `git clone git@github.com:USERNAME/otel-js.git`.

You should also add this repository as an "upstream" repo to your local copy, in order to keep it up to date. You can add this as a remote like so:

```bash
git remote add upstream https://github.com/cisco-open/otel-java.git

#verify that the upstream exists
git remote -v
```

To update your fork, fetch the upstream repo's branches and commits, then merge your main with upstream's main:

```bash
git fetch upstream
git checkout main
git merge upstream/main
```

#### Setup repo

After the clone you will want to install all repo deps, run:

```bash
./gradlew assemble
```

This will install the main package deps and then all the scope packages deps (and also will build the sub packages)

To run all tests (UT and smoke-tests):

```bash
./gradlew test
```

After setting up the repo you will be able to work with every scope package individually (using the familiar npm commands)

#### Apply lint
Run:
```shell
./gradlew spotlessApply
```

#### Release

The release is manual. in `otel-java/build.gradle`, update the version to the new version.
Then, build the agent:
```bash
./gradlew build
```

Take the agent from: `otel-java/agent/build/libs/agent-x.x.x-all.jar`, and create a new release in the github repo, with the new version.
Don't forget to upload the agent with the new github release

#### Conventional commit

The Conventional Commits specification is a lightweight convention on top of commit messages. It provides an easy set of rules for creating an explicit commit history; which makes it easier to write automated tools on top of. This convention dovetails with SemVer, by describing the features, fixes, and breaking changes made in commit messages. You can see examples [here](https://www.conventionalcommits.org/en/v1.0.0-beta.4/#examples).
We use [commitlint](https://github.com/conventional-changelog/commitlint) and [husky](https://github.com/typicode/husky) to prevent bad commit message.
For example, you want to submit the following commit message `git commit -s -am "my bad commit"`.
You will receive the following error :

```text
✖   type must be one of [ci, feat, fix, docs, style, refactor, perf, test, revert, chore] [type-enum]
```

Here's an example that will pass the verification: `git commit -s -am "chore(cisco-telescope): update deps"`
