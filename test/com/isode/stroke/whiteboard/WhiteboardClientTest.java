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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.isode.stroke.elements.WhiteboardDeleteOperation;
import com.isode.stroke.elements.WhiteboardElement;
import com.isode.stroke.elements.WhiteboardEllipseElement;
import com.isode.stroke.elements.WhiteboardInsertOperation;
import com.isode.stroke.elements.WhiteboardOperation;
import com.isode.stroke.elements.WhiteboardUpdateOperation;


/**
 * Tests for {@link WhiteboardClient}
 */
public class WhiteboardClientTest {

    /*!
     *  /\
     *  \/
     *   \
     */
    @Test
    public void testSynchronize_simplestSync() {
        WhiteboardClient client = new WhiteboardClient();
        WhiteboardInsertOperation serverOp = createInsertOperation("0", "", 0);
        WhiteboardClient.Result pairResult = client.handleServerOperationReceived(serverOp);
        assertEquals(serverOp, pairResult.client);
        assertNull(pairResult.server);

        //Client receives first local operation, because it's parented off "0" which exists
        //in server history and client doesn't wait for any operation ack from server,
        //so this operation could be send
        WhiteboardInsertOperation clientOp = createInsertOperation("a", "0", 1);
        WhiteboardEllipseElement aElement = new WhiteboardEllipseElement(0,0,0,0);
        clientOp.setElement(aElement);
        checkOperation(client.handleLocalOperationReceived(clientOp), "a", "0", 1, aElement);

        //Client receives server operation parented off "0", which isn't last client operation
        //so it have to be transformed against local operations and then transformed value can
        //be returned to draw
        serverOp = createInsertOperation("b", "0", 1);
        WhiteboardEllipseElement bElement = new WhiteboardEllipseElement(0,0,0,0);
        serverOp.setElement(bElement);
        pairResult = client.handleServerOperationReceived(serverOp);
        checkOperation(pairResult.client, "b", "a", 2, bElement);
        assertNull(pairResult.server);

        //Client receives confirmation from the server about processed "a" operation, it had to
        //be transformed against "b" on the server side to receive operation parented off "b".
        //There aren't any waiting operations to send to server, this operation should return
        //nothing
        serverOp = createInsertOperation("a", "b", 1);
        pairResult = client.handleServerOperationReceived(serverOp);
        assertNull(pairResult.client);
        assertNull(pairResult.server);

        //Client receives local operation, it doesn't have to be transformed against anything
        //but operation returned to send to the server should be parented off last server
        //operation, which is "b"
        clientOp = createInsertOperation("c", "b", 3);
        WhiteboardEllipseElement cElement = new WhiteboardEllipseElement(0,0,0,0);
        clientOp.setElement(cElement);
        checkOperation(client.handleLocalOperationReceived(clientOp), "c", "a", 3, cElement);

        //Client receives confirmation from the server about processed "a" operation, it
        //should be the same operation as it was sent because server didn't have to
        //transform it
        clientOp = createInsertOperation("c", "a", 3);
        clientOp.setElement(cElement);
        pairResult = client.handleServerOperationReceived(clientOp);
        assertNull(pairResult.client);
        assertNull(pairResult.server);

        //Results:
        //Client operations:
        //ID    pos
        //0     0
        //a     1
        //b     2
        //c     3
        //
        //Server operations:
        //ID    pos
        //0     0
        //b     1
        //a     1
        //c     3
        //
        //what gives 0abc on both sides
    }
    
