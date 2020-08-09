/*******************************************************************************
 * Copyright (c) 2015 LG Electronics. All Rights Reserved. This software is the 
 * confidential and proprietary information of LG Electronics. You shall not
 * disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with LG Electronics.
 *******************************************************************************/
package br.ufc.great.syssu.base;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * An Collection of AbstractFields used by Tuples and Patterns.
 *
 * @param <T> the type of the field.
 */
public abstract class AbstractFieldCollection<T extends AbstractField>
        implements Iterable<T> {

    protected List<T> mFields;

    /**
     * Default Constructor.
     */
    public AbstractFieldCollection() {
        mFields = new ArrayList<T>();
    }

    /**
     * Add a field in the collection.
     *
     * @param field the field to be added in the collection.
     * @return the updated collection.
     */
    public AbstractFieldCollection<T> addField(T field) {
        mFields.add(field);
        return this;
    }

    /**
     * Add a field in the collection
     *
     * @param name  the name of the field.
     * @param value the value of the field.
     * @return the updated collection.
     */
    public AbstractFieldCollection<T> addField(String name, Object value) {
        mFields.add(createField(name, value));
        return this;
    }

    /**
     * Remove a field from the collection.
     *
     * @param field the field to be removed from the collection.
     * @return the updated collection.
     */
    public AbstractFieldCollection<T> removeField(T field) {
        mFields.remove(field);
        return this;
    }

    /**
     * Remove a field from the collection.
     *
     * @param index the index of the field to be removed.
     * @return the updated collection.
     */
    public AbstractFieldCollection<T> removeField(int index) {
        mFields.remove(index);
        return this;
    }

    /**
     * Get a specific field of the collection.
     *
     * @param index the index of the field to be returned.
     * @return the field with the specified index.
     */
    public T getField(int index) {
        return mFields.get(index);
    }


    /**
     * Get a specific field of the collection.
     *
     * @param name the name of the field to be returned.
     * @return the field with the specified name.
     */
    public T getField(String name) {
        for (T field : mFields) {
            if ( field.getName().equalsIgnoreCase(name) ) {
				return field;
			}
        }

        return null;
    }

    /**
     * Get the number of fields of the collection.
     *
     * @return the number of fields of the collection.
     */
    public int size() {
        return mFields.size();
    }

    /**
     * Verify if the collection is empty.
     *
     * @return true if the collection is empty, false otherwise.
     */
    public boolean isEmpty() {
        return mFields.isEmpty();
    }

    @Override
    public Iterator<T> iterator() {
        return mFields.iterator();
    }

    /**
     * Create a field.
     *
     * @param name  the name of the field.
     * @param value the value of the field.
     * @return the created field of the specified type of this collection.
     */
    public abstract T createField(String name, Object value);

    /**
     * Utility method to print the state of the collection as a String.
     *
     * @return the string representing the state of the collection
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (T field : mFields) {
            sb.append("\n" + field.getName() + ": ");
            sb.append(field.getValue().toString());
        }

        return sb.toString();
    }
}
