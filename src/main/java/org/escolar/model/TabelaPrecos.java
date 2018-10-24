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

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlRootElement;

import org.escolar.enums.BairroEnum;

@Entity
@XmlRootElement
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "id"))
public class TabelaPrecos implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue
    private Long id;

	private BairroEnum bairroCrianca;
	
	private BairroEnum bairroEscola;
	
	private double valorJaneiro;
	
	private double valorFevereiro;
	
	private double valorMarco;
	
	private double valorMaio;
	
	private short ano;
    
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		TabelaPrecos other = (TabelaPrecos) obj;
		if (bairroCrianca == null) {
			if (other.bairroCrianca != null)
				return false;
		} if (bairroEscola == null) {
			if (other.bairroEscola != null)
				return false;
		}
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		
		return true;

	}

	public BairroEnum getBairroCrianca() {
		return bairroCrianca;
	}

	public void setBairroCrianca(BairroEnum bairroCrianca) {
		this.bairroCrianca = bairroCrianca;
	}

	public double getValorMarco() {
		return valorMarco;
	}

	public void setValorMarco(double valorMarco) {
		this.valorMarco = valorMarco;
	}

	public double getValorFevereiro() {
		return valorFevereiro;
	}

	public void setValorFevereiro(double valorFevereiro) {
		this.valorFevereiro = valorFevereiro;
	}

	public double getValorJaneiro() {
		return valorJaneiro;
	}

	public void setValorJaneiro(double valorJaneiro) {
		this.valorJaneiro = valorJaneiro;
	}

	public double getValorMaio() {
		return valorMaio;
	}

	public void setValorMaio(double valorMaio) {
		this.valorMaio = valorMaio;
	}

	public BairroEnum getBairroEscola() {
		return bairroEscola;
	}

	public void setBairroEscola(BairroEnum bairroEscola) {
		this.bairroEscola = bairroEscola;
	}

	public short getAno() {
		return ano;
	}

	public void setAno(short ano) {
		this.ano = ano;
	}

    
}