    /*!
     *    /
     *   /
     *   \
     */
    @Test
    public void testSynchronize_withoutTranslation() {
        WhiteboardClient client = new WhiteboardClient();
        WhiteboardInsertOperation serverOp = createInsertOperation("0", "", 0);
        WhiteboardClient.Result pairResult = client.handleServerOperationReceived(serverOp);
        assertEquals(serverOp, pairResult.client);
        assertNull(pairResult.server);

        //Client receives first local operation, because it's parented off "0" which exists
        //in server history and client doesn't wait for any operation ack from server,
        //so this operation could be send
        WhiteboardInsertOperation clientOp = createInsertOperation("c", "0", 1);
        WhiteboardEllipseElement cElement = new WhiteboardEllipseElement(0,0,0,0);
        clientOp.setElement(cElement);
        checkOperation(client.handleLocalOperationReceived(clientOp), "c", "0", 1, cElement);

        //Client receives second local operation, client didn't receive ack about previous
        //operation from the server so it can't be send.
        clientOp = createInsertOperation("d", "c", 2);
        WhiteboardEllipseElement dElement = new WhiteboardEllipseElement(0,0,0,0);
        clientOp.setElement(dElement);
        assertNull(client.handleLocalOperationReceived(clientOp));

        //Client receives confirmation about processing "c" operation, it should be the
        //same as sent operation because it wasn't transformed, client could send now
        //operation "d"
        clientOp = createInsertOperation("c", "0", 1);
        clientOp.setElement(cElement);
        pairResult = client.handleServerOperationReceived(clientOp);
        checkOperation(pairResult.server, "d", "c", 2, dElement);
        assertNull(pairResult.client);

        //Client receives confirmation about processing "d", it should be the same as
        //sent operation. There aren't any operations in queue to send.
        clientOp = createInsertOperation("d", "c", 2);
        clientOp.setElement(dElement);
        pairResult = client.handleServerOperationReceived(clientOp);
        assertNull(pairResult.client);
        assertNull(pairResult.server);

        //Client receives new operation from server, it's parented off "d" which is at
        //the end of local history so it doesn't have to be transformed, so operation
        //to pass to window should be the same
        serverOp = createInsertOperation("e", "d", 3);
        WhiteboardEllipseElement eElement = new WhiteboardEllipseElement(0,0,0,0);
        serverOp.setElement(eElement);
        pairResult = client.handleServerOperationReceived(serverOp);
        WhiteboardInsertOperation result = (WhiteboardInsertOperation) pairResult.client;
        assertEquals(serverOp, pairResult.client);
        assertNull(pairResult.server);


        //Client operations:
        //ID    pos
        //0     0
        //c     1
        //d     2
        //e     3
        //
        //Server operations:
        //ID    pos
        //0     0
        //c     1
        //d     2
        //e     3
    }
    
    /*!
     *     /\
     *    /  \
     *    \  /
     *     \/
     */
    @Test
    public void testSynchronize_nonInterrupted() {
        WhiteboardClient client = new WhiteboardClient();
        WhiteboardInsertOperation serverOp = createInsertOperation("0", "", 0);
        WhiteboardClient.Result pairResult = client.handleServerOperationReceived(serverOp);
        assertEquals(serverOp, pairResult.client);
        assertEquals(null, pairResult.server);

        //Client receives first local operation, because it's parented off "0" which exists
        //in server history and client doesn't wait for any operation ack from server,
        //so this operation could be send
        WhiteboardInsertOperation clientOp = createInsertOperation("a", "0", 1);
        WhiteboardEllipseElement aElement = new WhiteboardEllipseElement(0,0,0,0);
        clientOp.setElement(aElement);
        checkOperation(client.handleLocalOperationReceived(clientOp), "a", "0", 1, aElement);

        //Client receives second local operation, client didn't receive ack about previous
        //operation from the server so it can't be send.
        clientOp = createInsertOperation("b", "a", 2);
        WhiteboardEllipseElement bElement = new WhiteboardEllipseElement(0,0,0,0);
        clientOp.setElement(bElement);
        assertNull(client.handleLocalOperationReceived(clientOp));

        //Client receives new operation from server, it should be transformed against
        //"a" and "b" before adding to local operations history because it's parented off "0".
        //Because client is waiting for ack of "a", there is no operation to send to server
        serverOp = createInsertOperation("c", "0", 1);
        WhiteboardEllipseElement cElement = new WhiteboardEllipseElement(0,0,0,0);
        serverOp.setElement(cElement);
        pairResult = client.handleServerOperationReceived(serverOp);
        checkOperation(pairResult.client, "c", "b", 3, cElement);
        assertNull(pairResult.server);

        //Client receives new operation from server, it should be transformed against
        //results of previous transformations, returned operation should be parented off
        //"c" existing in local history.
        //Because client is waiting for ack of "a", there is no operation to send to server
        serverOp = createInsertOperation("d", "c", 2);
        WhiteboardEllipseElement dElement = new WhiteboardEllipseElement(0,0,0,0);
        serverOp.setElement(dElement);
        pairResult = client.handleServerOperationReceived(serverOp);
        checkOperation(pairResult.client, "d", "c", 4, dElement);
        assertNull(pairResult.server);

        //Client receives confirmation about processing "a", it should send next operation
        //to server which is "b", but it should be version parented of transformed "a"
        serverOp = createInsertOperation("a", "d", 1);
        pairResult = client.handleServerOperationReceived(serverOp);
        checkOperation(pairResult.server, "b", "a", 2, bElement);
        assertNull(pairResult.client);


        //Client receives confirmation about processing "b", there aren't any operations
        //waiting so it should return nothing.
        serverOp = createInsertOperation("b", "a", 2);
        pairResult = client.handleServerOperationReceived(serverOp);
        assertNull(pairResult.client);
        assertNull(pairResult.server);

        //Client operations:
        //ID    pos
        //0     0
        //a     1
        //b     2
        //c     3
        //d     4
        //
        //Server operations:
        //ID    pos
        //0     0
        //c     1
        //d     2
        //a     1
        //b     2
        //
        //what gives 0abcd on both sides.
    }
    
