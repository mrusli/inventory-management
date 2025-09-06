package com.pyramix.persistence.common.dao;

import java.util.List;

public interface Dao {
	@SuppressWarnings("rawtypes")
	public Object findById(Class target, Long id) throws Exception;
	
	@SuppressWarnings("rawtypes")
	public List findAll(Class target) throws Exception;
	
	public void save(Object object) throws Exception;
	
	public Object update(Object object) throws Exception;

	public void delete(Object object) throws Exception;
}
