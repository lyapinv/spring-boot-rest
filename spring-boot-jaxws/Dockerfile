FROM registry.access.redhat.com/openjdk/openjdk-11-rhel7
#FROM fabric8/java-jboss-openjdk8-jdk:1.5.2
#FROM openjdk:11
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} /application.jar
EXPOSE 9999
ENTRYPOINT ["java","-jar","/application.jar"]