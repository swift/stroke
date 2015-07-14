/*
 * Copyright (c) 2010-2015, Isode Limited, London, England.
 * All rights reserved.
 */

package com.isode.stroke.muc;

import com.isode.stroke.elements.Storage;
import com.isode.stroke.elements.Storage.Room;
import com.isode.stroke.jid.JID;

/**
 * Class representing a Bookmark to mult-user chatrooms. 
 * The chatroom bookmarking function includes the ability to auto-join rooms on login.
 *
 */
public class MUCBookmark {
    private JID room_;
    private String name_ = "";
    private String nick_;
    private String password_;
    private boolean autojoin_;   

    /**
     * Constructor
     * @param room storage room, not null
     */
    public MUCBookmark(Storage.Room room) {
        this.name_ = room.name;
        this.room_ = room.jid;
        this.nick_ = room.nick;
        this.password_ = room.password;
        this.autojoin_ = room.autoJoin;
    }

    /**
     * Constructor
     * @param room room jabber id, not null
     * @param bookmarkName name of bookmark, can be null
     */
    public MUCBookmark(JID room, String bookmarkName) {
        this.room_ = room; 
        this.name_ = bookmarkName; 
        this.autojoin_ = false;
    }

    /**
     * Set the autojoin value which determines whether the client should 
     * automatically join the conference room on login. 
     * @param enabled true to enable and false otherwise
     */
    public void setAutojoin(boolean enabled) {
        autojoin_ = enabled;
    }

    /**
     * get the autojoin attribute value
     * @return true or false
     */
    public boolean getAutojoin() {
        return autojoin_;
    }

    /**
     * Set the user's preferred roomnick for the chatroom.
     * @param nick nickname, can be null
     */
    public void setNick(String nick) {
        nick_ = nick;
    }

    /**
     * Set an unencrypted string for the password needed to enter a password-protected room. 
     * For security reasons, use of this element is NOT RECOMMENDED.
     * @param password password, can be null
     */
    public void setPassword(String password) {
        password_ = password;
    }

    /**
     * get the user's nick name
     * @return nick name, can be null
     */
    public String getNick() {
        return nick_;
    }

    /**
     * Get the room password
     * @return room password, can be null
     */
    public String getPassword() {
        return password_;
    }

    /**
     * Get the bookmark name
     * @return bookmark name, can be null
     */
    public String getName()  {
        return name_;
    }

    /**
     * Get the room's jabber ID
     * @return room JID, not null
     */
    public JID getRoom()  {
        return room_;
    }

    /**
     * Convert the bookmark to a room object
     * @return room object, not null
     */
    public Room toStorage()  {
        Storage.Room room = new Storage.Room();
        room.name = name_;
        room.jid = room_;
        if (nick_ != null) {
            room.nick = nick_;
        }
        if (password_ != null) {
            room.password = password_;
        }
        room.autoJoin = autojoin_;
        return room;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(!(obj instanceof MUCBookmark)) return false;
        MUCBookmark rhs = (MUCBookmark)obj;
        if(!checkEqualsWhenNull(rhs.room_,room_)) return false;
        if(!checkEqualsWhenNull(rhs.name_,name_)) return false;
        if(!checkEqualsWhenNull(rhs.nick_,nick_)) return false;
        //if(!checkEqualsWhenNull(rhs.password_,password_)) return false;
        if(!rhs.autojoin_ != autojoin_) return false;
        return true;
    }

    private static boolean checkEqualsWhenNull(Object thisObj, Object otherObj){
        return thisObj == null ? otherObj == null : thisObj.equals(otherObj);
    }
}
