package com.pyramix.persistence.company.dao.hibernate;

import java.util.List;

import com.pyramix.domain.entity.Ent_Company;
import com.pyramix.persistence.common.dao.hibernate.DaoHibernate;
import com.pyramix.persistence.company.dao.CompanyDao;

public class CompanyHibernate extends DaoHibernate implements CompanyDao {

	@Override
	public Ent_Company findCompanyById(long id) throws Exception {
		
		return (Ent_Company) super.findById(Ent_Company.class, id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Ent_Company> findAllCompany() throws Exception {
		
		return super.findAll(Ent_Company.class);
	}

	@Override
	public Ent_Company update(Ent_Company company) throws Exception {
		
		return (Ent_Company) super.update(company);
	}

	@Override
	public void save(Ent_Company company) throws Exception {
		
		super.save(company);
	}

	@Override
	public void delete(Ent_Company company) throws Exception {
		
		super.delete(company);
	}

}
