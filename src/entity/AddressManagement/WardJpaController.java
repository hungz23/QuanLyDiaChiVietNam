/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity.AddressManagement;

import entity.AddressManagement.exceptions.NonexistentEntityException;
import entity.AddressManagement.exceptions.PreexistingEntityException;
import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 *
 * @author QuestionBoy
 */
public class WardJpaController implements Serializable {

    public WardJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Ward ward) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(ward);
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findWard(ward.getWardid()) != null) {
                throw new PreexistingEntityException("Ward " + ward + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Ward ward) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            ward = em.merge(ward);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = ward.getWardid();
                if (findWard(id) == null) {
                    throw new NonexistentEntityException("The ward with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(String id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Ward ward;
            try {
                ward = em.getReference(Ward.class, id);
                ward.getWardid();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The ward with id " + id + " no longer exists.", enfe);
            }
            em.remove(ward);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Ward> findWardEntities() {
        return findWardEntities(true, -1, -1);
    }

    public List<Ward> findWardEntities(int maxResults, int firstResult) {
        return findWardEntities(false, maxResults, firstResult);
    }

    private List<Ward> findWardEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Ward.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Ward findWard(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Ward.class, id);
        } finally {
            em.close();
        }
    }

    public int getWardCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Ward> rt = cq.from(Ward.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
