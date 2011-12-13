/*-
 * Copyright © 2011 Diamond Light Source Ltd.
 *
 * This file is part of GDA.
 *
 * GDA is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License version 3 as published by the Free
 * Software Foundation.
 *
 * GDA is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along
 * with GDA. If not, see <http://www.gnu.org/licenses/>.
 */

package uk.ac.diamond.scisoft.analysis.io;


import java.util.Date;

/**
 * This is an interface that has been designed to add information about 
 * the file in addition to information which has been implemented 
 * in IMetaData
 * 
 * It is intended that the implementation of this class will be through an adaptor
 * and the getters specified here should return a value held in a map that is specified in
 * the interface IMetaData. This means that this class serves to decorate the IMetaData
 * interface by specifying some of the key value pairs within the metadata.
 */
public interface IExtendedMetadata extends IMetaData {

	/**
	 * This should be the timestamp when the experiment or measurement took place which should
	 * be recorded in the header of the file, if applicable
	 * 
	 * @return a object to represent when the data was created
	 */
	public Date getCreation();

	/**
	 * @return a date object that indicated when the data was last modified
	 */
	public Date getLastModified();

	/**
	 * @return String representing the user who created the file
	 */
	public String getCreator();

	/**
	 * @return a String containing the filename
	 */
	public String getFileName();

	/**
	 * @return the owner of the file
	 */
	public String getFileOwner();

	/**
	 * @return a long representing the size of the file in bytes
	 */
	public long getFileSize();

	/**
	 * @return the full path of the file
	 */
	public String getFullPath();

	/**
	 * @return The scan command as a String that was used to generate the data. This can be null as not always
	 *         applicable.
	 */
	public String getScanCommand();

}