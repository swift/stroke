/*
* Copyright (c) 2014, Isode Limited, London, England.
* All rights reserved.
*/
/*
* Copyright (c) 2014, Remko Tronçon.
* All rights reserved.
*/

package com.isode.stroke.elements;

import com.isode.stroke.elements.Payload;

public class PubSubAffiliation extends Payload {
public enum Type
{
	None,
	Member,
	Outcast,
	Owner,
	Publisher,
	PublishOnly
}

public PubSubAffiliation() {
}

public String getNode() {
	return node_;
}

public void setNode(String node) {
	node_ = node;
}

public Type getType() {
	return type_;
}

public void setType(Type type) {
	type_ = type;
}

private String node_ = "";
private Type type_;

}
