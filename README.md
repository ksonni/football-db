# Football DB

[![CI](https://github.com/ksonni/football-db/actions/workflows/ci.yml/badge.svg)](https://github.com/ksonni/football-db/actions/workflows/ci.yml)

Football DB is an API service that allows users to search a database football (soccer) players from different clubs across the world using highly customizable search and sort queries.

## Technical overview

- Rest API built with Java and Spring
- Checkstyle to enforce coding standards
- Schema management with [Liquibase](https://www.liquibase.org/) to automate database migrations
- Continuous Integrarion with GitHub Actions
- Spring Security and [token bucket](https://en.wikipedia.org/wiki/Token_bucket) rate limiting to secure the application
- Deployed inside Docker containers
- JUnit 5 and Mockito for testing
- Open API documentation & Swagger UI support


## Using the API

Demo and Open API can be found by navigating to /swagger-ui/index.html

The main endpoints are:
- `api/v1/leagues` - Info about different football leagues
- `api/v1/clubs`- Club stats such as budget, rating etc.
- `api/v1/players` - Player stats such as average penalty accuracy, height, monetary value, playing position etc.

All the above endpoints support a standard querying interface using URL query paramers. For eg:<br/>
`/api/v1/players?height=180&preferredFoot=LEFT&position=ST&sort=fullName`<br/>
will lookup all players who are 180 cm tall, are left footed and are strikers, sorted by their full name.

Special modifiers can be applied to the query parameters to perform different types of lookups:<br/>
`/api/v1/players?lt:height=180&sort=desc:fullName`<br/>
Note that `lt:` and `desc:` modifiers have been added which will now lookup players who are shorter than 180 cm, sorted in descending order of their full name.

The following modifiers are supported:
- `lt:` - less than<br/>
- `gt:` - greater than<br/>
- `lte:` - less than or equals<br/>
- `gte:` - greater than or equals<br/>
- `in:` - search for matches containing the value<br/>
- `or:` - applies the query parameter using logical OR instead of AND<br/>

All responses are in JSON format.

## Building the project

### System requirements

- Mac or Linux OS (The output jar can run on Windows, but dev scripts won't)
- Java 11 + JDK
- Docker

### SSL setup

To stay as close to prod as possible, a self signed SSL certificate is needed for development. To generate one:

`cd backend/ssl && bash ./gen-dev-certs.sh`

This will be stored in a folder called `.certs` which will be ignored by git.

### Environment setup

The project requires some API keys and secrets to function properly. This is provided using a file called `.env` (which again is ignored by git). So copy the contents of `.env-template` in the root of the project to a new file called `.env` and supply configuration data as prompted in the file.

### Running

`cd` to the `backend` folder and run `bash ./run-debug`. The service will then be available on `localhost`. 

### Debugging

The above script runs the containers using `docker-compose-debug.yml` which is basically a debug configuration that exposes additional ports to enable debugging:
- Port `8000` accepts remote JVM debugger connections. IDEs like IntelliJ can use this for setting up breakpoints and debugging code.
- Port `3306` exposes the MySQL database for connections from 3rd party GUI apps etc.

To hide these ports and run in prod mode instead, `cd` to the root of the project and simply run:

`docker-compose up`

which will only expose port `80` and `443` to access the server.

## Database container

The database container, along with the MySQL server also includes some lightweight jars and scripts to make database maintenance and rollbacks possible.
To access these scripts, start an interactive shell session inside the database container and:

`cd /liquibase/db/scripts` 

These scripts invoke the [Liquibase](https://www.liquibase.org/) schema versioning tool to perform tasks like rolling the datbase back to the previous version etc.
