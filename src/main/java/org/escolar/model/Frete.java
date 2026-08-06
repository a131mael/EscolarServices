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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlRootElement;

import org.escolar.enums.FormaPagamentoEnum;

@SuppressWarnings("serial")
@Entity
@XmlRootElement
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "id"))
public class Frete implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Contratante contratante;

    @ManyToOne
    private Funcionario motorista;

    @OneToMany
    private List<CarroFrete> carroFrete;
    
    @Column
    private Date horarioLocalOrigem;
    
    @Column
    private Date horarioParaRetorno;
    
    @Column
    private String descricao;

    private Double valor;
    
    private Double valorPago;
    
    private Double valorPagoMotorista;
    
    private String localOrigem;
    
    private String localDestino;
    
    private FormaPagamentoEnum formaPagamento;

    @Column(unique = true)
    private String tokenPublico;

    @OneToMany(mappedBy = "frete", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PassageiroViagem> passageiros = new ArrayList<>();

    /** Dados para emissao do CT-e OS (modelo 67) */
    /** 1 = Fretamento Eventual, 2 = Fretamento Continuo */
    @Column
    private String tpFretamento;

    @Column
    private String codMunOrigem;

    @Column
    private String codMunDestino;

    @Column
    private Integer numeroCte;

    @Column
    private Integer serieCte;

    @Column
    private String chaveCte;

    @Column
    private String statusCte;

    @Column(length = 65535)
    private String xmlCteOS;

    /** Numero do protocolo de autorizacao retornado pela SEFAZ */
    @Column
    private String protocoloCte;

    /** Motivo retornado pela SEFAZ (autorizacao ou rejeicao) */
    @Column
    private String motivoCte;

    /** Data/hora de recebimento (dhRecbto) retornada pela SEFAZ na autorizacao */
    @Column
    private String dataAutorizacaoCte;

    /** Numero do protocolo de homologacao do cancelamento retornado pela SEFAZ */
    @Column
    private String protocoloCancelamentoCte;

    /** Data/hora de registro (dhRegEvento) do cancelamento retornada pela SEFAZ */
    @Column
    private String dataCancelamentoCte;

    /** Justificativa informada pelo usuario para o cancelamento do CT-e */
    @Column(length = 500)
    private String justificativaCancelamentoCte;

    /** Dados para emissao da Licenca de Fretamento Eventual no scMOBI */
    @Column
    private Double quilometragem;

    /** null / "PROCESSANDO" / "CONCLUIDO" / "ERRO" */
    @Column
    private String statusLicencaScmobi;

    /** Numero do contrato retornado pelo scMOBI (ex: 388071) */
    @Column
    private Integer numeroLicencaScmobi;

    @Column(length = 2000)
    private String erroLicencaScmobi;

    /** Id do job assincrono no scmobi-automation-service */
    @Column
    private String jobIdScmobi;

    @Column
    private String dataGeracaoLicencaScmobi;

    @Lob
    @Column
    private byte[] licencaScmobiPdf;

    @Lob
    @Column
    private byte[] passageirosScmobiPdf;

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}


	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}

	public Contratante getContratante() {
		return contratante;
	}


	public void setContratante(Contratante contratante) {
		this.contratante = contratante;
	}

	public Funcionario getMotorista() {
		return motorista;
	}

	public void setMotorista(Funcionario motorista) {
		this.motorista = motorista;
	}


	public List<CarroFrete> getCarroFrete() {
		return carroFrete;
	}


	public void setCarroFrete(List<CarroFrete> carroFrete) {
		this.carroFrete = carroFrete;
	}


	public Date getHorarioLocalOrigem() {
		return horarioLocalOrigem;
	}


	public void setHorarioLocalOrigem(Date horarioLocalOrigem) {
		this.horarioLocalOrigem = horarioLocalOrigem;
	}


	public Date getHorarioParaRetorno() {
		return horarioParaRetorno;
	}


	public void setHorarioParaRetorno(Date horarioParaRetorno) {
		this.horarioParaRetorno = horarioParaRetorno;
	}


	public Double getValor() {
		return valor;
	}


	public void setValor(Double valor) {
		this.valor = valor;
	}


	public String getLocalOrigem() {
		return localOrigem;
	}


	public void setLocalOrigem(String localOrigem) {
		this.localOrigem = localOrigem;
	}


	public String getLocalDestino() {
		return localDestino;
	}


	public void setLocalDestino(String localDestino) {
		this.localDestino = localDestino;
	}

	public Double getValorPago() {
		return valorPago;
	}

	public void setValorPago(Double valorPago) {
		this.valorPago = valorPago;
	}

	public FormaPagamentoEnum getFormaPagamento() {
		return formaPagamento;
	}

	public void setFormaPagamento(FormaPagamentoEnum formaPagamento) {
		this.formaPagamento = formaPagamento;
	}

	public Double getValorPagoMotorista() {
		return valorPagoMotorista;
	}

	public void setValorPagoMotorista(Double valorPagoMotorista) {
		this.valorPagoMotorista = valorPagoMotorista;
	}

	public String getTokenPublico() { return tokenPublico; }
	public void setTokenPublico(String tokenPublico) { this.tokenPublico = tokenPublico; }

	public List<PassageiroViagem> getPassageiros() { return passageiros; }
	public void setPassageiros(List<PassageiroViagem> passageiros) { this.passageiros = passageiros; }

	public void gerarToken() {
		if (this.tokenPublico == null || this.tokenPublico.isEmpty()) {
			this.tokenPublico = UUID.randomUUID().toString();
		}
	}

	public String getTpFretamento() { return tpFretamento; }
	public void setTpFretamento(String tpFretamento) { this.tpFretamento = tpFretamento; }

	public String getCodMunOrigem() { return codMunOrigem; }
	public void setCodMunOrigem(String codMunOrigem) { this.codMunOrigem = codMunOrigem; }

	public String getCodMunDestino() { return codMunDestino; }
	public void setCodMunDestino(String codMunDestino) { this.codMunDestino = codMunDestino; }

	public Integer getNumeroCte() { return numeroCte; }
	public void setNumeroCte(Integer numeroCte) { this.numeroCte = numeroCte; }

	public Integer getSerieCte() { return serieCte; }
	public void setSerieCte(Integer serieCte) { this.serieCte = serieCte; }

	public String getChaveCte() { return chaveCte; }
	public void setChaveCte(String chaveCte) { this.chaveCte = chaveCte; }

	public String getStatusCte() { return statusCte; }
	public void setStatusCte(String statusCte) { this.statusCte = statusCte; }

	public String getXmlCteOS() { return xmlCteOS; }
	public void setXmlCteOS(String xmlCteOS) { this.xmlCteOS = xmlCteOS; }

	public String getProtocoloCte() { return protocoloCte; }
	public void setProtocoloCte(String protocoloCte) { this.protocoloCte = protocoloCte; }

	public String getMotivoCte() { return motivoCte; }
	public void setMotivoCte(String motivoCte) { this.motivoCte = motivoCte; }

	public String getDataAutorizacaoCte() { return dataAutorizacaoCte; }
	public void setDataAutorizacaoCte(String dataAutorizacaoCte) { this.dataAutorizacaoCte = dataAutorizacaoCte; }

	public String getProtocoloCancelamentoCte() { return protocoloCancelamentoCte; }
	public void setProtocoloCancelamentoCte(String protocoloCancelamentoCte) { this.protocoloCancelamentoCte = protocoloCancelamentoCte; }

	public String getDataCancelamentoCte() { return dataCancelamentoCte; }
	public void setDataCancelamentoCte(String dataCancelamentoCte) { this.dataCancelamentoCte = dataCancelamentoCte; }

	public String getJustificativaCancelamentoCte() { return justificativaCancelamentoCte; }
	public void setJustificativaCancelamentoCte(String justificativaCancelamentoCte) { this.justificativaCancelamentoCte = justificativaCancelamentoCte; }

	public Double getQuilometragem() { return quilometragem; }
	public void setQuilometragem(Double quilometragem) { this.quilometragem = quilometragem; }

	public String getStatusLicencaScmobi() { return statusLicencaScmobi; }
	public void setStatusLicencaScmobi(String statusLicencaScmobi) { this.statusLicencaScmobi = statusLicencaScmobi; }

	public Integer getNumeroLicencaScmobi() { return numeroLicencaScmobi; }
	public void setNumeroLicencaScmobi(Integer numeroLicencaScmobi) { this.numeroLicencaScmobi = numeroLicencaScmobi; }

	public String getErroLicencaScmobi() { return erroLicencaScmobi; }
	public void setErroLicencaScmobi(String erroLicencaScmobi) { this.erroLicencaScmobi = erroLicencaScmobi; }

	public String getJobIdScmobi() { return jobIdScmobi; }
	public void setJobIdScmobi(String jobIdScmobi) { this.jobIdScmobi = jobIdScmobi; }

	public String getDataGeracaoLicencaScmobi() { return dataGeracaoLicencaScmobi; }
	public void setDataGeracaoLicencaScmobi(String dataGeracaoLicencaScmobi) { this.dataGeracaoLicencaScmobi = dataGeracaoLicencaScmobi; }

	public byte[] getLicencaScmobiPdf() { return licencaScmobiPdf; }
	public void setLicencaScmobiPdf(byte[] licencaScmobiPdf) { this.licencaScmobiPdf = licencaScmobiPdf; }

	public byte[] getPassageirosScmobiPdf() { return passageirosScmobiPdf; }
	public void setPassageirosScmobiPdf(byte[] passageirosScmobiPdf) { this.passageirosScmobiPdf = passageirosScmobiPdf; }
}
