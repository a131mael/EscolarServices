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
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlRootElement;

@SuppressWarnings("serial")
@Entity
@XmlRootElement
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "id"))
public class PromessaPagamentoBoleto implements Serializable {

	@Id
	@GeneratedValue
	private Long id;

	@Column
	private Date dataCadastroPromessa;
	
	@Column
	private Date dataPromessaPagamento;

	@Column(length = 2048)
	private String descricao;

	@ManyToOne
	private Boleto boleto;
	
	@ManyToOne
	private ContratoAluno contrato;
	
	@ManyToOne
	private Aluno aluno;
	
	@Column
	private boolean ativo;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}


	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Date getDataPromessaPagamento() {
		return dataPromessaPagamento;
	}

	public void setDataPromessaPagamento(Date dataPromessaPagamento) {
		this.dataPromessaPagamento = dataPromessaPagamento;
	}

	public Boleto getBoleto() {
		return boleto;
	}

	public void setBoleto(Boleto boleto) {
		this.boleto = boleto;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public ContratoAluno getContrato() {
		return contrato;
	}

	public void setContrato(ContratoAluno contrato) {
		this.contrato = contrato;
	}

	public Date getDataCadastroPromessa() {
		return dataCadastroPromessa;
	}

	public void setDataCadastroPromessa(Date dataCadastroPromessa) {
		this.dataCadastroPromessa = dataCadastroPromessa;
	}

	public Aluno getAluno() {
		return aluno;
	}

	public void setAluno(Aluno aluno) {
		this.aluno = aluno;
	}

}
