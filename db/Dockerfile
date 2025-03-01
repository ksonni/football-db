FROM mysql:8.0-debian

RUN apt-get update
RUN apt-get -y install curl
RUN apt-get -y install openjdk-17-jre-headless

WORKDIR /liquibase

COPY db/scripts db/scripts
RUN cd db/scripts && bash install-deps.sh
COPY app/src/main/resources/db app/src/main/resources/db 