    /*!
     *     /\
     *    /  \
     *    \  /
     *    / /
     *    \/
     */
    @Test
    public void testSynchronize_clientInterruption() {
        WhiteboardClient client = new WhiteboardClient();
        WhiteboardInsertOperation serverOp = createInsertOperation("0", "", 0);
        WhiteboardClient.Result pairResult = client.handleServerOperationReceived(serverOp);
        assertEquals(serverOp, pairResult.client);
        assertNull(pairResult.server);

        //Client receives first local operation, because it's parented off "0" which exists
        //in server history and client doesn't wait for any operation ack from server,
        //so this operation could be send
        WhiteboardInsertOperation clientOp = createInsertOperation("a", "0", 1);
        WhiteboardEllipseElement aElement = new WhiteboardEllipseElement(0,0,0,0);
        clientOp.setElement(aElement);
        checkOperation(client.handleLocalOperationReceived(clientOp), "a", "0", 1, aElement);

        //Client receives second local operation, client didn't receive ack about previous
        //operation from the server so it can't be send.
        clientOp = createInsertOperation("b", "a", 2);
        WhiteboardEllipseElement bElement = new WhiteboardEllipseElement(0,0,0,0);
        clientOp.setElement(bElement);
        assertNull(client.handleLocalOperationReceived(clientOp));

        //Client receives new operation from server, it should be transformed against
        //"a" and "b" before adding to local operations history because it's parented off "0".
        //Because client is waiting for ack of "a", there is no operation to send to server
        serverOp = createInsertOperation("c", "0", 1);
        WhiteboardEllipseElement cElement = new WhiteboardEllipseElement(0,0,0,0);
        serverOp.setElement(cElement);
        pairResult = client.handleServerOperationReceived(serverOp);
        checkOperation(pairResult.client, "c", "b", 3, cElement);
        assertNull(pairResult.server);

        //Client receives new local operation, client is still waiting for ack so, it
        //should return nothing
        clientOp = createInsertOperation("e", "a", 4);
        WhiteboardEllipseElement eElement = new WhiteboardEllipseElement(0,0,0,0);
        clientOp.setElement(eElement);
        assertNull(client.handleLocalOperationReceived(clientOp));

        //Client receives new server operation, to add it to local history it should be transformed
        //against result of previous transformations and operation "e", returned operation should
        //be parented off "e", which was last local operation
        serverOp = createInsertOperation("d", "c", 2);
        WhiteboardEllipseElement dElement = new WhiteboardEllipseElement(0,0,0,0);
        serverOp.setElement(dElement);
        pairResult = client.handleServerOperationReceived(serverOp);
        checkOperation(pairResult.client, "d", "e", 5, dElement);
        assertNull(pairResult.server);

        //Client receives confirmation about processing "a", it had to be transformed against
        //"c" and "d" and it is now parented off "d", returned value should be next operation
        //which have to be send to server("b" parented off server version of "a").
        serverOp = createInsertOperation("a", "d", 1);
        pairResult = client.handleServerOperationReceived(serverOp);
        checkOperation(pairResult.server, "b", "a", 2, bElement);
        assertNull(pairResult.client);

        //Client receives confirmation about processing "b", it is the same operation as sent because
        //it didn't have to be transformed, returned value should be next operation
        //which have to be send to server("e" parented off server version of "b").
        serverOp = createInsertOperation("b", "a", 2);
        pairResult = client.handleServerOperationReceived(serverOp);
        checkOperation(pairResult.server, "e", "b", 4, eElement);
        assertNull(pairResult.client);

        //Client receives confirmation about processing "b", it is the same operation as sent because
        //it didn't have to be transformed, there aren't any operations to send so this function returns
        //nothing
        serverOp = createInsertOperation("e", "b", 4);
        pairResult = client.handleServerOperationReceived(serverOp);
        assertNull(pairResult.client);
        assertNull(pairResult.server);

        //Result:
        //Client operations:
        //ID    pos
        //0     0
        //a     1
        //b     2
        //c     3
        //e     4
        //d     5
        //
        //Server operations:
        //0     0
        //c     1
        //d     2
        //a     1
        //b     2
        //e     4
        //what gives 0abced on both sides
    }
    
