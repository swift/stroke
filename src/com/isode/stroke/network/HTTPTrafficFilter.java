/*
 * Copyright (c) 2015-2016 Isode Limited.
 * All rights reserved.
 * See the COPYING file for more information.
 */
/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.network;

import java.util.Vector;

public interface HTTPTrafficFilter {

	/**
	 * @brief This method is called by the HTTPConnectPRoxiedConnection on every incoming HTTP response.
	 *        It can be used to insert additional HTTP requests into the HTTP CONNECT proxy initalization process.
	 * @param statusLine status line from a HTTP header
	 * @return A vector of HTTP header fields to use in a new request. If an empty vector is returned,
	 *         no new request will be send and the normal proxy logic continues.
	 */
	public Vector<HTTPConnectProxiedConnection.Pair> filterHTTPResponseHeader(String statusLine, final Vector<HTTPConnectProxiedConnection.Pair> responseHeader);
}