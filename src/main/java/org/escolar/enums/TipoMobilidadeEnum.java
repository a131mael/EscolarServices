package org.escolar.enums;

public enum TipoMobilidadeEnum {

	SO_VAI(""),

	SO_VAI_TROCA_IDA(""),

	SO_VOLTA(""),

	SO_VOLTA_TROCA_VOLTA(""),
			
	VAI_VOLTA_MESMO_CARRO(""),
	
	VAI_VOLTA_IDA_CARRO_DIFERENTE(""),
	
	VAI_VOLTA_VOLTA_CARRO_DIFERENTE(""),
		
	VAI_VOLTA_IDA_COM_TROCA_IDA(""),
	
	VAI_VOLTA_IDA_COM_TROCA_VOLTA(""),
	
	VAI_VOLTA_VOLTA_COM_TROCA_IDA(""),
	
	VAI_VOLTA_VOLTA_COM_TROCA_VOLTA(""),
	
	VAI_VOLTA_IDA_COM_TROCA_IDA_E_COM_TROCA_VOLTA(""),
	
	VAI_VOLTA_VOLTA_COM_TROCA_IDA_E_COM_TROCA_VOLTA("");
	
	
	private String tipo;
	
	TipoMobilidadeEnum(String tipo){
		this.tipo = tipo;
	}

	public String getName() {
		return tipo;
	}

}
