#!/bin/bash


# Script that checks if documentation is consistent with properties
# defined by Ogham.
#
# It searches for property references across all documentation files.
# For each referenced property, it:
# - checks if the property exists in code (report an error if not)
# 
# It then searches for property definitions (except excluded ones) across 
# all files.
# For each defined property, it:
# - checks if the property is documented (report a warning if not)
# - indicates every locations that the property is documented


set -e

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
OGHAM_DIR="$SCRIPT_DIR/../.."

PROPERTY_FILE="ogham-spring-boot-autoconfigure/src/test/resources/config/application-consistency-check.properties"

DEBUG_FILE="${OGHAM_DIR}/target/check-documented-properties-debug.log"
DEBUG_SEPARATOR="\n--------------------------------------------\n"
echo "" > "${DEBUG_FILE}"



PROPERTY_PATTERN='\b(ogham\.|mail\.|spring\.)[a-z0-9.\-]+\b'
EXCLUSIONS="$(sed -e 's/^[[:space:]]*#.*// ; /^[[:space:]]*$/d' "${SCRIPT_DIR}/.ignore-doc-matches")" 
EXCLUDED_PROPS="$(sed -e 's/^[[:space:]]*#.*// ; /^[[:space:]]*$/d' "${SCRIPT_DIR}/.ignore-props")" 


ERROR='\033[0;91m\e[1m'
WARNING='\033[38;5;220m\e[1m'
OK='\033[0;32m'
INFO='\033[0;34m'
NORMAL='\033[0m'
UNEXISTING='\e[1m'


# 1) Find all lines containing possible property (with associated file and line number)
#    Property may be of the form:
#    - ogham.property-key
#    - surrounded by some characters
#      - "" in code samples
#      - `` manually surrounded in explanations for example
#    - somethig-else.property-key
#
#    Properties can be referenced in paragraphs, in admonitions, code samples,
#    tables...
#    
#    As it is impossible to distinguish properties from method call, it includes
#    more matches. Matches will then be filtered (automatially and from manual exclusions).
FOUND="$(grep -rnE "$OGHAM_DIR" \
		--include="*.adoc" \
		--exclude="README.adoc" \
		--exclude="DEV.adoc" \
		-e "${PROPERTY_PATTERN}" \
	| cat)"
																						echo -e "${DEBUG_SEPARATOR}FOUND=\n$FOUND\n\n" >> "${DEBUG_FILE}"

# generate list of properties and filter some properties
ALL_POSSIBLE_PROPERTIES="$(echo "$FOUND" | grep -ohE -e "${PROPERTY_PATTERN}" \
	| sort | uniq)"
