#!/bin/sh

set -e${DEBUG_COMMANDS:+x}

./mvnw verify \
	-Dmaven.javadoc.skip=true \
	-Dmaven.test.redirectTestOutputToFile=true \
	-fae \
	-B \
	-q