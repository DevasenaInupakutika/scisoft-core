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

import java.io.Serializable;
import java.util.Arrays;

import org.apache.commons.math.ConvergenceException;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.MultivariateMatrixFunction;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.solvers.BrentSolver;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math.optimization.VectorialPointValuePair;
import org.apache.commons.math.optimization.general.LevenbergMarquardtOptimizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.diamond.scisoft.analysis.dataset.AbstractDataset;
import uk.ac.diamond.scisoft.analysis.dataset.DoubleDataset;
import uk.ac.diamond.scisoft.analysis.dataset.IndexIterator;
import uk.ac.diamond.scisoft.analysis.dataset.LinearAlgebra;
import uk.ac.diamond.scisoft.analysis.dataset.Maths;
import Jama.Matrix;

/**
 * This class represents the derivative with respect to angle of the geometric
 * distance function from a given point to an ellipse. It is used by an optimizer
 * to find the closest point on an ellipse to a given point.
 * <p>
 * The ellipse is centred on the origin.
 */
//Fix to http://jira.diamond.ac.uk/browse/DAWNSCI-549, add Serializable
class AngleDerivativeFunction implements UnivariateFunction, Serializable {
	double ra, rb; // major and minor semi-axes
	double alpha;  // orientation angle of major axis
	double A, B, C;

	/**
	 * Set major and minor semi-axes of ellipse
	 */
	public void setRadii(final double a, final double b) {
		ra = a;
		rb = b;
		A = ra*ra - rb*rb;
	}

	/**
	 * Set angle of major axis of ellipse
	 * @param angle
	 */
	public void setAngle(final double angle) {
		alpha = angle;
	}

	/**
	 * Set coordinates of given point
	 * @param Xc
	 * @param Yc
	 */
	public void setCoordinate(final double Xc, final double Yc) {
		final double c = Math.cos(alpha);
		final double s = Math.sin(alpha);
		B = -ra*(Xc*c + Yc*s);
		C = -rb*(Xc*s - Yc*c);
	}

	/**
	 * @return value proportional to the derivative of the distance function wrt. angle
	 */
	@Override
	public double value(final double angle) {
		final double c = Math.cos(angle);
		final double s = Math.sin(angle);

		return (A*c + B)*s + C*c; // -ve and 1/2 value of squared distance derivative
	}
}

/**
 * This function returns the coordinates (interleaved) for the points specified by the
 * geometric parameters and an array of angles
 */
//Fix to http://jira.diamond.ac.uk/browse/DAWNSCI-549, add Serializable
class EllipseCoordinatesFunction implements IConicSectionFitFunction, Serializable {
	private static final int PARAMETERS = EllipseFitter.PARAMETERS;
	private AbstractDataset X;
	private AbstractDataset Y;
	private DoubleDataset v;
	private double[][] j;
	private int n; // number of points
	private int m; // number of equations
	private double[] ca;
	private double[] sa;

	AngleDerivativeFunction angleDerivative = new AngleDerivativeFunction();
	BrentSolver solver = new BrentSolver();

	public EllipseCoordinatesFunction(AbstractDataset x, AbstractDataset y) {
		setPoints(x, y);
	}

//	public UnivariateRealFunction getAngleDerivative() {
//		return angleDerivative;
//	}

	@Override
	public void setPoints(AbstractDataset x, AbstractDataset y) {
		X = x;
		Y = y;
		n = X.getSize();
		m = 2*n;
		v = new DoubleDataset(m);
		j = new double[m][PARAMETERS+n];
		for (int i = 0; i < m; i++) {
			j[i][3] = 1;
			i++;
			j[i][4] = 1;
		}
		ca = new double[n];
		sa = new double[n];
	}

	@Override
	public double[] getTarget() {
		double[] target = new double[m];
		final IndexIterator itx = X.getIterator();
		final IndexIterator ity = Y.getIterator();
		int i = 0;
		while (itx.hasNext() && ity.hasNext()) {
			target[i++] = X.getElementDoubleAbs(itx.index);
			target[i++] = Y.getElementDoubleAbs(ity.index);
		}
		return target;
	}

