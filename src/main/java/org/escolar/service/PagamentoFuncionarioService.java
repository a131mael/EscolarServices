package org.escolar.service;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.escolar.model.Funcionario;
import org.escolar.model.PagamentoFuncionario;

@Stateless
public class PagamentoFuncionarioService {

    @PersistenceContext(unitName = "EscolarDS")
    private EntityManager em;

    public PagamentoFuncionario salvar(PagamentoFuncionario p) {
        em.persist(p);
        em.flush();
        return p;
    }

    public List<PagamentoFuncionario> findByFuncionario(Long funcionarioId) {
        return em.createQuery(
                "SELECT p FROM PagamentoFuncionario p WHERE p.funcionario.id = :id ORDER BY p.dataPagamento DESC",
                PagamentoFuncionario.class)
                .setParameter("id", funcionarioId)
                .getResultList();
    }

    public boolean temPagamentoUltimos7Dias(Long funcionarioId) {
        Date limite = new Date(System.currentTimeMillis() - 7L * 24 * 60 * 60 * 1000);
        Long count = em.createQuery(
                "SELECT COUNT(p) FROM PagamentoFuncionario p WHERE p.funcionario.id = :id AND p.dataPagamento >= :limite AND p.status = 'SUCESSO'",
                Long.class)
                .setParameter("id", funcionarioId)
                .setParameter("limite", limite)
                .getSingleResult();
        return count > 0;
    }

    public void atualizarUltimoPagamento(Funcionario f, Date data) {
        Funcionario managed = em.find(Funcionario.class, f.getId());
        managed.setUltimoPagamento(data);
        em.merge(managed);
        em.flush();
    }

    public long contarPagamentosHoje() {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(java.util.Calendar.HOUR_OF_DAY, 0);
        cal.set(java.util.Calendar.MINUTE, 0);
        cal.set(java.util.Calendar.SECOND, 0);
        cal.set(java.util.Calendar.MILLISECOND, 0);
        Date inicioDia = cal.getTime();
        return em.createQuery(
                "SELECT COUNT(p) FROM PagamentoFuncionario p WHERE p.dataPagamento >= :inicio AND p.status = 'SUCESSO'",
                Long.class)
                .setParameter("inicio", inicioDia)
                .getSingleResult();
    }
}
