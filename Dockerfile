FROM openjdk:17-alpine
ADD ./target/*.jar /app/
RUN  mv /app/*.jar /app/app.jar
WORKDIR /app/
CMD ["java", "-jar", "app.jar"]