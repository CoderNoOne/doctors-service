FROM openjdk:16
EXPOSE 8080
ADD target/*.jar app.jar
ENTRYPOINT ["java","-jar","--enable-preview","app.jar"]