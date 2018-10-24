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
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

import org.escolar.enums.BimestreEnum;
import org.escolar.enums.DisciplinaEnum;
import org.escolar.enums.Serie;

@SuppressWarnings("serial")
@Entity
@XmlRootElement
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "id"))
public class ContratoAluno implements Serializable {

	@Id
	@GeneratedValue(generator = "GENERATE_contratoAluno", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "GENERATE_contratoAluno", sequenceName = "contratoAluno_pk_seq", allocationSize = 1)
	private Long id;

	@ManyToOne
	private Aluno aluno;

	@OneToMany(fetch = FetchType.LAZY)
	private List<Boleto> boletos;

	@Column
	private short ano;

	@Column
	private String nomeResponsavel;

	@Column
	private String nomeMaeResponsavel;

	@Column
	private String nomePaiResponsavel;

	@Column
	private String cnabEnviado;
	
	@Column
	private String cancelado;

	@Column
	private String numero;

	@Column
	private String bairro;

	@Column
	private Boolean vencimentoUltimoDia;

	@Column
	private String cep;

	@Column
	private String cidade;

	@Column
	private Double anuidade;

	@Column
	private Integer numeroParcelas;

	@Column
	private String cpfResponsavel;

	@Column
	private String rgResponsavel;

	@Column
	private double valorMensal;
	
	@Column
	private Date dataCancelamento;
	
	@Column
	private Date dataCriacaoContrato;
	
	@Column
    private Boolean enviadoParaCobrancaCDL;
	
	@Column
    private Boolean enviadoSPC;
	
	@Column
    private Boolean contratoTerminado;

	public Boolean getContratoTerminado() {
		return contratoTerminado;
	}

	public void setContratoTerminado(Boolean contratoTerminado) {
		this.contratoTerminado = contratoTerminado;
	}

	public Boolean getEnviadoSPC() {
		return enviadoSPC;
	}

	public void setEnviadoSPC(Boolean enviadoSPC) {
		this.enviadoSPC = enviadoSPC;
	}

	public Boolean getEnviadoParaCobrancaCDL() {
		return enviadoParaCobrancaCDL;
	}

	public void setEnviadoParaCobrancaCDL(Boolean enviadoParaCobrancaCDL) {
		this.enviadoParaCobrancaCDL = enviadoParaCobrancaCDL;
	}

	public Date getDataCancelamento() {
		return dataCancelamento;
	}

	public void setDataCancelamento(Date dataCancelamento) {
		this.dataCancelamento = dataCancelamento;
	}

	public double getValorMensal() {
		return valorMensal;
	}

	public void setValorMensal(double valorMensal) {
		this.valorMensal = valorMensal;
	}

	public String getRgResponsavel() {
		return rgResponsavel;
	}

	public void setRgResponsavel(String rgResponsavel) {
		this.rgResponsavel = rgResponsavel;
	}

	public String getCpfResponsavel() {
		return cpfResponsavel;
	}

	public void setCpfResponsavel(String cpfResponsavel) {
		this.cpfResponsavel = cpfResponsavel;
	}

	public Integer getNumeroParcelas() {
		return numeroParcelas;
	}

	public void setNumeroParcelas(Integer numeroParcelas) {
		this.numeroParcelas = numeroParcelas;
	}

	public Double getAnuidade() {
		return anuidade;
	}

	public void setAnuidade(Double anuidade) {
		this.anuidade = anuidade;
	}

	public String getCidade() {
		return cidade;
	}

	public void setCidade(String cidade) {
		this.cidade = cidade;
	}

	public String getCep() {
		return cep;
	}

	public void setCep(String cep) {
		this.cep = cep;
	}

	public Boolean getVencimentoUltimoDia() {
		return vencimentoUltimoDia;
	}

	public void setVencimentoUltimoDia(Boolean vencimentoUltimoDia) {
		this.vencimentoUltimoDia = vencimentoUltimoDia;
	}

	public String getBairro() {
		return bairro;
	}

	public void setBairro(String bairro) {
		this.bairro = bairro;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getCancelado() {
		return cancelado;
	}

	public void setCancelado(String cancelado) {
		this.cancelado = cancelado;
	}

	public String getCnabEnviado() {
		return cnabEnviado;
	}

	public void setCnabEnviado(String cnabEnviado) {
		this.cnabEnviado = cnabEnviado;
	}

	public String getNomePaiResponsavel() {
		return nomePaiResponsavel;
	}

	public void setNomePaiResponsavel(String nomePaiResponsavel) {
		this.nomePaiResponsavel = nomePaiResponsavel;
	}

	public String getNomeMaeResponsavel() {
		return nomeMaeResponsavel;
	}

	public void setNomeMaeResponsavel(String nomeMaeResponsavel) {
		this.nomeMaeResponsavel = nomeMaeResponsavel;
	}

	public String getNomeResponsavel() {
		return nomeResponsavel;
	}

	public void setNomeResponsavel(String nomeResponsavel) {
		this.nomeResponsavel = nomeResponsavel;
	}

	public short getAno() {
		return ano;
	}

	public void setAno(short ano) {
		this.ano = ano;
	}

	public List<Boleto> getBoletos() {
		return boletos;
	}

	public void setBoletos(List<Boleto> boletos) {
		this.boletos = boletos;
	}

	public Aluno getAluno() {
		return aluno;
	}

	public void setAluno(Aluno aluno) {
		this.aluno = aluno;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getDataCriacaoContrato() {
		return dataCriacaoContrato;
	}

	public void setDataCriacaoContrato(Date dataCriacaoContrato) {
		this.dataCriacaoContrato = dataCriacaoContrato;
	}

}
