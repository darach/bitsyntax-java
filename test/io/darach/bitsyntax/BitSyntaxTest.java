// Copyright (c) 2013 Darach Ennis < darach at gmail dot com >.
//
// Permission is hereby granted, free of charge, to any person obtaining a
// copy of this software and associated documentation files (the
// "Software"), to deal in the Software without restriction, including
// without limitation the rights to use, copy, modify, merge, publish,
// distribute, sublicense, and/or sell copies of the Software, and to permit
// persons to whom the Software is furnished to do so, subject to the
// following conditions:  
//
// The above copyright notice and this permission notice shall be included
// in all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
// OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
// MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN
// NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
// DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
// OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE
// USE OR OTHER DEALINGS IN THE SOFTWARE.


package io.darach.bitsyntax;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Map;

import org.junit.Test;

import static io.darach.bitsyntax.BitHelper.*;

public class BitSyntaxTest {

	@Test
	public void testCompilerDefaultsAndOverrides() throws IOException {
		// Should generate unique names in default package with default class prefix
		BitPattern expr0 = BitSyntax.compile("<< >>");
		BitPattern expr1 = BitSyntax.compile("<< >>");
		BitPattern expr2 = BitSyntax.compile("<< >>");
		assertEquals("bitsyntax.generated.Expr1", expr0.getClass().getName());
		assertEquals("bitsyntax.generated.Expr2", expr1.getClass().getName());
		assertEquals("bitsyntax.generated.Expr3", expr2.getClass().getName());
		
		// Should generate unique class names in custom package with custom class prefix
		BitPattern expr3 = BitSyntax.compile("foo", "bar", "<< >>");
		BitPattern expr4 = BitSyntax.compile("bar", "foo", "<< >>");
		assertEquals("foo.bar4", expr3.getClass().getName());
		assertEquals("bar.foo5", expr4.getClass().getName());
		
	}

	@Test
	public void testEmpty() throws IOException {
		BitPattern expr = BitSyntax.compile("<< >>");
		assertEquals("<<>>", expr.specification());
		assertEquals("<<\n>>", expr.expansion()); // @TODO better equals ...
		Map<String,byte[]> emptyMap = expr.extract(new byte[] { });
		assertEquals(0, emptyMap.size());
	}
	
	@Test
	public void testBinary() throws IOException {
		byte[] binary = fromHex("CAFEBABE");
		assertExtractTest("CA", "<< test:1/binary, foo:1/binary, 1/binary >>", binary);
		assertExtractTest("FE", "<< foo:1/binary, test:1/binary, 3/binary >>", binary);
		assertExtractTest("BABE", "<< foo:1/binary, bar:1/binary, test:2/binary >>", binary);
		assertExtractTest("0F", "<< foo:1/binary, test:1/binary-unit:4, _:4/unit:1, bar:2/binary >>", binary);
		assertExtractTest("0F", "<< foo:1/binary, test:4/binary-unit:1, _:4/unit:1, bar:2/binary >>", binary);
		assertExtractTest("0F", "<< foo:1/binary, test:4/binary-unit:1, x:16/binary-unit:1 >>", binary);
		assertExtractTest("BABE", "<< foo:1/binary, bar:1/binary, test:16/binary-unit:1 >>", binary);
		assertExtractTest("CA", "<< test:1/binary, bar:1/binary, n:13/binary-unit:1, x:3/binary-unit:1 >>", binary);
		assertExtractTest("FE", "<< foo:1/binary, test:1/binary, n:13/binary-unit:1, x:3/binary-unit:1 >>", binary);
		assertExtractTest("BA", "<< foo:1/binary, bar:1/binary, test:13/binary-unit:1, x:3/binary-unit:1 >>", binary);
		assertExtractTest("06", "<< foo:1/binary, bar:1/binary, n:13/binary-unit:1, test:3/binary-unit:1 >>", binary);
	}

	@Test
	public void testNumericInteger() throws IOException {
		byte[] integer = fromHex("CAFEBABE");
		assertExtractTest("CA", "<< test:8/integer, 1:8/integer, 10:16/integer >>", integer);
		assertExtractTest("CA", "<< test:8/integer, foo:8/integer, bar:16/integer >>", integer);
		assertExtractTest("FE", "<< foo:8/integer, test:8/integer, bar:16/integer >>", integer);
		assertExtractTest("BABE", "<< foo:8/integer, bar:8/integer, test:16/integer >>", integer);
		assertExtractTest("0F", "<< foo:8/integer, test:1/integer-unit:4, _:4/unit:1, bar:16/integer >>", integer);
		assertExtractTest("0F", "<< foo:8/integer, test:4/integer-unit:1, _:4/unit:1, bar:16/integer >>", integer);
		assertExtractTest("0F", "<< foo:8/integer, test:4/integer-unit:1, x:16/integer-unit:1 >>", integer);
		assertExtractTest("BABE", "<< foo:8/integer, bar:8/integer, test:16/integer-unit:1 >>", integer);
		assertExtractTest("CA", "<< test:8/integer, bar:8/integer, n:13/integer-unit:1, x:3/integer-unit:1 >>", integer);
		assertExtractTest("FE", "<< foo:8/integer, test:8/integer, n:13/integer-unit:1, x:3/integer-unit:1 >>", integer);
		assertExtractTest("BA", "<< foo:8/integer, bar:8/integer, test:13/integer-unit:1, x:3/integer-unit:1 >>", integer);
		assertExtractTest("06", "<< foo:8/integer, bar:8/integer, n:13/integer-unit:1, test:3/integer-unit:1 >>", integer);
	}
	
