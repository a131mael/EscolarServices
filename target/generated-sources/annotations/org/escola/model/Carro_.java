package org.escola.model;

import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Carro.class)
public abstract class Carro_ {

	public static volatile ListAttribute<Carro, AlunoCarro> alunosTurmas;
	public static volatile ListAttribute<Carro, FuncionarioCarro> professoresTurma;
	public static volatile SingularAttribute<Carro, String> nome;
	public static volatile SingularAttribute<Carro, Long> id;

}

