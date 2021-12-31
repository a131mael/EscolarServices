package org.escolar.enums;

public enum TipoMembro {

	SECRETARIA("Secretaria"),
	
	MOTORISTA("Motorista"),
	
	ADMIM("Administrador"),
	
	ALUNO("Aluno"),
	
	MONITOR("Monitor"),
	
	MESTRE("Mestre"),
	
	FINANCEIRO("Financeiro"),
	
	USUARIO("Usuario"),
	
	FILIAL("Filial"),
	
	DONO("Dono");
	
	
	private String tipo;
	
	TipoMembro(String tipo){
		this.tipo = tipo;
	}

	public String getName() {
		
		return tipo;
	}

}
