# lib-airfoil-plugin-jobs

A kotlin/ktor library that provides a background jobs plugin.

## Development Setup

**Ensure your development machine is setup for the Airfoil Framework**

[Common Development Setup](https://github.com/airfoil-io/airfoil-docs/common-development-setup/README.md)

**Clone the repository**

```sh
$ git clone git@github.com:airfoil-io/lib-airfoil-plugin-jobs.git
$ cd lib-airfoil-plugin-jobs
```

**Build the project**

```sh
$ ./gradlew build
```

**Clean rebuild of the project**

```sh
$ ./gradlew clean build
```

**Build using local snapshots**

By default, Airfoil dependencies are retrieved by released versions from the GitHub packages maven repository. If you need to do active development while making changes in one or more of those dependencies, you can build against local snapshots. To do that:

1. Clone all of the Airfoil dependencies for this project locally and build them. Snapshots will be published to your local repository.
2. Build this project with the `useSnapshots` property

```sh
$ ./gradlew build -PuseSnapshots
```
