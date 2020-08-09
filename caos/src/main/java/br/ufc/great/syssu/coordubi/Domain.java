/*******************************************************************************
 * Copyright (c) 2015 LG Electronics. All Rights Reserved. This software is the 
 * confidential and proprietary information of LG Electronics. You shall not
 * disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with LG Electronics.
 *******************************************************************************/
package br.ufc.great.syssu.coordubi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import android.os.DeadObjectException;
import br.ufc.great.syssu.base.FilterException;
import br.ufc.great.syssu.base.Pattern;
import br.ufc.great.syssu.base.Query;
import br.ufc.great.syssu.base.Tuple;
import br.ufc.great.syssu.base.TupleSpaceException;
import br.ufc.great.syssu.base.TupleSpaceSecurityException;
import br.ufc.great.syssu.base.interfaces.IDomain;
import br.ufc.great.syssu.base.interfaces.IFilter;
import br.ufc.great.syssu.base.interfaces.ILocalDomain;
import br.ufc.great.syssu.base.interfaces.IReaction;
import br.ufc.great.syssu.coordubi.security.DomainSecurityChecker;

public class Domain implements IDomain, ILocalDomain {

	private static long sEventId = 1;

	private String mName;
	private IDomain mParent;

	private Map<Integer, List<Tuple>> mCollections;
	private Map<String, Domain> mSubDomains;
	private Map<String, List<IReaction>> mReactions;

	Domain(String name) throws TupleSpaceException {
		verifyDomainName(name);
		this.mName = name;
		this.mCollections = new HashMap<Integer, List<Tuple>>();
		this.mSubDomains = new HashMap<String, Domain>();
		this.mReactions = new HashMap<String, List<IReaction>>();
	}

	@Override
	public String getName() {
		return mName;
	}

	public IDomain getDomain(String domainName) throws TupleSpaceException {
		verifyDomainName(domainName);

		if (!mSubDomains.containsKey(domainName)) {
			Domain domain = new Domain(domainName);
			domain.setParent(this);
			mSubDomains.put(domainName, domain);
		}
		return mSubDomains.get(domainName);
	}

	@Override
	public void put(Tuple tuple, String key) throws TupleSpaceException, 
		TupleSpaceSecurityException {
		if (!DomainSecurityChecker.getInstance().canPut(getName(), key)) {
			throw new TupleSpaceSecurityException("No permission to put method");
		}

		put(tuple);
	}

	@Override
	public List<Tuple> read(Pattern pattern, String restriction, String key) 
			throws TupleSpaceException, TupleSpaceSecurityException {
		if (!DomainSecurityChecker.getInstance().canRead(getName(), key)) {
			throw new TupleSpaceSecurityException("No permission to read method");
		}

		return read(pattern, restriction);
	}

	@Override
	public List<Tuple> readSync(Pattern pattern, String restriction, String key, long timeout) 
			throws TupleSpaceException, TupleSpaceSecurityException {
		if (!DomainSecurityChecker.getInstance().canRead(getName(), key)) {
			throw new TupleSpaceSecurityException("No permission to read sync method");
		}

		return readSync(pattern, restriction, timeout);
	}

	@Override
	public Tuple readOne(Pattern pattern, String restriction, String key)
			throws TupleSpaceException, TupleSpaceSecurityException {
		if (!DomainSecurityChecker.getInstance().canRead(getName(), key)) {
			throw new TupleSpaceSecurityException("No permission to read one method");
		}
		return readOne(pattern, restriction);
	}

	@Override
	public Tuple readOneSync(Pattern pattern, String restriction, String key, long timeout) 
			throws TupleSpaceException, TupleSpaceSecurityException {
		if (!DomainSecurityChecker.getInstance().canRead(getName(), key)) {
			throw new TupleSpaceSecurityException("No permission to "
					+ "read one sync method");
		}
		return readOneSync(pattern, restriction, timeout);
	}

	@Override
	public List<Tuple> take(Pattern pattern, String restriction, String key) 
			throws TupleSpaceException, TupleSpaceSecurityException {
		if (!DomainSecurityChecker.getInstance().canTake(getName(), key)) {
			throw new TupleSpaceSecurityException("No permission to take method");
		}
		return take(pattern, restriction);
	}

