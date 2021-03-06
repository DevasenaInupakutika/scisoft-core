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

package uk.ac.diamond.scisoft.analysis.monitor;

/**
 * Interface to monitoring loading of files, which may take a while.
 */
public interface IMonitor {

	/**
	 * @param amount
	 */
	public void worked(int amount);
	
	/**
	 * @return true if user cancelled loading.
	 */
	public boolean isCancelled();
	
	/**
	 * Starts a subtask.
	 * 
	 * @param taskName
	 */
	public void subTask(String taskName);
	
	public class Stub implements IMonitor {

		@Override
		public void worked(int amount) {
			
		}

		@Override
		public boolean isCancelled() {
			return false;
		}

		@Override
		public void subTask(String taskName) {
			// nothing
		}
		
	}
}
