#!/bin/sh

set -ex

# TODO: can't use -T 2.5C anymore due to fixed ports in some tests
$HOME/classpath-tests/$TEST_FOLDER/./mvnw test -f "$HOME/classpath-tests/$TEST_FOLDER/pom.xml" -Dsurefire.rerunFailingTestsCount=3 -Dfailsafe.rerunFailingTestsCount=3 -Dmaven.javadoc.skip=true -Dmaven.test.redirectTestOutputToFile=true -fae -B #| grep -Ei '(error|exception|Tests run:)'