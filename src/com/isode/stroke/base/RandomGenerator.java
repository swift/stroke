/*  Copyright (c) 2016, Isode Limited, London, England.
 *  All rights reserved.
 *
 *  Acquisition and use of this software and related materials for any
 *  purpose requires a written license agreement from Isode Limited,
 *  or a written license from an organisation licensed by Isode Limited
 *  to grant such a license.
 *
 */
package com.isode.stroke.base;

public interface RandomGenerator {

    /**
     * Generates a random integer between 0 and 'max',
     * 'max' inclusive.
     * @param max The maximum possible value 
     * to generate (inclusive)
     * @return A random integer between 0 and 'max' 
     */
    public int generateRandomInteger(int max);
    
}
