FROM registry.access.redhat.com/openjdk/openjdk-11-rhel7
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} /application.jar
EXPOSE 8082
#ENTRYPOINT ["java","-jar","/application.jar"]
ENTRYPOINT exec java $JAVA_OPTS -jar /application.jar