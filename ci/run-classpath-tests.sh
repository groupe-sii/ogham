#!/bin/sh

set -ex

dir="${ROOT_TEST_DIR:-$HOME/classpath-tests}"

# TODO: can't use -T 2.5C anymore due to fixed ports in some tests
$dir/$TEST_FOLDER/./mvnw verify -f "$dir/$TEST_FOLDER/pom.xml" \
	-Dsurefire.rerunFailingTestsCount=3 \
	-Dfailsafe.rerunFailingTestsCount=3 \
	-Dmaven.javadoc.skip=true \
	-Dmaven.test.redirectTestOutputToFile=true \
	-fae \
	-B $*
