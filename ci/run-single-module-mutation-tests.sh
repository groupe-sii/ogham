#!/bin/sh

set -ex

module="$1"

./mvnw install post-site -DtargetModules="$module" -Dpit.report.skip=true -Pmutation-testing-only -Dmaven.test.redirectTestOutputToFile=true -Dsurefire.useSystemClassLoader=false -Dfailsafe.useSystemClassLoader=false -B
