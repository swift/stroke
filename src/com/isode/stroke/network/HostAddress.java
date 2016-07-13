/*
 * Copyright (c) 2010 Remko Tronçon
 * Licensed under the GNU General Public License v3.
 * See Documentation/Licenses/GPLv3.txt for more information.
 */
/*
 * Copyright (c) 2010-2016, Isode Limited, London, England.
 * All rights reserved.
 */
package com.isode.stroke.network;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Pattern;

public class HostAddress {

    public HostAddress() {
        address_ = null;
    }

    public HostAddress(String address) {
        // To match C++ code this should try and create a InetAddress
        // if and only if the input is a IPv4 or IPv6 address.
        if (isIPv4OrIPv6Address(address)) {
            try {
                address_ = InetAddress.getByName(address);
            }
            catch (UnknownHostException e) {
                address_ = null;
            }
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
    
    /**
     * Indicates if the value is an IPv4 address in dot notation or 
     * an IPv6 address in hexadecimal notation
     * @param value A string to test. If {@code null} result will
     * be false.
     * @return {@code true} if the string is an IPv4 address in dot
     * notation, or a IPv6 address in hexadecimal notation.
     */
    private static boolean isIPv4OrIPv6Address(String value) {
        if (value == null) {
            return false;
        }
        if (ipv4Pattern.matcher(value).matches()) {
            return true;
        }
        if (ipv6Pattern.matcher(value).matches()) {
            return true;
        }
        return false;
    }
    
    /**
     * Regular expression for IPv4 address.
     *
     * @see <a href="http://www.mkyong.com/regular-expressions/how-to-validate-ip-address-with-regular-expression/"> web link</a>
     */
    private static final String ipv4Regex =
            "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
            + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
            + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
            + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
    /**
     * A pattern that can be used to match an IPv4 address (e.g. "1.2.3.4").
     * 
     */
    private static final Pattern ipv4Pattern = Pattern.compile(ipv4Regex);
    
    /**
     * Regular expression for IPv6 address. Note that this needs to be
     * compiled with the CASE_INSENSITIVE option.
     */
    private static final String ipv6Regex =
            "^(((?=(?>.*?::)(?!.*::)))(::)?([0-9A-F]{1,4}::?){0,5}"
            + "|([0-9A-F]{1,4}:){6})(\\2([0-9A-F]{1,4}(::?|$)){0,2}"
            + "|((25[0-5]|(2[0-4]|1\\d|[1-9])?\\d)(\\.|$)){4}"
            + "|[0-9A-F]{1,4}:[0-9A-F]{1,4})(?<![^:]:|\\.)\\z";
    
    /**
     * A pattern that can be used to match an IPv6 address (e.g. "3ffe:1900:4545:3:200:f8ff:fe21:67cf").
     */
    private static final Pattern ipv6Pattern = Pattern.compile(ipv6Regex, Pattern.CASE_INSENSITIVE);
    
}
