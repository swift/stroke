/*
* Copyright (c) 2014, Isode Limited, London, England.
* All rights reserved.
*/
/*
* Copyright (c) 2014, Remko Tronçon.
* All rights reserved.
*/

package com.isode.stroke.pubsub;

import java.util.ArrayList;
import java.util.List;

import com.isode.stroke.elements.DiscoInfo;
import com.isode.stroke.elements.DiscoItems;
import com.isode.stroke.elements.DiscoItems.Item;
import com.isode.stroke.elements.ErrorPayload;
import com.isode.stroke.elements.Form;
import com.isode.stroke.elements.Payload;
import com.isode.stroke.elements.PubSub;
import com.isode.stroke.elements.PubSubEventItem;
import com.isode.stroke.elements.PubSubEventItems;
import com.isode.stroke.elements.PubSubEventPayload;
import com.isode.stroke.elements.PubSubEventRetract;
import com.isode.stroke.elements.PubSubItem;
import com.isode.stroke.elements.PubSubItems;
import com.isode.stroke.elements.PubSubOwnerAffiliation;
import com.isode.stroke.elements.PubSubOwnerAffiliations;
import com.isode.stroke.elements.PubSubOwnerPubSub;
import com.isode.stroke.elements.PubSubOwnerSubscription;
import com.isode.stroke.elements.PubSubOwnerSubscriptions;
import com.isode.stroke.elements.PubSubPublish;
import com.isode.stroke.elements.PubSubSubscriptions;
import com.isode.stroke.elements.RawXMLPayload;
import com.isode.stroke.elements.SoftwareVersion;
import com.isode.stroke.eventloop.DummyEventLoop;
import com.isode.stroke.eventloop.Event;
import com.isode.stroke.jid.JID;
import com.isode.stroke.network.JavaNetworkFactories;
import com.isode.stroke.signals.Slot;
import com.isode.stroke.signals.Slot2;
import com.isode.stroke.base.SafeByteArray;

public class TestMain {
    
    /* begin test parameters */
    static String _server = "stan.isode.net";
    static String _pubJID = "test1@stan.isode.net";
    static SafeByteArray _pubPass = new SafeByteArray("password");
    static String _subJID = "test2@stan.isode.net";
    static SafeByteArray _subPass = new SafeByteArray("password");
    static String _pubSubDomain = "pubsub.stan.isode.net";
    static String _pubSubNode = "testnode";
    /* end test parameters */
    
    static DummyEventLoop eventLoop_ = new DummyEventLoop();
    static ArrayList<TestCase> testRoutines_ = new ArrayList<TestCase>();
    static int testRoutinesIndex_;
    static Client clientPub_;
    static Client clientSub_;
    static boolean shutdown_;
    
    static class ShutdownEvent implements Event.Callback {
        public void run() {
            shutdown_ = true;
            beginNextTest();
        }
    };
    
    static class NodeCreate implements Event.Callback {
        public void run() {
            /* create a temporary pubsub node */
            PubSubTools.create(clientPub_, _pubSubDomain, _pubSubNode, new Slot2<PubSub, ErrorPayload>() {
                public void call(PubSub pubSub, ErrorPayload error) {
                    /* we do not assert here, since failing tests can leave old nodes hanging */
                    /* change the access mode to 'open' */
                    PubSubTools.ownerConfigure(clientPub_, _pubSubDomain, _pubSubNode, "pubsub#access_model", "open", new Slot2<PubSubOwnerPubSub, ErrorPayload>() {
                        public void call(PubSubOwnerPubSub pubSub, ErrorPayload error) {
                            if (!assertTest(error==null, "Failed to change node access mode to 'open'.")) {
                                return;
                            }
                            /* change the number of items to return to '10' */
                            PubSubTools.ownerConfigure(clientPub_,  _pubSubDomain,  _pubSubNode,  "pubsub#max_items", "10", new Slot2<PubSubOwnerPubSub, ErrorPayload>() {
                                public void call(PubSubOwnerPubSub pubSub, ErrorPayload error) {
                                    if (!assertTest(error==null, "Failed to change node 'max_items'.")) {
                                        return;
                                    }
                                    /* enable retract notifications */
                                    PubSubTools.ownerConfigure(clientPub_,  _pubSubDomain,  _pubSubNode,  "pubsub#notify_retract", "1", new Slot2<PubSubOwnerPubSub, ErrorPayload>() {
                                        public void call(PubSubOwnerPubSub pubSub, ErrorPayload error) {
                                            if (!assertTest(error==null, "Failed to change node 'notify_retract'.")) {
                                                return;
                                            }
                                            beginNextTest();
                                        }
                                    });
                                }
                            });
                        }
                    });
                }
            });
        }
    };
    
