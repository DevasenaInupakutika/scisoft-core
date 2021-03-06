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

package uk.ac.diamond.scisoft.analysis.optimize;

import uk.ac.diamond.scisoft.analysis.dataset.AbstractDataset;
import uk.ac.diamond.scisoft.analysis.dataset.IDataset;
import uk.ac.diamond.scisoft.analysis.fitting.functions.IFunction;
import Jama.Matrix;

/**
 * Basic least squares optimiser, this currently works for polynomial
 * functions, and not really for more complex functions.
 */
public class LeastSquares implements IOptimizer {

	/**
	 * Setup the logging facilities
	 */
//	private static final Logger logger = LoggerFactory.getLogger(LeastSquares.class);

	/**
	 * Base constructor
	 * 
	 * @param tolerence
	 *            Not currently used.
	 */
	// TODO Add in the ability to use the tolerance parameter
	public LeastSquares(@SuppressWarnings("unused") double tolerence) {

	}

	private double chiSquared(double[] params, IDataset xAxis, IDataset yAxis,
			AbstractDataset sigma, IFunction function) {

		double result = 0.0;

		final double[] oldParams = function.getParameterValues();

		function.setParameterValues(params);

		for (int i = 0; i < xAxis.getSize(); i++) {
			final double x = (yAxis.getDouble(i) - function.val(xAxis.getDouble(i))) / sigma.getDouble(i);
			result += x*x;
		}

		function.setParameterValues(oldParams);

		return result;
	}

	private double alpha(int k, int l, IDataset xAxis, AbstractDataset sigma,
			IFunction func) {
		double sum = 0.0;
		final int n = xAxis.getShape()[0];
		for (int i = 0; i < n; i++) {
			double value = 1.0 / Math.pow(sigma.getDouble(i), 2);
			value *= func.partialDeriv(k, xAxis.getDouble(i));
			value *= func.partialDeriv(l, xAxis.getDouble(i));
			sum += value;
		}
		return sum;
	}

	private double alphaPrime(int k, int l, IDataset xAxis, AbstractDataset sigma,
			IFunction func, double lambda) {
		if (k == l) {
			return (alpha(k, l, xAxis, sigma, func)) * (1 + lambda);
		}

		return (alpha(k, l, xAxis, sigma, func));

	}

	private Matrix evaluateMatrix(IDataset xAxis, AbstractDataset sigma,
			IFunction func, double lambda) {
		int nparams = func.getNoOfParameters();
		Matrix mat = new Matrix(nparams, nparams);
		for (int i = 0; i < nparams; i++) {
			for (int j = 0; j < nparams; j++) {
				mat.set(i, j, alphaPrime(i, j, xAxis, sigma, func, lambda));
			}
		}
		return mat;
	}

	private final static double PERT = 1e-4;

	private double beta(int k, IDataset xAxis, IDataset yAxis, AbstractDataset sigma,
			IFunction func) {
		// do a numerical differential for the time being.
		final double[] params = func.getParameterValues();
		final double v = params[k];

		final double chimin;
		final double chimax;
		if (v != 0) {
			params[k] = v*(1-PERT);
			chimin = chiSquared(params, xAxis, yAxis, sigma, func);
			params[k] = v*(1+PERT);
			chimax = chiSquared(params, xAxis, yAxis, sigma, func);
			params[k] = v;
		} else {
			params[k] = -PERT;
			chimin = chiSquared(params, xAxis, yAxis, sigma, func);
			params[k] = PERT;
			chimax = chiSquared(params, xAxis, yAxis, sigma, func);
			params[k] = 0;
		}

		return -0.5 * (chimax - chimin) / (2 * PERT);
	}

	private Matrix evaluateChiSquared(IDataset xAxis, IDataset yAxis,
			AbstractDataset sigma, IFunction func) {
		int nparams = func.getNoOfParameters();
		Matrix mat = new Matrix(nparams, 1);
		for (int i = 0; i < nparams; i++) {
			mat.set(i, 0, beta(i, xAxis, yAxis, sigma, func));
		}
		return mat;
	}

	private double[] solveDa(IDataset xAxis, IDataset yAxis, AbstractDataset sigma,
			IFunction func, double lambda) {
		Matrix A = evaluateMatrix(xAxis, sigma, func, lambda);
		Matrix B = evaluateChiSquared(xAxis, yAxis, sigma, func);

		Matrix mat = A.inverse();
		mat = mat.times(B);
		double[] result = new double[func.getNoOfParameters()];
		for (int i = 0; i < result.length; i++) {
			result[i] = mat.get(i, 0);
		}

		return result;
	}

	@Override
	public void optimize(IDataset[] coords, IDataset yAxis, IFunction function) {

		// first set sigma, this is artificial at first but should be sorted
		// out once the basics work.
		AbstractDataset sigma = AbstractDataset.ones(coords[0].getShape(), AbstractDataset.FLOAT64);

		// get the parameters
		double[] params = function.getParameterValues();

		// first calculate the quality of the fit.
		double eval = chiSquared(params, coords[0], yAxis, sigma, function);

		// choose a value for lambda
		double lambda = 0.001;

		double[] testParams = new double[params.length];

		for (int j = 0; j < 1; j++) {

			// solve the least squares calculation
			double[] dParams = solveDa(coords[0], yAxis, sigma, function, lambda);

			// evaluate the new position
			for (int i = 0; i < params.length; i++) {
				testParams[i] = params[i] + dParams[i];
			}

			double testEval = chiSquared(testParams, coords[0], yAxis, sigma,
					function);

			//logger.debug(testEval + "," + eval + "," + lambda + "["
			//		+ dParams[0] + "," + dParams[1] + "," + dParams[2] + "]");

			if (testEval >= eval) {
				lambda *= 10;
			} else {
				lambda /= 10;
				eval = testEval;
				for (int i = 0; i < params.length; i++) {
					params[i] = testParams[i];
				}
			}

		}

		function.setParameterValues(params);

	}

}
