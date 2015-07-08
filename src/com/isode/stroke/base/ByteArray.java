/*
 * Copyright (c) 2010 Remko Tronçon
 * All rights reserved.
 */
/*
 * Copyright (c) 2010-2015, Isode Limited, London, England.
 * All rights reserved.
 */
package com.isode.stroke.base;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Maintains an arbitrarily long array of bytes.
 */
public class ByteArray {

    /**
     * Construct a new, empty ByteArray that contains no data.
     */
    public ByteArray() {
    }

    /**
     * Constructs a new ByteArray from the bytes corresponding to encoding
     * the provided String as UTF-8.
     * <p> Note that this should only be used for 'real' Strings (e.g. not
     * for TLS data)
     *  
     * @param s String, must not be null.
     */
    public ByteArray(String s) {
        try {
            appendInternal(s.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException ex) {
            throw new IllegalStateException("JVM has no 'UTF-8' encoding");
        }
    }

    /**
     * Constructs a new ByteArray containing the bytes in a user-supplied
     * byte[]
     * @param c an array of bytes, which must not be null, but may contain
     * zero elements.
     */
    public ByteArray(byte[] c) {
        append(c);
    }

    /**
     * Constructs a new ByteArray object by performing a deep copy of the 
     * contents of an existing ByteArray
     * @param b another ByteArray; must not be null
     */
    public ByteArray(ByteArray b) {
        append(b);
    }

    /*public ByteArray(char[] c, int n) {
        for (int i = 0; i < n; i++) {
            append(c[i]);
        }
    }*/

    /**
     * Returns a byte[] containing the contents of this object.
     * @return array copy of internal data, will never be null, but may
     * contain zero elements.
     */
    public synchronized byte[] getData() {
        if (dataCopy_ == null) {
            if (data_.size() == 1) {
                dataCopy_ = data_.get(0);
            } else {
                dataCopy_ = new byte[getSize()];
                int pos = 0;
                for (byte[] chunk : data_) {
                    System.arraycopy(chunk, 0, dataCopy_, pos, chunk.length);
                    pos += chunk.length;
                }
            }
        }
        return dataCopy_;
    }

    /**
     * Returns the number of bytes in this ByteArray
     * @return number of bytes
     */
    public int getSize() {
        return dataSize_;
    }

    /**
     * Determines whether the ByteArray is empty
     * @return <em>true</em> if there are no elements in the ByteArray,
     * <em>false</em> otherwise.
     */
    public boolean isEmpty() {
        return getSize() == 0;
    }

    /*public void resize(size_t size) {
    return data_.resize(size);
    }*/
    /** 
     * Creates a new ByteArray object containing all 
     * the elements from two existing ByteArrays (immutable add).
     * 
     * @param a an existing ByteArray. Must not be null, but may be empty.
     * @param b an existing ByteArray. Must not be null, but may be empty.
     * @return a new ByteArray containing all the elements of <em>a</em>
     * followed by all the elements of <em>b</em>.
     */  
    public static ByteArray plus(ByteArray a, ByteArray b) {
        ByteArray x = new ByteArray(a);
        x.append(b);
        return x;
    }

    /* * Immutable add */
    /*public ByteArray plus(ByteArray a, char b) {
        ByteArray x = new ByteArray(a.getData());
        x.append(b);
        return x;
    }*/
    
    /**
     * Updates the ByteArray by adding all the elements
     * of another ByteArray to the end of the array (mutable add).
     * @param b an existing ByteArray. Must not be null, but may be empty
     * @return a reference to the updated object 
     */
    public synchronized ByteArray append(ByteArray b) {
        dataCopy_ = null; /* Invalidate cache */
        data_.addAll(b.data_);
        dataSize_ += b.getSize();
        return this;
    }

    /** 
     * Updates the ByteArray by adding all the bytes
     * in a byte[] to the end of the array (mutable add).  
     * 
     * @param b an array of bytes. Must not be null, but may contain zero
     * elements.
     * 
     * @return a reference to the updated object
     */
    public ByteArray append(byte[] b) {
        return append(b, b.length);
    }

    /** Mutable add */
    public ByteArray append(byte[] b, int len) {
        return appendInternal(Arrays.copyOf(b, len));
    }

    /* Does not copy data - all calls must provide a copy as necessary */
    private synchronized ByteArray appendInternal(byte[] b) {
        dataCopy_ = null; /* Invalidate cache */
        data_.add(b);
        dataSize_ += b.length;
        return this;
    }

    /** 
     * Updates the ByteArray by adding a single byte
     * value to the end of the array (mutable add).
     * @param b a single byte
     * @return a reference to the updated object
     */
    public ByteArray append(byte b) {
        return appendInternal(new byte[]{b});
    }

    /**
     * Updates the ByteArray by adding all the bytes
     * obtained by UTF-8 encoding the provided String to the end of the array (mutable add).
     * @param s a String that must not be null.
     * @return a reference to the updated object.
     */ 
    public ByteArray append(String s) {
        try {
            return appendInternal(s.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException ex) {
            throw new IllegalStateException("JVM has no 'UTF-8' encoding");
        }
    }

    @Override
    public synchronized int hashCode() {
        return Arrays.hashCode(getData());
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof ByteArray && Arrays.equals(getData(), ((ByteArray)other).getData());
    }

    /*public char charAt(int i) {
        return data_.charAt(i);
    }*/

    /*public const_iterator begin() const {
    return data_.begin();
    }

    public const_iterator end() const {
    return data_.end();
    }*/
    /**
     * Returns a String representation of this object by attempting to 
     * treat its component bytes as a UTF-8 String.  If the ByteArray is empty,
     * an empty String ("") will be returned. If the bytes inside the object
     * are not valid UTF-8, then the resultant String may not be very
     * user-friendly.
     * 
     * @return a String representation of the bytes inside this object
     */
    @Override
    public String toString() {
        try {
            return new String(getData(), "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            throw new IllegalStateException("JVM has no 'UTF-8' encoding");
        }
    }

    /**
     * Appends the contents of a file to the end of an existing ByteStream
     * object (mutable add).
     * 
     * @param file the name of the file to be read. If the file cannot be
     * opened for any reason, or if an error occurs during an attempt to
     * read it, then the contents of the ByteStream are left
     * unchanged (i.e. no exception is thrown).
     */
    public void readFromFile(String file) {
        FileInputStream fis = null;
        ByteArrayOutputStream bos = null;
        try {
            fis = new FileInputStream(file);
            bos = new ByteArrayOutputStream();
            while (fis.available() != 0) {
                bos.write(fis.read());
            }
            byte[] bytes = bos.toByteArray();
            appendInternal(bytes);
        }
        catch (FileNotFoundException e) {
            // Leave things as they were
            return;
        }
        catch (IOException e) {
            // Leave things as they were
            return;
        } finally {
            try {
                if(bos != null) bos.close();
            }
            catch (IOException e) {
                // Needs a catch clause
            }
            try {
                if(fis != null) fis.close();
            }
            catch (IOException e) {
                // Needs a catch clause
            }
        }
    }

    /**
     * Clears the contents of this ByteArray, leaving it with zero elements.
     */
    public synchronized void clear() {
        data_.clear();
        dataSize_ = 0;
        dataCopy_ = null;
    }
    
    private final ArrayList<byte[]> data_ = new ArrayList<byte[]>(1);
    private int dataSize_ = 0;
    private byte[] dataCopy_ = null;
}
