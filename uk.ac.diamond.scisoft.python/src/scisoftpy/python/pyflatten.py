###
# Copyright © 2011 Diamond Light Source Ltd.
# Contact :  ScientificSoftware@diamond.ac.uk
# 
# This is free software: you can redistribute it and/or modify it under the
# terms of the GNU General Public License version 3 as published by the Free
# Software Foundation.
# 
# This software is distributed in the hope that it will be useful, but 
# WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
# Public License for more details.
# 
# You should have received a copy of the GNU General Public License along
# with this software. If not, see <http://www.gnu.org/licenses/>.
###

''' 
Python version of flattening. See Java version for documentation on flattening.
This module is generally for internal use by AnalysisRPC and is only made
public for the purpose of testing, see pyflatten_test.py
'''

import scisoftpy.python.pyroi as _roi
import scisoftpy.python.pybeans as _beans
import scisoftpy.python.pywrapper as _wrapper
import numpy as _np #@UnresolvedImport
from tempfile import mkstemp
import os
import copy
import uuid

TYPE = "__type__"
CONTENT = "content"

_TEMP_LOCATION = None

def settemplocation(loc=None):
    '''
     Set a custom temporary file location. This is used by some flatteners to store large data sets which are faster
     to write to disk than send over XML-RPC.
     
     There is no requirement for the unflattener at the other end to have the same temp location as a full path to the
     temp file should be stored in the flattened form.
     
     loc new temp file location to use, or None to use default of system temp
    '''
    global _TEMP_LOCATION
    _TEMP_LOCATION = loc

class flatteningHelper(object):
    def __init__(self, typeObj, typeName):
        self.typeObj = typeObj
        self.typeName = typeName

    def canunflatten(self, obj):
        return isinstance(obj, dict) and obj.get(TYPE) == self.typeName
    
    def canflatten(self, obj):
        return isinstance(obj, self.typeObj)

class roiHelper(flatteningHelper):

    def __init__(self, typeObj, typeName):
        super(roiHelper, self).__init__(typeObj, typeName)
        
    def flatten(self, thisRoiBase):
        d = copy.deepcopy(thisRoiBase.__dict__)
        rval = dict()
        for k, v in d.iteritems():
            rval[k] = flatten(v)
        rval[TYPE] = self.typeName
        return rval
    
    def unflatten(self, obj):
        unflattenedObj = dict()
        for k, v in obj.iteritems():
            if k == TYPE:
                continue
            v = unflatten(v)
            unflattenedObj[k] = v
        return self.typeObj(**unflattenedObj)

    @staticmethod
    def getROIBaseHelper():
        return roiHelper(_roi.roibase, "uk.ac.diamond.scisoft.analysis.roi.ROIBase")
        
    @staticmethod
    def getRectHelper():
        return roiHelper(_roi.rect, "uk.ac.diamond.scisoft.analysis.roi.RectangularROI")
        
    @staticmethod
    def getSectHelper():
        return roiHelper(_roi.sect, "uk.ac.diamond.scisoft.analysis.roi.SectorROI")

    @staticmethod
    def getLineHelper():
        return roiHelper(_roi.line, "uk.ac.diamond.scisoft.analysis.roi.LinearROI")

class axisMapBeanHelper(flatteningHelper):

    TYPE_NAME = "uk.ac.diamond.scisoft.analysis.plotserver.AxisMapBean"
    
    def __init__(self):
        super(axisMapBeanHelper, self).__init__(_beans.axismapbean, self.TYPE_NAME)
    
    def flatten(self, thisAxisMapBean):
        rval = dict()
        rval[TYPE] = self.TYPE_NAME
        rval[_beans.axismapbean._AXIS_ID] = thisAxisMapBean.axisID
        rval[_beans.axismapbean._MAP_MODE] = thisAxisMapBean.mapMode
        return rval
    
    def unflatten(self, obj):
        unflattenedMap = dict()
        unflattenedMap[_beans.axismapbean._AXIS_ID] = unflatten(obj[_beans.axismapbean._AXIS_ID])
        unflattenedMap[_beans.axismapbean._MAP_MODE] = unflatten(obj[_beans.axismapbean._MAP_MODE])        
        return _beans.axismapbean(**unflattenedMap)
        
