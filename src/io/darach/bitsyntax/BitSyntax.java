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

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.antlr.v4.runtime.*;

import static io.darach.bitsyntax.BitSyntaxHelper.*;

public class BitSyntax {
	public static enum SegmentType {
    	Binary,
    	Integer,
    	Float,
    	Double,
    	String
    };
    
    public static enum Signedness {
    	Signed,
    	Unsigned
    };
    
    public static enum Endianness {
    	Big,
    	Little
    };
    
    public static class Unit {
    	public static Unit ONE = make(1);
    	public static Unit EIGHT = make(8);
		public final int size;
		public Unit(int size) {
    		this.size = size;
    	}
		public static Unit make(int sizeInBits) {
			return new Unit(sizeInBits);
		}
		public String toString() {
			return "Unit:" + size;
		}
    }

    private static void visit(BitSyntaxParser.SegmentContext segment, BitSyntaxVisitor v) {
    	if (isSkip(segment)) {
    		v.skipSegment(segment);
    		return;
    	}
        switch(typeof(segment)) {
        case Integer: 
        case Float:
        case Double: v.numberSegment(segment); break;
        case Binary: v.binarySegment(segment); break;
        case String: v.stringSegment(segment); break;
        }
    }

    public static BitSyntaxVisitor visit(BitSyntaxParser.BinaryContext binary, BitSyntaxVisitor v) {
        final BitSyntaxParser.SegmentsContext segments = binary.segments();

        // Consider hoisting to visitor backend specific
        v.setSource(binary.getText().replace("\"", "\\\""));

        v.prologue();

        // For each segment
        if (segments != null) {
            for (BitSyntaxParser.SegmentContext segment : segments.segment()) {
                visit(segment, v);
            }
        }

        v.epilogue();
                
        return v;
    }

	public static BitPattern compile(String source) throws IOException {
		return compile("bitsyntax.generated", "Expr", source);
	}
	
	public static BitPattern compile( String packageName, String className, String source) throws IOException {
		// Convert string to stream
		final ByteArrayInputStream bais = new ByteArrayInputStream(source.getBytes());
        final ANTLRInputStream input = new ANTLRInputStream(bais);
        
        // Lex bit syntax expression
        final BitSyntaxLexer lexer = new BitSyntaxLexer(input);
        final CommonTokenStream tokens = new CommonTokenStream(lexer);

        // Parser lexed tokens into an AST
        final BitSyntaxParser parser = new BitSyntaxParser(tokens);
        final BitSyntaxParser.BinaryContext binary = parser.binary();

        final BitSyntaxExpandoVisitor visitor0 = new BitSyntaxExpandoVisitor();
        final BitSyntaxSourcePrinter visitor1 = new BitSyntaxSourcePrinter();
        final BitSyntaxBytecodePrinter visitor2 = new BitSyntaxBytecodePrinter();

        // Extract Segments from abstract syntax tree
        
        visitor0.setSource(source);
        visitor0.setPackageName(packageName);
        visitor0.setClassName(className);
        visitor1.setSource(source);
        visitor1.setPackageName(packageName);
        visitor1.setClassName(className);
        visitor2.setSource(source);
        try {
        	visit(binary,visitor0);
        } catch(Exception e) {
        	e.printStackTrace();
        }
        visitor2.setExpansion(visitor0.meta());
        visitor2.setPackageName(packageName);
        visitor2.setClassName(className);
        visit(binary,visitor1);
        visit(binary,visitor2);

        try {
			return visitor2.load();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
