/*  Copyright (c) 2016, Isode Limited, London, England.
 *  All rights reserved.
 *
 *  Acquisition and use of this software and related materials for any
 *  purpose requires a written license agreement from Isode Limited,
 *  or a written license from an organisation licensed by Isode Limited
 *  to grant such a license.
 *
 */
package com.isode.stroke.whiteboard;

import com.isode.stroke.elements.WhiteboardDeleteOperation;
import com.isode.stroke.elements.WhiteboardInsertOperation;
import com.isode.stroke.elements.WhiteboardOperation;
import com.isode.stroke.elements.WhiteboardUpdateOperation;

public final class WhiteboardTransformer {

    public static final class Pair {
        public final WhiteboardOperation first;
        public final WhiteboardOperation second;
        public Pair(WhiteboardOperation first,WhiteboardOperation second) {
            this.first = first;
            this.second = second;
        }
        public Pair() {
            this(new WhiteboardOperation(),new WhiteboardOperation());
        }
    }
    
    private WhiteboardTransformer() {
        // Static class so constructor is private
    }
    
    public static Pair transform(WhiteboardOperation clientOp,WhiteboardOperation serverOp) {
        if (clientOp instanceof WhiteboardInsertOperation) {
            WhiteboardInsertOperation clientInsert = (WhiteboardInsertOperation) clientOp;
            if (serverOp instanceof WhiteboardInsertOperation) {
                WhiteboardInsertOperation serverInsert = (WhiteboardInsertOperation) serverOp;
                return transform(clientInsert, serverInsert);
            }
            else if (serverOp instanceof WhiteboardUpdateOperation) {
                WhiteboardUpdateOperation serverUpdate = (WhiteboardUpdateOperation) serverOp;
                return transform(clientInsert, serverUpdate);
            }
            else if (serverOp instanceof WhiteboardDeleteOperation) {
                WhiteboardDeleteOperation serverDelete = (WhiteboardDeleteOperation) serverOp;
                return transform(clientInsert, serverDelete);
            }
        }
        else if (clientOp instanceof WhiteboardUpdateOperation) {
            WhiteboardUpdateOperation clientUpdate = (WhiteboardUpdateOperation) clientOp;
            if (serverOp instanceof WhiteboardInsertOperation) {
                WhiteboardInsertOperation serverInsert = (WhiteboardInsertOperation) serverOp;
                return transform(clientUpdate, serverInsert);
            }
            else if (serverOp instanceof WhiteboardUpdateOperation) {
                WhiteboardUpdateOperation serverUpdate = (WhiteboardUpdateOperation) serverOp;
                return transform(clientUpdate, serverUpdate);
            }
            else if (serverOp instanceof WhiteboardDeleteOperation) {
                WhiteboardDeleteOperation serverDelete = (WhiteboardDeleteOperation) serverOp;
                return transform(clientUpdate, serverDelete);
            }
        }
        else if (clientOp instanceof WhiteboardDeleteOperation) {
            WhiteboardDeleteOperation clientDelete = (WhiteboardDeleteOperation) clientOp;
            if (serverOp instanceof WhiteboardInsertOperation) {
                WhiteboardInsertOperation serverInsert = (WhiteboardInsertOperation) serverOp;
                return transform(clientDelete, serverInsert);
            }
            else if (serverOp instanceof WhiteboardUpdateOperation) {
                WhiteboardUpdateOperation serverUpdate = (WhiteboardUpdateOperation) serverOp;
                return transform(clientDelete, serverUpdate);
            }
            else if (serverOp instanceof WhiteboardDeleteOperation) {
                WhiteboardDeleteOperation serverDelete = (WhiteboardDeleteOperation) serverOp;
                return transform(clientDelete, serverDelete);
            }
        }
        return new Pair();
    }
    
