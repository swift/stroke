/*
* Copyright (c) 2014, Isode Limited, London, England.
* All rights reserved.
*/
/*
* Copyright (c) 2014, Remko Tronçon.
* All rights reserved.
*/

package com.isode.stroke.elements;

import com.isode.stroke.jid.JID;
import com.isode.stroke.elements.Payload;

public class PubSubOwnerAffiliation extends Payload {
public enum Type
{
	None,
	Member,
	Outcast,
	Owner,
	Publisher,
	PublishOnly
}

public PubSubOwnerAffiliation() {
}

public JID getJID() {
	return jid_;
}

public void setJID(JID jid) {
	jid_ = jid;
}

public Type getType() {
	return type_;
}

public void setType(Type type) {
	type_ = type;
}

JID jid_;
Type type_;

}
