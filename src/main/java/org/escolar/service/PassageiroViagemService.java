package org.escolar.service;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.escolar.model.Frete;
import org.escolar.model.PassageiroViagem;

@Stateless
public class PassageiroViagemService {

    @PersistenceContext(unitName = "EscolarDS")
    private EntityManager em;

    public PassageiroViagem save(PassageiroViagem passageiro) {
        if (passageiro.getId() != null) {
            return em.merge(passageiro);
        }
        em.persist(passageiro);
        em.flush();
        return passageiro;
    }

    public void remover(Long id) {
        PassageiroViagem p = em.find(PassageiroViagem.class, id);
        if (p != null) {
            em.remove(p);
            em.flush();
        }
    }

    public List<PassageiroViagem> findByFrete(Frete frete) {
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<PassageiroViagem> cq = cb.createQuery(PassageiroViagem.class);
            Root<PassageiroViagem> root = cq.from(PassageiroViagem.class);
            Predicate where = cb.equal(root.get("frete"), frete);
            cq.select(root).where(where).orderBy(cb.asc(root.get("nome")));
            return em.createQuery(cq).getResultList();
        } catch (NoResultException e) {
            return new ArrayList<>();
        }
    }

    public boolean cpfJaCadastrado(Long freteId, String cpf) {
        try {
            Long count = em.createQuery(
                "SELECT COUNT(p) FROM PassageiroViagem p WHERE p.frete.id = :freteId AND p.cpf = :cpf",
                Long.class)
                .setParameter("freteId", freteId)
                .setParameter("cpf", cpf)
                .getSingleResult();
            return count > 0;
        } catch (Exception e) {
            return false;
        }
    }
}
