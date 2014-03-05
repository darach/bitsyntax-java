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

import java.util.Map;

import static io.darach.bitsyntax.BitSyntaxParser.*;
import static io.darach.bitsyntax.BitSyntaxHelper.*;


/**
 * The <code>BitSyntaxExpandoVisitor</code> visits a bit syntax
 * ANTLR v4 AST and expands metadata to a formal descriptive
 * string useful for debugging purposes.
 * 
 */
public class BitSyntaxExpandoVisitor implements BitSyntaxVisitor {
	private String meta = "";
	
	public void setPackageName(String string) {
		// Ignore
	}

	public void setSource(String text) {
		// Ignore
	}

	public void setClassName(String string) {
		// Ignore
	}

	public void setBinding(Map<String, Integer> binding) {
		// Ignore
	}

	public void prologue() {
		meta += "<<";
	}

	public void skipSegment(SegmentContext segment) {
		if (!"<<".equals(meta)) {
			meta += ",";
		}
		meta += "\n  _:" + bits(segment) + segmentSpecification(segment);
	}

	@Override
	public void numberSegment(SegmentContext segment) {
		if (!"<<".equals(meta)) {
			meta += ",";
		}
		meta += "\n  " + label(segment) + ":" + bits(segment) + segmentSpecification(segment);
	}

	@Override
	public void binarySegment(SegmentContext segment) {
		if (!"<<".equals(meta)) {
			meta += ",";
		}
		meta += "\n  " + label(segment) + ":"  + bits(segment) + segmentSpecification(segment);
	}

	@Override
	public void stringSegment(SegmentContext segment) {
		if (!"<<".equals(meta)) {
			meta += ",";
		}
		meta += "\n  " + segment.QS().getText() + ":" + (segment.QS().getText().length() - 2);
	}

	private String segmentSpecification(SegmentContext segment) {
		return "/" + typeof(segment).name().toLowerCase() +
			"-" + signedness(segment).name().toLowerCase() +
			"-" + endianess(segment).toString().toLowerCase() +
			"-" + unit(segment).toString().toLowerCase();
	}
	
	@Override
	public void epilogue() {
		meta += "\n>>";
	}

	public String meta() {
		return meta;
	}
}