ALL_PROPERTIES="$(echo "$FOUND" | grep -ohE -e "(^|[ \"\`'|])${PROPERTY_PATTERN}" \
	| sed -e "s/[ \"\`'|]//g" \
	| sort | uniq)"
PROPERTIES="$(echo "$ALL_PROPERTIES" \
	| grep -vE -e "^${EXCLUSIONS}$")"

																						echo -e "${DEBUG_SEPARATOR}ALL_POSSIBLE_PROPERTIES=\n$ALL_POSSIBLE_PROPERTIES\n\n" >> "${DEBUG_FILE}"
																						echo -e "${DEBUG_SEPARATOR}ALL_PROPERTIES=\n$ALL_PROPERTIES\n\n" >> "${DEBUG_FILE}"
																						echo -e "${DEBUG_SEPARATOR}PROPERTIES=\n$PROPERTIES\n\n" >> "${DEBUG_FILE}"

KEYS="$(echo "$PROPERTIES")"
																						echo -e "${DEBUG_SEPARATOR}FINAL KEYS=\n$KEYS\n\n" >> "${DEBUG_FILE}"



# check if keys are defined in code
NOT_IN_CODE=""
for KEY in $KEYS; do
	# use `tail -n +1 |` to avoid broken pipe due to output of echo command that may be greater than 8kb
	CODE_MATCHES="$(echo "$FOUND" | tail -n +1 | grep -rnF "$OGHAM_DIR" \
			--exclude-dir="target" \
			--exclude-dir=".externalToolBuilders" \
			--exclude-dir=".tools" \
			--exclude="site.xml" \
			--exclude="mvnw" \
			--exclude="*.sh" \
			--exclude="*.adoc" \
			-e "\${$KEY}" \
		| grep --invert "${PROPERTY_FILE}" \
		| sed -e 's#^.*\.tools/properties-consistency/\.\./\.\./##g')"
																						echo -e "key     = '$KEY'\nmatches = '$CODE_MATCHES'" >> "${DEBUG_FILE}"
	if [ -z "$CODE_MATCHES" ]; then
		ESCAPED_KEY_PATTERN="$(echo "$KEY" | sed 's/\./\\./g ; s/\-/\\-/g')"
		# find all lines that match the key in documentation
		LOCATIONS="$(grep -rnE "$OGHAM_DIR" \
				--include="*.adoc" \
				--exclude="README.adoc" \
				--exclude="DEV.adoc" \
				-e "(^|[ \"'\`\\|])${ESCAPED_KEY_PATTERN}([ \"\`'|]|$)" \
			| sed -e 's#^.*\.tools/properties-consistency/\.\./\.\./##g' \
			| sed -E -e 's#(\.adoc\:[0-9]+\:)#\1        #g' \
			| sed -e 's/^/    /g')"
		NOT_IN_CODE="${NOT_IN_CODE}\n${UNEXISTING}$KEY${NORMAL}\n${LOCATIONS}"
	fi
done


# 2) Find all lines containing possible property (with associated file and line number)
#    that are defined in the code.
#    Property definition are of the form: "${ogham.property-key}"
FOUND_IN_CODE="$(grep -rnE "$OGHAM_DIR" \
		--exclude-dir="target" \
		--exclude-dir=".externalToolBuilders" \
		--exclude-dir=".tools" \
		--exclude="site.xml" \
		--exclude="mvnw" \
		--exclude="*.sh" \
		--exclude="README.adoc" \
		-e '"\$\{[^}]+\}"' \
	| grep --invert "${PROPERTY_FILE}" \
	| cat)"
																						echo -e "${DEBUG_SEPARATOR}FOUND_IN_CODE=\n$FOUND_IN_CODE\n\n" >> "${DEBUG_FILE}"

# generate list of properties
# and filter some properties (defined in comments for example)
# and remove surrounding characters
ALL_POSSIBLE_PROPERTIES_DEFINED_IN_CODE="$(echo "$FOUND_IN_CODE" | grep -ohE -e '"\$\{[^}]+\}"' \
	| sort | uniq)"
ALL_PROPERTIES_DEFINED_IN_CODE="$(echo "$FOUND_IN_CODE" | grep -ohE -e '"\$\{[a-zA-Z0-9.\-]+\}"' \
	| sort | uniq)"
PROPERTIES_DEFINED_IN_CODE="$(echo "$ALL_PROPERTIES_DEFINED_IN_CODE" \
	| grep -vF "${EXCLUDED_PROPS}")"

																						echo -e "${DEBUG_SEPARATOR}ALL_POSSIBLE_PROPERTIES_DEFINED_IN_CODE=\n$ALL_POSSIBLE_PROPERTIES_DEFINED_IN_CODE\n\n" >> "${DEBUG_FILE}"
																						echo -e "${DEBUG_SEPARATOR}ALL_PROPERTIES_DEFINED_IN_CODE=\n$ALL_PROPERTIES_DEFINED_IN_CODE\n\n" >> "${DEBUG_FILE}"
																						echo -e "${DEBUG_SEPARATOR}PROPERTIES_DEFINED_IN_CODE=\n$PROPERTIES_DEFINED_IN_CODE\n\n" >> "${DEBUG_FILE}"