class datasetWithAxisInformationHelper(flatteningHelper):

    TYPE_NAME = "uk.ac.diamond.scisoft.analysis.plotserver.DataSetWithAxisInformation"
    
    def __init__(self):
        super(datasetWithAxisInformationHelper, self).__init__(_beans.datasetwithaxisinformation, self.TYPE_NAME)
        
    def flatten(self, thisDatasetWithAxisInformation):
        rval = dict()
        rval[TYPE] = self.TYPE_NAME
        rval[_beans.datasetwithaxisinformation._DATA] = flatten(thisDatasetWithAxisInformation.data)
        rval[_beans.datasetwithaxisinformation._AXIS_MAP] = flatten(thisDatasetWithAxisInformation.axisMap)
        return rval
    
    def unflatten(self, obj):
        unflattenedMap = dict()
        unflattenedMap[_beans.datasetwithaxisinformation._DATA] = unflatten(obj[_beans.datasetwithaxisinformation._DATA])
        unflattenedMap[_beans.datasetwithaxisinformation._AXIS_MAP] = unflatten(obj[_beans.datasetwithaxisinformation._AXIS_MAP])        
        return _beans.datasetwithaxisinformation(**unflattenedMap)
            
class dictHelper(flatteningHelper):
    TYPE_NAME = "java.util.Map"
    KEYS = "keys"
    VALUES = "values"
    
    def __init__(self):
        super(dictHelper, self).__init__(dict, self.TYPE_NAME)
        
    def flatten(self, thisDict):
        rval = dict()
        rval[TYPE] = self.TYPE_NAME
        rval[self.KEYS] = []
        rval[self.VALUES] = []
        for k, v in thisDict.iteritems():
            rval[self.KEYS].append(flatten(k))
            rval[self.VALUES].append(flatten(v))
        return rval

    def unflatten(self, thisDict):
        rval = dict()
        for k, v in zip(thisDict[self.KEYS], thisDict[self.VALUES]):
            rval[unflatten(k)] = unflatten(v)
        return rval

class passThroughHelper(object):
    def flatten(self, obj):
        return obj
    
    def unflatten(self, obj):
        return obj

    def canflatten(self, obj):
        return isinstance(obj, (str, int, long, float, _wrapper.binaryWrapper))

    def canunflatten(self, obj):
        return self.canflatten(obj)

class ndArrayHelper(flatteningHelper):
    TYPE_NAME = "uk.ac.diamond.scisoft.analysis.dataset.AbstractDataset"
    FILENAME = "filename"
    DELETEFILEAFTERLOAD = "deletefile"
    INDEX = "index"
    NAME = "name"
    
    def __init__(self):
        super(ndArrayHelper, self).__init__(_np.ndarray, self.TYPE_NAME)
    
    def flatten(self, obj):
        rval = dict()
        if isinstance(obj, _np.ndarray):
            global _TEMP_LOCATION
            (osfd, filename) = mkstemp(suffix='.npy', prefix='scisofttmp-', dir=_TEMP_LOCATION)
            os.close(osfd)
            try:
                _np.save(filename, obj)
            except:
                # If we failed to write the file, remove it
                # mkstemp returned a new file that did not exist, so 
                # we can't be removing someone else's file
                os.remove(filename)
                raise
            rval[self.FILENAME] = filename
            rval[self.DELETEFILEAFTERLOAD] = True
        elif isinstance(obj, _wrapper.abstractdatasetdescriptor):
            rval[self.FILENAME] = obj.filename
            if obj.deleteAfterLoad:
                rval[self.DELETEFILEAFTERLOAD] = True
            if obj.index != None:
                rval[self.INDEX] = obj.index
            if obj.name != None:
                rval[self.NAME] = obj.name

        rval[TYPE] = self.TYPE_NAME
        return rval

    def unflatten(self, obj):
        filename = obj[self.FILENAME]
        deletefile = False
        if self.DELETEFILEAFTERLOAD in obj:
            deletefile = obj[self.DELETEFILEAFTERLOAD]
        try:
            return _np.load(filename)
        finally:
            if deletefile:
                os.remove(filename)
                
    def canflatten(self, obj):
        return isinstance(obj, (_np.ndarray, _wrapper.abstractdatasetdescriptor))