    public static Pair transform(WhiteboardInsertOperation clientOp,WhiteboardInsertOperation serverOp) {
        WhiteboardInsertOperation first = new WhiteboardInsertOperation(serverOp);
        first.setParentID(clientOp.getID());
        WhiteboardInsertOperation second = new WhiteboardInsertOperation(clientOp);
        second.setParentID(serverOp.getID());
        if (clientOp.getPos() <= serverOp.getPos()) {
            first.setPos(first.getPos()+1);
        }
        else {
            second.setPos(second.getPos()+1);
        }
        return new Pair(first,second);
    }
    
    public static Pair transform(WhiteboardUpdateOperation clientOp,WhiteboardUpdateOperation serverOp) {

        WhiteboardUpdateOperation first = new WhiteboardUpdateOperation(serverOp);
        first.setParentID(clientOp.getID());
        
        WhiteboardUpdateOperation second;
        if (clientOp.getPos() == serverOp.getPos()) {
            second = new WhiteboardUpdateOperation(serverOp);
            second.setID(clientOp.getID());
            second.setParentID(serverOp.getID());
        }
        else {
            second = new WhiteboardUpdateOperation(clientOp);
            second.setParentID(serverOp.getID());
        }
        
        if (clientOp.getPos() < serverOp.getPos() && clientOp.getNewPos() > serverOp.getPos()) {
            first.setPos(first.getPos()-1);
            if (clientOp.getNewPos() >= serverOp.getNewPos()) {
                first.setNewPos(first.getNewPos()-1);
            }
        }
        else if (clientOp.getNewPos() >= serverOp.getNewPos()) {
            first.setNewPos(first.getNewPos()-1);
        }
        if (serverOp.getPos() < clientOp.getPos() && serverOp.getNewPos() > clientOp.getPos()) {
            second.setPos(second.getPos()-1);
            if (serverOp.getNewPos() >= clientOp.getNewPos()) {
                second.setNewPos(second.getNewPos()-1);
            }
        }
        else if (serverOp.getNewPos() >= clientOp.getNewPos()) {
            second.setNewPos(second.getNewPos()-1);
        }
        return new Pair(first,second);
    }
    
    public static Pair transform(WhiteboardUpdateOperation clientOp,WhiteboardInsertOperation serverOp) {
        WhiteboardInsertOperation first = new WhiteboardInsertOperation(serverOp);
        first.setParentID(clientOp.getID());
        WhiteboardUpdateOperation second = new WhiteboardUpdateOperation(clientOp);
        second.setParentID(serverOp.getID());
        if (serverOp.getPos() <= clientOp.getPos()) {
            second.setPos(second.getPos()+1);
        }
        return new Pair(first, second);
    }
    
    public static Pair transform(WhiteboardInsertOperation clientOp,WhiteboardUpdateOperation serverOp) {
        WhiteboardUpdateOperation first = new WhiteboardUpdateOperation(serverOp);
        first.setParentID(clientOp.getID());
        WhiteboardInsertOperation second = new WhiteboardInsertOperation(clientOp);
        second.setParentID(serverOp.getID());
        if (serverOp.getPos() >= clientOp.getPos()) {
            first.setPos(first.getPos()+1);
        }
        return new Pair(first,second);
    }
    
    public static Pair transform(WhiteboardDeleteOperation clientOp, WhiteboardDeleteOperation serverOp) {
          WhiteboardDeleteOperation first = new WhiteboardDeleteOperation(serverOp);
          first.setParentID(clientOp.getID());
          WhiteboardDeleteOperation second = new WhiteboardDeleteOperation(clientOp);
          second.setParentID(serverOp.getID());
          if (clientOp.getPos() == -1) {
              second.setPos(-1);
          }
          if (serverOp.getPos() == -1) {
              first.setPos(-1);
          }
          if (clientOp.getPos() < serverOp.getPos()) {
              first.setPos(first.getPos()-1);
          }
          else if (clientOp.getPos() > serverOp.getPos()) {
              second.setPos(second.getPos()-1);
          }
          else {
              first.setPos(-1);
              second.setPos(-1);
          }
          return new Pair(first, second);
    }
    
