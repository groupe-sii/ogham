#!/bin/sh

set -ex

./mvnw test -Dmaven.javadoc.skip=true -Dmaven.test.redirectTestOutputToFile=true -fae -B -q