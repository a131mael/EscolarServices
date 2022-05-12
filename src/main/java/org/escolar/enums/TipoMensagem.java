package org.escolar.enums;

public enum TipoMensagem {

	AVISO_BOLETO_ATRASAO_MES("Aviso boleto vencido"),
	
	AVISO_TRANSPORTE_SERA_SUSPENSO_AMANHA("Aviso Transporte sera suspenso amanha"),
	
	AVISO_TRANSPORTE_SERA_CANCELADO_AMANHA("Aviso Transporte sera cancelado amanha");
	
	
	private String tipo;
	
	TipoMensagem(String tipo){
		this.tipo = tipo;
	}

	public String getName() {
		return tipo;
	}

}
