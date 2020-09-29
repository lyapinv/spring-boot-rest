FROM registry.access.redhat.com/openjdk/openjdk-11-rhel7
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} /application.jar
EXPOSE 8080
ENTRYPOINT exec java $JAVA_OPTS" -Djava.rmi.server.hostname="$POD_IP -jar /application.jar