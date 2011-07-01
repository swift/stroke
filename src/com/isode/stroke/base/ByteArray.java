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
        data_ = new byte[b.length];
        System.arraycopy(b, 0, data_, 0, b.length);
    }

    /*public ByteArray(char[] c, int n) {
        for (int i = 0; i < n; i++) {
            append(c[i]);
        }
    }*/

    /**
     * These are the raw, modifyable data!
     * @return
     */
    public byte[] getData() {
        return data_;
    }

    public int getSize() {
        return data_.length;
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
        int newLength = data_.length + b.length;
        byte[] newData = new byte[newLength];
        for (int i = 0; i < data_.length; i++) {
            newData[i] = data_[i];
        }
        for (int i = 0; i < b.length; i++) {
            newData[i + data_.length] = b[i];
        }
        data_ = newData;
        return this;
    }

    /** Mutable add */
    public ByteArray append(byte b) {
        byte[] bytes = {b};
        append(bytes);
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
            return new String(data_, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            throw new IllegalStateException("JVM has no 'UTF-8' encoding");
        }
    }

    public void readFromFile(String file) {
        //FIXME: port
    }

    public void clear() {
        data_ = new byte[]{};
    }
    private byte[] data_ = {};

}
