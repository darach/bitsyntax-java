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

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import org.junit.Test;

import static org.junit.Assert.*;
import static io.darach.bitsyntax.BitHelper.*;

public class BitHelperTest {

	@Test
	public void testLogicalShiftLeft() {
		// operations on 1 byte
		assertEquals("0F", toHex(shl(new byte[] { 0x0F }, 0)));
		assertEquals("1E", toHex(shl(new byte[] { 0x0F }, 1)));
		assertEquals("3C", toHex(shl(new byte[] { 0x0F }, 2)));
		assertEquals("F0", toHex(shl(new byte[] { 0x0F }, 4)));
		
		// overflow
		assertEquals("00", toHex(shl(new byte[] { 0x0F }, 8)));
		
		// operations on more than one byte, shifting less than one byte
		assertEquals("000F", toHex(shl(new byte[] { 0x00, 0x0F }, 0)));
		assertEquals("001E", toHex(shl(new byte[] { 0x00, 0x0F }, 1)));
		assertEquals("003C", toHex(shl(new byte[] { 0x00, 0x0F }, 2)));
		assertEquals("00F0", toHex(shl(new byte[] { 0x00, 0x0F }, 4)));
		
		// operations on more than one byte, shifting more than one byte
		assertEquals("0F00", toHex(shl(new byte[] { 0x00, 0x0F }, 8)));
		assertEquals("F000", toHex(shl(new byte[] { 0x00, 0x0F }, 12)));
		
		// overflow
		assertEquals("0000", toHex(shl(new byte[] { 0x00, 0x0F }, 16)));
	}
	
	@Test
	public void testLogicalShiftRight() {
		// operations on 1 byte
		assertEquals("F0", toHex(shr(new byte[] { (byte)0xF0 }, 0)));
		assertEquals("78", toHex(shr(new byte[] { (byte)0xF0 }, 1)));
		assertEquals("3C", toHex(shr(new byte[] { (byte)0xF0 }, 2)));
		assertEquals("0F", toHex(shr(new byte[] { (byte)0xF0 }, 4)));
		
		// underflow
		assertEquals("00", toHex(shr(new byte[] { (byte)0xF0 }, 8)));
		
		// operations on more than one byte, shifting less than one byte
		assertEquals("F000", toHex(shr(new byte[] { (byte)0xF0, 0x00 }, 0)));
		assertEquals("7800", toHex(shr(new byte[] { (byte)0xF0, 0x00 }, 1)));
		assertEquals("3C00", toHex(shr(new byte[] { (byte)0xF0, 0x00 }, 2)));
		assertEquals("0F00", toHex(shr(new byte[] { (byte)0xF0, 0x00 }, 4)));
		
		// operations on more than one byte, shifting more than one byte
		assertEquals("00F0", toHex(shr(new byte[] { (byte)0xF0, 0x00 }, 8)));
		assertEquals("000F", toHex(shr(new byte[] { (byte)0xF0, 0x00 }, 12)));
		
		// underflow
		assertEquals("0000", toHex(shr(new byte[] { (byte)0xF0, 0x00 }, 16)));
		
		// 1 bit shifts
		assertEquals("0A", toHex(shr(new byte[] { (byte)0xA0 }, 4)));
		assertEquals("05", toHex(shr(new byte[] { (byte)0xA0 }, 5)));
		assertEquals("02", toHex(shr(new byte[] { (byte)0xA0 }, 6)));
		assertEquals("01", toHex(shr(new byte[] { (byte)0xA0 }, 7)));
		assertEquals("00", toHex(shr(new byte[] { (byte)0xA0 }, 8)));		

	}
	
	@Test
	public void testBitGrok() {
		byte[] test = new byte[] { (byte)0xCA, (byte)0xFE, (byte)0xBA, (byte)0xBE };

		assertEquals("CAFE", toHex(bitgrok(test, 0, 16)));
		assertEquals("BABE", toHex(bitgrok(test, 16, 16)));
		assertEquals("FEBA", toHex(bitgrok(test, 8, 16)));
		assertEquals("AFEB", toHex(bitgrok(test, 4, 16)));
		assertEquals("AF", toHex(bitgrok(test, 4, 8)));
		assertEquals("0A", toHex(bitgrok(test, 4, 4)));
		assertEquals("02", toHex(bitgrok(test, 4, 2)));
		assertEquals("01", toHex(bitgrok(test, 4, 1)));
		assertEquals("", toHex(bitgrok(test, 4, 0)));
	}
	
	@Test
	public void testHexConversions() throws UnsupportedEncodingException {
		assertEquals("6265C3A970", toHex("beép")); // @NOTE default encoding is UTF-8
		assertEquals("6265C3A970", toHex("beép", Charset.forName("UTF-8")));
		assertEquals("62653F70", toHex("beép", Charset.forName("ASCII")));
		assertEquals("beép", new String(fromHex("6265C3A970"), "UTF-8"));
		assertEquals("be��p", new String(fromHex("6265C3A970"), "ASCII"));
	}
}
