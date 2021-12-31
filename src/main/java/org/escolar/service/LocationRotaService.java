package org.escolar.service;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
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

import org.aaf.escolar.LocationRotaDTO;
import org.escolar.model.LocalizacaoRota;
import org.escolar.util.Service;

@Stateless
public class LocationRotaService extends Service {

	@PersistenceContext(unitName = "EscolarDS")
	private EntityManager em;

	public LocalizacaoRota findById(Long loc) {
		return em.find(LocalizacaoRota.class, loc);
	}

	public List<LocalizacaoRota> findAll() {
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<LocalizacaoRota> criteria = cb.createQuery(LocalizacaoRota.class);
			Root<LocalizacaoRota> member = criteria.from(LocalizacaoRota.class);
			criteria.select(member).orderBy(cb.desc(member.get("id")));
			return em.createQuery(criteria).getResultList();

		} catch (NoResultException nre) {
			return new ArrayList<>();
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}
	
	public LocalizacaoRota findByAndroidID(String androidUI) {
		LocalizacaoRota loc = null;
		try{
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT m from  LocalizacaoRota m ");
			sql.append("where m.androidID = '");
			sql.append(androidUI);
			sql.append("'");
			
			Query query = em.createQuery(sql.toString());
			 
			try{
				loc = (LocalizacaoRota) query.getSingleResult();
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

	public LocationRotaDTO saveNewLocation(LocationRotaDTO loc) {
		TimeZone.setDefault(TimeZone.getTimeZone("America/Sao_Paulo"));
		LocalizacaoRota m = null;
		if(m == null){
			m = new LocalizacaoRota();
		}
		m.setLatitude(loc.getLatitude());
		m.setLongitude(loc.getLongitude());
		m.setNome(loc.getNome());
		m.setAndroidID(loc.getAndroidID());
		m.setNomeMapa(loc.getNomeMapa());
		
		LocalDateTime now = LocalDateTime.now();
		m.setHora(new Date());
		m.setPeriodo(getPeriodo(now));
		if(m.getId() != null){
			em.persist(m);
			
		}else{
			em.persist(m);
		}
		return m.getDTO();
		
	}
	
	private int getPeriodo(LocalDateTime now) {
		if(now.getHour() < 10){
			return 0;
		}else if (now.getHour() >= 10 && now.getHour() <= 15){
			return 1;
		}
		return 2;
	}
	
	private int getPeriodo(Date now) {
		Calendar rightNow = Calendar.getInstance();
		rightNow.setTime(now);
		int hora = rightNow.get(Calendar.HOUR_OF_DAY);
		if(hora < 10){
			return 0;
		}else if (hora >= 10 && hora <= 15){
			return 1;
		}
		return 2;
	}

	public List<LocationRotaDTO> findAllDTO() {
		List<LocationRotaDTO> recadosDto = new ArrayList<>();
		for (LocalizacaoRota r : findAll()) {
			recadosDto.add(r.getDTO());
		}

		return recadosDto;
	}

	public List<LocationRotaDTO> findByName(String uiiD) {
		return findByName(uiiD,null);
    	
	}
	
	public List<LocationRotaDTO> findByName(String uiiD, Date dataUltimaAtualizacao) {
		TimeZone.setDefault(TimeZone.getTimeZone("America/Sao_Paulo"));
		SimpleDateFormat formatador = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
		Date inicio = new Date(); 
		inicio.setHours(0);
		inicio.setMinutes(0);
		inicio.setSeconds(0);
		
		Date fim = new Date();
		fim.setHours(23);
		fim.setMinutes(59);
		fim.setSeconds(59);
		
		
		List<LocationRotaDTO> dtos = new ArrayList<>();
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT m from  LocalizacaoRota m ");
		sql.append("where m.nome = '");
		sql.append(uiiD);
		sql.append("'");
		if(dataUltimaAtualizacao != null){
			sql.append(" and m.hora > '");
			sql.append(formatador.format(dataUltimaAtualizacao));
			sql.append("'");
		}
		
		sql.append(" and m.periodo = ");
		sql.append(getPeriodo(new Date()));
		
		
		sql.append(" and m.hora > '");
		sql.append(formatador.format(inicio));
		sql.append("'");
		sql.append(" and m.hora <  '");
		sql.append(formatador.format(fim));
		sql.append(" '");
		
		System.out.println(sql.toString());
		System.out.println("PIKAAAAAAAAAAAAAAa" + dataUltimaAtualizacao);
		Query query = em.createQuery(sql.toString());
		 
		try{
			List<LocalizacaoRota>  locs =  (List<LocalizacaoRota>)query.getResultList(); 
			if(locs != null){
				for (LocalizacaoRota r : locs) {
					dtos.add(r.getDTO());
				}
			}
			return dtos;
		}catch (NoResultException ne) {
			System.out.println("Exceção zuou tudo, nao conseguiu achar a localização pelo nome: " + uiiD);
		}catch (Exception e) {
			System.out.println("Exceção zuou tudo, nao conseguiu achar a localização pelo nome: " + uiiD);
		}
		return dtos;
	}
	
	public LocalizacaoRota findRotabyName(String uiiD) {
		LocalizacaoRota dto = null;
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT m from  LocalizacaoRota m ");
		sql.append("where m.nome = '");
		sql.append(uiiD);
		sql.append("'");
		
		
		Query query = em.createQuery(sql.toString());
		 
		try{
			List<LocalizacaoRota>  locs =  (List<LocalizacaoRota>)query.getResultList(); 
			if(locs != null && locs.size()>0){
				dto = locs.get(0);	
			}
			return dto;
		}catch (NoResultException ne) {
			System.out.println("Exceção zuou tudo, nao conseguiu achar a localização pelo nome: " + uiiD);
		}catch (Exception e) {
			System.out.println("Exceção zuou tudo, nao conseguiu achar a localização pelo nome: " + uiiD);
		}
		return dto;
    	
	}

}
