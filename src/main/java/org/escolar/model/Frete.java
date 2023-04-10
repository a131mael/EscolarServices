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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
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

}
