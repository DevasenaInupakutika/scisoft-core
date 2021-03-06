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

package uk.ac.diamond.scisoft.analysis.roi;

import java.io.Serializable;
import java.util.Arrays;


/**
 * Class for linear regions of interest 
 */
public class LinearROI extends ROIBase implements Serializable {
	private double len;    // length
	private double ang;    // angle in radians
	private boolean crossHair; // enable secondary linear ROI that bisects at 90 degrees
	private double cang, sang; // cosine and sine of angle

	/**
	 * Default new line
	 */
	public LinearROI() {
		this(1.);
	}

	/**
	 * @param len
	 */
	public LinearROI(double len) {
		this(len, 0.0);
	}

	/**
	 * @param len
	 * @param ang in radians
	 */
	public LinearROI(double len, double ang) {
		this.spt = new double[] {0, 0};
		this.len = len;
		this.ang = ang;
		calcTrig();
		crossHair = false;
	}

	/**
	 * New line from origin to point
	 * @param pt 
	 */
	public LinearROI(double[] pt) {
		this(new double[] {0, 0}, pt);
	}

	/**
	 * New line given by points
	 * @param spt
	 * @param ept
	 */
	public LinearROI(double[] spt, double[] ept) {
		this.spt = new double[] {spt[0], spt[1]};
		double x = ept[0] - spt[0];
		double y = ept[1] - spt[1];
		len = Math.hypot(x, y);
		ang = Math.atan2(y, x);
		if (ang < 0) ang += 2.0*Math.PI;
		calcTrig();
		crossHair = false;
	}

	/**
	 * Set start point whilst keeping end point
	 * @param pt
	 */
	public void setPointKeepEndPoint(int[] pt) {
		double[] ept = getEndPoint();
		spt[0] = pt[0];
		spt[1] = pt[1];
		double x = ept[0] - pt[0];
		double y = ept[1] - pt[1];
		len = Math.hypot(x, y);
		ang = Math.atan2(y, x);
		if (ang < 0) ang += 2.0*Math.PI;
		calcTrig();
	}

	/**
	 * @param f 
	 * @return point from normalized length along line
	 */
	public double[] getPoint(double f) {
		return new double[] { spt[0] + f*len*cang, 
				spt[1] + f*len*sang };
	}

	/**
	 * @return end point
	 */
	public double[] getEndPoint() {
		return getPoint(1);
	}

	/**
	 * @param f 
	 * @return point from normalized length along line
	 */
	public int[] getIntPoint(double f) {
		double[] pt = getPoint(f);
		return new int[] { (int) pt[0], (int) pt[1] };
	}

	/**
	 * @return end point
	 */
	public int[] getIntEndPoint() {
		double[] pt = getPoint(1);
		return new int[] { (int) pt[0], (int) pt[1] };
	}

	/**
	 * Change line to have specified end point
	 * @param eptx 
	 * @param epty 
	 */
	public void setEndPoint(double eptx, double epty) {
		double x = eptx - spt[0];
		double y = epty - spt[1];
		len = Math.hypot(x, y);
		ang = Math.atan2(y, x);
		if (ang < 0) ang += 2.0*Math.PI;
		calcTrig();
	}

	/**
	 * Change line to have specified end point
	 * @param ept
	 */
	public void setEndPoint(double[] ept) {
		setEndPoint(ept[0], ept[1]);
	}

	/**
	 * @param ept
	 */
	public void setEndPoint(int[] ept) {
		double x = ept[0] - spt[0];
		double y = ept[1] - spt[1];
		len = Math.hypot(x, y);
		ang = Math.atan2(y, x);
		if (ang < 0) ang += 2.0*Math.PI;
		calcTrig();
	}

	/**
	 * @return mid point
	 */
	public double[] getMidPoint() {
		return getPoint(0.5);
	}

	/**
	 * Change line to have specified mid point
	 * @param mpt
	 */
	public void setMidPoint(double[] mpt) {
		spt[0] = mpt[0] - 0.5*len*cang;
		spt[1] = mpt[1] - 0.5*len*sang;
	}

	/**
	 * Change line to have specified angle
	 * @param ang
	 */
	public void setAngle(double ang) {
		if (ang < 0) ang += 2.0*Math.PI;
		this.ang = ang;
		calcTrig();
	}

	/**
	 * @return angle
	 */
	public double getAngle() {
		return ang;
	}

	/**
	 * Change line to have specified angle
	 * @param ang
	 */
	public void setAngleDegrees(double ang) {
		if (ang < 0) ang += 360.0;
		this.ang = Math.toRadians(ang);
		calcTrig();
	}

	/**
	 * For Jython
	 * @param angle The angle in degrees to set
	 */
	public void setAngledegrees(double angle) {
		setAngleDegrees(angle);
	}

	private void calcTrig() {
		cang = Math.cos(ang);
		sang = Math.sin(ang);
	}

	/**
	 * @return angle
	 */
	public double getAngleDegrees() {
		return Math.toDegrees(ang);
	}

	/**
	 * Change line to have specified length
	 * @param len
	 */
	public void setLength(double len) {
		this.len = len;
	}

	/**
	 * @return length
	 */
	public double getLength() {
		return len;
	}

	/**
	 * @param pt
	 * @return angle as measured from midpoint to given point
	 */
	public double getAngleRelativeToMidPoint(int[] pt) {
		double[] mpt = getMidPoint();
		mpt[0] = pt[0] - mpt[0];
		mpt[1] = pt[1] - mpt[1];
		return Math.atan2(mpt[1], mpt[0]);
	}
	
	/**
	 * @return a new copy
	 */
	@Override
	public LinearROI copy() {
		LinearROI roi = new LinearROI();
		roi.setName(name);
		roi.setPoint(spt.clone());
		roi.setPlot(plot);
		roi.setAngle(ang);
		roi.setLength(len);
		roi.setCrossHair(crossHair);
		return roi;
	}

	/**
	 * Subtract an offset to starting point
	 * @param pt
	 */
	public void subPoint(int[] pt) {
		spt[0] -= pt[0];
		spt[1] -= pt[1];		
	}

	/**
	 * Move line along its length
	 * @param distance
	 */
	public void translateAlongLength(double distance) {
		spt[0] += distance*cang;
		spt[1] += distance*sang;
	}

	/**
	 * @param f
	 * @return point given by normalised length on perpendicular bisector
	 */
	public int[] getPerpendicularBisectorIntPoint(double f) {
		double[] mpt = getMidPoint();
		double offset = (f-0.5)*len;
		double bangle = ang + Math.PI * 0.5; // bisector angle

		return new int[] { (int) (mpt[0] + offset*Math.cos(bangle)),
				(int) (mpt[1] + offset*Math.sin(bangle)) };
	}

	/**
	 * @param f
	 * @return point given by normalised length on perpendicular bisector
	 */
	public double[] getPerpendicularBisectorPoint(double f) {
		double[] mpt = getMidPoint();
		double offset = (f-0.5)*len;
		double bangle = ang + Math.PI * 0.5; // bisector angle

		return new double[] { mpt[0] + offset*Math.cos(bangle),
				mpt[1] + offset*Math.sin(bangle) };
	}

	/**
	 * @param crossHair The crossHair to set.
	 */
	public void setCrossHair(boolean crossHair) {
		this.crossHair = crossHair;
	}

	/**
	 * @return Returns the crossHair.
	 */
	public boolean isCrossHair() {
		return crossHair;
	}

	@Override
	public String toString() {
		return super.toString() + String.format("point=%s, length=%g, angle=%g", Arrays.toString(spt), len, getAngleDegrees());
	}
}
