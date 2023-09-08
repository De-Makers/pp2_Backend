# base-image
FROM openjdk:17
# jar file 경로
ARG JAR_FILE=build/libs/*.jar
# 파일 컨테이너로 복사
COPY ${JAR_FILE} pp2.jar
# jar 파일 실행
ENTRYPOINT ["java", "-jar", "/pp2.jar"]