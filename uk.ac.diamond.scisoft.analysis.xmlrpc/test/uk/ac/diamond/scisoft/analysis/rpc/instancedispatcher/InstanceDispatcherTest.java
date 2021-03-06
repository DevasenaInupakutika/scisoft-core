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

package uk.ac.diamond.scisoft.analysis.rpc.instancedispatcher;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;

import uk.ac.diamond.scisoft.analysis.MockDataset;
import uk.ac.diamond.scisoft.analysis.dataset.IDataset;
import uk.ac.diamond.scisoft.analysis.plotserver.GuiBean;
import uk.ac.diamond.scisoft.analysis.plotserver.GuiPlotMode;
import uk.ac.diamond.scisoft.analysis.rpc.AnalysisRpcException;
import uk.ac.diamond.scisoft.analysis.rpc.AnalysisRpcGenericInstanceDispatcher;
import uk.ac.diamond.scisoft.analysis.rpc.flattening.TypedNone;

// a bunch of casts aren't needed, but I find readability is nicer with them all being consistent
@SuppressWarnings("cast")
public class InstanceDispatcherTest {

	@Test
	public void testPrimitiveTypes() throws AnalysisRpcException {
		AnalysisRpcGenericInstanceDispatcher dispatcher = AnalysisRpcGenericInstanceDispatcher
				.getDispatcher(new SingleArgumentPrimitives());
		Assert.assertEquals(Boolean.TYPE, dispatcher.run(new Object[] { "call", false }));
		Assert.assertEquals(Byte.TYPE, dispatcher.run(new Object[] { "call", (byte) 0 }));
		Assert.assertEquals(Character.TYPE, dispatcher.run(new Object[] { "call", (char) 0 }));
		Assert.assertEquals(Double.TYPE, dispatcher.run(new Object[] { "call", (double) 0 }));
		Assert.assertEquals(Float.TYPE, dispatcher.run(new Object[] { "call", (float) 0 }));
		Assert.assertEquals(Integer.TYPE, dispatcher.run(new Object[] { "call", (int) 0 }));
		Assert.assertEquals(Long.TYPE, dispatcher.run(new Object[] { "call", (long) 0 }));
		Assert.assertEquals(Short.TYPE, dispatcher.run(new Object[] { "call", (short) 0 }));
	}

	@Test
	public void testPrimitiveArrayTypes() throws AnalysisRpcException {
		AnalysisRpcGenericInstanceDispatcher dispatcher = AnalysisRpcGenericInstanceDispatcher
				.getDispatcher(new SingleArgumentPrimitiveArrays());
		Assert.assertEquals(boolean[].class, dispatcher.run(new Object[] { "call", new boolean[] { false } }));
		Assert.assertEquals(byte[].class, dispatcher.run(new Object[] { "call", new byte[] { (byte) 0 } }));
		Assert.assertEquals(char[].class, dispatcher.run(new Object[] { "call", new char[] { (char) 0 } }));
		Assert.assertEquals(double[].class, dispatcher.run(new Object[] { "call", new double[] { (double) 0 } }));
		Assert.assertEquals(float[].class, dispatcher.run(new Object[] { "call", new float[] { (float) 0 } }));
		Assert.assertEquals(int[].class, dispatcher.run(new Object[] { "call", new int[] { (int) 0 } }));
		Assert.assertEquals(long[].class, dispatcher.run(new Object[] { "call", new long[] { (long) 0 } }));
		Assert.assertEquals(short[].class, dispatcher.run(new Object[] { "call", new short[] { (short) 0 } }));
	}

	@Test
	public void testBoxedPrimitiveTypes() throws AnalysisRpcException {
		AnalysisRpcGenericInstanceDispatcher dispatcher = AnalysisRpcGenericInstanceDispatcher
				.getDispatcher(new SingleArgumentBoxedPrimitives());
		Assert.assertEquals(Boolean.class, dispatcher.run(new Object[] { "call", false }));
		Assert.assertEquals(Byte.class, dispatcher.run(new Object[] { "call", (byte) 0 }));
		Assert.assertEquals(Character.class, dispatcher.run(new Object[] { "call", (char) 0 }));
		Assert.assertEquals(Double.class, dispatcher.run(new Object[] { "call", (double) 0 }));
		Assert.assertEquals(Float.class, dispatcher.run(new Object[] { "call", (float) 0 }));
		Assert.assertEquals(Integer.class, dispatcher.run(new Object[] { "call", (int) 0 }));
		Assert.assertEquals(Long.class, dispatcher.run(new Object[] { "call", (long) 0 }));
		Assert.assertEquals(Short.class, dispatcher.run(new Object[] { "call", (short) 0 }));
	}

