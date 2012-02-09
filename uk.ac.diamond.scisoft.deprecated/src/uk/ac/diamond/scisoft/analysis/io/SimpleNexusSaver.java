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

import gda.analysis.io.IFileSaver;
import gda.analysis.io.ScanFileHolderException;
import gda.data.nexus.tree.NexusTreeProvider;
import gda.data.nexus.tree.NexusTreeWriter;

import java.util.Enumeration;

import ncsa.hdf.hdf5lib.HDF5Constants;

import org.nexusformat.NexusException;
import org.nexusformat.NexusFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.diamond.scisoft.analysis.dataset.AbstractDataset;
import uk.ac.diamond.scisoft.analysis.dataset.Nexus;

/**
 * File saver for simple NeXus files.
 */
public class SimpleNexusSaver implements IFileSaver {
	transient private static final Logger logger = LoggerFactory.getLogger(SimpleNexusSaver.class);

	private String fileName = "";
	
	/**
	 * @param FileName
	 */
	public SimpleNexusSaver(String FileName) {
		fileName = FileName;
	}

	@Override
	public void saveFile(DataHolder dh) throws ScanFileHolderException {
		NexusFile file;
		boolean duplicate = false;

		try {
			// TODO Check to see if the file exists...
			// TODO then either delete it or fail gracefully!
			file = new NexusFile(fileName, HDF5Constants.H5F_ACC_TRUNC);
			file.makegroup("ScanFileHolder", "NXentry");
			file.opengroup("ScanFileHolder", "NXentry");

			file.makegroup("datasets", "NXdata");
			file.opengroup("datasets", "NXdata");

			String[] headings = dh.getNames();

			for (int i = 0; i < headings.length; i++) {

				// First lets check to see if this item already exists in the NeXus file.
				Enumeration<?> keys = file.groupdir().keys();
				while (keys.hasMoreElements()) {
					String name = (String) keys.nextElement();
					if (name.equalsIgnoreCase(headings[i])) {
						duplicate = true;
					}
				}

				if (duplicate) {
					logger.warn("Duplicate headings found - only writing the first one.");
				} else {
					AbstractDataset data = dh.getDataset(headings[i]);
					int[] shape = data.getShape();
					file.makedata(headings[i], Nexus.getGroupDataType(data.getDtype()), shape.length, shape);
					file.opendata(headings[i]);
					int data_type = Nexus.getGroupDataType(data.getDtype()); 
					file.putdata(data_type, data.getBuffer());
					file.closedata();
				}
			}
			if(dh instanceof NexusTreeProvider){
				NexusTreeWriter.writeHere(file, ((NexusTreeProvider)dh).getNexusTree());
			}
			file.close();
		} catch (NexusException e) {
			logger.error("Problem writing to NeXus file {}", fileName);
			throw new ScanFileHolderException("Problem writing to NeXus file " + fileName);
		}

	}

}
