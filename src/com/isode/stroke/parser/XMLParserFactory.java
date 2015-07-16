/*
 * Copyright (c) 2010 Isode Limited.
 * All rights reserved.
 * See the COPYING file for more information.
 */
/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.parser;

import com.isode.stroke.parser.XMLParser;
import com.isode.stroke.parser.XMLParserClient;

public abstract class XMLParserFactory {

	public abstract XMLParser createParser(XMLParserClient xmlParserClient);
}
