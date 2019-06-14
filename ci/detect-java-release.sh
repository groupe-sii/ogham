#!/bin/sh

java_version=$(java -version 2>&1 | grep -i version | cut -d'"' -f2)
major=$(echo $java_version | cut -d'.' -f1)
[ "1" = "$major" ] \
  && echo "$java_version" | cut -d'.' -f2 > "$HOME/java-version.info" \
  || echo "$java_version" | cut -d'.' -f1 > "$HOME/java-version.info"
JAVA_RELEASE_VERSION=$(cat "$HOME/java-version.info")