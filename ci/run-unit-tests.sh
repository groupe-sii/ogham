#!/bin/sh

./mvnw test -Dmaven.javadoc.skip=true -Dmaven.test.redirectTestOutputToFile=true -fae -B -q