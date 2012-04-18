/*-
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

package uk.ac.diamond.scisoft.analysis.roi;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Class for a polygonal ROI (really a list of point ROIs)
 */
public class PolygonalROI extends ROIBase implements Serializable, Iterable<ROIBase> {
	private List<ROIBase> pts;

	public PolygonalROI() {
		this(new double[] {0,0});
	}

	public PolygonalROI(double[] start) {
		pts = new ArrayList<ROIBase>();
		spt = start;
		pts.add(this);
	}

	/**
	 * Add point to polygon
	 * @param point
	 */
	public void insertPoint(double[] point) {
		ROIBase r = new ROIBase();
		r.spt = point;
		pts.add(r);
	}

	/**
	 * Add point to polygon
	 * @param point
	 */
	public void insertPoint(int[] point) {
		insertPoint(new double[] { point[0], point[1] });
	}

	/**
	 * Insert point to polygon at index
	 * @param i index
	 * @param point
	 */
	public void insertPoint(int i, double[] point) {
		ROIBase r = new ROIBase();
		if (i == 0) { // copy current and then shift
			r.spt = spt; 
			pts.set(0, r);
			spt = point;
			pts.add(0, this);
		} else {
			r.spt = point;
			pts.add(i, r);
		}
	}

	/**
	 * @return number of sides
	 */
	public int getSides() {
		return pts.size();
	}

	/**
	 * @param i
	 * @return x value of i-th point
	 */
	public double getPointX(int i) {
		return pts.get(i).spt[0];
	}

	/**
	 * @param i
	 * @return y value of i-th point
	 */
	public double getPointY(int i) {
		return pts.get(i).spt[1];
	}

	/**
	 * @param i
	 * @return i-th point as ROI
	 */
	public ROIBase getPoint(int i) {
		return pts.get(i);
	}

	/**
	 * @return iterator over points
	 */
	@Override
	public Iterator<ROIBase> iterator() {
		return pts.iterator();
	}

	@Override
	public PolygonalROI copy() {
		PolygonalROI croi = new PolygonalROI(spt.clone());
		for (int i = 1, imax = pts.size(); i < imax; i++)
			croi.insertPoint(pts.get(i).spt.clone());

		return croi;
	}
}