#!/bin/sh

set -e${DEBUG_COMMANDS:+x}


./mvnw install post-site \
	-Pmutation-testing-aggregate-report-only \
	-Dmaven.test.redirectTestOutputToFile=true \
	-Dsurefire.useSystemClassLoader=false \
	-Dfailsafe.useSystemClassLoader=false \
	-Dsurefire.rerunFailingTestsCount=3 \
	-Dfailsafe.rerunFailingTestsCount=3 \
	-B \
	$*

# FIXME: mutation testing produces surefire and failsafe reports => must build, test and generate site again...
./mvnw install post-site \
	-Dmaven.test.redirectTestOutputToFile=true \
	-Dsurefire.useSystemClassLoader=false \
	-Dfailsafe.useSystemClassLoader=false \
	-Dsurefire.rerunFailingTestsCount=3 \
	-Dfailsafe.rerunFailingTestsCount=3 \
	-B \
	$*
