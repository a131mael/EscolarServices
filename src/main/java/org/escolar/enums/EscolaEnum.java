package org.escolar.enums;

public enum EscolaEnum {

	ADONAI("Adonai","Bela Vista"),
	
	CEMA("Cema", "Palhoca"),
	
	VENCESLAU("Venceslau Bueno","Palhoca"),
	
	JOAO_SILVEIRA("João Silveira","Aririu"),
	
	EVANDRA_SUELI("Evandra Sueli","Palhoca"),
	
	INES_MARTA("Inês Marta","Bela Vista"),
	
	VOVO_MARIA("Vovó Maria","Palhoca"),
	
	VOO_LIVRE("Voo Livre","Bela Vista"),

	DOM_JAIME("Dom Jaime","Bela Vista"),
	
	ITERACAO("Iteração","Bela Vista"),
	
	PROJETO_ESPERANCA("Projeto Esperança","Guarda do Cubatão"),
	
	PROF_GUILHERME("Prof. Guilherme","Palhoca"),
	
	MULLER("Muller","Palhoca"),
	
	MODELO("Modelo","Palhoca"),
	
	MULTIPLA_ESCOLHA("Multipla Escolha","Palhoca"),
	
	RODA_PIAO("Roda Pião","Palhoca"),

	ELCANA("Elcana","Palhoca"),
	
	ELCANANINHA("Elcaninha","Palhoca"),
	
	VIVENCIA("Vivência","Aririu"),
	
	PARAISO_DO_AMOR("Paraíso do Amor","Aririu"),
	
	ZILAR_ROSAR("Zilar Rosar","Aririu"),
	
	NOVA_ESPERANCA("Nova Esperança","Guarda"),
	
	MARIA_JOSE_MEDEIROS("Maria José de Medeiros","Aririu"),
	
	N_S_FATIMA("N.S. Fátima","Aririu"),

	INOVACAO("Inovação","Pacheco"),
	
	MARIA_DO_CARMO("Maria do Carmo","Pacheco"),
	
	CETEK("CETEK","Aririu"),
	
	CENTRO_COMUNITARIO_ARIRIU("Centro comunitario aririu","Aririu"),
	
	CENTRO_COMUNITARIO_ALTO_ARIRIU("Centro comunitario alto aririu","Alto Aririu"),
	
	NICOLINA("Nicolina Tancredo","Alto Aririu");
	
	
	private String name;
	private String bairro;
	
	/*EscolaEnum(String name){
		this.name = name;
		
	}*/
	
	EscolaEnum(String name,String bairro){
		this.name = name;
		this.bairro = bairro;
		
	}
	public String getName() {
		return name;
	}
	public String getBairro() {
		return bairro;
	}

}
