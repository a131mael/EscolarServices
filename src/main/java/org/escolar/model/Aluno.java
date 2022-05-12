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
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

import org.escolar.enums.BairroEnum;
import org.escolar.enums.EscolaEnum;
import org.escolar.enums.PerioddoEnum;
import org.escolar.enums.Serie;
import org.escolar.enums.StatusBoletoEnum;
import org.escolar.util.Verificador;

@SuppressWarnings("serial")
@Entity
@XmlRootElement
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "id"))
public class Aluno implements Serializable {

	@Id
	@GeneratedValue
	private Long id;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<ContratoAluno> contratos;

	@Column
	private String contatoEmail1;

	@Column
	private String contatoEmail2;

	@Column
	private String contatoTelefone1;

	@Column
	private String contatoNome1;

	@Column
	private String contatoTelefone2;

	@Column
	private String contatoNome2;

	@Column
	private String contatoTelefone3;

	@Column
	private String contatoNome3;

	@Column
	private String contatoTelefone4;

	@Column
	private String contatoNome4;

	@Column
	private String contatoTelefone5;

	@Column
	private String contatoNome5;

	@Column
	private Boolean contato1WhatsValido;
	
	@Column
	private Boolean contato2WhatsValido;
	
	@Column
	private Boolean contato3WhatsValido;
	
	@Column
	private Boolean contato4WhatsValido;
	
	@Column
	private Boolean contato5WhatsValido;

	@Column
	private Boolean jaTestousContatosWhats;
	
	@Column
	private int anoLetivo;

	@ManyToOne(fetch = FetchType.LAZY)
	private Aluno irmao1;

	@ManyToOne(fetch = FetchType.LAZY)
	private Aluno irmao2;

	@ManyToOne(fetch = FetchType.LAZY)
	private Aluno irmao3;

	@ManyToOne(fetch = FetchType.LAZY)
	private Aluno irmao4;

	@ManyToOne(fetch = FetchType.LAZY)
	private Empresa empresa;
	
	@Column
	private Boolean verificadoOk;

	@Column
	private Boolean removido;

	@Column
	private Boolean restaurada;
	
	@Column
	private Boolean gerarNFSe;

	@Column
	private Boolean rematricular;

	/** DADOS DO ALUNO */
	@NotNull
	@Size(min = 1, max = 250)
	@Pattern(regexp = "[^0-9]*", message = "Must not contain numbers")
	private String nomeAluno;

	@OneToMany(fetch = FetchType.LAZY)
	private List<AlunoCarro> alunosCarros;

	@OneToMany(fetch = FetchType.LAZY)
	private List<AlunoAvaliacao> avaliacoes;

	@Column
	private String login;

	@Column
	private String codigo;

	@OneToOne(fetch = FetchType.LAZY)
	private Member member;

	@Column
	private String senha;

	@NotNull
	private Serie serie;

	@Column
	private String enderecoAluno;

	@NotNull
	private PerioddoEnum periodo;

	@Column
	private String telefone;

	@Column
	private Date dataMatricula;
	
	@Column
	private Date dataPrometeuPagar;
	
	@Column
	private String observacaoAtrasado;
	
	@Column
	private Boolean contactado;
	
	@Column
	private Date dataContato;
	
	@Column
	private Integer quantidadeContatos = 0;

	@Column
	private Date dataNascimento;

	@Column
	private EscolaEnum escola;

	@Column
	private BairroEnum bairroAluno;

	@Column
	private boolean trocaIDA;

	@Column
	private Boolean trocaIDA2;

	@Column
	private Boolean trocaIDA3;

	@Column
	private boolean trocaVolta;

	@Column
	private Boolean trocaVolta2;

	@Column
	private Boolean trocaVolta3;

	@ManyToOne(fetch = FetchType.LAZY)
	private Carro carroLevaParaEscola;

	@ManyToOne(fetch = FetchType.LAZY)
	private Carro carroLevaParaEscolaTroca;

	@ManyToOne(fetch = FetchType.LAZY)
	private Carro carroLevaParaEscolaTroca2;

	@ManyToOne(fetch = FetchType.LAZY)
	private Carro carroLevaParaEscolaTroca3;

	@ManyToOne(fetch = FetchType.LAZY)
	private Carro carroPegaEscola;

	@ManyToOne(fetch = FetchType.LAZY)
	private Carro carroPegaEscolaTroca;

	@ManyToOne(fetch = FetchType.LAZY)
	private Carro carroPegaEscolaTroca2;