    /*!
     *    /\
     *   / /
     *   \ \
     *    \/
     */
    @Test
    public void testSynchronize_serverInterruption() {
        WhiteboardClient client = new WhiteboardClient();
        WhiteboardInsertOperation serverOp = createInsertOperation("0", "", 0);
        WhiteboardClient.Result pairResult = client.handleServerOperationReceived(serverOp);
        assertEquals(serverOp, pairResult.client);
        assertNull(pairResult.server);

        //Client receives first local operation, because it's parented off "0" which exists
        //in server history and client doesn't wait for any operation ack from server,
        //so this operation could be send
        WhiteboardInsertOperation clientOp = createInsertOperation("a", "0", 1);
        WhiteboardEllipseElement aElement = new WhiteboardEllipseElement(0,0,0,0);
        clientOp.setElement(aElement);
        checkOperation(client.handleLocalOperationReceived(clientOp), "a", "0", 1, aElement);

        //Client receives second local operation, client didn't receive ack about previous
        //operation from the server so it can't be send.
        clientOp = createInsertOperation("b", "a", 2);
        WhiteboardEllipseElement bElement = new WhiteboardEllipseElement(0,0,0,0);
        clientOp.setElement(bElement);
        assertNull(client.handleLocalOperationReceived(clientOp));

        //Client receives new operation from server, it should be transformed against
        //"a" and "b" before adding to local operations history because it's parented off "0".
        //Because client is waiting for ack of "a", there is no operation to send to server
        serverOp = createInsertOperation("c", "0", 1);
        WhiteboardEllipseElement cElement = new WhiteboardEllipseElement(0,0,0,0);
        serverOp.setElement(cElement);
        pairResult = client.handleServerOperationReceived(serverOp);
        checkOperation(pairResult.client, "c", "b", 3, cElement);
        assertNull(pairResult.server);

        //Client receives confirmation about processing "a", it had to be transformed against
        //"c" and it is now parented off "c", returned value should be next operation
        //which have to be send to server("b" parented off server version of "a").
        serverOp = createInsertOperation("a", "c", 1);
        serverOp.setElement(aElement);
        pairResult = client.handleServerOperationReceived(serverOp);
        checkOperation(pairResult.server, "b", "a", 2, bElement);
        assertNull(pairResult.client);

        //Client receives new server operation, to add it to local history it should be transformed
        //against result of previous transformation(but only with transformation of "b"), returned
        //operation should be parented off "c", which was last local operation
        serverOp = createInsertOperation("d", "a", 3);
        WhiteboardEllipseElement dElement = new WhiteboardEllipseElement(0,0,0,0);
        serverOp.setElement(dElement);
        pairResult = client.handleServerOperationReceived(serverOp);
        checkOperation(pairResult.client, "d", "c", 4, dElement);
        assertNull(pairResult.server);

        //Client receives confirmation about processing "b", it had to be transformed against
        //"d" because both operations was parented off server version of "a".
        //there aren't any operations to send so this function returns nothing.
        serverOp = createInsertOperation("b", "d", 2);
        serverOp.setElement(bElement);
        pairResult = client.handleServerOperationReceived(serverOp);
        assertNull(pairResult.client);
        assertNull(pairResult.server);

        //Client operations:
        //ID    pos
        //0     0
        //a     1
        //b     2
        //c     3
        //d     4
        //
        //Server operations:
        //ID    pos
        //0     0
        //c     1
        //a     1
        //d     3
        //b     2
        //
        //what gives 0abcd on both sides
    }
    
