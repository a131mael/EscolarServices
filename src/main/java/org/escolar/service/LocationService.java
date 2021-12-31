package org.escolar.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

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
		try{
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
		}catch(Exception e){
			
		}
    	
    	return loc;
	}

	public LocationDTO saveCar(LocationDTO loc) {
		TimeZone.setDefault(TimeZone.getTimeZone("America/Sao_Paulo"));
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
		m.setNomeMapa(loc.getNomeMapa());
		
		SimpleDateFormat formatador = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		String dataFormatada = formatador.format(new Date());
		
		try {
			m.setDataUltimaAtualizacao(formatador.parse(dataFormatada));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(m.getId() != null){
			em.persist(m);
			
		}else{
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
