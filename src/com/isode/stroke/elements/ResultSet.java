/*
* Copyright (c) 2014 Kevin Smith and Remko Tronçon
* All rights reserved.
*/

/*
* Copyright (c) 2014, Isode Limited, London, England.
* All rights reserved.
*/

package com.isode.stroke.elements;

/**
 * ResultSet
 */
public class ResultSet extends Payload {
    public void setMaxItems(Long maxItems) {
        maxItems_ = maxItems;
    }

    public Long getMaxItems() {
        return maxItems_;
    }

    public void setCount(Long count) {
        count_ = count;
    }

    public Long getCount() {
        return count_;
    }

    public void setFirstIDIndex(Long firstIndex) {
        firstIndex_ = firstIndex;
    }

    public Long getFirstIDIndex() {
        return firstIndex_;
    }

    public void setFirstID(String firstID) {
        firstID_ = firstID;
    }

    public String getFirstID() {
        return firstID_;
    }

    public void setLastID(String lastID) {
        lastID_ = lastID;
    }

    public String getLastID() {
        return lastID_;
    }

    public void setAfter(String after) {
        after_ = after;
    }

    public String getAfter() {
        return after_;
    }
    
    public void setBefore(String before) {
        before_ = before;
    }

    public String getBefore() {
        return before_;
    }

    private Long maxItems_;
    private Long count_;
    private Long firstIndex_;
    private String firstID_;
    private String lastID_;
    private String after_;
    private String before_;
}
