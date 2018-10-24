 package org.escolar.enums;

public enum BairroEnum {

	ARIRIU("Aririu"),
	
	ALTO_ARIRIU("Alto Aririu"),
	
	FORMIGA("Ariru da Formiga"),
	
	BELA_VISTA("Bela Vista"),

	PALHOCA_CENTRO("Centro Palhoça"),
	
	GUARDA("Guarda do Cubatão"),
	
	JAQUEIRA("Jaqueira"),

	JARDINS("Jardins, Bela Vista"),
	
	PACHECOS("PACHECOS"),
	
	MORADAS("Moradas, Bela Vista"),
	
	TERRA_NOVA("Terra Nova, Bela Vista"),
	
	VILA_NOVA("Vila Nova, Pachecos");
	
	
	private String name;
	
	BairroEnum(String name){
		this.name = name;
		
	}

	public String getName() {
		return name;
	}

}
