/*
 * Copyright 2011 Diamond Light Source Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.diamond.scisoft.analysis.dataset;

import gda.analysis.io.ScanFileHolderException;
import gda.data.nexus.extractor.NexusExtractor;
import gda.data.nexus.extractor.NexusExtractorException;
import gda.data.nexus.extractor.NexusGroupData;
import gda.data.nexus.tree.INexusSourceProvider;
import gda.data.nexus.tree.INexusTree;

import java.io.File;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Arrays;

import ncsa.hdf.hdf5lib.H5;
import ncsa.hdf.hdf5lib.HDF5Constants;
import ncsa.hdf.hdf5lib.exceptions.HDF5LibraryException;

import org.nexusformat.NexusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.diamond.scisoft.analysis.io.ILazyLoader;
import uk.ac.gda.monitor.IMonitor;

/**
 * Helper methods for NeXus
 */
public class Nexus {
	/**
	 * Setup the logging facilities
	 */
	transient private static final Logger logger = LoggerFactory.getLogger(Nexus.class);

	/**
	 * Get dataset type from NeXus group data type
	 * @param type
	 * @return dataset type
	 */
	static public int getDType(int type) {
		try {
			if (H5.H5Tequal(type, HDF5Constants.H5T_NATIVE_INT8)
					|| H5.H5Tequal(type, HDF5Constants.H5T_NATIVE_UINT8))
				return AbstractDataset.INT8;

			if (H5.H5Tequal(type, HDF5Constants.H5T_NATIVE_INT16)
					|| H5.H5Tequal(type, HDF5Constants.H5T_NATIVE_UINT16))
				return AbstractDataset.INT16;

			if (H5.H5Tequal(type, HDF5Constants.H5T_NATIVE_INT32)
					|| H5.H5Tequal(type, HDF5Constants.H5T_NATIVE_UINT32))
				return AbstractDataset.INT32;

			if (H5.H5Tequal(type, HDF5Constants.H5T_NATIVE_INT64)
					|| H5.H5Tequal(type, HDF5Constants.H5T_NATIVE_UINT64))
				return AbstractDataset.INT64;

			if (H5.H5Tequal(type, HDF5Constants.H5T_NATIVE_FLOAT))
				return AbstractDataset.FLOAT32;

			if (H5.H5Tequal(type, HDF5Constants.H5T_NATIVE_DOUBLE))
				return AbstractDataset.FLOAT64;
		} catch (HDF5LibraryException e) {
			throw new RuntimeException("HDF5 type check error", e);
		}
		throw new IllegalArgumentException(
				"Unknown or unsupported NeXus data type");
	}

	/**
	 * Get NeXus group data type from dataset type
	 * @param dtype
	 * @return NeXus group data type
	 */
	static public int getGroupDataType(int dtype) {
		switch (dtype) {
		case AbstractDataset.FLOAT64:
			return HDF5Constants.H5T_NATIVE_DOUBLE;
		case AbstractDataset.FLOAT32:
			return HDF5Constants.H5T_NATIVE_FLOAT;
		case AbstractDataset.INT64:
			return HDF5Constants.H5T_NATIVE_INT64;
		case AbstractDataset.INT32:
			return HDF5Constants.H5T_NATIVE_INT32;
		case AbstractDataset.INT16:
			return HDF5Constants.H5T_NATIVE_INT16;
		case AbstractDataset.INT8:
			return HDF5Constants.H5T_NATIVE_INT8;
		default:
			throw new IllegalArgumentException(
					"Unknown or unsupported dataset type");
		}
	}

