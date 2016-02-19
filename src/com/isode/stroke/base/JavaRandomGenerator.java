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

import java.util.Random;

/**
 * A {@link RandomGenerator} that generates integers with a uniform 
 * distribution (using the java {@link Random} class).
 */
public final class JavaRandomGenerator implements RandomGenerator {

    /**
     * {@link Random} to use to generate the numbers
     */
    private final Random rng = new Random();

    @Override
    public int generateRandomInteger(int max) {
        // Random.nextInt(bound) is exclusive of bound
        return rng.nextInt(max+1);
    }

}
