/*******************************************************************************
 * Copyright (c) 2015 LG Electronics. All Rights Reserved. This software is the
 * confidential and proprietary information of LG Electronics. You shall not
 * disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with LG Electronics.
 *******************************************************************************/
package br.ufc.great.caos.transition;

import java.io.Serializable;

/**
 * This class represents the packet sent to compute the packet loss
 */
public class PacketLossData implements Serializable {

	private static final long serialVersionUID = -3739073070424804614L;

	private String deviceIP;
	private Integer packetId;

	public PacketLossData(Integer packetID, String deviceIP) {
		this.packetId = packetID;
		this.deviceIP = deviceIP;
	}

	public String getDeviceIP() {
		return deviceIP;
	}

	public void setDeviceIP(String deviceIP) {
		this.deviceIP = deviceIP;
	}

	public Integer getPacketId() {
		return packetId;
	}

	public void setPacketId(Integer packetId) {
		this.packetId = packetId;
	}
}
