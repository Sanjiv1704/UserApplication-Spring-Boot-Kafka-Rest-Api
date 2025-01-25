FROM amazoncorretto:18
WORKDIR /app
# Copy the built JAR file into the container
COPY build/libs/synchrony-0.0.1-snapshot.jar synchrony-0.0.1-snapshot.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/synchrony-0.0.1-snapshot.jar"]
