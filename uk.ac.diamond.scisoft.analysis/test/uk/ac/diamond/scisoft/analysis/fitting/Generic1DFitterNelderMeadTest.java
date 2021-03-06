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

package uk.ac.diamond.scisoft.analysis.fitting;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import uk.ac.diamond.scisoft.analysis.dataset.AbstractDataset;
import uk.ac.diamond.scisoft.analysis.dataset.DoubleDataset;
import uk.ac.diamond.scisoft.analysis.dataset.Maths;
import uk.ac.diamond.scisoft.analysis.dataset.Random;
import uk.ac.diamond.scisoft.analysis.fitting.functions.APeak;
import uk.ac.diamond.scisoft.analysis.fitting.functions.CompositeFunction;
import uk.ac.diamond.scisoft.analysis.fitting.functions.Gaussian;
import uk.ac.diamond.scisoft.analysis.fitting.functions.Lorentzian;
import uk.ac.diamond.scisoft.analysis.fitting.functions.PearsonVII;
import uk.ac.diamond.scisoft.analysis.fitting.functions.PseudoVoigt;
import uk.ac.diamond.scisoft.analysis.optimize.NelderMead;

public class Generic1DFitterNelderMeadTest {

	static final int dataRange = 550;
	static int[] defaultPeakPos = new int[] { 100, 200, 300, 400, 500, 150, 250, 350, 450 };
	static final int defaultFWHM = 100;
	static final int defaultArea = 50;
	static final double delta = 0.5;
	static final double lambda = 0.1;
	static final boolean backgroundDominated = true;
	static final boolean autoStopping = true;
	static final double threashold = 0.10;
	static final int numPeaks = -1;
	static final int smoothing = 5;
	static final DoubleDataset xAxis = (DoubleDataset) AbstractDataset.arange(0, dataRange, 1, AbstractDataset.FLOAT64);

	@Ignore("Test not finished and is failing. 9 Nov 11")
	@Test
	public void testPearsonVIIFitting() {
		int i = defaultPeakPos.length;
		int[] peakPos = new int[i];
		for (int j = 0; j < i; j++) {
			peakPos[j] = defaultPeakPos[j];
		}
		DoubleDataset testingPeaks = generatePearsonVII(i);
		try {
			FittingTest(peakPos, testingPeaks, new PearsonVII(1,1,1,1));
		} catch (Exception e) {
			System.out.println(e);
			fail("The number of generated peaks did not match the number of peaks found using NelderMead");
		}
	}

	@Test
	public void testGaussianFitting() {
		int i = defaultPeakPos.length;
		int[] peakPos = new int[i];
		for (int j = 0; j < i; j++) {
			peakPos[j] = defaultPeakPos[j];
		}
		DoubleDataset testingPeaks = generateGaussianPeaks(i);
		try {
			FittingTest(peakPos, testingPeaks, new Gaussian(1,1,1,1));
		} catch (IllegalArgumentException e) {
			System.out.println(e);
			fail("The number of generated peaks did not match the number of peaks found using NelderMead");
		}
		}

	@Test
	public void testPseudoVoigt() {
		int i = defaultPeakPos.length;
		int[] peakPos = new int[i];
		for (int j = 0; j < i; j++) {
			peakPos[j] = defaultPeakPos[j];
		}
		DoubleDataset testingPeaks = generatePseudoVoigt(i);	
		try {
			FittingTest(peakPos, testingPeaks,new PseudoVoigt(1, 1, 1, 1));
		} catch (Exception e) {
			System.out.println(e);
			fail("The number of generated peaks did not match the number of peaks found using NelderMead");
		}
	}

	@Ignore("Test not finished and is failing. 9 Nov 11")
	@Test
	public void testLorentzianFitting() {
		int i = defaultPeakPos.length;
		int[] peakPos = new int[i];
		for (int j = 0; j < i; j++) {
			peakPos[j] = defaultPeakPos[j];
		}
		DoubleDataset testingPeaks = generateLorentzianPeaks(i);
		try {
			FittingTest(peakPos, testingPeaks,new Lorentzian(1,1,1,1));
		} catch (Exception e) {
			System.out.println(e);
			fail("The number of generated peaks did not match the number of peaks found using NelderMead");
		}
	}
	
