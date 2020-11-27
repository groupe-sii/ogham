#!/bin/sh

set -e

java_version=$(java -version 2>&1 | grep -i version | cut -d'"' -f2)
major=$(echo $java_version | cut -d'.' -f1)
[ "1" = "$major" ] \
  && echo "$java_version" | cut -d'.' -f2 \
  || echo "$java_version" | cut -d'.' -f1
#JAVA_RELEASE_VERSION=$(cat "$HOME/java-version.info")