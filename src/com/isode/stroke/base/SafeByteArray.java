/*
 * Copyright (c) 2011-2015, Isode Limited, London, England.
 * All rights reserved.
 */
package com.isode.stroke.base;

import com.isode.stroke.base.SafeByteArray;
import com.isode.stroke.base.ByteArray;
import java.io.UnsupportedEncodingException;

/**
* It's currently not actually secure,
* and that we might consider if http://developer.android.com/reference/java/nio/ByteBuffer.html#allocateDirect(int) could help us in the future.
*/
public class SafeByteArray extends ByteArray {

	public SafeByteArray() {

	}

	public SafeByteArray(String s) {
		super(s);
	}

	public SafeByteArray(ByteArray b) {
		this.append(b.getData());
	}

	/**
	 * Constructs a new ByteArray containing the bytes in a user-supplied
	 * byte[]
	 * @param c an array of bytes, which must not be null, but may contain
	 * zero elements.
	 */
	public SafeByteArray(byte[] c) {
		super(c);
	}

	/**
	 * Creates a new SafeByteArray object containing all 
	 * the elements from two existing ByteArrays (immutable add).
	 * 
	 * @param a an existing SafeByteArray. Must not be null, but may be empty.
	 * @param b an existing SafeByteArray. Must not be null, but may be empty.
	 * @return a new SafeByteArray containing all the elements of <em>a</em>
	 * followed by all the elements of <em>b</em>.
	 */  
	public static SafeByteArray plus(SafeByteArray a, SafeByteArray b) {
		SafeByteArray x = new SafeByteArray().append(a.getData());
		x.append(b);
		return x;
	}
	
	/**
	 * Updates the SafeByteArray by adding all the elements
	 * of another SafeByteArray to the end of the array (mutable add).
	 * @param b an existing SafeByteArray. Must not be null, but may be empty
	 * @return a reference to the updated object 
	 */
	public SafeByteArray append(ByteArray b) {
		append(b.getData());
		return this;
	}

	/** 
	 * Updates the SafeByteArray by adding all the bytes
	 * in a byte[] to the end of the array (mutable add).  
	 * 
	 * @param b an array of bytes. Must not be null, but may contain zero
	 * elements.
	 * 
	 * @return a reference to the updated object
	 */
	public SafeByteArray append(byte[] b) {
		return append(b, b.length);
	}

	/** Mutable add */
	public SafeByteArray append(byte[] b, int len) {
		for (int i = 0; i < len; i++) {
			append(b[i]);
		}
		return this;
	}

	/** 
	 * Updates the SafeByteArray by adding a single byte
	 * value to the end of the array (mutable add).
	 * @param b a single byte
	 * @return a reference to the updated object
	 */
	public SafeByteArray append(byte b) {
		dataCopy_ = null; /* Invalidate cache */
		data_.add(Byte.valueOf(b));
		return this;
	}

	/**
	 * Updates the SafeByteArray by adding all the bytes
	 * obtained by UTF-8 encoding the provided String to the end of the array (mutable add).
	 * @param s a String that must not be null.
	 * @return a reference to the updated object.
	 */ 
	public SafeByteArray append(String s) {
		byte[] bytes;
		try {
			bytes = s.getBytes("UTF-8");
		} catch (UnsupportedEncodingException ex) {
			throw new IllegalStateException("JVM has no 'UTF-8' encoding");
		}
		append(bytes);
		return this;
	}
}