	@Override
	public List<Tuple> takeSync(Pattern pattern, String restriction, String key, long timeout) 
			throws TupleSpaceException, TupleSpaceSecurityException {
		if (!DomainSecurityChecker.getInstance().canTake(getName(), key)) {
			throw new TupleSpaceSecurityException("No permission to take sync method");
		}
		return takeSync(pattern, restriction, timeout);
	}

	@Override
	public Tuple takeOne(Pattern pattern, String restriction, String key) 
			throws TupleSpaceException, TupleSpaceSecurityException {
		if (!DomainSecurityChecker.getInstance().canTake(getName(), key)) {
			throw new TupleSpaceSecurityException("No permission to take onde method");
		}
		return takeOne(pattern, restriction);
	}

	@Override
	public Tuple takeOneSync(Pattern pattern, String restriction, String key, long timeout) 
			throws TupleSpaceException, TupleSpaceSecurityException {
		if (!DomainSecurityChecker.getInstance().canTake(getName(), key)) {
			throw new TupleSpaceSecurityException("No permission to "
					+ "take one syncmethod");
		}
		return takeOneSync(pattern, restriction, timeout);
	}

	@Override
	public String subscribe(IReaction reaction, String event, String key) 
			throws TupleSpaceException, TupleSpaceSecurityException {
		if (!DomainSecurityChecker.getInstance().canRead(getName(), key)) {
			throw new TupleSpaceSecurityException("No permission to take method");
		}
		return subscribe(reaction, event);
	}

	@Override
	public void unsubscribe(String reactionId, String key) 
			throws TupleSpaceException, TupleSpaceSecurityException {
		if (!DomainSecurityChecker.getInstance().canRead(getName(), key)) {
			throw new TupleSpaceSecurityException("No permission to take method");
		}
		unsubscribe(reactionId);
	}

	/**
	 * Coloca o interesse de uma informação contextual.
	 * @param tuple Tupla contendo a informação contextual.
	 */
	private synchronized void put(Tuple tuple) {
		notifyReactions(tuple, "put");

		tuple.setPutTime(System.currentTimeMillis());

		if (!mCollections.containsKey(tuple.size())) {
			mCollections.put(tuple.size(), new ArrayList<Tuple>());
		}
		mCollections.get(tuple.size()).add(tuple);

		notifyReaders();
	}

	/**
	 * Método para ler uma tupla do espaço de tuplas.
	 * @param pattern 
	 * @param restriction
	 * @return
	 * @throws TupleSpaceException
	 */
	List<Tuple> read(Pattern pattern, String restriction) throws TupleSpaceException {
		List<Tuple> tuples = new ArrayList<Tuple>();

		try {
			List<Tuple> tempTuples = find(pattern, restriction);
			tuples.addAll(tempTuples);
			synchronized (this) {
				for (Tuple tuple : tempTuples) {
					notifyReactions(tuple, "read");
				}
			}			
			for (Domain domain : mSubDomains.values()) {
				tuples.addAll(domain.read(pattern, restriction));
			}

		} catch (FilterException ex) {
			throw new TupleSpaceException(ex);
		}
		return tuples;
	}

	/**
	 * Método para ler uma tupla do espaço de tuplas com um limite de tempo.
	 * @param pattern
	 * @param restriction
	 * @param timeout
	 * @return
	 * @throws TupleSpaceException
	 */
	private List<Tuple> readSync(Pattern pattern, String restriction, long timeout) 
			throws TupleSpaceException {
		List<Tuple> result = read(pattern, restriction);

		// take before reading
		while (result.isEmpty()) {
			try {
				result = read(pattern, restriction);
				synchronized (this) {
					if (result.size() == 0) {
						if (timeout > 0) {
							wait(timeout);
						} else {
							wait();
						}
					}
				}
			} catch (InterruptedException ex) {
				throw new TupleSpaceException(ex.getMessage());
			}
		}
		return result;
	}

	/**
	 * Metodo para ler uma tupla.
	 * @param pattern
	 * @param restriction
	 * @return
	 * @throws TupleSpaceException
	 */
	private Tuple readOne(Pattern pattern, String restriction) throws TupleSpaceException {
		List<Tuple> all = read(pattern, restriction);
		if (all.size() > 0) {	
			Random generator = new Random();
			Tuple tuple = all.get(generator.nextInt(all.size())); 
			notifyReactions(tuple, "readone");
			return tuple;
		}
		return null;
	}

