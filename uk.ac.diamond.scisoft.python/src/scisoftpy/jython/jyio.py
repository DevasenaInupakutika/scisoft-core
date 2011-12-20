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

from uk.ac.diamond.scisoft.analysis.io import PNGLoader as _pngload
from uk.ac.diamond.scisoft.analysis.io import PNGSaver as _pngsave
from uk.ac.diamond.scisoft.analysis.io import PNGScaledSaver as _pngscaledsave
from uk.ac.diamond.scisoft.analysis.io import JPEGLoader as _jpegload
from uk.ac.diamond.scisoft.analysis.io import JPEGSaver as _jpegsave
from uk.ac.diamond.scisoft.analysis.io import JPEGScaledSaver as _jpegscaledsave
try:
    from uk.ac.diamond.scisoft.analysis.io import TIFFImageLoader as _tiffload
except:
    import sys
    print >> sys.stderr, "Could not import TIFF loader"
    _tiffload = None

from uk.ac.diamond.scisoft.analysis.io import TIFFImageSaver as _tiffsave
from uk.ac.diamond.scisoft.analysis.io import JavaImageLoader as _imgload
from uk.ac.diamond.scisoft.analysis.io import JavaImageSaver as _imgsave
from uk.ac.diamond.scisoft.analysis.io import ADSCImageLoader as _adscload

try:
    from uk.ac.diamond.scisoft.analysis.io import CBFLoader as _cbfload
except:
    import sys #@Reimport
    print >> sys.stderr, "Could not import CBF loader"
    print >> sys.stderr, "Problem with path for dynamic/shared library or product bundling"
    _cbfload = None

from uk.ac.diamond.scisoft.analysis.io import CrysalisLoader as _crysload
from uk.ac.diamond.scisoft.analysis.io import MARLoader as _marload
from uk.ac.diamond.scisoft.analysis.io import PilatusTiffLoader as _ptiffload
from uk.ac.diamond.scisoft.analysis.io import SRSLoader as _srsload
from uk.ac.diamond.scisoft.analysis.io import PilatusEdfLoader as _pilatusEdfLoader
from uk.ac.diamond.scisoft.analysis.io import RawBinarySaver as _rawbinsave
from uk.ac.diamond.scisoft.analysis.io import RawBinaryLoader as _rawbinload
try:
    from gda.analysis.io import RawOutput as _rawtextsave
except:
    import sys #@Reimport
    print >> sys.stderr, "Could not import raw text loader"
    print >> sys.stderr, "Problem with product bundling"
    _rawtextsave = None

from uk.ac.diamond.scisoft.analysis.io import XMapLoader as _xmapload
from uk.ac.diamond.scisoft.analysis.io import DatLoader as _dlsdatload
from uk.ac.diamond.scisoft.analysis.io import NumPyFileLoader as _numpyload
from uk.ac.diamond.scisoft.analysis.io import NumPyFileSaver as _numpysave
from uk.ac.diamond.scisoft.analysis.io import RAxisImageLoader as _raxisload

from gda.analysis.io import ScanFileHolderException as io_exception

from uk.ac.diamond.scisoft.analysis.io import DataHolder as _jdataholder
from uk.ac.diamond.scisoft.analysis.io import MetaDataAdapter as _jmetadata

from jycore import asDatasetList#, asDatasetDict, toList

from scisoftpy.dictutils import DataHolder

from uk.ac.diamond.scisoft.analysis.io import HDF5Loader as _hdf5loader
from uk.ac.diamond.scisoft.analysis.hdf5 import HDF5File as _hdf5file
from uk.ac.diamond.scisoft.analysis.hdf5 import HDF5Group as _hdf5group

class h5manager(object):
    '''This holds a HDF5 tree and manages access to it. This provides
    dictionary-like access to lists of datasets
    '''
    def __init__(self, tree):
        '''Arguments:
        tree -- HDF tree
        '''
        if isinstance(tree, _hdf5file):
            self.file = tree
            self.grp = self.file.getGroup()
        elif isinstance(tree, _hdf5group):
            self.file = None
            self.grp = tree
        else:
            raise ValueError, "Tree not a hdf5 file or hdf5 group"

    def gettree(self):
        if self.file is None:
            t = _hdf5file(-1, "FakePlasticTree.h5")
            t.setGroup(self.grp)
            return t
        return self.file

    def __getitem__(self, key):
        '''Return a list of datasets in tree whose names match given key'''
        return asDatasetList(self.grp.getDatasets(key))

