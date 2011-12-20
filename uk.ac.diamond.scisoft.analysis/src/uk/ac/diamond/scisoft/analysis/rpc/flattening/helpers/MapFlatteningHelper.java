/*
 * Copyright © 2011 Diamond Light Source Ltd.
 * Contact :  ScientificSoftware@diamond.ac.uk
 * 
 * This is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License version 3 as published by the Free
 * Software Foundation.
 * 
 * This software is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this software. If not, see <http://www.gnu.org/licenses/>.
 */

package uk.ac.diamond.scisoft.analysis.rpc.flattening.helpers;

import java.util.HashMap;
import java.util.Map;

import uk.ac.diamond.scisoft.analysis.rpc.flattening.IFlattener;
import uk.ac.diamond.scisoft.analysis.rpc.flattening.IRootFlattener;

abstract public class MapFlatteningHelper<T> extends FlatteningHelper<T> {
	protected static final String CONTENT = "content";

	public MapFlatteningHelper(Class<T> type) {
		super(type);
	}

	public abstract T unflatten(Map<?, ?> thisMap, IRootFlattener rootFlattener);

	public Map<String, Object> createMap(String typeName) {
		Map<String, Object> outMap = new HashMap<String, Object>();
		outMap.put(IFlattener.TYPE_KEY, typeName);
		return outMap;
	}

	@Override
	public T unflatten(Object obj, IRootFlattener rootFlattener) {
		return unflatten((Map<?, ?>) obj, rootFlattener);
	}

	@Override
	public boolean canUnFlatten(Object obj) {
		if (obj instanceof Map<?, ?>) {
			Map<?, ?> thisMap = (Map<?, ?>) obj;
			return getTypeCanonicalName().equals(thisMap.get(IFlattener.TYPE_KEY));
		}

		return false;
	}
}
