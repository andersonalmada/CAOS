/*******************************************************************************
 * Copyright (c) 2015 LG Electronics. All Rights Reserved. This software is the
 * confidential and proprietary information of LG Electronics. You shall not
 * disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with LG Electronics.
 *******************************************************************************/
package br.ufc.great.caos.api;

/**
 * This class contains ports that will be used throughout the code. The values
 * will be updated with values defined on the controller.
 */
public class Ports {

	private Ports() {

	}

	public static int authentication = 0;
	public static int offloading = 0;
	public static int packetLoss = 0;
	public static int profileSync = 0;
	public static int reasoner = 0;
	public static int rtt = 0;
	public static int throughput = 0;

	public final static int discoveryRequest = 31001;
	public final static int discoveryReceive = 31002;

	public static void updatePorts(int authentication, int offloading, int packetLoss, int profileSync, int reasoner,
			int rtt, int throughput) {
		Ports.authentication = authentication;
		Ports.offloading = offloading;
		Ports.packetLoss = packetLoss;
		Ports.profileSync = profileSync;
		Ports.reasoner = reasoner;
		Ports.rtt = rtt;
		Ports.throughput = throughput;
	}
}