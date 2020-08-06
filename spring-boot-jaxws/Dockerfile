FROM registry.access.redhat.com/openjdk/openjdk-11-rhel7
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} /application.jar
#ENTRYPOINT ["java","-jar","/application.jar"]
ENTRYPOINT exec java $JAVA_OPTS -jar /application.jar