/*
 * Copyright (c) 2010 Remko Tronçon
 * Licensed under the GNU General Public License v3.
 * See Documentation/Licenses/GPLv3.txt for more information.
 */
/*
 * Copyright (c) 2010, Isode Limited, London, England.
 * All rights reserved.
 */
package com.isode.stroke.network;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class HostAddress {

    public HostAddress() {
        address_ = null;
    }

    public HostAddress(String address) {
        try {
            address_ = InetAddress.getByName(address);
        }
        catch (UnknownHostException e) {
            address_ = null;
        }
    }

    public HostAddress(InetAddress address) {
        address_ = address;
    }
    
    public HostAddress(final char[] address, int length) {
        try {
            assert(length == 4 || length == 16);
            byte[] data = new byte[length];
            if (length == 4) {
                for (int i = 0; i < length; ++i) {
                    data[i] = (byte)address[i];
                }
            }
            else {
                for (int i = 0; i < length; ++i) {
                    data[i] = (byte)address[i];
                }
            }
            address_ = InetAddress.getByAddress(data);
        } catch (UnknownHostException e) {
            address_ = null;
        }
    }
    

    @Override
    public String toString() {
        // toString() should always be callable without risking 
        // NullPointerException
        if (address_ == null) {
            return "<no address>";
        }
        return address_.getHostAddress();
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + (this.address_ != null ? this.address_.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof HostAddress) {
            return address_.equals(((HostAddress)other).getInetAddress());
        }
        return false;
    }

    public boolean isValid() {
        return address_ != null;
    }

    public InetAddress getInetAddress() {
        return address_;
    }
    
    private InetAddress address_;
}
