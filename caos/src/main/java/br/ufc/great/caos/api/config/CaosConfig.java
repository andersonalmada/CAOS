/*******************************************************************************
 * Copyright (c) 2015 LG Electronics. All Rights Reserved. This software is the
 * confidential and proprietary information of LG Electronics. You shall not
 * disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with LG Electronics.
 *******************************************************************************/
package br.ufc.great.caos.api.config;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import br.ufc.great.caos.data.StrategyOffload;

/**
 * Application configuration must be used only in MainActivity!
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CaosConfig {
	ProfileNetwork profile() default ProfileNetwork.LIGHT;

	String primaryEndpoint() default "";

	boolean deviceDetails() default true;

	boolean locationCollect() default false;

	boolean decisionMakerActive() default true;

	boolean deployService() default true;
		
	StrategyOffload sync() default StrategyOffload.TIME;	
	
	int value() default 10000;
}