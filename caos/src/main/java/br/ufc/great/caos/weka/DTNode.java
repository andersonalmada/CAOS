/*******************************************************************************
 * Copyright (c) 2015 LG Electronics. All Rights Reserved. This software is the
 * confidential and proprietary information of LG Electronics. You shall not
 * disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with LG Electronics.
 *******************************************************************************/
package br.ufc.great.caos.weka;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.Map;

/**
 * This class represents the node of the decision tree
 */
public class DTNode implements Serializable{

	private static final long serialVersionUID = -3993690553197059507L;

	private short label;
	private String refValue;
	private short type;
	private short trueIndex;
	private short falseIndex;
	
	public DTNode() {
	}
	
	public DTNode(DTNode node) {
		this.label=node.getLabel();
		this.refValue=node.getRefValue();
		this.type=node.getType();
		this.trueIndex=node.getTrueIndex();
		this.falseIndex=node.getFalseIndex();
	}
	
	/**
	 * Validates the decision tree
	 * @param map of attributes and their values
	 * @return boolean
	 */
	public boolean validate(Map<Short,Object> data){return false;};

	/**
	 * Creates the right node, depending on the operation type
	 * @return Object
	 * @throws ObjectStreamException
	 */
	private Object readResolve() throws ObjectStreamException {
		switch (this.type) {
			case 1:
				return new DTNodeGreater(this);
			case 2:
				return new DTNodeGreaterOrEqual(this);
			case 3:
				return new DTNodeLess(this);
			case 4:
				return new DTNodeLessOrEqual(this);
			case 5:
				return new DTNodeEqual(this);
			case 6:
				return new DTNodeNotEqual(this);
			default:
				return this;
		}
	}

	public short getLabel() {
		return label;
	}

	public void setLabel(short label) {
		this.label = label;
	}
	
	public void setLabel(String label) {
		switch(label) {
			case "Download":
				this.label = 1;
				break;
			case "Upload":
				this.label = 2;
				break;
			case "RTT":
				this.label = 3;
				break;
			case "ExecutionCpuTime":
				this.label = 4;
				break;
			case "UploadSize":
				this.label = 5;
				break;
			case "DownloadSize":
				this.label = 6;
				break;
			case "Local":
				this.label = 7;
				break;
			case "Cloud":
				this.label = 8;
				break;
			case "MethodName":
				this.label = 9;
				break;
			default:
				break;
		}
	}

	public String getRefValue() {
		return refValue;
	}

	public void setRefValue(String refValue) {
		this.refValue = refValue;
	}

	public short getType() {
		return type;
	}

	public void setType(short type) {
		this.type = type;
	}
	
	public void setType(String type) {
		switch(type) {
			case ">":
				this.type = 1;
				break;
			case ">=":
				this.type = 2;
				break;
			case "<":
				this.type = 3;
				break;
			case "<=":
				this.type = 4;
				break;
			case "=":
				this.type = 5;
				break;
			case "!=":
				this.type = 6;
				break;
			default:
				break;
		}
	}

	public short getTrueIndex() {
		return trueIndex;
	}

	public void setTrueIndex(short indiceTrue) {
		this.trueIndex = indiceTrue;
	}

	public short getFalseIndex() {
		return falseIndex;
	}

	public void setFalseIndex(short indiceFalse) {
		this.falseIndex = indiceFalse;
	}
}