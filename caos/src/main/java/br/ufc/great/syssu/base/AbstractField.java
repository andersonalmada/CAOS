/*******************************************************************************
 * Copyright (c) 2015 LG Electronics. All Rights Reserved. This software is the 
 * confidential and proprietary information of LG Electronics. You shall not
 * disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with LG Electronics.
 *******************************************************************************/
package br.ufc.great.syssu.base;

import java.util.List;

/**
 * Base class for tuple and pattern fields.
 */
public abstract class AbstractField {

	/**
	 * Get the name of the field.
	 * @return the name of the field.
	 */
	public abstract String getName();

    /**
     * Get the type (int, boolean, String, float, double) of the field.
     * @return the type of the field.
     */
	public abstract String getType();

    /**
     * Get the value of the field.
     * @return the value of the field.
     */
	public abstract Object getValue();

	/**
	 * Verify if the name and the value are according with some rules.
	 * @param name the name of the field.
	 * @param value the value of the field
	 * @throws IllegalArgumentException if the name is null or empty or contains '.' and the value
     * is not null and is one of the acceptable types (Boolean, Number, String, List, Tuple).
	 */
	protected void verify(String name, Object value) throws IllegalArgumentException {
        if (name == null || name.equals("") || name.indexOf(".") != -1) {
            throw new IllegalArgumentException(
                    "Invalid field name. Field name can not be empty or contains '.'");
        }

        if (value == null || (value != null && !(
                   value instanceof Boolean
                || value instanceof Number
                || value instanceof String
                || value instanceof List
                || value instanceof Tuple))) {
            throw new IllegalArgumentException(
                    "Invalid value type. Only Boolean, "
                    + "Number, String, List and Tuple are accepted.");
        }
    } 
}
