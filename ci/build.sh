#!/bin/sh

set -ex

./mvnw clean install -DskipTests=true -Dmaven.javadoc.skip=true -Dskip.integration.tests=true -Dskip.unit.tests=true -B -q
