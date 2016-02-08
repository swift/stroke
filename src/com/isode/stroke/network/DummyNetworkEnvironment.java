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

import java.util.Vector;

/**
 * Dummy {@link NetworkEnvironment} for testing, returns an empty vector
 * of {@link NetworkInterface}
 */
public class DummyNetworkEnvironment extends NetworkEnvironment {

    /**
     * Constructor
     */
    public DummyNetworkEnvironment() {
        // Empty Constructor
    }

    @Override
    public Vector<NetworkInterface> getNetworkInterfaces() {
        return new Vector<NetworkInterface>();
    }

}
