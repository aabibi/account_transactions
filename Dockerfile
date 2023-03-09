FROM maven:3.6.3-jdk-11 AS build
COPY src /usr/src/app/src
COPY pom.xml /usr/src/app
RUN mvn -f /usr/src/app/pom.xml clean package -DskipTests=true

FROM openjdk:13-jdk-alpine
ARG JAR_FILE=target/*.jar
COPY --from=build /usr/src/app/${JAR_FILE}  app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]
