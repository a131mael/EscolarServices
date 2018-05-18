package org.escola.model;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import org.escola.enums.EscolaEnum;
import org.escola.enums.PegarEntregarEnun;
import org.escola.enums.PerioddoEnum;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(ObjetoRota.class)
public abstract class ObjetoRota_ {

	public static volatile SingularAttribute<ObjetoRota, Aluno> aluno;
	public static volatile SingularAttribute<ObjetoRota, EscolaEnum> escola;
	public static volatile SingularAttribute<ObjetoRota, PerioddoEnum> periodo;
	public static volatile SingularAttribute<ObjetoRota, Integer> quantidadeAlunos;
	public static volatile SingularAttribute<ObjetoRota, Integer> posicao;
	public static volatile SingularAttribute<ObjetoRota, Long> idCarro;
	public static volatile SingularAttribute<ObjetoRota, Long> idCarroAlvo;
	public static volatile SingularAttribute<ObjetoRota, String> nome;
	public static volatile SingularAttribute<ObjetoRota, Long> id;
	public static volatile SingularAttribute<ObjetoRota, PegarEntregarEnun> pegarEntregar;
	public static volatile SingularAttribute<ObjetoRota, String> descricao;

}

