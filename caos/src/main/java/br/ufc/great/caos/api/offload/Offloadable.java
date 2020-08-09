/*******************************************************************************
 * Copyright (c) 2015 LG Electronics. All Rights Reserved. This software is the
 * confidential and proprietary information of LG Electronics. You shall not
 * disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with LG Electronics.
 *******************************************************************************/
package br.ufc.great.caos.api.offload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for supporting offloading process. User can selected priority
 * endpoint and debug network (upload time [milliseconds], upload size [bytes],
 * download time and download size)
 */
@Documented
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Offloadable {
	Offload value() default Offload.DYNAMIC;

	boolean cloudletPrority() default true;

	boolean status() default false; // see on log stats

	public enum Offload {
		STATIC, DYNAMIC;

		public String toString() {
			if (ordinal() == STATIC.ordinal()) {
				return "Offload Static"; // always try invoke remotable method
			} else {
				return "Offload Dynamic"; // depends from decision making
			}
		};
	}
}