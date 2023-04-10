
package org.escolar.service;

import java.util.ArrayList;
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

import org.escolar.model.Evento;
import org.escolar.util.Service;


@Stateless
public class EventoService extends Service {


	@PersistenceContext(unitName = "EscolarDS")
	private EntityManager em;

	public Evento findById(EntityManager em, Long id) {
		return em.find(Evento.class, id);
	}

	public Evento findById(Long id) {
		return em.find(Evento.class, id);
	}
	
	public Evento findByCodigo(Long id) {
		return em.find(Evento.class, id);
	}
	
	public String remover(Long idEvento){
		em.remove(findById(idEvento));
		em.flush();
		return "index";
	}

	public List<Evento> findAll() {
		try{
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Evento> criteria = cb.createQuery(Evento.class);
			Root<Evento> member = criteria.from(Evento.class);
			// Swap criteria statements if you would like to try out type-safe
			// criteria queries, a new
			// feature in JPA 2.0
			// criteria.select(member).orderBy(cb.asc(member.get(Member_.name)));
			criteria.select(member).orderBy(cb.asc(member.get("id")));
			return em.createQuery(criteria).getResultList();
	
		}catch(NoResultException nre){
			return new ArrayList<>();
		}catch(Exception e){
			e.printStackTrace();
			return new ArrayList<>();
		}
	}

	public Evento save(Evento evento) {
		Evento user = null;
		try {

			if (evento.getId() != null && evento.getId() != 0L) {
				user = findById(evento.getId());
			} else {
				user = new Evento();
			}
			
			user.setDataFim(evento.getDataFim());
			user.setDataInicio(evento.getDataInicio());
			user.setDescricao(evento.getDescricao());
			user.setNome(evento.getNome());
			user.setCodigo(evento.getCodigo());
			
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

		em.flush();
		return user;
	}

	public Evento findByCodigo(String codigo) {
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Evento> criteria = cb.createQuery(Evento.class);
			Root<Evento> member = criteria.from(Evento.class);

			Predicate whereSerie = null;

			whereSerie = cb.equal(member.get("codigo"), codigo);
			criteria.select(member).where(whereSerie);

			criteria.select(member);
			return em.createQuery(criteria).getSingleResult();

		} catch (NoResultException nre) {
			return null;
		} catch (Exception e) {
			return null;
		}
	}
}

