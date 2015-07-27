/*
 * Copyright (c) 2011-2015 Isode Limited.
 * All rights reserved.
 * See the COPYING file for more information.
 */
/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.network;

import com.isode.stroke.tls.TLSOptions;
import com.isode.stroke.tls.TLSContextFactory;
import com.isode.stroke.tls.TLSContext;
import com.isode.stroke.network.ConnectionFactory;

public class TLSConnectionFactory implements ConnectionFactory {

	private TLSContextFactory contextFactory;
	private ConnectionFactory connectionFactory;
	private TLSOptions options_;

	public TLSConnectionFactory(TLSContextFactory contextFactory, ConnectionFactory connectionFactory, final TLSOptions tlsOptions) {
		this.contextFactory = contextFactory;
		this.connectionFactory = connectionFactory;
		this.options_ = tlsOptions;
	}

	public Connection createConnection() {
		return new TLSConnection(connectionFactory.createConnection(), contextFactory, options_);
	}


}