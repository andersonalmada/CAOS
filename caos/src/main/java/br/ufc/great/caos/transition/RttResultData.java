/*******************************************************************************
 * Copyright (c) 2015 LG Electronics. All Rights Reserved. This software is the
 * confidential and proprietary information of LG Electronics. You shall not
 * disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with LG Electronics.
 *******************************************************************************/
package br.ufc.great.caos.transition;

import java.io.Serializable;

/**
 * This class contains the computed ping delay
 */
public class RttResultData implements Serializable {

	private static final long serialVersionUID = 4182083760028268074L;

	private String rttMin;
	private String rttMax;
	private String rttAvg;
	private String ipDevice;

	public RttResultData(String rttMin, String rttMax, String rttAvg, String ipDevice) {
		this.rttMin = rttMin;
		this.rttMax = rttMax;
		this.rttAvg = rttAvg;
		this.ipDevice = ipDevice;
	}

	public String getRttMin() {
		return rttMin;
	}

	public void setRttMin(String rttMin) {
		this.rttMin = rttMin;
	}

	public String getRttMax() {
		return rttMax;
	}

	public void setRttMax(String rttMax) {
		this.rttMax = rttMax;
	}

	public String getRttAvg() {
		return rttAvg;
	}

	public void setRttAvg(String rttAvg) {
		this.rttAvg = rttAvg;
	}

	public String getIpDevice() {
		return ipDevice;
	}

	public void setIpDevice(String ipDevice) {
		this.ipDevice = ipDevice;
	}
}
