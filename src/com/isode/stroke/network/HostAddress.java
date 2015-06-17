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

public class HostAddress {

    public HostAddress() {
        address_ = null;
    }

    public HostAddress(InetAddress address) {
        address_ = address;
    }
    /*			public HostAddress(const String&);
    public 			HostAddress(const unsigned char* address, int length);
    public 		HostAddress(const boost::asio::ip::address& address);*/

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
    
    private final InetAddress address_;
}