	/**
	 * Get unsigned integer NeXus group data type from dataset type
	 * @param dtype
	 * @return NeXus group data type
	 */
	static public int getUnsignedGroupDataType(int dtype) {
		switch (dtype) {
		case AbstractDataset.INT64:
			return HDF5Constants.H5T_NATIVE_UINT64;
		case AbstractDataset.INT32:
			return HDF5Constants.H5T_NATIVE_UINT32;
		case AbstractDataset.INT16:
			return HDF5Constants.H5T_NATIVE_UINT16;
		case AbstractDataset.INT8:
			return HDF5Constants.H5T_NATIVE_UINT8;
		default:
			throw new IllegalArgumentException("Unknown or unsupported dataset type");
		}
	}
	/**
	 * Create a dataset from NeXus group data
	 * @param groupData
	 * @param keepBitWidth if true, does not promoted unsigned types to wider (signed) Java primitive type 
	 * @return dataset
	 */
	static public AbstractDataset createDataset(NexusGroupData groupData, boolean keepBitWidth) {
		AbstractDataset ds = null;
		boolean no_type = true;
		try {
			if (H5.H5Tequal(groupData.type, HDF5Constants.H5T_NATIVE_INT8)
					|| H5.H5Tequal(groupData.type, HDF5Constants.H5T_NATIVE_UINT8)) {
				byte[] bData = (byte[]) groupData.getBuffer();
				ds = new ByteDataset(Arrays.copyOf(bData, bData.length), groupData.dimensions);
				no_type = false;
			}

			if (H5.H5Tequal(groupData.type, HDF5Constants.H5T_NATIVE_INT16)
					|| H5.H5Tequal(groupData.type, HDF5Constants.H5T_NATIVE_UINT16)) {
				short[] sData = (short[]) groupData.getBuffer();
				ds = new ShortDataset(Arrays.copyOf(sData, sData.length), groupData.dimensions);
				no_type = false;
			}

			if (H5.H5Tequal(groupData.type, HDF5Constants.H5T_NATIVE_INT32)
					|| H5.H5Tequal(groupData.type, HDF5Constants.H5T_NATIVE_UINT32)) {
				int[] iData = (int[]) groupData.getBuffer();
				ds = new IntegerDataset(Arrays.copyOf(iData, iData.length), groupData.dimensions);
				no_type = false;
			}

			if (H5.H5Tequal(groupData.type, HDF5Constants.H5T_NATIVE_INT64)
					|| H5.H5Tequal(groupData.type, HDF5Constants.H5T_NATIVE_UINT64)) {
				long[] lData = (long[]) groupData.getBuffer();
				ds = new LongDataset(Arrays.copyOf(lData, lData.length), groupData.dimensions);
				no_type = false;
			}

			if (H5.H5Tequal(groupData.type, HDF5Constants.H5T_NATIVE_FLOAT)) {
				float[] fData = (float[]) groupData.getBuffer();
				ds = new FloatDataset(Arrays.copyOf(fData, fData.length), groupData.dimensions);
				no_type = false;
			}

			if (H5.H5Tequal(groupData.type, HDF5Constants.H5T_NATIVE_DOUBLE)) {
				double[] dData = (double[]) groupData.getBuffer();
				ds = new DoubleDataset(Arrays.copyOf(dData, dData.length), groupData.dimensions);
				no_type = false;
			}
		} catch (HDF5LibraryException e) {
			throw new RuntimeException("HDF5 type check error", e);
		}
		if (no_type)
			throw new IllegalArgumentException("Unknown or unsupported dataset type");

		if (!keepBitWidth) {
			try {
				if (H5.H5Tequal(groupData.type, HDF5Constants.H5T_NATIVE_UINT8)) {
					ds = new ShortDataset(ds);
					DatasetUtils.unwrapUnsigned(ds, 8);
				}

				if (H5.H5Tequal(groupData.type, HDF5Constants.H5T_NATIVE_UINT16)) {
					ds = new IntegerDataset(ds);
					DatasetUtils.unwrapUnsigned(ds, 16);
				}

				if (H5.H5Tequal(groupData.type, HDF5Constants.H5T_NATIVE_UINT32)) {
					ds = new LongDataset(ds);
					DatasetUtils.unwrapUnsigned(ds, 32);
				}
			} catch (HDF5LibraryException e) {
				throw new RuntimeException("HDF5 type check error", e);
			}
		}
		return ds;
	}

	/**
	 * Make a NeXus group data object from a IDataset
	 * @param data
	 * @return group data
	 */
	public static NexusGroupData createNexusGroupData(IDataset data) {
		AbstractDataset ad = DatasetUtils.convertToAbstractDataset(data);
		return new NexusGroupData(ad.getShape(), getGroupDataType(ad.getDtype()), ad.getBuffer());
	}

