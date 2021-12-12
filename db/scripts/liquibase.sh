set -xe

# Setup environment
LIQUIBASE_PATH=deps/liquibase-core-4.6.1.jar
DB_FILES_PATH=../../backend/src/main/resources/db/
BIN_PATH=./.bin
MYSQL_CONNECTOR_ABS_PATH=$(pwd)/deps/mysql-connector-java-8.0.27.jar

# Temp working directory
rm -rf $BIN_PATH
mkdir $BIN_PATH

# Copy migration files
cp -r $DB_FILES_PATH $BIN_PATH/db

# Execute liquibase command
cd $BIN_PATH && java -jar ../$LIQUIBASE_PATH $@ \
    --username=$SPRING_DATASOURCE_USERNAME \
    --password=$SPRING_DATASOURCE_PASSWORD \
    --url=$SPRING_DATASOURCE_URL \
    --changeLogFile=db/migrations/change-log.xml \
    --classpath=$MYSQL_CONNECTOR_ABS_PATH

# Cleanup temp folder
rm -rf ../$BIN_PATH
