set -xe

rm -rf deps
mkdir deps

curl -L "https://repo1.maven.org/maven2/org/liquibase/liquibase-core/4.6.1/liquibase-core-4.6.1.jar" \
    --output deps/liquibase-core-4.6.1.jar

curl -L "https://repo1.maven.org/maven2/mysql/mysql-connector-java/8.0.27/mysql-connector-java-8.0.27.jar" \
     --output deps/mysql-connector-java-8.0.27.jar
