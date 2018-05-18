package org.escola.model;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(AlunoAvaliacao.class)
public abstract class AlunoAvaliacao_ {

	public static volatile SingularAttribute<AlunoAvaliacao, Integer> anoLetivo;
	public static volatile SingularAttribute<AlunoAvaliacao, Aluno> aluno;
	public static volatile SingularAttribute<AlunoAvaliacao, Long> id;
	public static volatile SingularAttribute<AlunoAvaliacao, Avaliacao> avaliacao;
	public static volatile SingularAttribute<AlunoAvaliacao, Float> nota;

}

