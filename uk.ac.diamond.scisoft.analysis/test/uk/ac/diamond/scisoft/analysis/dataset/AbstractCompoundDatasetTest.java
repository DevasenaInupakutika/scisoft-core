/*-
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

package uk.ac.diamond.scisoft.analysis.dataset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

public class AbstractCompoundDatasetTest {
	long[] ldata = {0, 1, 2, 3, 4, 5};
	int[] idata = {0, 1, 2, 3, 4, 5};
	short[] sdata = {0, 1, 2, 3, 4, 5};
	byte[] bdata = {0, 1, 2, 3, 4, 5};
	double[] ddata = {0., 1., 2., 3., 4., 5.};
	float[] fdata = {0.f, 1.f, 2.f, 3.f, 4.f, 5.f};

	@Test
	public void testToDoubleArray() {
		double[] d;

		d = AbstractCompoundDataset.toDoubleArray(ddata, idata.length);
		for (int i = 0; i < idata.length; i++) {
			assertEquals(ddata[i], d[i], 1e-10);
		}
		d = AbstractCompoundDataset.toDoubleArray(fdata, idata.length);
		for (int i = 0; i < idata.length; i++) {
			assertEquals(ddata[i], d[i], 1e-10);
		}
		d = AbstractCompoundDataset.toDoubleArray(ldata, idata.length);
		for (int i = 0; i < idata.length; i++) {
			assertEquals(ddata[i], d[i], 1e-10);
		}
		d = AbstractCompoundDataset.toDoubleArray(idata, idata.length);
		for (int i = 0; i < idata.length; i++) {
			assertEquals(ddata[i], d[i], 1e-10);
		}
		d = AbstractCompoundDataset.toDoubleArray(sdata, idata.length);
		for (int i = 0; i < idata.length; i++) {
			assertEquals(ddata[i], d[i], 1e-10);
		}
		d = AbstractCompoundDataset.toDoubleArray(bdata, idata.length);
		for (int i = 0; i < idata.length; i++) {
			assertEquals(ddata[i], d[i], 1e-10);
		}
	}

	@Test
	public void testToFloatArray() {
		float[] d;

		d = AbstractCompoundDataset.toFloatArray(ddata, idata.length);
		for (int i = 0; i < idata.length; i++) {
			assertEquals(ddata[i], d[i], 1e-10);
		}
		d = AbstractCompoundDataset.toFloatArray(fdata, idata.length);
		for (int i = 0; i < idata.length; i++) {
			assertEquals(ddata[i], d[i], 1e-10);
		}
		d = AbstractCompoundDataset.toFloatArray(ldata, idata.length);
		for (int i = 0; i < idata.length; i++) {
			assertEquals(ddata[i], d[i], 1e-10);
		}
		d = AbstractCompoundDataset.toFloatArray(idata, idata.length);
		for (int i = 0; i < idata.length; i++) {
			assertEquals(ddata[i], d[i], 1e-10);
		}
		d = AbstractCompoundDataset.toFloatArray(sdata, idata.length);
		for (int i = 0; i < idata.length; i++) {
			assertEquals(ddata[i], d[i], 1e-10);
		}
		d = AbstractCompoundDataset.toFloatArray(bdata, idata.length);
		for (int i = 0; i < idata.length; i++) {
			assertEquals(ddata[i], d[i], 1e-10);
		}
	}

	@Test
	public void testToLongArray() {
		long[] d;

		d = AbstractCompoundDataset.toLongArray(ddata, idata.length);
		for (int i = 0; i < idata.length; i++) {
			assertEquals(ddata[i], d[i], 1e-10);
		}
		d = AbstractCompoundDataset.toLongArray(fdata, idata.length);
		for (int i = 0; i < idata.length; i++) {
			assertEquals(ddata[i], d[i], 1e-10);
		}
		d = AbstractCompoundDataset.toLongArray(ldata, idata.length);
		for (int i = 0; i < idata.length; i++) {
			assertEquals(ddata[i], d[i], 1e-10);
		}
		d = AbstractCompoundDataset.toLongArray(idata, idata.length);
		for (int i = 0; i < idata.length; i++) {
			assertEquals(ddata[i], d[i], 1e-10);
		}
		d = AbstractCompoundDataset.toLongArray(sdata, idata.length);
		for (int i = 0; i < idata.length; i++) {
			assertEquals(ddata[i], d[i], 1e-10);
		}
		d = AbstractCompoundDataset.toLongArray(bdata, idata.length);
		for (int i = 0; i < idata.length; i++) {
			assertEquals(ddata[i], d[i], 1e-10);
		}
	}

	@Test
	public void testToIntegerArray() {
		int[] d;

		d = AbstractCompoundDataset.toIntegerArray(ddata, idata.length);
		for (int i = 0; i < idata.length; i++) {
			assertEquals(ddata[i], d[i], 1e-10);
		}
		d = AbstractCompoundDataset.toIntegerArray(fdata, idata.length);
		for (int i = 0; i < idata.length; i++) {
			assertEquals(ddata[i], d[i], 1e-10);
		}
		d = AbstractCompoundDataset.toIntegerArray(ldata, idata.length);
		for (int i = 0; i < idata.length; i++) {
			assertEquals(ddata[i], d[i], 1e-10);
		}
		d = AbstractCompoundDataset.toIntegerArray(idata, idata.length);
		for (int i = 0; i < idata.length; i++) {
			assertEquals(ddata[i], d[i], 1e-10);
		}
		d = AbstractCompoundDataset.toIntegerArray(sdata, idata.length);
		for (int i = 0; i < idata.length; i++) {
			assertEquals(ddata[i], d[i], 1e-10);
		}
		d = AbstractCompoundDataset.toIntegerArray(bdata, idata.length);
		for (int i = 0; i < idata.length; i++) {
			assertEquals(ddata[i], d[i], 1e-10);
		}
	}

	@Test
	public void testToShortArray() {
		short[] d;

		d = AbstractCompoundDataset.toShortArray(ddata, idata.length);
		for (int i = 0; i < idata.length; i++) {
			assertEquals(ddata[i], d[i], 1e-10);
		}
		d = AbstractCompoundDataset.toShortArray(fdata, idata.length);
		for (int i = 0; i < idata.length; i++) {
			assertEquals(ddata[i], d[i], 1e-10);
		}
		d = AbstractCompoundDataset.toShortArray(ldata, idata.length);
		for (int i = 0; i < idata.length; i++) {
			assertEquals(ddata[i], d[i], 1e-10);
		}
		d = AbstractCompoundDataset.toShortArray(idata, idata.length);
		for (int i = 0; i < idata.length; i++) {
			assertEquals(ddata[i], d[i], 1e-10);
		}
		d = AbstractCompoundDataset.toShortArray(sdata, idata.length);
		for (int i = 0; i < idata.length; i++) {
			assertEquals(ddata[i], d[i], 1e-10);
		}
		d = AbstractCompoundDataset.toShortArray(bdata, idata.length);
		for (int i = 0; i < idata.length; i++) {
			assertEquals(ddata[i], d[i], 1e-10);
		}
	}

	@Test
	public void testToByteArray() {
		byte[] d;

		d = AbstractCompoundDataset.toByteArray(ddata, idata.length);
		for (int i = 0; i < idata.length; i++) {
			assertEquals(ddata[i], d[i], 1e-10);
		}
		d = AbstractCompoundDataset.toByteArray(fdata, idata.length);
		for (int i = 0; i < idata.length; i++) {
			assertEquals(ddata[i], d[i], 1e-10);
		}
		d = AbstractCompoundDataset.toByteArray(ldata, idata.length);
		for (int i = 0; i < idata.length; i++) {
			assertEquals(ddata[i], d[i], 1e-10);
		}
		d = AbstractCompoundDataset.toByteArray(idata, idata.length);
		for (int i = 0; i < idata.length; i++) {
			assertEquals(ddata[i], d[i], 1e-10);
		}
		d = AbstractCompoundDataset.toByteArray(sdata, idata.length);
		for (int i = 0; i < idata.length; i++) {
			assertEquals(ddata[i], d[i], 1e-10);
		}
		d = AbstractCompoundDataset.toByteArray(bdata, idata.length);
		for (int i = 0; i < idata.length; i++) {
			assertEquals(ddata[i], d[i], 1e-10);
		}
	}

	@Test
	public void testSum() {
		AbstractDataset d = Random.randint(0, 255, new int[] {5,2});
		AbstractCompoundDataset dc = DatasetUtils.createCompoundDatasetFromLastAxis(d, true);
		AbstractDataset dd = DatasetUtils.createDatasetFromCompoundDataset(dc, true);
		double[] dcsum = (double[]) dc.sum();
		double dsum = ((Number) d.sum()).doubleValue();
		double ddsum = ((Number) dd.sum()).doubleValue();
		assertEquals(dsum, dcsum[0]+dcsum[1], 1e-10);
		assertEquals(dsum, ddsum, 1e-10);

		d = Random.randint(0, 255, new int[] {5,3,2});
		dc = DatasetUtils.createCompoundDatasetFromLastAxis(d, true);
		AbstractDataset dca = DatasetUtils.createDatasetFromCompoundDataset((AbstractCompoundDataset) dc.sum(0), true);
		AbstractDataset da = d.sum(0);
		IndexIterator it = da.getIterator();
		while (it.hasNext()) {
			assertEquals(da.getElementDoubleAbs(it.index), dca.getElementDoubleAbs(it.index), 1e-15);
		}
		AbstractDataset dcb = DatasetUtils.createDatasetFromCompoundDataset((AbstractCompoundDataset) dc.sum(1), true);
		AbstractDataset db = d.sum(1);
		it = db.getIterator();
		while (it.hasNext()) {
			assertEquals(db.getElementDoubleAbs(it.index), dcb.getElementDoubleAbs(it.index), 1e-15);
		}
	}

	@Test
	public void testCompoundCreators() {
		double dz = 0.5;
		CompoundDoubleDataset z = CompoundDoubleDataset.createFromObject(dz);
		assertEquals(0, z.getRank());
		assertEquals(1, z.getSize());
		assertEquals(1, z.getElementsPerItem());
		assertEquals(dz, z.getElementDoubleAbs(0), 1e-14);

		double[] da = { 0, 1, 2, 3, 4, 5 };
		CompoundDoubleDataset a = CompoundDoubleDataset.createFromObject(da);
		int is = a.getElementsPerItem();
		assertEquals(6, is);
		assertEquals(1, a.getRank());
		assertEquals(1, a.getSize());
		assertEquals(1, a.getShape()[0]);
		IndexIterator it = a.getIterator();
		for (int i = 0; it.hasNext();) {
			for (int j = 0; j < is; j++, i++)
				assertEquals(i, a.getElementDoubleAbs(it.index + j), 1e-15*i);
		}

		double[][] db = { {0, 1, 2}, {3, 4, 5} };
		CompoundDoubleDataset b = CompoundDoubleDataset.createFromObject(db);
		is = b.getElementsPerItem();
		assertEquals(3, is);
		assertEquals(1, b.getRank());
		assertEquals(2, b.getSize());
		assertEquals(2, b.getShape()[0]);
		it = b.getIterator();
		for (int i = 0; it.hasNext();) {
			for (int j = 0; j < is; j++, i++)
				assertEquals(i, b.getElementDoubleAbs(it.index + j), 1e-15*i);
		}
		b.hashCode();
		double[] mb = (double[]) b.mean();
		double[] rb = new double[] {1.5, 2.5, 3.5};
		for (int j = 0; j < is; j++)
			assertEquals(rb[j], mb[j], 1e-15);

		double[][] dc = { {0, 1, 2, 3}, {4, 5, 6} };
		CompoundDoubleDataset c = CompoundDoubleDataset.createFromObject(dc);
		is = c.getElementsPerItem();
		assertEquals(4, is);
		assertEquals(1, c.getRank());
		assertEquals(2, c.getSize());
		assertEquals(2, c.getShape()[0]);
		it = c.getIterator();
		for (int i = 0; it.hasNext();) {
			for (int j = 0; j < is; j++, i++) {
				if (i < 7)
					assertEquals(i, c.getElementDoubleAbs(it.index + j), 1e-15 * i);
				else
					assertEquals(0, c.getElementDoubleAbs(it.index + j), 1e-15);
			}
		}

		double[][] dd = { {0, 1, 2}, {4, 5, 6, 7} };
		CompoundDoubleDataset d = CompoundDoubleDataset.createFromObject(dd);
		is = d.getElementsPerItem();
		assertEquals(4, is);
		assertEquals(1, d.getRank());
		assertEquals(2, d.getSize());
		assertEquals(2, d.getShape()[0]);
		it = d.getIterator();
		for (int i = 0; it.hasNext();) {
			for (int j = 0; j < is; j++, i++) {
				if (i != 3)
					assertEquals(i, d.getElementDoubleAbs(it.index + j), 1e-15 * i);
				else
					assertEquals(0, d.getElementDoubleAbs(it.index + j), 1e-15);
			}
		}
	}
	
	@Test
	public void testRGB() {
		AbstractDataset r = Random.randint(0, 255, new int[] {128, 128});
		AbstractDataset g = Random.randint(0, 255, r.getShape());
		AbstractDataset b = Random.randint(0, 255, r.getShape());
		RGBDataset c = new RGBDataset(r, g, b);
		System.out.println("" + c.hashCode());
		double[] mc = (double[]) c.mean();
		double[] rc = new double[] {((Number) r.mean()).doubleValue(),
				((Number) g.mean()).doubleValue(), ((Number) b.mean()).doubleValue()};
		
		for (int j = 0; j < 3; j++)
			assertEquals(rc[j], mc[j], 1e-15);
	}

	@Test
	public void test1DErrors() {
	
		// test 1D errors for single value
		AbstractDataset[] aa =  new AbstractDataset[5];
		for (int i = 0 ; i < 5; i++) {
			aa[i] = AbstractDataset.arange(100, AbstractDataset.INT32);
		}
		AbstractCompoundDataset a = new CompoundIntegerDataset(aa);
		
		a.setError(5);
		assertTrue(a.hasErrors());
		
		assertEquals(5.0, a.getErrorArray(0)[0], 0.001);
		assertEquals(5.0, a.getErrorArray(0)[2], 0.001);
		assertEquals(5.0, a.getErrorArray(0)[4], 0.001);
		
		assertEquals(5.0, a.getErrorArray(50)[0], 0.001);
		assertEquals(5.0, a.getErrorArray(50)[2], 0.001);
		assertEquals(5.0, a.getErrorArray(50)[4], 0.001);
		
		assertEquals(5.0, a.getErrorArray(99)[0], 0.001);
		assertEquals(5.0, a.getErrorArray(99)[2], 0.001);
		assertEquals(5.0, a.getErrorArray(99)[4], 0.001);
		
		// now for pulling out the full error array
		AbstractCompoundDataset error = a.getError();
		
		// check compatibility
		try {
			AbstractDataset.checkCompatibility(a, error);
		} catch (Exception e) {
			fail("Error shape is not the same as input datasets");
		}
		
		assertEquals(5.0, error.getElements(0).getDouble(0), 0.001);
		assertEquals(5.0, error.getElements(0).getDouble(50), 0.001);
		assertEquals(5.0, error.getElements(0).getDouble(99), 0.001);
		
		assertEquals(5.0, error.getElements(2).getDouble(0), 0.001);
		assertEquals(5.0, error.getElements(2).getDouble(50), 0.001);
		assertEquals(5.0, error.getElements(2).getDouble(99), 0.001);
		
		assertEquals(5.0, error.getElements(4).getDouble(0), 0.001);
		assertEquals(5.0, error.getElements(4).getDouble(50), 0.001);
		assertEquals(5.0, error.getElements(4).getDouble(99), 0.001);
		
		// Now set the error as a whole array
		a.setError(Maths.multiply(error, 2));
		
		assertEquals(10.0, a.getErrorArray(0)[0], 0.001);
		assertEquals(10.0, a.getErrorArray(0)[2], 0.001);
		assertEquals(10.0, a.getErrorArray(0)[4], 0.001);
		
		assertEquals(10.0, a.getErrorArray(50)[0], 0.001);
		assertEquals(10.0, a.getErrorArray(50)[2], 0.001);
		assertEquals(10.0, a.getErrorArray(50)[4], 0.001);
		
		assertEquals(10.0, a.getErrorArray(99)[0], 0.001);
		assertEquals(10.0, a.getErrorArray(99)[2], 0.001);
		assertEquals(10.0, a.getErrorArray(99)[4], 0.001);
		
		// test pulling the error out again, to make sure its correct
		AbstractCompoundDataset error2 = a.getError();
		
		// check compatibility
		try {
			AbstractDataset.checkCompatibility(a, error2);
		} catch (Exception e) {
			fail("Error shape is not the same as input datasets");
		}
		
		assertEquals(10.0, error2.getElements(0).getDouble(0), 0.001);
		assertEquals(10.0, error2.getElements(0).getDouble(50), 0.001);
		assertEquals(10.0, error2.getElements(0).getDouble(99), 0.001);
		
		assertEquals(10.0, error2.getElements(2).getDouble(0), 0.001);
		assertEquals(10.0, error2.getElements(2).getDouble(50), 0.001);
		assertEquals(10.0, error2.getElements(2).getDouble(99), 0.001);
		
		assertEquals(10.0, error2.getElements(4).getDouble(0), 0.001);
		assertEquals(10.0, error2.getElements(4).getDouble(50), 0.001);
		assertEquals(10.0, error2.getElements(4).getDouble(99), 0.001);
		
		
		// finaly check the array setting
		
		a.setError(new Double[] { 1.0, 2.0, 3.0, 4.0, 5.0});
		
		assertEquals(1.0, a.getErrorArray(0)[0], 0.001);
		assertEquals(3.0, a.getErrorArray(0)[2], 0.001);
		assertEquals(5.0, a.getErrorArray(0)[4], 0.001);
		
		assertEquals(1.0, a.getErrorArray(50)[0], 0.001);
		assertEquals(3.0, a.getErrorArray(50)[2], 0.001);
		assertEquals(5.0, a.getErrorArray(50)[4], 0.001);
		
		assertEquals(1.0, a.getErrorArray(99)[0], 0.001);
		assertEquals(3.0, a.getErrorArray(99)[2], 0.001);
		assertEquals(5.0, a.getErrorArray(99)[4], 0.001);
		
		// test pulling the error out again, to make sure its correct
		AbstractCompoundDataset error3 = a.getError();
		
		// check compatibility
		try {
			AbstractDataset.checkCompatibility(a, error2);
		} catch (Exception e) {
			fail("Error shape is not the same as input datasets");
		}
		
		assertEquals(1.0, error3.getElements(0).getDouble(0), 0.001);
		assertEquals(2.0, error3.getElements(1).getDouble(50), 0.001);
		assertEquals(1.0, error3.getElements(0).getDouble(99), 0.001);
		
		assertEquals(2.0, error3.getElements(1).getDouble(0), 0.001);
		assertEquals(3.0, error3.getElements(2).getDouble(50), 0.001);
		assertEquals(4.0, error3.getElements(3).getDouble(99), 0.001);
		
		assertEquals(5.0, error3.getElements(4).getDouble(0), 0.001);
		assertEquals(4.0, error3.getElements(3).getDouble(50), 0.001);
		assertEquals(5.0, error3.getElements(4).getDouble(99), 0.001);
		
	}
	
	
	@Test
	public void testInternalErrors() {
		
		
		AbstractDataset[] aa =  new AbstractDataset[5];
		for (int i = 0 ; i < 5; i++) {
			aa[i] = AbstractDataset.arange(100, AbstractDataset.INT32);
		}
		AbstractCompoundDataset a = new CompoundIntegerDataset(aa);
		
		a.setError(new Double[] { 1.0, 2.0, 3.0, 4.0, 5.0});
		
		// should be squared
		assertEquals(1.0, AbstractCompoundDataset.toDoubleArray(a.errorData,5)[0], 0.001);
		assertEquals(4.0, AbstractCompoundDataset.toDoubleArray(a.errorData,5)[1], 0.001);
		assertEquals(9.0, AbstractCompoundDataset.toDoubleArray(a.errorData,5)[2], 0.001);
		assertEquals(16.0, AbstractCompoundDataset.toDoubleArray(a.errorData,5)[3], 0.001);
		assertEquals(25.0, AbstractCompoundDataset.toDoubleArray(a.errorData,5)[4], 0.001);
		
		// now for pulling out the full error array
		AbstractCompoundDataset error = a.getError();
	
		a.setError(error);
		
		// should also be squared
		AbstractCompoundDataset ae = (AbstractCompoundDataset) a.errorData;
		assertEquals(1.0, ae.getElements(0).getDouble(0), 0.001);
		assertEquals(4.0, ae.getElements(1).getDouble(0), 0.001);
		assertEquals(9.0, ae.getElements(2).getDouble(0), 0.001);
		assertEquals(16.0, ae.getElements(3).getDouble(0), 0.001);
		assertEquals(25.0, ae.getElements(4).getDouble(0), 0.001);
		
		assertEquals(1.0, ae.getElements(0).getDouble(99), 0.001);
		assertEquals(4.0, ae.getElements(1).getDouble(99), 0.001);
		assertEquals(9.0, ae.getElements(2).getDouble(99), 0.001);
		assertEquals(16.0, ae.getElements(3).getDouble(99), 0.001);
		assertEquals(25.0, ae.getElements(4).getDouble(99), 0.001);
	}
}
