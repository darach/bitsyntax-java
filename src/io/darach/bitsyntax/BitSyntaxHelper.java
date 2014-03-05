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

import java.util.LinkedList;
import java.util.List;

import io.darach.bitsyntax.BitSyntax.Endianness;
import io.darach.bitsyntax.BitSyntax.SegmentType;
import io.darach.bitsyntax.BitSyntax.Signedness;
import io.darach.bitsyntax.BitSyntax.Unit;
import io.darach.bitsyntax.BitSyntaxParser.SegmentContext;
import io.darach.bitsyntax.BitSyntaxParser.SpecifierContext;

public class BitSyntaxHelper {
	private BitSyntaxHelper() { }
	
	public static SegmentType typeof(BitSyntaxParser.SegmentContext segment) {
    	if (segment.QS() != null) return SegmentType.String;
    	if (hasSpecifier(SegmentType.Binary, segment)) return SegmentType.Binary;
    	if (hasSpecifier(SegmentType.Float, segment)) return SegmentType.Float;
    	if (hasSpecifier(SegmentType.Double, segment)) return SegmentType.Double;
    	return SegmentType.Integer;
    }

    private static boolean hasSpecifier(SegmentType type, BitSyntaxParser.SegmentContext segment) {
    	if (segment.specifiers() == null) return false;
    	switch(type) {
    	case Float: { return hasSpecifier(type,segment.specifiers().specifier()); }
    	case Double: { return hasSpecifier(type,segment.specifiers().specifier()); }
    	case Binary: { return hasSpecifier(type,segment.specifiers().specifier()); }
    	case String: { return segment.QS() != null; }
    	case Integer: { return true; }
    	default: return false;
    	}
    }
    
    private static boolean hasSpecifier(SegmentType type,
			List<BitSyntaxParser.SpecifierContext> specifier) {
    	for(BitSyntaxParser.SpecifierContext s : specifier) {
    		if (SegmentType.Float.equals(type) && s.FT() != null) return true;
    		if (SegmentType.Double.equals(type) && s.DB() != null) return true;
    		if (SegmentType.Binary.equals(type) && s.BT() != null) return true;
    	}
    	return false;
	}

     public static Endianness endianess(BitSyntaxParser.SegmentContext segment) {
    	List<BitSyntaxParser.SpecifierContext> specifiers =  (segment.specifiers() == null) ? new LinkedList<BitSyntaxParser.SpecifierContext>() : segment.specifiers().specifier();
    	for (BitSyntaxParser.SpecifierContext specifier : specifiers) {
    		if (specifier.LE() != null) {
    			return Endianness.Little;
    		}
    		if (specifier.BE() != null) {
    			return Endianness.Big;
    		}
    	}

    	// Default
    	return Endianness.Big;
    }

    public static Signedness signedness(BitSyntaxParser.SegmentContext segment) {
    	List<BitSyntaxParser.SpecifierContext> specifiers =  (segment.specifiers() == null) ? new LinkedList<BitSyntaxParser.SpecifierContext>() : segment.specifiers().specifier();
    	for (BitSyntaxParser.SpecifierContext specifier : specifiers) {
    		if (specifier.ST() != null) {
    			return Signedness.Signed;
    		}
    		if (specifier.UT() != null) {
    			return Signedness.Unsigned;
    		}
    	}
    	
    	// Default, Signed (Java)
    	return Signedness.Signed;
    }

	public static Unit unit(SegmentContext segment) {
		if (segment.specifiers() != null) {
			final List<SpecifierContext> specifiers = segment.specifiers().specifier();
			for (SpecifierContext s : specifiers) {
				if (s.unit() != null) {
					return Unit.make(Integer.parseInt(s.unit().NM().getText()));
				}
			}
		}
		
		switch(typeof(segment)) {
		case Binary:
		case String: return Unit.EIGHT;
		default: return Unit.ONE;
		}
	}

	public static String bits(SegmentContext segment) {
		if (segment.size() == null && segment.QS() != null) {
			return "" + (segment.QS().getText().length()-2)*8;
		} 
		
		if (segment.size() == null){
			return "" + unit(segment).size;
		}

		// Check based on specified type
		if (segment.size() != null && segment.size().NM() != null) {
			return "" + Integer.parseInt(segment.size().NM().getText()) * unit(segment).size;
		}

		throw new BitSyntaxException("Invalid segment. Unable to determine size in bits");
	}

    public static boolean isSkip(BitSyntaxParser.SegmentContext segment) {
    	return (segment.ID() != null && "_".equals(segment.ID().getText()));
    }
    
    public static boolean hasLabel(BitSyntaxParser.SegmentContext segment) {
    	return segment.ID() != null && !isSkip(segment);
    }

    public static String label(BitSyntaxParser.SegmentContext segment) {
        return (hasLabel(segment)) ? segment.ID().getText() : null;
    }
    
    private static boolean hasSize(BitSyntaxParser.SegmentContext segment) {
    	return segment.size() != null && segment.size().ID() != null;
    }

    public static String size(BitSyntaxParser.SegmentContext segment) {
        return (hasLabel(segment) && hasSize(segment)) ? segment.size().ID().getText() : (segment.size().NM() != null) ? segment.size().NM().getText() : null;
    }    
}
