/*******************************************************************************
 * Copyright (c) 2015 LG Electronics. All Rights Reserved. This software is the
 * confidential and proprietary information of LG Electronics. You shall not
 * disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with LG Electronics.
 *******************************************************************************/
package br.ufc.great.caos.weka;

import java.util.Map;

/**
 * This class represents the node of the decision tree when a not equal (!=)
 * operation is used
 */
public class DTNodeNotEqual extends DTNode {

	private static final long serialVersionUID = 2960479162031322868L;

	public DTNodeNotEqual(DTNode node) {
		super(node);
	}

	/**
	 * Validates the decision tree
	 * 
	 * @param map of attributes and their values
	 * @return boolean
	 */
	@Override
	public boolean validate(Map<Short, Object> data) {
		return !data.get(getLabel()).equals(getRefValue());
	}
}