    static class NodeDelete implements Event.Callback {
        public void run() {
            PubSubTools.delete(clientPub_, _pubSubDomain, _pubSubNode, "", new Slot2<PubSubOwnerPubSub, ErrorPayload>() {
                public void call(PubSubOwnerPubSub pubSub, ErrorPayload error) {
                    beginNextTest();
                }
            });
        }
    };
    
    static boolean assertTest(boolean cond, String reason) {
        if (!cond) {
            System.out.println("\n-----BEGIN TEST FAILURE REPORT-----\n");
            System.out.println("Name: " + testRoutines_.get(testRoutinesIndex_-1).getName() + "\n");
            System.out.println("Reason: " + reason + "\n");
            System.out.println("-----END TEST FAILURE REPORT-----\n");
            eventLoop_.postEvent(new ShutdownEvent());
        }
        return cond;
    }
    
    static void beginNextTest() {
        /* return to the main loop and kick off the next test */
        
        String testName = testRoutines_.get(testRoutinesIndex_).getName();
        if (!testName.isEmpty()) {
            System.out.println("Test complete: " + testName);
        }
        
        if (testRoutinesIndex_ < testRoutines_.size()) {
            TestCase nextTest = testRoutines_.get(testRoutinesIndex_);
            eventLoop_.postEvent(nextTest.getRoutine());
            testRoutinesIndex_++;
        }
    }
    
    static void addTest(String name, final Event.Callback routine) {
        /* create a temporary pubsub node and configure it, run the test case, then delete the item afterwards */
        
        testRoutines_.add(new TestCase("", new NodeCreate()));
        testRoutines_.add(new TestCase(name, routine));
        testRoutines_.add(new TestCase("", new NodeDelete()));
    }
    
    static boolean compareItems(Payload ax, String ay, Payload bx, String by) {
        /* compare two item payload's and id's for equality */
        
        if (!(ax instanceof SoftwareVersion) || !(bx instanceof SoftwareVersion)) {
            return false;
        }
        SoftwareVersion az = (SoftwareVersion)ax;
        SoftwareVersion bz = (SoftwareVersion)bx;
        if (!az.getName().equals(bz.getName())) {
            return false;
        }
        if (!az.getVersion().equals(bz.getVersion())) {
            return false;
        }
        if (!az.getOS().equals(bz.getOS())) {
            return false;
        }
        if (!ay.equals(by)) {
            return false;
        }
        return true;
    }
    
    static boolean compareJID(JID a, JID b) {
        return a.compare(b, JID.CompareType.WithoutResource) == 0;
    }
    
    static void testEntityUseCases() {
        //--------------------------------------------------------------------------------
        //-- 5. Entity use cases
        //--------------------------------------------------------------------------------
        
        addTest("5.2 List nodes", new Event.Callback() {
            public void run() {
                PubSubTools.itemList(clientSub_, _server, new Slot2<DiscoItems, ErrorPayload>() {
                    public void call(DiscoItems items, ErrorPayload error) {
                        if (!assertTest(items!=null, "Service discovery failed.")) {
                            return;
                        }
                        if (!assertTest(items.getItems().size()>0, "There are no services on this domain.")) {
                            return;
                        }
                        System.out.println("Services at " + _server + ":");
                        boolean foundTestService = false;
                        for (Item item : items.getItems()) {
                            String jid = item.getJID().toString();
                            if (!item.getName().isEmpty()) {
                                System.out.println("\t" + jid + " (" + item.getName() + ")");
                            } else {
                                System.out.println("\t" + jid);
                            }
                            if (jid.equals(_pubSubDomain)) {
                                foundTestService = true;
                            }
                        }
                        System.out.println("");
                        if (!assertTest(foundTestService, "The service could not be found.")) {
                            return;
                        }
                        beginNextTest();
                    }
                });
            }
        });
        
        addTest("5.5 Discover items of node", new Event.Callback() {
            public void run() {
                PubSubTools.itemList(clientPub_, _pubSubDomain, new Slot2<DiscoItems, ErrorPayload>() {
                    public void call(DiscoItems items, ErrorPayload error) {
                        if (!assertTest(error==null, "Failed to retreieve pubsub node items.")) {
                            return;
                        }
                        if (!assertTest(items.getItems().size()>0, "There are no nodes on this domain.")) {
                            return;
                        }
                        System.out.println("Services at " + _pubSubDomain + ":");
                        boolean foundTestService = false;
                        for (Item item : items.getItems()) {
                            System.out.println("\t" + item.getNode());
                            if (item.getNode().equals(_pubSubNode)) {
                                foundTestService = true;
                            }
                        }
                        System.out.println("");
                        if (!assertTest(foundTestService, "The service could not be found.")) {
                            return;
                        }
                        beginNextTest();
                    }
                });
            }
        });
        
        addTest("5.6 Subscriptions", new Event.Callback() {
            public void run() {
                PubSubTools.subscribe(clientSub_, _pubSubDomain, _pubSubNode, new Slot2<PubSub, ErrorPayload>() {
                    public void call(PubSub pubSub, ErrorPayload error) {
                        if (!assertTest(error==null, "Failed to subscribe to a node.")) {
                            return;
                        }
                        PubSubTools.subscriptionList(clientSub_, _pubSubDomain, _pubSubNode, new Slot2<PubSub, ErrorPayload>() {
                            public void call(PubSub pubSub, ErrorPayload error) {
                                if (!assertTest(error==null && pubSub!=null, "Failed to list subscriptions on a node.")) {
                                    return;
                                }
                                PubSubSubscriptions results = (PubSubSubscriptions)pubSub.getPayload();
                                if (!assertTest(results.getSubscriptions().size() == 1, "Unexpected subscription count.")) {
                                    return;
                                }
                                beginNextTest();
                            }
                        });
                    }
                });
            }
        });
    }
    