	@ManyToOne(fetch = FetchType.LAZY)
	private Carro carroPegaEscolaTroca3;

	@Column
	private int idaVolta;

	@Column
	private String horaPegar;

	@Column
	private String horaEntregar;

	/** DADOS DO PAI */
	@Pattern(regexp = "[^0-9]*", message = "Must not contain numbers")
	private String nomePaiAluno;

	@Pattern(regexp = "[^0-9]*", message = "Must not contain numbers")
	private String nomeAvoPaternoPai;

	@Pattern(regexp = "[^0-9]*", message = "Must not contain numbers")
	private String nomeAvoHPaternoPai;

	@Pattern(regexp = "[^0-9]*", message = "Must not contain numbers")
	private String naturalidadePai;

	@Column
	private String cpfPai;

	@Column
	private String rgPai;

	@Column
	private String telefoneCelularPai;

	@Column
	private String telefoneResidencialPai;

	@Column
	private String emailPai;

	@Column
	private String empresaTrabalhaPai;

	@Column
	private String telefoneEmpresaTrabalhaPai;

	/** DADOS DA MAE */
	@Pattern(regexp = "[^0-9]*", message = "Must not contain numbers")
	private String nomeMaeAluno;

	@Pattern(regexp = "[^0-9]*", message = "Must not contain numbers")
	private String nomeAvoPaternoMae;

	@Pattern(regexp = "[^0-9]*", message = "Must not contain numbers")
	private String nomeAvoHPaternoMae;

	@Pattern(regexp = "[^0-9]*", message = "Must not contain numbers")
	private String naturalidadeMae;

	@Column
	private String cpfMae;

	@Column
	private String rgMae;

	@Column
	private String telefoneCelularMae;

	@Column
	private String telefoneResidencialMae;

	@Column
	private String emailMae;

	@Column
	private String empresaTrabalhaMae;

	@Column
	private String telefoneEmpresaTrabalhaMae;

	/* DADOS DE CONTATOS PARA SAIDAS */

	@Column
	private boolean ativo;

	@Column
	private String observacaoSecretaria;

	@Column
	private String observacaoMotorista;

	@Column
	private int diaVencimento = 10;

	@Column
	@Deprecated
	private String endereco; // TODO REMOVER ja no contrato

	@Column
	@Deprecated
	private Boolean cnabEnviado;

	@Column
	@Deprecated
	private String nomePaiResponsavel;

	@OneToMany(fetch = FetchType.LAZY)
	private List<Boleto> boletos;

	@Column
	@Deprecated
	private String nomeMaeResponsavel;

	@Column
	@Deprecated
	private boolean vencimentoUltimoDia;

	@Column
	@Deprecated
	private String bairro;

	@Column
	@Deprecated
	private String cep;

	@Column
	@Deprecated
	private String cidade;

	@Column
	@Deprecated
	private Double anuidade;

	@Column
	@Deprecated
	private Integer numeroParcelas;

	@Column
	@Deprecated
	private String nomeResponsavel;

	@Column
	@Deprecated
	private String cpfResponsavel;

	@Column
	@Deprecated
	private String rgResponsavel;

	@Column
	@Deprecated
	private double valorMensal;

	@Column
	@Deprecated
	private Date dataCancelamento;

	// DADOS PARA O FINANCEIRO

	@Column
	@Deprecated
	private Boolean enviadoParaCobrancaCDL;

	@Column
	@Deprecated
	private Boolean enviadoSPC;

	@Column
	@Deprecated
	private Boolean contratoTerminado;

	@Transient
	private Double valorTotalDevido;

	@Transient
	private int quantidadeMesAtrasado;
	
	@Transient
	private String mesesAtrasados;

	@Transient
	private String contatos;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Serie getSerie() {
		return serie;
	}

