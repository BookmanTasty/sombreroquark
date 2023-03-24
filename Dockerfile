FROM eclipse-temurin:17.0.6_10-jdk-alpine
RUN mkdir /opt/app
COPY target/quarkus-app /opt/app
EXPOSE 8080
CMD ["java", "-jar", "-Dquarkus.http.host=0.0.0.0", "-Djava.util.logging.manager=org.jboss.logmanager.LogManager", "/opt/app/quarkus-run.jar"]