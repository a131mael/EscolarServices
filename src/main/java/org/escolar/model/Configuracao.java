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
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlRootElement;


@SuppressWarnings("serial")
@Entity
@XmlRootElement
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "id"))
public class Configuracao implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    @Column
    private int anoLetivo;
    
    @Column
    private Double valordinheiroEmcaixa;
    
    @Column
    private Short anoRematricula;
    
    @Column
    private long sequencialArquivoCNAB;

    /** Dados fixos do emitente (Favo) usados na emissao do CT-e OS (modelo 67) */
    @Column
    private String cnpj;

    @Column
    private String ie;

    @Column
    private String razaoSocial;

    @Column
    private String nomeFantasia;

    @Column
    private String crt;

    @Column
    private String logradouro;

    @Column
    private String numeroEndereco;

    @Column
    private String complementoEndereco;

    @Column
    private String bairro;

    @Column
    private String codMunicipio;

    @Column
    private String municipio;

    @Column
    private String uf;

    @Column
    private String cep;

    @Column
    private String telefone;

    /** Termo de Autorizacao de Fretamento (preencher TAF ou nroRegEstadual) */
    @Column
    private String taf;

    @Column
    private String nroRegEstadual;

    @Column
    private Integer serieCte;

    @Column
    private Long proximoNumeroCte;

    /** 1 = Producao, 2 = Homologacao */
    @Column
    private String tpAmb;

    @Column
    private String cfop;

    @Column
    private String naturezaOperacao;

    /** Aliquota de ICMS (%) usada quando o emitente nao for optante do Simples Nacional */
    @Column
    private Double aliquotaIcms;

    /** Caminho absoluto do arquivo do certificado digital A1 (.pfx) usado para assinar o CT-e OS */
    @Column
    private String certificadoPath;

    @Column
    private String certificadoSenha;

    /** Credenciais de login no scMOBI (scmobi.sie.sc.gov.br), usadas pelo scmobi-automation-service */
    @Column
    private String scmobiUsuario;

    @Column
    private String scmobiSenha;

    /** Client ID da aplicacao cadastrada no Portal Developers do Sicoob (API Cobranca Bancaria) */
    @Column
    private String sicoobClientId;

    /** Numero do contrato de cobranca no Sicoob */
    @Column
    private String sicoobNumeroContrato;

    /** Codigo da modalidade da carteira de cobranca (ex: 1 = Simples com registro) */
    @Column
    private String sicoobCodigoModalidade;

    /** "producao" ou "sandbox" (homologacao). Usa o mesmo certificado A1 do CT-e OS em producao */
    @Column
    private String sicoobAmbiente;

    /** Codigo da agencia (cooperativa) Sicoob — usado para calcular o DV do Nosso Numero (ex: 3069) */
    @Column
    private String sicoobCodigoAgencia;

    /** Limite maximo de pagamento via Pix (oculto na UI). Padrao: 5000.00 */
    @Column
    private Double pixValorMaximo;

    /** Senha necessaria para autorizar pagamentos Pix de funcionarios (oculto na UI) */
    @Column
    private String pixSenha;

    /** Client ID do app Sicoob com escopo pix_pagamentos (pode ser diferente do client_id de boletos) */
    @Column
    private String sicoobPixClientId;

    /** ISPB da cooperativa/banco Sicoob (ex: 07853842). Usado no campo origem.ispb do PIX confirmacao */
    @Column
    private String sicoobIspb;

    /** Numero da conta corrente da empresa no Sicoob. Usado no campo origem.conta do PIX confirmacao */
    @Column
    private String sicoobContaCorrente;

    @Transient
    private double valorNotas;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}


	public int getAnoLetivo() {
		return anoLetivo;
	}


	public void setAnoLetivo(int anoLetivo) {
		this.anoLetivo = anoLetivo;
	}

	public long getSequencialArquivoCNAB() {
		return sequencialArquivoCNAB;
	}

	public void setSequencialArquivoCNAB(long sequencialArquivoCNAB) {
		this.sequencialArquivoCNAB = sequencialArquivoCNAB;
	}

	public Short getAnoRematricula() {
		return anoRematricula;
	}

	public void setAnoRematricula(Short anoRematricula) {
		this.anoRematricula = anoRematricula;
	}

	public double getValorNotas() {
		return valorNotas;
	}

	public void setValorNotas(double valorNotas) {
		this.valorNotas = valorNotas;
	}

	public Double getValordinheiroEmcaixa() {
		return valordinheiroEmcaixa;
	}

	public void setValordinheiroEmcaixa(Double valordinheiroEmcaixa) {
		this.valordinheiroEmcaixa = valordinheiroEmcaixa;
	}

	public String getCnpj() {
		return cnpj;
	}

	public void setCnpj(String cnpj) {
		this.cnpj = cnpj;
	}

	public String getIe() {
		return ie;
	}

	public void setIe(String ie) {
		this.ie = ie;
	}

	public String getRazaoSocial() {
		return razaoSocial;
	}

	public void setRazaoSocial(String razaoSocial) {
		this.razaoSocial = razaoSocial;
	}

	public String getNomeFantasia() {
		return nomeFantasia;
	}

	public void setNomeFantasia(String nomeFantasia) {
		this.nomeFantasia = nomeFantasia;
	}

	public String getCrt() {
		return crt;
	}

	public void setCrt(String crt) {
		this.crt = crt;
	}

	public String getLogradouro() {
		return logradouro;
	}

	public void setLogradouro(String logradouro) {
		this.logradouro = logradouro;
	}

	public String getNumeroEndereco() {
		return numeroEndereco;
	}

	public void setNumeroEndereco(String numeroEndereco) {
		this.numeroEndereco = numeroEndereco;
	}

	public String getComplementoEndereco() {
		return complementoEndereco;
	}

	public void setComplementoEndereco(String complementoEndereco) {
		this.complementoEndereco = complementoEndereco;
	}

	public String getBairro() {
		return bairro;
	}

	public void setBairro(String bairro) {
		this.bairro = bairro;
	}

	public String getCodMunicipio() {
		return codMunicipio;
	}

	public void setCodMunicipio(String codMunicipio) {
		this.codMunicipio = codMunicipio;
	}

	public String getMunicipio() {
		return municipio;
	}

	public void setMunicipio(String municipio) {
		this.municipio = municipio;
	}

	public String getUf() {
		return uf;
	}

	public void setUf(String uf) {
		this.uf = uf;
	}

	public String getCep() {
		return cep;
	}

	public void setCep(String cep) {
		this.cep = cep;
	}

	public String getTelefone() {
		return telefone;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	public String getTaf() {
		return taf;
	}

	public void setTaf(String taf) {
		this.taf = taf;
	}

	public String getNroRegEstadual() {
		return nroRegEstadual;
	}

	public void setNroRegEstadual(String nroRegEstadual) {
		this.nroRegEstadual = nroRegEstadual;
	}

	public Integer getSerieCte() {
		return serieCte;
	}

	public void setSerieCte(Integer serieCte) {
		this.serieCte = serieCte;
	}

	public Long getProximoNumeroCte() {
		return proximoNumeroCte;
	}

	public void setProximoNumeroCte(Long proximoNumeroCte) {
		this.proximoNumeroCte = proximoNumeroCte;
	}

	public String getTpAmb() {
		return tpAmb;
	}

	public void setTpAmb(String tpAmb) {
		this.tpAmb = tpAmb;
	}

	public String getCfop() {
		return cfop;
	}

	public void setCfop(String cfop) {
		this.cfop = cfop;
	}

	public String getNaturezaOperacao() {
		return naturezaOperacao;
	}

	public void setNaturezaOperacao(String naturezaOperacao) {
		this.naturezaOperacao = naturezaOperacao;
	}

	public Double getAliquotaIcms() {
		return aliquotaIcms;
	}

	public void setAliquotaIcms(Double aliquotaIcms) {
		this.aliquotaIcms = aliquotaIcms;
	}

	public String getCertificadoPath() {
		return certificadoPath;
	}

	public void setCertificadoPath(String certificadoPath) {
		this.certificadoPath = certificadoPath;
	}

	public String getCertificadoSenha() {
		return certificadoSenha;
	}

	public void setCertificadoSenha(String certificadoSenha) {
		this.certificadoSenha = certificadoSenha;
	}

	public String getScmobiUsuario() {
		return scmobiUsuario;
	}

	public void setScmobiUsuario(String scmobiUsuario) {
		this.scmobiUsuario = scmobiUsuario;
	}

	public String getScmobiSenha() {
		return scmobiSenha;
	}

	public void setScmobiSenha(String scmobiSenha) {
		this.scmobiSenha = scmobiSenha;
	}

	public String getSicoobClientId() {
		return sicoobClientId;
	}

	public void setSicoobClientId(String sicoobClientId) {
		this.sicoobClientId = sicoobClientId;
	}

	public String getSicoobNumeroContrato() {
		return sicoobNumeroContrato;
	}

	public void setSicoobNumeroContrato(String sicoobNumeroContrato) {
		this.sicoobNumeroContrato = sicoobNumeroContrato;
	}

	public String getSicoobCodigoModalidade() {
		return sicoobCodigoModalidade;
	}

	public void setSicoobCodigoModalidade(String sicoobCodigoModalidade) {
		this.sicoobCodigoModalidade = sicoobCodigoModalidade;
	}

	public String getSicoobAmbiente() {
		return sicoobAmbiente;
	}

	public void setSicoobAmbiente(String sicoobAmbiente) {
		this.sicoobAmbiente = sicoobAmbiente;
	}

	public String getSicoobCodigoAgencia() {
		return sicoobCodigoAgencia;
	}

	public void setSicoobCodigoAgencia(String sicoobCodigoAgencia) {
		this.sicoobCodigoAgencia = sicoobCodigoAgencia;
	}

	public Double getPixValorMaximo() { return pixValorMaximo; }
	public void setPixValorMaximo(Double pixValorMaximo) { this.pixValorMaximo = pixValorMaximo; }

	public String getPixSenha() { return pixSenha; }
	public void setPixSenha(String pixSenha) { this.pixSenha = pixSenha; }

	public String getSicoobPixClientId() { return sicoobPixClientId; }
	public void setSicoobPixClientId(String sicoobPixClientId) { this.sicoobPixClientId = sicoobPixClientId; }

	public String getSicoobIspb() { return sicoobIspb; }
	public void setSicoobIspb(String sicoobIspb) { this.sicoobIspb = sicoobIspb; }

	public String getSicoobContaCorrente() { return sicoobContaCorrente; }
	public void setSicoobContaCorrente(String sicoobContaCorrente) { this.sicoobContaCorrente = sicoobContaCorrente; }

}
