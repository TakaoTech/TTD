FROM gradle:8.4-jdk17 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle server:buildFatJar --no-daemon

FROM amazoncorretto:17.0.11
EXPOSE 8080:8080
RUN mkdir /app
COPY --from=build /home/gradle/src/server/build/libs/*.jar /app/kotlin-italia-server.jar
CMD ["java","-jar","/kotlin-italia-server.jar"]