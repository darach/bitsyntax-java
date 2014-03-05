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

import java.nio.charset.Charset;
import java.util.Arrays;

import javax.xml.bind.DatatypeConverter;

public class BitHelper {
	private static final byte[] EMPTY_BYTE_ARRAY = new byte[] {};
	
	private BitHelper() { }

	/**
	 * Given an array of bytes, a starting bit and a length in bits produce
	 * a result array sized to the nearest byte to accomodate the number of
	 * bits with the matching subset of bits in the host byte array.
	 * 
	 * @param host		The buffer from which to isolate bits of interest
	 * @param bitStart  The offset in bits that demarcates the 1st bit of interest
	 * @param numBits   The number of bits of interest
	 * @return			The bits of interest as a byte array
	 */
	public static byte[] bitgrok(final byte[] host, final int bitStart, final int numBits) {
		if (numBits == 0) return EMPTY_BYTE_ARRAY;

		final int bitEnd = bitStart + numBits;
		final int s = bitStart >> 3; 			// starting byte index
		final int e = (bitEnd >> 3) + 1; 		// ending byte index
		final int so = bitStart - (s << 3); 	// number of LR leading offset bits from starting byte to starting bit
		final int eo = bitEnd - (e << 3);   	// number of RL trailing offset bits from ending byte to ending bit

		byte[] r = Arrays.copyOfRange(host, s,  e);

		// iff byte boundary, we're done here
		if (so == 0 && eo == 0) return r;
		
		if (so > 0) r = shl(r, so);
		if (eo > 0) r = shr(r,eo);
		if (numBits / 8 == 0 && numBits % 8 > 0) r = shr(r, 8 - numBits);
		
		// Return a copy
		return Arrays.copyOf(r, (numBits / 8) > 0 ? (numBits / 8) : 1);
	}
	
	/**
	 * Logical shift left operation on a byte array by a number of bits
	 * 
	 * @param ba		The byte array to be operated on
	 * @param numBits	The number of bits to shift
	 * @return			The logically shifted byte array
	 */
	public static byte[] shl(final byte[] ba, final int numBits) {
		final int byteStart = numBits / 8;
		final int bitDiff = numBits % 8;

		// iff, shift bigger than byte array
		if (byteStart >= ba.length) {
			// zero
			for (int i = 0; i < ba.length; i++) ba[i] = 0;
			return ba; // early
		}
		
		// iff, byte shift
		if (byteStart > 0) {
			// shl bytes
			for (int i = 0, j = byteStart; j < ba.length; i++, j++) {
				ba[i] = ba[j];
			}
		}
		
		// iff, bit shift
		if (bitDiff > 0) {			
			if (ba.length == 1) {
				ba[0] = (byte) (ba[0] << bitDiff);
			} else {
				int i = 1;
				do {
					byte p = ba[i - 1];
					byte c = ba[i];
					ba[i - 1] = (byte)((p << bitDiff & 0xF0) | (c << bitDiff >> 8 & 0x0F));
				} while(i++ < ba.length - 1);
				ba[ba.length-1] = (byte)(ba[ba.length-1] << bitDiff);
			}
		}
		
		// zero rest
		for (int j = ba.length - byteStart; j < ba.length; j++) {
			ba[j] = 0;
		}

		return ba;
	}

	/**
	 * Logical shift right operation on a byte array by a number of bits
	 * 
	 * @param ba		The byte array to be operated on
	 * @param numBits	The number of bits to shift
	 * @return			The logically shifted byte array
	 */
	public static byte[] shr(final byte[] ba, final int numBits) {
		final int byteStart = numBits / 8;
		final int bitDiff = numBits % 8;
		final int byteEnd = byteStart;

		// iff, shift bigger than byte array
		if (byteStart >= ba.length) {
			// zero
			for (int i = 0; i < ba.length; i++) ba[i] = 0;
			return ba; // early
		}
		
		// iff, byte shift
		if (byteStart > 0) {
			// shr bytes
			for (int j = ba.length - 1; j >= byteStart; --j) {
				ba[j] = ba[j - byteStart];
			}
		}

		if (bitDiff > 0) {
			if (ba.length == 1) {
				ba[0] = (byte)((ba[0] & 0xFF) >> bitDiff);
			} else {
				int c = ba[ba.length - byteStart - 1] & 0xFF;
				for (int i = ba.length - 1; i > 0; i--) {
					int p = c;
					c = ba[ (ba.length + i - byteStart - 1) % ba.length ] & 0xFF;
					ba[i] = (byte)( (p >> bitDiff) | (c << 8 >> bitDiff));
				}
				ba[byteEnd] = (byte)(c >> bitDiff);
				
			}
		}

		// zero rest
		for (int i = 0; i < byteEnd; i++) {
			ba[i] = 0;
		}

		return ba;
	}
	
	public static String toHex(String s, Charset cs) {
		return toHex(s.getBytes(cs));
	}
	
	public static String toHex(String s) {
		return toHex(s.getBytes(Charset.forName("UTF-8")));
	}

	public static String toHex(byte[] bytes) {
		return DatatypeConverter.printHexBinary(bytes);
	}

	public static byte[] fromHex(String hexString) {
		return DatatypeConverter.parseHexBinary(hexString);
	}
}