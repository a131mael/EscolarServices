package org.escolar.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;

import org.escolar.enums.BairroEnum;
import org.escolar.enums.EscolaEnum;
import org.escolar.enums.PerioddoEnum;
import org.escolar.enums.Serie;
import org.escolar.model.Aluno;
import org.escolar.model.Avaliacao;
import org.escolar.model.Recado;
import org.escolar.model.TabelaPrecos;
import org.escolar.util.Service;


@Stateless
public class TabelaPrecoService extends Service {


	@PersistenceContext(unitName = "EscolarDS")
	private EntityManager em;

	public TabelaPrecos findById(Long id) {
		return em.find(TabelaPrecos.class, id);
	}

	public String remover(Long idRecado){
		em.remove(findById(idRecado));
		return "index";
	}
	
	public List<TabelaPrecos> findAll() {
		try{
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<TabelaPrecos> criteria = cb.createQuery(TabelaPrecos.class);
			Root<TabelaPrecos> member = criteria.from(TabelaPrecos.class);
			criteria.select(member).orderBy(cb.desc(member.get("bairroCrianca")));
			return em.createQuery(criteria).getResultList();
	
		}catch(NoResultException nre){
			return new ArrayList<>();
		}catch(Exception e){
			e.printStackTrace();
			return new ArrayList<>();
		}
	}
	
	public TabelaPrecos save(TabelaPrecos tabelaPrecos) {
		TabelaPrecos user = null;
		try {
		
			if (tabelaPrecos.getId() != null && tabelaPrecos.getId() != 0L) {
				user = findById(tabelaPrecos.getId());
			} else {
				user = new TabelaPrecos();
			}
			
			user.setBairroCrianca(tabelaPrecos.getBairroCrianca());
			user.setBairroEscola(tabelaPrecos.getBairroEscola());
			user.setValorJaneiro(tabelaPrecos.getValorJaneiro());
			user.setValorFevereiro(tabelaPrecos.getValorFevereiro());
			user.setValorMarco(tabelaPrecos.getValorMarco());
			user.setValorMaio(tabelaPrecos.getValorMaio());
			
			user.setAno(tabelaPrecos.getAno());
			em.persist(user);
			
		} catch (ConstraintViolationException ce) {
			// Handle bean validation issues
			// builder = createViolationResponse(ce.getConstraintViolations());
		} catch (ValidationException e) {
			// Handle the unique constrain violation
			Map<String, String> responseObj = new HashMap<>();
			responseObj.put("email", "Email taken");

		} catch (Exception e) {
			// Handle generic exceptions
			Map<String, String> responseObj = new HashMap<>();
			responseObj.put("error", e.getMessage());

			e.printStackTrace();
		}

		return user;
	}

	public double getValor(int mesMatricula, Aluno aluno){
		TabelaPrecos tp = find(aluno.getBairroAluno(), aluno.getEscola().getBairroEnum());
		if(tp == null){
			tp = find(aluno.getEscola().getBairroEnum(),aluno.getBairroAluno());
		}
		double valor = 0;
		if(aluno.getEscola().equals(EscolaEnum.ADONAI)){
			valor -=40;
		}
		
		switch (mesMatricula) {
		case 1:
			valor+= tp.getValorJaneiro();
			break;
		case 2:
			valor+= tp.getValorFevereiro();
			break;
		case 3:
			valor+= tp.getValorMarco();
			break;

		default:
			valor+= tp.getValorMarco();
			break;
		}
		return valor;
	}
	
	public TabelaPrecos find(BairroEnum bairroCrianca, BairroEnum bairroEscola) {
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<TabelaPrecos> criteria = cb.createQuery(TabelaPrecos.class);
			Root<TabelaPrecos> member = criteria.from(TabelaPrecos.class);

			Predicate whereSerie = null;
			Predicate wherePeriodo = null;
			Predicate whereAno = null;

			StringBuilder sb = new StringBuilder();
			whereSerie = cb.equal(member.get("bairroCrianca"), bairroCrianca);
			wherePeriodo = cb.equal(member.get("bairroEscola"), bairroEscola);
			whereAno = cb.equal(member.get("ano"), "2019");

			criteria.select(member).where(whereSerie, wherePeriodo,whereAno);
			

			criteria.select(member).orderBy(cb.asc(member.get("bairroCrianca")));
			return em.createQuery(criteria).getSingleResult();

		} catch (NoResultException nre) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	
}

