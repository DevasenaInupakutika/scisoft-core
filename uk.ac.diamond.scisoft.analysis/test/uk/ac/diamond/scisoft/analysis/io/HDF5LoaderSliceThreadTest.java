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

package uk.ac.diamond.scisoft.analysis.io;

import gda.analysis.io.ScanFileHolderException;

import java.io.File;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import uk.ac.diamond.scisoft.analysis.dataset.IDataset;
import uk.ac.diamond.scisoft.analysis.dataset.ILazyDataset;
import uk.ac.diamond.scisoft.analysis.dataset.Slice;
import uk.ac.diamond.scisoft.analysis.hdf5.HDF5Dataset;
import uk.ac.diamond.scisoft.analysis.hdf5.HDF5File;
import uk.ac.diamond.scisoft.analysis.hdf5.HDF5NodeLink;

public class HDF5LoaderSliceThreadTest extends LoaderThreadTestBase {
	
	private static String filename = System.getProperty("GDALargeTestFilesLocation")+"/NexusUITest/DCT_201006-good.h5";

	private ILazyDataset dataset;

	@Before
	public void createLazyDataset() {
		Assert.assertTrue(new File(filename).canRead());
		HDF5Loader l = new HDF5Loader(filename);
		HDF5File t;
		try {
			t = l.loadTree();
		} catch (ScanFileHolderException e) {
			throw new IllegalArgumentException("Could not load tree");
		}
		HDF5NodeLink n = t.findNodeLink("/RawDCT/data");
		Assert.assertTrue(n.isDestinationADataset());
		HDF5Dataset d = (HDF5Dataset) n.getDestination();

		dataset = d.getDataset();
	}

	@Override
	@Test
	public void testInTestThread() throws Exception {
		super.testInTestThread();
	}

	@Override
	@Test
	public void testWithTenThreads() throws Exception {
		super.testWithTenThreads();
	}

	@Test
	public void testWithNThreads() throws Exception {
		super.testWithNThreads(60);
	}

	@Override
	public void doTestOfDataSet(int threadIndex) throws Exception {
		final Slice[] nSlice = new Slice[] { new Slice(threadIndex, threadIndex+1), null, null };
		final IDataset s = dataset.getSlice(nSlice);
		Assert.assertEquals("Thd " + threadIndex + " is not correct size", 171*1692, s.getSize());
	}
}
