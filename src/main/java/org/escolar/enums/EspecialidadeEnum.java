package org.escolar.enums;

public enum EspecialidadeEnum {

	MOTORISTA("Motorista"),
	
	MONITOR("Monitor");
	
	private String tipo;
	
	EspecialidadeEnum(String tipo){
		this.tipo = tipo;
	}

	public String getName() {
		return tipo;
	}

}
