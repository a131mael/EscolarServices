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
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlRootElement;

import org.aaf.escolar.RecadoDestinatarioDTO;

@SuppressWarnings("serial")
@Entity
@XmlRootElement
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "id"))
public class RecadoDestinatario implements Serializable {

    @Id
    @GeneratedValue
    private Long id;
    
    @ManyToOne
    private Recado recado;
    
    @ManyToOne
    private Member destinatario;
    
    @Column
    private int resposta;
    
    @Column
    private String respostaExtenso;
   
	public RecadoDestinatarioDTO getDTO(){
		RecadoDestinatarioDTO recadoDestinatarioDTO = new RecadoDestinatarioDTO();
		recadoDestinatarioDTO.setId(id);
		recadoDestinatarioDTO.setResposta(resposta);
		recadoDestinatarioDTO.setRespostaExtenso(respostaExtenso);
		
		recadoDestinatarioDTO.setDestinatario(destinatario.getDTO());
		recadoDestinatarioDTO.setRecado(recado.getDTO());
		
		return recadoDestinatarioDTO;
	}

	public Recado getRecado() {
		return recado;
	}

	public void setRecado(Recado recado) {
		this.recado = recado;
	}

	public int getResposta() {
		return resposta;
	}

	public void setResposta(int resposta) {
		this.resposta = resposta;
	}

	public String getRespostaExtenso() {
		return respostaExtenso;
	}

	public void setRespostaExtenso(String respostaExtenso) {
		this.respostaExtenso = respostaExtenso;
	}

	public Member getDestinatario() {
		return destinatario;
	}

	public void setDestinatario(Member destinatario) {
		this.destinatario = destinatario;
	}




}