    static void testSubscriberUseCases() {
        //--------------------------------------------------------------------------------
        //-- 6. Subscriber use cases
        //--------------------------------------------------------------------------------
        
        addTest("6.1 Subscribe to a node", new Event.Callback() {
            public void run() {
                PubSubTools.subscribe(clientSub_, _pubSubDomain, _pubSubNode, new Slot2<PubSub, ErrorPayload>() {
                    public void call(PubSub pubSub, ErrorPayload error) {
                        if (!assertTest(error==null && pubSub!=null, "Failed to subscribe to a node.")) {
                            return;
                        }
                        beginNextTest();
                    }
                });
            }
        });
        
        addTest("6.2 Unsubscribe from a node", new Event.Callback() {
            public void run() {
                PubSubTools.subscribe(clientSub_, _pubSubDomain, _pubSubNode, new Slot2<PubSub, ErrorPayload>() {
                    public void call(PubSub pubSub, ErrorPayload error) {
                        if (!assertTest(error==null && pubSub!=null, "Failed to subscribe to a node.")) {
                            return;
                        }
                        PubSubTools.unsubscribe(clientSub_, _pubSubDomain, _pubSubNode, new Slot2<PubSub, ErrorPayload>() {
                            public void call(PubSub pubSub, ErrorPayload error) {
                                if (!assertTest(error==null, "Failed to unsubscribe from a node.")) {
                                    return;
                                }
                                beginNextTest();
                            }
                        });
                    }
                });
            }
        });
        
        addTest("6.5 Retrieve items of a node", new Event.Callback() {
            public void run() { /* publish 2 items, then retrieve them, making sure that we get back what we sent */
                final String publishItem1Id = "item_id";
                final SoftwareVersion publishItem1Payload = new SoftwareVersion("MyTest1", "1.0", "Java");
                PubSubTools.publish(clientPub_, _pubSubDomain, _pubSubNode, publishItem1Id, publishItem1Payload, new Slot2<PubSubPublish, ErrorPayload>() {
                    public void call(PubSubPublish publish, ErrorPayload error) {
                        if (!assertTest(error==null, "Failed to publish item.")) {
                            return;
                        }
                        final String publishItem2Id = "item_id2";
                        final SoftwareVersion publishItem2Payload = new SoftwareVersion("MyTest2", "2.0", "Java");
                        PubSubTools.publish(clientPub_, _pubSubDomain, _pubSubNode, publishItem2Id, publishItem2Payload, new Slot2<PubSubPublish, ErrorPayload>() {
                            public void call(PubSubPublish publish, ErrorPayload error) {
                                if (!assertTest(error==null, "Failed to publish item.")) {
                                    return;
                                }
                                PubSubTools.getItems(clientSub_, _pubSubDomain, _pubSubNode, 2, new Slot2<PubSub, ErrorPayload>() {
                                    public void call(PubSub pubSub, ErrorPayload error) {
                                        if (!assertTest(error==null, "Failed to retrieve items.")) {
                                            return;
                                        }
                                        PubSubItems itemsPayload = (PubSubItems)pubSub.getPayload();
                                        List<PubSubItem> items = itemsPayload.getItems();
                                        if (!assertTest(items.size()==2, "Incorrect number of items.")) {
                                            return;
                                        }
                                        Payload payload1 = items.get(0).getData().get(0);
                                        if (!assertTest(compareItems(payload1, items.get(0).getID(), publishItem1Payload, publishItem1Id), "Invalid item received.")) {
                                            return;
                                        }
                                        Payload payload2 = items.get(1).getData().get(0);
                                        if (!assertTest(compareItems(payload2, items.get(1).getID(), publishItem2Payload, publishItem2Id), "Invalid item received.")) {
                                            return;
                                        }
                                        beginNextTest();
                                    }
                                });
                            };
                        });
                    }
                });
            }
        });
        
        addTest("6.5.7 Requesting most recent items", new Event.Callback() {
            public void run() { /* publish 3 items, then retrieve 2 of them, making sure that we receive the 2 most recent items published */
                final String publishItem1Id = "item_id";
                final SoftwareVersion publishItem1Payload = new SoftwareVersion("MyTest1", "1.0", "Java");
                PubSubTools.publish(clientPub_, _pubSubDomain, _pubSubNode, publishItem1Id, publishItem1Payload, new Slot2<PubSubPublish, ErrorPayload>() {
                    public void call(PubSubPublish publish, ErrorPayload error) {
                        if (!assertTest(error==null, "Failed to publish item.")) {
                            return;
                        }
                        final String publishItem2Id = "item_id2";
                        final SoftwareVersion publishItem2Payload = new SoftwareVersion("MyTest2", "2.0", "Java");
                        PubSubTools.publish(clientPub_, _pubSubDomain, _pubSubNode, publishItem2Id, publishItem2Payload, new Slot2<PubSubPublish, ErrorPayload>() {
                            public void call(PubSubPublish publish, ErrorPayload error) {
                                if (!assertTest(error==null, "Failed to publish item.")) {
                                    return;
                                }
                                final String publishItem3Id = "item_id3";
                                final SoftwareVersion publishItem3Payload = new SoftwareVersion("MyTest3", "3.0", "Java");
                                PubSubTools.publish(clientPub_, _pubSubDomain, _pubSubNode, publishItem3Id, publishItem3Payload, new Slot2<PubSubPublish, ErrorPayload>() {
                                    public void call(PubSubPublish publish, ErrorPayload error) {
                                        if (!assertTest(error==null, "Failed to publish item.")) {
                                            return;
                                        }
                                        PubSubTools.getItems(clientSub_, _pubSubDomain, _pubSubNode, 2, new Slot2<PubSub, ErrorPayload>() {
                                            public void call(PubSub pubSub, ErrorPayload error) {
                                                if (!assertTest(error==null, "Failed to retrieve items.")) {
                                                    return;
                                                }
                                                PubSubItems itemsPayload = (PubSubItems)pubSub.getPayload();
                                                List<PubSubItem> items = itemsPayload.getItems();
                                                if (!assertTest(items.size()==2, "Incorrect number of items.")) {
                                                    return;
                                                }
                                                Payload payload1 = items.get(0).getData().get(0);
                                                if (!assertTest(compareItems(payload1, items.get(0).getID(), publishItem2Payload, publishItem2Id), "Invalid item received.")) {
                                                    return;
                                                }
                                                Payload payload2 = items.get(1).getData().get(0);
                                                if (!assertTest(compareItems(payload2, items.get(1).getID(), publishItem3Payload, publishItem3Id), "Invalid item received.")) {
                                                    return;
                                                }
                                                beginNextTest();
                                            }
                                        });
                                    }
                                });
                            };
                        });
                    }
                });
            }
        });
        
        addTest("6.5.8 requesting specific item", new Event.Callback() {
            public void run() { /* publish 2 items, then ask for a specific item, making sure that we get back what we sent */
                final String publishItem1Id = "item_id";
                final SoftwareVersion publishItem1Payload = new SoftwareVersion("MyTest1", "1.0", "Java");
                PubSubTools.publish(clientPub_, _pubSubDomain, _pubSubNode, publishItem1Id, publishItem1Payload, new Slot2<PubSubPublish, ErrorPayload>() {
                    public void call(PubSubPublish publish, ErrorPayload error) {
                        if (!assertTest(error==null, "Failed to publish item.")) {
                            return;
                        }
                        final String publishItem2Id = "item_id2";
                        final SoftwareVersion publishItem2Payload = new SoftwareVersion("MyTest2", "2.0", "Java");
                        PubSubTools.publish(clientPub_, _pubSubDomain, _pubSubNode, publishItem2Id, publishItem2Payload, new Slot2<PubSubPublish, ErrorPayload>() {
                            public void call(PubSubPublish publish, ErrorPayload error) {
                                if (!assertTest(error==null, "Failed to publish item.")) {
                                    return;
                                }
                                PubSubTools.getItem(clientSub_, _pubSubDomain, _pubSubNode, publishItem2Id, new Slot2<PubSub, ErrorPayload>() {
                                    public void call(PubSub pubSub, ErrorPayload error) {
                                        if (!assertTest(error==null, "Failed to retrieve items.")) {
                                            return;
                                        }
                                        PubSubItems itemsPayload = (PubSubItems)pubSub.getPayload();
                                        List<PubSubItem> items = itemsPayload.getItems();
                                        if (!assertTest(items.size()==1, "Incorrect number of items.")) {
                                            return;
                                        }
                                        Payload payload2 = items.get(0).getData().get(0);
                                        if (!assertTest(compareItems(payload2, items.get(0).getID(), publishItem2Payload, publishItem2Id), "Invalid item received.")) {
                                            return;
                                        }
                                        beginNextTest();
                                    }
                                });
                            };
                        });
                    }
                });
            }
        });
    }
    
