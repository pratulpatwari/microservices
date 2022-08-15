# Start with a base image containing Java runtime
FROM azul/zulu-openjdk:11

# Add Maintainer Info
LABEL maintainer="pratul_patwari@yahoo.com"

# Add a volume pointing to /tmp
VOLUME /tmp

# Make port 8888 available to the work outside this container
EXPOSE 9090

# The application's jar file
ARG JAR_FILE=./target/user-service-0.0.1-SNAPSHOT.jar

# Add the application's jar to the container
ADD ${JAR_FILE} user-service.jar

ENV JAVA_OPTS=""

# Run the jar file
# ENTRYPOINT ["java", "-jar", "user-service.jar"]
ENTRYPOINT ["sh","-c", "java $JAVA_OPTS -jar user-service.jar"]
