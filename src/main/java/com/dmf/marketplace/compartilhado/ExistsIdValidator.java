package com.dmf.marketplace.compartilhado;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ExistsIdValidator implements ConstraintValidator<ExistsId, Long> {

	private String fieldName;
	private Class<?> domainClass;

	@PersistenceContext
	private EntityManager manager;

	@Override
	public void initialize(ExistsId params) {
		fieldName = params.fieldName();
		domainClass = params.domainClass();
	}

	@Override
	public boolean isValid(Long value, ConstraintValidatorContext context) {
		if (value == null) {
			return true;
		}

		Query query = manager.createQuery("select 1 from "+ domainClass.getName()+" where "+ fieldName +"=:value");
		query.setParameter("value", value).setMaxResults(1);
		return !query.getResultList().isEmpty();
	}
}