    static void testPublisherUseCases() {
        //--------------------------------------------------------------------------------
        //-- 7. Publisher use cases
        //--------------------------------------------------------------------------------
        
        addTest("7.1 Publish item to a node", new Event.Callback() {
            public void run() { /* register a pubsub event handler callback and publish an item to the node, making sure we get back the same data */
                final String publishItemId = "item_id";
                final SoftwareVersion publishItemPayload = new SoftwareVersion("MyTest", "1.0", "Java");
                final PubSubManager clientPubManager = clientSub_.getPubSubManager();
                clientPubManager.onEvent.connect(new Slot2<JID, PubSubEventPayload>() {
                    public void call(JID jid, PubSubEventPayload payload) {	
                        if (payload instanceof PubSubEventItems) {
                            PubSubEventItems items = (PubSubEventItems)payload;
                            if (items.getNode().equals(_pubSubNode) && items.getItems().size()>0) {
                                PubSubEventItem item = items.getItems().get(0);
                                Payload itemPayload = item.getData().get(0);
                                if (itemPayload instanceof SoftwareVersion) {
                                    SoftwareVersion version = (SoftwareVersion)itemPayload;
                                    if (!assertTest(compareItems(version, item.getID(), publishItemPayload, publishItemId), "Item data mismatch.")) {
                                        return;
                                    }
                                    System.out.println("\tGot publish notification.\n");
                                    clientPubManager.onEvent.disconnectAll();
                                    beginNextTest();
                                }
                            }
                        }
                    }
                });
                PubSubTools.subscribe(clientSub_, _pubSubDomain, _pubSubNode, new Slot2<PubSub, ErrorPayload>() {
                    public void call(PubSub pubSub, ErrorPayload error) {
                        if (!assertTest(error==null, "Failed to subscribe to a node.")) {
                            return;
                        }
                        PubSubTools.publish(clientPub_, _pubSubDomain, _pubSubNode, publishItemId, publishItemPayload, new Slot2<PubSubPublish, ErrorPayload>() {
                            public void call(PubSubPublish pubSub, ErrorPayload error) {
                                if (!assertTest(error==null, "Failed to publish item to a node.")) {
                                    return;
                                }
                                System.out.println("\tWaiting for publish notification...");
                            }
                        });
                    }
                });
            }
        });
        
        addTest("7.2 Delete item from a node", new Event.Callback() {
            public void run() { /* subscribe to a node, publish an item, then 'retract' (delete) it */
                PubSubTools.subscribe(clientSub_, _pubSubDomain, _pubSubNode, new Slot2<PubSub, ErrorPayload>() {
                    public void call(PubSub pubSub, ErrorPayload error) {
                        if (!assertTest(error==null, "Failed to subscribe to a node.")) {
                            return;
                        }
                        final String publishItemId = "item_id";
                        SoftwareVersion publishItemPayload = new SoftwareVersion("MyTest", "1.0", "Java");
                        PubSubTools.publish(clientPub_, _pubSubDomain, _pubSubNode, publishItemId, publishItemPayload, new Slot2<PubSubPublish, ErrorPayload>() {
                            public void call(PubSubPublish pubSub, ErrorPayload error) {
                                if (!assertTest(error==null, "Failed to publish item to a node.")) {
                                    return;
                                }
                                PubSubTools.retract(clientPub_, _pubSubDomain, _pubSubNode, publishItemId, true, new Slot2<PubSub, ErrorPayload>() {
                                    public void call(PubSub pubSub, ErrorPayload error) {
                                        if (!assertTest(error==null, "Failed to retract item from a node.")) {
                                            return;
                                        }
                                        beginNextTest();
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });
        
        addTest("7.2.2.1 Delete and notify", new Event.Callback() {
            public void run() { /* register a pubsub event handler callback and retract an item to the node, check that we are notified */
                final String publishItemId = "item_id";
                final SoftwareVersion publishItemPayload = new SoftwareVersion("MyTest", "1.0", "Java");
                final PubSubManager clientPubManager = clientSub_.getPubSubManager();
                clientPubManager.onEvent.connect(new Slot2<JID, PubSubEventPayload>() {
                    public void call(JID jid, PubSubEventPayload payload) {
                        if (payload instanceof PubSubEventItems) {
                            PubSubEventItems items = (PubSubEventItems)payload;
                            List<PubSubEventRetract> retracts = items.getRetracts();
                            if (items.getNode().equals(_pubSubNode) && retracts.size()==1) {
                                if (!assertTest(retracts.get(0).getID().equals(publishItemId), "Unexpected retract item.")) {
                                    return;
                                }
                                System.out.println("\tGot retract notification.\n");
                                clientPubManager.onEvent.disconnectAll();
                                beginNextTest();
                            }
                        }
                    }
                });
                PubSubTools.subscribe(clientSub_, _pubSubDomain, _pubSubNode, new Slot2<PubSub, ErrorPayload>() {
                    public void call(PubSub pubSub, ErrorPayload error) {
                        if (!assertTest(error==null, "Failed to subscribe to a node.")) {
                            return;
                        }
                        PubSubTools.publish(clientPub_, _pubSubDomain, _pubSubNode, publishItemId, publishItemPayload, new Slot2<PubSubPublish, ErrorPayload>() {
                            public void call(PubSubPublish pubSub, ErrorPayload error) {
                                if (!assertTest(error==null, "Failed to publish item to a node.")) {
                                    return;
                                }
                                PubSubTools.retract(clientPub_, _pubSubDomain, _pubSubNode, publishItemId, true, new Slot2<PubSub, ErrorPayload>() {
                                    public void call(PubSub pubSub, ErrorPayload error) {
                                        if (!assertTest(error==null, "Failed to retract item from a node.")) {
                                            return;
                                        }
                                        System.out.println("Waiting for retract notification...");
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });
        
        addTest("Publish an unknown element type", new Event.Callback() {
            public void run() { /* subscribe to a node and publish raw xml, making sure we get back the data in the handler */
                final String publishItemId = "item_id";
                final RawXMLPayload publishItemPayload = new RawXMLPayload();
                publishItemPayload.setRawXML(
                        "<entry xmlns='http://www.w3.org/2005/Atom'>"
                                + "<title>Down the Rabbit Hole</title>"
                                + "<summary>"
                                + "Alice was beginning to get very tired of sitting by her sister on the"
                                + "bank and of having nothing to do: once or twice she had peeped into the"
                                + "book her sister was reading, but it had no pictures or conversations in"
                                + "it, \"and what is the use of a book,\" thought Alice, \"without pictures"
                                + "or conversations?\'"
                                + "</summary>"
                                + "<link rel='alternate' type='text/html' href='http://www.gutenberg.org/files/11/11-h/11-h.htm#link2HCH0001'/>"
                                + "<id>tag:gutenberg.org,2008:entry-1234</id>"
                                + "<published>2008-06-25T18:30:02Z</published>"
                                + "<updated>2008-06-25T18:30:02Z</updated>"
                                + "</entry>"
                        );
                final PubSubManager clientPubManager = clientSub_.getPubSubManager();
                clientPubManager.onEvent.connect(new Slot2<JID, PubSubEventPayload>() {
                    public void call(JID jid, PubSubEventPayload payload) {	
                        if (payload instanceof PubSubEventItems) {
                            PubSubEventItems items = (PubSubEventItems)payload;
                            List<PubSubEventItem> itemList = items.getItems();
                            if (itemList.size() > 0) {
                                List<Payload> payloads = itemList.get(0).getData();
                                if (payloads.size() > 0) {
                                    if (payloads.get(0) instanceof RawXMLPayload) {
                                        System.out.println("\tGot publish notification.\n");
                                        clientPubManager.onEvent.disconnectAll();
                                        beginNextTest();
                                    }
                                }
                            }
                        }
                    }
                });
                PubSubTools.subscribe(clientSub_, _pubSubDomain, _pubSubNode, new Slot2<PubSub, ErrorPayload>() {
                    public void call(PubSub pubSub, ErrorPayload error) {
                        if (!assertTest(error==null, "Failed to subscribe to a node.")) {
                            return;
                        }
                        PubSubTools.publish(clientPub_, _pubSubDomain, _pubSubNode, publishItemId, publishItemPayload, new Slot2<PubSubPublish, ErrorPayload>() {
                            public void call(PubSubPublish pubSub, ErrorPayload error) {
                                if (!assertTest(error==null, "Failed to publish item to a node.")) {
                                    return;
                                }
                                System.out.println("Waiting for publish notification...");
                            }
                        });
                    }
                });
            }
        });
    }
    
    static void testOwnerUseCases() {
        //--------------------------------------------------------------------------------
        //-- 8 Owner Use Cases
        //--------------------------------------------------------------------------------
        
        //-- 8.1 Create a node
        //--   Create node with default config
        //--   Create node with custom config
        //-- 8.2 Configure node
        // ^ not applicable ^ exist as separate tests in Sluift, but already used as part of the other tests
        
        addTest("8.3 Request Default Node Configuration Options", new Event.Callback() {
            public void run() {
                PubSubTools.ownerDefaultConfiguration(clientPub_, _pubSubDomain, new Slot2<Form, ErrorPayload>() {
                    public void call(Form config, ErrorPayload error) {
                        if (!assertTest(error==null && config!=null, "Failed to retrieve default configuration.")) {
                            return;
                        }
                        beginNextTest();
                    }
                });
            }
        });
        
        addTest("8.4 Delete node - with redirection", new Event.Callback() {
            public void run() {
                PubSubTools.subscribe(clientSub_, _pubSubDomain, _pubSubNode, new Slot2<PubSub, ErrorPayload>() {
                    public void call(PubSub pubSub, ErrorPayload error) {
                        if (!assertTest(error==null, "Failed to subscribe to a node.")) {
                            return;
                        }
                        PubSubTools.delete(clientPub_, _pubSubDomain, _pubSubNode, "foo@bar.com", new Slot2<PubSubOwnerPubSub, ErrorPayload>() {
                            public void call(PubSubOwnerPubSub pubSub, ErrorPayload error) {
                                if (!assertTest(error==null, "Failed to delete a node.")) {
                                    return;
                                }
                                beginNextTest();
                            }
                        });
                    }
                });
            }
        });
        
        // NOT IMPLEMENTED
        //	addTest("8.5 Purge node items", new Event.Callback() {
        //		public void run() { /* publish 2 items, then purge the node */
        //			PubSubTools.subscribe(clientSub_, _pubSubDomain, _pubSubNode, new Slot2<PubSub, ErrorPayload>() {
        //				public void call(PubSub pubSub, ErrorPayload error) {
        //					if (!assertTest(error==null, "Failed to subscribe to a node.")) {
        //						return;
        //					}
        //					final String publishItem1Id = "item_id";
        //					final SoftwareVersion publishItem1Payload = new SoftwareVersion("MyTest1", "1.0", "Java");
        //					PubSubTools.publish(clientPub_, _pubSubDomain, _pubSubNode, publishItem1Id, publishItem1Payload, new Slot2<PubSubPublish, ErrorPayload>() {
        //						public void call(PubSubPublish publish, ErrorPayload error) {
        //							if (!assertTest(error==null, "Failed to publish item.")) {
        //								return;
        //							}
        //							final String publishItem2Id = "item_id2";
        //							final SoftwareVersion publishItem2Payload = new SoftwareVersion("MyTest2", "2.0", "Java");
        //							PubSubTools.publish(clientPub_, _pubSubDomain, _pubSubNode, publishItem2Id, publishItem2Payload, new Slot2<PubSubPublish, ErrorPayload>() {
        //								public void call(PubSubPublish publish, ErrorPayload error) {
        //									if (!assertTest(error==null, "Failed to publish item.")) {
        //										return;
        //									}
        //									PubSubTools.purge(clientPub_, _pubSubDomain, _pubSubNode, new Slot2<PubSubOwnerPubSub, ErrorPayload>() {
        //										public void call(PubSubOwnerPubSub pubSub, ErrorPayload error) {
        //											if (!assertTest(error==null, "Failed to purge node items.")) {
        //												return;
        //											}
        //											beginNextTest();
        //										}
        //									});
        //								}
        //							});
        //						}
        //					});
        //				}
        //			});
        //		}
        //	});
        
        addTest("8.8 Manage Subscriptions", new Event.Callback() {
            public void run() {
                PubSubTools.subscribe(clientSub_, _pubSubDomain, _pubSubNode, new Slot2<PubSub, ErrorPayload>() {
                    public void call(PubSub pubSub, ErrorPayload error) {
                        if (!assertTest(error==null, "Failed to subscribe to a node.")) {
                            return;
                        }
                        PubSubTools.ownerSubscriptionlist(clientPub_, _pubSubDomain, _pubSubNode, new Slot2<PubSubOwnerPubSub, ErrorPayload>() {
                            public void call(PubSubOwnerPubSub pubSub, ErrorPayload error) {
                                if (!assertTest(error==null, "Failed to retrieve subscription list.")) {
                                    return;
                                }
                                System.out.println("Subscriptions to " + _pubSubNode + " on " + _pubSubDomain + ":");
                                PubSubOwnerSubscriptions subscriptionPayload = (PubSubOwnerSubscriptions)pubSub.getPayload();
                                ArrayList<PubSubOwnerSubscription> subscriptionList = subscriptionPayload.getSubscriptions();
                                for (PubSubOwnerSubscription subscription : subscriptionList) {
                                    System.out.println("\t" + subscription.getJID() + " (" + subscription.getSubscription().toString() + ")");
                                }
                                System.out.println("");
                                beginNextTest();
                            }
                        });
                    }
                });
            }
        });
        
        addTest("8.9 Manage Affiliations", new Event.Callback() {
            public void run() {
                PubSubTools.ownerSetAffiliations(clientPub_, _pubSubDomain, _pubSubNode, clientSub_.getJID(), PubSubOwnerAffiliation.Type.Publisher, new Slot2<PubSubOwnerPubSub, ErrorPayload>() {
                    public void call(PubSubOwnerPubSub pubSub, ErrorPayload error) {
                        if (!assertTest(error==null, "Failed to set affiliation.")) {
                            return;
                        }
                        PubSubTools.ownerGetAffiliations(clientPub_, _pubSubDomain, _pubSubNode, new Slot2<PubSubOwnerPubSub, ErrorPayload>() {
                            public void call(PubSubOwnerPubSub pubSub, ErrorPayload error) {
                                if (!assertTest(error==null, "Failed to retrieve affiliations.")) {
                                    return;
                                }
                                PubSubOwnerAffiliations affiliationsPayload = (PubSubOwnerAffiliations)pubSub.getPayload();
                                ArrayList<PubSubOwnerAffiliation> affiliations = affiliationsPayload.getAffiliations();
                                if (!assertTest(affiliations.size()==2, "Unexpected affiliation count.")) {
                                    return;
                                }
                                PubSubOwnerAffiliation affiliationPub = affiliations.get(0);
                                if (!assertTest(compareJID(affiliationPub.getJID(),clientPub_.getJID()), "Unexpected affiliation JID.")) {
                                    return;
                                }
                                if (!assertTest(affiliationPub.getType() == PubSubOwnerAffiliation.Type.Owner, "Unexpected affiliation type.")) {
                                    return;
                                }
                                PubSubOwnerAffiliation affiliationSub = affiliations.get(1);
                                if (!assertTest(compareJID(affiliationSub.getJID(),clientSub_.getJID()), "Unexpected affiliation JID.")) {
                                    return;
                                }
                                if (!assertTest(affiliationSub.getType() == PubSubOwnerAffiliation.Type.Publisher, "Unexpected affiliation type.")) {
                                    return;
                                }
                                beginNextTest();
                            }
                        });
                    }
                });
            }
        });
    }
    
    public static void main(String[] args) {
        /* print server supported features */
        
        addTest("Query Domain Features", new Event.Callback() {
            public void run() {
                PubSubTools.featureList(clientSub_, _pubSubDomain, new Slot2<DiscoInfo, ErrorPayload>() {
                    public void call(DiscoInfo info, ErrorPayload error) {
                        if (!assertTest(error==null, "Failed to retrieve server feature list.")) {
                            return;
                        }
                        System.out.println("List of features at " + _pubSubDomain + ":");
                        for (String feature : info.getFeatures()) {
                            System.out.println("\t" + feature);
                        }
                        System.out.println("");
                        beginNextTest();
                    };
                });
            }
        });
        
        /* set up test cases */
        
        testEntityUseCases();
        testSubscriberUseCases();
        testPublisherUseCases();
        testOwnerUseCases();
        
        /* the final "test" disconnects the clients */
        
        addTest("ShutdownEvent", new ShutdownEvent());
        
        /* connect the client and begin the tests */
        
        JavaNetworkFactories networkFactories = new JavaNetworkFactories(eventLoop_);
        
        Slot connectCallback = new Slot() {
            public void call() {
                if (clientPub_!=null && clientPub_.isConnected() && clientSub_!=null && clientSub_.isConnected()) {
                    beginNextTest(); /* kick off the tests once both clients are connected */
                }
            }
        };
        
        clientPub_ = new Client("PUBLISHER", new JID(_pubJID), _pubPass, networkFactories, connectCallback);
        clientSub_ = new Client("SUBSCRIBER", new JID(_subJID), _subPass, networkFactories, connectCallback);
        
        while (!shutdown_) {
            eventLoop_.processEvents();
        }
        
        clientSub_.disconnect();
        clientPub_.disconnect();
        
        /* the tests are over */
        
        if (testRoutinesIndex_ == testRoutines_.size()) {
            System.out.println("\n-----BEGIN TEST REPORT-----\n");
            System.out.println("Success: The tests completed.\n");
            System.out.println("-----END TEST REPORT-----\n");
        }
        
        System.out.println("Finished.");
    }
}
