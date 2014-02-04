/*
* Copyright (c) 2014, Isode Limited, London, England.
* All rights reserved.
*/
/*
* Copyright (c) 2014, Remko Tronçon.
* All rights reserved.
*/

package com.isode.stroke.pubsub;
import com.isode.stroke.eventloop.Event;

public class TestCase {
    TestCase(String name, Event.Callback routine) {
        name_ = name;
        routine_ = routine;
    }
    
    String getName() {
        return name_;
    }
    
    Event.Callback getRoutine() {
        return routine_;
    }
    String name_;
    Event.Callback routine_;
}