	@Test
	public void testBoxedPrimitiveArrayTypes() throws AnalysisRpcException {
		AnalysisRpcGenericInstanceDispatcher dispatcher = AnalysisRpcGenericInstanceDispatcher
				.getDispatcher(new SingleArgumentBoxedPrimitiveArrays());

		Assert.assertEquals(Boolean[].class, dispatcher.run(new Object[] { "call", new Boolean[] { false } }));
		Assert.assertEquals(Byte[].class, dispatcher.run(new Object[] { "call", new Byte[] { (byte) 0 } }));
		Assert.assertEquals(Character[].class, dispatcher.run(new Object[] { "call", new Character[] { (char) 0 } }));
		Assert.assertEquals(Double[].class, dispatcher.run(new Object[] { "call", new Double[] { (double) 0 } }));
		Assert.assertEquals(Float[].class, dispatcher.run(new Object[] { "call", new Float[] { (float) 0 } }));
		Assert.assertEquals(Integer[].class, dispatcher.run(new Object[] { "call", new Integer[] { (int) 0 } }));
		Assert.assertEquals(Long[].class, dispatcher.run(new Object[] { "call", new Long[] { (long) 0 } }));
		Assert.assertEquals(Short[].class, dispatcher.run(new Object[] { "call", new Short[] { (short) 0 } }));
	}

	@Test
	public void testSDAPlotterTypes() throws AnalysisRpcException {
		AnalysisRpcGenericInstanceDispatcher dispatcher = AnalysisRpcGenericInstanceDispatcher
				.getDispatcher(new SingleArgumentSDAPlotterDataTypes());
		Assert.assertEquals(GuiBean.class, dispatcher.run(new Object[] { "call", new GuiBean() }));
		Assert.assertEquals(GuiPlotMode.class, dispatcher.run(new Object[] { "call", GuiPlotMode.EMPTY }));
		Assert.assertEquals(IDataset.class, dispatcher.run(new Object[] { "call", new MockDataset() }));
		Assert.assertEquals(IDataset[].class,
				dispatcher.run(new Object[] { "call", new IDataset[] { new MockDataset() } }));
		Assert.assertEquals(String[].class, dispatcher.run(new Object[] { "call", new String[] { "Hello" } }));
		Assert.assertEquals(String.class, dispatcher.run(new Object[] { "call", "Hello" }));
	}

	@Test
	public void testNoArgs() throws AnalysisRpcException {
		AnalysisRpcGenericInstanceDispatcher dispatcher = AnalysisRpcGenericInstanceDispatcher
				.getDispatcher(new NoArgument());
		Assert.assertEquals("no argument", dispatcher.run(new Object[] { "call" }));
	}

	@Test
	public void testVoidReturn() throws AnalysisRpcException {
		AnalysisRpcGenericInstanceDispatcher dispatcher = AnalysisRpcGenericInstanceDispatcher
				.getDispatcher(new VoidReturn());
		Assert.assertEquals(null, dispatcher.run(new Object[] { "call", 0 }));
	}

	@Test
	public void testManyArguments() throws AnalysisRpcException {
		AnalysisRpcGenericInstanceDispatcher dispatcher = AnalysisRpcGenericInstanceDispatcher
				.getDispatcher(new ManyArguments());
		Assert.assertEquals(8, dispatcher.run(new Object[] { "call", 1, 2, 3, 4, 5, 6, 7, 8 }));
		Assert.assertEquals(7, dispatcher.run(new Object[] { "call", 1, 2, 3, 4, 5, 6, 7 }));
		Assert.assertEquals(6, dispatcher.run(new Object[] { "call", 1, 2, 3, 4, 5, 6 }));
		Assert.assertEquals(5, dispatcher.run(new Object[] { "call", 1, 2, 3, 4, 5 }));
		Assert.assertEquals(4, dispatcher.run(new Object[] { "call", 1, 2, 3, 4 }));
		Assert.assertEquals(3, dispatcher.run(new Object[] { "call", 1, 2, 3 }));
		Assert.assertEquals(2, dispatcher.run(new Object[] { "call", 1, 2 }));
		Assert.assertEquals(1, dispatcher.run(new Object[] { "call", 1 }));
		Assert.assertEquals(0, dispatcher.run(new Object[] { "call" }));
	}

