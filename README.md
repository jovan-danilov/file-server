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
`docker run --name file-server -p 8090:8090 file-server:0.0.1-SNAPSHOT`

## run in k8s
1. tag docker image 
`docker tag file-server:0.0.1-SNAPSHOT <your_registry_url>/file-server:0.0.1-SNAPSHOT`

2. push docker image to registry
`docker push <your_registry_url>/file-server:0.0.1-SNAPSHOT`

3. run
- `cd helm/file-server`
- In values.yaml set "image.registryUrl" to your image registry
- `helm install fs .`

Default exposed URL: `<k8s_host>:30090/jsonrpc/v1/files`

## run integration tests locally
`mvnw -Pintegration-tests clean install` 
