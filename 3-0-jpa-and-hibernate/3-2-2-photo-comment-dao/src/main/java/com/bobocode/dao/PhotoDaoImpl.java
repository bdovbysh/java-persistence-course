package com.bobocode.dao;

import com.bobocode.model.Photo;
import com.bobocode.model.PhotoComment;
import com.bobocode.util.ExerciseNotCompletedException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Please note that you should not use auto-commit mode for your implementation.
 */
public class PhotoDaoImpl implements PhotoDao {
    private EntityManagerFactory entityManagerFactory;

    public PhotoDaoImpl(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public void save(Photo photo) {
        executeInContext(em -> em.persist(photo));
    }

    @Override
    public Photo findById(long id) {
        return executeInContextWithResult(em -> em.find(Photo.class, id));
    }

    @Override
    public List<Photo> findAll() {
        return executeInContextWithResult(em -> {
            TypedQuery<Photo> selectAllPhotoQuery = em.createQuery("SELECT p from Photo p", Photo.class);
            return selectAllPhotoQuery.getResultList();
        });
    }

    @Override
    public void remove(Photo photo) {
        executeInContext(em -> {
            Photo entityFromContext = em.merge(photo);
            em.remove(entityFromContext);
        });
    }

    @Override
    public void addComment(long photoId, String comment) {
        executeInContext(em -> {
//            Photo photo = findById(photoId); Make call to a database
            Photo photo = em.getReference(Photo.class, photoId); // Reference from the Context, doesn't make a call to a database.
            PhotoComment photoComment = new PhotoComment(comment, photo);
            em.persist(photoComment);
        });
    }

    private void executeInContext(Consumer<EntityManager> consumer) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            consumer.accept(entityManager);
            entityManager.getTransaction().commit();
        }
        catch(Exception ex) {
            entityManager.getTransaction().rollback();
            throw ex;
        }
        finally {
            entityManager.close();
        }
    }


    private <T> T executeInContextWithResult(Function<EntityManager, T> function) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            T result = function.apply(entityManager);
            entityManager.getTransaction().commit();
            return result;
        }
        catch(Exception ex) {
            entityManager.getTransaction().rollback();
            throw ex;
        }
        finally {
            entityManager.close();
        }
    }


}