	@Test
	public void testNoSuchMethod() {
		AnalysisRpcGenericInstanceDispatcher dispatcher = AnalysisRpcGenericInstanceDispatcher
				.getDispatcher(new UsedForVarious());
		try {
			dispatcher.run(new Object[] { "missing", 0 });
			Assert.fail();
		} catch (AnalysisRpcException e) {
			Assert.assertTrue(e.getMessage().indexOf("Failed to find method") != -1);
		}
	}

	@Test
	public void testOnlyMatchingMethodIsNonStatic() {
		AnalysisRpcGenericInstanceDispatcher dispatcher = AnalysisRpcGenericInstanceDispatcher
				.getDispatcher(new UsedForVarious());
		try {
			dispatcher.run(new Object[] { "nonStaticMethod", 0 });
			Assert.fail();
		} catch (AnalysisRpcException e) {
			Assert.assertTrue(e.getMessage().indexOf("Failed to find method") != -1);
		}
	}

	@Test
	public void testSimilarSignatureStaticAndNonStatic() throws AnalysisRpcException {
		AnalysisRpcGenericInstanceDispatcher dispatcher = AnalysisRpcGenericInstanceDispatcher
				.getDispatcher(new UsedForVarious());
		Assert.assertEquals("non-static", dispatcher.run(new Object[] { "similarSignatureStaticAndNonStatic", 0 }));
	}

	@Test
	public void testCallCheckedException() {
		AnalysisRpcGenericInstanceDispatcher dispatcher = AnalysisRpcGenericInstanceDispatcher
				.getDispatcher(new UsedForVarious());
		try {
			dispatcher.run(new Object[] { "callCheckedException", 0 });
			Assert.fail();
		} catch (AnalysisRpcException e) {
			Assert.assertTrue(e.getCause() instanceof IOException);
		}
	}

	@Test
	public void testCallUnCheckedException() {
		AnalysisRpcGenericInstanceDispatcher dispatcher = AnalysisRpcGenericInstanceDispatcher
				.getDispatcher(new UsedForVarious());
		try {
			dispatcher.run(new Object[] { "callUnCheckedException", 0 });
			Assert.fail();
		} catch (AnalysisRpcException e) {
			Assert.assertTrue(e.getCause() instanceof UnsupportedOperationException);
		}
	}

	// This test shows how overloaded, but assignable types may cause the wrong one to execute
	@Test
	public void testOverloadedAssignable() throws AnalysisRpcException {
		AnalysisRpcGenericInstanceDispatcher dispatcher = AnalysisRpcGenericInstanceDispatcher
				.getDispatcher(new UsedForVarious());
		Assert.assertFalse((new UsedForVarious().overloaded(new Object())).equals(new UsedForVarious()
				.overloaded(0)));
		Assume.assumeTrue(dispatcher.run(new Object[] { "overloaded", new Object() }).equals(
				dispatcher.run(new Object[] { "overloaded", 0 })));
	}

	@Test
	public void testNullArgument() throws AnalysisRpcException {
		AnalysisRpcGenericInstanceDispatcher dispatcher = AnalysisRpcGenericInstanceDispatcher
				.getDispatcher(new UsedForVarious());
		Assert.assertEquals("callObjectWithNull", dispatcher.run(new Object[] { "callObjectWithNull", null }));
	}

	@Test
	public void testNullDoesntMatchPrimitives1() {
		AnalysisRpcGenericInstanceDispatcher dispatcher = AnalysisRpcGenericInstanceDispatcher
				.getDispatcher(new SingleArgumentPrimitives());
		try {
			dispatcher.run(new Object[] { "call", null });
			Assert.fail();
		} catch (AnalysisRpcException e) {
			Assert.assertTrue(e.getMessage().indexOf("Failed to find method") != -1);
		}

	}

