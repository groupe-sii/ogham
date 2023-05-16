#!/bin/sh

set -e${DEBUG_COMMANDS:+x}

module="$1"
engine="${2:-descartes}"
mutators=$([ -z "$3" ] && echo ''  || echo "-Dmutators=$3")
features=$([ "$engine" = "gregor" ] && echo '-Dfeatures=+EXPORT'  || echo '')
history_dir="${4:-/tmp}"

# need to build fat JAR before
./mvnw install -pl ogham-test-utils-dependencies --also-make -B

./mvnw install post-site \
	-rf :ogham-test-utils \
	-DtargetModules="$module" \
	-Dpit.mutation-engine="$engine" \
	$features \
	$mutators \
	-Dpit.history.dir="$history_dir" \
	-Dpit.report.skip=true \
	-Pmutation-testing-only \
	-Dmaven.test.redirectTestOutputToFile=true \
	-Dsurefire.useSystemClassLoader=false \
	-Dfailsafe.useSystemClassLoader=false \
	-B
