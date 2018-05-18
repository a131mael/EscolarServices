package org.escola.model;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Boleto.class)
public abstract class Boleto_ {

	public static volatile SingularAttribute<Boleto, Double> valorNominal;
	public static volatile SingularAttribute<Boleto, Date> dataPagamento;
	public static volatile SingularAttribute<Boleto, Aluno> pagador;
	public static volatile SingularAttribute<Boleto, Double> valorPago;
	public static volatile SingularAttribute<Boleto, Boolean> conciliacaoPorExtrato;
	public static volatile SingularAttribute<Boleto, Date> vencimento;
	public static volatile SingularAttribute<Boleto, Boolean> baixaManual;
	public static volatile SingularAttribute<Boleto, Boolean> alteracaoBoletoManual;
	public static volatile SingularAttribute<Boleto, Boolean> cancelado;
	public static volatile SingularAttribute<Boleto, Boolean> manterAposRemovido;
	public static volatile SingularAttribute<Boleto, Long> nossoNumero;
	public static volatile SingularAttribute<Boleto, Boolean> baixaGerada;
	public static volatile SingularAttribute<Boleto, Long> id;
	public static volatile SingularAttribute<Boleto, Date> emissao;

}

