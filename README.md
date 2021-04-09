# RadarCOVID NotifyMe Fork

<p align="center">
    <a href="https://github.com/RadarCOVID/radar-covid-backend-notifyme-server/commits/" title="Last Commit"><img src="https://img.shields.io/github/last-commit/RadarCOVID/radar-covid-backend-notifyme-server?style=flat"></a>
    <a href="https://github.com/RadarCOVID/radar-covid-backend-notifyme-server/issues" title="Open Issues"><img src="https://img.shields.io/github/issues/RadarCOVID/radar-covid-backend-notifyme-server?style=flat"></a>
    <a href="https://github.com/RadarCOVID/radar-covid-backend-notifyme-server/blob/master/LICENSE" title="License"><img src="https://img.shields.io/badge/License-MPL%202.0-brightgreen.svg?style=flat"></a>
</p>

Fork from [NotifyMe SDK Backend](https://github.com/notifyme-app/notifyme-sdk-backend) with the following changes:

- Functional:
  - When a venue gets a case code by health authorities, owner must set the code in the web application and upload their private QR.
  - Web application will check the case code. If it's valid, check service will return a token and code will be redeemed.
  - Web application will connect to this service (NotifyMe Server) with the JWT and trace keys.

  You can find these changes in this [package](./notifyme-sdk-backend/notifyme-sdk-backend-ws/src/main/java/ch/ubique/notifyme/sdk/backend/ws/radarcovid).

- Technical:
  - OpenAPI generation using [springdoc-openapi](https://github.com/springdoc/springdoc-openapi).
  - Upgraded versions (PostgreSQL, Tomcat,...).
  - Degraded Spring Boot to 2.3.x version for AWS compatibility.

## Installation and Getting Started

### Building from Source

To build the project, you need to run this command:

```shell
mvn clean package -P <environment>
```

Where `<environment>` has these possible values:

- `radarcovid-local`. To run the application from local (eg, from IDE o from Maven using `mvn spring-boot:run`). It is the default profile, using [`application.yaml`](./notifyme-sdk-backend/notifyme-sdk-backend-ws/src/main/resources/application.yaml) configuration file. If any properties need to be modified, you can create application-radarcovid-local.yml configuration file.
- `radarcovid-docker`. To run the application in a Docker container with `docker-compose`, using [`application.yaml`](./notifyme-sdk-backend/notifyme-sdk-backend-ws/src/main/resources/application.yaml) configuration file. If any properties need to be modified, you can create application-docker.yml configuration file.
- `radarcovid-pre`. To run the application in the Preproduction environment.  Preproduction environment properties are configured in the infrastructure.
- `radarcovid-pro`. To run the application in the Production environment.  Production environment properties are configured in the infrastructure.

The project also uses Maven profile `aws-env` to include dependencies when it is running on AWS environment, so the compilation command for Preproduction and Production environments would be:

```shell
mvn clean package -P radarcovid-pre,aws-env
mvn clean package -P radarcovid-pro,aws-env
```

All profiles will load the default [configuration](./notifyme-sdk-backend/notifyme-sdk-backend-ws/src/main/resources/application.yaml).

Application uses [The Twelve-Factor App - Config](https://12factor.net/config) approach so configuration is stored in _environment variables_.

## Support and Feedback
The following channels are available for discussions, feedback, and support requests:

| Type       | Channel                                                |
| ---------- | ------------------------------------------------------ |
| **Issues** | <a href="https://github.com/RadarCOVID/radar-covid-backend-notifyme-server/issues" title="Open Issues"><img src="https://img.shields.io/github/issues/RadarCOVID/radar-covid-backend-notifyme-server?style=flat"></a> |

## Contribute

If you want to contribute with this exciting project follow the steps in [How to create a Pull Request in GitHub](https://opensource.com/article/19/7/create-pull-request-github).

More details in [CONTRIBUTING.md](./CONTRIBUTING.md).

## License

This Source Code Form is subject to the terms of the [Mozilla Public License, v. 2.0](https://www.mozilla.org/en-US/MPL/2.0/).
