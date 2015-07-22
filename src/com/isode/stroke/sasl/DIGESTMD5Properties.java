/*
 * Copyright (c) 2010-2013 Isode Limited.
 * All rights reserved.
 * See the COPYING file for more information.
 */
/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.sasl;

import com.isode.stroke.sasl.ClientAuthenticator;
import com.isode.stroke.base.SafeByteArray;
import com.isode.stroke.base.ByteArray;
import java.util.TreeMap;
import java.util.Map;
import java.util.Vector;

public class DIGESTMD5Properties {

	// Swiften uses std::multimap, but this is not required, so a TreeMap is used here.
	private TreeMap<String, ByteArray> properties = new TreeMap<String, ByteArray>();

	static boolean insideQuotes(final ByteArray v) {
		if (v.isEmpty()) {
			return false;
		}
		else if (v.getSize() == 1) {
			return v.getData()[0] == '"';
		}
		else if (v.getData()[0] == '"') {
			return v.getData()[v.getSize() - 1] != '"';
		}
		else {
			return false;
		}
	}

	static ByteArray stripQuotes(final ByteArray v) {
		String s = new String(v.getData()); // possibly with a charset
		int size = v.getSize();		
		int i = 0;
		if(s.charAt(0) == '"') {
			i++;
			size--;
		}
		if(s.charAt(v.getSize() - 1) == '"') {
			size--;
		}
		String data = s.substring(i, size+1);
		return new ByteArray(data);
	}

	public DIGESTMD5Properties() {

	}

	public String getValue(final String key) {
		if (properties.containsKey(key)) {
			return properties.get(key).toString();
		}
		else {
			return null;
		}
	}

	public void setValue(final String key, final String value) {
		if(!(properties.containsKey(key))) {
			properties.put(key, new ByteArray(value));
		}	
	}

	public ByteArray serialize() {
		ByteArray result = new ByteArray();
		for(Map.Entry<String, ByteArray> entry : properties.entrySet()) {
			if(entry.getKey() != properties.firstKey()) {
				result.append((byte)(','));
			}
			result.append(new ByteArray(entry.getKey()));
			result.append((byte)('='));
			if (isQuoted(entry.getKey())) {
				result.append(new ByteArray("\""));
				result.append(entry.getValue()); //It does not iterate over all values possible for this key. So no need for MultiMap. Also serialize test also indicates the same.
				result.append(new ByteArray("\""));
			}
			else {
				result.append(entry.getValue());
			}			
		}
		return result;
	}

	public static DIGESTMD5Properties parse(final ByteArray data) {
		DIGESTMD5Properties result = new DIGESTMD5Properties();
		boolean inKey = true;
		ByteArray currentKey = new ByteArray();
		ByteArray currentValue = new ByteArray();
		byte[] byteArrayData = data.getData();
		for (int i = 0; i < data.getSize(); ++i) {
			char c = (char)(byteArrayData[i]);
			if (inKey) {
				if (c == '=') {
					inKey = false;
				}
				else {
					currentKey.append((byte)(c));
				}
			}
			else {
				if (c == ',' && !insideQuotes(currentValue)) {
					String key = currentKey.toString();
					if (isQuoted(key)) {
						result.setValue(key, stripQuotes(currentValue).toString());
					}
					else {
						result.setValue(key, currentValue.toString());
					}
					inKey = true;
					currentKey = new ByteArray();
					currentValue = new ByteArray();
				}
				else {
					currentValue.append((byte)(c));
				}
			}
		}

		if (!currentKey.isEmpty()) {
			String key = currentKey.toString();
			if (isQuoted(key)) {
				result.setValue(key, stripQuotes(currentValue).toString());
			}
			else {
				result.setValue(key, currentValue.toString());
			}
		}

		return result;
	}

	private static boolean isQuoted(final String p) {
		return p.equals("authzid") || p.equals("cnonce") || p.equals("digest-uri") || p.equals("nonce") || p.equals("realm") || p.equals("username");
	}
}