	/**
	 * Metodo para ler uma tupla com um limite de tempo.
	 * @param pattern
	 * @param restriction
	 * @param timeout
	 * @return
	 * @throws TupleSpaceException
	 */
	private Tuple readOneSync(Pattern pattern, String restriction, long timeout) 
			throws TupleSpaceException {
		Tuple result = readOne(pattern, restriction);
		while (result == null) {
			try {
				Thread.sleep(100);
				result = readOne(pattern, restriction);
				synchronized (this) {
					if (result == null) {
						if (timeout > 0) {
							wait(timeout);
						} else {
							wait();
						}
					}
				}
			} catch (InterruptedException ex) {
				throw new TupleSpaceException(ex.getMessage());
			}
		}
		return result;
	}

	/**
	 * Método que retira o interesse de uma informação contextual.
	 * @param pattern
	 * @param restriction
	 * @return
	 * @throws TupleSpaceException
	 */
	List<Tuple> take(Pattern pattern, String restriction) throws TupleSpaceException {
		List<Tuple> tuples = new ArrayList<Tuple>();

		try {
			List<Tuple> tempTuples = find(pattern, restriction);
			tuples.addAll(tempTuples);
			synchronized (this) {
				for (Tuple tuple : tempTuples) {
					mCollections.get(tuple.size()).remove(tuple);
					notifyReactions(tuple, "take");
				}
			}

			for (Domain domain : mSubDomains.values()) {
				tuples.addAll(domain.take(pattern, restriction));
			}
		} catch (FilterException ex) {
			throw new TupleSpaceException(ex);
		}

		return tuples;
	}

	/**
	 * Método que retira o interesse de uma informação contextual com um limite de tempo
	 * @param pattern
	 * @param restriction
	 * @param timeout
	 * @return
	 * @throws TupleSpaceException
	 */
	private List<Tuple> takeSync(Pattern pattern, String restriction, long timeout) 
			throws TupleSpaceException {
		List<Tuple> result = take(pattern, restriction);
		while (result.size() == 0) {
			try {
				Thread.sleep(100);
				result = take(pattern, restriction);
				synchronized (this) {
					if (result.size() == 0) {
						if (timeout > 0) {
							wait(timeout);
						} else {
							wait();
						}
					}
				}
			} catch (InterruptedException ex) {
				throw new TupleSpaceException(ex.getMessage());
			}
		}
		return result;
	}

	/**
	 * Método que retira o interesse de uma informação contextual.
	 * @param pattern
	 * @param restriction
	 * @return
	 * @throws TupleSpaceException
	 */
	private Tuple takeOne(Pattern pattern, String restriction) throws TupleSpaceException {
		List<Tuple> tuples = read(pattern, restriction);
		if (tuples.size() > 0) {
			Random generator = new Random();
			Tuple tuple = tuples.get(generator.nextInt(tuples.size()));
			synchronized (this) {
				removeTuple(tuple);
				notifyReactions(tuple, "take");
			}
			return tuple;
		}
		return null;
	}

	/**
	 * Método que retira o interesse de uma informação contextual com limite de tempo.
	 * @param pattern
	 * @param restriction
	 * @param timeout
	 * @return
	 * @throws TupleSpaceException
	 */
	private Tuple takeOneSync(Pattern pattern, String restriction, long timeout) 
			throws TupleSpaceException {
		Tuple result = null;
		while (result == null) {
			try {
				Thread.sleep(100);
				result = takeOne(pattern, restriction);
				synchronized (this) {
					if (result == null) {
						if (timeout > 0) {
							wait(timeout);
						} else {
							wait();
						}
					}
				}
			} catch (InterruptedException ex) {
				throw new TupleSpaceException(ex.getMessage());
			}
		}
		return result;
	}

	/**
	 * Subscrição de uma informação contextual.
	 * @param reaction
	 * @param event
	 * @return
	 * @throws TupleSpaceException
	 */
	private String subscribe(IReaction reaction, String event) throws TupleSpaceException {
		if (!mReactions.containsKey(event)) {
			mReactions.put(event, new ArrayList<IReaction>());
		}

		Long id = sEventId++;
		reaction.setId(id.toString());

		mReactions.get(event).add(reaction);
		return id.toString();
	}

