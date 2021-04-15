FROM openjdk:16
MAINTAINER CoderNoOne firelight.code@gmail.com

EXPOSE 8080
WORKDIR ./usr/webflux-app
ADD target/doctors-service-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "--enable-preview", "app.jar"]

#ARG DEPENDENCY=target/dependency
#COPY ${DEPENDENCY}/BOOT-INF/lib /app/lib
#COPY ${DEPENDENCY}/META-INF /app/META-INF
#COPY ${DEPENDENCY}/BOOT-INF/classes /app
#
#ENTRYPOINT ["java", "-cp", "app:app/lib/*", "--enable-preview", "-Djava.net.preferIPv4Stack=true", "com.app.DoctorsServiceApplication"]
