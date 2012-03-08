/*
 * Copyright (c) 2011-2012 Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010 Remko Tronçon.
 * All rights reserved.
 */
package com.isode.stroke.tls;

import com.isode.stroke.base.ByteArray;
import com.isode.stroke.base.NotNull;

public class PKCS12Certificate extends CertificateWithKey {

    public PKCS12Certificate() {
    }

    /**
     * Construct a new object.
     * @param filename the name of the P12 file, must not be null.
     * @param password the password for the P12 file. Must not be null,
     * but may be empty if no password is to be used.
     */
    public PKCS12Certificate(String filename, char[] password) {

        NotNull.exceptIfNull(filename,"filename"); 
        NotNull.exceptIfNull(password,"password"); 
        filename_ = filename;
        password_ = new char[password.length];
        System.arraycopy(password,0,password_,0,password.length);
        data_ = new ByteArray();
        data_.readFromFile(filename);
    }

    public boolean isNull() {
        return data_.isEmpty();
    }
    
    public boolean isPrivateKeyExportable() {
    /////Hopefully a PKCS12 is never missing a private key
        return true;
    }

    /**
     * This returns the name of the P12 file.
     * @return the P12 filename, never null.
     */
    public String getCertStoreName() {
        return filename_;
    }

    public String getCertName() {
        /* TODO */
        return null;
    }


    public ByteArray getData() {
        return data_;
    }

    public void setData(ByteArray data) {
        data_ = data;
    }

    /**
     * Returns a reference to the password in this object. If {@link #reset()} 
     * has been called, then the method will return an empty array.
     * @return the password for this object.
     */
    public char[] getPassword() {
        return password_;
    }
    @Override
    public String toString() {
        return "PKCS12Certificate based on file " + filename_;
    }
    
    /**
     * This method may be used once the PKCS12Certificate is no longer 
     * required, and will attempt to clear the memory containing the
     * password in this object. After calling this method, you should 
     * not expect this object to be usable for subsequent authentication.
     * 
     * <p>Note that this operation does <em>NOT</em> guarantee that all traces 
     * of the password will have been removed from memory.
     */
    public void reset() {
        if (password_ != null) {
            for (int i=0; i<password_.length; i++) {
                password_[i] = 'x';
            }
        }
        password_ = new char[] {};
        
    }
    
    @Override
    protected void finalize() {
        reset();
    }
    
    private ByteArray data_;
    private char[] password_;
    private String filename_;
}
