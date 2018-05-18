package org.escola.model;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import org.escola.enums.BimestreEnum;
import org.escola.enums.DisciplinaEnum;
import org.escola.enums.Serie;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Avaliacao.class)
public abstract class Avaliacao_ {

	public static volatile SingularAttribute<Avaliacao, Date> data;
	public static volatile SingularAttribute<Avaliacao, Boolean> bimestral;
	public static volatile SingularAttribute<Avaliacao, Integer> peso;
	public static volatile SingularAttribute<Avaliacao, Boolean> prova;
	public static volatile SingularAttribute<Avaliacao, String> nome;
	public static volatile SingularAttribute<Avaliacao, Boolean> provaFinal;
	public static volatile SingularAttribute<Avaliacao, Boolean> recuperacao;
	public static volatile SingularAttribute<Avaliacao, Funcionario> professor;
	public static volatile SingularAttribute<Avaliacao, BimestreEnum> bimestre;
	public static volatile ListAttribute<Avaliacao, AlunoAvaliacao> avaliacoes;
	public static volatile SingularAttribute<Avaliacao, Boolean> trabalho;
	public static volatile SingularAttribute<Avaliacao, DisciplinaEnum> disciplina;
	public static volatile SingularAttribute<Avaliacao, Serie> serie;
	public static volatile SingularAttribute<Avaliacao, Long> id;

}

