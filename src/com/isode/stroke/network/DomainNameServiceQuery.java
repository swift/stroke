/*
 * Copyright (c) 2010, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tronçon.
 * All rights reserved.
 */
package com.isode.stroke.network;

import com.isode.stroke.signals.Signal1;
import java.util.Collection;

public abstract class DomainNameServiceQuery {

    public class Result {

        public Result() {
            hostname = "";
            port = -1;
            priority = -1;
            weight = -1;
        }

        public Result(String hostname, int port, int priority, int weight) {
            this.hostname = hostname;
            this.port = port;
            this.priority = priority;
            this.weight = weight;
        }
        public final String hostname;
        public final int port;
        public final int priority;
        public final int weight;
    };

    public class ResultPriorityComparator {

        public boolean compare(DomainNameServiceQuery.Result a, DomainNameServiceQuery.Result b) {
            return a.priority < b.priority;
        }
    };

    public abstract void run();
    public final Signal1<Collection<Result>> onResult = new Signal1<Collection<Result>>();
}