	public void setSerie(Serie serie) {
		this.serie = serie;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public String getTelefoneEmpresaTrabalhaMae() {
		return telefoneEmpresaTrabalhaMae;
	}

	public void setTelefoneEmpresaTrabalhaMae(String telefoneEmpresaTrabalhaMae) {
		this.telefoneEmpresaTrabalhaMae = telefoneEmpresaTrabalhaMae;
	}

	public String getEmpresaTrabalhaMae() {
		return empresaTrabalhaMae;
	}

	public void setEmpresaTrabalhaMae(String empresaTrabalhaMae) {
		this.empresaTrabalhaMae = empresaTrabalhaMae;
	}

	public String getEmailMae() {
		return emailMae;
	}

	public void setEmailMae(String emailMae) {
		this.emailMae = emailMae;
	}

	public String getTelefoneResidencialMae() {
		return telefoneResidencialMae;
	}

	public void setTelefoneResidencialMae(String telefoneResidencialMae) {
		this.telefoneResidencialMae = telefoneResidencialMae;
	}

	public String getTelefoneCelularMae() {
		return telefoneCelularMae;
	}

	public void setTelefoneCelularMae(String telefoneCelularMae) {
		this.telefoneCelularMae = telefoneCelularMae;
	}

	public String getRgMae() {
		return rgMae;
	}

	public void setRgMae(String rgMae) {
		this.rgMae = rgMae;
	}

	public String getCpfMae() {
		return cpfMae;
	}

	public void setCpfMae(String cpfMae) {
		this.cpfMae = cpfMae;
	}

	public String getNaturalidadeMae() {
		return naturalidadeMae;
	}

	public void setNaturalidadeMae(String naturalidadeMae) {
		this.naturalidadeMae = naturalidadeMae;
	}

	public String getNomeAvoHPaternoMae() {
		return nomeAvoHPaternoMae;
	}

	public void setNomeAvoHPaternoMae(String nomeAvoHPaternoMae) {
		this.nomeAvoHPaternoMae = nomeAvoHPaternoMae;
	}

	public String getNomeAvoPaternoMae() {
		return nomeAvoPaternoMae;
	}

	public void setNomeAvoPaternoMae(String nomeAvoPaternoMae) {
		this.nomeAvoPaternoMae = nomeAvoPaternoMae;
	}

	public String getNomeMaeAluno() {
		return nomeMaeAluno;
	}

	public void setNomeMaeAluno(String nomeMaeAluno) {
		this.nomeMaeAluno = nomeMaeAluno;
	}

	public String getTelefoneEmpresaTrabalhaPai() {
		return telefoneEmpresaTrabalhaPai;
	}

	public void setTelefoneEmpresaTrabalhaPai(String telefoneEmpresaTrabalhaPai) {
		this.telefoneEmpresaTrabalhaPai = telefoneEmpresaTrabalhaPai;
	}

	public String getEmpresaTrabalhaPai() {
		return empresaTrabalhaPai;
	}

	public void setEmpresaTrabalhaPai(String empresaTrabalhaPai) {
		this.empresaTrabalhaPai = empresaTrabalhaPai;
	}

	public String getEmailPai() {
		return emailPai;
	}

	public void setEmailPai(String emailPai) {
		this.emailPai = emailPai;
	}

	public String getTelefoneResidencialPai() {
		return telefoneResidencialPai;
	}

	public void setTelefoneResidencialPai(String telefoneResidencialPai) {
		this.telefoneResidencialPai = telefoneResidencialPai;
	}

	public String getTelefoneCelularPai() {
		return telefoneCelularPai;
	}

	public void setTelefoneCelularPai(String telefoneCelularPai) {
		this.telefoneCelularPai = telefoneCelularPai;
	}

	public String getRgPai() {
		return rgPai;
	}

	public void setRgPai(String rgPai) {
		this.rgPai = rgPai;
	}

	public String getCpfPai() {
		return cpfPai;
	}

	public void setCpfPai(String cpfPai) {
		this.cpfPai = cpfPai;
	}

	public String getNaturalidadePai() {
		return naturalidadePai;
	}

	public void setNaturalidadePai(String naturalidadePai) {
		this.naturalidadePai = naturalidadePai;
	}

	public String getNomeAvoHPaternoPai() {
		return nomeAvoHPaternoPai;
	}

	public void setNomeAvoHPaternoPai(String nomeAvoHPaternoPai) {
		this.nomeAvoHPaternoPai = nomeAvoHPaternoPai;
	}

	public String getNomeAvoPaternoPai() {
		return nomeAvoPaternoPai;
	}

	public void setNomeAvoPaternoPai(String nomeAvoPaternoPai) {
		this.nomeAvoPaternoPai = nomeAvoPaternoPai;
	}

	public String getNomePaiAluno() {
		return nomePaiAluno;
	}

	public void setNomePaiAluno(String nomePaiAluno) {
		this.nomePaiAluno = nomePaiAluno;
	}

	public String getTelefone() {
		return telefone;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
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

	public String getBairro() {
		return bairro;
	}

	public void setBairro(String bairro) {
		this.bairro = bairro;
	}

	public String getEndereco() {
		return endereco;
	}

	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}

	public String getNomeAluno() {
		return nomeAluno;
	}

	public void setNomeAluno(String nomeAluno) {
		this.nomeAluno = nomeAluno;
	}

	public PerioddoEnum getPeriodo() {
		return periodo;
	}

	public void setPeriodo(PerioddoEnum periodo) {
		this.periodo = periodo;
	}

	public Double getAnuidade() {
		return anuidade;
	}

	public void setAnuidade(double anuidade) {
		this.anuidade = anuidade;
	}

	public Integer getNumeroParcelas() {
		return numeroParcelas;
	}

	public void setNumeroParcelas(Integer numeroParcelas) {
		this.numeroParcelas = numeroParcelas;
	}

	public double getValorMensal() {
		return valorMensal;
	}

	public double getValorMensalComDesconto() {
		return valorMensal - 20;
	}

	public void setValorMensal(double valorMensal) {
		this.valorMensal = valorMensal;
	}

	public Date getDataMatricula() {
		return dataMatricula;
	}

	public void setDataMatricula(Date dataMatricula) {
		this.dataMatricula = dataMatricula;
	}

	public Date getDataNascimento() {
		return dataNascimento;
	}

	public void setDataNascimento(Date dataNascimento) {
		this.dataNascimento = dataNascimento;
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

	public List<AlunoAvaliacao> getAvaliacoes() {
		return avaliacoes;
	}

	public void setAvaliacoes(List<AlunoAvaliacao> avaliacoes) {
		this.avaliacoes = avaliacoes;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		Aluno other = (Aluno) obj;
		if (nomeAluno == null) {
			if (other.nomeAluno != null)
				return false;
		}
		if (serie == null) {
			if (other.serie != null)
				return false;
		} else if (!periodo.equals(other.periodo))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;

		return true;

	}

	public String getNomeResponsavel() {
		return nomeResponsavel;
	}

	public void setNomeResponsavel(String nomeResponsavel) {
		this.nomeResponsavel = nomeResponsavel;
	}

	public String getCpfResponsavel() {
		return cpfResponsavel;
	}

	public void setCpfResponsavel(String cpfResponsavel) {
		this.cpfResponsavel = cpfResponsavel;
	}

	public String getObservacaoSecretaria() {
		return observacaoSecretaria;
	}

	public void setObservacaoSecretaria(String observacaoSecretaria) {
		this.observacaoSecretaria = observacaoSecretaria;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public int getAnoLetivo() {
		return anoLetivo;
	}

	public void setAnoLetivo(int anoLetivo) {
		this.anoLetivo = anoLetivo;
	}

	public Boolean getRemovido() {
		return removido;
	}

	public void setRemovido(Boolean removido) {
		this.removido = removido;
	}

	public boolean isTrocaIDA() {
		return trocaIDA;
	}

	public void setTrocaIDA(boolean trocaIDA) {
		this.trocaIDA = trocaIDA;
	}

	public boolean isTrocaVolta() {
		return trocaVolta;
	}

	public void setTrocaVolta(boolean trocaVolta) {
		this.trocaVolta = trocaVolta;
	}

	public Carro getCarroLevaParaEscola() {
		return carroLevaParaEscola;
	}

	public void setCarroLevaParaEscola(Carro carroLevaParaEscola) {
		this.carroLevaParaEscola = carroLevaParaEscola;
	}

	public Carro getCarroLevaParaEscolaTroca() {
		return carroLevaParaEscolaTroca;
	}

	public void setCarroLevaParaEscolaTroca(Carro carroLevaParaEscolaTroca) {
		this.carroLevaParaEscolaTroca = carroLevaParaEscolaTroca;
	}

	public Carro getCarroPegaEscola() {
		return carroPegaEscola;
	}

	public void setCarroPegaEscola(Carro carroPegaEscola) {
		this.carroPegaEscola = carroPegaEscola;
	}

	public Carro getCarroPegaEscolaTroca() {
		return carroPegaEscolaTroca;
	}

	public void setCarroPegaEscolaTroca(Carro carroPegaEscolaTroca) {
		this.carroPegaEscolaTroca = carroPegaEscolaTroca;
	}

	public int getIdaVolta() {
		return idaVolta;
	}

	public void setIdaVolta(int idaVolta) {
		this.idaVolta = idaVolta;
	}

	public String getHoraPegar() {
		return horaPegar;
	}

	public void setHoraPegar(String horaPegar) {
		this.horaPegar = horaPegar;
	}

	public String getHoraEntregar() {
		return horaEntregar;
	}

	public void setHoraEntregar(String horaEntregar) {
		this.horaEntregar = horaEntregar;
	}

	public EscolaEnum getEscola() {
		return escola;
	}

	public void setEscola(EscolaEnum escola) {
		this.escola = escola;
	}

	@Override
	public String toString() {
		if (escola != null) {
			return nomeAluno + " - " + escola.getName();
		}
		return nomeAluno;
	}

	public String getRgResponsavel() {
		return rgResponsavel;
	}

	public void setRgResponsavel(String rgResponsavel) {
		this.rgResponsavel = rgResponsavel;
	}

	public Aluno getIrmao1() {
		return irmao1;
	}

	public void setIrmao1(Aluno irmao1) {
		this.irmao1 = irmao1;
	}

	public Aluno getIrmao2() {
		return irmao2;
	}

	public void setIrmao2(Aluno irmao2) {
		this.irmao2 = irmao2;
	}

	public Aluno getIrmao3() {
		return irmao3;
	}

	public void setIrmao3(Aluno irmao3) {
		this.irmao3 = irmao3;
	}

	public Aluno getIrmao4() {
		return irmao4;
	}

	public void setIrmao4(Aluno irmao4) {
		this.irmao4 = irmao4;
	}

	public String getNomeMaeResponsavel() {
		return nomeMaeResponsavel;
	}

	public void setNomeMaeResponsavel(String nomeMaeResponsavel) {
		this.nomeMaeResponsavel = nomeMaeResponsavel;
	}

	public String getNomePaiResponsavel() {
		return nomePaiResponsavel;
	}

	public void setNomePaiResponsavel(String nomePaiResponsavel) {
		this.nomePaiResponsavel = nomePaiResponsavel;
	}

	public String getContatoNome5() {
		return contatoNome5;
	}

	public void setContatoNome5(String contatoNome5) {
		this.contatoNome5 = contatoNome5;
	}

	public String getContatoTelefone5() {
		return contatoTelefone5;
	}

	public void setContatoTelefone5(String contatoTelefone5) {
		this.contatoTelefone5 = contatoTelefone5;
	}

	public String getContatoNome4() {
		return contatoNome4;
	}

	public void setContatoNome4(String contatoNome4) {
		this.contatoNome4 = contatoNome4;
	}

	public String getContatoTelefone4() {
		return contatoTelefone4;
	}

	public void setContatoTelefone4(String contatoTelefone4) {
		this.contatoTelefone4 = contatoTelefone4;
	}

	public String getContatoNome3() {
		return contatoNome3;
	}

	public void setContatoNome3(String contatoNome3) {
		this.contatoNome3 = contatoNome3;
	}

	public String getContatoTelefone3() {
		return contatoTelefone3;
	}

	public void setContatoTelefone3(String contatoTelefone3) {
		this.contatoTelefone3 = contatoTelefone3;
	}

	public String getContatoNome2() {
		return contatoNome2;
	}

	public void setContatoNome2(String contatoNome2) {
		this.contatoNome2 = contatoNome2;
	}

	public String getContatoTelefone2() {
		return contatoTelefone2;
	}

	public void setContatoTelefone2(String contatoTelefone2) {
		this.contatoTelefone2 = contatoTelefone2;
	}

	public String getContatoNome1() {
		return contatoNome1;
	}

	public void setContatoNome1(String contatoNome1) {
		this.contatoNome1 = contatoNome1;
	}

	public String getContatoTelefone1() {
		return contatoTelefone1;
	}

	public void setContatoTelefone1(String contatoTelefone1) {
		this.contatoTelefone1 = contatoTelefone1;
	}

	public String getContatoEmail2() {
		return contatoEmail2;
	}

	public void setContatoEmail2(String contatoEmail2) {
		this.contatoEmail2 = contatoEmail2;
	}

	public String getContatoEmail1() {
		return contatoEmail1;
	}

	public void setContatoEmail1(String contatoEmail1) {
		this.contatoEmail1 = contatoEmail1;
	}

	public List<Boleto> getBoletos() {
		return boletos;
	}

	public List<Boleto> getBoletos2() {
		List<Boleto> boletos = new ArrayList<>();
		for (ContratoAluno contrato : contratos) {
			boletos.addAll(contrato.getBoletos());
		}
		return boletos;
	}

	@Deprecated
	public List<org.aaf.financeiro.model.Boleto> getBoletosFinanceiro() {
		List<org.aaf.financeiro.model.Boleto> boletosFinanceiro = new ArrayList<>();
		/*
		 * if(boletos!= null){ for(Boleto boleto : boletos){
		 * org.aaf.financeiro.model.Boleto boletoFinanceiro = new
		 * org.aaf.financeiro.model.Boleto();
		 * boletoFinanceiro.setEmissao(boleto.getEmissao());
		 * boletoFinanceiro.setId(boleto.getId());
		 * boletoFinanceiro.setValorNominal(boleto.getValorNominal());
		 * boletoFinanceiro.setVencimento(boleto.getVencimento());
		 * boletoFinanceiro.setNossoNumero(String.valueOf(boleto.getNossoNumero(
		 * ))); boletoFinanceiro.setDataPagamento(OfficeUtil.
		 * retornaDataSomenteNumeros(boleto.getDataPagamento()));
		 * boletoFinanceiro.setValorPago(boleto.getValorPago());
		 * boletosFinanceiro.add(boletoFinanceiro); } }
		 */
		return boletosFinanceiro;
	}

	public void setBoletos(List<Boleto> boletos) {
		this.boletos = boletos;
	}

	public Boolean isTrocaIDA2() {
		return trocaIDA2;
	}

	public void setTrocaIDA2(Boolean trocaIDA2) {
		this.trocaIDA2 = trocaIDA2;
	}

	public Boolean isTrocaIDA3() {
		return trocaIDA3;
	}

	public void setTrocaIDA3(Boolean trocaIDA3) {
		this.trocaIDA3 = trocaIDA3;
	}

	public Boolean isTrocaVolta3() {
		return trocaVolta3;
	}

	public void setTrocaVolta3(Boolean trocaVolta3) {
		this.trocaVolta3 = trocaVolta3;
	}

	public Boolean isTrocaVolta2() {
		return trocaVolta2;
	}

	public void setTrocaVolta2(Boolean trocaVolta2) {
		this.trocaVolta2 = trocaVolta2;
	}

	public Carro getCarroLevaParaEscolaTroca2() {
		return carroLevaParaEscolaTroca2;
	}

	public void setCarroLevaParaEscolaTroca2(Carro carroLevaParaEscolaTroca2) {
		this.carroLevaParaEscolaTroca2 = carroLevaParaEscolaTroca2;
	}

	public Carro getCarroLevaParaEscolaTroca3() {
		return carroLevaParaEscolaTroca3;
	}

	public void setCarroLevaParaEscolaTroca3(Carro carroLevaParaEscolaTroca3) {
		this.carroLevaParaEscolaTroca3 = carroLevaParaEscolaTroca3;
	}

	public Carro getCarroPegaEscolaTroca2() {
		return carroPegaEscolaTroca2;
	}

	public void setCarroPegaEscolaTroca2(Carro carroPegaEscolaTroca2) {
		this.carroPegaEscolaTroca2 = carroPegaEscolaTroca2;
	}

	public Carro getCarroPegaEscolaTroca3() {
		return carroPegaEscolaTroca3;
	}

	public void setCarroPegaEscolaTroca3(Carro carroPegaEscolaTroca3) {
		this.carroPegaEscolaTroca3 = carroPegaEscolaTroca3;
	}

	public Boolean getRematricular() {
		return rematricular;
	}

	public void setRematricular(Boolean rematricular) {
		this.rematricular = rematricular;
	}

	public Boolean getEnviadoParaCobrancaCDL() {
		return enviadoParaCobrancaCDL;
	}

	public void setEnviadoParaCobrancaCDL(Boolean enviadoParaCobrancaCDL) {
		this.enviadoParaCobrancaCDL = enviadoParaCobrancaCDL;
	}

	public Boolean getEnviadoSPC() {
		return enviadoSPC;
	}

	public void setEnviadoSPC(Boolean enviadoSPC) {
		this.enviadoSPC = enviadoSPC;
	}

	public Boolean getContratoTerminado() {
		return contratoTerminado;
	}

	public void setContratoTerminado(Boolean contratoTerminado) {
		this.contratoTerminado = contratoTerminado;
	}

	public Double getValorTotalDevido() {
		return valorTotalDevido;
	}

	public void setValorTotalDevido(Double valorTotalDevido) {
		this.valorTotalDevido = valorTotalDevido;
	}

	public Boolean getCnabEnviado() {
		return cnabEnviado;
	}

	public void setCnabEnviado(Boolean cnabEnviado) {
		this.cnabEnviado = cnabEnviado;
	}

	public Date getDataCancelamento() {
		return dataCancelamento;
	}

	public void setDataCancelamento(Date dataCancelamento) {
		this.dataCancelamento = dataCancelamento;
	}

	public Boolean getRestaurada() {
		return restaurada;
	}

	public void setRestaurada(Boolean restaurada) {
		this.restaurada = restaurada;
	}

	public Boolean getVerificadoOk() {
		return verificadoOk;
	}

	public void setVerificadoOk(Boolean verificadoOk) {
		this.verificadoOk = verificadoOk;
	}

	public String getEnderecoAluno() {
		return enderecoAluno;
	}

	public void setEnderecoAluno(String enderecoAluno) {
		this.enderecoAluno = enderecoAluno;
	}

	public BairroEnum getBairroAluno() {
		return bairroAluno;
	}

	public void setBairroAluno(BairroEnum bairroAluno) {
		this.bairroAluno = bairroAluno;
	}

	public List<ContratoAluno> getContratos() {
		if (contratos != null) {
			Collections.sort(contratos);
		}
		return contratos;
	}

	public List<ContratoAluno> getContratosSux() {
		return contratos;
	}

	public void setContratos(List<ContratoAluno> contratos) {
		this.contratos = contratos;
	}

	public List<Boleto> getBoletos(int ano) {
		List<Boleto> boletos = new ArrayList<>();
		for (ContratoAluno contrato : contratos) {
			if (contrato.getAno() == ano) {
				boletos.addAll(contrato.getBoletos());
			}
		}
		return boletos;
	}

	public ContratoAluno getContratoVigente() {
		ContratoAluno contratoAtivo = null;
		if (contratos != null) {
			for (ContratoAluno contrato : contratos) {
				if ((contrato.getCancelado() == null || !contrato.getCancelado() )) {
					
					if(contratoAtivo != null && contrato.getDataCriacaoContrato().after(contratoAtivo.getDataCriacaoContrato())){
						contratoAtivo = contrato;
					}else if(contratoAtivo == null){
						contratoAtivo = contrato;
					}
				}
			}
		}
		
		if(contratoAtivo == null){
			if(contratos != null && contratos.size() > 0){
				return contratos.get(0);
			}
		}

		return contratoAtivo;
	}

	public ContratoAluno getContratoVigente(int anoLetivo) {
		ContratoAluno contratoAtivo = null;
		if (contratos != null) {
			for (ContratoAluno contrato : contratos) {
				if ((contrato.getCancelado() == null || !contrato.getCancelado() )) {
					
					if(contratoAtivo != null 
							&& contrato.getDataCriacaoContrato().after(contratoAtivo.getDataCriacaoContrato())
							&& contrato.getAno() == anoLetivo){
						contratoAtivo = contrato;
					}else if(contratoAtivo == null && contrato.getAno() == anoLetivo ){
						contratoAtivo = contrato;
					}
				}
			}
		}
		
		if(contratoAtivo == null){
			if(contratos != null && contratos.size() > 0){
				return contratos.get(0);
			}
		}

		return contratoAtivo;
	}

	
	public List<ContratoAluno> getContratosVigentes() {
		List<ContratoAluno> contratosAtivo = new ArrayList<>();
		for (ContratoAluno contrato : contratos) {
			if (contrato.isContratoAtivo()) {
				contratosAtivo.add(contrato);
			}
		}
		return contratosAtivo;
	}
	
	

	public ContratoAluno getUltimoContrato() {
		ContratoAluno conts = null;
		if (contratos != null) {
			for (ContratoAluno contrato : contratos) {
				if (contrato.getNumero() != null && !contrato.getNumero().equalsIgnoreCase("")) {
					if (conts == null || Integer.parseInt(contrato.getNumero()) > Integer.parseInt(conts.getNumero())) {
						conts = contrato;
					}
				}
			}
		}

		return conts;
	}

	public int getDiaVencimento() {
		return diaVencimento;
	}

	public void setDiaVencimento(int diaVencimento) {
		this.diaVencimento = diaVencimento;
	}

	public boolean isVencimentoUltimoDia() {
		return vencimentoUltimoDia;
	}

	public void setVencimentoUltimoDia(boolean vencimentoUltimoDia) {
		this.vencimentoUltimoDia = vencimentoUltimoDia;
	}

	public int getQuantidadeMesAtrasado() {
		return quantidadeMesAtrasado;
	}

	public void setQuantidadeMesAtrasado(int quantidadeMesAtrasado) {
		this.quantidadeMesAtrasado = quantidadeMesAtrasado;
	}

	public String getMesesAtrasados() {
		return mesesAtrasados;
	}

	public void setMesesAtrasados(String mesesAtrasados) {
		this.mesesAtrasados = mesesAtrasados;
	}

	public String getContatos() {
		return contatos;
	}

	public void setContatos(String contatos) {
		this.contatos = contatos;
	}

	public Boolean getContactado() {
		return contactado;
	}

	public void setContactado(Boolean contactado) {
		this.contactado = contactado;
	}

	public Date getDataContato() {
		return dataContato;
	}

	public void setDataContato(Date dataContato) {
		this.dataContato = dataContato;
	}

	public Integer getQuantidadeContatos() {
		if(quantidadeContatos == null){
			return 0; 
		}
		return quantidadeContatos;
	}

	public void setQuantidadeContatos(Integer quantidadeContatos) {
		this.quantidadeContatos = quantidadeContatos;
	}

	public Date getDataPrometeuPagar() {
		return dataPrometeuPagar;
	}

	public void setDataPrometeuPagar(Date dataPrometeuPagar) {
		this.dataPrometeuPagar = dataPrometeuPagar;
	}

	public String getObservacaoAtrasado() {
		return observacaoAtrasado;
	}

	public void setObservacaoAtrasado(String observacaoAtrasado) {
		this.observacaoAtrasado = observacaoAtrasado;
	}

	public Boolean getGerarNFSe() {
		return gerarNFSe;
	}

	public void setGerarNFSe(Boolean gerarNFSe) {
		this.gerarNFSe = gerarNFSe;
	}
	
	public double getTotalABerto(){
		Double total = 0D;
		List<Boleto> boletos = getBoletos2();
		for(Boleto b : boletos){
			StatusBoletoEnum status = Verificador.getStatusEnum(b); 
			if(status.equals(StatusBoletoEnum.ATRASADO)){
				total += Verificador.getValorFinal(b);
			}
		}
		
		return total;
	}

	public Boolean isJaTestousContatosWhats() {
		return jaTestousContatosWhats;
	}

	public void setJaTestousContatosWhats(Boolean jaTestousContatosWhats) {
		this.jaTestousContatosWhats = jaTestousContatosWhats;
	}

	public Boolean isContato5WhatsValido() {
		return contato5WhatsValido;
	}

	public void setContato5WhatsValido(Boolean contato5WhatsValido) {
		this.contato5WhatsValido = contato5WhatsValido;
	}

	public Boolean isContato4WhatsValido() {
		return contato4WhatsValido;
	}

	public void setContato4WhatsValido(Boolean contato4WhatsValido) {
		this.contato4WhatsValido = contato4WhatsValido;
	}

	public Boolean isContato3WhatsValido() {
		return contato3WhatsValido;
	}

	public void setContato3WhatsValido(Boolean contato3WhatsValido) {
		this.contato3WhatsValido = contato3WhatsValido;
	}

	public Boolean isContato2WhatsValido() {
		return contato2WhatsValido;
	}

	public void setContato2WhatsValido(Boolean contato2WhatsValido) {
		this.contato2WhatsValido = contato2WhatsValido;
	}

	public Boolean isContato1WhatsValido() {
		return contato1WhatsValido;
	}

	public void setContato1WhatsValido(Boolean contato1WhatsValido) {
		this.contato1WhatsValido = contato1WhatsValido;
	}
	
	public List<String> contatosWhatsValido(){
		List<String> contatos = new ArrayList<>();
		if(jaTestousContatosWhats!= null && jaTestousContatosWhats){
			if(contato1WhatsValido != null && contato1WhatsValido){
				contatos.add(contatoTelefone1);
			}
			if(contato2WhatsValido!= null &&contato2WhatsValido ){
				contatos.add(contatoTelefone2);
			}
			
			if(contato3WhatsValido!= null &&contato3WhatsValido ){
				contatos.add(contatoTelefone3);
			}
			
			if(contato4WhatsValido!= null && contato4WhatsValido ){
				contatos.add(contatoTelefone4);
			}
			
			if(contato5WhatsValido!= null && contato5WhatsValido ){
				contatos.add(contatoTelefone5);
			}
		}else{
			contatos.add(contatoTelefone1);
			contatos.add(contatoTelefone2);
			contatos.add(contatoTelefone3);
			contatos.add(contatoTelefone4);
			contatos.add(contatoTelefone5);
		}
		return contatos;
	}
}
