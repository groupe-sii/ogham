#!/bin/sh

set -e${DEBUG_COMMANDS:+x}

# build pitest fix report aggregation
#git clone --branch ogham https://github.com/aurelien-baudet/pitest.git $HOME/pitest
#echo "Building pitest..."
#cd $HOME/pitest
#mvn versions:set -DnewVersion=OGHAM
#mvn clean install -DskipTests -B
MAVEN_OPTS="$MAVEN_OPTS -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn --no-transfer-progress"
MAVEN_WRAPPER_VERSION=3.8.8

git clone --branch compatibility/pitest-1.13.2 https://github.com/aurelien-baudet/pitest-descartes.git $HOME/pitest-descartes
echo "Building pitest-descartes..."
cd $HOME/pitest-descartes
mvn wrapper:wrapper -Dmaven=${MAVEN_WRAPPER_VERSION}
./mvnw versions:set -DnewVersion=OGHAM
./mvnw clean install \
  -DskipTests \
  -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn \
  --no-transfer-progress \
  -B

git clone --branch compatibility/pitest-1.13.2 https://github.com/aurelien-baudet/pitmp-maven-plugin.git $HOME/pitmp-maven-plugin
echo "Building pitmp-maven-plugin..."
cd $HOME/pitmp-maven-plugin
mvn wrapper:wrapper -Dmaven=${MAVEN_WRAPPER_VERSION}
./mvnw versions:set -DnewVersion=OGHAM
./mvnw clean install \
  -DskipTests \
  -Ddescartes.version=OGHAM \
  -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn \
  --no-transfer-progress \
  -B
