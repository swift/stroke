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

import java.util.ArrayList;
import java.util.List;

import com.isode.stroke.elements.WhiteboardDeleteOperation;
import com.isode.stroke.elements.WhiteboardInsertOperation;
import com.isode.stroke.elements.WhiteboardOperation;
import com.isode.stroke.elements.WhiteboardUpdateOperation;
import com.isode.stroke.whiteboard.WhiteboardTransformer.Pair;

public class WhiteboardClient {
    
    private final List<WhiteboardOperation> localOperations_ = new ArrayList<WhiteboardOperation>();
    private final List<WhiteboardOperation> serverOperations_ = new ArrayList<WhiteboardOperation>();
    private final List<WhiteboardOperation> bridge_ = new ArrayList<WhiteboardOperation>();
    private String lastSentOperationID_ = "";

    public static class Result {
        public final WhiteboardOperation client;
        public final WhiteboardOperation server;
        public Result(WhiteboardOperation client,WhiteboardOperation server) {
            this.client = client;
            this.server = server;
        }
    }
    
    public WhiteboardOperation handleLocalOperationReceived(WhiteboardOperation operation) {
        localOperations_.add(operation);
        
        WhiteboardOperation op = null;
        if (operation instanceof WhiteboardInsertOperation) {
            op = new WhiteboardInsertOperation((WhiteboardInsertOperation) operation);
        }
        else if (operation instanceof WhiteboardUpdateOperation) {
            op = new WhiteboardUpdateOperation((WhiteboardUpdateOperation) operation);
        }
        else if (operation instanceof WhiteboardDeleteOperation) {
            op = new WhiteboardDeleteOperation((WhiteboardDeleteOperation) operation);
        }
        
        if (!bridge_.isEmpty()) {
            WhiteboardOperation back = bridge_.get(bridge_.size()-1);
            op.setParentID(back.getID());
        }
        bridge_.add(op);
        
        if (lastSentOperationID_.isEmpty()) {
            if (operation instanceof WhiteboardInsertOperation) {
                op = new WhiteboardInsertOperation((WhiteboardInsertOperation) operation);
            }
            else if (operation instanceof WhiteboardUpdateOperation) {
                op = new WhiteboardUpdateOperation((WhiteboardUpdateOperation) operation);
            }
            else if (operation instanceof WhiteboardDeleteOperation) {
                op = new WhiteboardDeleteOperation((WhiteboardDeleteOperation) operation);
            }
            
            if (!serverOperations_.isEmpty()) {
                WhiteboardOperation back = serverOperations_.get(serverOperations_.size()-1);
                op.setParentID(back.getID());
            }
            lastSentOperationID_ = operation.getID();
            return op;
        }
        else{
            return null;
        }
    }
    
    public Result handleServerOperationReceived(WhiteboardOperation operation) {
        serverOperations_.add(operation);
        WhiteboardOperation clientResult = null, serverResult = null;
        if (localOperations_.size() == (serverOperations_.size()-1) ) {
            localOperations_.add(operation);
            clientResult = operation;
        }
        else if (lastSentOperationID_ == operation.getID()) {
          //Client received confirmation about own operation and it sends next operation to server
          if (!bridge_.isEmpty() && lastSentOperationID_ .equals(bridge_.get(0).getID())) {
              bridge_.remove(0);
          }

          if (!bridge_.isEmpty() && lastSentOperationID_ .equals(bridge_.get(0).getParentID())) {
              lastSentOperationID_ = bridge_.get(0).getID();
              serverResult = bridge_.get(0);
          }
          if (serverResult == null) {
              lastSentOperationID_ = "";
          }            
        }
        else if (!bridge_.isEmpty()) {
            WhiteboardOperation temp;
            Pair opPair = WhiteboardTransformer.transform(bridge_.get(0), operation);
            temp = opPair.first;
            
            bridge_.set(0, opPair.second);
            String previousID = bridge_.get(0).getID();
            for (int i = 1; i < bridge_.size(); ++i) {
                opPair = WhiteboardTransformer.transform(bridge_.get(i), temp);
                temp = opPair.first;
                bridge_.set(i, opPair.second);
                bridge_.get(i).setParentID(previousID);
                previousID = bridge_.get(i).getID();
            }
            
            WhiteboardOperation localBack = localOperations_.get(localOperations_.size()-1);
            temp.setParentID(localBack.getID());
            localOperations_.add(temp);
            clientResult = temp;
        }
        
        return new Result(clientResult, serverResult);
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Client\n");
        for (WhiteboardOperation op : localOperations_) {
            builder.append(op.getID());
            builder.append(' ');
            builder.append(op.getPos());
            builder.append('\n');
        }
        builder.append("Server\n");
        for (WhiteboardOperation op : serverOperations_) {
            builder.append(op.getID());
            builder.append(' ');
            builder.append(op.getPos());
            builder.append('\n');
        }
        return builder.toString();
    }
    
    public void print() {
        System.out.println(this);
    }
    
}