def loadnexus(name):
    '''Load a HDF5 file and return a HDF5 tree manager'''
    import warnings
    warnings.warn("This will be deprecated in the next version", PendingDeprecationWarning)
    h5loader = _hdf5loader(name)
    return h5manager(h5loader.loadTree())


from jyhdf5io import HDF5Loader
from jynxio import NXLoader

from java.io import OutputStream as _ostream #@UnresolvedImport
class _NoOutputStream(_ostream):
    def write(self, b, off, length): pass

class JavaLoader(object):
    def load(self):
        # capture all error messages
        from java.lang import System #@UnresolvedImport
        from java.io import PrintStream #@UnresolvedImport
        oldErr = System.err
        System.setErr(PrintStream(_NoOutputStream())) #@UndefinedVariable
        try:
            jdh = self.loadFile()
        finally:
            System.setErr(oldErr) #@UndefinedVariable

        data = asDatasetList(jdh.getList())
        names = jdh.getNames()
        basenames = []
        from os import path as _path
        for n in names: # remove bits of path so sanitising works
            if _path.exists(n):
                basenames.append(_path.basename(n))
            else:
                basenames.append(n)

        if len(data) != len(basenames):
            raise io_exception, "Number of names does not match number of datasets"

        metadata = None
        if self.load_metadata:
            meta = jdh.getMetadata()
            if meta:
                mnames = meta.metaNames
                if mnames:
                    metadata = [ (k, meta.getMetaValue(k)) for k in mnames ]

        return DataHolder(zip(basenames, data), metadata)

    def setloadmetadata(self, load_metadata):
        self.load_metadata = load_metadata
        self.setLoadMetadata(load_metadata)

class SRSLoader(JavaLoader, _srsload):
    def __init__(self, *arg):
        _srsload.__init__(self, *arg) #@UndefinedVariable
        self.load_metadata = True

class PilatusEdfLoader(JavaLoader, _pilatusEdfLoader):
    def __init__(self, *arg):
        _pilatusEdfLoader.__init__(self, *arg) #@UndefinedVariable
        self.load_metadata = True

class PNGLoader(JavaLoader, _pngload):
    def __init__(self, *arg):
        _pngload.__init__(self, *arg) #@UndefinedVariable
        self.load_metadata = True

class JPEGLoader(JavaLoader, _jpegload):
    def __init__(self, *arg):
        _jpegload.__init__(self, *arg) #@UndefinedVariable
        self.load_metadata = True

class ImageLoader(JavaLoader, _imgload):
    def __init__(self, *arg):
        _imgload.__init__(self, *arg) #@UndefinedVariable
        self.load_metadata = True

if _tiffload is None:
    TIFFLoader = None
else:
    class TIFFLoader(JavaLoader, _tiffload):
        def __init__(self, *arg):
            _tiffload.__init__(self, *arg) #@UndefinedVariable
            self.load_metadata = True

class ADSCLoader(JavaLoader, _adscload):
    def __init__(self, *arg):
        _adscload.__init__(self, *arg) #@UndefinedVariable
        self.load_metadata = True

if _cbfload is None:
    CBFLoader = None
else:
    try:
        _c = _cbfload()
    except:
        CBFLoader = None
        import sys #@Reimport
        print >> sys.stderr, "Problem creating CBF loader so no CBF support"
    else:
        del _c
        class CBFLoader(JavaLoader, _cbfload):
            def __init__(self, *arg):
                _cbfload.__init__(self, *arg) #@UndefinedVariable
                self.load_metadata = True

class CrysLoader(JavaLoader, _crysload):
    def __init__(self, *arg):
        _crysload.__init__(self, *arg) #@UndefinedVariable
        self.load_metadata = True

class MARLoader(JavaLoader, _marload):
    def __init__(self, *arg):
        _marload.__init__(self, *arg) #@UndefinedVariable
        self.load_metadata = True

class PilLoader(JavaLoader, _ptiffload):
    def __init__(self, *arg):
        _ptiffload.__init__(self, *arg) #@UndefinedVariable
        self.load_metadata = True

class RawLoader(JavaLoader, _rawbinload):
    def __init__(self, *arg):
        _rawbinload.__init__(self, *arg) #@UndefinedVariable
        self.load_metadata = True