    /*!
     *     /\
     *    /  \
     *    \  /
     *     \/
     */
    @Test
    public void testSynchronize_nonInterruptedMixOperations() {
        WhiteboardClient client = new WhiteboardClient();
        WhiteboardInsertOperation serverOp = createInsertOperation("0", "", 0);
        WhiteboardClient.Result pairResult = client.handleServerOperationReceived(serverOp);
        assertEquals(serverOp, pairResult.client);
        assertNull(pairResult.server);

        //Client receives first local operation, because it's parented off "0" which exists
        //in server history and client doesn't wait for any operation ack from server,
        //so this operation could be send
        WhiteboardInsertOperation clientOp = createInsertOperation("a", "0", 1);
        WhiteboardEllipseElement aElement = new WhiteboardEllipseElement(0,0,0,0);
        clientOp.setElement(aElement);
        checkOperation(client.handleLocalOperationReceived(clientOp), "a", "0", 1, aElement);

        //Client receives second local operation, client didn't receive ack about previous
        //operation from the server so it can't be send.
        WhiteboardUpdateOperation clientUpdateOp = createUpdateOperation("b", "a", 0);
        WhiteboardEllipseElement bElement = new WhiteboardEllipseElement(0,0,0,0);
        clientUpdateOp.setElement(bElement);
        assertNull(client.handleLocalOperationReceived(clientUpdateOp));

        //Client receives new operation from server, it should be transformed against
        //"a" and "b" before adding to local operations history because it's parented off "0".
        //Because client is waiting for ack of "a", there is no operation to send to server
        WhiteboardUpdateOperation serverUpdateOp = createUpdateOperation("c", "0", 0);
        WhiteboardEllipseElement cElement = new WhiteboardEllipseElement(0,0,0,0);
        serverUpdateOp.setElement(cElement);
        pairResult = client.handleServerOperationReceived(serverUpdateOp);
        checkOperation(pairResult.client, "c", "b", 0, cElement);
        assertNull(pairResult.server);

        //Client receives new operation from server, it should be transformed against
        //results of previous transformations, returned operation should be parented off
        //"c" existing in local history.
        //Because client is waiting for ack of "a", there is no operation to send to server
        serverOp = createInsertOperation("d", "c", 1);
        WhiteboardEllipseElement dElement = new WhiteboardEllipseElement(0,0,0,0);
        serverOp.setElement(dElement);
        pairResult = client.handleServerOperationReceived(serverOp);
        checkOperation(pairResult.client, "d", "c", 2, dElement);
        assertNull(pairResult.server);

        //Client receives confirmation about processing "a", it should send next operation
        //to server which is "b", but it should be version parented of transformed "a"
        serverOp = createInsertOperation("a", "d", 1);
        pairResult = client.handleServerOperationReceived(serverOp);
        checkOperation(pairResult.server, "b", "a", 0, cElement);
        assertNull(pairResult.client);


        //Client receives confirmation about processing "b", there aren't any operations
        //waiting so it should return nothing.
        serverUpdateOp = createUpdateOperation("b", "a", 0);
        pairResult = client.handleServerOperationReceived(serverUpdateOp);
        assertNull(pairResult.client);
        assertNull(pairResult.server);

        //Client operations:
        //ID    pos
        //0     0
        //a     1
        //b     2
        //c     3
        //d     4
        //
        //Server operations:
        //ID    pos
        //0     0
        //c     1
        //d     2
        //a     1
        //b     2
        //
        //what gives 0abcd on both sides.
    }
    
