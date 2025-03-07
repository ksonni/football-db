set -xe

COMPOSE_FILE=../docker-compose-debug.yml

./gradlew build -x test -x checkstyleMain -x checkstyleTest
docker-compose -f $COMPOSE_FILE build db
docker-compose -f $COMPOSE_FILE up -d db
docker-compose -f $COMPOSE_FILE build app
docker-compose -f $COMPOSE_FILE up app
