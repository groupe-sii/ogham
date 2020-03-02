#!/bin/bash


# Script that generates the `application-consistency-check.properties` file that will
# contain all properties defined by Ogham (such as "${propery.key}").
# Then we can use Spring to automatically check if properties are well-formed or not.
# This is also useful to detect a property defined by Ogham but not known by
# Spring Boot configuration processor (so completion won't be available).
#
# It searches across all files using regular expression and for each
# defined property (except excluded ones), it generates a comment to indicate:
# - where the property is defined
# - where the property is used


set -e

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
OGHAM_DIR="$SCRIPT_DIR/../.."

PROPERTY_FILE="ogham-spring-boot-autoconfigure/src/test/resources/config/application-consistency-check.properties"

DEBUG_FILE="${OGHAM_DIR}/target/properties-debug.log"
DEBUG_SEPARATOR="\n--------------------------------------------\n"
echo "" > "${DEBUG_FILE}"

SKIPPED_PROPERTIES_FILE="${OGHAM_DIR}/target/skipped-properties.diff"
echo "" > "${SKIPPED_PROPERTIES_FILE}"

EXCLUDED_PROPS="$(sed -e 's/^[[:space:]]*#.*// ; /^[[:space:]]*$/d' "${SCRIPT_DIR}/.ignore-props")" 
DEFAULT_VALUES="$(sed -e 's/^[[:space:]]*#.*// ; /^[[:space:]]*$/d' "${SCRIPT_DIR}/.default-values")" 


# 1) find all lines surrounded by "${}" (with associated file and line number)
echo "Finding all lines that contains defined property..."
FOUND="$(grep -rnE "$OGHAM_DIR" \
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
																						echo -e "${DEBUG_SEPARATOR}FOUND=\n$FOUND\n\n" >> "${DEBUG_FILE}"

# 2) generate list of properties
#    and filter some properties (defined in comments for example)
#    and remove surrounding characters
echo "Generating list of property keys..."
ALL_POSSIBLE_PROPERTIES="$(echo "$FOUND" | grep -ohE -e '"\$\{[^}]+\}"' \
	| sort | uniq)"
ALL_PROPERTIES="$(echo "$FOUND" | grep -ohE -e '"\$\{[a-zA-Z0-9.\-]+\}"' \
	| sort | uniq)"
PROPERTIES="$(echo "$ALL_PROPERTIES" \
	| grep -vF "${EXCLUDED_PROPS}")"

																						echo -e "${DEBUG_SEPARATOR}ALL_POSSIBLE_PROPERTIES=\n$ALL_POSSIBLE_PROPERTIES\n\n" >> "${DEBUG_FILE}"
																						echo -e "${DEBUG_SEPARATOR}ALL_PROPERTIES=\n$ALL_PROPERTIES\n\n" >> "${DEBUG_FILE}"
																						echo -e "${DEBUG_SEPARATOR}PROPERTIES=\n$PROPERTIES\n\n" >> "${DEBUG_FILE}"

KEYS="$(echo "$PROPERTIES" | sed 's/"${//' | sed 's/}"//')"
																						echo -e "${DEBUG_SEPARATOR}FINAL KEYS=\n$KEYS\n\n" >> "${DEBUG_FILE}"

# 3) display skipped keys
echo -e "Automatically skipped properties:\n" >> "${SKIPPED_PROPERTIES_FILE}"
diff <(echo "$ALL_POSSIBLE_PROPERTIES") <(echo "$ALL_PROPERTIES") >> "${SKIPPED_PROPERTIES_FILE}" 2>&1 || true
echo -e "\n\nManually skipped properties:\n" >> "${SKIPPED_PROPERTIES_FILE}"
diff <(echo "$ALL_PROPERTIES") <(echo "$PROPERTIES") >> "${SKIPPED_PROPERTIES_FILE}" 2>&1 || true


# 4) group by key
echo "Generating comments with property definition locations and usage locations..."
GROUPED=""
for KEY in $KEYS; do
	# find all lines that match the key
	# remove useless part of the path (make it relative to root)
	# prefix by comment character
	MATCHES="$(echo "$FOUND" | grep -F "\${$KEY}" \
		| sed -e 's#^.*\.tools/properties-consistency/\.\./\.\./##g' \
		| sed -e 's/^/# /g')"
																						echo -e "key     = '$KEY'\nmatches = '$MATCHES'" >> "${DEBUG_FILE}"
	# include files where the key is used
	USAGES="$(grep -rnF "$OGHAM_DIR" \
			--exclude-dir="target" \
			--exclude-dir=".externalToolBuilders" \
			--exclude-dir=".tools" \
			--exclude="site.xml" \
			--exclude="mvnw" \
			--exclude="*.sh" \
			--exclude="README.adoc" \
			-e "\"$KEY\"" \
		| grep --invert "${PROPERTY_FILE}" \
		| sed -e 's#^.*\.tools/properties-consistency/\.\./\.\./##g' \
		| sed -e 's/^/# /g')"
																						echo -e "usages  = '$USAGES'" >> "${DEBUG_FILE}"
	GROUPED="$(echo -e "${GROUPED}\n\n#-------------------\n# DEFINITIONS\n#-------------------\n#\n${MATCHES}\n#\n#-------------------\n# USAGES\n#-------------------\n#\n${USAGES}\n${KEY}")"
done
																						echo -e "${DEBUG_SEPARATOR}GROUPED=\n$GROUPED\n\n" >> "${DEBUG_FILE}"

# 5) add default value
echo "Adding a default value for each key..."
GROUPED_WITH_VALUE="$GROUPED"
for KEY in $KEYS; do
	ESCAPED_KEY_PATTERN="$(echo "$KEY" | sed 's/\./\\./g ; s/\-/\\-/g')"
	DEFAULT_VALUE="$(echo "$DEFAULT_VALUES" | grep -Ee "^${ESCAPED_KEY_PATTERN}=" | sed -re 's/^.+=(.+)$/\1/')"
	DEFAULT_VALUE="${DEFAULT_VALUE:-0}"
																						echo -e "key           = '$KEY'\nescaped key   = '$ESCAPED_KEY_PATTERN'\ndefault value = '$DEFAULT_VALUE'\nsed           = sed -re \"s#^${ESCAPED_KEY_PATTERN}\$#${KEY}=${DEFAULT_VALUE}#\"\n" >> "${DEBUG_FILE}"
	
	GROUPED_WITH_VALUE="$(echo "$GROUPED_WITH_VALUE" | sed -re "s#^${ESCAPED_KEY_PATTERN}\$#${KEY}=${DEFAULT_VALUE}#")"
done

# 6) write to file
echo "Generating final file..."
HEADER=$(cat <<-END
#=========================================================================================
#                                         WARNING
#=========================================================================================
# This file is auto-generated using '.tools/properties-consistency/list-all-properties.sh' 
# script.
#
# /!\ DO NOT EDIT THIS FILE DIRECTLY /!\ 
#=========================================================================================
END
)

SKIPPED_PROPERTIES="$(cat "${SKIPPED_PROPERTIES_FILE}" \
	| sed -E -e '/^[[:space:]]*[0-9,]+d[0-9,]+[[:space:]]*$/d' \
	| sed -e 's/< //g' \
	| sed -e 's/^/# /g')"
echo -e "${HEADER}\n\n${SKIPPED_PROPERTIES}\n\n${GROUPED_WITH_VALUE}" > "${OGHAM_DIR}/${PROPERTY_FILE}"

# # 7) print result
echo "Generated '${PROPERTY_FILE}':"
cat "${OGHAM_DIR}/${PROPERTY_FILE}"
