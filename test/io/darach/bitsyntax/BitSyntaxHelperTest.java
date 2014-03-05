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

import java.io.ByteArrayInputStream;
import java.io.IOException;

import io.darach.bitsyntax.BitSyntaxParser.SegmentContext;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.Test;

import static io.darach.bitsyntax.BitHelper.*;

public class BitSyntaxHelperTest {

	@Test
	public void testBits() throws IOException {
		// Default integer size is 8 bits
		SegmentContext sc = toSegment("<<id:1/integer>>");
		assertEquals("1", BitSyntaxHelper.bits(sc));

		// Default float size is 32 bits
		sc = toSegment("<<2.0e/float>>");
		// TODO assertEquals("32", BitSyntaxHelper.bits(sc));

		// Default double size is 64 bits
		sc = toSegment("<<3.14e10/double>>");
		// TODO assertEquals("64", BitSyntaxHelper.bits(sc));

		// Default "foo" is 1 byte per character, so for 3 bytes is 24 bits total
		sc = toSegment("<<\"foo\">>");
		assertEquals("24", BitSyntaxHelper.bits(sc));
		
	}

	private SegmentContext toSegment(final String s) throws IOException {
		// Convert string to stream
		final ByteArrayInputStream bais = new ByteArrayInputStream(s.getBytes());
        final ANTLRInputStream input = new ANTLRInputStream(bais);
        
        // Lex bit syntax expression
        final BitSyntaxLexer lexer = new BitSyntaxLexer(input);
        final CommonTokenStream tokens = new CommonTokenStream(lexer);
        
        BitSyntaxParser parser = new BitSyntaxParser(tokens);
        
        SegmentContext sc = parser.segments().segment(0);
		return sc;
	}
}
