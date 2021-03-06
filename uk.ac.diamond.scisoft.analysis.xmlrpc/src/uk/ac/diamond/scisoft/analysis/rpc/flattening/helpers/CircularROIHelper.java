/*
 * Copyright 2012 Diamond Light Source Ltd.
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

package uk.ac.diamond.scisoft.analysis.rpc.flattening.helpers;

import java.util.Map;

import uk.ac.diamond.scisoft.analysis.roi.CircularROI;
import uk.ac.diamond.scisoft.analysis.rpc.flattening.IRootFlattener;

public class CircularROIHelper extends ROIHelper<CircularROI> {
	public static final String RAD = "rad";

	public CircularROIHelper() {
		super(CircularROI.class);
	}

	@Override
	public Map<String, Object> flatten(Object obj, IRootFlattener rootFlattener) {
		CircularROI roi = (CircularROI) obj;
		Map<String, Object> outMap = super.flatten(roi, CircularROI.class.getCanonicalName(), rootFlattener);
		outMap.put(RAD, rootFlattener.flatten(roi.getRadius()));
		return outMap;
	}

	@Override
	public CircularROI unflatten(Map<?, ?> inMap, IRootFlattener rootFlattener) {
		CircularROI roiOut = new CircularROI();
		roiOut.setName((String) rootFlattener.unflatten(inMap.get(ROIHelper.NAME)));
		roiOut.setPoint((double[]) rootFlattener.unflatten(inMap.get(ROIHelper.SPT)));
		roiOut.setPlot((Boolean) rootFlattener.unflatten(inMap.get(ROIHelper.PLOT)));
		roiOut.setRadius((Double) rootFlattener.unflatten(inMap.get(RAD)));

		return roiOut;
	}
}
