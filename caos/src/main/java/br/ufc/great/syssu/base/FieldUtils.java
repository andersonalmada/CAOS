/*******************************************************************************
 * Copyright (c) 2015 LG Electronics. All Rights Reserved. This software is the 
 * confidential and proprietary information of LG Electronics. You shall not
 * disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with LG Electronics.
 *******************************************************************************/
package br.ufc.great.syssu.base;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Utility class to work with Fields.
 */
public class FieldUtils {

	/**
	 * Get the type of an object.
     *
	 * @param value the object to be analyzed.
	 * @return the type of the object.
	 */
    public static String getType(Object value) {
        if (value != null) {
            if (value instanceof Boolean) {
                return "?boolean";
            } else if (value instanceof Integer || value instanceof Long) {
                return "?integer";
            } else if (value instanceof Float || value instanceof Double) {
                return "?float";
            } else if (value instanceof String) {
                return "?string";
            } else if (value instanceof List) {
                return "?array";
            } else if (value instanceof Tuple) {
                return "?object";
            }
        }
        throw new IllegalArgumentException(
                "Invalid value type. Only Boolean, Number, String, List and Tuple are accepted");
    }

    /**
     * Get the default value for a type.
     *
     * @param value the object of the specific type.
     * @return the default value of that object.
     */
    public static Object getDefaultValue(Object value) {
        if (value != null) {
            if (value instanceof Boolean) {
                return false;
            } else if (value instanceof Integer || value instanceof Long) {
                return 0;
            } else if (value instanceof Float || value instanceof Double) {
                return 0.0;
            } else if (value instanceof String) {
                return getDefaultValue((String)value);
            } else if (value instanceof List) {
                return new ArrayList<Object>();
            } else if (value instanceof Tuple) {
                return new LinkedHashMap<String, Object>();
            }
        }
        throw new IllegalArgumentException(
                "Invalid value type. Only Boolean, Number, String, List and Tuple are accepted");
    }

    /**
     * Get the default value for a type.
     *
     * @param type the type for getting the default value.
     * @return the default value of that type.
     */
    private static Object getDefaultValue(String type) {
        if (type.equals("?boolean")) {
            return false;
        } else if (type.equals("?integer")) {
            return 0;
        } else if (type.equals("?float")) {
            return 0.0;
        } else if (type.equals("?string")) {
            return "";
        } else if (type.equals("?array")) {
            return new ArrayList<Object>();
        } else if (type.equals("?object")) {
            return new LinkedHashMap<String, Object>();
        } else {
        	return "";
        }
    }
}