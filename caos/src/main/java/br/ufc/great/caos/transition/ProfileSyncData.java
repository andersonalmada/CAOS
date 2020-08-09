/*******************************************************************************
 * Copyright (c) 2015 LG Electronics. All Rights Reserved. This software is the
 * confidential and proprietary information of LG Electronics. You shall not
 * disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with LG Electronics.
 *******************************************************************************/
package br.ufc.great.caos.transition;

import java.io.Serializable;

/**
 * Contains data about local method executions
 */
public class ProfileSyncData implements Serializable {

	private static final long serialVersionUID = 1004644282642425678L;

	private String mMethodID;
	private int mArgSize;
	private long mExecTime;
	private String mMethodName;
	private String mAppName;
	private String mAppVersion;
	private String ipDevice;

	public ProfileSyncData(String methodID, String methodName, String appName, String appVersion, int argSize,
			long execTime, String ipDevice) {
		super();
		this.mMethodID = methodID;
		this.mMethodName = methodName;
		this.mAppName = appName;
		this.mAppVersion = appVersion;
		this.mArgSize = argSize;
		this.mExecTime = execTime;
		this.ipDevice = ipDevice;
	}

	public String getMethodID() {
		return mMethodID;
	}

	public void setMethodID(String methodID) {
		this.mMethodID = methodID;
	}

	public int getArgSize() {
		return mArgSize;
	}

	public void setArgSize(int argSize) {
		this.mArgSize = argSize;
	}

	public long getExecTime() {
		return mExecTime;
	}

	public void setExecTime(long execTime) {
		this.mExecTime = execTime;
	}

	public String getMethodName() {
		return mMethodName;
	}

	public void setMethodName(String mMethodName) {
		this.mMethodName = mMethodName;
	}

	public String getAppName() {
		return mAppName;
	}

	public void setAppName(String mAppName) {
		this.mAppName = mAppName;
	}

	public String getAppVersion() {
		return mAppVersion;
	}

	public void setAppVersion(String mAppVersion) {
		this.mAppVersion = mAppVersion;
	}

	@Override
	public String toString() {
		return "LocalMethodExecutionBean [methodID=" + mMethodID + ", argSize=" + mArgSize + ", execTime=" + mExecTime
				+ "]";
	}

	public String getIpDevice() {
		return ipDevice;
	}

	public void setIpDevice(String ipDevice) {
		this.ipDevice = ipDevice;
	}

}
