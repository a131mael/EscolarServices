 package org.escolar.enums;

public enum BimestreEnum {

	PRIMEIRO_BIMESTRE("1º Bimestre"),
	
	SEGUNDO_BIMESTRE("2º Bimestre"),
	
	TERCEIRO_BIMESTRE("3º Bimestre"),
	
	QUARTO_BIMESTRE("4º Bimestre");
	
	
	private String name;
	
	BimestreEnum(String name){
		this.name = name;
		
	}

	public String getName() {
		return name;
	}

}
