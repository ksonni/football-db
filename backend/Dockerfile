FROM openjdk:17
COPY ssl/.certs certificates
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT java -jar $JAVA_OPTS /app.jar
