/*******************************************************************************
 * Copyright (c) 2015 LG Electronics. All Rights Reserved. This software is the
 * confidential and proprietary information of LG Electronics. You shall not
 * disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with LG Electronics.
 *******************************************************************************/
package br.ufc.great.caos.api.util.device;

import java.io.Serializable;

public class ApplicationBean implements Serializable {

	private static final long serialVersionUID = -5901059948798053518L;
	
	private String mVersion;
	private String mAppPackage;
	
	public ApplicationBean(String version, String appPackage) {
		this.mVersion = version;
		this.mAppPackage = appPackage;
	}
	
	public String getVersion() {
		return mVersion;
	}
	public void setVersion(String version) {
		this.mVersion = version;
	}
	public String getAppPackage() {
		return mAppPackage;
	}
	public void setAppPackage(String appPackage) {
		this.mAppPackage = appPackage;
	} 

}
