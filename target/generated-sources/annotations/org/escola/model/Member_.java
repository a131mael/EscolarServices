package org.escola.model;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import org.escola.enums.TipoMembro;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Member.class)
public abstract class Member_ {

	public static volatile SingularAttribute<Member, String> senha;
	public static volatile SingularAttribute<Member, Funcionario> professor;
	public static volatile SingularAttribute<Member, Aluno> aluno;
	public static volatile SingularAttribute<Member, String> phoneNumber;
	public static volatile SingularAttribute<Member, String> name;
	public static volatile SingularAttribute<Member, TipoMembro> tipoMembro;
	public static volatile SingularAttribute<Member, Long> id;
	public static volatile SingularAttribute<Member, String> login;
	public static volatile SingularAttribute<Member, String> email;

}

