package org.escolar.enums;

public enum StatusBoletoEnum {

	A_VENCER("A Vencer"),
	PAGO("PAGO"),
	ATRASADO("Atrasado"),
	VENCE_HOJE("Vence Hoje"),
	RENEGOCIADO_A_VENCER("Renegociado "),
	RENEGOCIADO_ATRASADO("Renegociado atrasado"),
	CANCELADO("CANCELADO"),
	DIVIDA_PERDOADA("Divida Perdoada");
	
	
	private String name;
	
	StatusBoletoEnum(String name){
		this.name = name;
		
	}

	public String getName() {
		return name;
	}

}
