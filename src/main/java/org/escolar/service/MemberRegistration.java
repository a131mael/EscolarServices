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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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
import org.escolar.enums.StatusBoletoEnum;
import org.escolar.enums.TipoMembro;
import org.escolar.model.Aluno;
import org.escolar.model.ContratoAluno;
import org.escolar.model.Member;
import org.escolar.util.Verificador;

@Stateless
public class MemberRegistration {

	@PersistenceContext(unitName = "EscolarDS")
    private EntityManager em;

	public void register(MemberDTO member) throws Exception {
	    Member m = new Member();
	    if (member.getId() != null) {
	        m = findById(member.getId());
	    }
	    
	    if (member.getEmail() != null) {
	        m.setEmail(member.getEmail());
	    }
	    if (member.getLogin() != null) {
	        m.setLogin(member.getLogin());
	    }
	    if (member.getName() != null) {
	        m.setName(member.getName());
	    }
	    if (member.getSenha() != null) {
	        m.setSenha(member.getSenha());
	    }
	        m.setAlertaProximidade(member.isAlertaProximidade());
	    if (member.getDistanciaAlerta() != 0) {
	        m.setDistanciaAlerta(member.getDistanciaAlerta());
	    }
	      m.setEnviarBoletosEmail(member.isEnviarBoletosEmail());
	    if (member.getQuantidadeAcessos() != 0) {
	        m.setQuantidadeAcessos(member.getQuantidadeAcessos());
	    }
	    if (member.getTokenFCM() != null) {
	        m.setTokenFCM(member.getTokenFCM());
	    }
	    if (member.getPhoneNumber() != null) {
	        m.setPhoneNumber(member.getPhoneNumber());
	    }
	    if (member.getLatitude() != 0) {
	        m.setLatitude(member.getLatitude());
	    }
	    if (member.getLongitude() != 0) {
	        m.setLongitude(member.getLongitude());
	    }
	    
	    em.persist(m);
	    /* memberEventSrc.fire(member); */
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
		if(m!= null) {
			return m.getDTO();
		}else {
			return null;
		}
	}
    
    public ContratoAluno findContratoById(Long id) {
  		ContratoAluno m = em.find(ContratoAluno.class, id); 
  		return m;
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
    
    public List<MemberDTO> findMemberValidoAPPDTO(String periodo){
    	
    	String usuariosValidosPeriodoSQL = construirSQLUsuariosValidos(periodo);
    	
    	Query query = em.createQuery(usuariosValidosPeriodoSQL.toString());
		 
    	List<Member> membros = query.getResultList();
		System.out.println("--------------" + query);
		List<MemberDTO> membrosDTO = new ArrayList<>();
		try{
			
			
			for (Member member : membros) {
				  MemberDTO memberDTO = member.getDTO();
				  membrosDTO.add(memberDTO);
			}
    	
		}catch (Exception e) {
			e.printStackTrace();
		}
		return membrosDTO; 
    }
    
    private String construirSQLUsuariosValidos(String periodo) {
        // Obter o ano atual dinamicamente
        int anoAtual = Calendar.getInstance().get(Calendar.YEAR);

        // Construir a query
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT DISTINCT m FROM Member m ");
        sql.append("LEFT JOIN m.aluno a "); // Alterado para LEFT JOIN para permitir alunos nulos
        sql.append("LEFT JOIN a.contratos c "); // Mantém o LEFT JOIN para lidar com alunos nulos
        sql.append("WHERE (");
        sql.append("  (a IS NOT NULL AND "); // Condição para membros com aluno
        sql.append("   c.ano = ").append(anoAtual).append(" "); // Ano atual
        sql.append("   AND tokenFCM IS NOT NULL ");
        sql.append("   AND latitude IS NOT NULL ");
        sql.append("   AND (c.cancelado IS NULL OR c.cancelado = false) "); // Contrato não cancelado ou nulo
        sql.append("   AND (");

        // Condição para o período
        if ("manha".equalsIgnoreCase(periodo)) {
            sql.append("a.periodo = 1 OR a.periodo = 0"); // 1 = MANHA, 0 = INTEGRAL
        } else if ("meio_dia".equalsIgnoreCase(periodo)) {
            sql.append("a.periodo = 1 OR a.periodo = 2"); // 1 = MANHA, 2 = TARDE
        } else if ("tarde".equalsIgnoreCase(periodo)) {
            sql.append("a.periodo = 0 OR a.periodo = 2"); // 0 = INTEGRAL, 2 = TARDE
        } else {
            throw new IllegalArgumentException("Período inválido: " + periodo);
        }

        sql.append(")) "); // Fechando condição para membros com aluno

        // Condição para membros sem aluno e tipoMembro diferente de 3
        sql.append("OR (a IS NULL AND m.tipoMembro <> 3) ");

        // Condições globais
        sql.append("AND tokenFCM IS NOT NULL "); // Token deve ser preenchido
        sql.append("AND latitude IS NOT NULL "); // Localização deve ser preenchida

        sql.append(")"); // Fechando a condição principal
        return sql.toString();
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
