/*******************************************************************************
 * Copyright (c) 2015 LG Electronics. All Rights Reserved. This software is the 
 * confidential and proprietary information of LG Electronics. You shall not
 * disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with LG Electronics.
 *******************************************************************************/
package br.ufc.great.syssu.coordubi.security;

import java.util.HashMap;
import java.util.Map;

public class DomainSecurityChecker {
	
	private static DomainSecurityChecker sInstance;
	private Map<String, Map<String, String>> mDomainKeys;
	
	/**
	 * Checa a segurança do Domain.
	 */
	private DomainSecurityChecker() {
		mDomainKeys = new HashMap<String, Map<String, String>>();
	}
	
	/**
	 * Retorna uma instancia do DomainSecurityChecker.
	 * @return
	 */
	public static DomainSecurityChecker getInstance() {
		if (sInstance == null) {
			sInstance = new DomainSecurityChecker();
		}
		return sInstance;
	}
	
	/**
	 * Verifica se pode ler uma informação contextual.
	 * @param domainName Nome do Domain
	 * @param key Context-Key
	 * @return True se sim, false se não.
	 */
	public boolean canRead(String domainName, String key) {
		Map<String, String> keys = mDomainKeys.get(domainName);
		return keys == null || keys.isEmpty()
			|| (keys.containsKey("put") && keys.get("put").equals(key))
			|| (keys.containsKey("take") && keys.get("take").equals(key))
			|| (keys.containsKey("read") && keys.get("read").equals(key));
	}
	
	/**
	 * Verifica se pode tirar o interesse de uma aplicação.
	 * @param domainName Nome do Domain
	 * @param key Context-Key
	 * @return True se sim, false se não.
	 */
	public boolean canTake(String domainName, String key) {
		Map<String, String> keys = mDomainKeys.get(domainName);
		return keys == null || keys.isEmpty()
			|| (keys.containsKey("put") && keys.get("put").equals(key))
			|| (keys.containsKey("take") && keys.get("take").equals(key));
	}
	
	/**
	 * Verifica se pode colocar o interesse de uma aplicação.
	 * @param domainName Nome do Domain
	 * @param key Context-Key
	 * @return True se sim, false se não.
	 */
	public boolean canPut(String domainName, String key) {
		Map<String, String> keys = mDomainKeys.get(domainName);
		return keys == null || keys.isEmpty()
			|| (keys.containsKey("put") && keys.get("put").equals(key));
	}

}
