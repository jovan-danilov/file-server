# Source code
Projects has modules:
- app - application
- docker - docker config files
- tests - integration (API) tests

# Environment (build & runtime)
- Java 21+
- Docker

# Default app configuration
- HTTP port: `8090`
- root files folder: `./root-dir` (from starting dir)
- API URL: `<host>:8090/jsonrpc/v1/files`

# Build 
In console from root source dir run: 
-`mvnw clean install` 

# Run
## run locally
In console run:
- `cd app` 
- `../mvnw spring-boot:run`

## run in IDEA
Run `FileServerApp.java`

## run in docker
`docker run --name file-server -p 8090:8090 com.id/file-server:0.0.1-SNAPSHOT`

## run integration tests locally
`mvnw -Pintegration-tests clean install` 

