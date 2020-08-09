/*******************************************************************************
 * Copyright (c) 2015 LG Electronics. All Rights Reserved. This software is the
 * confidential and proprietary information of LG Electronics. You shall not
 * disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with LG Electronics.
 *******************************************************************************/
package br.ufc.great.caos.transition;

import java.io.Serializable;

/**
 * Data containing the ports that will be used and the kind of offloading that
 * will be performed (with or without coap)
 */
public class DiscoveryData implements Serializable {

	private static final long serialVersionUID = 7324459507101013471L;
	private int authentication;
	private int offloading;
	private int packetLoss;
	private int profileSync;
	private int reasoner;
	private int rtt;
	private int throughput;

	private boolean coapForOffloading;

	public void updateProperties(int authentication, int offloading, int packetLoss, int profileSync, int reasoner,
			int rtt, int throughput, boolean coapForOffloading) {
		this.authentication = authentication;
		this.offloading = offloading;
		this.packetLoss = packetLoss;
		this.profileSync = profileSync;
		this.reasoner = reasoner;
		this.rtt = rtt;
		this.throughput = throughput;
		this.coapForOffloading = coapForOffloading;
	}

	public int getAuthentication() {
		return authentication;
	}

	public int getOffloading() {
		return offloading;
	}

	public int getPacketLoss() {
		return packetLoss;
	}

	public int getProfileSync() {
		return profileSync;
	}

	public int getReasoner() {
		return reasoner;
	}

	public int getRtt() {
		return rtt;
	}

	public int getThroughput() {
		return throughput;
	}

	public boolean getCoapForOffloading() {
		return coapForOffloading;
	}
}