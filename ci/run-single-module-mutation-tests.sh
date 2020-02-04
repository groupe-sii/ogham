#!/bin/sh

set -ex

module="$1"
engine="${2:-descartes}"
features=$([ "$engine" = "gregor" ] && echo '-Dfeatures=+EXPORT'  || echo '')


./mvnw install post-site -DtargetModules="$module" -Dpit.mutation-engine="$engine" "$features" -Dpit.report.skip=true -Pmutation-testing-only -Dmaven.test.redirectTestOutputToFile=true -Dsurefire.useSystemClassLoader=false -Dfailsafe.useSystemClassLoader=false -B
