/*******************************************************************************
 * Copyright (c) 2015 LG Electronics. All Rights Reserved. This software is the
 * confidential and proprietary information of LG Electronics. You shall not
 * disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with LG Electronics.
 *******************************************************************************/
package br.ufc.great.caos.weka;

import java.util.Map;

/**
 * Utility class for handling the Weka J48 decision tree
 */
public class AndroidWekaTreeUtil {

	/**
	 * Method that classifies an instance and returns whether a method must be
	 * offloaded or not
	 * 
	 * @param context
	 * @param tree
	 * @return boolean
	 */
	public static boolean mustOffload(Map<Short, Object> context, DTNode[] tree) {
		if (classify(context, tree, (short) 0).equals("Local")) {
			return false;
		}
		return true;
	}

	/**
	 * Method that classifies an instance and returns where the method must be
	 * executed
	 * 
	 * @param context
	 * @param tree
	 * @param index
	 * @return String (Local or Cloud)
	 */
	public static String classify(Map<Short, Object> context, DTNode[] tree,
			short index) {
		DTNode node = tree[index];

		switch (node.getLabel()) {
		case 7:
			return "Local";
		case 8:
			return "Cloud";
		default:
			short nextNodeIndex;

			if (node.validate(context))
				nextNodeIndex = node.getTrueIndex();
			else
				nextNodeIndex = node.getFalseIndex();

			return classify(context, tree, nextNodeIndex);
		}
	}
}