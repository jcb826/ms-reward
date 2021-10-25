FROM openjdk:16
ARG JAR_FILE=build/libs/ms-reward-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} ms-reward-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java", "-jar", "/ms-reward-0.0.1-SNAPSHOT.jar"]
EXPOSE 8092
