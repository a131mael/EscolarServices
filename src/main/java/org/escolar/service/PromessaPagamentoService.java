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
import org.escolar.util.Service;

@Stateless
public class PromessaPagamentoService extends Service implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@PersistenceContext(unitName = "EscolarDS")
	private EntityManager em;

	public PromessaPagamentoBoleto findById(Long id) {
		return em.find(PromessaPagamentoBoleto.class, id);
	}

	public void save(PromessaPagamentoBoleto promessa) {
		Boleto bol = null;
		if(promessa.getBoleto() != null){
			bol = em.find(Boleto.class, promessa.getBoleto().getId());
		}
		
		ContratoAluno contrato = null;
		if(promessa.getContrato() != null){
			contrato = em.find(ContratoAluno.class, promessa.getContrato().getId());
		}
		Aluno aluno = null;
		if(promessa.getAluno() != null){
			aluno = em.find(Aluno.class, promessa.getAluno().getId());
		}
		promessa.setAluno(aluno);
		promessa.setContrato(contrato);
		promessa.setBoleto(bol);
		promessa.setAtivo(true);
		em.persist(promessa);
		em.flush();
	}

	public void remove(PromessaPagamentoBoleto promessa) {
		
		Boleto bol = null;
		if(promessa.getBoleto() != null){
			bol = em.find(Boleto.class, promessa.getBoleto().getId());
		}
		
		ContratoAluno contrato = null;
		if(promessa.getContrato() != null){
			contrato = em.find(ContratoAluno.class, promessa.getContrato().getId());
		}
		Aluno aluno = null;
		if(promessa.getAluno() != null){
			aluno = em.find(Aluno.class, promessa.getAluno().getId());
		}
		promessa = findById(promessa.getId());
		promessa.setAluno(aluno);
		promessa.setContrato(contrato);
		promessa.setBoleto(bol);
		promessa.setAtivo(false);
		em.merge(promessa);
		em.flush();
	}

	
	public List<PromessaPagamentoBoleto> findByParam(Long idboleto) {
		List<PromessaPagamentoBoleto> mensagens = new ArrayList<>();
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("select id from PromessaPagamentoBoleto mensagem  where 1=1 ");

			if (idboleto != null && idboleto > 0) {
				sql.append(" and boleto_id = ");
				sql.append(idboleto);
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
	
	public List<PromessaPagamentoBoleto> findByAluno(Long idALuno, boolean ativo) {
		List<PromessaPagamentoBoleto> mensagens = new ArrayList<>();
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("select id from PromessaPagamentoBoleto mensagem  where 1=1 ");

			if (idALuno != null && idALuno > 0) {
				sql.append(" and aluno_id= ");
				sql.append(idALuno);
			}
			
				sql.append(" and ativo= ");
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
	
	public List<PromessaPagamentoBoleto> findByAluno(Long idALuno) {
		List<PromessaPagamentoBoleto> mensagens = new ArrayList<>();
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("select id from PromessaPagamentoBoleto mensagem  where 1=1 ");

			if (idALuno != null && idALuno > 0) {
				sql.append(" and aluno_id= ");
				sql.append(idALuno);
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

}
