package org.escolar.service;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.escolar.enums.CanalMensagem;
import org.escolar.model.Aluno;
import org.escolar.model.Boleto;
import org.escolar.model.ContratoAluno;
import org.escolar.model.MensagemAluno;
import org.escolar.model.PromessaPagamentoBoleto;
import org.escolar.model.SuspensaoContrato;
import org.escolar.util.Service;

@Stateless
public class SuspensaoContratoService extends Service implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@PersistenceContext(unitName = "EscolarDS")
	private EntityManager em;

	public SuspensaoContrato findById(Long id) {
		return em.find(SuspensaoContrato.class, id);
	}

	public void save(SuspensaoContrato suspensao) {
		ContratoAluno al = em.find(ContratoAluno.class, suspensao.getContrato().getId());
		suspensao.setContrato(al);
		suspensao.setAtivo(true);
		suspensao.setDataSuspensao(new Date());
		em.persist(suspensao);
		em.flush();
	}
	
	public void remove(SuspensaoContrato suspensao) {
		suspensao = findById(suspensao.getId());
		ContratoAluno al = em.find(ContratoAluno.class, suspensao.getContrato().getId());
		suspensao.setContrato(al);
		suspensao.setAtivo(false);
		suspensao.setDataContratoRetornado(new Date());
		em.persist(suspensao);
		em.flush();
	}

	public List<SuspensaoContrato> findByParam(Long idcontratoAluno) {
		List<SuspensaoContrato> mensagens = new ArrayList<>();
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("select id from SuspensaoContrato mensagem  where 1=1 ");

			if (idcontratoAluno != null && idcontratoAluno > 0) {
				sql.append(" and contrato_id = ");
				sql.append(idcontratoAluno);
			}

		
			Query query = em.createNativeQuery(sql.toString());
			List<BigInteger> codigo = (List<BigInteger>) query.getResultList();
			if (codigo == null) {
				return new ArrayList<>();
			}

			for (BigInteger id : codigo) {
				mensagens.add(findById(id.longValue()));
			}

			return mensagens;

		} catch (Exception e) {
			return mensagens;
		}
	}
	
	public List<SuspensaoContrato> findByParam(Long idcontratoAluno, boolean ativo) {
		List<SuspensaoContrato> mensagens = new ArrayList<>();
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("select id from SuspensaoContrato mensagem  where 1=1 ");

			if (idcontratoAluno != null && idcontratoAluno > 0) {
				sql.append(" and contrato_id = ");
				sql.append(idcontratoAluno);
			}
			sql.append(" and ativo = ");
			sql.append(ativo);
		
			Query query = em.createNativeQuery(sql.toString());
			List<BigInteger> codigo = (List<BigInteger>) query.getResultList();
			if (codigo == null) {
				return new ArrayList<>();
			}

			for (BigInteger id : codigo) {
				mensagens.add(findById(id.longValue()));
			}

			return mensagens;

		} catch (Exception e) {
			return mensagens;
		}
	}

}
