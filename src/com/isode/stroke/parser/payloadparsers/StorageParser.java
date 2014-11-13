/*
 * Copyright (c) 2010-2015, Isode Limited, London, England.
 * All rights reserved.
 */
package com.isode.stroke.parser.payloadparsers;

import com.isode.stroke.elements.Storage;
import com.isode.stroke.elements.Storage.Room;
import com.isode.stroke.elements.Storage.URL;
import com.isode.stroke.jid.JID;
import com.isode.stroke.parser.AttributeMap;
import com.isode.stroke.parser.GenericPayloadParser;

public class StorageParser extends GenericPayloadParser<Storage> {

    private final static int BookmarkLevel = 1;
    private static final int DetailLevel = 2;
    private int level;
    private String currentText;
    private Room room;
    private URL url;
    
    public StorageParser() {
        super(new Storage());
    }

    public void handleStartElement(String element, String ns, AttributeMap attributes) {
        if (level == BookmarkLevel) {
            if ("conference".equals(element)) {
                assert(room == null);
                room = new Storage.Room();
                room.autoJoin = attributes.getBoolAttribute("autojoin", false);
                room.jid = new JID(attributes.getAttribute("jid"));
                room.name = attributes.getAttribute("name");
            }
            else if ("url".equals(element)) {
                assert(url == null);
                url = new Storage.URL();
                url.name = attributes.getAttribute("name");
                url.url = attributes.getAttribute("url");
            }
        }
        else if (level == DetailLevel) {
            currentText = "";
        }
        ++level;
    }

    public void handleEndElement(String element, String ns) {
        --level;
        if (level == BookmarkLevel) {
            if ("conference".equals(element)) {
                assert(room != null);
                getPayloadInternal().addRoom(room);
                room = null;
            }
            else if ("url".equals(element)) {
                assert(url != null);
                getPayloadInternal().addURL(url);
                url = null;
            }
        }
        else if (level == DetailLevel && room != null) {
            if ("nick".equals(element)) {
                room.nick = currentText;
            }
            else if ("password".equals(element)) {
                room.password = currentText;
            }
        }
    }

    public void handleCharacterData(String data) {
        currentText += data;
    }
}