	@Override
	public double[] getWeight() {
		double[] weight = new double[m];
		Arrays.fill(weight, 1.0);
		return weight;
	}

	/**
	 * Calculate angles of closest points on ellipse to targets
	 * @param initParameters geometric parameters
	 * @return array of all initial parameters
	 */
	double[] calcAllInitValues(double[] initParameters) {
		double[] init = new double[n+PARAMETERS];
		for (int i = 0; i < initParameters.length; i++) {
			init[i] = initParameters[i];
		}
		final double a = initParameters[0];
		final double b = initParameters[1];
		final double alpha = initParameters[2];
		final double x = initParameters[3];
		final double y = initParameters[4];
		final double twopi = 2*Math.PI;
		angleDerivative.setRadii(a, b);
		angleDerivative.setAngle(alpha);

		// work out the angle values for the closest points on ellipse
		final IndexIterator itx = X.getIterator();
		final IndexIterator ity = Y.getIterator();
		int i = PARAMETERS;
		while (itx.hasNext() && ity.hasNext()) {
			final double Xc = X.getElementDoubleAbs(itx.index) - x;
			final double Yc = Y.getElementDoubleAbs(ity.index) - y;

			angleDerivative.setCoordinate(Xc, Yc);
			try {
				// find quadrant to use
				double pa = Math.atan2(Yc, Xc);
				if (pa < 0)
					pa += twopi;
				pa -= alpha;
				final double end;
				final double halfpi = 0.5*Math.PI;
				pa /= halfpi;
				end = Math.ceil(pa)*halfpi;
				final double angle = solver.solve(100, angleDerivative, end-halfpi, end);
				init[i++] = angle;
			} catch (TooManyEvaluationsException e) {
				throw new IllegalArgumentException("Problem with solver converging as iterations exceed limit");
			} catch (MathIllegalArgumentException e) {
				// cannot happen
			}
		}
		return init;
	}

	@Override
	public double[] value(double[] p) throws FunctionEvaluationException, IllegalArgumentException {
		final double[] values = v.getData();
		final double a = p[0];
		final double b = p[1];
		final double cosa = Math.cos(p[2]);
		final double sina = Math.sin(p[2]);
		final double x = p[3];
		final double y = p[4];

		for (int i = 0; i < n; i++) {
			final double t = p[i+PARAMETERS];
			final double cost = Math.cos(t);
			final double sint = Math.sin(t);
			ca[i] = cost;
			sa[i] = sint;
			values[2*i] = x + a*cosa*cost - b*sina*sint;
			values[2*i+1] = y + a*sina*cost + b*cosa*sint;
		}

		return values;
	}

	@Override
	public AbstractDataset calcDistanceSquared(double[] parameters) throws IllegalArgumentException {
		final double[] p = calcAllInitValues(parameters);

		final DoubleDataset v = new DoubleDataset(n);
		final double[] values = v.getData();
		final double a = p[0];
		final double b = p[1];
		final double cosa = Math.cos(p[2]);
		final double sina = Math.sin(p[2]);
		final double x = p[3];
		final double y = p[4];

		for (int i = 0; i < n; i++) {
			final double t = p[i+PARAMETERS];
			final double cost = Math.cos(t);
			final double sint = Math.sin(t);
			double px = x + a*cosa*cost - b*sina*sint - X.getElementDoubleAbs(i);
			double py = y + a*sina*cost + b*cosa*sint - Y.getElementDoubleAbs(i);
			values[i] = px*px + py*py;
		}

		return v;
	}

