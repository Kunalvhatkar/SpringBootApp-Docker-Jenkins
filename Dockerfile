FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw

COPY src ./src

RUN ./mvnw clean package -DskipTests -B

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "target/demo-app-SNAPSHOT.jar"]