	/**
	 * Retira uma subscrição.
	 * @param reactionId
	 * @throws TupleSpaceException
	 */
	private void unsubscribe(String reactionId) throws TupleSpaceException {
		for (List<IReaction> eventReactions : mReactions.values()) {
			for (IReaction r : eventReactions) {
				if (r.getId().equals(reactionId)) {
					eventReactions.remove(r);
					return;
				}
			}
		}
	}

	/**
	 * Encontrar tuplas de acordo com o pattern.
	 * @param pattern
	 * @param restriction
	 * @return
	 * @throws FilterException
	 */
	private List<Tuple> find(Pattern pattern, String restriction) throws FilterException {
		return matchTuples(new Query(pattern, restriction), filterTuples(pattern));
	}

	/**
	 * Associação de tuplas.
	 * @param query
	 * @param candidates
	 * @return
	 * @throws FilterException
	 */
	private List<Tuple> matchTuples(Query query, List<Tuple> candidates)
			throws FilterException {
		List<Tuple> tuples = new ArrayList<Tuple>();

		for (Tuple tuple : candidates) {
			if (!tuple.isAlive()) {
				removeTuple(tuple);
				continue;
			}

			if (tuple.matches(query)) {
				tuples.add(tuple);
			}
		}
		return tuples;
	}

	/**
	 * Filtro para filtrar as informações contextuais.
	 * @param pattern
	 * @return
	 */
	private List<Tuple> filterTuples(Pattern pattern) {
		List<Tuple> candidates = new ArrayList<Tuple>();
		for (Integer size : mCollections.keySet()) {
			if (size >= pattern.size()) {
				List<Tuple> coll = mCollections.get(size);
				if (coll != null) {
					candidates.addAll(coll);
				}
			}
		}
		return candidates;
	}

	/**
	 * Setar parent de um domain. 
	 * @param parent
	 */
	private void setParent(Domain parent) {
		this.mParent = parent;
	}

	/**
	 * Verifica o nome do Domain.
	 * @param domainName
	 * @throws TupleSpaceException
	 */
	private void verifyDomainName(String domainName) throws TupleSpaceException {
		if (domainName == null || domainName.equals("") || domainName.indexOf(".") != -1) {
			throw new TupleSpaceException("Invalid domain name.");
		}
	}

	/**
	 * Notifica os readers.
	 */
	private void notifyReaders() {
		synchronized (this) {
			notifyAll();
		}
		if (mParent != null) {
			((Domain) mParent).notifyReaders();
		}
	}