	@Test
	public void testNullDoesntMatchPrimitives2() throws AnalysisRpcException {
		AnalysisRpcGenericInstanceDispatcher dispatcher = AnalysisRpcGenericInstanceDispatcher
				.getDispatcher(new NullPrimitive());
		Assert.assertEquals(Object.class, dispatcher.run(new Object[] { "call", null }));
	}

	@Test
	public void testNullReturn() throws AnalysisRpcException {
		AnalysisRpcGenericInstanceDispatcher dispatcher = AnalysisRpcGenericInstanceDispatcher
				.getDispatcher(new UsedForVarious());
		Assert.assertEquals(null, dispatcher.run(new Object[] { "callNullReturn", 0 }));
		Assert.assertEquals(null, dispatcher.run(new Object[] { "callNullReturn", "arg" }));
	}

	@Test
	public void testMisMatched() throws AnalysisRpcException {
		AnalysisRpcGenericInstanceDispatcher dispatcher = AnalysisRpcGenericInstanceDispatcher
				.getDispatcher(new UsedForVarious());
		Assert.assertEquals(null, dispatcher.run(new Object[] { "callNullReturn", 0 }));
		Assert.assertEquals(null, dispatcher.run(new Object[] { "callNullReturn", "arg" }));
	}

	@Test
	public void testInterfaceAndImpl_InterfaceDelegate() throws AnalysisRpcException {
		AnalysisRpcGenericInstanceDispatcher dispatcher = new AnalysisRpcGenericInstanceDispatcher(
				InterfaceAndImpl.class, new InterfaceAndImpl.Impl());
		Assert.assertEquals("interfacecall", dispatcher.run(new Object[] { "interfacecall", 0 }));
		Assert.assertEquals("implcalloverloaded - interface", dispatcher.run(new Object[] { "implcalloverloaded", 0 }));
		try {
			dispatcher.run(new Object[] { "implcalloverloaded", "string" });
			Assert.fail();
		} catch (AnalysisRpcException e) {
			Assert.assertTrue(e.getMessage().indexOf("Failed to find method") != -1);
		}
		try {
			dispatcher.run(new Object[] { "implcall", 0 });
			Assert.fail();
		} catch (AnalysisRpcException e) {
			Assert.assertTrue(e.getMessage().indexOf("Failed to find method") != -1);
		}
	}

	@Test
	public void testInterfaceAndImpl_ImplDelegate() throws AnalysisRpcException {
		AnalysisRpcGenericInstanceDispatcher dispatcher = AnalysisRpcGenericInstanceDispatcher
				.getDispatcher(new InterfaceAndImpl.Impl());
		Assert.assertEquals("interfacecall", dispatcher.run(new Object[] { "interfacecall", 0 }));
		Assert.assertEquals("implcalloverloaded - interface", dispatcher.run(new Object[] { "implcalloverloaded", 0 }));
		Assert.assertEquals("implcalloverloaded - impl", dispatcher.run(new Object[] { "implcalloverloaded", "string" }));
		Assert.assertEquals("implcall", dispatcher.run(new Object[] { "implcall", 0 }));
	}

	@SuppressWarnings("unused")
	@Test(expected = NullPointerException.class)
	public void testExceptionOnCreation1() {
		new AnalysisRpcGenericInstanceDispatcher(null, null);
	}

	@SuppressWarnings("unused")
	@Test(expected = NullPointerException.class)
	public void testExceptionOnCreation2() {
		new AnalysisRpcGenericInstanceDispatcher(Object.class, null);
	}

	@SuppressWarnings("unused")
	@Test(expected = NullPointerException.class)
	public void testExceptionOnCreation3() {
		new AnalysisRpcGenericInstanceDispatcher(null, new Object());
	}

	@SuppressWarnings("unused")
	@Test(expected = IllegalArgumentException.class)
	public void testExceptionOnCreation4() {
		new AnalysisRpcGenericInstanceDispatcher(Integer.class, new Object());
	}
	
	@Test
	public void testTypedNullArgument() throws AnalysisRpcException {
		AnalysisRpcGenericInstanceDispatcher dispatcher = AnalysisRpcGenericInstanceDispatcher.getDispatcher(new UsedForVarious());
		Assert.assertEquals(Integer.class, dispatcher.run(new Object[] { "typednone", new TypedNone(Integer.class) }));
		Assert.assertEquals(String.class, dispatcher.run(new Object[] { "typednone", new TypedNone(String.class) }));
	}

}
