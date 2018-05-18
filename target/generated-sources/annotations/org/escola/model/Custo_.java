package org.escola.model;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import org.escola.enums.CustoEnum;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Custo.class)
public abstract class Custo_ {

	public static volatile SingularAttribute<Custo, CustoEnum> tipoCusto;
	public static volatile SingularAttribute<Custo, Carro> carro;
	public static volatile SingularAttribute<Custo, Date> data;
	public static volatile SingularAttribute<Custo, Double> valor;
	public static volatile SingularAttribute<Custo, String> nome;
	public static volatile SingularAttribute<Custo, Long> id;
	public static volatile SingularAttribute<Custo, String> descricao;

}

