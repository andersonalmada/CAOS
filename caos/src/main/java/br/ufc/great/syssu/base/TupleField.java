/*******************************************************************************
 * Copyright (c) 2015 LG Electronics. All Rights Reserved. This software is the 
 * confidential and proprietary information of LG Electronics. You shall not
 * disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with LG Electronics.
 *******************************************************************************/
package br.ufc.great.syssu.base;

/**
 * A class that represents a field of a tuple.
 */
public class TupleField extends AbstractField {

    private String mName;
    private Object mValue;
    private String mType;

    /**
     * Default Constructor.
     *
     * @param name the name of the field.
     * @param value the value of the field.
     */
    public TupleField(String name, Object value) {
    	verify(name, value);
        this.mName = name;
        this.mValue = value;
        this.mType = FieldUtils.getType(value);
    }

    @Override
    public String getName() {
        return mName;
    }

    @Override
    public Object getValue() {
        return mValue;
    }

    @Override
    public String getType() {
        return mType;
    }

    /**
     * Verify if the pattern field patches this field.
     *
     * @param pattern the pattern field to be analyzed.
     * @return true if the fields matches, false otherwise.
     */
    public boolean associates(PatternField pattern) {
        return associatesName(pattern) && associatesValue(pattern);
    }

    /**
     * Verify the name of the fields.
     *
     * @param pattern the pattern field to be verified.
     * @return true if the names matches, false otherwise.
     */
    private boolean associatesName(PatternField pattern) {
        return pattern.getName().equals("?") || this.getName().equals(pattern.getName());
    }

    /**
     * Verify the value of the fields.
     *
     * @param pattern the pattern field to be verified.
     * @return true if the values matches, false otherwise.
     */
    private boolean associatesValue(PatternField pattern) {
        return (pattern.getValue().equals("?"))
                || (pattern.hasWildCardValue() && this.getType().equals(pattern.getValue()))
                || (!pattern.hasWildCardValue() && this.getValue().equals(pattern.getValue()));
    }   
}
