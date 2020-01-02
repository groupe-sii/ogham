#!/bin/sh

set -ex

./mvnw clean install javadoc:javadoc -Dmaven.test.redirectTestOutputToFile=true -fae -B | grep -Ei '(error|warn)'