	private void calculateJacobian(double[] p) {
		final double ta = 2*p[0];
		final double aa = p[0]*p[0];
		final double tb = 2*p[1];
		final double bb = p[1]*p[1];
		final double cosa = Math.cos(p[2]);
		final double sina = Math.sin(p[2]);

		for (int i = 0; i < n; i++) {
			final double cost = ca[i];
			final double sint = sa[i];
			final double cc = cosa*cost;
			final double cs = cosa*sint;
			final double sc = sina*cost;
			final double ss = sina*sint;

			final int ti = 2*i;
			final int tj = 2*i + 1;
			j[ti][0] = ta*cc;
			j[ti][1] = -tb*ss;
			j[ti][2] = -aa*sc - bb*cs;
			j[ti][PARAMETERS+i] = -aa*cs - bb*sc;

			j[tj][0] = ta*sc;
			j[tj][1] = tb*cs;
			j[tj][2] = aa*cc - bb*ss;
			j[tj][PARAMETERS+i] = -aa*ss + bb*cc;
		}
	}

	@Override
	public MultivariateMatrixFunction jacobian() {
		return new MultivariateMatrixFunction() {
			@Override
			public double[][] value(double[] p) throws FunctionEvaluationException, IllegalArgumentException {
				calculateJacobian(p);
				return j;
			}
		};
	}
}

/**
 * Fit an ellipse whose geometric parameters are
 *  major, minor semi-axes, angle of major axis, centre coordinates
 */
//Fix to http://jira.diamond.ac.uk/browse/DAWNSCI-549, add Serializable
public class EllipseFitter implements IConicSectionFitter, Serializable {
	/**
	 * Setup the logging facilities
	 */
	private static final Logger logger = LoggerFactory.getLogger(EllipseFitter.class);

	private double[] parameters;
	private double residual;

	private EllipseCoordinatesFunction fitFunction;

	final static int PARAMETERS = 5;

	public EllipseFitter() {
		parameters = new double[PARAMETERS];
	}

	@Override
	public double[] getParameters() {
		return parameters;
	}

	@Override
	public IConicSectionFitFunction getFitFunction(AbstractDataset x, AbstractDataset y) {
		if (fitFunction == null) {
			if (x == null || y == null)
				throw new IllegalArgumentException("Fitter uninitialized so coordinate datasets are needed");
			fitFunction = new EllipseCoordinatesFunction(x, y);
		} else if (x != null && y != null) {
			fitFunction.setPoints(x, y);
		}
		return fitFunction;
	}

	@Override
	public double getRMS() {
		return residual;
	}

	@Override
	public void geometricFit(AbstractDataset x, AbstractDataset y, double[] init) {
		residual = Double.NaN;
		if (x.getSize() < PARAMETERS || y.getSize() < PARAMETERS) {
			throw new IllegalArgumentException("Need " + PARAMETERS + " or more points");
		}

		if (init == null)
			init = quickfit(x, y);
		else if (init.length < PARAMETERS)
			throw new IllegalArgumentException("Need " + PARAMETERS + " parameters");

		EllipseCoordinatesFunction f = (EllipseCoordinatesFunction) getFitFunction(x, y);
		LevenbergMarquardtOptimizer opt = new LevenbergMarquardtOptimizer();

		try {
			VectorialPointValuePair result = opt.optimize(f, f.getTarget(), f.getWeight(), f.calcAllInitValues(init));

			double[] point = result.getPointRef(); 
			for (int i = 0; i < PARAMETERS; i++)
				parameters[i] = point[i];

			residual = opt.getRMS();

			logger.trace("Ellipse fit: rms = {}, x^2 = {}", residual, opt.getChiSquare());
		} catch (FunctionEvaluationException e) {
			// cannot happen
		} catch (IllegalArgumentException e) {
			// should not happen!
		} catch (ConvergenceException e) {
			throw new IllegalArgumentException("Problem with optimizer converging");
		}
	}

