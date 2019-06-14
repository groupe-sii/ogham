#!/bin/sh

./mvnw clean install javadoc:javadoc -Dmaven.test.redirectTestOutputToFile=true -fae -B | grep -Ei '(error|warn)'
