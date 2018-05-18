package org.escola.model;

import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Devedor.class)
public abstract class Devedor_ {

	public static volatile SingularAttribute<Devedor, String> telefoneResidencial;
	public static volatile SingularAttribute<Devedor, String> cidade;
	public static volatile SingularAttribute<Devedor, String> observacao;
	public static volatile SingularAttribute<Devedor, Boolean> enviadoParaCobrancaCDL;
	public static volatile SingularAttribute<Devedor, Boolean> enviadoSPC;
	public static volatile SingularAttribute<Devedor, String> numeroContrato;
	public static volatile SingularAttribute<Devedor, String> endereco;
	public static volatile SingularAttribute<Devedor, String> bairro;
	public static volatile SingularAttribute<Devedor, String> nome;
	public static volatile SingularAttribute<Devedor, String> telefoneCelular;
	public static volatile SingularAttribute<Devedor, String> cep;
	public static volatile SingularAttribute<Devedor, String> telefoneCelular2;
	public static volatile SingularAttribute<Devedor, String> cpf;
	public static volatile SingularAttribute<Devedor, Boolean> contratoTerminado;
	public static volatile SingularAttribute<Devedor, Long> id;
	public static volatile SingularAttribute<Devedor, Boolean> removido;
	public static volatile ListAttribute<Devedor, Boleto> boletos;
	public static volatile SingularAttribute<Devedor, String> email;

}