    /*!
     *     /\
     *    /  \
     *    \  /
     *     \/
     */
    @Test
    public void testSynchronize_nonInterruptedMixOperations2() {
        WhiteboardClient client = new WhiteboardClient();
        WhiteboardInsertOperation serverOp = createInsertOperation("0", "", 0);
        WhiteboardClient.Result pairResult = client.handleServerOperationReceived(serverOp);
        assertEquals(serverOp, pairResult.client);
        assertNull(pairResult.server);

        serverOp = createInsertOperation("1", "0", 1);
        pairResult = client.handleServerOperationReceived(serverOp);
        assertEquals(serverOp, pairResult.client);
        assertNull(pairResult.server);
        //Client receives first local operation, because it's parented off "0" which exists
        //in server history and client doesn't wait for any operation ack from server,
        //so this operation could be send
        WhiteboardInsertOperation clientOp;
        WhiteboardUpdateOperation clientUpdateOp;
        WhiteboardDeleteOperation clientDeleteOp;
        clientUpdateOp = createUpdateOperation("a", "1", 0);
        WhiteboardEllipseElement aElement = new WhiteboardEllipseElement(0,0,0,0);
        clientUpdateOp.setElement(aElement);
        checkOperation(client.handleLocalOperationReceived(clientUpdateOp), "a", "1", 0, aElement);

        //Client receives second local operation, client didn't receive ack about previous
        //operation from the server so it can't be send.
        clientDeleteOp = createDeleteOperation("b", "a", 1);
        assertNull(client.handleLocalOperationReceived(clientDeleteOp));

        //Client receives new operation from server, it should be transformed against
        //"a" and "b" before adding to local operations history because it's parented off "0".
        //Because client is waiting for ack of "a", there is no operation to send to server
        serverOp = createInsertOperation("c", "1", 2);
        WhiteboardEllipseElement cElement = new WhiteboardEllipseElement(0,0,0,0);
        serverOp.setElement(cElement);
        pairResult = client.handleServerOperationReceived(serverOp);
        checkOperation(pairResult.client, "c", "b", 1, cElement);
        assertNull(pairResult.server);

        //Client receives new operation from server, it should be transformed against
        //results of previous transformations, returned operation should be parented off
        //"c" existing in local history.
        //Because client is waiting for ack of "a", there is no operation to send to server
        WhiteboardUpdateOperation serverUpdateOp = createUpdateOperation("d", "c", 0);
        WhiteboardEllipseElement dElement = new WhiteboardEllipseElement(0,0,0,0);
        serverUpdateOp.setElement(dElement);
        pairResult = client.handleServerOperationReceived(serverUpdateOp);
        checkOperation(pairResult.client, "d", "c", 0, dElement);
        assertNull(pairResult.server);

        //Client receives confirmation about processing "a", it should send next operation
        //to server which is "b", but it should be version parented of transformed "a"
        serverUpdateOp = createUpdateOperation("a", "d", 0);
        pairResult = client.handleServerOperationReceived(serverUpdateOp);
        checkOperation(pairResult.server, "b", "a", 1);
        assertNull(pairResult.client);


        //Client receives confirmation about processing "b", there aren't any operations
        //waiting so it should return nothing.
        WhiteboardDeleteOperation serverDeleteOp = createDeleteOperation("b", "a", 0);
        pairResult = client.handleServerOperationReceived(serverDeleteOp);
        assertNull(pairResult.client);
        assertNull(pairResult.server);

        //Client operations:
        //ID    pos
        //0     0
        //a     1
        //b     2
        //c     3
        //d     4
        //
        //Server operations:
        //ID    pos
        //0     0
        //c     1
        //d     2
        //a     1
        //b     2
        //
        //what gives 0abcd on both sides.
    }
    
    private WhiteboardInsertOperation createInsertOperation(String id, String parent, int pos) {
        WhiteboardInsertOperation operation = new WhiteboardInsertOperation();
        operation.setParentID(parent);
        operation.setID(id);
        operation.setPos(pos);
        return operation;
    }

    private WhiteboardUpdateOperation createUpdateOperation(String id, String parent, int pos) {
        WhiteboardUpdateOperation operation = new WhiteboardUpdateOperation();
        operation.setParentID(parent);
        operation.setID(id);
        operation.setPos(pos);
        return operation;
    }

    private WhiteboardDeleteOperation createDeleteOperation(String id, String parent, int pos) {
        WhiteboardDeleteOperation operation = new WhiteboardDeleteOperation();
        operation.setParentID(parent);
        operation.setID(id);
        operation.setPos(pos);
        return operation;
    }
    
    private void checkOperation(WhiteboardOperation operation, String id, String parent) {
        checkOperation(operation,id,parent,-1);
    }

    private void checkOperation(WhiteboardOperation operation, String id, String parent, int pos) {
       checkOperation(operation,id,parent,pos,null);
    }
    
    private void checkOperation(WhiteboardOperation operation, String id, String parent, int pos, WhiteboardElement element) {
        assertEquals(id, operation.getID());
        assertEquals(parent, operation.getParentID());
        if (pos != -1) {
            assertEquals(pos, operation.getPos());
        }

        if (element != null) {
            if (operation instanceof WhiteboardInsertOperation) {
                WhiteboardInsertOperation insertOp = (WhiteboardInsertOperation) operation;
                assertEquals(element,insertOp.getElement());
            }
            else if (operation instanceof WhiteboardUpdateOperation) {
                WhiteboardUpdateOperation updateOp = (WhiteboardUpdateOperation) operation;
                assertEquals(element,updateOp.getElement());
            }
        }
    }

}
