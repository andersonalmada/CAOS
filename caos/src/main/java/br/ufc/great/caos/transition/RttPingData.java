/*******************************************************************************
 * Copyright (c) 2015 LG Electronics. All Rights Reserved. This software is the
 * confidential and proprietary information of LG Electronics. You shall not
 * disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with LG Electronics.
 *******************************************************************************/
package br.ufc.great.caos.transition;

import java.io.Serializable;

/**
 * Data used to compute ping delay between the device and the cloudlet
 */
public class RttPingData implements Serializable {

	private static final long serialVersionUID = 8604805028987978350L;
	private Long time;
	private String ipDevice;

	public RttPingData(Long time, String ipDevice) {
		this.time = time;
		this.ipDevice = ipDevice;
	}

	public Long getTime() {
		return time;
	}

	public void setTime(Long time) {
		this.time = time;
	}

	public String getIpDevice() {
		return ipDevice;
	}

	public void setIpDevice(String ipDevice) {
		this.ipDevice = ipDevice;
	}
}
