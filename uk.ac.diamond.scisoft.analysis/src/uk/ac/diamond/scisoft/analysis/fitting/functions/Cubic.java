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

package uk.ac.diamond.scisoft.analysis.fitting.functions;


/**
 * Class that wrappers the equation <br>
 * y(x) = ax^3 + bx^2 + cx + d
 */
public class Cubic extends AFunction {
	private static String cname = "Cubic";
	private static String cdescription = "y(x) = ax^3 + bx^2 + cx + d";
	private static String[] paramNames = new String[]{"A", "B", "C", "D"};
	@SuppressWarnings("unused")
	private static double[] params = new double[]{0,0,0,0};
	/**
	 * Basic constructor, not advisable to use
	 */
	public Cubic() {
		super(4);
		name = cname;
		description = cdescription;
		for(int i =0; i<paramNames.length;i++)
			setParameterName(paramNames[i], i);
	}

	public Cubic(double[] params) {
		super(params);
		name = cname;
		description = cdescription;
		for(int i =0; i<paramNames.length;i++)
			setParameterName(paramNames[i], i);
	}

	public Cubic(IParameter[] params) {
		super(params);
		name = cname;
		description = cdescription;
		for(int i =0; i<paramNames.length;i++)
			setParameterName(paramNames[i], i);
	}

	/**
	 * Constructor that allows for the positioning of all the parameter bounds
	 * 
	 * @param minA
	 *            minimum boundary for the A parameter
	 * @param maxA
	 *            maximum boundary for the A parameter
	 * @param minB
	 *            minimum boundary for the B parameter
	 * @param maxB
	 *            maximum boundary for the B parameter
	 * @param minC
	 *            minimum boundary for the C parameter
	 * @param maxC
	 *            maximum boundary for the C parameter
	 * @param minD
	 *            minimum boundary for the D parameter
	 * @param maxD
	 *            maximum boundary for the D parameter
	 */
	public Cubic(double minA, double maxA, double minB, double maxB, double minC, double maxC, double minD, double maxD) {
		super(4);

		getParameter(0).setLimits(minA,maxA);
		getParameter(0).setValue((minA + maxA) / 2.0);

		getParameter(1).setLimits(minB,maxB);
		getParameter(1).setValue((minB + maxB) / 2.0);

		getParameter(2).setLimits(minC,maxC);
		getParameter(2).setValue((minC + maxC) / 2.0);

		getParameter(3).setLimits(minD,maxD);
		getParameter(3).setValue((minD + maxD) / 2.0);

		name = cname;
		description = cdescription;
		for(int i =0; i<paramNames.length;i++)
			setParameterName(paramNames[i], i);
	}

	double a, b, c, d;
	private void calcCachedParameters() {
		a = getParameterValue(0);
		b = getParameterValue(1);
		c = getParameterValue(2);
		d = getParameterValue(3);

		markParametersClean();
	}

	@Override
	public double val(double... values) {
		if (areParametersDirty())
			calcCachedParameters();

		double position = values[0];
		return a * position * position * position + b * position * position + c * position + d;
	}

	@Override
	public String toString() {
		final StringBuilder out = new StringBuilder();

		out.append(String.format("A Has Value %f within the bounds [%f,%f]\n", getParameterValue(0), 
				getParameter(0).getLowerLimit(), getParameter(0).getUpperLimit()));
		out.append(String.format("B Has Value %f within the bounds [%f,%f]\n", getParameterValue(1),
				getParameter(1).getLowerLimit(), getParameter(1).getUpperLimit()));
		out.append(String.format("C Has Value %f within the bounds [%f,%f]\n", getParameterValue(2),
				getParameter(2).getLowerLimit(), getParameter(2).getUpperLimit()));
		out.append(String.format("D Has Value %f within the bounds [%f,%f]", getParameterValue(3),
				getParameter(3).getLowerLimit(), getParameter(3).getUpperLimit()));
		return out.toString();
	}

	@Override
	public double partialDeriv(int parameter, double... position) {
		final double pos = position[0];
		switch (parameter) {
		case 0:
			return pos * pos * pos;
		case 1:
			return pos * pos;
		case 2:
			return pos;
		case 3:
			return 1.0;
		default:
			throw new IndexOutOfBoundsException("Parameter index is out of bounds");
		}
	}
}
