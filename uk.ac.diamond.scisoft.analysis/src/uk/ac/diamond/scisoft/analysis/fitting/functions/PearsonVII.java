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

import org.apache.commons.math3.special.Gamma;


/**
 * Class which expands on the AFunction class to give the properties of a pearsonVII. A 1D implementation
 * function derived from Gozzo, F. (2004). 
 * First experiments at the Swiss Light Source Materials Science beamline powder diffractometer.
 * Journal of Alloys and Compounds, 362(1-2), 206-217. doi:10.1016/S0925-8388(03)00585-1
 */
public class PearsonVII extends APeak implements IPeak {
	private static String cname = "PearsonVII";
	private static String[] paramNames = new String[]{"Min Peak Position", "Max Peak Position", "Max FWHM", "Max Area"};
	private static String cdescription = "y(x) = PearsonVII distribution";
	private static double[] params = new double[]{0,0,0,0};

	public PearsonVII(){
		this(params);
	}
	/**
	 * Constructor which takes the three properties required, which are
	 * 
	 * <pre>
	 *    position
	 *    FWHM
	 *    mixing
	 *    Area
	 * </pre>
	 * 
	 * @param params
	 */
	public PearsonVII(double[] params) {
		super(params);
		name = cname;
		description = cdescription;
		for(int i =0; i<paramNames.length;i++)
			setParameterName(paramNames[i], i);
	}

	public PearsonVII(IParameter[] params) {
		super(params);
		name = cname;
		description = cdescription;
		for(int i =0; i<paramNames.length;i++)
			setParameterName(paramNames[i], i);
	}

	double defaultMixing = 2;
	public PearsonVII(IdentifiedPeak peakParameters){
		super(4); 
		
		//pos
		double range = peakParameters.getMaxXVal()-peakParameters.getMinXVal();
		getParameter(0).setValue(peakParameters.getPos());
		getParameter(0).setLowerLimit(peakParameters.getMinXVal());//-range);
		getParameter(0).setUpperLimit(peakParameters.getMaxXVal());//+range);
		
		//fwhm
		getParameter(1).setLowerLimit(0);
		getParameter(1).setUpperLimit(range*2);
		getParameter(1).setValue(peakParameters.getFWHM()/2);
		
		//mixing		
		getParameter(2).setValue(defaultMixing);
		getParameter(2).setLowerLimit(1.0);
		getParameter(2).setUpperLimit(10.0);

		//area
		// better fitting is generally found if sigma expands into the peak.
		getParameter(3).setLowerLimit(0);
		getParameter(3).setUpperLimit((peakParameters.getHeight()*2)*(range*2));
		getParameter(3).setValue(peakParameters.getArea()/2);

		name = cname;
		description = cdescription;
		for(int i =0; i<paramNames.length;i++)
			setParameterName(paramNames[i], i);
	}

	/**
	 * Constructor which takes more sensible values for the parameters, which also incorporates the limits which they
	 * can be in, reducing the overall complexity of the problem
	 * 
	 * @param minPeakPosition
	 *            The minimum value the peak position of the Pearson VII
	 * @param maxPeakPosition
	 *            The maximum value of the peak position
	 * @param maxFWHM
	 *            The maximum full width half maximum
	 * @param maxArea
	 *            The maximum area under the PDF 
	 *            
	 * There is also a mixing parameter for the Pearson VII distrubution. This parameter defines the mixture between 
	 * Gaussian and Lorentzian function. Where m = 1 the function is Lorentzian and m = infinity the function is Gaussian. 
	 * With this constructor the mixing parameter is set to 10 with the lower limit set to 1 and the Upper 
	 * limit set to Double.MAX_VALUE.
	 */
	public PearsonVII(double minPeakPosition, double maxPeakPosition, double maxFWHM, double maxArea) {
		super(4);

		getParameter(0).setValue(minPeakPosition + ((maxPeakPosition - minPeakPosition) / 2.0));
		getParameter(0).setLowerLimit(minPeakPosition);
		getParameter(0).setUpperLimit(maxPeakPosition);

		getParameter(1).setLowerLimit(0);
		getParameter(1).setUpperLimit(maxFWHM);
		getParameter(1).setValue(maxFWHM / 2);

		getParameter(2).setValue(defaultMixing);
		getParameter(2).setLowerLimit(1.0);
		getParameter(2).setUpperLimit(10.0);

		getParameter(3).setLowerLimit(0.0);
		getParameter(3).setUpperLimit(maxArea);
		getParameter(3).setValue(maxArea / 10);

		name = cname;
		description = cdescription;
		for(int i =0; i<paramNames.length;i++)
			setParameterName(paramNames[i], i);
	}
	
