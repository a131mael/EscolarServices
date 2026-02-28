 package org.escolar.enums;

public enum BairroEnum2 {

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
	
	VILA_NOVA("Vila Nova, Pachecos"),
	
	Ponte_IMARUIM("Ponte Imaruim, Ponte Imaruim"),
	
	VALE_VERDE("Vale Verde, Bela Vista"),
	
	AZALEIA("Azaleia, Bela Vista"),
	
	BROMELIAS("Bromelias, Bela Vista"),
	
	PARQUE_DAS_ROSAS("Parque das rosas, Bela Vista"),
	
	Tabuleiro("Tabuleiro, Bela Vista");
	
	private String name;
	
	BairroEnum2(String name){
		this.name = name;
		
	}

	public String getName() {
		return name;
	}

}
