#!/bin/sh

set -ex

profile="$1"
INITIALIZER_ARGS=$([ "$profile" = "" ] && echo ''  || echo '-Polder-versions -Dspring-boot.version=1.5.21.RELEASE -Dspring-initializr.version=0.4.0.RELEASE -Drun.jvmArguments="-Dspring.profiles.active=older-versions"')
INITIALIZER_NAME=$([ "$profile" = "" ] && echo 'spring-initializr'  || echo 'spring-initializr-older-versions')
GENERATION_ARGS=$([ "$profile" = "" ] && echo ''  || echo '-Dspring.profiles.active=older-versions')

# start Spring Initializr
touch "$HOME/$INITIALIZER_NAME.log"
spring-initializr/./mvnw clean spring-boot:run -f spring-initializr > "$HOME/$INITIALIZER_NAME.log" &
echo "$!" > "$HOME/$INITIALIZER_NAME.pid"
# wait until started
(tail -f "$HOME/$INITIALIZER_NAME.log" &) | grep -q 'Started SpringInitializrApplication'

# Generate projects
OGHAM_VERSION=$(./mvnw -q -Dexec.executable="echo" -Dexec.args='${project.version}' --non-recursive org.codehaus.mojo:exec-maven-plugin:1.3.1:exec)
./mvnw spring-boot:run -pl :ogham-test-classpath -Dspring-boot.run.arguments="$HOME/classpath-tests" -Dspring-boot.run.jvmArguments="-Drunner.parallel=false -Dogham-version=$OGHAM_VERSION -Dspring.initializer.url=http://localhost:$INITIALIZER_PORT/starter.zip $GENERATION_ARGS"

ls -l "$HOME"
ls -l "$HOME/classpath-tests"
ls -l "$HOME/classpath-tests/$TEST_FOLDER"

kill `cat "$HOME/$INITIALIZER_NAME.pid"` || echo ""

