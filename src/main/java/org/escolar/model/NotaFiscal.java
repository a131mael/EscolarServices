package org.escolar.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlRootElement;

/** Nota fiscal de servico (NFS-e) emitida pelo sistema nacional (ADN -- Ambiente
 * de Dados Nacional, gov.br/nfse). Mesma logica do projeto Escola (Adonai),
 * replicada pra Favo de Mel (transporte escolar). */
@SuppressWarnings("serial")
@Entity
@XmlRootElement
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "id"))
public class NotaFiscal implements Serializable {

	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne
	private Boleto boleto;

	/** "homologacao" ou "producao" -- pra nunca confundir nota de teste com de verdade */
	@Column
	private String ambiente;

	/** PENDENTE, AUTORIZADA, REJEITADA, CANCELADA */
	@Column
	private String status;

	/** Id da DPS que foi enviada (antes de ter retorno do governo) */
	@Column
	private String idDps;

	/** Chave de acesso da NFS-e, devolvida pelo governo quando autorizada */
	@Column
	private String chaveAcesso;

	/** Numero da NFS-e (quando autorizada) */
	@Column
	private String numero;

	@Column
	private Double valor;

	/** "SISTEMA" (emitida por este software via API) ou "HISTORICO_ADN" (importada da
	 * distribuicao do governo -- nota emitida manualmente, sem boleto vinculado aqui). */
	@Column
	private String origem;

	/** So preenchido pra notas importadas do ADN (sem boleto vinculado) -- nome de quem pagou. */
	@Column
	private String nomeTomador;

	@Column
	private Date dataEnvio;

	@Column
	private Date dataAutorizacao;

	/** Motivo caso tenha sido rejeitada pelo governo */
	@Lob
	@Column
	private String motivoRejeicao;

	@Lob
	@Column
	private String xmlDpsEnviado;

	@Lob
	@Column
	private String xmlNfseRetorno;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Boleto getBoleto() {
		return boleto;
	}

	public void setBoleto(Boleto boleto) {
		this.boleto = boleto;
	}

	public String getAmbiente() {
		return ambiente;
	}

	public void setAmbiente(String ambiente) {
		this.ambiente = ambiente;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getIdDps() {
		return idDps;
	}

	public void setIdDps(String idDps) {
		this.idDps = idDps;
	}

	public String getChaveAcesso() {
		return chaveAcesso;
	}

	public void setChaveAcesso(String chaveAcesso) {
		this.chaveAcesso = chaveAcesso;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public Double getValor() {
		return valor;
	}

	public void setValor(Double valor) {
		this.valor = valor;
	}

	public String getOrigem() {
		return origem;
	}

	public void setOrigem(String origem) {
		this.origem = origem;
	}

	public String getNomeTomador() {
		return nomeTomador;
	}

	public void setNomeTomador(String nomeTomador) {
		this.nomeTomador = nomeTomador;
	}

	public Date getDataEnvio() {
		return dataEnvio;
	}

	public void setDataEnvio(Date dataEnvio) {
		this.dataEnvio = dataEnvio;
	}

	public Date getDataAutorizacao() {
		return dataAutorizacao;
	}

	public void setDataAutorizacao(Date dataAutorizacao) {
		this.dataAutorizacao = dataAutorizacao;
	}

	public String getMotivoRejeicao() {
		return motivoRejeicao;
	}

	public void setMotivoRejeicao(String motivoRejeicao) {
		this.motivoRejeicao = motivoRejeicao;
	}

	public String getXmlDpsEnviado() {
		return xmlDpsEnviado;
	}

	public void setXmlDpsEnviado(String xmlDpsEnviado) {
		this.xmlDpsEnviado = xmlDpsEnviado;
	}

	public String getXmlNfseRetorno() {
		return xmlNfseRetorno;
	}

	public void setXmlNfseRetorno(String xmlNfseRetorno) {
		this.xmlNfseRetorno = xmlNfseRetorno;
	}

}
