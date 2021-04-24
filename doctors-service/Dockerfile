FROM openjdk:16
EXPOSE 8100
ADD target/*.jar app.jar
ENTRYPOINT ["java","-jar","--enable-preview","app.jar"]