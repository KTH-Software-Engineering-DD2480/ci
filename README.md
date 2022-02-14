
# CI

[![Java CI with Gradle](https://github.com/KTH-Software-Engineering-DD2480/ci/actions/workflows/gradle.yml/badge.svg)](https://github.com/KTH-Software-Engineering-DD2480/ci/actions/workflows/gradle.yml)

> Group 17
This is an implementation of the CI routine described [here](https://kth.instructure.com/courses/31884/assignments/185708).


## Description

This is a small CI server that is triggered by webhooks from the Github API. The CI server clones and executes Gradle build & test on the repository when and where a push event has occured. Depending on the result of the gradle build and testing, it sets the commit status which is also displayed in a browser.

The server keeps the history of the past build logs in a local folder, more details about the log can be found [here](https://github.com/KTH-Software-Engineering-DD2480/ci/tree/main/logs), note that this **README** is only a guide (the [Log_entry](https://github.com/KTH-Software-Engineering-DD2480/ci/blob/main/src/main/java/ci/Log_entry.java) class determines what is actually saved to log files). Additionally, each feature has its corresponding unit-test which is triggered by the Github Actions which served as a CI server during the development.

## Prerequisites

To test the service you will need the following:

- Java 11+
- Gradle
- Ngrok
- Github repository

## Usage

1. Get your personal github token and store it as an environment variable `GITHUB_ACCESS_TOKEN`
2. (Optional) Generate an URL with ngrok (specify the correct local port)
3. Add the URL to the Github Webhook
4. Add the secret you created with the webhook to the environment variable `CI_GITHUB_SECRET`.
5. Start the server with `gradle run` 
6. Make a commit and check that the server displays the status
7. Logs from the CI server are made available in the browser at the link `http://ci-server.whatever/view-logs` and locally at `{path_to_local_dir}/logs/`

## Building from Source

This project uses Gradle to build. You can either choose to install it from [here](https://gradle.org/) or use one of the build scripts (`gradlew` on *nix or `gradlew.bat` on Windows) from the terminal.

### Compiling

```sh
gradle build
```

### Running the Test Suite

```sh
gradle test
```


# Contributions

We work by submitting and then merging Pull Requests (PRs). Every PR must address an open Issue.

When creating a branch to work on an issue, create a new branch with the following name: `<descriptive-title>-#<issue-number>`. For example: `input-class-#5`, `point-math-#7`, etc.

Every commit must start with one of the following:

- `feat:` if a new feature was added
- `fix:` if a bug was fixed
- `doc:` if documentation was added
- `refactor:` if code was restructured/renamed
- `wip:` if unfinished work has to be commited temporarily (discouraged, consider squashing the commits afterward)


# Contributors

This project is a group effort by:

- Christofer Nolander [cnol@kth.se](mailto:cnol@kth.se)
    - Webhook setup & handler
    - REST API endpoint
    - Frontend
- Jiayi Guo [jiayig@kth.se](mailto:jiayig@kth.se)
    - Clone functionality
    - JSONparser test
- Kunal Bhatnagar [kunalb@kth.se](mailto:kunalb@kth.se)
    - Gradle build functionality
    - Gradle test functionality
- Mark Bergrahm [bergrahm@kth.se](mailto:bergrahm@kth.se)
    - Queue system
    - Persistent logs
- Philip Salqvist [phisal@kth.se](mailto:phisal@kth.se)
    - Update commit message
    - Linking each functionalities together
## Way of Working

[https://docs.google.com/document/d/1PODeI8va49XdJgh6EDSfmRnwnGFsB_-AD8VI5771rP0/edit#](https://docs.google.com/document/d/1PODeI8va49XdJgh6EDSfmRnwnGFsB_-AD8VI5771rP0/edit#)
