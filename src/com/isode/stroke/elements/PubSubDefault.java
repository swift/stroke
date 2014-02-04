/*
* Copyright (c) 2014, Isode Limited, London, England.
* All rights reserved.
*/
/*
* Copyright (c) 2014, Remko Tronçon.
* All rights reserved.
*/

package com.isode.stroke.elements;

import com.isode.stroke.elements.PubSubPayload;

public class PubSubDefault extends PubSubPayload {
public enum Type
{
	None,
	Collection,
	Leaf
}

public PubSubDefault() {
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

String node_;
Type type_;

}
