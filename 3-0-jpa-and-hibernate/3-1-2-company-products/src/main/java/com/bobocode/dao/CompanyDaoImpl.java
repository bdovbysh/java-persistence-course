package com.bobocode.dao;

import com.bobocode.exception.CompanyDaoException;
import com.bobocode.model.Company;
import com.bobocode.util.ExerciseNotCompletedException;
import org.hibernate.Session;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;

public class CompanyDaoImpl implements CompanyDao {

    private final static String GET_COMPANY_AND_PRODUCTS_BY_ID = "select c from Company c " +
            "join fetch c.products " +
            "where c.id = :id";

    private EntityManagerFactory entityManagerFactory;

    public CompanyDaoImpl(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public Company findByIdFetchProducts(Long id) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        Session session = entityManager.unwrap(Session.class);
        session.setDefaultReadOnly(true); // disable dirty check

        try {
            TypedQuery<Company> query = entityManager.createQuery(GET_COMPANY_AND_PRODUCTS_BY_ID, Company.class);
            query.setParameter("id", id);
            return query.getSingleResult();
        }
        catch(Exception ex) {
            throw new CompanyDaoException("Exception while searching Company by ID ", ex);
        }
        finally {
            entityManager.close();
        }
    }
}
