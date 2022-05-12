package org.escolar.enums;

public enum CustoEnum {

	SALARIO_MOTORISTA("Salário Motorista"),
	SALARIO_MONITOR("Salário monitor"),
	COMBUSTIVEL("Combustivel"),
	MECANICA("Mecânica"),
	LIMPEZA("Limpeza"),
	PRESTACAO_CARRO("Prestação Carro"),
	SEGURO("Seguro"),
	DOCUMENTACAO("DOCUMENTOS E IMPOSTOS"),
	INTERNET("Internet"),
	OUTROS("Outros");
	
	
	private String name;
	
	CustoEnum(String name){
		this.name = name;
		
	}

	public String getName() {
		return name;
	}

}
