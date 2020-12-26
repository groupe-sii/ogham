import os
import json
from string import Template
from functools import total_ordering


def loadVersions(sitedir):
	versionNames = []
	for entry in os.listdir(sitedir):
		if os.path.isdir(entry) and entry.startswith('v'):
			versionNames.append(entry)
	return Versions(versionNames)
	

def generateVersions(sitedir, versions):
	with open(sitedir+'/versions.json', 'w') as file:
		json.dump(versions.asList(), file)


def generateIndex(sitedir, baseUrl, currentVersion):
	tpl = Template('<html><head><meta http-equiv="refresh" content="0; URL=$baseUrl/$currentVersion"></head><body></body></html>')
	with open(sitedir+'/index.html', 'w') as file:
		file.write(tpl.substitute(baseUrl=baseUrl, currentVersion=currentVersion.name))
	

@total_ordering
class Version:
	def __init__(self, version):
		self.name = version
		parts = version.split('.')
		self.major = parts[0].replace('v', '')
		self.minor = parts[1]
		self.patch = parts[2].replace('-SNAPSHOT', '')
		self.snapshot = parts[2].find('-SNAPSHOT')>=0
	
	def __cmp__(self, other):
		if self.major != other.major:
			return cmp(self.major, other.major) 
		if self.minor != other.minor:
			return cmp(self.minor, other.minor)
		if self.patch != other.patch:
			return cmp(self.patch, other.patch)
		if self.snapshot != other.snapshot:
			return -1 if self.snapshot else 1
		return 0
	
	def __lt__(self, other):
		return self.__cmp__(other) < 0

	def __gt__(self, other):
		return self.__cmp__(other) > 0
		

class Versions:
	def __init__(self, versionNames):
		self.versions = []
		for versionName in versionNames:
			self.versions.append(Version(versionName))
		self.versions.sort(reverse=True)
		
	def getLast(self):
		return self.versions[0]

	def getLastRelease(self):
		for version in self.versions:
			if not version.snapshot:
				return version
		return None
	
	def asList(self):
		return list(map(lambda v: v.name, self.versions))

def cmp(a, b):
    return (a > b) - (a < b) 


sitedir = os.path.dirname(os.path.realpath(__file__))

versions = loadVersions(sitedir)
print(versions.asList())
generateVersions(sitedir, versions)
generateIndex(sitedir, 'http://groupe-sii.github.io/ogham/', versions.getLastRelease())
