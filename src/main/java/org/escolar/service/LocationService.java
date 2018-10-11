package org.escolar.service;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.aaf.escolar.LocationDTO;
import org.escolar.model.Localizacao;
import org.escolar.model.Member;
import org.escolar.util.Service;

@Stateless
public class LocationService extends Service {

	@PersistenceContext(unitName = "EscolarDS")
	private EntityManager em;

	public Localizacao findById(Long loc) {
		return em.find(Localizacao.class, loc);
	}

	public List<Localizacao> findAll() {
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Localizacao> criteria = cb.createQuery(Localizacao.class);
			Root<Localizacao> member = criteria.from(Localizacao.class);
			criteria.select(member).orderBy(cb.desc(member.get("id")));
			return em.createQuery(criteria).getResultList();

		} catch (NoResultException nre) {
			return new ArrayList<>();
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}
	
	public Localizacao findByAndroidID(String androidUI) {
		Localizacao loc = null;
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT m from  Localizacao m ");
		sql.append("where m.androidID = '");
		sql.append(androidUI);
		sql.append("'");
		
		Query query = em.createQuery(sql.toString());
		 
		try{
			loc = (Localizacao) query.getSingleResult();
		}catch (NoResultException ne) {
			System.out.println("nao encontrou localização pelo UI");
			
		}catch (NonUniqueResultException nure) {
			System.out.println("nao encontrou localização UNICA");
		}
		catch (Exception e) {
			System.out.println("deu pau pra achar a localização pelo UI");
		}
    	
    	return loc;
	}

	public LocationDTO saveCar(LocationDTO loc) {
		Localizacao m = null;
		
		if (loc != null && loc.getAndroidID() != null  ) {
			m = findByAndroidID(loc.getAndroidID());
		}
		
		if (m == null  && loc.getNome() != null) {
			m = findByName(loc.getNome());
		}
		
		if(m == null){
			m = new Localizacao();
		}else{
			m = findById(m.getId());
		}
		m.setLatitude(loc.getLatitude());
		m.setLongitude(loc.getLongitude());
		m.setNome(loc.getNome());
		m.setAndroidID(loc.getAndroidID());
		if(m.getId() != null){
			System.out.println("persistiu com latitude " + m.getLatitude());
			System.out.println("persistiu com longitude " + m.getLongitude());
			
			System.out.println("persistiu com latitude LOC" + loc.getLatitude());
			System.out.println("persistiu com longitude LOC " + loc.getLongitude());
			em.persist(m);
			
		}else{
			System.out.println("persistiu ELSE com latitude " + m.getLatitude());
			System.out.println("persistiu ELSE com longitude " + m.getLongitude());
			System.out.println("persistiu com latitude LOC" + loc.getLatitude());
			System.out.println("persistiu com longitude LOC " + loc.getLongitude());
			em.persist(m);
		}
		return m.getDTO();
		
	}

	public List<LocationDTO> findAllDTO() {
		List<LocationDTO> recadosDto = new ArrayList<>();
		for (Localizacao r : findAll()) {
			recadosDto.add(r.getDTO());
		}

		return recadosDto;
	}

	public Localizacao findByName(String uiiD) {
		Localizacao loc = null;
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT m from  Localizacao m ");
		sql.append("where m.nome = '");
		sql.append(uiiD);
		sql.append("'");
		
		Query query = em.createQuery(sql.toString());
		 
		try{
			@SuppressWarnings("unchecked")
			List<Localizacao> locs =  (List<Localizacao>)query.getResultList(); 
			if(locs != null && locs.size() >0){
				loc = locs.get(0);
			}
		}catch (NoResultException ne) {
			System.out.println("Exceção zuou tudo, nao conseguiu achar a localização pelo nome: " + uiiD);
		}catch (Exception e) {
			System.out.println("Exceção zuou tudo, nao conseguiu achar a localização pelo nome: " + uiiD);
		}
    	
    	return loc;
	}

}
