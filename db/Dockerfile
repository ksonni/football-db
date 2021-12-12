FROM mysql:8

RUN apt-get update
RUN apt-get -y install curl
RUN apt-get -y install openjdk-11-jre-headless

WORKDIR /liquibase

COPY db/scripts db/scripts
RUN cd db/scripts && bash install-deps.sh
COPY backend/src/main/resources/db backend/src/main/resources/db 
