#!/bin/sh

set -e${DEBUG_COMMANDS:+x}

DIR="${CLASSPATH_TESTS_ROOT_DIR:-$HOME/classpath-tests}/$TEST_FOLDER"

# TODO: can't use -T 2.5C anymore due to fixed ports in some tests
$DIR/./mvnw verify -f "$DIR/pom.xml" \
	-Dsurefire.rerunFailingTestsCount=3 \
	-Dfailsafe.rerunFailingTestsCount=3 \
	-Dmaven.javadoc.skip=true \
	-Dmaven.test.redirectTestOutputToFile=true \
	-fae \
	-B $*