FROM maven:3-openjdk-17-slim AS build

COPY . .

RUN mvn clean package

FROM openjdk:17-alpine
EXPOSE 8080

COPY --from=build /target/todolist-1.0.0.jar app.jar

ENTRYPOINT [ "java", "-jar", "app.jar" ]
