package org.escolar.enums;

public enum StatusContratoEnum {

	NENHUM("Nenhum"),
	
	NAO_ENVIAR_CONVITE("Não rematricular"),
	
	CONVITE_ENVIADO("Convite enviado"),
	
	RECUSADO("Convite Recusado"),
	
	ACEITO("Convite Aceito"),
	
	ACEITO_CONTRATO_ENVIADO("Convite aceito contrato enviado"),
	
	ASSINADO("Contrato Assinado");
	
	
	
	
	private String name;
	
	StatusContratoEnum(String name){
		this.name = name;
		
	}

	public String getName() {
		return name;
	}

}
