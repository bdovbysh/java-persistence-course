package com.bobocode.dao;

import com.bobocode.exception.AccountDaoException;
import com.bobocode.model.Account;
import org.hibernate.jpa.QueryHints;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.QueryHint;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public class AccountDaoImpl implements AccountDao {

    private EntityManagerFactory entityManagerFactory;

    public AccountDaoImpl(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public void save(Account account) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        try {
            entityManager.getTransaction().begin();

            entityManager.persist(account);

            entityManager.getTransaction().commit();
        }
        catch(Exception e) {
            entityManager.getTransaction().rollback();
            throw new AccountDaoException("Error while saving account", e);
        }
    }

    @Override
    public Account findById(Long id) {

        return applyInPersistenceContextWithResult(entityManager -> {
            return entityManager.find(Account.class, id);
        });

//        EntityManager entityManager = entityManagerFactory.createEntityManager();
//        try {
//            return entityManager.find(Account.class, id);
//        }
//        catch(Exception e) {
//            throw new AccountDaoException("Error while saving account", e);
//        }
    }

    @Override
    public Account findByEmail(String email) {

        return applyInPersistenceContextWithResult(
                (entityManager) -> {
                    TypedQuery<Account> query = entityManager.createQuery("SELECT A FROM Account A WHERE A.email = :email ", Account.class);
                    query.setParameter("email", email);
                    query.setHint(QueryHints.HINT_READONLY, true);
                    return query.getSingleResult();
                }
        );

//        EntityManager entityManager = entityManagerFactory.createEntityManager();
//        try {
//            TypedQuery<Account> query = entityManager.createQuery("SELECT A FROM Account A WHERE A.email = :email ", Account.class);
//            query.setParameter("email", email);
//
//            return query.getSingleResult();
//
//        }
//        catch(Exception e) {
//            throw new AccountDaoException("Error while using to findByEmail", e);
//        }
    }

    @Override
    public List<Account> findAll() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            TypedQuery<Account> selectAllQuery = entityManager.createQuery("SELECT A FROM Account A", Account.class);
            selectAllQuery.setHint(QueryHints.HINT_READONLY, true);
            return selectAllQuery.getResultList();
        }
        catch(Exception e) {
            throw new AccountDaoException("Error for findAll", e);
        }
    }

    @Override
    public void update(Account account) {

        applyInPersistenceContextWithoutResult(entityManager -> {
             entityManager.merge(account);
        });

//        EntityManager entityManager = entityManagerFactory.createEntityManager();
//        try {
//            entityManager.getTransaction().begin();
//            Account merge = entityManager.merge(account);
//            entityManager.getTransaction().commit();
//        }
//        catch(Exception e) {
//            entityManager.getTransaction().rollback();
//            throw new AccountDaoException("Error while updating account", e);
//        }
    }

    @Override
    public void remove(Account account) {
        applyInPersistenceContextWithoutResult(entityManager -> {
            Account managedAccount = entityManager.merge(account);
            entityManager.remove(managedAccount);
        });
    }

    private void applyInPersistenceContextWithoutResult(Consumer<EntityManager> entityManagerConsumer) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            entityManagerConsumer.accept(entityManager);
            entityManager.getTransaction().commit();
        }
        catch(Exception e) {
            entityManager.getTransaction().rollback();
            throw new AccountDaoException("Something goes wrong", e);
        }
        finally {
            entityManager.close();
        }
    }

    private <T> T applyInPersistenceContextWithResult(Function<EntityManager, T> entityManagerFunction) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        T result = null;
        try {
            entityManager.getTransaction().begin();
            result = entityManagerFunction.apply(entityManager);
            entityManager.getTransaction().commit();

            return result;
        }
        catch(Exception e) {
            entityManager.getTransaction().rollback();
            throw new AccountDaoException("Something goes wrong", e);
        }
        finally {
            entityManager.close();
        }
    }

}

