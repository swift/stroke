/*
 * Copyright (c) 2010 Remko Tronçon
 * Licensed under the GNU General Public License v3.
 * See Documentation/Licenses/GPLv3.txt for more information.
 */
/*
 * Copyright (c) 2010, Isode Limited, London, England.
 * All rights reserved.
 */
package com.isode.stroke.session;

import com.isode.stroke.base.ByteArray;
import com.isode.stroke.signals.Slot1;

public class SessionTracer {

    public SessionTracer(Session session) {
        this.session = session;
        session.onDataRead.connect(new Slot1<ByteArray>() {

            public void call(ByteArray p1) {
                printData('<', p1);
            }
        });

        session.onDataWritten.connect(new Slot1<ByteArray>() {

            public void call(ByteArray p1) {
                printData('>', p1);
            }
        });
    }

    private void printData(char direction, ByteArray data) {
        System.err.print("" + direction + direction + " " + session.getLocalJID().toString() + " ");
        for (int i = 0; i < 72 - session.getLocalJID().toString().length() - session.getRemoteJID().toString().length(); ++i) {
            System.err.print(direction);
        }
        System.err.println(" " + session.getRemoteJID().toString() + " " + direction + direction);
        System.err.println(data);
    }
    private Session session;
}