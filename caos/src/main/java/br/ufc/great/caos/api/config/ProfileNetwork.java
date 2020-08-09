/*******************************************************************************
 * Copyright (c) 2015 LG Electronics. All Rights Reserved. This software is the
 * confidential and proprietary information of LG Electronics. You shall not
 * disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with LG Electronics.
 *******************************************************************************/
package br.ufc.great.caos.api.config;

/**
 * None = None profile. Light = Only RTT (TCP). Default = Only RTT (TCP, UDP) e
 * loss packet (UDP). Full = Only RTT (TCP, UDP), loss packet (UDP), jitter
 * (UDP) and bandwidth.
 */
public enum ProfileNetwork {
	NONE, LIGHT, DEFAULT, FULL;
}