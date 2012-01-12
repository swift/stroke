/*
 * Copyright (c) 2010, Isode Limited, London, England.
 * All rights reserved.
 */
package com.isode.stroke.parser;

import com.isode.stroke.base.ByteArray;
import com.isode.stroke.eventloop.Event.Callback;
import com.isode.stroke.eventloop.EventLoop;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.xmlpull.mxp1.MXParser;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/**
 * Parser based around the XmlPullParser
 */
class PullXMLParser extends XMLParser {

    private final Logger logger_ = Logger.getLogger(this.getClass().getName());
    private final XmlPullParser parser_ = new MXParser();
    private final PipedInputStream reader_;
    private final PipedOutputStream writer_;
    private final ArrayBlockingQueue<Event> events_ = new ArrayBlockingQueue<Event>(20);
    private final Thread parserThread_;
    private boolean error_ = false;
    private final EventLoop eventLoop_;


    private enum EventType {Start, End, Text};

    /**
     * XML Event struct.
     */
    private class Event {
        private final EventType type;
        private final String name;
        private final String namespace;
        private final AttributeMap attributes;
        public Event(EventType type, String name, String namespace, AttributeMap attributes) {
            this.type = type;
            this.name = name;
            this.namespace = namespace;
            this.attributes = attributes;
        }

        public Event(String name) {
            this(EventType.Text, name, null, null);
        }


        public Event(String name, String namespace) {
            this(EventType.End, name, namespace, null);
        }

        public Event(String name, String namespace, AttributeMap attributes) {
            this(EventType.Start, name, namespace, attributes);
        }

    }

    /**
     * Put an XML event onto the queue ready for the main thread to pick up later.
     */
    private void addEvent(Event event) throws InterruptedException {
        events_.put(event);
    }

    /**
     * Deal with whatever was just read out of the parser_.
     */
    private void handleEvent(int eventType) throws XmlPullParserException, InterruptedException {
        if (eventType == XmlPullParser.START_TAG) {
            AttributeMap map = new AttributeMap();
            for (int i = 0; i < parser_.getAttributeCount(); i++) {
                map.addAttribute(parser_.getAttributeName(i), parser_.getAttributeNamespace(i), parser_.getAttributeValue(i));
            }
            addEvent(new Event(parser_.getName(), parser_.getNamespace(), map));
            bump();
        } else if (eventType == XmlPullParser.END_TAG) {
            addEvent(new Event(parser_.getName(), parser_.getNamespace()));
            bump();
        } else if (eventType == XmlPullParser.TEXT) {
            StringBuilder text = new StringBuilder();
            int holderForStartAndLength[] = new int[2];
            char ch[] = parser_.getTextCharacters(holderForStartAndLength);
            int start = holderForStartAndLength[0];
            int length = holderForStartAndLength[1];
            for (int i = start; i < start + length; i++) {
                text.append(ch[i]);
            }
            addEvent(new Event(text.toString()));
            bump();
        } else if (eventType == XmlPullParser.START_DOCUMENT) {
            //System.out.println("Starting document");
        } else if (eventType == XmlPullParser.END_DOCUMENT) {
            //System.out.println("Ending document");

        } else {
            //System.out.println("Unhandled event");
        }
    }

    /**
     * Cause the main thread to process any outstanding events.
     */
    private void bump() {
        eventLoop_.postEvent(new Callback() {
            public void run() {
                processEvents();
            }
        });
    }

    public PullXMLParser(XMLParserClient client, EventLoop eventLoop) {
        super(client);
        eventLoop_ = eventLoop;
        writer_ = new PipedOutputStream();
        try {
            reader_ = new PipedInputStream(writer_, 128000);
        } catch (IOException ex) {
            Logger.getLogger(PullXMLParser.class.getName()).log(Level.SEVERE, null, ex);
            throw new IllegalStateException(ex);
        }
        try {
            parser_.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
            parser_.setInput(reader_, "UTF-8");
        } catch (XmlPullParserException ex) {
            Logger.getLogger(PullXMLParser.class.getName()).log(Level.SEVERE, null, ex);
            throw new IllegalStateException(ex);
        }
        Runnable parserRunnable = new Runnable() {
            public void run() {
                int eventType = XmlPullParser.END_DOCUMENT - 1; /* Anything to make the following not true*/
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    try {
                        parser_.next();
                        eventType = parser_.getEventType();
                        handleEvent(eventType);
                    } catch (XmlPullParserException ex) {
                        error_ = true;
                        Logger.getLogger(PullXMLParser.class.getName()).log(Level.SEVERE, null, ex);
                        break;
                    } catch (IOException ex) {
                        error_ = true;
                        Logger.getLogger(PullXMLParser.class.getName()).log(Level.SEVERE, null, ex);
                        break;
                    } catch (InterruptedException ex) {
                        /* The thread was interrupted while trying to process an event - presumably this is because we're shutting down.*/
                        error_ = true;
                        Logger.getLogger(PullXMLParser.class.getName()).log(Level.SEVERE, null, ex);
                        break;
                    }
                }
            }
        };
        parserThread_ = new Thread(parserRunnable);
        parserThread_.setDaemon(true);
        parserThread_.start();
    }

    /**
     * Do not do this!
     * This is only to allow the unit tests to join onto it.
     * @return
     */
    Thread getParserThread() {
        return parserThread_;
    }

    /**
     * Process outstanding events.
     * Call in the main thread only.
     */
    private void processEvents() {
        while (events_.size() > 0) {
            processEvent(events_.poll());
        }
    }

    /**
     * Main thread only.
     */
    private void processEvent(Event event) {
        String name = event.name;
        String namespace = event.namespace;
        AttributeMap attributes = event.attributes;
        switch (event.type) {
            case Start: getClient().handleStartElement(name, namespace, attributes); break;
            case End: getClient().handleEndElement(name, namespace); break;
            case Text: getClient().handleCharacterData(name); break;
        }
    }

    /**
     * Cause the parser thread to parse these data later.
     * Note that the return code is a best guess based on previous parsing,
     * and will almost always give a false negative on a stanza before a
     * true negative. True negatives will always mean an error in the stream.
     */
    @Override
    public boolean parse(String data) {
        try {
            writer_.write(new ByteArray(data).getData());
            writer_.flush();
        } catch (IOException ex) {
            error_ = true;
            Logger.getLogger(PullXMLParser.class.getName()).log(Level.SEVERE, null, ex);
        }
        return !error_;
    }

    
}
