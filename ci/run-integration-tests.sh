#!/bin/sh

set -ex

./mvnw verify \
	-Dmaven.javadoc.skip=true \
	-Dmaven.test.redirectTestOutputToFile=true \
	-fae \
	-B \
	-q