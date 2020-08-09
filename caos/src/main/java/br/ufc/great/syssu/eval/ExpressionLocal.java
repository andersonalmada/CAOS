/*******************************************************************************
 * Copyright (c) 2015 LG Electronics. All Rights Reserved. This software is the 
 * confidential and proprietary information of LG Electronics. You shall not
 * disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with LG Electronics.
 *******************************************************************************/
package br.ufc.great.syssu.eval;

public class ExpressionLocal extends Expression {

	@Override
	public String toString() {
		switch (this.mOperator) {
		case AND:
			return "(" + mLeftSide.toString() + " & " + mRightSide.toString()
					+ ")";
		case OR:
			return "(" + mLeftSide.toString() + " | " + mRightSide.toString()
					+ ")";
		case NOT:
			return "( ! " + mLeftSide.toString() + ")";
		case EQ:
			return "(" + mLeftSide.toString() + " == " + mRightSide.toString()
					+ ")";
		case LT:
			return "(" + mLeftSide.toString() + " < " + mRightSide.toString()
					+ ")";
		case GT:
			return "(" + mLeftSide.toString() + " > " + mRightSide.toString()
					+ ")";
		case LTEQ:
			return "(" + mLeftSide.toString() + " <= " + mRightSide.toString()
					+ ")";
		case GTEQ:
			return "(" + mLeftSide.toString() + " >= " + mRightSide.toString()
					+ ")";
		default:
			return "Invalid Operator!";
		}
	}
}