	/**
	 * Notifica as subscrições.
	 * @param tuple
	 * @param event
	 */
	private void notifyReactions(Tuple tuple, String event) {
		IReaction lastReaction = null;
		
		try {
			if (mReactions.containsKey(event)) {
                Map<String, List<IReaction>> reactionsTemp = new HashMap<String, 
                		List<IReaction>>(mReactions);
				for (IReaction reaction : new ArrayList<IReaction>(
						reactionsTemp.get(event))) {
					if (tuple != null){
						lastReaction = reaction;
						
						if (reaction.getJavaFilter() != null) {
							if (tuple.matches(
							  new Query(
							    reaction.getPattern(), 
							      reaction.getJavaFilter()))) {
								reaction.react(tuple);
							}
						} else if (tuple.matches(
							  new Query(
							    reaction.getPattern(),
							      reaction.getJavaScriptFilter()))) {
							reaction.react(tuple);
						}
					}
				}
			}
			if (mParent != null) {
				((Domain) mParent).notifyReactions(tuple, event);
			}
		} catch (DeadObjectException ex) {
			ex.printStackTrace();
			try {
				if (lastReaction != null) {
					unsubscribe(lastReaction.getId(), event);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Remove um tupla do espaço de tuplas.
	 * @param tuple
	 * @return
	 */
	private boolean removeTuple(Tuple tuple) {
		for (Integer size : mCollections.keySet()) {
			if (size == tuple.size()) {
				List<Tuple> coll = mCollections.get(size);
				if (coll != null && coll.contains(tuple)) {
					coll.remove(tuple);
					return true;
				}
			}
		}

		for (IDomain domain : mSubDomains.values()) {
			if (((Domain) domain).removeTuple(tuple)) {
				return true;
			}
		}

		return false;
	}
	
	
	// Filtro com Java
	@Override
	public List<Tuple> read(Pattern pattern, IFilter restriction, String key) 
		throws TupleSpaceException, TupleSpaceSecurityException {
		
		if (!DomainSecurityChecker.getInstance().canRead(getName(), key)) {
			throw new TupleSpaceSecurityException("No permission to read method");
		}
		
		return read(pattern, restriction);
	}

	@Override
	public List<Tuple> readSync(Pattern pattern, IFilter restriction, String key, long timeout) 
		throws TupleSpaceException, TupleSpaceSecurityException {
		if (!DomainSecurityChecker.getInstance().canRead(getName(), key)) {
			throw new TupleSpaceSecurityException("No permission to read sync method");
		}
		
		return readSync(pattern, restriction, timeout);
	}

	@Override
	public Tuple readOne(Pattern pattern, IFilter restriction, String key)
		throws TupleSpaceException, TupleSpaceSecurityException {
		if (!DomainSecurityChecker.getInstance().canRead(getName(), key)) {
			throw new TupleSpaceSecurityException("No permission to read one method");
		}
		return readOne(pattern, restriction);
	}

	@Override
	public Tuple readOneSync(Pattern pattern, IFilter restriction, String key, long timeout) 
		throws TupleSpaceException, TupleSpaceSecurityException {
		if (!DomainSecurityChecker.getInstance().canRead(getName(), key)) {
			throw new TupleSpaceSecurityException("No permission to "
					+ "read one sync method");
		}
		return readOneSync(pattern, restriction, timeout);
	}

	@Override
	public List<Tuple> take(Pattern pattern, IFilter restriction, String key) 
		throws TupleSpaceException, TupleSpaceSecurityException {
		if (!DomainSecurityChecker.getInstance().canTake(getName(), key)) {
			throw new TupleSpaceSecurityException("No permission to take method");
		}
		return take(pattern, restriction);
	}

	@Override
	public List<Tuple> takeSync(Pattern pattern, IFilter restriction, String key, long timeout) 
		throws TupleSpaceException, TupleSpaceSecurityException {
		if (!DomainSecurityChecker.getInstance().canTake(getName(), key)) {
			throw new TupleSpaceSecurityException("No permission to take sync method");
		}
		return takeSync(pattern, restriction, timeout);
	}

	@Override
	public Tuple takeOne(Pattern pattern, IFilter restriction, String key) 
		throws TupleSpaceException, TupleSpaceSecurityException {
		if (!DomainSecurityChecker.getInstance().canTake(getName(), key)) {
			throw new TupleSpaceSecurityException("No permission to take onde method");
		}
		return takeOne(pattern, restriction);
	}

	@Override
	public Tuple takeOneSync(Pattern pattern, IFilter restriction, String key, long timeout) 
		throws TupleSpaceException, TupleSpaceSecurityException {
		if (!DomainSecurityChecker.getInstance().canTake(getName(), key)) {
			throw new TupleSpaceSecurityException("No permission to "
					+ "take one syncmethod");
		}
		return takeOneSync(pattern, restriction, timeout);
	}

	List<Tuple> read(Pattern pattern, IFilter restriction) throws TupleSpaceException {
		List<Tuple> tuples = new ArrayList<Tuple>();

		try {
			List<Tuple> tempTuples = find(pattern, restriction);
			tuples.addAll(tempTuples);
			
			synchronized (this) {
				for (Tuple tuple : tempTuples) {
					notifyReactions(tuple, "read");
				}
			}
			
			for (Domain domain : mSubDomains.values()) {
				tuples.addAll(domain.read(pattern, restriction));
			}
		} catch (FilterException ex) {
			throw new TupleSpaceException(ex);
		}
		return tuples;
	}

	private List<Tuple> readSync(Pattern pattern, IFilter restriction, 
			long timeout) throws TupleSpaceException {

		List<Tuple> result = read(pattern, restriction);

		long waitTime = 0; // FIXME synchronized is needed to create waitTime????

		while (result.isEmpty()) {
			try {
				Thread.sleep(100);
				waitTime += 100;
				result = read(pattern, restriction);
				if (result.size() == 0) {
					if (waitTime >= timeout) {
						break;
					}
				}
			} catch (InterruptedException ex) {
				throw new TupleSpaceException(ex.getMessage());
			}
		}

		// take after reading
		take(pattern, restriction);

		return result;
	}

	/**
	 * Método que ler uma tupla do espaço de tuplas.
	 * @param pattern
	 * @param restriction
	 * @return
	 * @throws TupleSpaceException
	 */
	private Tuple readOne(Pattern pattern, IFilter restriction) throws TupleSpaceException {
		List<Tuple> all = read(pattern, restriction);
		if (all.size() > 0) {
			Random generator = new Random();
			return all.get(generator.nextInt(all.size()));
		}
		return null;
	}

	/**
	 * Método que ler uma tupla do espaço de tuplas com limite de tempo.
	 * @param pattern
	 * @param restriction
	 * @param timeout
	 * @return
	 * @throws TupleSpaceException
	 */
	private Tuple readOneSync(Pattern pattern, IFilter restriction, 
			long timeout) throws TupleSpaceException {
		Tuple result = readOne(pattern, restriction);
		while (result == null) {
			try {
				Thread.sleep(100);
				result = readOne(pattern, restriction);
				synchronized (this) {
					if (result == null) {
						if (timeout > 0) {
							wait(timeout);
						} else {
							wait();
						}
					}
				}
			} catch (InterruptedException ex) {
				throw new TupleSpaceException(ex.getMessage());
			}
		}
		return result;
	}

	/**
	 * Método que retira o interesse de uma aplicação por um tipo de informação contextual.
	 * @param pattern
	 * @param restriction
	 * @return
	 * @throws TupleSpaceException
	 */
	List<Tuple> take(Pattern pattern, IFilter restriction) throws TupleSpaceException {
		List<Tuple> tuples = new ArrayList<Tuple>();

		try {
			List<Tuple> tempTuples = find(pattern, restriction);
			tuples.addAll(tempTuples);
			synchronized (this) {
				for (Tuple tuple : tempTuples) {
					mCollections.get(tuple.size()).remove(tuple);
					notifyReactions(tuple, "take");
				}
			}
			
//			synchronized (this) {
//				for (Tuple tuple : tempTuples) {
//					notifyReactions(tuple, "read");
//				}
//			}

			for (Domain domain : mSubDomains.values()) {
				tuples.addAll(domain.take(pattern, restriction));
			}
		} catch (FilterException ex) {
			throw new TupleSpaceException(ex);
		}

		return tuples;
	}

	private List<Tuple> takeSync(Pattern pattern, IFilter restriction, 
			long timeout) throws TupleSpaceException {
		List<Tuple> result = take(pattern, restriction);
		while (result.size() == 0) {
			try {
				Thread.sleep(100);
				result = take(pattern, restriction);
				synchronized (this) {
					if (result.size() == 0) {
						if (timeout > 0) {
							wait(timeout);
						} else {
							wait();
						}
					}
				}
			} catch (InterruptedException ex) {
				throw new TupleSpaceException(ex.getMessage());
			}
		}
		return result;
	}

	/**
	 * Método que retira o interesse de uma aplicação por um tipo de informação contextual.
	 * @param pattern
	 * @param restriction
	 * @return
	 * @throws TupleSpaceException
	 */
	private Tuple takeOne(Pattern pattern, IFilter restriction) throws TupleSpaceException {
		List<Tuple> tuples = read(pattern, restriction);
		if (tuples.size() > 0) {
			Random generator = new Random();
			Tuple tuple = tuples.get(generator.nextInt(tuples.size()));
			synchronized (this) {
				removeTuple(tuple);
				notifyReactions(tuple, "take");
			}
			return tuple;
		}
		return null;
	}
	
	private Tuple takeOneSync(Pattern pattern, IFilter restriction, 
			long timeout) throws TupleSpaceException {
		Tuple result = null;
		while (result == null) {
			try {
				Thread.sleep(100);
				result = takeOne(pattern, restriction);
				synchronized (this) {
					if (result == null) {
						if (timeout > 0) {
							wait(timeout);
						} else {
							wait();
						}
					}
				}
			} catch (InterruptedException ex) {
				throw new TupleSpaceException(ex.getMessage());
			}
		}
		return result;
	}

	/**
	 * Método para procurar uma tupla no espaço de tuplas.
	 * @param pattern
	 * @param restriction
	 * @return
	 * @throws FilterException
	 */
	private List<Tuple> find(Pattern pattern, IFilter restriction) throws FilterException {
		return matchTuples(new Query(pattern, restriction), filterTuples(pattern));
	}
}
