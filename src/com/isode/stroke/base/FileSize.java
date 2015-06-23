/*
 * Copyright (c) 2010-2014 Isode Limited.
 * All rights reserved.
 * See the COPYING file for more information.
 */

package com.isode.stroke.base;

public class FileSize {

	public static String formatSize(long bytes) {
		char siPrefix[] = {'k', 'M', 'G', 'T', 'P', 'E', 'Z', 'Y'};
		int power = 0;
		double engBytes = (double)bytes;
		while (engBytes >= 1000) {
			++power;
			engBytes = (double)(engBytes / 1000.0);
		}
		return String.format("%.1f", engBytes) + (power > 0 ? (siPrefix[power-1] + "B") : "" );
	}
}