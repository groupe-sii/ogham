#!/bin/sh

set -ex

# build pitest fix report aggregation
git clone --branch ogham https://github.com/aurelien-baudet/pitest.git $HOME/pitest
echo "Building pitest..."
cd $HOME/pitest
mvn versions:set -DnewVersion=OGHAM
mvn clean install -DskipTests -B
