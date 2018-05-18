package org.escola.model;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Faturamento.class)
public abstract class Faturamento_ {

	public static volatile SingularAttribute<Faturamento, Carro> carro;
	public static volatile SingularAttribute<Faturamento, Integer> anoLetivo;
	public static volatile SingularAttribute<Faturamento, Date> data;
	public static volatile SingularAttribute<Faturamento, Double> valor;
	public static volatile SingularAttribute<Faturamento, Long> id;
	public static volatile SingularAttribute<Faturamento, Long> quantidadeCriancas;

}

