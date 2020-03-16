

class ExclusionLoader:
    def __init__(self, file):
        self.file = file
        
    def load(self):
        found = []
        with open(self.file) as f:
            for line in f:
                trimmed = line.strip()
                if not trimmed.startswith('#'):
                    found.append(trimmed)
        return found
    


class DefaultValueLoader:
    def __init__(self, file):
        self.file = file
        
    def load(self):
        found = []
        with open(self.file) as f:
            for line in f:
                trimmed = line.strip()
                if not trimmed.startswith('#'):
                    parts = trimmed.split('=')
                    found.append(PropertyWithDefaultValue(parts[0], parts[1]))
        return found


class PropertyWithDefaultValue:
    def __init__(self, key, defaultValue):
        self.key = key
        self.defaultValue = defaultValue