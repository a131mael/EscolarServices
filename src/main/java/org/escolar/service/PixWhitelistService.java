package org.escolar.service;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class PixWhitelistService {

    @PersistenceContext
    private EntityManager em;

    public boolean isChaveIsenta(String chavePix) {
        if (chavePix == null) return false;
        String chaveNormalizada = chavePix.replaceAll("\\D", "").toLowerCase().trim();
        try {
            Long count = em.createQuery(
                "SELECT COUNT(w) FROM PixWhitelist w WHERE LOWER(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(w.chavePix,'.',''),(','',''),'/',''),'-',''),'@','')) = :chave",
                Long.class)
                .setParameter("chave", chaveNormalizada)
                .getSingleResult();
            return count != null && count > 0;
        } catch (Exception e) {
            return false;
        }
    }
}
