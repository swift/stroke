/*  Copyright (c) 2016, Isode Limited, London, England.
 *  All rights reserved.
 *
 *  Acquisition and use of this software and related materials for any
 *  purpose requires a written license agreement from Isode Limited,
 *  or a written license from an organisation licensed by Isode Limited
 *  to grant such a license.
 *
 */
package com.isode.stroke.network;

import java.net.SocketException;
import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Logger;

/**
 * Java implementation of {@link NetworkEnvironment}
 */
public class JavaNetworkEnviroment extends NetworkEnvironment {

    /**
     * Logger
     */
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    
    @Override
    public Vector<NetworkInterface> getNetworkInterfaces() {
        Vector<NetworkInterface> results = new Vector<NetworkInterface>();
        try {
            Enumeration<java.net.NetworkInterface> javaNIEnumeration = 
                    java.net.NetworkInterface.getNetworkInterfaces();
            if (javaNIEnumeration.hasMoreElements()) {
                java.net.NetworkInterface javaNI = javaNIEnumeration.nextElement();
                try {
                    NetworkInterface strokeNI = new NetworkInterface(javaNI);
                    results.add(strokeNI);
                } catch (SocketException e) {
                    logger.warning("Error determining if "+javaNI+
                            " is loopback : "+e.getMessage());
                }
                
            }
        } 
        catch (SocketException e) {
            logger.warning("Error occured when getting network interfaces - "+e.getMessage());
        }
        return results;
    }

}
