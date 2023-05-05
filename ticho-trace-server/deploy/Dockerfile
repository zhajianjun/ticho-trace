FROM java:8u92-jre-alpine
VOLUME /tmp
ENV ENC_PASSWORD=""
COPY target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "-Xms256m", "-Xmx512m", "-Xmn170m", "-Dserver.port=8080", "-Djasypt.encryptor.password=${ENC_PASSWORD}", "/app.jar"]