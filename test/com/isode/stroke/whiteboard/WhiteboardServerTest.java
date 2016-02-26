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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.isode.stroke.elements.WhiteboardDeleteOperation;
import com.isode.stroke.elements.WhiteboardEllipseElement;
import com.isode.stroke.elements.WhiteboardInsertOperation;
import com.isode.stroke.elements.WhiteboardOperation;
import com.isode.stroke.elements.WhiteboardUpdateOperation;

/**
 * Tests for {@link WhiteboardServer}
 */
public class WhiteboardServerTest {

    @Test
    public void testSimpleOp() {
        WhiteboardServer server = new WhiteboardServer();
        WhiteboardInsertOperation firstOp = new WhiteboardInsertOperation();
        firstOp.setID("0");
        server.handleLocalOperationReceived(firstOp);
        WhiteboardInsertOperation serverOp = new WhiteboardInsertOperation();
        serverOp.setID("b");
        serverOp.setParentID("0");
        serverOp.setPos(1);
        server.handleLocalOperationReceived(serverOp);
        WhiteboardInsertOperation clientOp = new WhiteboardInsertOperation();
        WhiteboardEllipseElement clientElement = new WhiteboardEllipseElement(0,0,0,0);
        clientOp.setID("a");
        clientOp.setParentID("0");
        clientOp.setPos(1);
        clientOp.setElement(clientElement);
        WhiteboardOperation resultOp = server.handleClientOperationReceived(clientOp);
        assertNotNull(resultOp);
        assertTrue("resultOp is not a WhiteboardInsertOperation",
                resultOp instanceof WhiteboardInsertOperation);
        WhiteboardInsertOperation op = (WhiteboardInsertOperation) resultOp;
        assertEquals("b",op.getParentID());
        assertEquals("a",op.getID());
        assertEquals(1,op.getPos());
        assertEquals(clientElement,op.getElement());
    }
    
    @Test
    public void testSimpleOp1() {
        WhiteboardServer server = new WhiteboardServer();
        WhiteboardInsertOperation firstOp = new WhiteboardInsertOperation();
        firstOp.setID("0");
        server.handleLocalOperationReceived(firstOp);
        WhiteboardDeleteOperation serverOp = new WhiteboardDeleteOperation();
        serverOp.setID("b");
        serverOp.setParentID("0");
        serverOp.setPos(1);
        server.handleLocalOperationReceived(serverOp);
        WhiteboardUpdateOperation clientOp = new WhiteboardUpdateOperation();
        WhiteboardEllipseElement clientElement = new WhiteboardEllipseElement(0,0,0,0);
        clientOp.setID("a");
        clientOp.setParentID("0");
        clientOp.setPos(1);
        clientOp.setElement(clientElement);
        WhiteboardOperation resultOp = server.handleClientOperationReceived(clientOp);
        assertNotNull(resultOp);
        assertTrue("resultOp is not a WhiteboardDeleteOperation",
                resultOp instanceof WhiteboardDeleteOperation);
        WhiteboardDeleteOperation op = (WhiteboardDeleteOperation) resultOp;
        assertEquals("b", op.getParentID());
        assertEquals("a", op.getID());
        assertEquals(-1, op.getPos());
    }
    
    @Test
    public void testSimpleOp2() {
        WhiteboardServer server = new WhiteboardServer();
        WhiteboardInsertOperation firstOp = new WhiteboardInsertOperation();
        firstOp.setID("0");
        server.handleLocalOperationReceived(firstOp);
        WhiteboardUpdateOperation serverOp = new WhiteboardUpdateOperation();
        serverOp.setID("b");
        serverOp.setParentID("0");
        serverOp.setPos(1);
        server.handleLocalOperationReceived(serverOp);
        WhiteboardDeleteOperation clientOp = new WhiteboardDeleteOperation();
        clientOp.setID("a");
        clientOp.setParentID("0");
        clientOp.setPos(1);
        WhiteboardOperation resultOp = server.handleClientOperationReceived(clientOp);
        assertNotNull(resultOp);
        assertTrue("resultOp is not a WhiteboardDeleteOperation",
                resultOp instanceof WhiteboardDeleteOperation);
        WhiteboardDeleteOperation op = (WhiteboardDeleteOperation) resultOp;
        assertEquals("b", op.getParentID());
        assertEquals("a", op.getID());
        assertEquals(1, op.getPos());
    }
    
    @Test
    public void testFewSimpleOps() {
        WhiteboardServer server = new WhiteboardServer();
        WhiteboardInsertOperation firstOp = new WhiteboardInsertOperation();
        firstOp.setID("0");
        server.handleLocalOperationReceived(firstOp);
        WhiteboardInsertOperation serverOp = new WhiteboardInsertOperation();
        serverOp.setID("a");
        serverOp.setParentID("0");
        serverOp.setPos(1);
        server.handleLocalOperationReceived(serverOp);
        serverOp = new WhiteboardInsertOperation();
        serverOp.setID("b");
        serverOp.setParentID("a");
        serverOp.setPos(2);
        server.handleLocalOperationReceived(serverOp);
        serverOp = new WhiteboardInsertOperation();
        serverOp.setID("c");
        serverOp.setParentID("b");
        serverOp.setPos(3);
        server.handleLocalOperationReceived(serverOp);
        WhiteboardInsertOperation clientOp = new WhiteboardInsertOperation();
        WhiteboardEllipseElement clientElement = new WhiteboardEllipseElement(0,0,0,0);
        clientOp.setID("d");
        clientOp.setParentID("0");
        clientOp.setPos(1);
        clientOp.setElement(clientElement);
        WhiteboardOperation resultOp = server.handleClientOperationReceived(clientOp);
        assertNotNull(resultOp);
        assertTrue("resultOp is not a WhiteboardInsertOperation",resultOp instanceof WhiteboardInsertOperation);
        WhiteboardInsertOperation op = (WhiteboardInsertOperation) resultOp;
        assertEquals("c", op.getParentID());
        assertEquals("d", op.getID());
        assertEquals(1, op.getPos());
        assertEquals(clientElement, op.getElement());
    }

}