	@Override
	public void algebraicFit(AbstractDataset x, AbstractDataset y) {
		residual = Double.NaN;
		if (x.getSize() < PARAMETERS || y.getSize() < PARAMETERS) {
			throw new IllegalArgumentException("Need " + PARAMETERS + " or more points");
		}

		double[] quick = quickfit(x, y);
		IConicSectionFitFunction f = getFitFunction(x, y);
		residual = Math.sqrt((Double) f.calcDistanceSquared(quick).mean());

		for (int i = 0; i < PARAMETERS; i++)
			parameters[i] = quick[i];
	}

	/**
	 * least-squares using algebraic cost function
	 * <p>
	 * This uses the method in "Numerically stable direct least squares fitting of ellipses"
	 * by R. Halir and J. Flusser, Proceedings of the 6th International Conference in Central Europe
	 * on Computer Graphics and Visualization. WSCG '98. CZ, Pilsen 1998, pp 125-132. 
	 * <p>
	 * @param x
	 * @param y
	 * @return geometric parameters
	 */
	private static double[] quickfit(AbstractDataset x, AbstractDataset y) {
		final AbstractDataset xx = Maths.square(x);
		final AbstractDataset yy = Maths.square(y);
		final AbstractDataset xxx = Maths.multiply(xx, x);
		final AbstractDataset yyy = Maths.multiply(yy, y);
		final AbstractDataset xy = Maths.multiply(x, y);

		Matrix S1 = new Matrix(3, 3);
		S1.set(0, 0, LinearAlgebra.dotProduct(xx, xx).getDouble());
		S1.set(0, 1, LinearAlgebra.dotProduct(xxx, y).getDouble());
		S1.set(0, 2, LinearAlgebra.dotProduct(xx, yy).getDouble());

		S1.set(1, 0, S1.get(0, 1));
		S1.set(1, 1, S1.get(0, 2));
		S1.set(1, 2, LinearAlgebra.dotProduct(x, yyy).getDouble());

		S1.set(2, 0, S1.get(0, 2));
		S1.set(2, 1, S1.get(1, 2));
		S1.set(2, 2, LinearAlgebra.dotProduct(yy, yy).getDouble());

		Matrix S2 = new Matrix(3, 3);
		S2.set(0, 0, ((Number) xxx.sum()).doubleValue());
		S2.set(0, 1, LinearAlgebra.dotProduct(xx, y).getDouble());
		S2.set(0, 2, ((Number) xx.sum()).doubleValue());

		S2.set(1, 0, S2.get(0, 1));
		S2.set(1, 1, LinearAlgebra.dotProduct(x, yy).getDouble());
		S2.set(1, 2, ((Number) xy.sum()).doubleValue());

		S2.set(2, 0, S2.get(1, 1));
		S2.set(2, 1, ((Number) yyy.sum()).doubleValue());
		S2.set(2, 2, ((Number) yy.sum()).doubleValue());
		
		Matrix S3 = new Matrix(3, 3);
		S3.set(0, 0, S2.get(0, 2));
		S3.set(0, 1, S2.get(1, 2));
		S3.set(0, 2, ((Number) x.sum()).doubleValue());

		S3.set(1, 0, S3.get(0, 1));
		S3.set(1, 1, S2.get(2, 2));
		S3.set(1, 2, ((Number) y.sum()).doubleValue());

		S3.set(2, 0, S3.get(0, 2));
		S3.set(2, 1, S3.get(1, 2));
		S3.set(2, 2, x.getSize());

		Matrix T = S3.solve(S2.transpose()).uminus();

		Matrix M = S1.plus(S2.times(T));

		Matrix Cinv = new Matrix(new double[] {0,0,0.5,0,-1.0,0,0.5,0,0}, 3);
		Matrix Mp = Cinv.times(M);

//		System.err.println("M " + Arrays.toString(Mp.getRowPackedCopy()));
		Matrix V = Mp.eig().getV();
//		System.err.println("V " + Arrays.toString(V.getRowPackedCopy()));
		double[][] mv = V.getArray();
		ArrayRealVector v1 = new ArrayRealVector(mv[0]);
		ArrayRealVector v2 = new ArrayRealVector(mv[1]);
		ArrayRealVector v3 = new ArrayRealVector(mv[2]);

		v1.mapMultiplyToSelf(4);

		ArrayRealVector v = v1.ebeMultiply(v3).subtract(v2.ebeMultiply(v2));
		double[] varray = v.getDataRef();
		int i = 0;
		for (; i < 3; i++) {
			if (varray[i] > 0)
				break;
		}
		if (i == 3) {
			throw new IllegalArgumentException("Could not find solution that satifies constraint");
		}

		v = new ArrayRealVector(new double[] {mv[0][i], mv[1][i], mv[2][i]});
		varray = v.getDataRef();
		final double ca = varray[0];
		final double cb = varray[1];
		final double cc = varray[2];
		Array2DRowRealMatrix mt = new Array2DRowRealMatrix(T.getArray(), false);
		varray = mt.operate(varray);
		final double cd = varray[0];
		final double ce = varray[1];
		final double cf = varray[2];

//		System.err.println(String.format("Algebraic: %g, %g, %g, %g, %g, %g", ca, cb, cc, cd, ce, cf));
		final double disc = cb*cb - 4.*ca*cc;
		if (disc >= 0) {
			throw new IllegalArgumentException("Solution is not an ellipse");
		}

		if (cb == 0) {
			throw new IllegalArgumentException("Solution is a circle");
		}

		double[] qparameters = new double[PARAMETERS];
		qparameters[3] = (2.*cc*cd - cb*ce)/disc;
		qparameters[4] = (2.*ca*ce - cb*cd)/disc;

		final double sqrt = Math.sqrt((ca-cc)*(ca-cc) + cb*cb);
		qparameters[0] = -2.*(ca*ce*ce + cc*cd*cd + cf*cb*cb - cb*cd*ce - 4.*ca*cc*cf)/disc;
		qparameters[1] = qparameters[0]/(ca + cc + sqrt);
		qparameters[0] /= (ca + cc - sqrt);
		qparameters[0] = Math.sqrt(qparameters[0]);
		qparameters[1] = Math.sqrt(qparameters[1]);
		if (cb == 0) {
			qparameters[2] = 0.;
		} else {
			qparameters[2] = 0.5*Math.atan2(cb, ca - cc);
		}
		if (qparameters[0] < qparameters[1]) {
			final double t = qparameters[0];
			qparameters[0] = qparameters[1];
			qparameters[1] = t;
		} else {
			qparameters[2] += Math.PI*0.5;
		}

		return qparameters;
	}

