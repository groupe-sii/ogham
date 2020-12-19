#!/bin/sh

set -e${DEBUG_COMMANDS:+x}

DIR="${CLASSPATH_TESTS_ROOT_DIR:-$HOME/classpath-tests}/$TEST_FOLDER"

group_by_test() {
	PROJECT_NAME=${1#"$DIR"}
	PROJECT_NAME=${PROJECT_NAME%%/target/*}
	TEST_NAME="$(xmllint --xpath "concat(string(/testsuite/testcase/@classname), '.', string(/testsuite/testcase/@name))" $1)"
	echo "ODILE"
	IS_UNIT_TEST="${1##*surefire-reports*}"
	IS_INTEGRATION_TEST="${1##*failsafe-reports*}"
	TEST_TYPE="$( ( [ ! -z "$IS_UNIT_TEST" ] && echo "UT" ) || ( [ ! -z "$IS_INTEGRATION_TEST" ] && echo "IT" ) || echo "??" )"
	HAS_FAILURE="$(xmllint --xpath "boolean(/testsuite/testcase/failure)" $1)"
	HAS_ERROR="$(xmllint --xpath "boolean(/testsuite/testcase/error)" $1)"
	HAS_SKIPPED="$(xmllint --xpath "boolean(/testsuite/testcase/skipped)" $1)"
	SKIPPED_MESSAGE="$(xmllint --xpath "string(/testsuite/testcase/skipped/text())" $1)"
	ERROR_MESSAGE="$(xmllint --xpath "string(/testsuite/testcase/error/text())" $1)"
	FAILURE_MESSAGE="$(xmllint --xpath "string(/testsuite/testcase/failure/text())" $1)"
# 	echo -e "FILE=$1\nPROJECT_NAME=$PROJECT_NAME\nHAS_FAILURE=$HAS_FAILURE\nHAS_SKIPPED=$HAS_SKIPPED\nHAS_ERROR=$HAS_ERROR\n"
	STATUS="$( ( [ "$HAS_SKIPPED" = "true" ] && echo "SKIPPED" ) || ( [ "$HAS_ERROR" = "true" ] && echo "FAILED" ) || ( [ "$HAS_FAILURE" = "true" ] && echo "FAILED" ) || echo "SUCCESS" )"
cat <<EOF
::group::$PROJECT_NAME | $TEST_TYPE | $TEST_NAME [$STATUS]
$SKIPPED_MESSAGE
$ERROR_MESSAGE
$FAILURE_MESSAGE
::endgroup::
EOF
}

TEST_RESULTS="$(find $DIR -name "TEST-*.xml" -type f)"

for file in $TEST_RESULTS; do
    group_by_test $file
done
