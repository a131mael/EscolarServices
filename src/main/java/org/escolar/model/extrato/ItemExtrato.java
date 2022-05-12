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
package org.escolar.model.extrato;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlRootElement;

import org.escolar.model.Custo;

import br.com.aaf.base.importacao.extrato.ExtratoGruposPagamentoRecebimentoEnum;
import br.com.aaf.base.importacao.extrato.ExtratoTiposEntradaEnum;
import br.com.aaf.base.importacao.extrato.ExtratoTiposEntradaSaidaEnum;

@SuppressWarnings("serial")
@Entity
@XmlRootElement
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "id"))
public class ItemExtrato implements Serializable {

	@Id
	@GeneratedValue
	private Long id;

	@Column
	private ExtratoGruposPagamentoRecebimentoEnum grupoRecebimento;
	
	@Column
	private ExtratoTiposEntradaEnum tipoEntrada;
	
	@Column
	private ExtratoTiposEntradaSaidaEnum tipoEntradaSaida;
	
	@Column
	private Double valor;
	
	@Column(length = 2048)
	private String observacao;
	
	@Column
	private Date dataEvento;

	private int mes;
	
	private int ano;
	
	private Boolean atualizado;
	
	private String codigoEntrada;
	
	@OneToMany(cascade=CascadeType.ALL)
	private List<ItemExtrato> itensFilhos;
	
	private Boolean pai;
	
	@OneToOne(cascade=CascadeType.ALL,orphanRemoval=true)
	private Custo custo;
	
	@Override
	public boolean equals(Object obj) {
		ItemExtrato i = (ItemExtrato) obj;
		if(this.getCodigoEntrada().equalsIgnoreCase(i.getCodigoEntrada())){
			if(this.ano == i.getAno()){
				if(this.mes == i.getMes()){
					return true;
				}
			}
		}
		return false;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ExtratoGruposPagamentoRecebimentoEnum getGrupoRecebimento() {
		return grupoRecebimento;
	}

	public void setGrupoRecebimento(ExtratoGruposPagamentoRecebimentoEnum grupoRecebimento) {
		this.grupoRecebimento = grupoRecebimento;
	}

	public Double getValor() {
		return valor;
	}

	public void setValor(Double valor) {
		this.valor = valor;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public Date getDataEvento() {
		return dataEvento;
	}

	public void setDataEvento(Date dataEvento) {
		this.dataEvento = dataEvento;
	}

	public ExtratoTiposEntradaEnum getTipoEntrada() {
		return tipoEntrada;
	}

	public void setTipoEntrada(ExtratoTiposEntradaEnum tipoEntrada) {
		this.tipoEntrada = tipoEntrada;
	}

	public int getMes() {
		return mes;
	}

	public void setMes(int mes) {
		this.mes = mes;
	}

	public int getAno() {
		return ano;
	}

	public void setAno(int ano) {
		this.ano = ano;
	}

	public String getCodigoEntrada() {
		return codigoEntrada;
	}

	public void setCodigoEntrada(String codigoEntrada) {
		this.codigoEntrada = codigoEntrada;
	}

	public ExtratoTiposEntradaSaidaEnum getTipoEntradaSaida() {
		return tipoEntradaSaida;
	}

	public void setTipoEntradaSaida(ExtratoTiposEntradaSaidaEnum tipoEntradaSaida) {
		this.tipoEntradaSaida = tipoEntradaSaida;
	}

	public Boolean getAtualizado() {
		return atualizado;
	}

	public void setAtualizado(Boolean atualizado) {
		this.atualizado = atualizado;
	}

	public List<ItemExtrato> getItensFilhos() {
		if(itensFilhos == null){
			itensFilhos = new ArrayList<>();
		}
		return itensFilhos;
	}

	public void setItensFilhos(List<ItemExtrato> itensFilhos) {
		this.itensFilhos = itensFilhos;
	}

	public boolean isPai() {
		if(pai == null){
			return false;
		}
		return pai;
	}

	public void setPai(boolean pai) {
		this.pai = pai;
	}
	
	public void addFilho(ItemExtrato filho){
		if(itensFilhos != null && itensFilhos.size() > 0){
			itensFilhos.add(filho);
		}else{
			itensFilhos = new ArrayList<ItemExtrato>();
			itensFilhos.add(filho);
		}
	}

	public Custo getCusto() {
		return custo;
	}

	public void setCusto(Custo custo) {
		this.custo = custo;
	}
}
