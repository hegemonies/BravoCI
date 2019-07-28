FROM gradle:latest

RUN git clone https://github.com/hegemonies/BravoCI

WORKDIR /home/gradle/BravoCI
USER root
RUN gradle build

CMD [ "gradle", "bootRun" ]