	public static ILazyDataset createLazyDataset(INexusTree node) {
		NexusGroupData groupData = node.getData();
		final URL source;
		final String nodePath;
		INexusTree top = node;
		while (top.getParentNode() != null) {
			top = top.getParentNode();
		}
		if (top instanceof INexusSourceProvider) {
			source = ((INexusSourceProvider) top).getSource();
		} else {
			source = null;
		}

		if (source == null) {
			logger.error("Source of Nexus tree is not defined");
			return null;
		}

		final String name = node.getName();
		nodePath = node.getNodePathWithClasses();

		final int[] trueShape = groupData.dimensions;
		ILazyDataset groupDataset;


		if (groupData.getBuffer() == null) {
			
			ILazyLoader l = new ILazyLoader() {
				@Override
				public boolean isFileReadable() {
					try {
						String host = source.getHost();
						if (host != null && host.length() > 0 && !host.equals(InetAddress.getLocalHost().getHostName()))
							return false;
					} catch (UnknownHostException e) {
						logger.warn("Problem finding local host so ignoring check", e);
					}
					return new File(source.getPath()).canRead();
				}

				@Override
				public String toString() {
					return source.getFile() + ":" + nodePath;
				}

				@Override
				public AbstractDataset getDataset(IMonitor mon, int[] shape, int[] start, int[] stop, int[] step) throws ScanFileHolderException {
					final int rank = shape.length;
					int[] lstart, lstop, lstep;

					if (step == null) {
						lstep = new int[rank];
						for (int i = 0; i < rank; i++) {
							lstep[i] = 1;
						}
					} else {
						lstep = step;
					}

					if (start == null) {
						lstart = new int[rank];
					} else {
						lstart = start;
					}

					if (stop == null) {
						lstop = new int[rank];
					} else {
						lstop = stop;
					}

					int[] newShape;
					if (rank > 1 || shape[0] > 0) {
						newShape = AbstractDataset.checkSlice(shape, start, stop, lstart, lstop, lstep);
					} else {
						newShape = new int[rank];
					}

					boolean useSteps = false;
					int[] size;

					for (int i = 0; i < rank; i++) {
						if (lstep[i] != 1) {
							useSteps = true;
							break;
						}
					}

					if (useSteps) { // have to get superset of slice as NeXus API's getslab doesn't allow steps
						size = new int[rank];
						for (int i = 0; i < rank; i++) {
							int last = lstart[i] + (newShape[i]-1)*lstep[i]; // last index
							if (lstep[i] < 0) {
								size[i] = lstart[i] - last + 1;
								lstart[i] = last;
							} else {
								size[i] = last - lstart[i] + 1;
							}
						}
					} else {
						size = newShape;
					}

					AbstractDataset d = null;
					try {
						NexusGroupData ngd = null;
						if (!Arrays.equals(trueShape, shape)) { // if shape was squeezed then need to translate to true slice
							final int trank = trueShape.length;
							int[] tstart = new int[trank];
							int[] tsize = new int[trank];

							int j = 0;
							for (int i = 0; i < trank; i++) {
								if (trueShape[i] == 1) {
									tstart[i] = 0;
									tsize[i] = 1;
								} else {
									tstart[i] = lstart[j];
									tsize[i] = size[j];
									j++;
								}
							}
							ngd = NexusExtractor.getNexusGroupData(source, nodePath, tstart, tsize, logger.isDebugEnabled());
							d = createDataset(ngd, false);
							d.setShape(size); // squeeze shape back
						} else {
							ngd = NexusExtractor.getNexusGroupData(source, nodePath, lstart, size, logger.isDebugEnabled());
							d = createDataset(ngd, false);
						}
						if (d != null) {
							if (useSteps)
								d = d.getSlice(null, null, lstep); // reduce dataset to requested elements
							d.setName(name);
						}
					} catch (NexusException e) {
						logger.error("Problem with NeXus library: {}", e.getMessage());
					} catch (NexusExtractorException e) {
						logger.error("Problem with NeXus extraction: {}", e.getMessage());
					}
					return d;
				}
			};

			groupDataset = new LazyDataset(name, getDType(groupData.type), trueShape.clone(), l);
		} else {
			AbstractDataset dataset = createDataset(groupData, false);
			dataset.setName(name);
			groupDataset = dataset;
		}

		return groupDataset;
	}
}
