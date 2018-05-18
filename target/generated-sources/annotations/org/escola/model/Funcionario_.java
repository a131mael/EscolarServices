package org.escola.model;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import org.escola.enums.EspecialidadeEnum;
import org.escola.enums.TipoMembro;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Funcionario.class)
public abstract class Funcionario_ {

	public static volatile SingularAttribute<Funcionario, Boolean> ativo;
	public static volatile SingularAttribute<Funcionario, String> codigo;
	public static volatile SingularAttribute<Funcionario, Date> nascimento;
	public static volatile SingularAttribute<Funcionario, String> telefone1;
	public static volatile SingularAttribute<Funcionario, EspecialidadeEnum> especialidade;
	public static volatile SingularAttribute<Funcionario, String> endereco;
	public static volatile SingularAttribute<Funcionario, String> salario;
	public static volatile SingularAttribute<Funcionario, TipoMembro> tipoMembro;
	public static volatile SingularAttribute<Funcionario, Date> inicio;
	public static volatile SingularAttribute<Funcionario, String> nome;
	public static volatile SingularAttribute<Funcionario, String> telefone2;
	public static volatile SingularAttribute<Funcionario, String> login;
	public static volatile SingularAttribute<Funcionario, String> telefone3;
	public static volatile SingularAttribute<Funcionario, String> senha;
	public static volatile SingularAttribute<Funcionario, String> rg;
	public static volatile ListAttribute<Funcionario, Avaliacao> avaliacoes;
	public static volatile ListAttribute<Funcionario, FuncionarioCarro> turmas;
	public static volatile SingularAttribute<Funcionario, Member> member;
	public static volatile SingularAttribute<Funcionario, String> cpf;
	public static volatile SingularAttribute<Funcionario, Long> id;
	public static volatile SingularAttribute<Funcionario, String> email;

}

