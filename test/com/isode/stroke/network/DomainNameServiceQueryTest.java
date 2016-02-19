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

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.isode.stroke.base.RandomGenerator;
import com.isode.stroke.network.DomainNameServiceQuery.Result;

/**
 * Test for {@link DomainNameServiceQuery}
 */
public class DomainNameServiceQueryTest {

    private static class RandomGenerator1 implements RandomGenerator {

        @Override
        public int generateRandomInteger(int max) {
            return 0;
        }
        
    }
    
    public static class RandomGenerator2 implements RandomGenerator {

        @Override
        public int generateRandomInteger(int max) {
            return max;
        }
        
    }
    
    @Test
    public void testSortResults_Random1() {
        List<Result> results = new ArrayList<Result>(6);
        results.add(new Result("server1.com", 5222, 5, 1));
        results.add(new Result("server2.com", 5222, 3, 10));
        results.add(new Result("server3.com", 5222, 6, 1));
        results.add(new Result("server4.com", 5222, 3, 20));
        results.add(new Result("server5.com", 5222, 2, 1));
        results.add(new Result("server6.com", 5222, 3, 10));

        RandomGenerator generator = new RandomGenerator1();
        DomainNameServiceQuery.sortResults(results, generator);

        assertEquals("server5.com",results.get(0).hostname);
        assertEquals("server2.com",results.get(1).hostname);
        assertEquals("server4.com",results.get(2).hostname);
        assertEquals("server6.com",results.get(3).hostname);
        assertEquals("server1.com",results.get(4).hostname);
        assertEquals("server3.com",results.get(5).hostname);
    }
    
    @Test
    public void testSortResults_Random2() {
        List<Result> results = new ArrayList<Result>(7);
        results.add(new Result("server1.com", 5222, 5, 1));
        results.add(new Result("server2.com", 5222, 3, 10));
        results.add(new Result("server3.com", 5222, 6, 1));
        results.add(new Result("server4.com", 5222, 3, 20));
        results.add(new Result("server5.com", 5222, 2, 1));
        results.add(new Result("server6.com", 5222, 3, 10));
        results.add(new Result("server7.com", 5222, 3, 40));

        RandomGenerator generator = new RandomGenerator2();
        DomainNameServiceQuery.sortResults(results, generator);

        assertEquals("server5.com",results.get(0).hostname);
        assertEquals("server7.com",results.get(1).hostname);
        assertEquals("server2.com",results.get(2).hostname);
        assertEquals("server4.com",results.get(3).hostname);
        assertEquals("server6.com",results.get(4).hostname);
        assertEquals("server1.com",results.get(5).hostname);
        assertEquals("server3.com",results.get(6).hostname);
    }
    
}
