# twitter-api-demo
Twitter API Demo

## Building application

### Pre-requisites

- JDK 21+
- maven 3
- docker CLI

### Option 1: Building Executable JAR

To create an `executable jar`, simply run:

```sh
 mvn clean package
```

### Option 2: Building a non-native OCI Images

To create a non-native OCI docker image, simply run:

```sh
mvn clean spring-boot:build-image
```

### Option 3: Building native image with GraalVM

To create a `native image`, Run the following command

```sh
mvn -Pnative clean native:compile 
```

## Running the native application

To run the demo using docker, invoke the following:

```sh
docker run --rm -p 8080:8082 springdoc-openapi-spring-boot-2-webflux:3.1.6-SNAPSHOT
```
