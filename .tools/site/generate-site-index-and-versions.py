import os
import json
from string import Template
from functools import total_ordering
import argparse
from os import path

def generate(sitedir, siteBaseUrl, codeBaseUrl, logoPath):
	versions = loadVersions(sitedir)
	print(versions.asList())
	generateVersions(sitedir, versions)
	generateIndex(sitedir, siteBaseUrl, versions.getLastRelease())
	with open(logoPath, 'r') as file:
		logo = file.read()
	generateLatestReleaseResources(sitedir, siteBaseUrl, codeBaseUrl, versions.getLastRelease(), logo)
	generateNightlyBuildResources(sitedir, siteBaseUrl, codeBaseUrl, versions.getNightlyBuild(), logo)
	generateRedirect(sitedir+'/donate/thanks.html', siteBaseUrl, versions.getLastRelease().name+'/donate-thanks.html')


def loadVersions(sitedir):
	versionNames = []
	for entry in os.scandir(sitedir):
		if entry.is_dir() and entry.name.startswith('v'):
			versionNames.append(entry.name)
	return Versions(versionNames)
	

def generateVersions(sitedir, versions):
	with open(sitedir+'/versions.json', 'w') as file:
		json.dump(versions.asList(), file)

def generateIndex(sitedir, baseUrl, currentVersion):
	generateRedirect(sitedir+'/index.html', baseUrl, currentVersion.name)
	
def generateLatestReleaseResources(sitedir, baseUrl, codeBaseUrl, version, logo):
	generateBadge(sitedir, 'latest-release-version', 'latest release', version.name, logo=logo)
	generateRedirect(sitedir+'/redirects/latest-release-site.html', baseUrl, version.name)
	generateRedirect(sitedir+'/redirects/latest-release-showcase.html', baseUrl, version.name+'/presentation/showcase.html')
	generateRedirect(sitedir+'/redirects/latest-release-code.html', codeBaseUrl, version.name)
	
def generateNightlyBuildResources(sitedir, baseUrl, codeBaseUrl, version, logo):
	generateBadge(sitedir, 'nightly-build-version', 'nightly build', version.name, logo=logo)
	generateRedirect(sitedir+'/redirects/nightly-build-site.html', baseUrl, version.name)
	generateRedirect(sitedir+'/redirects/nightly-build-showcase.html', baseUrl, version.name+'/presentation/showcase.html')
	generateRedirect(sitedir+'/redirects/nightly-build-code.html', codeBaseUrl, version.name)
	
def generateBadge(sitedir, badgeType, label, message, color='lightgrey', logo=None):
	jsonContent  = {"schemaVersion": 1, "label": label, "message": message, "color": color}
	if logo is not None:
		if logo.startswith('<?xml') or logo.startswith('<svg'):
			jsonContent['logoSvg'] = logo
		else:
			jsonContent['namedLogo'] = logo
	os.makedirs(sitedir+'/badges', exist_ok=True)
	with open(sitedir+'/badges/'+badgeType+'.json', 'w') as file:
		json.dump(jsonContent, file)

	
def generateRedirect(htmlFile, baseUrl, target):
	tpl = Template('<html><head><meta http-equiv="refresh" content="0; URL=$baseUrl/$target"></head><body></body></html>')
	os.makedirs(path.dirname(htmlFile), exist_ok=True)
	with open(htmlFile, 'w') as file:
		file.write(tpl.substitute(baseUrl=baseUrl, target=target))


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

	def getNightlyBuild(self):
		for version in self.versions:
			if version.snapshot:
				return version
		return None
	
	def asList(self):
		return list(map(lambda v: v.name, self.versions))

def cmp(a, b):
    return (a > b) - (a < b) 

def parseArgs():
	parser = argparse.ArgumentParser(description='Generate versions.json and index.html files for site.')
	parser.add_argument('sitedir', help='The directory that contains the site index')
	parser.add_argument('--base-url', default='https://groupe-sii.github.io/ogham/', help='The URL of the generated site')
	parser.add_argument('--code-base-url', default='https://github.com/groupe-sii/ogham/tree/', help='The URL of the github sources')
	parser.add_argument('--logo-svg-path', default='src/docs/resources/images/logo.svg', help='The path to the logo (relative to ogham root directory)')
	return parser.parse_args()
	

if __name__ == '__main__':
	args = parseArgs()
	generate(args.sitedir, args.base_url, args.code_base_url, os.path.dirname(__file__)+'/../../'+args.logo_svg_path)

