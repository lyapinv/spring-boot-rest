#FROM openjdk:14-jdk-alpine
FROM registry.access.redhat.com/openjdk/openjdk-11-rhel7
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} /application.jar

# JMX
#EXPOSE 9010
# Rest entrypoint
#EXPOSE 8081
#ENTRYPOINT [ "/opt/app/entrypoint.sh" ]
#ENTRYPOINT exec java $JAVA_OPTS $WS_URL -jar /application.jar
ENTRYPOINT exec java $JAVA_OPTS -jar /application.jar