class guiBeanHelper(flatteningHelper):
    TYPE_NAME = "uk.ac.diamond.scisoft.analysis.plotserver.GuiBean"
    
    def __init__(self):
        super(guiBeanHelper, self).__init__(_beans.guibean, self.TYPE_NAME)
    
    def flatten(self, obj):
        rval = dict()
        for k, v in obj.iteritems():
            rval[str(k)] = flatten(v)
            
        rval[TYPE] = self.TYPE_NAME
        return rval

    def unflatten(self, thisDict):
        rval = _beans.guibean()
        for k, v in thisDict.iteritems():
            if k == TYPE:
                continue
            guiparam = _beans.parameters.get(k)
            val = unflatten(v)
            if guiparam == _beans.parameters.plotid and not isinstance(val, uuid.UUID):
                val = uuid.UUID(val)
            elif guiparam == _beans.parameters.plotmode and not isinstance(val, _beans.plotmode._plotmodehelper):
                val = _beans.plotmode.get(val)
            rval[guiparam] = val
        return rval

class guiParametersHelper(flatteningHelper):
    TYPE_NAME = "uk.ac.diamond.scisoft.analysis.plotserver.GuiParameters"

    def __init__(self):
        super(guiParametersHelper, self).__init__(_beans._parameters._parametershelper, self.TYPE_NAME)
    
    def flatten(self, obj):
        rval = dict()
        rval[TYPE] = self.TYPE_NAME
        rval[CONTENT] = str(obj)
        return rval

    def unflatten(self, thisDict):
        return _beans.parameters.get(thisDict[CONTENT])

class plotModeHelper(flatteningHelper):
    TYPE_NAME = "uk.ac.diamond.scisoft.analysis.plotserver.GuiPlotMode"

    def __init__(self):
        super(plotModeHelper, self).__init__(_beans._plotmode._plotmodehelper, self.TYPE_NAME)
    
    def flatten(self, obj):
        rval = dict()
        rval[TYPE] = self.TYPE_NAME
        rval[CONTENT] = str(obj)
        return rval

    def unflatten(self, thisDict):
        return _beans.plotmode.get(thisDict[CONTENT])

class roiListHelper(flatteningHelper):
    
    def __init__(self, typeObj, typeName):
        super(roiListHelper, self).__init__(typeObj, typeName)
        
    def flatten(self, thisList):
        flatList = []
        for thisRoi in thisList:
            flatList.append(flatten(thisRoi))
        rval = dict()
        rval[TYPE] = self.typeName
        rval[CONTENT] = flatList
        return rval
    
    def unflatten(self, obj):
        rval = self.typeObj()
        for thisRoi in obj[CONTENT]:
            rval.append(unflatten(thisRoi))
        return rval

    @staticmethod
    def getLineListHelper():
        return roiListHelper(_roi.linelist, "uk.ac.diamond.scisoft.analysis.roi.LinearROIList")

    @staticmethod
    def getRectListHelper():
        return roiListHelper(_roi.rectlist, "uk.ac.diamond.scisoft.analysis.roi.RectangularROIList")

    @staticmethod
    def getSectListHelper():
        return roiListHelper(_roi.sectlist, "uk.ac.diamond.scisoft.analysis.roi.SectorROIList")
            
class listAndTupleHelper(object):
    def flatten(self, obj):
        outList = []
        for thisItem in obj:
            outList.append(flatten(thisItem))
        return outList
    
    def unflatten(self, obj):
        outList = []
        for thisItem in obj:
            outList.append(unflatten(thisItem))
        return outList
        
    
    def canflatten(self, obj):
        return isinstance(obj, (list, tuple))

    def canunflatten(self, obj):
        return isinstance(obj, (list, tuple))

