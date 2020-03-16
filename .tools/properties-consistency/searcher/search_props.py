from glob import glob
import os
import re

from pathspec.pathspec import PathSpec
from pathspec.patterns.gitwildmatch import GitWildMatchPattern
from collections import OrderedDict


class Searcher:
    def __init__(self, basedir, regex, searchFilter):
        self.basedir = basedir
        self.regex = regex
        self.searchFilter = searchFilter
    
    def find(self):
        found = dict()
        files = glob(self.basedir + '/' + self.searchFilter.filePattern, recursive=True)
        for filename in files:
            relativePath = os.path.relpath(filename, self.basedir)
            if not os.path.isdir(filename) and self.searchFilter.isIncludedFile(relativePath):
                linenumber = 0
                with open(filename) as f:
                    self.findInFile(f, relativePath, linenumber, found)
        return OrderedDict(sorted(found.items()))
    
    def findInFile(self, f, filename, linenumber, found):
        for line in f:
            if len(line) == 0:
                continue
            linenumber += 1
            for match in re.finditer(self.regex, line):
                key = match.group('key')
                if not self.searchFilter.isAcceptedProperty(key):
                    continue
                if found.get(key) is None:
                    found[key] = DocumentedProperty(key)
                found[key].addLocation(Location(filename, line, linenumber, match.group(0)))

    
class SearchFilter:
    def __init__(self, filePattern, excludedFiles, excludedProperties):
        self.filePattern = filePattern
        self.excludedFiles = excludedFiles
        self.excludedProperties = excludedProperties
        
    def isIncludedFile(self, filename):
        spec = PathSpec.from_lines(GitWildMatchPattern, self.excludedFiles)
        included = not spec.match_file(filename)
        return included
    
    def isAcceptedProperty(self, prop):
        for excludedProp in self.excludedProperties:
            if excludedProp.matches(prop):
                return False 
        return True


class ExactMatch:
    def __init__(self, exact):
        self.exact = exact
        
    def matches(self, prop):
        return self.exact == prop
   


class PropertyDeclarationMatch:
    def __init__(self, decl):
        self.decl = decl
        
    def matches(self, prop):
        return self.decl == '${'+prop+'}'
   

class DocumentedProperty:
    def __init__(self, key):
        self.key = key
        self.locations = []
        
    def addLocation(self, location):
        self.locations.append(location)
        
    
class Location:
    def __init__(self, filename, line, linenumber, match):
        self.filename = filename
        self.line = line
        self.linenumber = linenumber
        self.match = match

