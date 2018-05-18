package org.escola.model;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import org.escola.enums.EscolaEnum;
import org.escola.enums.PerioddoEnum;
import org.escola.enums.Serie;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Aluno.class)
public abstract class Aluno_ {

	public static volatile SingularAttribute<Aluno, String> cpfResponsavel;
	public static volatile SingularAttribute<Aluno, Boolean> trocaVolta3;
	public static volatile SingularAttribute<Aluno, String> emailPai;
	public static volatile SingularAttribute<Aluno, String> telefone;
	public static volatile SingularAttribute<Aluno, Boolean> trocaVolta2;
	public static volatile SingularAttribute<Aluno, Integer> numeroParcelas;
	public static volatile SingularAttribute<Aluno, String> nomeMaeAluno;
	public static volatile SingularAttribute<Aluno, String> telefoneEmpresaTrabalhaPai;
	public static volatile SingularAttribute<Aluno, Integer> anoLetivo;
	public static volatile SingularAttribute<Aluno, String> rgResponsavel;
	public static volatile SingularAttribute<Aluno, Carro> carroLevaParaEscolaTroca3;
	public static volatile SingularAttribute<Aluno, Boolean> trocaVolta;
	public static volatile SingularAttribute<Aluno, String> contatoEmail2;
	public static volatile SingularAttribute<Aluno, String> contatoTelefone5;
	public static volatile SingularAttribute<Aluno, String> telefoneCelularMae;
	public static volatile SingularAttribute<Aluno, String> contatoTelefone4;
	public static volatile SingularAttribute<Aluno, String> telefoneResidencialMae;
	public static volatile SingularAttribute<Aluno, String> observacaoMotorista;
	public static volatile SingularAttribute<Aluno, Boolean> contratoTerminado;
	public static volatile SingularAttribute<Aluno, Long> id;
	public static volatile SingularAttribute<Aluno, Date> dataNascimento;
	public static volatile SingularAttribute<Aluno, String> nomeAvoHPaternoMae;
	public static volatile SingularAttribute<Aluno, Carro> carroLevaParaEscolaTroca2;
	public static volatile SingularAttribute<Aluno, String> codigo;
	public static volatile SingularAttribute<Aluno, String> nomeAvoPaternoMae;
	public static volatile SingularAttribute<Aluno, Boolean> enviadoParaCobrancaCDL;
	public static volatile SingularAttribute<Aluno, Boolean> enviadoSPC;
	public static volatile SingularAttribute<Aluno, Carro> carroLevaParaEscola;
	public static volatile SingularAttribute<Aluno, String> bairro;
	public static volatile SingularAttribute<Aluno, String> rgMae;
	public static volatile SingularAttribute<Aluno, String> contatoEmail1;
	public static volatile SingularAttribute<Aluno, String> horaEntregar;
	public static volatile SingularAttribute<Aluno, Boolean> restaurada;
	public static volatile SingularAttribute<Aluno, Carro> carroPegaEscolaTroca2;
	public static volatile SingularAttribute<Aluno, Carro> carroPegaEscolaTroca3;
	public static volatile SingularAttribute<Aluno, String> empresaTrabalhaPai;
	public static volatile SingularAttribute<Aluno, Boolean> cnabEnviado;
	public static volatile SingularAttribute<Aluno, Boolean> verificadoOk;
	public static volatile SingularAttribute<Aluno, String> cpfMae;
	public static volatile SingularAttribute<Aluno, Boolean> vencimentoUltimoDia;
	public static volatile SingularAttribute<Aluno, Boolean> trocaIDA3;
	public static volatile SingularAttribute<Aluno, String> naturalidadeMae;
	public static volatile ListAttribute<Aluno, AlunoCarro> alunosCarros;
	public static volatile SingularAttribute<Aluno, Integer> idaVolta;
	public static volatile ListAttribute<Aluno, Boleto> boletos;
	public static volatile SingularAttribute<Aluno, String> nomePaiAluno;
	public static volatile SingularAttribute<Aluno, String> nomeResponsavel;
	public static volatile SingularAttribute<Aluno, Boolean> trocaIDA2;
	public static volatile SingularAttribute<Aluno, Carro> carroLevaParaEscolaTroca;
	public static volatile SingularAttribute<Aluno, String> cidade;
	public static volatile SingularAttribute<Aluno, Double> valorMensal;
	public static volatile SingularAttribute<Aluno, Boolean> ativo;
	public static volatile SingularAttribute<Aluno, Aluno> irmao4;
	public static volatile SingularAttribute<Aluno, PerioddoEnum> periodo;
	public static volatile SingularAttribute<Aluno, Aluno> irmao2;
	public static volatile SingularAttribute<Aluno, Aluno> irmao3;
	public static volatile SingularAttribute<Aluno, String> emailMae;
	public static volatile SingularAttribute<Aluno, String> contatoNome5;
	public static volatile SingularAttribute<Aluno, Boolean> rematricular;
	public static volatile SingularAttribute<Aluno, String> telefoneEmpresaTrabalhaMae;
	public static volatile SingularAttribute<Aluno, String> contatoNome4;
	public static volatile SingularAttribute<Aluno, String> contatoNome3;
	public static volatile SingularAttribute<Aluno, String> login;
	public static volatile SingularAttribute<Aluno, String> cep;
	public static volatile SingularAttribute<Aluno, Date> dataCancelamento;
	public static volatile SingularAttribute<Aluno, Boolean> trocaIDA;
	public static volatile SingularAttribute<Aluno, String> telefoneCelularPai;
	public static volatile SingularAttribute<Aluno, String> telefoneResidencialPai;
	public static volatile SingularAttribute<Aluno, String> senha;
	public static volatile SingularAttribute<Aluno, String> nomeMaeResponsavel;
	public static volatile SingularAttribute<Aluno, EscolaEnum> escola;
	public static volatile SingularAttribute<Aluno, String> contatoNome2;
	public static volatile SingularAttribute<Aluno, String> nomeAvoHPaternoPai;
	public static volatile SingularAttribute<Aluno, String> contatoNome1;
	public static volatile SingularAttribute<Aluno, String> rgPai;
	public static volatile ListAttribute<Aluno, AlunoAvaliacao> avaliacoes;
	public static volatile SingularAttribute<Aluno, Member> member;
	public static volatile SingularAttribute<Aluno, Date> dataMatricula;
	public static volatile SingularAttribute<Aluno, String> nomePaiResponsavel;
	public static volatile SingularAttribute<Aluno, String> observacaoSecretaria;
	public static volatile SingularAttribute<Aluno, Double> anuidade;
	public static volatile SingularAttribute<Aluno, String> nomeAluno;
	public static volatile SingularAttribute<Aluno, String> endereco;
	public static volatile SingularAttribute<Aluno, Carro> carroPegaEscola;
	public static volatile SingularAttribute<Aluno, String> empresaTrabalhaMae;
	public static volatile SingularAttribute<Aluno, String> contatoTelefone3;
	public static volatile SingularAttribute<Aluno, String> contatoTelefone2;
	public static volatile SingularAttribute<Aluno, String> contatoTelefone1;
	public static volatile SingularAttribute<Aluno, String> nomeAvoPaternoPai;
	public static volatile SingularAttribute<Aluno, Carro> carroPegaEscolaTroca;
	public static volatile SingularAttribute<Aluno, String> naturalidadePai;
	public static volatile SingularAttribute<Aluno, String> cpfPai;
	public static volatile SingularAttribute<Aluno, Serie> serie;
	public static volatile SingularAttribute<Aluno, Aluno> irmao1;
	public static volatile SingularAttribute<Aluno, Boolean> removido;
	public static volatile SingularAttribute<Aluno, String> horaPegar;

}

