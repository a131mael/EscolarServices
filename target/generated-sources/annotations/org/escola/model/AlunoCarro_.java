package org.escola.model;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(AlunoCarro.class)
public abstract class AlunoCarro_ {

	public static volatile SingularAttribute<AlunoCarro, Carro> carro;
	public static volatile SingularAttribute<AlunoCarro, Integer> anoLetivo;
	public static volatile SingularAttribute<AlunoCarro, Aluno> aluno;
	public static volatile SingularAttribute<AlunoCarro, Long> id;

}

