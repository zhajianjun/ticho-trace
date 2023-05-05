FROM java:8u92-jre-alpine
VOLUME /tmp
ENV ENC_PASSWORD=""
COPY target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "-Djasypt.encryptor.password=$ENC_PASSWORD", "/app.jar"]