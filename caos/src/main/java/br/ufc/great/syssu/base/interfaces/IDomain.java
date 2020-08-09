/*******************************************************************************
 * Copyright (c) 2015 LG Electronics. All Rights Reserved. This software is the 
 * confidential and proprietary information of LG Electronics. You shall not
 * disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with LG Electronics.
 *******************************************************************************/
package br.ufc.great.syssu.base.interfaces;

import java.util.List;

import br.ufc.great.syssu.base.Pattern;
import br.ufc.great.syssu.base.Tuple;
import br.ufc.great.syssu.base.TupleSpaceException;
import br.ufc.great.syssu.base.TupleSpaceSecurityException;

/**
 * Access interface to a domain of the Tuple Space, using filters in the form of String.
 */
public interface IDomain extends IDomainComposite{
	
	/**
	 * Get the name of the domain.
	 * @return the name of the domain.
	 */
	public String getName();
	
	/**
	 * Put a tuple in this domain.
	 * @param tuple the input tuple.
	 * @param key a security key that permits the put operation.
	 * @throws TupleSpaceException If some general exception occurs on the tuple space.
	 * @throws TupleSpaceSecurityException If the key is not valid.
	 */
	public void put(Tuple tuple, String key) 
		throws TupleSpaceException, TupleSpaceSecurityException;

	/**
	 * Read tuples in this domain.
	 * @param pattern the pattern used to search for desired tuples.
	 * @param restriction the filter applied over the tuples that matches the pattern.
	 * @param key a security key that permits the read operation.
	 * @return the resulted list of tuples.
	 * @throws TupleSpaceException If some general exception occurs on the tuple space.
	 * @throws TupleSpaceSecurityException If the key is not valid.
	 */
	public List<Tuple> read(Pattern pattern, String restriction, String key) 
			throws TupleSpaceException, TupleSpaceSecurityException;

	/**
	 * Read synchronously tuples in this domain.
	 * @param pattern the pattern used to search for desired tuples.
	 * @param restriction the filter applied over the tuples that matches the pattern.
	 * @param key a security key that permits the read operation.
	 * @param timeout the max amount of time (in milliseconds) that this method will block waiting for the tuples.
	 * @return the resulted list of tuples.
	 * @throws TupleSpaceException If some general exception occurs on the tuple space.
	 * @throws TupleSpaceSecurityException If the key is not valid.
	 */
	public List<Tuple> readSync(Pattern pattern, String restriction, String key, long timeout) 
			throws TupleSpaceException, TupleSpaceSecurityException;

	/**
	 * Read one single tuple in this domain.
	 * @param pattern the pattern used to search for desired tuples.
	 * @param restriction the filter applied over the tuples that matches the pattern.
	 * @param key a security key that permits the read operation.
	 * @return the last resulted tuple.
	 * @throws TupleSpaceException If some general exception occurs on the tuple space.
	 * @throws TupleSpaceSecurityException If the key is not valid.
	 */
	public Tuple readOne(Pattern pattern, String restriction, String key) 
			throws TupleSpaceException, TupleSpaceSecurityException;

	/**
	 * Read synchronously one tuple in this domain.
	 * @param pattern the pattern used to search for desired tuples.
	 * @param restriction the filter applied over the tuples that matches the pattern.
	 * @param key a security key that permits the read operation.
	 * @param timeout the max amount of time (in milliseconds) that this method will block waiting for the tuple.
	 * @return the last resulted tuple.
	 * @throws TupleSpaceException If some general exception occurs on the tuple space.
	 * @throws TupleSpaceSecurityException If the key is not valid.
	 */
	public Tuple readOneSync(Pattern pattern, String restriction, String key, long timeout) 
			throws TupleSpaceException, TupleSpaceSecurityException;

	/**
	 * Take tuples on this domain, these tuples are deleted after the return.
	 * @param pattern the pattern used to search for desired tuples.
	 * @param restriction the filter applied over the tuples that matches the pattern.
	 * @param key a security key that permits the take operation.
	 * @return the resulted list of tuples.
	 * @throws TupleSpaceException If some general exception occurs on the tuple space.
	 * @throws TupleSpaceSecurityException If the key is not valid.
	 */
	public List<Tuple> take(Pattern pattern, String restriction, String key) 
			throws TupleSpaceException, TupleSpaceSecurityException;

	/**
	 * Take synchronously tuples on this domain, these tuples are deleted after the return.
	 * @param pattern the pattern used to search for desired tuples.
	 * @param restriction the filter applied over the tuples that matches the pattern.
	 * @param key a security key that permits the take operation.
	 * @param timeout the max amount of time (in milliseconds) that this method will block waiting for the tuples.
	 * @return the resulted list of tuples.
	 * @throws TupleSpaceException If some general exception occurs on the tuple space.
	 * @throws TupleSpaceSecurityException If the key is not valid.
	 */
	public List<Tuple> takeSync(Pattern pattern, String restriction, String key, long timeout) 
			throws TupleSpaceException, TupleSpaceSecurityException;

	/**
	 * Take one single tuple in this domain.
	 * @param pattern the pattern used to search for desired tuples.
	 * @param restriction the filter applied over the tuples that matches the pattern.
	 * @param key a security key that permits the take operation.
	 * @return the last resulted tuple.
	 * @throws TupleSpaceException If some general exception occurs on the tuple space.
	 * @throws TupleSpaceSecurityException If the key is not valid.
	 */
	public Tuple takeOne(Pattern pattern, String restriction, String key) 
			throws TupleSpaceException, TupleSpaceSecurityException;

	/**
	 * Take one single tuple in this domain.
	 * @param pattern the pattern used to search for desired tuples.
	 * @param restriction the filter applied over the tuples that matches the pattern.
	 * @param key a security key that permits the take operation.
	 * @param timeout the max amount of time (in milliseconds) that this method will block waiting for the tuple
	 * @return the last resulted tuple.
	 * @throws TupleSpaceException If some general exception occurs on the tuple space.
	 * @throws TupleSpaceSecurityException If the key is not valid.
	 */
	public Tuple takeOneSync(Pattern pattern, String restriction, String key, long timeout) 
			throws TupleSpaceException, TupleSpaceSecurityException;

	/**
	 * Subscribe to be notified when specific event occurs on the tuple space.
	 * @param reaction the callback used to be notified about tuples subscribed.
	 * @param event the type of event (PUT, READ, TAKE).
	 * @param key a security key that permits the subscribe operation.
	 * @return the subscribe id.
	 * @throws TupleSpaceException If some general exception occurs on the tuple space.
	 * @throws TupleSpaceSecurityException If the key is not valid.
	 */
	public String subscribe(IReaction reaction, String event, String key) 
			throws TupleSpaceException, TupleSpaceSecurityException;

	/**
	 * Unsubscribe to no longer be notified when a specific event occurs in the tuple space.
	 * @param reactionId the id of the subscribe that you want to cancel.
	 * @param key a security key that permits the unsubscribe operation.
	 * @throws TupleSpaceException If some general exception occurs on the tuple space.
	 * @throws TupleSpaceSecurityException If the key is not valid.
	 */
	public void unsubscribe(String reactionId, String key) 
			throws TupleSpaceException, TupleSpaceSecurityException;
}
