/*
 * Copyright (c) 2010 Remko Tronçon
 * Licensed under the GNU General Public License v3.
 * See Documentation/Licenses/GPLv3.txt for more information.
 */
/*
 * Copyright (c) 2010, Isode Limited, London, England.
 * All rights reserved.
 */
package com.isode.stroke.base;

import java.io.UnsupportedEncodingException;
import java.util.Vector;

/**
 *
 */
public class ByteArray {

    public ByteArray() {
    }

    public ByteArray(String s) {
        try {
            fromBytes(s.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException ex) {
            throw new IllegalStateException("JVM has no 'UTF-8' encoding");
        }
    }

    public ByteArray(byte[] c) {
        fromBytes(c);
    }

    public ByteArray(ByteArray b) {
        fromBytes(b.getData());
    }

    private void fromBytes(final byte[] b) {
        clear();
        append(b);
    }

    /*public ByteArray(char[] c, int n) {
        for (int i = 0; i < n; i++) {
            append(c[i]);
        }
    }*/

    /**
     * @return array copy of internal data.
     */
    public byte[] getData() {
        if (dataCopy_ == null) {
            dataCopy_ = new byte[getSize()];
            for (int i = 0; i < data_.size(); i++) {
                dataCopy_[i] = data_.get(i);
            }
        }
        return dataCopy_;
    }

    public int getSize() {
        return data_.size();
    }

    public boolean isEmpty() {
        return getSize() == 0;
    }

    /*public void resize(size_t size) {
    return data_.resize(size);
    }*/
    /** Immutable add */
    public static ByteArray plus(ByteArray a, ByteArray b) {
        ByteArray x = new ByteArray(a.getData());
        x.append(b);
        return x;
    }

    /** Immutable add */
    /*public ByteArray plus(ByteArray a, char b) {
        ByteArray x = new ByteArray(a.getData());
        x.append(b);
        return x;
    }*/

    /** Mutable add */
    public ByteArray append(ByteArray b) {
        append(b.getData());
        return this;
    }

    /** Mutable add */
    private ByteArray append(byte[] b) {
        for (int i = 0; i < b.length; i++) {
            append(b[i]);
        }
        return this;
    }

    /** Mutable add */
    public ByteArray append(byte b) {
        dataCopy_ = null; /* Invalidate cache */
        data_.add(b);
        return this;
    }

    /** mutable add */
    public ByteArray append(String s) {
        byte[] bytes;
        try {
            bytes = s.getBytes("UTF-8");
        } catch (UnsupportedEncodingException ex) {
            throw new IllegalStateException("JVM has no 'UTF-8' encoding");
        }
        append(bytes);
        return this;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + (this.data_ != null ? this.data_.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof ByteArray && toString().equals(other.toString());
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
    @Override
    public String toString() {
        try {
            return new String(getData(), "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            throw new IllegalStateException("JVM has no 'UTF-8' encoding");
        }
    }

    public void readFromFile(String file) {
        //FIXME: port
    }

    public void clear() {
        data_ = new Vector<Byte>();
        dataCopy_ = null;
    }
    Vector<Byte> data_ = new Vector<Byte>();
    byte[] dataCopy_ = null;
}
