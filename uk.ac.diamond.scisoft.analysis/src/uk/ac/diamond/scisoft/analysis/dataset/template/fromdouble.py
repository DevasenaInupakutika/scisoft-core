###
# Copyright 2011 Diamond Light Source Ltd.
# 
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
# 
#   http://www.apache.org/licenses/LICENSE-2.0
# 
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
###

#!/usr/bin/env python
'''
From double dataset generate other classes

$ python fromdouble.py ../DoubleDataset.java

'''

from markers import transmutate #@UnresolvedImport

# default dataset definition
defds = { "DoubleDataset":["FLOAT64", "Double", "double", "getElementDoubleAbs", "toReal(obj)", "%.8g",
"NaN"] }

defkey = defds.keys()[0]

# all other dataset definitions
fds = { "FloatDataset":["FLOAT32", "Float", "float", "getElementDoubleAbs", "(float) toReal(obj)", "%.8g",
"NaN"] }

allds = { 
"IntegerDataset":["INT32", "Integer", "int", "getElementLongAbs", "(int) toLong(obj)", "%d",
"MIN_VALUE"],
"LongDataset":["INT64", "Long", "long", "getElementLongAbs", "toLong(obj)", "%d",
"MIN_VALUE"],
"ShortDataset":["INT16", "Short", "short", "getElementLongAbs", "(short) toLong(obj)", "%d",
"MIN_VALUE"],
"ByteDataset":["INT8", "Byte", "byte", "getElementLongAbs", "(byte) toLong(obj)", "%d",
"MIN_VALUE"]
 }

bds = {
"BooleanDatasetBase":["BOOL", "Boolean", "boolean", "getElementBooleanAbs", "toBoolean(obj)", "%b", "FALSE"]
 }

ods = {
"StringDatasetBase":["STRING", "String", "String", "getStringAbs", "obj.toString()", "%s", "FALSE"],
"ObjectDatasetBase":["OBJECT", "Object", "Object", "getObjectAbs", "obj", "%s", "FALSE"]
 }

def generateclass(dclass):
    handlers  = [ transmutate(__file__, defkey, defds[defkey], d, fds[d], True) for d in fds ]
    handlers += [ transmutate(__file__, defkey, defds[defkey], d, allds[d], False) for d in allds ]
    handlers += [ transmutate(__file__, defkey, defds[defkey], d, bds[d], False, True) for d in bds ]
    handlers += [ transmutate(__file__, defkey, defds[defkey], d, ods[d], False, False, True) for d in ods ]
    files  = [ open(d + ".java", "w") for d in fds ]
    files += [ open(d + ".java", "w") for d in allds ]
    files += [ open(d + ".java", "w") for d in bds ]
    files += [ open(d + ".java", "w") for d in ods ]
    ncls = len(files)

    while True:
        l = dclass.readline()
        if not l:
            break
        for n in range(ncls):
            nl = handlers[n].processline(l)
            if nl != None:
                print >> files[n], nl

if __name__ == '__main__':
    import sys
    if len(sys.argv) > 1:
        fname = sys.argv[1]
    else:
        fname = "../DoubleDataset.java"

    dclass_file = open(fname, 'r')

    generateclass(dclass_file)
