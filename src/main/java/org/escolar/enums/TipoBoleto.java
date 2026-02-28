package org.escolar.enums;

public enum TipoBoleto {

	MENSALIDADE("Mensalidade"),
	
	ACORDO_DIVIDA("Acordo divida"),
	
	OUTRO("Outro");
	
	private String tipo;
	
	TipoBoleto(String tipo){
		this.tipo = tipo;
	}

	public String getName() {
		return tipo;
	}

}
