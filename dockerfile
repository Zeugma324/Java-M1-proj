FROM openjdk:17-jdk-slim

WORKDIR /usr/src/app

COPY lib/mysql-connector-j-9.1.0.jar /usr/src/app/lib/
COPY data/ data/