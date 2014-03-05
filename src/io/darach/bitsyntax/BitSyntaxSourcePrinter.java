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

import io.darach.bitsyntax.BitSyntax.SegmentType;
import io.darach.bitsyntax.BitSyntaxParser.SegmentContext;
import static io.darach.bitsyntax.BitSyntaxHelper.*;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class BitSyntaxSourcePrinter implements BitSyntaxVisitor {
	private static AtomicLong counter = new AtomicLong();
	private final long invocationCount = counter.incrementAndGet();
	private String sourceText;
	
	// defaults
	private String packageName = "bitsyntax.generated";
	private String clazzNameStub = "Expr";
	private String clazzName;
	
	// result
	private String clazzText = "";
	
	public void setPackageName(final String packageName) {
		this.packageName = packageName;
	}

	public void setSource(final String sourceText) {
		this.sourceText = sourceText;
	}

	public void setClassName(String clazzNameStub) {
		this.clazzNameStub = clazzNameStub;
		clazzName = this.clazzNameStub + invocationCount;
	}

	public void setBinding(Map<String, Integer> binding) {
		// Not needed
	}

	public void prologue() {
    	clazzText += "package " + packageName + ";\n" +
    	"\n" +
    	"import io.darach.bitsyntax.AbstractBitPattern;\n" +
    	"import io.darach.bitsyntax.BitHelper;\n" +
    	"import io.darach.bitsyntax.BitSyntaxException;\n" +
    	"\n" +
    	"import org.objectweb.asm.util.ASMifier;\n" +
    	"\n"+
    	"import java.util.HashMap;\n" +
    	"import java.util.Map;\n" +
    	"\n" +
    	"\n" +
    	"public final class " + clazzName + " extends AbstractBitPattern {\n" +
    	"\n" +
    	"    private static final String SOURCE = \"" + sourceText + "\";\n" +
    	"\n" +
    	"    public Map<String,byte[]> generated(byte[] binary, Map<String,Integer> vars) {\n" +
		"        Map<String,byte[]> bindings = new HashMap<String,byte[]>();\n" +
	    "        scope = (vars != null) ? vars : new HashMap<String,Integer>();\n" +
	    "        int offset = 0, mark = 0;\n" +
	    "        int total_size_in_bits = binary.length * 8;\n" +
	    "        byte[] result = null;\n" +
	    "\n\n";
	}

	@Override
	public void skipSegment(SegmentContext segment) {
    	SegmentType type = typeof(segment);
        clazzText += "        // " + segment.getText() + "\n";
        clazzText += "        //\n";
        if (SegmentType.String.equals(type)) {
            // Damn. Have to look up the size.
            String lookup = "    var skipbits = " + sizeof(segment.size()) + " * " + unit(segment) + ";\n";
            String test = "    if (offset + skipbits > total_size_in_bits) { throw new BitSyntaxException(\"Actual vs expected size mismatch\"); }\n" + "else { offset += skipbits; }\n";
            clazzText += lookup + test;
        } else if (SegmentType.Binary.equals(type)) {
            clazzText += "    if (offset % 8 === 0) { offset = total_size_in_bits; }\n" + "else { throw new BitSyntaxException(\"Actual vs expected size mismatch\"); }\n";
        } else {
            String bits = sizeof(segment.size());
            clazzText += "    if (offset + " + bits + " > total_size_in_bits) { throw new BitSyntaxException(\"Actual vs expected size mismatch\"); }\n" + "else { offset += " + bits + "; }\n";
        }
	}

	@Override
	public void numberSegment(SegmentContext segment) {
    	final SegmentType type = typeof(segment);
        final String endianness = "ByteOrder." + endianess(segment).toString();
        final String signedness = signedness(segment).name();
        final String parser = SegmentType.Integer.equals(type) ? "Long" : type.name();
        clazzText += "        // " + segment.getText() + "\n";
        clazzText += "        //\n";
        clazzText += "        result = null;\n";
        clazzText += "        mark = offset;\n";
        clazzText += "        offset += " + bits_expr(segment) + ";\n";
        clazzText += "        if (offset > total_size_in_bits) { throw new BitSyntaxException(\"Actual vs expected size mismatch\"); }\n";
        clazzText += "        else {\n";
        clazzText += "        	result = BitHelper.bitgrok(binary, mark, offset - mark);\n";
        clazzText += "        //        	BitHelper.as" + parser + "(" + endianness + ", result); // " + signedness + "\n";
        clazzText += "        }\n";
        if (hasLabel(segment)) {
        	// extract
        	clazzText += "        bindings.put(\"" + label(segment) + "\",result);\n" ; // "else if (result != " + repr + ") { return false; }\n";
        }
	}
	
    private static String bits_expr(BitSyntaxParser.SegmentContext segment) {
    	if (segment.QS() != null) {
            return segment.size().getText().substring(1) + " * " + segment.specifiers();
        }
        return sizeof(segment.size());
    }

    private static String sizeof(BitSyntaxParser.SizeContext size) {
    	if (size == null) {
    		return "1";
    	}
    	if (size.NM() != null) {
    		return size.NM().getText();
    	} 
    	if (size.ID() != null) {
			return size.ID().getText();
    	}
    	return "1";
    }

	public void binarySegment(SegmentContext segment) {
		clazzText += "        // " + segment.getText() + "\n";
		clazzText += "        //\n";        
		clazzText += "        result = null;\n";
		clazzText += "        mark = offset;\n";
	    if (segment.size() != null) {
	    	if (segment.size().NM() != null) {
	    		clazzText += "        offset += " + size(segment) + " * 8;\n";
	    	} else {
	    		clazzText += "        offset += scope.get(\"" + size(segment) + "\") * 8;\n";
	    	}
		    clazzText += "        if (offset > total_size_in_bits) { throw new BitSyntaxException(\"Actual vs expected size mismatch\"); }\n";
		    clazzText += "        else {\n";
		    clazzText += "        	result = BitHelper.bitgrok(binary, mark, offset - mark);\n";
		    clazzText += "        }\n";
	    } else {
	    	// @TODO confirm last segment only
	    	clazzText += "        offset = total_size_in_bits;\n";
	    	clazzText += "        result = BitHelper.bitgrok(binary, mark, offset - mark);\n";
//            clazzText += "        bindings.put(\"" + label(segment) + "\",result);\n";
	    }
        if (hasLabel(segment)) {
        	// extract
        	clazzText += "        bindings.put(\"" + label(segment) + "\",result);\n" ; // "else if (result != " + repr + ") { return false; }\n";
        }
	}

	public void stringSegment(SegmentContext segment) {
        final int sizeBytes = segment.QS().getText().length() - 2;
        final int sizeBits = sizeBytes << 3;

        clazzText += "        // " + segment.getText() + "\n";
        clazzText += "        //\n";        
        clazzText += "        result = null;\n";
        clazzText += "        mark = offset;\n";
        clazzText += "        offset += " + sizeBits + ";\n";
        clazzText += "        if (offset > total_size_in_bits) { throw new BitSyntaxException(\"Actual vs expected size mismatch\"); }\n";
        clazzText += "        else {\n";
        clazzText += "        	result = BitHelper.bitgrok(binary, mark, offset - mark);\n";
        clazzText += "        }\n";
        if (hasLabel(segment)) {
        	// extract
        	clazzText += "        bindings.put(\"" + label(segment) + "\",result);\n" ; // "else if (result != " + repr + ") { return false; }\n";
        }
    	clazzText += "        System.out.println(\"%s: %s\\n\", string, new String(result));\n";
    }

	public void epilogue() {
        clazzText += "        if (offset == total_size_in_bits) {\n";
        clazzText += "            return bindings;\n";
        clazzText += "        } else {\n";
        clazzText += "            throw new BitSyntaxException(\"Actual vs expected size mismatch\");\n";
        clazzText += "        }\n";
        clazzText += "    }\n";
        clazzText += "\n";
        clazzText += "    public String specification() {\n";
        clazzText += "        return SOURCE;\n";
        clazzText += "    }\n";
        clazzText += "\n";
        clazzText += "    public void debug() throws Exception {\n";
        clazzText += "        ASMifier.main(new String[] { getClass().getName() });\n";
        clazzText += "    }\n";
        clazzText += "}\n\n";
	}
	
	@Override 
	public String toString() {
		return clazzText;
	}
}
