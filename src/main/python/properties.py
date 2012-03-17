#!/usr/bin/python

class PropertiesReader(object):
    
    def load(self, filename):
        try:
            self.__property_map = dict()
            for line in open(filename, "r"):
                key_value = line.split("=")
                if len(key_value) == 2:
                    self.__property_map[key_value[0]] = key_value[1]

            return True
        except IOError:
            return False

    def get_property(self, property_name):
        if property_name in self.__property_map:
            return self.__property_map[property_name]
        return None

    def get_int_property(self, property_name):
        value = self.get_property(property_name)
        if value is not None:
            return int(value)
        else:
            return None
