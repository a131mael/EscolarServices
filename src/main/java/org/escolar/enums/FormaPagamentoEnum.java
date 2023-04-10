package org.escolar.enums;

public enum FormaPagamentoEnum {

	DINHEIRO("Dinheiro"),
	PIX("PIX"),
	CHEQUE("Cheque"),
	CARTAO("Cartao"),;
	
	
	private String name;
	
	FormaPagamentoEnum(String name){
		this.name = name;
		
	}

	public String getName() {
		return name;
	}

}