class dataBeanHelper(flatteningHelper):
    TYPE_NAME = "uk.ac.diamond.scisoft.analysis.plotserver.DataBean"
    
    def __init__(self):
        super(dataBeanHelper, self).__init__(_beans.databean, self.TYPE_NAME)

    def flatten(self, thisDataBean):
        rval = dict()
        rval[TYPE] = self.TYPE_NAME
        rval[_beans.databean._AXIS_DATA] = flatten(thisDataBean.axisData)
        rval[_beans.databean._DATA] = flatten(thisDataBean.data)
        return rval
    
    def unflatten(self, obj):
        unflattenedMap = dict()
        unflattenedMap[_beans.databean._AXIS_DATA] = unflatten(obj[_beans.databean._AXIS_DATA])
        unflattenedMap[_beans.databean._DATA] = unflatten(obj[_beans.databean._DATA])
        return _beans.databean(**unflattenedMap)

class noneHelper(flatteningHelper):
    TYPE_NAME = "__None__"
    TYPED_NONE_TYPE = "typedNoneType";
    NULL = "null";
    
    def __init__(self):
        super(noneHelper, self).__init__(None, self.TYPE_NAME)

    def flatten(self, thisNone):
        rval = dict()
        rval[TYPE] = self.TYPE_NAME
        if not (thisNone is None):
            rval[self.TYPED_NONE_TYPE] = thisNone.typedNoneType
        else:
            rval[self.TYPED_NONE_TYPE] = self.NULL
        return rval
    
    def unflatten(self, obj):
        if obj is None or obj[self.TYPED_NONE_TYPE] == self.NULL:
            return None
        
        rval = _wrapper.typednone()
        rval.typedNoneType = obj[self.TYPED_NONE_TYPE]
        return rval
    
    def canflatten(self, obj):
        return obj is None or isinstance(obj, _wrapper.typednone)
    
    def canunflatten(self, obj):
        return obj is None or super(noneHelper, self).canunflatten(obj)
        
class uuidHelper(flatteningHelper):
    TYPE_NAME = "java.util.UUID"
    
    def __init__(self):
        super(uuidHelper, self).__init__(uuid.UUID, self.TYPE_NAME)

    def flatten(self, thisUUID):
        rval = dict()
        rval[TYPE] = self.TYPE_NAME
        rval[CONTENT] = str(thisUUID)
        return rval
    
    def unflatten(self, obj):
        return uuid.UUID(obj[CONTENT])
    
class exceptionHelper(flatteningHelper):
    TYPE_NAME = "java.lang.Exception"
    
    def __init__(self):
        super(exceptionHelper, self).__init__(Exception, self.TYPE_NAME)

    def flatten(self, thisException):
        rval = dict()
        rval[TYPE] = self.TYPE_NAME
        rval[CONTENT] = str(thisException)
        return rval
    
    def unflatten(self, obj):
        return Exception(obj[CONTENT])
    
helpers = [noneHelper(), roiListHelper.getLineListHelper(),
           roiListHelper.getSectListHelper(), roiListHelper.getRectListHelper(),
           roiHelper.getRectHelper(), roiHelper.getSectHelper(),
           roiHelper.getLineHelper(), roiHelper.getROIBaseHelper(), 
           ndArrayHelper(), guiBeanHelper(),
           guiParametersHelper(), plotModeHelper(), axisMapBeanHelper(),
           datasetWithAxisInformationHelper(), dataBeanHelper(),
           dictHelper(), passThroughHelper(), listAndTupleHelper(),
           uuidHelper(), exceptionHelper()]

def addhelper(helper):
    helpers.insert(0, helper)

def flatten(obj):
    for thisHelper in helpers:
        if thisHelper.canflatten(obj):
            return thisHelper.flatten(obj)
    raise TypeError("Object " + repr(obj) + " cannot be flattened")
        
def unflatten(obj):  
    for thisHelper in helpers:
        if thisHelper.canunflatten(obj):
            return thisHelper.unflatten(obj)
    raise TypeError("Object " + repr(obj) + " cannot be unflattened")

def canflatten(obj):
    for thisHelper in helpers:
        if thisHelper.canflatten(obj):
            return True
    return False
        
def canunflatten(obj):
    for thisHelper in helpers:
        if thisHelper.canunflatten(obj):
            return True
    return False
        

        