    public static Pair transform(WhiteboardInsertOperation clientOp, WhiteboardDeleteOperation serverOp) {
        WhiteboardDeleteOperation first = new WhiteboardDeleteOperation(serverOp);
        first.setParentID(clientOp.getID());
        WhiteboardInsertOperation second = new WhiteboardInsertOperation(clientOp);
        second.setParentID(serverOp.getID());
        if (clientOp.getPos() <= serverOp.getPos()) {
            first.setPos(first.getPos()+1);
        }
        else if (serverOp.getPos() != -1) {
            second.setPos(second.getPos()-1);
        }
        return new Pair(first, second);
    }
    
    public static Pair transform(WhiteboardDeleteOperation clientOp, WhiteboardInsertOperation serverOp) {
        WhiteboardInsertOperation first = new WhiteboardInsertOperation(serverOp);
        first.setParentID(clientOp.getID());
        WhiteboardDeleteOperation second = new WhiteboardDeleteOperation(clientOp);
        second.setParentID(serverOp.getID());
        if (serverOp.getPos() <= clientOp.getPos()) {
            second.setPos(second.getPos()+1);
        }
        else if (clientOp.getPos() != -1) {
            first.setPos(first.getPos()-1);
        }
        return new Pair(first, second);
    }
    
    public static Pair transform(WhiteboardUpdateOperation clientOp, WhiteboardDeleteOperation serverOp) {
        WhiteboardDeleteOperation first = new WhiteboardDeleteOperation(serverOp);
        first.setParentID(clientOp.getID());
        WhiteboardUpdateOperation updateOp = new WhiteboardUpdateOperation(clientOp);;
        WhiteboardOperation second = updateOp;
        second.setParentID(serverOp.getID());
        if (clientOp.getPos() == serverOp.getPos()) {
            WhiteboardDeleteOperation deleteOp = new WhiteboardDeleteOperation();
            second = deleteOp;
            second.setPos(-1);
            second.setID(clientOp.getID());
            second.setParentID(serverOp.getID());
            deleteOp.setElementID(serverOp.getElementID());
        }
        else if (clientOp.getPos() > serverOp.getPos() && clientOp.getNewPos() <= serverOp.getPos()) {
            second.setPos(second.getPos()-1);
        }
        else if (clientOp.getPos() < serverOp.getPos() && clientOp.getNewPos() >= serverOp.getPos()) {
            updateOp.setNewPos(updateOp.getNewPos()-1);
        }
        else if (clientOp.getPos() > serverOp.getPos()) {
            second.setPos(second.getPos() - 1);
            updateOp.setNewPos(updateOp.getNewPos()-1);
        }
        return new Pair(first, second);
    }
    
    public static Pair transform(WhiteboardDeleteOperation clientOp, WhiteboardUpdateOperation serverOp) {
        WhiteboardUpdateOperation updateOp = new WhiteboardUpdateOperation(serverOp);
        WhiteboardOperation first = updateOp;
        first.setParentID(clientOp.getID());
        WhiteboardDeleteOperation second = new WhiteboardDeleteOperation(clientOp);
        second.setParentID(serverOp.getID());
        if (clientOp.getPos() == serverOp.getPos()) {
            WhiteboardDeleteOperation deleteOp = new WhiteboardDeleteOperation();
            first = deleteOp;
            first.setPos(-1);
            first.setID(serverOp.getID());
            first.setParentID(clientOp.getID());
            deleteOp.setElementID(clientOp.getElementID());
        }
        else if (clientOp.getPos() < serverOp.getPos() && clientOp.getPos() >= serverOp.getNewPos()) {
            first.setPos(first.getPos()-1);
        }
        else if (clientOp.getPos() > serverOp.getPos() && clientOp.getPos() <= serverOp.getNewPos()) {
            updateOp.setNewPos(updateOp.getNewPos()-1);
        }
        else if (clientOp.getPos() < serverOp.getPos()) {
            first.setPos(first.getPos()-1);
            updateOp.setNewPos(updateOp.getNewPos()-1);
        }
        return new Pair(first, second);
    }
    

}
