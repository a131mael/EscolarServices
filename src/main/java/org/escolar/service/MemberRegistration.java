/*
' * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.escolar.service;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.aaf.escolar.MemberDTO;
import org.escolar.enums.TipoMembro;
import org.escolar.model.Member;

// The @Stateless annotation eliminates the need for manual transaction demarcation
@Stateless
public class MemberRegistration {

	@PersistenceContext(unitName = "EscolarDS")
    private EntityManager em;

	 public void register(MemberDTO member) throws Exception {
	        Member m = new Member();
	        if(member.getId() != null){
	        	m = findById(member.getId());
	        }
	        m.setEmail(member.getEmail());
	        m.setLogin(member.getLogin());
	        m.setName(member.getName());
	        m.setSenha(member.getSenha());
	        m.setAlertaProximidade(member.isAlertaProximidade());
	        m.setDistanciaAlerta(member.getDistanciaAlerta());
	        m.setEnviarBoletosEmail(member.isEnviarBoletosEmail());
	        m.setQuantidadeAcessos(member.getQuantidadeAcessos());
	        
	        em.persist(m);
	        /*memberEventSrc.fire(member);*/
	        em.flush();
	    }

    public void register(Member member) throws Exception {
        em.persist(member);
        em.flush();
    }
    
    public Member login(Member member) throws Exception {
    	Member m = findMember(member);
    	
    	return m;
    }
    
    public org.aaf.escolar.MemberDTO findByIdDTO(Long id) {
		Member m = em.find(Member.class, id); 
 		return m.getDTO();
	}
    
    public MemberDTO findByLoginSenha(String login, String senha) {
		try {
			System.out.println("ENTRANDO NO REGISTRATION USUARIO E SENHA");
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Member> criteria = cb.createQuery(Member.class);
			Root<Member> member = criteria.from(Member.class);

			Predicate whereLogin = null;
			Predicate whereSenha = null;

			whereLogin = cb.equal(member.get("login"), login);
			whereSenha = cb.equal(member.get("senha"), senha);
			criteria.select(member).where(whereLogin,whereSenha);

			criteria.select(member);
			return em.createQuery(criteria).getSingleResult().getDTO();

		} catch (NoResultException nre) {
			return null;
		} catch (Exception e) {
			return null;
		}
	}
    
    public MemberDTO findMemberDTO(String login, String senha){
    	Member member = null;
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT m from Member m ");
		sql.append("where (m.login = '");
		sql.append(login);
		sql.append("' or m.email = '");
		sql.append(login);
		sql.append("' ) and m.senha = '");
		sql.append(senha);
		sql.append("'");
		
		Query query = em.createQuery(sql.toString());
		 
		System.out.println("--------------" + query);
		try{
			member = (Member) query.getSingleResult();
		}catch (NoResultException ne) {
			System.out.println(ne);
		}catch (NonUniqueResultException nure) {
			System.out.println(nure);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("--------------" + member);
		if(member == null){
			return null;
		}
		System.out.println("--------------" + member.getDTO());
    	return member.getDTO();
    }
    
    public Member findById(EntityManager em, Long id) {
		return em.find(Member.class, id);
	}

	public Member findById(Long id) {
		return em.find(Member.class, id);
	}
    
    private Member findMember(Member m){
    	Member member = null;
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT m from  Member m ");
		sql.append("where (m.login = '");
		sql.append(m.getLogin());
		sql.append("' or m.email = '");
		sql.append(m.getEmail());
		sql.append("' ) and m.senha = '");
		sql.append(m.getSenha());
		sql.append("'");
		
		Query query = em.createQuery(sql.toString());
		 
		try{
			member = (Member) query.getSingleResult();
		}catch (NoResultException ne) {
			
		}catch (NonUniqueResultException nure) {
			System.out.println(nure);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
    	
    	return member;
    }
    
    private Member isInterMember(Member member) {
		boolean inter = false;
		if(member.getLogin().equalsIgnoreCase("admin")){
			if(member.getSenha().equalsIgnoreCase("12345")){
				member.setTipoMembro(TipoMembro.SECRETARIA);
				inter = true;
			}
		}
		if(member.getLogin().equalsIgnoreCase("professor")){
			if(member.getSenha().equalsIgnoreCase("12345")){
				member.setTipoMembro(TipoMembro.MOTORISTA);
				inter = true;
			}
		}
		return member;
	}
    
}