	@Test
	public void testNumericFloat() throws IOException {
		byte[] integer = fromHex("CAFEBABE");
		assertExtractTest("CA", "<< test:8/float, foo:8/float, 16/float-unit:1 >>", integer);
		assertExtractTest("CA", "<< test:8/float, foo:8/float, baz/float >>", integer);
		assertExtractTest("CA", "<< test:8/float, foo:8/float, bar:16/float >>", integer);
		assertExtractTest("FE", "<< foo:8/float, test:8/float, bar:16/float >>", integer);
		assertExtractTest("BABE", "<< foo:8/float, bar:8/float, test:16/float >>", integer);
		assertExtractTest("0F", "<< foo:8/float, test:1/float-unit:4, _:4/unit:1, bar:16/float >>", integer);
		assertExtractTest("0F", "<< foo:8/float, test:4/float-unit:1, _:4/unit:1, bar:16/float >>", integer);
		assertExtractTest("0F", "<< foo:8/float, test:4/float-unit:1, x:16/float-unit:1 >>", integer);
		assertExtractTest("BABE", "<< foo:8/float, bar:8/float, test:16/float-unit:1 >>", integer);
		assertExtractTest("CA", "<< test:8/float, bar:8/float, n:13/float-unit:1, x:3/float-unit:1 >>", integer);
		assertExtractTest("FE", "<< foo:8/float, test:8/float, n:13/float-unit:1, x:3/float-unit:1 >>", integer);
		assertExtractTest("BA", "<< foo:8/float, bar:8/float, test:13/float-unit:1, x:3/float-unit:1 >>", integer);
		assertExtractTest("06", "<< foo:8/float, bar:8/float, n:13/float-unit:1, test:3/float-unit:1 >>", integer);
	}

	@Test
	public void testNumericDouble() throws IOException {
		byte[] integer = fromHex("CAFEBABE");
		assertExtractTest("CA", "<< test:8/double, foo:8/double, bar:16/double >>", integer);
		assertExtractTest("FE", "<< foo:8/double, test:8/double, bar:16/double >>", integer);
		assertExtractTest("BABE", "<< foo:8/double, bar:8/double, test:16/double >>", integer);
		assertExtractTest("0F", "<< foo:8/double, test:1/double-unit:4, _:4/unit:1, bar:16/double >>", integer);
		assertExtractTest("0F", "<< foo:8/double, test:4/double-unit:1, _:4/unit:1, bar:16/double >>", integer);
		assertExtractTest("0F", "<< foo:8/double, test:4/double-unit:1, x:16/double-unit:1 >>", integer);
		assertExtractTest("BABE", "<< foo:8/double, bar:8/double, test:16/double-unit:1 >>", integer);
		assertExtractTest("CA", "<< test:8/double, bar:8/double, n:13/double-unit:1, x:3/double-unit:1 >>", integer);
		assertExtractTest("FE", "<< foo:8/double, test:8/double, n:13/double-unit:1, x:3/double-unit:1 >>", integer);
		assertExtractTest("BA", "<< foo:8/double, bar:8/double, test:13/double-unit:1, x:3/double-unit:1 >>", integer);
		assertExtractTest("06", "<< foo:8/double, bar:8/double, n:13/double-unit:1, test:3/double-unit:1 >>", integer);
	}
	
	@Test
	public void testQuotedString() throws IOException {
		byte[] integer = fromHex(toHex("be") + toHex("beep") + toHex("ep"));
		assertExtractTest("beep".getBytes(), "<< \"be\", test:32, \"ep\" >>", integer);
	}

	@Test
	public void testSkippable() throws IOException {
		byte[] integer = fromHex("CAFEBABE");
//		assertExtractTest("CA", "<< test:8/double, _:8/double, bar:16/double >>", integer);
//		assertExtractTest("FE", "<< _:8/double, test:8/double, bar:16/double >>", integer);
//		assertExtractTest("BABE", "<< _:8/double, _:8/double, test:16/double >>", integer);
//		assertExtractTest("0F", "<< foo:8/double, test:1/double-unit:4, _:4/unit:1, _:16/double >>", integer);
//		assertExtractTest("0F", "<< foo:8/double, test:4/double-unit:1, _:4/unit:1, bar:16/double >>", integer);
	}

	private void assertExtractTest(byte[] expected, String source, byte[] payload) throws IOException {
		assertExtractTest(toHex(expected), source, payload);
	}

	private void assertExtractTest(String expected, String source, byte[] payload) throws IOException {
		BitPattern bp = BitSyntax.compile(source);
		Map<String,byte[]> map = bp.extract(payload);
		assertEquals(expected, toHex(map.get("test")));
		System.out.println("Expansion: " + bp.expansion());
		BitPattern bpe = BitSyntax.compile(bp.expansion());
		System.out.println("Expansion: " + bp.expansion());
		Map<String,byte[]> map2 = bp.extract(payload);
		assertEquals(expected, toHex(map2.get("test")));
	}
	
	private void assertMatchTest(Map<Integer,byte[]> expected, String source, byte[] payload) throws IOException {
		BitPattern bp = BitSyntax.compile(source);
		Map<String,byte[]> actual = bp.fixed(payload);
		assertEquals(expected, actual);
	}

}
