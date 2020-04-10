from loader.loader import ExclusionLoader
from searcher.search_props import SearchFilter, Searcher,\
    PropertyDeclarationMatch, ExactMatch
import logging
from utils import stringify, diff
import re


def findPropertiesInDocs(scriptDir, searchRoot):
    excludedProps = ExclusionLoader(scriptDir + '.ignore-doc-matches').load()
    noExcludeFilter = SearchFilter('**/*.adoc', ['README.adoc', 'DEV.adoc'], [])
    searchFilter = SearchFilter('**/*.adoc', ['README.adoc', 'DEV.adoc'], [ExactMatch(p) for p in excludedProps])
    mayBePropertySeacher = Searcher(searchRoot, r'\b(?P<key>(ogham\.|mail\.|spring\.)[a-z0-9.\-]+)\b', noExcludeFilter)
    propertiesWithoutExclusionSearcher = Searcher(searchRoot, r'(^|[ \"\`\'|])(?P<key>(ogham\.|mail\.|spring\.)[a-z0-9.\-]+)([ =\"\`\'|]|$)', noExcludeFilter)
    propertiesSearcher = Searcher(searchRoot, r'(^|[ \"\`\'|])(?P<key>(ogham\.|mail\.|spring\.)[a-z0-9.\-]+)([ =\"\`\'|]|$)', searchFilter)
    mayBeProps = mayBePropertySeacher.find()
    allProps = propertiesWithoutExclusionSearcher.find()
    properties = propertiesSearcher.find()
    logging.debug('May be props:\n%s\n\n', stringify(mayBeProps))
    logging.debug('All props in documentation:\n%s\n\n', stringify(allProps))
    logging.debug('props:\n%s\n\n', stringify(properties))
    automaticallySkippedProperties = diff(mayBeProps, allProps)
    manuallySkippedProperties = diff(allProps, properties)
    logging.debug('Automatically skipped props:\n%s\n\n', stringify(automaticallySkippedProperties))
    logging.debug('Manually skipped props:\n%s\n\n', stringify(manuallySkippedProperties))
    return {'properties': properties, 'automaticallySkipped': automaticallySkippedProperties, 'manuallySkipped': manuallySkippedProperties}

def findPropertiesDefinedInCode(scriptDir, searchRoot):
    excludedProps = ExclusionLoader(scriptDir + '.ignore-props').load()
    excludedFiles = ExclusionLoader(scriptDir + '.ignore-files').load()
    noExcludeFilter = SearchFilter('**/*.java', excludedFiles, [])
    searchFilter = SearchFilter('**/*.java', excludedFiles, [PropertyDeclarationMatch(p) for p in excludedProps])
    mayBePropertySeacher = Searcher(searchRoot, r'"\$\{(?P<key>[^}]+)\}"', noExcludeFilter)
    propertiesWithoutExclusionSearcher = Searcher(searchRoot, r'"\$\{(?P<key>[a-zA-Z0-9.\-]+)\}"', noExcludeFilter)
    propertiesSearcher = Searcher(searchRoot, r'"\$\{(?P<key>[a-zA-Z0-9.\-]+)\}"', searchFilter)
    mayBeProps = mayBePropertySeacher.find()
    allProps = propertiesWithoutExclusionSearcher.find()
    properties = propertiesSearcher.find()
    logging.debug('May be props:\n%s\n\n', stringify(mayBeProps))
    logging.debug('All props in documentation:\n%s\n\n', stringify(allProps))
    logging.debug('props:\n%s\n\n', stringify(properties))
    automaticallySkippedProperties = diff(mayBeProps, allProps)
    manuallySkippedProperties = diff(allProps, properties)
    logging.debug('Automatically skipped props:\n%s\n\n', stringify(automaticallySkippedProperties))
    logging.debug('Manually skipped props:\n%s\n\n', stringify(manuallySkippedProperties))
    return {'properties': properties, 'automaticallySkipped': automaticallySkippedProperties, 'manuallySkipped': manuallySkippedProperties}

def findUsages(properties, scriptDir, searchRoot):
    excludedFiles = ExclusionLoader(scriptDir + '.ignore-files').load()
    searchFilter = SearchFilter('**/*.java', excludedFiles, [])
    usages = dict()
    for key in properties:
        escapedKey = re.escape(key)
        usageSearcher = Searcher(searchRoot, r'"(?P<key>'+escapedKey+')"', searchFilter)
        usages[key] = usageSearcher.find().get(key)
    return usages
