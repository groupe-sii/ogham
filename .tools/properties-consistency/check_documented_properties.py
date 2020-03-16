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


import logging
import os

from colored import fore, style

from searcher import findPropertiesInDocs, findPropertiesDefinedInCode
from utils import stringify


def displayReport(propertiesInDocs, propertiesInCode, missingProperties, documentedProperties, undocumentedProperties):
    errorStyle = fore.LIGHT_RED + style.BOLD
    warningStyle = fore.YELLOW_1 + style.BOLD
    infoStyle = fore.BLUE + style.BOLD
    okStyle = fore.GREEN + style.BOLD
    keyStyle = style.BOLD
    normalStyle = style.RESET
    keyFormatter = lambda key: keyStyle + key + normalStyle
    
    print(errorStyle + '--------------------------------------------------------------' + normalStyle)
    print(errorStyle + ' Keys that are documented but not present in code' + normalStyle)
    print(errorStyle + '--------------------------------------------------------------' + normalStyle)
    print(stringify(missingProperties, keyFormatter))

    print(warningStyle + '--------------------------------------------------------------' + normalStyle)
    print(warningStyle + ' Undocumented keys (present in code but not in documentation)' + normalStyle)
    print(warningStyle + '--------------------------------------------------------------' + normalStyle)
    print(stringify(undocumentedProperties, keyFormatter))
    print('\n\n')

    print(warningStyle + '--------------------------------------------------------------' + normalStyle)
    print(warningStyle + ' Automatically skipped keys referenced in documentation' + normalStyle)
    print(warningStyle + '--------------------------------------------------------------' + normalStyle)
    print(stringify(propertiesInDocs.get('automaticallySkipped'), keyFormatter))
    print(warningStyle + '--------------------------------------------------------------' + normalStyle)
    print(warningStyle + ' Automatically skipped properties defined in code' + normalStyle)
    print(warningStyle + '--------------------------------------------------------------' + normalStyle)
    print(stringify(propertiesInCode.get('automaticallySkipped'), keyFormatter))
    print('\n\n')

    print(infoStyle + '--------------------------------------------------------------' + normalStyle)
    print(infoStyle + ' Manually skipped keys referenced in documentation' + normalStyle)
    print(infoStyle + ' (based on .ignore-doc-matches)' + normalStyle)
    print(infoStyle + '--------------------------------------------------------------' + normalStyle)
    print(stringify(propertiesInDocs.get('manuallySkipped'), keyFormatter))
    print(infoStyle + '--------------------------------------------------------------' + normalStyle)
    print(infoStyle + ' Manually skipped properties defined in code' + normalStyle)
    print(infoStyle + ' (based on .ignore-props)' + normalStyle)
    print(infoStyle + '--------------------------------------------------------------' + normalStyle)
    print(stringify(propertiesInCode.get('manuallySkipped'), keyFormatter))
    print('\n\n')

    print(okStyle + '--------------------------------------------------------------' + normalStyle)
    print(okStyle + ' Documented keys (present in code and in documentation)' + normalStyle)
    print(okStyle + '--------------------------------------------------------------' + normalStyle)
    print(stringify(documentedProperties, keyFormatter))
    print('\n\n')



def main():
    # script configuration
    logging.basicConfig(level=logging.INFO)
    scriptDir = os.path.dirname(os.path.realpath(__file__))+'/'
    searchRoot = scriptDir+'../../'
    # 1) Find all lines containing possible property (with associated file and line number)
    #    in documentation.
    #    Property may be of the form:
    #    - ogham.property-key
    #    - surrounded by some characters
    #      - "" in code samples
    #      - `` manually surrounded in explanations for example
    #    - something-else.property-key
    #
    #    Properties can be referenced in paragraphs, in admonitions, code samples,
    #    tables...
    #    
    #    As it is impossible to distinguish properties from method call, it includes
    #    more matches. Matches will then be filtered (automatically and from manual exclusions).
    propertiesInDocs = findPropertiesInDocs(scriptDir, searchRoot)
    # 2) Find all lines containing possible property (with associated file and line number)
    #    that are defined in the code.
    #    Property definition are of the form: "${ogham.property-key}"
    propertiesInCode = findPropertiesDefinedInCode(scriptDir, searchRoot)
    # 3) For each property documented, check if the property really exists in code
    missingProperties = dict()
    for prop in propertiesInDocs.get('properties'):
        if not prop in propertiesInCode.get('properties').keys():
            missingProperties[prop] = propertiesInDocs.get('properties').get(prop)
    # 4) For each property defined in code, check if the property is documented
    documentedProperties = dict()
    undocumentedProperties = dict()
    for prop in propertiesInCode.get('properties'):
        if prop in propertiesInDocs.get('properties').keys():
            documentedProperties[prop] = propertiesInDocs.get('properties').get(prop)
        else:
            undocumentedProperties[prop] = propertiesInCode.get('properties').get(prop)
    # 5) Display report (most important first):
    #    - list of invalid property references
    #    - list of undocumented properties
    #    - skipped keys (to check if a key has be excluded but should not)
    #    - skipped properties (to check if a key has be excluded but should not)
    #    - documented properties (to see where the property is documented and if it is documented enough)
    displayReport(propertiesInDocs, propertiesInCode, missingProperties, documentedProperties, undocumentedProperties)
    

if __name__ == "__main__":
    main()
    
    