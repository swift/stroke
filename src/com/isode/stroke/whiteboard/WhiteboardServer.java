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

import com.isode.stroke.elements.WhiteboardOperation;

public class WhiteboardServer {
    
    private final List<WhiteboardOperation> operations_ = new ArrayList<WhiteboardOperation>();

    public void handleLocalOperationReceived(WhiteboardOperation operation) {
        operations_.add(operation);
    }
    
    public WhiteboardOperation handleClientOperationReceived(WhiteboardOperation newOperation) {
        
        if (operations_.isEmpty() || 
                newOperation.getParentID().equals(operations_.get(operations_.size()-1).getID())) {
            operations_.add(newOperation);
            return newOperation;
        }
        for (int i = (operations_.size()-1); i >= 0; i--) {
            WhiteboardOperation operation = operations_.get(i);
            while (newOperation.getParentID().equals(operation.getParentID())) {
                WhiteboardTransformer.Pair tResult = 
                        WhiteboardTransformer.transform(newOperation, operation);
                if (i == (operations_.size()-1)) {
                    operations_.add(tResult.second);
                    return tResult.second;
                }
                else {
                    newOperation = tResult.second;
                    i++;
                    operation = operations_.get(i);
                }
            }
        }
        return null;
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Server:\n");
        for (WhiteboardOperation op : operations_) {
            builder.append(op.getID());
            builder.append(" '");
            builder.append(op.getParentID());
            builder.append("' ");
            builder.append(op.getPos());
            builder.append("\n");
        }
        return builder.toString();
    }
    
    public void print() {
        System.out.println(this);
    }

}
