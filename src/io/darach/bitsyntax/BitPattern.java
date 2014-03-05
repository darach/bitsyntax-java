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

/**
 * The BitPattern defines operations on a compiled syntactic bit specification.
 * BitPattern's are generated via <code>BitSyntax.compile</code> from a textual domain
 * specific language derived from the Erlang/OTP bit syntax with some accommodations
 * for the JVM's builtin type system.
 * 
 */
public interface BitPattern {
	/**
	 * The <code>extract</code> is an analog of pattern matching in the Erlang
	 * programming language, expressed as an operation on a binary (byte array).
	 * 
	 * Patterns may define segments with variable sizes. It is an error to attempt
	 * to invoke a match on a pattern with one or many variable segments without
	 * providing the size of those variable segments. The specification allows variable
	 * segments to be named such that the size can be provided programatically on a
	 * per call basis.
	 * 
	 * Static segments are of fixed size and are validated against the specification
	 * unless the specification specifies them as skippable. A skippable segment is
	 * not validated.
	 * 
	 * Variable segments are extracted into an associative map if the match succeeds.
	 * 
	 * The last segment may be of arbitrary length. All other segments must have a
	 * length with an a priori known length.
	 * 
	 * @param binary	The binary to be matched and extracted
	 * @param sizes		A possibly null or empty map of variable segment size names and their respective sizes
	 * @return			The extracted values and associated names of variable segments
	 * @throws BitSyntaxException	Thrown if the binary does not match the bit pattern syntax specification
	 */
	public Map<String,byte[]> extract(final byte[] binary, final Map<String,Integer> sizes) throws BitSyntaxException;

	/**
	 * The <code>match</code> is an analog of pattern matching in the Erlang
	 * programming language, expressed as an operation on a binary (byte array).
	 * 
	 * Patterns may define segments with variable sizes. It is an error to attempt
	 * to invoke a match on a pattern with one or many variable segments without
	 * providing the size of those variable segments. The specification allows variable
	 * segments to be named such that the size can be provided programatically on a
	 * per call basis.
	 * 
	 * Static segments are of fixed size and are validated against the specification
	 * unless the specification specifies them as skippable. A skippable segment is
	 * not validated.
	 * 
	 * Variable segments are extracted into an associative map if the match succeeds.
	 * 
	 * The last segment may be of arbitrary length. All other segments must have a
	 * length with an a priori known length.
	 * 
	 * @param binary	The binary to be matched and extracted
	 * @param sizes		A possibly null or empty map of variable segment size names and their respective sizes
	 * @return			The extracted values and associated names of variable segments
	 * @throws BitSyntaxException	Thrown if the binary does not match the bit pattern syntax specification
	 */	
	public Map<String,byte[]> extract(final byte[] binary) throws BitSyntaxException;
	 
	// Returns a bit pattern with variable segment sizes bound and sealed
	public BitPattern bind(Map<String,Integer> sizes) throws BitSyntaxException;


	public Map<String, byte[]> fixed(byte[] payload) throws BitSyntaxException;

	/**
	 * The <code>specification</code> source this compiled artefact is an expression of
	 * 
	 * @return The bit syntax specification as text
	 */
	public String specification();
	
	/**
	 * The <code>expansion</code> is a compact representation of the internal normal form
	 * of the expression. The expansion expands any defaults and computes the effective size
	 * in bits of each segment.
	 * 
	 * @return The expanded form of the bit syntax specification as text
	 */
	public String expansion();

	public void debug() throws Exception;
}