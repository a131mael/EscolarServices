package org.escolar.enums;

public enum TipoMensagem {

	AVISO_BOLETO_ATRASAO_MES("Aviso boleto vencido"),
	
	AVISO_BOLETO_ATRASAO_MES_ALERTA_PRE_SUSPENSAO("Alerta de suspensao 5 dias de atraso"),
	
	AVISO_BOLETO_ATRASAO_MES_ALERTA_SUSPENSAO("Alerta de suspensao 10 dias de atraso"),
	
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
