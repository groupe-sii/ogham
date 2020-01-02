#!/bin/sh

set -ex

# build pitest fix report aggregation
git clone --branch fix/report-aggregation https://github.com/aurelien-baudet/pitest.git $HOME/pitest
echo "Building pitest..."
cd $HOME/pitest
mvn clean install -DskipTests -B
