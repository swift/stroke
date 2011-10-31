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

public class HostAddressPort {

    public HostAddressPort(HostAddress address) {
        address_ = address;
        port_ = -1;
    }

    public HostAddressPort(HostAddress address, int port) {
        address_ = address;
        port_ = port;
    }

    /*
    public 	HostAddressPort(const boost::asio::ip::tcp::endpoint& endpoint) {
    address_ = HostAddress(endpoint.address());
    port_ = endpoint.port();
    }*/ //FIXME
    public HostAddress getAddress() {
        return address_;
    }

    public int getPort() {
        return port_;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof HostAddressPort)) return false;
        HostAddressPort o = (HostAddressPort)other;
        return getAddress().equals(o.getAddress()) && port_ == o.getPort();
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 17 * hash + (this.address_ != null ? this.address_.hashCode() : 0);
        hash = 17 * hash + this.port_;
        return hash;
    }

    public boolean isValid() {
        return address_.isValid() && port_ > 0;
    }
    private HostAddress address_;
    private int port_;
}
