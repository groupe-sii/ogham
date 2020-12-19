#!/bin/sh

set -e${DEBUG_COMMANDS:+x}

./mvnw clean install javadoc:javadoc \
	-Dmaven.test.redirectTestOutputToFile=true \
	-fae \
	-B \
	| grep -Ei '(error|warn)'
