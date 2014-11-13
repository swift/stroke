/*
 * Copyright (c) 2010-2015, Isode Limited, London, England.
 * All rights reserved.
 */

package com.isode.stroke.serializer.payloadserializers;

import com.isode.stroke.elements.Storage;
import com.isode.stroke.serializer.GenericPayloadSerializer;
import com.isode.stroke.serializer.xml.XMLElement;
import com.isode.stroke.serializer.xml.XMLTextNode;

class StorageSerializer extends GenericPayloadSerializer<Storage>{
    
    public StorageSerializer() {
        super(Storage.class);
    }

    @Override
    protected String serializePayload(Storage storage) {
        XMLElement storageElement = new XMLElement("storage", "storage:bookmarks");

        for (final Storage.Room room : storage.getRooms()) {
            XMLElement conferenceElement = new XMLElement("conference");
            conferenceElement.setAttribute("name", room.name);
            conferenceElement.setAttribute("jid", room.jid.toString());
            conferenceElement.setAttribute("autojoin", room.autoJoin ? "1" : "0");
            if (room.nick != null && !room.nick.isEmpty()) {
                XMLElement nickElement = new XMLElement("nick");
                nickElement.addNode(new XMLTextNode(room.nick));
                conferenceElement.addNode(nickElement);
            }
            if (room.password != null) {
                XMLElement passwordElement = new XMLElement("password");
                passwordElement.addNode(new XMLTextNode(room.password));
                conferenceElement.addNode(passwordElement);
            }
            storageElement.addNode(conferenceElement);
        }

        for (final Storage.URL url : storage.getURLs()) {
            XMLElement urlElement = new XMLElement("url");
            urlElement.setAttribute("name", url.name);
            urlElement.setAttribute("url", url.url);
            storageElement.addNode(urlElement);
        }

        return storageElement.serialize();
    }

}
