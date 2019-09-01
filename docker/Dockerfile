FROM openjdk:8-alpine

COPY build/libs/*.jar /BravoCI/bravoci.jar
COPY docker/configs/application.properties /BravoCI/
COPY docker/configs/queue.properties /BravoCI/

WORKDIR /BravoCI

CMD java -jar bravoci.jar
