/*******************************************************************************
 * Copyright (c) 2015 LG Electronics. All Rights Reserved. This software is the 
 * confidential and proprietary information of LG Electronics. You shall not
 * disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with LG Electronics.
 *******************************************************************************/
package br.ufc.great.syssu.eval;

public class ExpressionMongo extends Expression {

	// TODO: RETIRAR ASPAS DUPLAS
	@Override
	public String toString() {
		switch (this.mOperator) {
		case TIMESTAMP:
			if (mRightSide == null) {
				return mLeftSide.toString() + " , { 'map.timestamp' : { $gt : 0 } } } }";
			} 
			
			return mLeftSide.toString() + " , " + mRightSide.toString() + " } }";
			
		case MATCH:
			if (mRightSide == null) {
				return "{ 'map.sensors.myArrayList': { $elemMatch : " + mLeftSide.toString() + " } }";						
			}
			
			return "{ 'map.sensors.myArrayList': { $elemMatch : " + mLeftSide.toString() + " , "
					+ mRightSide.toString();

		case AND:
			return "{ $and : [ " + mLeftSide.toString() + " , " + mRightSide.toString() + " ] }";
		case OR:
			return "{ $or : [ " + mLeftSide.toString() + " , " + mRightSide.toString() + " ] }";
		case NOT:
			return "{ $not : " + mLeftSide.toString() + " }";

		case EQ:
			if (isNumeric(mRightSide.toString())) {
				return "{ 'map." + mLeftSide.toString() + "' : { $eq : " + mRightSide.toString() + " } }";
			}

			return "{ 'map." + mLeftSide.toString() + "' : { $eq : '" + mRightSide.toString() + "' } }";
		case LT:
			return "{ 'map." + mLeftSide.toString() + "' : { $lt : " + mRightSide.toString() + " } }";
		case GT:
			return "{ 'map." + mLeftSide.toString() + "' : { $gt : " + mRightSide.toString() + " } }";
		case LTEQ:
			return "{ 'map." + mLeftSide.toString() + "' : { $lte : " + mRightSide.toString() + " } }";
		case GTEQ:
			return "{ 'map." + mLeftSide.toString() + "' : { $gte : " + mRightSide.toString() + " } }";
		default:
			return "Invalid Operator!";
		}
	}

	public static boolean isNumeric(String str) {
		return str.matches("-?\\d+(\\.\\d+)?"); // match a number with optional '-' and decimal.
	}
}