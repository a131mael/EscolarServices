package org.escolar.enums;

public enum EscolaEnum {

	ADONAI("Adonai","Bela Vista",BairroEnum.BELA_VISTA),
	
	CEMA("Cema", "Palhoca",BairroEnum.PALHOCA_CENTRO),
	
	VENCESLAU("Venceslau Bueno","Palhoca",BairroEnum.PALHOCA_CENTRO),
	
	JOAO_SILVEIRA("João Silveira","Aririu",BairroEnum.ARIRIU),
	
	EVANDRA_SUELI("Evandra Sueli","Palhoca",BairroEnum.PALHOCA_CENTRO),
	
	INES_MARTA("Inês Marta","Bela Vista",BairroEnum.BELA_VISTA),
	
	VOVO_MARIA("Vovó Maria","Palhoca",BairroEnum.PALHOCA_CENTRO),
	
	VOO_LIVRE("Voo Livre","Bela Vista",BairroEnum.BELA_VISTA),

	DOM_JAIME("Dom Jaime","Bela Vista",BairroEnum.BELA_VISTA),
	
	ITERACAO("Interação","Bela Vista",BairroEnum.BELA_VISTA),
	
	PROJETO_ESPERANCA("Projeto Esperança","Guarda do Cubatão",BairroEnum.GUARDA),
	
	PROF_GUILHERME("Prof. Guilherme","Palhoca",BairroEnum.PALHOCA_CENTRO),
	
	MULLER("Muller","Palhoca",BairroEnum.PALHOCA_CENTRO),
	
	MODELO("Modelo","Palhoca",BairroEnum.PALHOCA_CENTRO),
	
	MULTIPLA_ESCOLHA("Multipla Escolha","Palhoca",BairroEnum.PALHOCA_CENTRO),
	
	RODA_PIAO("Roda Pião","Palhoca",BairroEnum.PALHOCA_CENTRO),

	ELCANA("Elcana","Palhoca",BairroEnum.PALHOCA_CENTRO),
	
	ELCANANINHA("Elcaninha","Palhoca",BairroEnum.PALHOCA_CENTRO),
	
	VIVENCIA("Vivência","Aririu",BairroEnum.ARIRIU),
	
	PARAISO_DO_AMOR("Paraíso do Amor","Aririu",BairroEnum.ARIRIU),
	
	ZILAR_ROSAR("Zilar Rosar","Aririu",BairroEnum.ARIRIU),
	
	NOVA_ESPERANCA("Nova Esperança","Guarda",BairroEnum.GUARDA),
	
	MARIA_JOSE_MEDEIROS("Maria José de Medeiros","Aririu",BairroEnum.ARIRIU),
	
	N_S_FATIMA("N.S. Fátima","Aririu",BairroEnum.ARIRIU),

	INOVACAO("Inovação","Pacheco",BairroEnum.PACHECOS),
	
	MARIA_DO_CARMO("Maria do Carmo","Pacheco",BairroEnum.PACHECOS),
	
	CETEK("CETEK","Aririu",BairroEnum.ARIRIU),
	
	CENTRO_COMUNITARIO_ARIRIU("Centro comunitario aririu","Aririu",BairroEnum.ARIRIU),
	
	CENTRO_COMUNITARIO_ALTO_ARIRIU("Centro comunitario alto aririu","Alto Aririu",BairroEnum.ALTO_ARIRIU),
	
	NICOLINA("Nicolina Tancredo","Alto Aririu",BairroEnum.ALTO_ARIRIU),
	
	MUNDO_ENCANTADO("Mundo Encantado","Bela Vista",BairroEnum.BELA_VISTA),
	
	RAIZES("Raizes","Bela Vista",BairroEnum.BELA_VISTA),
	
	CIRANDA_COLORIDA("Ciranda Colorida","Pachecos",BairroEnum.PACHECOS),
	
	COLEGIO_MILITAR("Colégio Militar","Bela Vista",BairroEnum.BELA_VISTA),
	
	SESC("SESC","Ponte Imaruim",BairroEnum.Ponte_IMARUIM),
	
	EDUCARE("Centro Educacional Educare","Centro Palhoça",BairroEnum.PALHOCA_CENTRO),
	
	IVO_SILVEIRA("Ivo Silveira","Centro Palhoça",BairroEnum.PALHOCA_CENTRO);
	
	
	private String name;
	private String bairro;
	private BairroEnum bairroEnum;
	
	/*EscolaEnum(String name){
		this.name = name;
		
	}*/
	
	EscolaEnum(String name,String bairro,BairroEnum bairroEnum){
		this.name = name;
		this.bairro = bairro;
		this.bairroEnum = bairroEnum;
		
	}
	public String getName() {
		return name;
	}
	public String getBairro() {
		return bairro;
	}
	public BairroEnum getBairroEnum() {
		return bairroEnum;
	}
}
