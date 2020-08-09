/*******************************************************************************
 * Copyright (c) 2015 LG Electronics. All Rights Reserved. This software is the
 * confidential and proprietary information of LG Electronics. You shall not
 * disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with LG Electronics.
 *******************************************************************************/
package br.ufc.great.caos.transition;

import java.io.Serializable;
import java.util.HashSet;

import br.ufc.great.caos.weka.DTNode;

/**
 * Contains the structure for the client to decide whether the execution will be
 * local or remote
 */
public class OffloadingReasonerData implements Serializable {

	private static final long serialVersionUID = -52925780713415810L;

	public OffloadingReasonerData() {
		this.decisionTree = new DTNode[1];
		DTNode cloudLeafNode = new DTNode();
		cloudLeafNode.setLabel("Cloud");
		this.decisionTree[0] = cloudLeafNode;

		this.setMethodsList(new HashSet<String>());
	}

	private DTNode[] decisionTree;

	private HashSet<String> methodsList;

	public DTNode[] getDecisionTree() {
		return decisionTree;
	}

	public void setDecisionTree(DTNode[] decisionTree) {
		this.decisionTree = decisionTree;
	}

	public HashSet<String> getMethodsList() {
		return methodsList;
	}

	public void setMethodsList(HashSet<String> methodsList) {
		this.methodsList = methodsList;
	}

}