	/**
	 * Compute coordinates from an array of angles
	 * @param angles
	 * @param geometricParameters
	 * @return x and y datasets
	 */
	public static AbstractDataset[] generateCoordinates(AbstractDataset angles, final double[] geometricParameters) {
		if (geometricParameters.length != PARAMETERS)
			throw new IllegalArgumentException("Need " + PARAMETERS + " parameters");

		AbstractDataset[] coords = new AbstractDataset[2];

		DoubleDataset x = new DoubleDataset(angles.getShape());
		DoubleDataset y = new DoubleDataset(angles.getShape());
		coords[0] = x;
		coords[1] = y;

		final double ca = Math.cos(geometricParameters[2]);
		final double sa = Math.sin(geometricParameters[2]);
		final IndexIterator it = angles.getIterator();

		int i = 0;
		
		while (it.hasNext()) {
			final double t = angles.getElementDoubleAbs(it.index);
			final double ct = Math.cos(t);
			final double st = Math.sin(t);
			x.setAbs(i, geometricParameters[3] + geometricParameters[0]*ca*ct - geometricParameters[1]*sa*st);
			y.setAbs(i, geometricParameters[4] + geometricParameters[0]*sa*ct + geometricParameters[1]*ca*st);
			i++;
		}
		return coords;
	}

}
