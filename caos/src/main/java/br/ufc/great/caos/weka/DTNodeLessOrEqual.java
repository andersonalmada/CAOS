/*******************************************************************************
 * Copyright (c) 2015 LG Electronics. All Rights Reserved. This software is the
 * confidential and proprietary information of LG Electronics. You shall not
 * disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with LG Electronics.
 *******************************************************************************/
package br.ufc.great.caos.weka;
import java.util.Map;

/**
 * This class represents the node of the decision tree when a less or equal than (<=)
 * operation is used
 */
public class DTNodeLessOrEqual extends DTNode{

	private static final long serialVersionUID = 5352335690264575334L;
	
	private double value;
	
	public DTNodeLessOrEqual(DTNode node) {
		super(node);
		
		this.value = Double.valueOf(node.getRefValue());
	}

	/**
	 * Validates the decision tree
	 * 
	 * @param map of attributes and their values
	 * @return boolean
	 */
	@Override
	public boolean validate(Map<Short,Object> data) {
		return ((Double)data.get(getLabel()))<=value;
	}
}