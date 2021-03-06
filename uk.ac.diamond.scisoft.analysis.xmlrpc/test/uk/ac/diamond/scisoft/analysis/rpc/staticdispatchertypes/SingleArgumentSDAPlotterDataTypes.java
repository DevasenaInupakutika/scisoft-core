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

package uk.ac.diamond.scisoft.analysis.rpc.staticdispatchertypes;

import uk.ac.diamond.scisoft.analysis.dataset.IDataset;
import uk.ac.diamond.scisoft.analysis.plotserver.GuiBean;
import uk.ac.diamond.scisoft.analysis.plotserver.GuiPlotMode;

/**
 * Tests for all the currently supported (i.e. flattenable) data types
 * used in SDAPlotter (apart from primitives and arrays of primitives tested elsewhere)
 */
@SuppressWarnings("unused")
public class SingleArgumentSDAPlotterDataTypes {
	public static Class<GuiBean> call(GuiBean param) {
		return GuiBean.class;
	}
	public static Class<GuiPlotMode> call(GuiPlotMode param) {
		return GuiPlotMode.class;
	}
	public static Class<IDataset> call(IDataset param) {
		return IDataset.class;
	}
	public static Class<IDataset[]> call(IDataset[] param) {
		return IDataset[].class;
	}
	public static Class<String[]> call(String[] param) {
		return String[].class;
	}
	public static Class<String> call(String param) {
		return String.class;
	}

}
