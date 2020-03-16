from collections import OrderedDict

def stringify(properties, keyFormatter = lambda key: key):
    locationWidth = computeLocationWidth(properties)
    s = ''
    for key in OrderedDict(sorted(properties.items())):
        s += keyFormatter(key) + '\n'
        for location in properties[key].locations:
            s += '              ' + (location.filename + ':' + str(location.linenumber)).ljust(locationWidth) + '          ' + location.line.strip() + '\n'
    return s
    
def diff(a, b):
    props = dict()
    for key in a:
        if key not in b.keys():
            props[key] = a[key]
    return OrderedDict(sorted(props.items()))

def computeLocationWidth(properties):
    locationWidth = 0
    for prop in properties:
        for location in properties.get(prop).locations:
            length = len(location.filename + ':' + str(location.linenumber))
            if length > locationWidth:
                locationWidth = length
    return locationWidth
