# Football DB

[![CI](https://github.com/ksonni/football-db/actions/workflows/ci.yml/badge.svg)](https://github.com/ksonni/football-db/actions/workflows/ci.yml)

Football DB is an API service that allows users to search a database of football (soccer) players from different clubs 
across the world using highly customizable search and sort queries with Graph QL. The query implementation is reflection
& code-generation based, making it quite simple to implement additional endpoints.

Interactive GraphQL interface (has docs and autocomplete): https://football-db.k-sonni.com/graphiql

For e.g. if you were interested in finding professional right-footed 
football players who aren't Ronaldo, have a certain contract value and also fetch some other info about football clubs,
you could make a single GraphQL request like this:

(Note that the query also supports custom enum values like LEFT/RIGHT etc.)

<pre>
query { 
  players(
    filter: {
      fullName: { contains: "Chris",  ne: "Christiano Ronaldo" },
      wageEuro: { gte: 8000 },
      preferredFoot: { eq: RIGHT }
    },
    sort: { wageEuro: { direction: DESC } },
    page: { page: 0, size: 5 }
  ) {
  	content { fullName, wageEuro, valueEuro, contractEndYear, clubId, preferredFoot }
    totalPages
  }
  clubs(filter: { name: { contains: "Manchester" } }) { 
    content { name, leagueId } 
  }
}
</pre>

Swagger UI docs for the management REST endpoints (these are authenticated APIs) can be found at: https://football-db.k-sonni.com/swagger-ui/index.html

## Technical overview

- GraphQL API built with Java and Spring
  - The implementation of the query mechanism is in `app/src/main/.../query` and uses reflection to construct Spring Data specifications to execute queries
  - Code-generation is used to generate Java models from the GraphQL schema
- Spring Security and [token bucket](https://en.wikipedia.org/wiki/Token_bucket) rate limiting to secure the application
- Schema management with [Liquibase](https://www.liquibase.org/) to automate database migrations
- Continuous Integration with GitHub Actions
- Runs in a containerised docker-compose setup
- JUnit 5 and Mockito for testing
- Checkstyle to enforce coding standards

## Building the project

### System requirements

- Mac or Linux OS (The output jar can run on Windows, but dev scripts won't)
- Java 17 + JDK
- Docker

### SSL setup

To stay as close to prod as possible, a self-signed SSL certificate is needed for development. To generate one:

`cd app/ssl && bash ./gen-dev-certs.sh`

This will be stored in a folder called `.certs` which will be ignored by git.

### Environment setup

The project requires some config data to function properly. This is provided in development using a file called `.env` (which again is ignored by git). 
So copy the contents of `.env-template` in the root of the project to a new file called `.env` and supply configuration data as prompted in the file.

### Running

`cd` to the `app` folder and run `./run-debug`. The service will then be available on `localhost`. 

### Debugging

The above script runs the containers using `docker-compose-debug.yml` which is a debug configuration that exposes additional ports only for debugging:
- Port `8000` accepts remote JVM debugger connections. IDEs like IntelliJ can use this for setting up breakpoints and debugging code.
- Port `3306` exposes the MySQL database for connections from 3rd party GUI apps etc.

To hide these ports and run in prod mode instead, `cd` to the root of the project and simply run:

`docker-compose up`

which will only expose ports `80` and `443` to access the server.

## Database container

The database container, along with the MySQL server also includes some lightweight jars and scripts to make database maintenance and rollbacks possible.
To access these scripts, start an interactive shell session inside the database container and:

`cd /liquibase/db/scripts` 

These scripts invoke the [Liquibase](https://www.liquibase.org/) schema versioning tool to perform tasks like rolling the database back to the previous version etc.

## Dataset accreditation

Sincere thanks to the user Alex (cashncarry) for making the dataset available on Kaggle:
https://www.kaggle.com/cashncarry/fifa-22-complete-player-dataset

It was originally published by the author mentioned above under the [CC BY-NC-SA 4.0 license](https://creativecommons.org/licenses/by-nc-sa/4.0/) 
and usage of any data extracted from this API service is dictated by the same license.
