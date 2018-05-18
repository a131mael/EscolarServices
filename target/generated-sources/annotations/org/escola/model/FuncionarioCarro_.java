package org.escola.model;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(FuncionarioCarro.class)
public abstract class FuncionarioCarro_ {

	public static volatile SingularAttribute<FuncionarioCarro, Boolean> principal;
	public static volatile SingularAttribute<FuncionarioCarro, Funcionario> professor;
	public static volatile SingularAttribute<FuncionarioCarro, Long> id;
	public static volatile SingularAttribute<FuncionarioCarro, Carro> turma;

}

