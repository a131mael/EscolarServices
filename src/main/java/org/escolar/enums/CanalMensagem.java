package org.escolar.enums;

public enum CanalMensagem {

	WHATSAPP("Whatsapp"),
	
	EMAIL("EMAIL");
	
	
	private String tipo;
	
	CanalMensagem(String tipo){
		this.tipo = tipo;
	}

	public String getName() {
		return tipo;
	}

}
