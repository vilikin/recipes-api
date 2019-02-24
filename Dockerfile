FROM openjdk:8-jdk

COPY . .

RUN ./gradlew clean build