KEYS_DEFINED_IN_CODE="$(echo "$PROPERTIES_DEFINED_IN_CODE" | sed 's/"${//' | sed 's/}"//')"
																						echo -e "${DEBUG_SEPARATOR}FINAL KEYS=\n$KEYS\n\n" >> "${DEBUG_FILE}"


# For each key defined in code, check if documented or not 
DOCUMENTED_WITH_MATCHES=""
UNDOCUMENTED=""
for KEY in $KEYS_DEFINED_IN_CODE; do
	ESCAPED_KEY_PATTERN="$(echo "$KEY" | sed 's/\./\\./g ; s/\-/\\-/g')"
	# find all lines that match the key in documentation
	MATCHES="$(grep -rnE "$OGHAM_DIR" \
			--include="*.adoc" \
			--exclude="README.adoc" \
			--exclude="DEV.adoc" \
			-e "(^|[ \"'\`\\|])${ESCAPED_KEY_PATTERN}([ \"\`'|]|$)" \
		| sed -e 's#^.*\.tools/properties-consistency/\.\./\.\./##g' \
		| sed -E -e 's#(\.adoc\:[0-9]+\:)#\1        #g' \
		| sed -e 's/^/    /g')"
																						echo -e "key     = '$KEY'\nmatches = '$MATCHES'" >> "${DEBUG_FILE}"
	if [ -z "$MATCHES" ]; then
		UNDOCUMENTED="${UNDOCUMENTED}\n${KEY}"
	else
		DOCUMENTED_WITH_MATCHES="${DOCUMENTED_WITH_MATCHES}\n${KEY}\n${MATCHES}"
	fi
done

# 3) Display report (more important first):
#    - list of invalid property references
#    - list of undocumented properties
#    - skipped keys (to check if a key has be excluded but should not)
#    - skipped properties (to check if a key has be excluded but should not)
#    - documented properties (to see where the property is documented and if it is documented enough)

echo -e "${ERROR}--------------------------------------------------------------${NORMAL}"
echo -e "${ERROR} Keys that are documented but not present in code${NORMAL}"
echo -e "${ERROR}--------------------------------------------------------------${NORMAL}"
echo -e "${NOT_IN_CODE}\n\n"


echo -e "${WARNING}--------------------------------------------------------------${NORMAL}"
echo -e "${WARNING} Undocumented keys (present in code but not in documentation)${NORMAL}"
echo -e "${WARNING}--------------------------------------------------------------${NORMAL}"
echo -e "${UNDOCUMENTED}\n\n"


echo -e "${WARNING}--------------------------------------------------------------${NORMAL}"
echo -e "${WARNING} Automatically skipped keys referenced in documentation${NORMAL}"
echo -e "${WARNING}--------------------------------------------------------------${NORMAL}"
diff <(echo "$ALL_POSSIBLE_PROPERTIES") <(echo "$ALL_PROPERTIES") \
	| sed -E -e '/^[[:space:]]*[0-9,]+d[0-9,]+[[:space:]]*$/d' \
	| sed -e 's/< //g'
echo -e "${WARNING}--------------------------------------------------------------${NORMAL}"
echo -e "${WARNING} Automatically skipped properties defined in code${NORMAL}"
echo -e "${WARNING}--------------------------------------------------------------${NORMAL}"
diff <(echo "$ALL_POSSIBLE_PROPERTIES_DEFINED_IN_CODE") <(echo "$ALL_PROPERTIES_DEFINED_IN_CODE") \
	| sed -E -e '/^[[:space:]]*[0-9,]+d[0-9,]+[[:space:]]*$/d' \
	| sed -e 's/< //g'
echo -e "\n\n"


echo -e "${INFO}--------------------------------------------------------------${NORMAL}"
echo -e "${INFO} Manually skipped keys referenced in documentation${NORMAL}"
echo -e "${INFO} (based on .ignore-doc-matches)${NORMAL}"
echo -e "${INFO}--------------------------------------------------------------${NORMAL}"
diff <(echo "$ALL_PROPERTIES") <(echo "$PROPERTIES") \
	| sed -E -e '/^[[:space:]]*[0-9,]+d[0-9,]+[[:space:]]*$/d' \
	| sed -e 's/< //g'
echo -e "${INFO}--------------------------------------------------------------${NORMAL}"
echo -e "${INFO} Manually skipped properties defined in code${NORMAL}"
echo -e "${INFO} (based on .ignore-props)${NORMAL}"
echo -e "${INFO}--------------------------------------------------------------${NORMAL}"
diff <(echo "$ALL_PROPERTIES_DEFINED_IN_CODE") <(echo "$PROPERTIES_DEFINED_IN_CODE") \
	| sed -E -e '/^[[:space:]]*[0-9,]+d[0-9,]+[[:space:]]*$/d' \
	| sed -e 's/< //g'
echo -e "\n\n"


echo -e "${OK}--------------------------------------------------------------${NORMAL}"
echo -e "${OK} Documented keys (present in code and in documentation)${NORMAL}"
echo -e "${OK}--------------------------------------------------------------${NORMAL}"
echo -e "${DOCUMENTED_WITH_MATCHES}\n\n"

