#!/bin/sh

$HOME/classpath-tests/$TEST_FOLDER/./mvnw test -T 2.5C -f "$HOME/classpath-tests/$TEST_FOLDER/pom.xml" -Dmaven.test.redirectTestOutputToFile=true -fae -q -B #| grep -Ei '(error|exception|Tests run:)'