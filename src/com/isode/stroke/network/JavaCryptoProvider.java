/*
 * Copyright (c) 2011-2015 Isode Limited, London, England.
 * All rights reserved.
 */
package com.isode.stroke.network;

import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.isode.stroke.base.ByteArray;
import com.isode.stroke.crypto.CryptoProvider;
import com.isode.stroke.crypto.Hash;
import com.isode.stroke.base.SafeByteArray;

public class JavaCryptoProvider extends CryptoProvider {
    
    private static class HashProvider implements Hash {
        private final MessageDigest digest;
        
        HashProvider(String algorithm) throws NoSuchAlgorithmException {
            digest = MessageDigest.getInstance("SHA-1");
        }

        @Override
        public Hash update(ByteArray data) {
            digest.update(data.getData());
            return this;
        }

        @Override
        public Hash update(SafeByteArray data) {
//            digest.update(data.getData());
            return this;
        }

        @Override
        public ByteArray getHash() {
            return new ByteArray(digest.digest());
        }

    }

    @Override
    public Hash createSHA1() {
        try {
            return new HashProvider("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    @Override
    public Hash createMD5() {
        try {
            return new HashProvider("MD5");
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

//    @Override
    public ByteArray getHMACSHA1(SafeByteArray key, ByteArray data) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ByteArray getHMACSHA1(ByteArray key, ByteArray data) {
        Mac mac;
        try {
            mac = Mac.getInstance("HmacSHA1");
            mac.init(new SecretKeySpec(key.getData(), mac.getAlgorithm()));
            return new ByteArray(mac.doFinal(data.getData()));
        } catch (NoSuchAlgorithmException e) {
            return null;
        } catch (InvalidKeyException e) {
            return null;
        }
    }

    @Override
    public boolean isMD5AllowedForCrypto() {
        return false;
    }

}
