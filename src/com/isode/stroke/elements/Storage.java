/*
 * Copyright (c) 2012, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tronçon.
 * All rights reserved.
 */

package com.isode.stroke.elements;

import java.util.Vector;
import com.isode.stroke.jid.JID;

/**
 * Class representing storage for storing payloads
 *
 */
public class Storage extends Payload {
    private Vector<Room> rooms = new Vector<Room>();
    private Vector<URL> urls = new Vector<URL>();

    /**
     * Class representing a chat room
     *
     */
    public static class Room {
        public Room() {
            autoJoin = false; 
        }

        public String name = "";
        public JID jid = JID.fromString("");
        public boolean autoJoin = false;
        public String nick = "";
        public String password;
    }

    /**
     * Class for bookmarking web pages, i.e., HTTP or HTTPS URLs. 
     *
     */
    public static class URL {
        public URL() {

        }
        public String name = "";
        public String url = "";
    }

    /**
     * Constructor 
     */
    public Storage() {
    }

    /**
     * Clear the list of rooms 
     */
    public void clearRooms() {
        rooms.clear();
    }

    /**
     * Get the list of rooms
     * @return room list, can be empty but not null
     */
    public Vector<Room> getRooms() {
        return rooms;
    }

    /**
     * Add a room to the list
     * @param room room, not null
     */
    public void addRoom(Room room) {
        rooms.add(room);
    }

    /**
     * Get a list of URLs
     * @return URL list, can be empty but not null
     */
    public Vector<URL> getURLs() {
        return urls;
    }

    /**
     * Add a URL
     * @param url rul, not null
     */
    public void addURL(URL url) {
        urls.add(url);
    }
}