	public PearsonVII(double minPeakPosition, double maxPeakPosition, double maxFWHM, double maxArea, double mixing) {
		super(4);

		getParameter(0).setLowerLimit(minPeakPosition);
		getParameter(0).setUpperLimit(maxPeakPosition);
		getParameter(0).setValue(minPeakPosition + ((maxPeakPosition - minPeakPosition) / 2.0));

		getParameter(1).setLowerLimit(0);
		getParameter(1).setUpperLimit(maxFWHM);
		getParameter(1).setValue(maxFWHM / 10);

		getParameter(2).setLowerLimit(1.0);
		getParameter(2).setUpperLimit(10.0);
		getParameter(2).setValue(mixing);

		getParameter(3).setLowerLimit(-maxArea);
		getParameter(3).setUpperLimit(maxArea);
		getParameter(3).setValue(maxArea / 10);

		name = cname;
		description = cdescription;
		for(int i =0; i<paramNames.length;i++)
			setParameterName(paramNames[i], i);
	}	

	public PearsonVII createPeakFunction(double minPosition, double maxPosition, double maxArea, double maxFWHM) {
		return new PearsonVII(minPosition,maxPosition,maxArea,maxFWHM);
	}
	
	double mean, FWHM, mixing, area, c2_fwhm, c3;

	private void calcCachedParameters() {

		mean = getParameterValue(0);
		FWHM = getParameterValue(1);
		mixing = getParameterValue(2);
		area = getParameterValue(3);
		
		
		double c2top = 2.0* Math.pow(Math.E, Gamma.logGamma(mixing)) * Math.sqrt(Math.pow(2.0, 1.0/mixing)-1);
		double c2bottom =  Math.pow(Math.E, Gamma.logGamma(mixing-0.5))* Math.sqrt(Math.PI);
		
		double c2 = c2top/c2bottom;
		
		c2_fwhm = c2/FWHM;		
		
		c3 = 4.0* Math.sqrt(Math.pow(2.0, 1.0/mixing)-1.0);
		
//		
		
		
//		alpha = Math.sqrt(Math.pow(2, 1 / mixing) - 1);
//		double a = (2 * Math.pow(Math.E ,Math.pow(Math.E,Gamma.logGamma(mixing)) )* alpha;
//		double b = Math.pow(Math.E , Gamma.logGamma(mixing - 0.5)) * Math.sqrt(Math.PI);
//	
//		staticComponent = (a / b) / FWHM;
		
		markParametersClean();
	}

	@Override
	public double val(double... values) {
		if (areParametersDirty())
			calcCachedParameters();

		double position = values[0];
		
		double a = c3*Math.pow((position - mean),2) / Math.pow(FWHM,2);
		
		double result = c2_fwhm * Math.pow((1.0 + a), -mixing);
		
		//double result = area * staticComponent
		//		* Math.pow((1 + (4*alpha * ((position - mean) * (position - mean))) / FWHM*FWHM), -mixing);
		return result * area;
	}

	@Override
	public String toString() {
		final StringBuilder out = new StringBuilder();
		out.append(String.format("Pearson VII position has value %f within the bounds [%f,%f]\n", getParameterValue(0),
				getParameter(0).getLowerLimit(), getParameter(0).getUpperLimit()));
		out.append(String.format("Pearson VII standard deviation has value %f within the bounds [%f,%f]\n",
				getParameterValue(1), getParameter(1).getLowerLimit(), getParameter(1).getUpperLimit()));
		out.append(String.format("Pearson VII kurtosis has value %f within the bounds [%f,%f]", getParameterValue(2),
				getParameter(2).getLowerLimit(), getParameter(2).getUpperLimit()));
		return out.toString();
	}

	@Override
	public double getArea() {
		return getParameter(3).getValue();
	}

	@Override
	public double getFWHM() {
		return getParameter(1).getValue();
	}

	@Override
	public double getPosition() {
		return getParameter(0).getValue();
	}

}
