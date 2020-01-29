/*
] * JBoss, Home of Professional Open Source
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
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlRootElement;

import org.aaf.financeiro.util.OfficeUtil;

@SuppressWarnings("serial")
@Entity
@XmlRootElement
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "id"))
public class ContratoAluno implements Serializable,Comparable<ContratoAluno> {

	@Id
	@GeneratedValue(generator = "GENERATE_contratoAluno", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "GENERATE_contratoAluno", sequenceName = "contratoAluno_pk_seq", allocationSize = 1)
	private Long id;

	@ManyToOne
	private Aluno aluno;

	@OneToMany(fetch = FetchType.LAZY, cascade= CascadeType.ALL)
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
	private String endereco;

	@Column
	private Boolean cnabEnviado;
	
	@Column
	private Boolean cancelado;
	
	@Column
	private Boolean usuarioAppCriado;

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
	private int diaVencimento =10;		
	
	@Column
    private Boolean contratoTerminado;
	
	@Column
    private Boolean protestado;
	
	@Column
    private Boolean podeProtestarFinal;
	
	@Column
    private Boolean enviadoProtestoDefinitivo;
	
	@Column
    private Boolean enviadoPorEmailProtesto;
	
	@Lob
	private String comentario;
	
	@Transient
	private Boolean atrasado;

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

	public Boolean getCancelado() {
		return cancelado;
	}

	public void setCancelado(Boolean cancelado) {
		this.cancelado = cancelado;
	}

	public Boolean getCnabEnviado() {
		return cnabEnviado;
	}

	public void setCnabEnviado(Boolean cnabEnviado) {
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
		if(boletos != null){
			Collections.sort(boletos);
		}
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

	public int getDiaVencimento() {
		return diaVencimento;
	}

	public void setDiaVencimento(int diaVencimento) {
		this.diaVencimento = diaVencimento;
	}

	public String getEndereco() {
		return endereco;
	}

	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}
	
	public boolean isContratoAtivo(){
		for(Boleto boleto: boletos){
			if(boleto.getCancelado() != null && boleto.getValorPago() != null){
				if(!boleto.getCancelado() && boleto.getValorPago()<boleto.getValorNominal()-20 ){
					return true;
				}
			}
		}
		return true;
	}
	
	public ContratoAluno clone(){
		ContratoAluno contratoPersistence = new ContratoAluno();
		if (this.getEndereco() != null) {
			contratoPersistence.setEndereco(removeCaracteresEspeciais(this.getEndereco()));
		}
		if (this.getBairro() != null) {
			contratoPersistence.setBairro(removeCaracteresEspeciais(this.getBairro().replace("ç", "c")));

		}
		contratoPersistence.setCep(this.getCep());
		if (this.getCidade() != null) {
			contratoPersistence.setCidade(removeCaracteresEspeciais(this.getCidade().replace("ç", "c")));
		}
		contratoPersistence.setValorMensal(this.getValorMensal());
		

		contratoPersistence.setAnuidade(this.getAnuidade() != null ? this.getAnuidade() : 0);
		if (this.getBairro() != null) {
			contratoPersistence.setBairro(removeCaracteresEspeciais(this.getBairro().replace("ç", "c")));
		}
		
		contratoPersistence.setCep(this.getCep());
		if (this.getCidade() != null) {
			contratoPersistence.setCidade(removeCaracteresEspeciais(this.getCidade().replace("ç", "c")));
		}
	//	contratoPersistence.setCpfPai(this.getCpfPai());
		if (this.getCpfResponsavel() != null) {
			contratoPersistence.setCpfResponsavel(this.getCpfResponsavel().replace(".", "").replace("-", ""));
		}
		contratoPersistence.setRgResponsavel(this.getRgResponsavel());
		if (this.getNomeResponsavel() != null) {
			contratoPersistence.setNomeResponsavel(removeCaracteresEspeciais(this.getNomeResponsavel().toUpperCase()));
		}
		
		if (this.getNomePaiResponsavel() != null) {
			contratoPersistence.setNomePaiResponsavel(removeCaracteresEspeciais(this.getNomePaiResponsavel().toUpperCase()));
		}
		
		if (this.getNomeMaeResponsavel() != null) {
			contratoPersistence.setNomeMaeResponsavel(removeCaracteresEspeciais(this.getNomeMaeResponsavel().toUpperCase()));
		}
		
		contratoPersistence.setNumeroParcelas(this.getNumeroParcelas());
		
		contratoPersistence.setValorMensal(this.getValorMensal());
		contratoPersistence.setDiaVencimento(this.getDiaVencimento());
		contratoPersistence.setVencimentoUltimoDia(this.getVencimentoUltimoDia());
		if (contratoPersistence.getCidade() != null && contratoPersistence.getCidade().equalsIgnoreCase("Palhoa")) {
			contratoPersistence.setCidade("Palhoca");
		}
		if (contratoPersistence.getBairro() != null && contratoPersistence.getBairro().equalsIgnoreCase("Palhoa")) {
			contratoPersistence.setBairro("Palhoca");
		}

		contratoPersistence.setCnabEnviado(this.getCnabEnviado());
		return contratoPersistence;
	}

	public String removeCaracteresEspeciais(String texto) {
		texto = texto.replaceAll("[^aA-zZ-Z0-9 ]", "");
		return texto;
	}
	
	public List<org.aaf.financeiro.model.Boleto> getBoletosFinanceiro() {
		List<org.aaf.financeiro.model.Boleto> boletosFinanceiro = new ArrayList<>();
		if(boletos!= null){
			for(Boleto boleto : boletos){
				org.aaf.financeiro.model.Boleto boletoFinanceiro = new org.aaf.financeiro.model.Boleto();
				boletoFinanceiro.setEmissao(boleto.getEmissao());
				boletoFinanceiro.setId(boleto.getId());
				boletoFinanceiro.setValorNominal(boleto.getValorNominal());
				boletoFinanceiro.setVencimento(boleto.getVencimento());
				boletoFinanceiro.setNossoNumero(String.valueOf(boleto.getNossoNumero()));
				boletoFinanceiro.setDataPagamento(OfficeUtil.retornaDataSomenteNumeros(boleto.getDataPagamento()));
				boletoFinanceiro.setValorPago(boleto.getValorPago());
				boletosFinanceiro.add(boletoFinanceiro);
			}
		}
		return boletosFinanceiro;
	}
	
	@Override
	public int compareTo(ContratoAluno o) {
		if(this.getId() > o.getId()){
			return -1;
		}else if(this.getId() < o.getId()){
			return 1;
		}
		
		return 0;
	}

	public Boolean getAtrasado() {
		return atrasado;
	}

	public void setAtrasado(Boolean atrasado) {
		this.atrasado = atrasado;
	}

	public Boolean getProtestado() {
		return protestado;
	}

	public void setProtestado(Boolean protestado) {
		this.protestado = protestado;
	}

	public Boolean getPodeProtestarFinal() {
		return podeProtestarFinal;
	}

	public void setPodeProtestarFinal(Boolean podeProtestarFinal) {
		this.podeProtestarFinal = podeProtestarFinal;
	}

	public Boolean getEnviadoProtestoDefinitivo() {
		return enviadoProtestoDefinitivo;
	}

	public void setEnviadoProtestoDefinitivo(Boolean enviadoProtestoDefinitivo) {
		this.enviadoProtestoDefinitivo = enviadoProtestoDefinitivo;
	}

	public String getComentario() {
		return comentario;
	}

	public void setComentario(String comentario) {
		this.comentario = comentario;
	}

	public Boolean getEnviadoPorEmailProtesto() {
		return enviadoPorEmailProtesto;
	}

	public void setEnviadoPorEmailProtesto(Boolean enviadoPorEmailProtesto) {
		this.enviadoPorEmailProtesto = enviadoPorEmailProtesto;
	}

	public Boolean getUsuarioAppCriado() {
		return usuarioAppCriado;
	}

	public void setUsuarioAppCriado(Boolean usuarioAppCriado) {
		this.usuarioAppCriado = usuarioAppCriado;
	}
	
}
