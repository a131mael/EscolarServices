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
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlRootElement;

import org.aaf.escolar.LocationRotaDTO;

@SuppressWarnings("serial")
@Entity
@XmlRootElement
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "id"))
public class LocalizacaoRota implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    @Column
    private String nome;
    
    @Column
    private String nomeMapa;
    
    @Column
    private double latitude;
    
    @Column
    private double longitude;
    
    @Column
    private Date hora;
    
    @Column
    private int periodo; 
    
    @Column
    private String androidID;
    
    public LocationRotaDTO getDTO(){
    	LocationRotaDTO dto = new LocationRotaDTO();
    	dto.setId(id);
    	dto.setLatitude(latitude);
    	dto.setLongitude(longitude);
    	dto.setNome(nome);
    	dto.setAndroidID(androidID);
    	dto.setNomeMapa(nomeMapa);
    	dto.setPeriodo(getPeriodo());
    	dto.setHora(getHora());
    	
    	return dto;
    }
    
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public String getAndroidID() {
		return androidID;
	}

	public void setAndroidID(String androidID) {
		this.androidID = androidID;
	}

	public String getNomeMapa() {
		return nomeMapa;
	}

	public void setNomeMapa(String nomeMapa) {
		this.nomeMapa = nomeMapa;
	}

	public Date getHora() {
		return hora;
	}

	public void setHora(Date hora) {
		this.hora = hora;
	}

	public int getPeriodo() {
		return periodo;
	}

	public void setPeriodo(int periodo) {
		this.periodo = periodo;
	}
}
