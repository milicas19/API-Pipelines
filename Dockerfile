FROM openjdk:11
LABEL maintainer = "milicas"
ADD target/project-first-0.0.1-SNAPSHOT.jar api-pipelines.jar
ENTRYPOINT ["java", "-jar", "api-pipelines.jar"]