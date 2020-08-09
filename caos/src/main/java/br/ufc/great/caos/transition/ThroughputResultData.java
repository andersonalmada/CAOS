/*******************************************************************************
 * Copyright (c) 2015 LG Electronics. All Rights Reserved. This software is the
 * confidential and proprietary information of LG Electronics. You shall not
 * disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with LG Electronics.
 *******************************************************************************/
package br.ufc.great.caos.transition;

import java.io.Serializable;

/**
 * This class contains data related to the download and upload rate
 */
public class ThroughputResultData implements Serializable {

	private static final long serialVersionUID = -5804578618350601116L;

	private double mDownloadRate;
	private double mUploadRate;
	private String ipDevice;

	public ThroughputResultData(double downloadRate, double uploadRate, String ipDevice) {
		mDownloadRate = downloadRate;
		mUploadRate = uploadRate;
		this.ipDevice = ipDevice;
	}

	public double getDownloadRate() {
		return mDownloadRate;
	}

	public void setDownloadRate(double mDownloadRate) {
		this.mDownloadRate = mDownloadRate;
	}

	public double getUploadRate() {
		return mUploadRate;
	}

	public void setUploadRate(double mUploadRate) {
		this.mUploadRate = mUploadRate;
	}

	public String getIpDevice() {
		return ipDevice;
	}

	public void setIpDevice(String ipDevice) {
		this.ipDevice = ipDevice;
	}
}
