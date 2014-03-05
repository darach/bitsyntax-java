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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractBitPattern implements BitPattern {
	private static final Map<String,Integer> EMPTY_MAP = Collections.unmodifiableMap(new HashMap<String, Integer>());
	protected Map<String,Integer> scope;

	public AbstractBitPattern() { }
	
	public abstract Map<String, byte[]> generated(byte[] binary, Map<String,Integer> sizes);

	/* (non-Javadoc)
	 * @see BitPattern#specification()
	 */
	@Override
	public abstract String specification();
	
	/* (non-Javadoc)
	 * @see BitPattern#extract(byte[], java.util.Map)
	 */
	@Override
	public Map<String, byte[]> extract(byte[] binary,
			Map<String, Integer> sizes) throws BitSyntaxException {
		return generated(binary, sizes);
	}

	/* (non-Javadoc)
	 * @see BitPattern#extract(byte[])
	 */
	@Override
	public Map<String, byte[]> extract(byte[] binary)
			throws BitSyntaxException {
		return extract(binary, EMPTY_MAP);
	}

	/* (non-Javadoc)
	 * @see BitPattern#bind(java.util.Map)
	 */
	@Override
	public BitPattern bind(Map<String, Integer> sizes)
			throws BitSyntaxException {
		return null;
	}
	
	public abstract void debug() throws Exception;
}