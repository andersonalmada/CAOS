/*******************************************************************************
 * Copyright (c) 2015 LG Electronics. All Rights Reserved. This software is the 
 * confidential and proprietary information of LG Electronics. You shall not
 * disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with LG Electronics.
 *******************************************************************************/
package br.ufc.great.syssu.base.interfaces;

import java.util.List;

import br.ufc.great.syssu.base.Tuple;

/**
 * Created by Almada on 04/06/2015.
 *
 * Access interface to the Tuples Database.
 */
public interface IDatabase {

    /**
     * Initialize a database that will storage contextual tuples.
     */
    void start();

    /**
     * Close a previous started database.
     */
    void stop();

    /**
     * Check if the database is started.
     * @return the state of the database (true if it is started).
     */
    boolean isActive();

    /**
     * Create a specific table in the database.
     * @param table the id of the table.
     */
    void createTable(String table);

    /**
     * Delete all tuples of a specific table.
     * @param table the id of the table.
     */
    void clearTable(String table);

    /**
     * Insert a tuple in a specific table.
     * @param table the id of the table.
     * @param tuple the tuple to be inserted.
     */
    void insert(String table, Tuple tuple);

    /**
     * Get all the tuples presents in a specific table.
     * @param table the id of the table.
     * @return the list of tuples presents in the table.
     */
    List<Tuple> get(String table);

    /**
     * Get the number of tuples of a specific table.
     * @param table the id of the table.
     * @return the number of tuples presents in the table.
     */
    int getSize(String table);
}
