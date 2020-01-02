#!/bin/sh

set -ex

# ./mvnw --also-make dependency:tree -B | grep maven-dependency-plugin | awk '{ print $(NF-1) }' > "$HOME/all.modules"
# cat pom.xml | tr '\n' '~' | grep -oP '<skippedModules>.*?</skippedModules>' | tr '~' '\n' | grep -oP '(?<=<param>).+(?=</param>)' > "$HOME/skipped.modules"
# modules=$(grep -Fxv -f "$HOME/skipped.modules" "$HOME/all.modules" | grep -Fxv ogham-parent)
# echo "Target modules for PIT: $modules"
# 
# # build pitest fix report aggregation fix
# git clone --branch fix/report-aggregation https://github.com/aurelien-baudet/pitest.git $HOME/pitest
# mvn clean install -DskipTests -f $HOME/pitest -q -B > $HOME/pitest-build.log
# 
# ./mvnw clean
# 
# # generate pit-reports for each module
# mkdir "$HOME/logs"
# echo "$modules" |
# while IFS= read -r module; do
# 	./mvnw install post-site -DtargetModules="$module" -Dpit.report.skip=true -Pmutation-testing-only -Dmaven.test.redirectTestOutputToFile=true -Dsurefire.useSystemClassLoader=false -Dfailsafe.useSystemClassLoader=false -B > "$HOME/logs/$module-mutation.log" 2>&1
# done

./mvnw install post-site -Pmutation-testing-aggregate-report-only -Dmaven.test.redirectTestOutputToFile=true -Dsurefire.useSystemClassLoader=false -Dfailsafe.useSystemClassLoader=false -B

# FIXME: mutation testing produces surefire and failsafe reports => must build, test and generate site again...
./mvnw install post-site -Dmaven.test.redirectTestOutputToFile=true -Dsurefire.useSystemClassLoader=false -Dfailsafe.useSystemClassLoader=false -B

