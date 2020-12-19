#!/bin/sh

set -e${DEBUG_COMMANDS:+x}

profile="$1"
INITIALIZER_ARGS=$([ "$profile" = "" ] && echo ''  || echo '-Polder-versions -Dspring-boot.version=1.5.21.RELEASE -Dspring-initializr.version=0.4.0.RELEASE -Drun.jvmArguments="-Dspring.profiles.active=older-versions"')
INITIALIZER_NAME=$([ "$profile" = "" ] && echo 'spring-initializr'  || echo 'spring-initializr-older-versions')
GENERATION_ARGS=$([ "$profile" = "" ] && echo ''  || echo '-Dspring.profiles.active=older-versions')
CLASSPATH_TESTS_ROOT_DIR="${CLASSPATH_TESTS_ROOT_DIR:-$HOME/classpath-tests}"

# start Spring Initializr
touch "$HOME/$INITIALIZER_NAME.log"
(spring-initializr/./mvnw clean spring-boot:run -f spring-initializr $INITIALIZER_ARGS > "$HOME/$INITIALIZER_NAME.log") &
echo "$!" > "$HOME/$INITIALIZER_NAME.pid"
# wait until started or failed
(tail -f "$HOME/$INITIALIZER_NAME.log" &) | grep -q -e 'Started SpringInitializrApplication' -e 'BUILD FAILURE'
# if failed => log and stop
if grep 'BUILD FAILURE' "$HOME/$INITIALIZER_NAME.log"
then
	echo "Failed to start spring-initializr:"
	cat "$HOME/$INITIALIZER_NAME.log"
	sleep 2s
	exit 1
fi


# Generate projects
OGHAM_VERSION=$(./mvnw -q -Dexec.executable="echo" -Dexec.args='${project.version}' --non-recursive org.codehaus.mojo:exec-maven-plugin:1.3.1:exec)
./mvnw spring-boot:run \
	-pl :ogham-test-classpath \
	-Dspring-boot.run.arguments="$CLASSPATH_TESTS_ROOT_DIR" \
	-Dspring-boot.run.jvmArguments="-Drunner.parallel=false -Dogham-version=$OGHAM_VERSION -Dspring.initializer.url=http://localhost:$INITIALIZER_PORT/starter.zip $GENERATION_ARGS"

ls -l "$CLASSPATH_TESTS_ROOT_DIR/$TEST_FOLDER"

kill `cat "$HOME/$INITIALIZER_NAME.pid"` || echo ""
