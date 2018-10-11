package org.escolar.enums;

public enum TipoDestinatario {

	TODOS("Todos"),
	
	PROFESSORES("Professores"),
	
	PRIMEIRO("1 ano"),
	
	SEGUNDO("2 ano"),
	
	TERCEIRO("3 ano"),
	
	QUARTO("4 ano"),
	
	QUINTO("5 ano"),
	
	SEXTO("6 ano"),
	
	SETIMO("7 ano"),
	
	OITAVO("8 ano"),
	
	NONO("9 ano"),
	
	MEMBRO("Membro");
	
	private String tipo;
	
	TipoDestinatario(String tipo){
		this.tipo = tipo;
	}

	public String getName() {
		
		return tipo;
	}

}
