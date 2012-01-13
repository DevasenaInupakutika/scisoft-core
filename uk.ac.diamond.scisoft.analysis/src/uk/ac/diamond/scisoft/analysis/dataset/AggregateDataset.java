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

package uk.ac.diamond.scisoft.analysis.dataset;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import gda.analysis.io.ScanFileHolderException;
import uk.ac.diamond.scisoft.analysis.io.IMetaData;
import uk.ac.gda.monitor.IMonitor;

/**
 * Class to aggregate a set of lazy datasets and present them as a single lazy dataset where
 * the first position value accesses the aggregation
 */
public class AggregateDataset implements ILazyDataset {
	private ILazyDataset[] data = null; // array of lazy datasets
	private int[] map = null;    // map first dimension to index of dataset
	private int[] offset = null; // cumulative first dimension lengths used as slice offsets
	private int[] shape;
	private int size;
	private String name;
	private int dtype = -1;

	/**
	 * Create an aggregate dataset
	 * @param extend if true, extend rank by one
	 * @param datasets
	 */
	public AggregateDataset(boolean extend, ILazyDataset... datasets) {

		// make datasets have same rank
		int maxRank = -1;
		for (ILazyDataset d : datasets) {
			int r = d.getRank();
			if (r > maxRank)
				maxRank = r;
		}

		if (extend)
			maxRank++;

		data = new ILazyDataset[datasets.length];
		for (int j = 0; j < datasets.length; j++) {
			ILazyDataset d = datasets[j];
			int[] s = d.getShape();
			if (s.length < maxRank) {
				d = d.clone();
				int[] ns = new int[maxRank];
				int start = maxRank - s.length;

				for (int i = 0; i < start; i++) {
					ns[i] = 1;
				}
				for (int i = 0; i < s.length; i++) {
					ns[i+start] = s[i];
				}

				d.setShape(ns);
			}
			data[j] = d;
		}

		// check for same (sub-)shape
		int[] s = data[0].getShape();
		int axis = extend ? -1 : 0;
		for (ILazyDataset d : data) {
			if (!AbstractDataset.areShapesCompatible(s, d.getShape(), axis))
				throw new IllegalArgumentException("Dataset '" + d.getName() + "' has wrong shape");
		}

		// calculate new shape
		shape = new int[maxRank];
		for (int i = 1; i < shape.length; i++) {
			shape[i] = s[i];
		}
		if (extend) {
			shape[0] = data.length;
		} else {
			for (ILazyDataset d : data) {
				shape[0] += d.getShape()[0];
			}
		}
		size = AbstractDataset.calcSize(shape);

		// work out offsets from cumulative lengths
		offset = new int[data.length];
		int cd = 0;
		for (int i = 0; i < data.length; i++) {
			offset[i] = cd;
			cd += data[i].getShape()[0];
		}

		// calculate mapping from aggregate dimension to dataset array index
		map = new int[shape[0]];
		int k = 0;
		for (int i = 0; i < data.length; i++) {
			int jmax = data[i].getShape()[0];
			for (int j = 0; j < jmax; j++)
				map[k++] = i;
		}

		for (ILazyDataset d : data) {
			if (d instanceof LazyDataset) {
				dtype = AbstractDataset.getBestDType(dtype, ((LazyDataset) d).getDtype());
			} else {
				dtype = AbstractDataset.getBestDType(dtype, AbstractDataset.getDTypeFromClass(d.elementClass()));
			}
		}
	}

	@Override
	public Class<?> elementClass() {
		return AbstractDataset.elementClass(getDtype());
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int getSize() {
		return size;
	}

	@Override
	public int[] getShape() {
		return shape;
	}

	@Override
	public void setShape(int... shape) {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public int getRank() {
		return shape.length;
	}

	@Override
	public ILazyDataset squeeze() {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public ILazyDataset squeeze(boolean onlyFromEnd) {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public IDataset getSlice(int[] start, int[] stop, int[] step) {
		try {
			return getSlice(null, start, stop, step);
		} catch (ScanFileHolderException e) {
			return null;
		}
	}

	/**
	 * @return type of dataset item
	 */
	public int getDtype() {
		return dtype;
	}

	@Override
	public IDataset getSlice(IMonitor monitor, int[] start, int[] stop, int[] step) throws ScanFileHolderException {
		if (start == null) {
			start = new int[shape.length];
		}
		if (stop == null) {
			stop = shape.clone();
		}
		if (step == null) {
			step = new int[shape.length];
			Arrays.fill(step, 1);
		}

		// convert first dimension's slice to individual slices per stored dataset
		int fb = start[0];
		int fe = stop[0];
		int fs = step[0];

		List<AbstractDataset> sliced = new ArrayList<AbstractDataset>();
		int op = fb;
		int p = op;
		ILazyDataset od = data[map[op]];
		ILazyDataset nd; 
		while (p < fe) {
			nd = data[map[p]];
			if (nd != od) {
				start[0] = op - offset[map[op]];
				stop[0] = p - offset[map[op]];
				AbstractDataset a = DatasetUtils.convertToAbstractDataset(od.getSlice(monitor, start, stop, step));
				a = a.cast(dtype);
				sliced.add(a);

				od = nd;
				op = p;
			}
			p += fs;
		}
		start[0] = op - offset[map[op]];
		stop[0] = p - offset[map[op]];
		sliced.add(DatasetUtils.convertToAbstractDataset(od.getSlice(monitor, start, stop, step)));

		return DatasetUtils.concatenate(sliced.toArray(new AbstractDataset[0]), 0);
	}

	@Override
	public IDataset getSlice(Slice... slice) {
		final int rank = shape.length;
		final int[] start = new int[rank];
		final int[] stop = new int[rank];
		final int[] step = new int[rank];
		Slice.convertFromSlice(slice, shape, start, stop, step);
		return getSlice(start, stop, step);
	}

	@Override
	public IDataset getSlice(IMonitor monitor, Slice... slice) throws ScanFileHolderException {
		final int rank = shape.length;
		final int[] start = new int[rank];
		final int[] stop = new int[rank];
		final int[] step = new int[rank];
		Slice.convertFromSlice(slice, shape, start, stop, step);
		return getSlice(monitor, start, stop, step);
	}

	@Override
	public IMetaData getMetadata() throws Exception {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public void setMetadata(IMetaData metadata) {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public AggregateDataset clone() {
		throw new UnsupportedOperationException("Not implemented");
	}
}