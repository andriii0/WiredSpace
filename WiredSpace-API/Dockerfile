FROM eclipse-temurin:17-jdk AS build

WORKDIR /app

COPY gradle gradle
COPY gradlew .
COPY settings.gradle.kts .
COPY build.gradle.kts .

RUN chmod +x gradlew

COPY src src

RUN ./gradlew build -x test

FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
