FROM openjdk:21-jdk-slim
ARG JAR_FILE=build/libs/backend-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
# 포트 설정
EXPOSE 8080
# 커맨드 실행
ENTRYPOINT ["java","-jar","/app.jar"]