class XMapLoader(JavaLoader, _xmapload):
    def __init__(self, *arg):
        _xmapload.__init__(self, *arg) #@UndefinedVariable
        self.load_metadata = True

class DLSLoader(JavaLoader, _dlsdatload):
    def __init__(self, *arg):
        _dlsdatload.__init__(self, *arg) #@UndefinedVariable
        self.load_metadata = True

class NumPyLoader(JavaLoader, _numpyload):
    def __init__(self, *arg):
        _numpyload.__init__(self, *arg) #@UndefinedVariable
        self.load_metadata = True

class RAxisLoader(JavaLoader, _raxisload):
    def __init__(self, *arg):
        _raxisload.__init__(self, *arg) #@UndefinedVariable
        self.load_metadata = True

input_formats = { "png": PNGLoader, "gif": ImageLoader,
               "jpeg": JPEGLoader,
               "tiff": TIFFLoader,
               "adsc": ADSCLoader, "img": ADSCLoader,
               "cbf": CBFLoader, "crys": CrysLoader,
               "mar": MARLoader, "mccd": MARLoader,
               "pil": PilLoader,
               "srs": SRSLoader,
               "binary": RawLoader, "xmap": XMapLoader,
               "npy": NumPyLoader,
               "dls": DLSLoader,
               "osc": RAxisLoader,
               "nx": NXLoader,
               "hdf5": HDF5Loader,
               "edf": PilatusEdfLoader
               }
colour_loaders  = [ PNGLoader, ImageLoader, JPEGLoader, TIFFLoader ]
loaders = [ ImageLoader, ADSCLoader, CrysLoader, MARLoader, CBFLoader, XMapLoader, RawLoader, SRSLoader, PilatusEdfLoader, HDF5Loader ]

class _Metadata(_jmetadata):
    def __init__(self, metadata):
        self.mdata = metadata

    def getMetaNames(self):
        return self.mdata.keys()

    def getMetaValue(self, key):
        return self.mdata.get(key)

class JavaSaver(object):
    @classmethod
    def tojava(cls, dataholder):
        '''
        Make a java data holder 
        '''
        jdh = _jdataholder()
        for k,v in dataholder.items():
            if k != 'metadata':
                jdh.addDataset(k, v)
        md = dict()
        for k, v in dataholder.metadata.items():
            md[k] = v
        jdh.setMetadata(_Metadata(md))
        return jdh

    def save(self, dataholder):
        jdh = JavaSaver.tojava(dataholder)
        self.saveFile(jdh)

class PNGSaver(JavaSaver, _pngsave):
    def __init__(self, *arg):
        _pngsave.__init__(self, *arg) #@UndefinedVariable

class ImageSaver(JavaSaver, _imgsave):
    def __init__(self, *arg):
        _imgsave.__init__(self, *arg) #@UndefinedVariable

class JPEGSaver(JavaSaver, _jpegsave):
    def __init__(self, *arg):
        _jpegsave.__init__(self, *arg) #@UndefinedVariable

class TIFFSaver(JavaSaver, _tiffsave):
    def __init__(self, *arg):
        _tiffsave.__init__(self, *arg) #@UndefinedVariable

class TextSaver(JavaSaver, _rawtextsave):
    def __init__(self, *arg):
        _rawtextsave.__init__(self, *arg) #@UndefinedVariable

class BinarySaver(JavaSaver, _rawbinsave):
    def __init__(self, *arg):
        _rawbinsave.__init__(self, *arg) #@UndefinedVariable

class NumPySaver(JavaSaver, _numpysave):
    def __init__(self, *arg):
        _numpysave.__init__(self, *arg) #@UndefinedVariable

output_formats = { "png": PNGSaver, "gif": ImageSaver, "jpeg": JPEGSaver, "tiff": TIFFSaver, "text": TextSaver,
              "binary": BinarySaver, "npy": NumPySaver }

class ScaledPNGSaver(JavaSaver, _pngscaledsave):
    def __init__(self, *arg):
        _pngscaledsave.__init__(self, *arg) #@UndefinedVariable

class ScaledJPEGSaver(JavaSaver, _jpegscaledsave):
    def __init__(self, *arg):
        _jpegscaledsave.__init__(self, *arg) #@UndefinedVariable

scaled_output_formats = { "png": ScaledPNGSaver, "jpeg": ScaledJPEGSaver }

