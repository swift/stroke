/*
 * Copyright (c) 2010-2015, Isode Limited, London, England.
 * All rights reserved.
 */
package com.isode.stroke.muc;

import java.util.Vector;

import com.isode.stroke.elements.ErrorPayload;
import com.isode.stroke.elements.Storage;
import com.isode.stroke.queries.IQRouter;
import com.isode.stroke.queries.requests.GetPrivateStorageRequest;
import com.isode.stroke.queries.requests.SetPrivateStorageRequest;
import com.isode.stroke.signals.Signal;
import com.isode.stroke.signals.Signal1;
import com.isode.stroke.signals.Slot2;

/**
 * Class representing a manager for MUC Bookmarks
 *
 */
public class MUCBookmarkManager {
    private boolean ready_;
    private Vector<MUCBookmark> bookmarks_ = new Vector<MUCBookmark>();
    private IQRouter iqRouter_;
    private Storage storage = new Storage();

    /**
     * Constructor
     * @param iqRouter IQ router,not null
     */
    public MUCBookmarkManager(IQRouter iqRouter) {
        iqRouter_ = iqRouter;
        ready_ = false;
        GetPrivateStorageRequest<Storage> request = GetPrivateStorageRequest.create(storage,iqRouter_);
        request.onResponse.connect(new Slot2<Storage, ErrorPayload>(){
            @Override
            public void call(Storage p1, ErrorPayload p2) {
                handleBookmarksReceived(p1, p2);                
            }            
        });
        request.send();
    }

    /**
     * Replace the bookmark
     * @param oldBookmark bookmark to be replaced, not null
     * @param newBookmark bookmark to replace with
     */
    public void replaceBookmark(MUCBookmark oldBookmark, MUCBookmark newBookmark) {
        if (!ready_) return;
        for (int i = 0; i < bookmarks_.size(); i++) {
            if (bookmarks_.get(i).equals(oldBookmark)) {
                bookmarks_.add(i, newBookmark);
                flush();
                onBookmarkRemoved.emit(oldBookmark);
                onBookmarkAdded.emit(newBookmark);
                return;
            }
        }
    }

    /**
     * Add a bookmark
     * @param bookmark bookmark to be added, not null
     */
    public void addBookmark(MUCBookmark bookmark) {
        if (!ready_) return;
        bookmarks_.add(bookmark);
        onBookmarkAdded.emit(bookmark);
        flush();
    }

    /**
     * Remove the given bookmark
     * @param bookmark bookmark to be removed
     */
    public void removeBookmark(MUCBookmark bookmark) {
        if (!ready_) return;
        for (MUCBookmark mb : bookmarks_) {
            if (mb.equals(bookmark)) {
                bookmarks_.remove(mb);
                onBookmarkRemoved.emit(bookmark);
                break;
            }
        }
        flush();
    }

    /**
     * Get a list of bookmarks
     * @return list of bookmarks, can be empty but not null
     */
    public Vector<MUCBookmark> getBookmarks() {
        return bookmarks_;
    }

    public Signal1<MUCBookmark> onBookmarkAdded = new Signal1<MUCBookmark>();
    public Signal1<MUCBookmark> onBookmarkRemoved = new Signal1<MUCBookmark>();
    /**
     * When server bookmarks are ready to be used (request response has been received).
     */
    public Signal onBookmarksReady = new Signal();

    private void handleBookmarksReceived(Storage payload, ErrorPayload error) {
        if (error != null) {
            return;
        }

        ready_ = true;
        onBookmarksReady.emit();
        storage = payload;

        Vector<MUCBookmark> receivedBookmarks = new Vector<MUCBookmark>();
        if (payload != null) for (Storage.Room room : payload.getRooms()) {
            receivedBookmarks.add(new MUCBookmark(room));
        }

        Vector<MUCBookmark> newBookmarks = new Vector<MUCBookmark>();
        for (MUCBookmark oldBookmark : bookmarks_) {
            if (containsEquivalent(receivedBookmarks,oldBookmark)) {
                newBookmarks.add(oldBookmark);
            } else {
                onBookmarkRemoved.emit(oldBookmark);
            }
        }

        for (MUCBookmark newBookmark : receivedBookmarks) {
            if (!containsEquivalent(bookmarks_,newBookmark)) {
                newBookmarks.add(newBookmark);
                onBookmarkAdded.emit(newBookmark);
            }
        }
        bookmarks_ = newBookmarks;
    }

    private boolean containsEquivalent(Vector<MUCBookmark> list, MUCBookmark bookmark) {
        for(MUCBookmark mb : list) {
            if(mb.equals(bookmark)) {
                return true;
            }
        }
        return false;
    }


    private void flush() {
        if (storage == null) {
            storage = new Storage();
        }
        // Update the storage element
        storage.clearRooms();
        for(MUCBookmark bookmark :bookmarks_) {
            storage.addRoom(bookmark.toStorage());
        }

        // Send an iq to save the storage element
        SetPrivateStorageRequest<Storage> request = SetPrivateStorageRequest.create(storage, iqRouter_);
        // FIXME: We should care about the result        
        /*request.onResponse.connect(new Slot1<ErrorPayload>() {
            @Override
            public void call(ErrorPayload p1) {                                 
            }            
        });*/
        request.send();
    }
}