	private DoubleDataset generatePseudoVoigt(int numPeaks) {
		CompositeFunction function = new CompositeFunction();
		if (numPeaks > defaultPeakPos.length)
			numPeaks = defaultPeakPos.length;
		for (int i = 0; i < numPeaks; i++) {
			function.addFunction(new PseudoVoigt(defaultPeakPos[i] - 20, defaultPeakPos[i] + 20, defaultFWHM,
					defaultArea));
		}
		DoubleDataset data = function.makeDataset(xAxis);
		return (DoubleDataset) Maths.add(data, generateNoisePlusBackground());
	}

	private DoubleDataset generatePearsonVII(int numPeaks) {
		CompositeFunction function = new CompositeFunction();
		if (numPeaks > defaultPeakPos.length)
			numPeaks = defaultPeakPos.length;
		for (int i = 0; i < numPeaks; i++) {
			function.addFunction(new PearsonVII(defaultPeakPos[i] - 20, defaultPeakPos[i] + 20, defaultFWHM,
					defaultArea));
		}
		DoubleDataset data = function.makeDataset(xAxis);
		return (DoubleDataset) Maths.add(data, generateNoisePlusBackground());
	}

	private DoubleDataset generateLorentzianPeaks(int numPeaks) {
		CompositeFunction function = new CompositeFunction();
		if (numPeaks > defaultPeakPos.length)
			numPeaks = defaultPeakPos.length;
		for (int i = 0; i < numPeaks; i++) {
			function.addFunction(new Lorentzian(defaultPeakPos[i] - 20, defaultPeakPos[i] + 20, defaultArea,
					defaultFWHM/2));
		}
		DoubleDataset data = function.makeDataset(xAxis);
		return (DoubleDataset) Maths.add(data, generateNoisePlusBackground());
	}

	private DoubleDataset generateGaussianPeaks(int numPeaks) {
		CompositeFunction function = new CompositeFunction();
		if (numPeaks > defaultPeakPos.length)
			numPeaks = defaultPeakPos.length;
		for (int i = 0; i < numPeaks; i++) {
			function.addFunction(new Gaussian(defaultPeakPos[i] - 20, defaultPeakPos[i] + 20, defaultFWHM, defaultArea));
		}
		DoubleDataset data = function.makeDataset(xAxis);
		return (DoubleDataset) Maths.add(data, generateNoisePlusBackground());
	}
	
	private DoubleDataset generateNoisePlusBackground() {
		return generateBackground();
	}

	private DoubleDataset generateBackground() {
		CompositeFunction comp = new CompositeFunction();
		comp.addFunction(new Gaussian(-10, 10, dataRange / 4, dataRange / 2));
		return comp.makeDataset(DoubleDataset.arange(dataRange));
	}

	@SuppressWarnings("unused")
	private DoubleDataset addNoiseToDataSet(DoubleDataset data) {
		Random.seed(123568);
		DoubleDataset noise = (DoubleDataset) Random.poisson(lambda, dataRange).cast(AbstractDataset.FLOAT);
		noise = (DoubleDataset) Maths.multiply(noise, 0.01);
		return (DoubleDataset) Maths.add(data, noise);
	}
	
	private void FittingTest(int[] peakPos, DoubleDataset data, APeak peakFunction) {
		List<CompositeFunction> fittedPeakList = Generic1DFitter.fitPeakFunctions(xAxis, data, peakFunction, new NelderMead(0.00001),
				smoothing, numPeaks, threashold, autoStopping, backgroundDominated);
		double[] fittedPeakPos = new double[fittedPeakList.size()];
		int i = 0;
		for (CompositeFunction p : fittedPeakList) {
			fittedPeakPos[i++] = p.getPeak(0).getPosition();
		}
		Arrays.sort(fittedPeakPos);
		Arrays.sort(peakPos);

//		for (int j = 0; j < fittedPeakPos.length; j++) {
//			System.out.println("fitted peak pos "+fittedPeakPos[j]+" position of generated data "+peakPos[j]);
//		}
		
		assertEquals("The number of peaks found was not the same as generated", fittedPeakPos.length, peakPos.length);

		for (int k = 0; k < fittedPeakPos.length; k++) {
			assertEquals(peakPos[k], fittedPeakPos[k], delta);
		}
	}
}
