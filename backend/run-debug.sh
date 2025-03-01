set -xe

COMPOSE_FILE=../docker-compose-debug.yml

./gradlew build -x test
docker-compose -f $COMPOSE_FILE build db
docker-compose -f $COMPOSE_FILE up -d db
docker-compose -f $COMPOSE_FILE build backend
docker-compose -f $COMPOSE_FILE up backend
