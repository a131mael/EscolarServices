/*
 * JBoss, Home of Professional Open Source
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
package org.escolar.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

import org.escolar.enums.TipoMembro;

@Entity
@XmlRootElement
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "email") )
public class Member implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(generator = "GENERATE_member", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "GENERATE_member", sequenceName = "Member_pk_seq", allocationSize = 1)
	private Long id;

	@NotNull
	@Size(min = 1, max = 60)
	@Pattern(regexp = "[^0-9]*", message = "Must not contain numbers")
	private String name;

	@Column
	private String email;

	@Column
	private TipoMembro tipoMembro;

	@Column
	private String login;

	@Column
	private String senha;

	@Column
	private String telefone;

	@Column
	private String idCrianca1;
	
	@Column
	private String idCrianca2;
	@Column
	
	private String idCrianca3;
	@Column
	
	private String idCrianca4;
	
	@Column
	private String idCrianca5;
	
	@Column
	private String idContratoAtivo;
	
	@OneToOne(mappedBy = "member")
	private Funcionario professor;
	
	@OneToOne(mappedBy = "member")
	private Aluno aluno;

	@Column(name = "phone_number")
	private String phoneNumber;
	
	@Column
	private boolean alertaProximidade;
	
	@Column
	private boolean enviarBoletosEmail;
	
	@Column
	private int distanciaAlerta;
	
	@Column
	private int quantidadeAcessos;

	
	public org.aaf.escolar.MemberDTO getDTO(){
		org.aaf.escolar.MemberDTO dto = new org.aaf.escolar.MemberDTO();
		dto.setTipoMembro(tipoMembro.ordinal());
		dto.setEmail(email);
		dto.setId(id);
		dto.setLogin(login);
		dto.setName(name);
		dto.setSenha(senha);
		//dto.setTokenFCM(tokenFCM);
		dto.setTelefone(phoneNumber);
		dto.setIdCrianca1(idCrianca1);
		dto.setIdCrianca2(idCrianca2);
		dto.setIdCrianca3(idCrianca3);
		dto.setIdCrianca4(idCrianca4);
		dto.setIdCrianca5(idCrianca5);
		dto.setIdContratoAtivo(idContratoAtivo);
		dto.setAlertaProximidade(alertaProximidade);
		dto.setDistanciaAlerta(distanciaAlerta);
		dto.setQuantidadeAcessos(quantidadeAcessos);
		dto.setEnviarBoletosEmail(enviarBoletosEmail);
		return dto;
	}

	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public TipoMembro getTipoMembro() {
		return tipoMembro;
	}

	public void setTipoMembro(TipoMembro tipoMembro) {
		this.tipoMembro = tipoMembro;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public Funcionario getProfessor() {
		return professor;
	}

	public void setProfessor(Funcionario professor) {
		this.professor = professor;
	}

	public String getTelefone() {
		return telefone;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	public String getIdCrianca2() {
		return idCrianca2;
	}

	public void setIdCrianca2(String idCrianca2) {
		this.idCrianca2 = idCrianca2;
	}

	public String getIdCrianca3() {
		return idCrianca3;
	}

	public void setIdCrianca3(String idCrianca3) {
		this.idCrianca3 = idCrianca3;
	}

	public String getIdCrianca4() {
		return idCrianca4;
	}

	public void setIdCrianca4(String idCrianca4) {
		this.idCrianca4 = idCrianca4;
	}

	public String getIdCrianca5() {
		return idCrianca5;
	}

	public void setIdCrianca5(String idCrianca5) {
		this.idCrianca5 = idCrianca5;
	}


	public String getIdContratoAtivo() {
		return idContratoAtivo;
	}


	public void setIdContratoAtivo(String idContratoAtivo) {
		this.idContratoAtivo = idContratoAtivo;
	}


	public boolean isAlertaProximidade() {
		return alertaProximidade;
	}


	public void setAlertaProximidade(boolean alertaProximidade) {
		this.alertaProximidade = alertaProximidade;
	}


	public int getDistanciaAlerta() {
		return distanciaAlerta;
	}


	public void setDistanciaAlerta(int distanciaAlerta) {
		this.distanciaAlerta = distanciaAlerta;
	}


	public int getQuantidadeAcessos() {
		return quantidadeAcessos;
	}


	public void setQuantidadeAcessos(int quantidadeAcessos) {
		this.quantidadeAcessos = quantidadeAcessos;
	}


	public boolean isEnviarBoletosEmail() {
		return enviarBoletosEmail;
	}


	public void setEnviarBoletosEmail(boolean enviarBoletosEmail) {
		this.enviarBoletosEmail = enviarBoletosEmail;
	}
}
