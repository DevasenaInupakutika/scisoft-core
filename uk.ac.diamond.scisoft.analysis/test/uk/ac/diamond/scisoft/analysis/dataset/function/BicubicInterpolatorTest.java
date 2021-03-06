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

package uk.ac.diamond.scisoft.analysis.dataset.function;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import uk.ac.diamond.scisoft.analysis.dataset.AbstractDataset;
import uk.ac.diamond.scisoft.analysis.dataset.DoubleDataset;

public class BicubicInterpolatorTest {

	@Test
	public void testBicubicInterpolate() {
		double[][] p = new double[4][4];
		p[0][0] = 0.0; p[0][1] = 0.0; p[0][2] = 0.0; p[0][3] = 0.0;
		p[1][0] = 0.0; p[1][1] = 1.0; p[1][2] = 1.0; p[1][3] = 0.0;
		p[2][0] = 0.0; p[2][1] = 1.0; p[2][2] = 1.0; p[2][3] = 0.0;
		p[3][0] = 0.0; p[3][1] = 0.0; p[3][2] = 0.0; p[3][3] = 0.0;
		
		BicubicInterpolator bicube = new BicubicInterpolator(new int[] {1,2});
		
		
		bicube.calculateParameters(p);
		
		assertEquals(1.265,bicube.bicubicInterpolate (0.5, 0.5),0.001);		
		
	}

	@Test
	public void testGenerateSurroundingPoints() {
		
		DoubleDataset ds = DoubleDataset.arange(0.0, 9.0, 1.0);
		ds = (DoubleDataset) ds.reshape(3,3);
		
		BicubicInterpolator bicube = new BicubicInterpolator(new int[] {20,20});
		double[][] val = bicube.generateSurroundingPoints(0, 0, ds);
		
		assertEquals(0.0, val[0][0], 0.1);	
		assertEquals(0.0, val[1][1], 0.1);	
		assertEquals(4.0, val[2][2], 0.1);	
		assertEquals(8.0, val[3][3], 0.1);	
		
		val = bicube.generateSurroundingPoints(1, 1, ds);
		
		assertEquals(0.0, val[0][0], 0.1);	
		assertEquals(4.0, val[1][1], 0.1);	
		assertEquals(8.0, val[2][2], 0.1);	
		assertEquals(8.0, val[3][3], 0.1);	
		
	}
	
	@Test
	public void testValue() {
		DoubleDataset ds = DoubleDataset.arange(0.0, 9.0, 1.0);
		ds = (DoubleDataset) ds.reshape(3,3);
		
		BicubicInterpolator bicube = new BicubicInterpolator(new int[] {5,5});
		List<AbstractDataset> ds2 = bicube.value(ds);
		
		ds2.get(0).peakToPeak();
		
	}

}
