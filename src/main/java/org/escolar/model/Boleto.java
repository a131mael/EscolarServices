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
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlRootElement;

import org.aaf.financeiro.util.OfficeUtil;


@SuppressWarnings("serial")
@Entity
@XmlRootElement
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "id"))
public class Boleto implements Serializable, Comparable<Boleto> {

	@Id
	@GeneratedValue
	private Long id;

	@Column

	private Date vencimento;
	@ManyToOne
	private ContratoAluno contrato;

	@Column
	private Date emissao;

	@Column
	private double valorNominal;

	@Column
	private long nossoNumero;

	@Column
	private Double valorPago;

	@Column
	private Date dataPagamento;

	@ManyToOne
	private Aluno pagador;

	@Transient
	private Boolean atrasado;

	@Column
	private Boolean alteracaoBoletoManual;

	@Column
	private Boolean baixaManual;
	
	@Column
	private Boolean dividaPerdoada;

	@Column
	private Boolean manterAposRemovido;
	
	@Column
	private Boolean nfsEnviada;

	@Column
	private Boolean conciliacaoPorExtrato;

	@Column
	private Boolean baixaGerada;

	@Column
	private Boolean enviadoParaBanco;

	@Column
	private Boolean cancelado;
	
	@Column
	private Boolean cnabCanceladoEnviado;

	@Column
	private Boolean cnabEnviado;
	
	@Column
    private Boolean protestado;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getVencimento() {
		return vencimento;
	}

	public void setVencimento(Date vencimento) {
		this.vencimento = vencimento;
	}

	public double getValorNominal() {
		return valorNominal;
	}

	public void setValorNominal(double valorNominal) {
		this.valorNominal = valorNominal;
	}

	public Date getEmissao() {
		return emissao;
	}

	public void setEmissao(Date emissao) {
		this.emissao = emissao;
	}

	public Aluno getPagador() {
		return pagador;
	}

	public void setPagador(Aluno pagador) {
		this.pagador = pagador;
	}

	public long getNossoNumero() {
		return nossoNumero;
	}

	public void setNossoNumero(long nossoNumero) {
		this.nossoNumero = nossoNumero;
	}

	public Double getValorPago() {
		return valorPago;
	}

	public void setValorPago(Double valorPago) {
		this.valorPago = valorPago;
	}

	public Date getDataPagamento() {
		return dataPagamento;
	}

	public void setDataPagamento(Date dataPagamento) {
		this.dataPagamento = dataPagamento;
	}

	public Boolean getAtrasado() {
		return atrasado;
	}

	public void setAtrasado(Boolean atrasado) {
		this.atrasado = atrasado;
	}

	public Boolean getBaixaManual() {
		return baixaManual;
	}

	public void setBaixaManual(Boolean baixaManual) {
		this.baixaManual = baixaManual;
	}

	public Boolean getBaixaGerada() {
		return baixaGerada;
	}

	public void setBaixaGerada(Boolean baixaGerada) {
		this.baixaGerada = baixaGerada;
	}

	public Boolean getAlteracaoBoletoManual() {
		return alteracaoBoletoManual;
	}

	public void setAlteracaoBoletoManual(Boolean alteracaoBoletoManual) {
		this.alteracaoBoletoManual = alteracaoBoletoManual;
	}

	public Boolean getConciliacaoPorExtrato() {
		return conciliacaoPorExtrato;
	}

	public void setConciliacaoPorExtrato(Boolean conciliacaoPorExtrato) {
		this.conciliacaoPorExtrato = conciliacaoPorExtrato;
	}

	public Boolean getCancelado() {
		return cancelado;
	}

	public void setCancelado(Boolean cancelado) {
		this.cancelado = cancelado;
	}

	public Boolean getManterAposRemovido() {
		return manterAposRemovido;
	}

	public void setManterAposRemovido(Boolean manterAposRemovido) {
		this.manterAposRemovido = manterAposRemovido;
	}

	public ContratoAluno getContrato() {
		return contrato;
	}

	public void setContrato(ContratoAluno contrato) {
		this.contrato = contrato;
	}

	@Override
	public int compareTo(Boleto o) {
		if (this.getId() > o.getId()) {
			return -1;
		} else if (this.getId() < o.getId()) {
			return 1;
		}

		return 0;
	}

	public Boolean getEnviadoParaBanco() {
		return enviadoParaBanco;
	}

	public void setEnviadoParaBanco(Boolean enviadoParaBanco) {
		this.enviadoParaBanco = enviadoParaBanco;
	}

	public org.aaf.financeiro.model.Boleto getBoletoFinanceiro() {
		org.aaf.financeiro.model.Boleto boletoFinanceiro = new org.aaf.financeiro.model.Boleto();
		boletoFinanceiro.setEmissao(getEmissao());
		boletoFinanceiro.setId(getId());
		boletoFinanceiro.setValorNominal(getValorNominal());
		boletoFinanceiro.setVencimento(getVencimento());
		boletoFinanceiro.setNossoNumero(String.valueOf(getNossoNumero()));
		boletoFinanceiro.setDataPagamento(OfficeUtil.retornaDataSomenteNumeros(getDataPagamento()));
		boletoFinanceiro.setValorPago(getValorPago());
		return boletoFinanceiro;
	}

	public Boolean getCnabEnviado() {
		return cnabEnviado;
	}

	public void setCnabEnviado(Boolean cnabEnviado) {
		this.cnabEnviado = cnabEnviado;
	}

	public Boolean getCnabCanceladoEnviado() {
		return cnabCanceladoEnviado;
	}

	public void setCnabCanceladoEnviado(Boolean cnabCanceladoEnviado) {
		this.cnabCanceladoEnviado = cnabCanceladoEnviado;
	}

	public Boolean getProtestado() {
		return protestado;
	}

	public void setProtestado(Boolean protestado) {
		this.protestado = protestado;
	}

	public Boolean getNfsEnviada() {
		return nfsEnviada;
	}

	public void setNfsEnviada(Boolean nfsEnviada) {
		this.nfsEnviada = nfsEnviada;
	}

	public Boolean getDividaPerdoada() {
		return dividaPerdoada;
	}

	public void setDividaPerdoada(Boolean dividaPerdoada) {
		this.dividaPerdoada = dividaPerdoada;
	}

}
