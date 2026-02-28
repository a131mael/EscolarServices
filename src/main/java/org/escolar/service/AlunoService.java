package org.escolar.service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.time.Year;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.ServletContext;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;

import org.aaf.escolar.ContratoAlunoDTO;
import org.aaf.escolar.enums.BairroEnum;
import org.aaf.escolar.enums.EscolaEnum;
import org.aaf.financeiro.model.Pagador;
import org.aaf.financeiro.sicoob.util.CNAB240_SICOOB;
import org.aaf.financeiro.util.OfficeUtil;
import org.escolar.controller.DocxToPdfConverter;
import org.escolar.enums.BimestreEnum;
import org.escolar.enums.DisciplinaEnum;
import org.escolar.enums.PegarEntregarEnun;
import org.escolar.enums.PerioddoEnum;
import org.escolar.enums.Serie;
import org.escolar.enums.StatusBoletoEnum;
import org.escolar.enums.StatusContratoEnum;
import org.escolar.model.Aluno;
import org.escolar.model.AlunoCarro;
import org.escolar.model.Boleto;
import org.escolar.model.Carro;
import org.escolar.model.ContratoAluno;
import org.escolar.model.Custo;
import org.escolar.model.Evento;
import org.escolar.model.Funcionario;
import org.escolar.model.ObjetoRota;
//import org.escolar.util.CurrencyWriter;
import org.escolar.util.ImpressoesUtils;
import org.escolar.util.Service;
import org.escolar.util.UtilFinalizarAnoLetivo;
import org.escolar.util.Verificador;

@Stateless
public class AlunoService extends Service {

	@Inject
	private EventoService eventoService;

	@Inject
	private UtilFinalizarAnoLetivo finalizarAnoLetivo;

	@Inject
	private ConfiguracaoService configuracaoService;
	
	 @Inject
	    private ServletContext context;

	@PersistenceContext(unitName = "EscolarDS")
	private EntityManager em;

	public Aluno findById(EntityManager em, Long id) {
		return em.find(Aluno.class, id);
	}

	public Boleto findBoletoById(Long id) {
		Boleto boleto = em.find(Boleto.class, id);
		return boleto;
	}

	@SuppressWarnings("unchecked")
	public List<Aluno> findAlunoDoAnoLetivo() {
		List<Aluno> alunos = new ArrayList<>();

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT al from  Aluno al ");
		sql.append("where 1=1 ");
		sql.append(" and al.removido = false ");
		sql.append(" and al.anoLetivo = ");
		sql.append(configuracaoService.getConfiguracao().getAnoLetivo());
		Query query = em.createQuery(sql.toString());

		alunos = query.getResultList();

		return alunos;
	}

	@SuppressWarnings("unchecked")
	public List<Aluno> findAlunoDoAnoLetivoComLzyContrato() {
		List<Aluno> alunos = new ArrayList<>();

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT al from  Aluno al ");
		sql.append("where 1=1 ");
		sql.append(" and al.removido = false ");
		sql.append(" and al.anoLetivo = ");
		sql.append(configuracaoService.getConfiguracao().getAnoLetivo());
		Query query = em.createQuery(sql.toString());

		alunos = query.getResultList();
		List<Aluno> alunos2 = new ArrayList<>();
		for (Aluno al : alunos) {
			if (al.getContratos() != null) {
				for (ContratoAluno c : al.getContratos()) {
					c.getId();
					c.getBoletos().size();
				}
			}
			alunos2.add(al);
		}
		return alunos2;
	}

	public ContratoAluno findContratoById(Long id) {
		ContratoAluno contrato = em.find(ContratoAluno.class, id);
		contrato.getAluno().getAnoLetivo();

		if (contrato.getAluno().getIrmao1() != null) {
			contrato.getAluno().getIrmao1().getId();
		}
		if (contrato.getAluno().getIrmao2() != null) {
			contrato.getAluno().getIrmao2().getId();
		}
		if (contrato.getAluno().getIrmao3() != null) {
			contrato.getAluno().getIrmao3().getId();
		}
		if (contrato.getAluno().getIrmao4() != null) {
			contrato.getAluno().getIrmao4().getId();
		}
		carregarLazyCarro(contrato.getAluno());
		carregarLazyCarro(contrato.getAluno().getIrmao1());
		carregarLazyCarro(contrato.getAluno().getIrmao2());
		carregarLazyCarro(contrato.getAluno().getIrmao3());
		carregarLazyCarro(contrato.getAluno().getIrmao4());

		if (contrato.getAluno().getContratos() != null) {
			contrato.getAluno().getContratos().size();
			for (ContratoAluno contratos : contrato.getAluno().getContratos()) {
				contratos.getBoletos().size();
			}
		}

		if (contrato.getBoletos() != null) {
			contrato.getBoletos().size();
		}
		return contrato;
	}

	private void carregarLazyCarro(Aluno aluno) {
		if (aluno != null) {
			if (aluno.getCarroLevaParaEscola() != null) {
				aluno.getCarroLevaParaEscola().getId();
			}

			if (aluno.getCarroLevaParaEscolaTroca() != null) {
				aluno.getCarroLevaParaEscolaTroca().getId();
			}

			if (aluno.getCarroLevaParaEscolaTroca2() != null) {
				aluno.getCarroLevaParaEscolaTroca2().getId();
			}

			if (aluno.getCarroLevaParaEscolaTroca3() != null) {
				aluno.getCarroLevaParaEscolaTroca3().getId();
			}

			if (aluno.getCarroPegaEscola() != null) {
				aluno.getCarroPegaEscola().getId();
			}

			if (aluno.getCarroPegaEscolaTroca() != null) {
				aluno.getCarroPegaEscolaTroca().getId();
			}

			if (aluno.getCarroLevaParaEscolaTroca2() != null) {
				aluno.getCarroPegaEscolaTroca2().getId();
			}

			if (aluno.getCarroPegaEscolaTroca3() != null) {
				aluno.getCarroPegaEscolaTroca3().getId();
			}
		}

	}

	public Aluno findById(Long id) {
		Aluno al = em.find(Aluno.class, id);
		if (al != null) {
			if (al.getIrmao1() != null) {
				al.getIrmao1().getAnoLetivo();
			}

			if (al.getIrmao2() != null) {
				al.getIrmao2().getAnoLetivo();
			}

			if (al.getIrmao3() != null) {
				al.getIrmao3().getAnoLetivo();
			}

			if (al.getIrmao4() != null) {
				al.getIrmao4().getAnoLetivo();
			}
			carregarLazyCarro(al);
			carregarLazyCarro(al.getIrmao1());
			carregarLazyCarro(al.getIrmao2());
			carregarLazyCarro(al.getIrmao3());
			carregarLazyCarro(al.getIrmao4());

			if (al.getBoletos() != null) {
				al.getBoletos().size();
			}

			if (al.getContratos() != null) {
				al.getContratos().size();
				for (ContratoAluno c : al.getContratos()) {
					c.getBoletos().size();
				}
			}
			
			
		}

		return al;
	}

	public Custo findHistoricoById(Long id) {
		return em.find(Custo.class, id);
	}

	public List<Aluno> findAll() {
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Aluno> criteria = cb.createQuery(Aluno.class);
			Root<Aluno> member = criteria.from(Aluno.class);
			// Swap criteria statements if you would like to try out type-safe
			// criteria queries, a new
			// feature in JPA 2.0
			// criteria.select(member).orderBy(cb.asc(member.get(Member_.name)));
			criteria.select(member).orderBy(cb.asc(member.get("nomeAluno")));
			List<Aluno> als = em.createQuery(criteria).getResultList();
			/*
			 * for(Aluno al : als){ al.getBoletos().size(); }
			 */
			for (Aluno al : als) {
				if (al.getIrmao1() != null) {
					al.getIrmao1().getId();
					for (ContratoAluno ca1 : al.getIrmao1().getContratosSux()) {
						ca1.getId();
					}
				}
				if (al.getIrmao2() != null) {
					al.getIrmao2().getId();
					for (ContratoAluno ca2 : al.getIrmao2().getContratosSux()) {
						ca2.getId();
					}
				}
				for (ContratoAluno ca : al.getContratosSux()) {
					ca.getId();
				}
			}
			return als;

		} catch (NoResultException nre) {
			return new ArrayList<>();
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}

	public List<Aluno> findAll(Map<String, Object> filtros) {
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Aluno> criteria = cb.createQuery(Aluno.class);
			Root<Aluno> member = criteria.from(Aluno.class);
			criteria.select(member).orderBy(cb.asc(member.get("nomeAluno")));

			final List<Predicate> predicates = new ArrayList<Predicate>();
			for (Map.Entry<String, Object> entry : filtros.entrySet()) {

				Predicate pred = cb.and();
				if (entry.getValue() instanceof String) {
					pred = cb.and(pred, cb.like(member.<String>get(entry.getKey()), "%" + entry.getValue() + "%"));
				} else {
					pred = cb.equal(member.get(entry.getKey()), entry.getValue());
				}
				predicates.add(pred);
				// cq.where(pred);
			}
			List<Aluno> als = em.createQuery(criteria).getResultList();
			List<Aluno> aux = new ArrayList<>();
			for (Aluno al : als) {
				al.getContratos().size();
				for (ContratoAluno ca : al.getContratos()) {
					ca.getBoletos().size();
				}
				if (temContratoAtivo(al)) {
					aux.add(al);
				}
			}
			return aux;

		} catch (NoResultException nre) {
			return new ArrayList<>();
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}

	public List<Aluno> findAll(Serie serie, PerioddoEnum periodo) {
		List<Aluno> alunos = new ArrayList<>();
		alunos.addAll(find(serie, periodo));
		if (periodo != null && periodo != PerioddoEnum.INTEGRAL) {
			alunos.addAll(find(serie, PerioddoEnum.INTEGRAL));
		} else if (periodo != null && periodo.equals(PerioddoEnum.INTEGRAL)) {
			alunos.addAll(find(serie, PerioddoEnum.MANHA));
			alunos.addAll(find(serie, PerioddoEnum.TARDE));
		}
		return alunos;
	}

	public List<Aluno> find(Serie serie, PerioddoEnum periodo) {
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Aluno> criteria = cb.createQuery(Aluno.class);
			Root<Aluno> member = criteria.from(Aluno.class);

			Predicate whereSerie = null;
			Predicate wherePeriodo = null;

			StringBuilder sb = new StringBuilder();
			if (serie != null) {
				sb.append("A");
				whereSerie = cb.equal(member.get("serie"), serie);
			}

			if (periodo != null) {
				sb.append("B");
				wherePeriodo = cb.equal(member.get("periodo"), periodo);
			}

			switch (sb.toString()) {

			case "A":
				criteria.select(member).where(whereSerie);
				break;

			case "B":
				criteria.select(member).where(wherePeriodo);
				break;

			case "AB":
				criteria.select(member).where(whereSerie, wherePeriodo);
				break;
			default:
				break;
			}

			criteria.select(member).orderBy(cb.asc(member.get("nomeAluno")));
			return em.createQuery(criteria).getResultList();

		} catch (NoResultException nre) {
			return new ArrayList<>();
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}

	@SuppressWarnings("unchecked")
	public List<Aluno> findAlunoByCarro(long idCarro) {
		List<Aluno> alunos = new ArrayList<>();

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT al from  Aluno al ");
		sql.append("where 1=1 ");
		sql.append(" and al.carroLevaParaEscola.id =   ");
		sql.append(idCarro);
		sql.append(" or al.carroLevaParaEscolaTroca.id =   ");
		sql.append(idCarro);
		sql.append(" or al.carroPegaEscola.id =   ");
		sql.append(idCarro);
		sql.append(" or al.carroPegaEscolaTroca.id =   ");
		sql.append(idCarro);
		sql.append(" and al.removido = false ");
		sql.append(" and al.anoLetivo = ");
		sql.append(configuracaoService.getConfiguracao().getAnoLetivo());

		Query query = em.createQuery(sql.toString());
		alunos = query.getResultList();
		return alunos;
	}

	@SuppressWarnings("unchecked")
	public List<Aluno> findAlunoByCarroIDA(long idCarro, PerioddoEnum periodo) {
		List<Aluno> alunos = new ArrayList<>();

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT al from  Aluno al ");
		sql.append("where 1=1 ");
		if (periodo != null) {
			sql.append(" and al.periodo =  ");
			sql.append(periodo.ordinal());
		}
		sql.append(" and (");
		sql.append(" al.carroLevaParaEscola.id =   ");
		sql.append(idCarro);
		sql.append(" ) ");
		sql.append(" and al.removido = false ");
		sql.append(" and al.anoLetivo = ");
		sql.append(configuracaoService.getConfiguracao().getAnoLetivo());
		Query query = em.createQuery(sql.toString());

		alunos = query.getResultList();

		return alunos;

	}

	@SuppressWarnings("unchecked")
	public List<Aluno> findAlunoByCarroVolta(long idCarro, PerioddoEnum periodo) {
		List<Aluno> alunos = new ArrayList<>();

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT al from  Aluno al ");
		sql.append("where 1=1 ");
		if (periodo != null) {
			sql.append(" and al.periodo =  ");
			sql.append(periodo.ordinal());
		}
		sql.append(" and (");
		sql.append(" al.carroPegaEscola.id =   ");
		sql.append(idCarro);
		sql.append(" or al.carroPegaEscolaTroca.id =   ");
		sql.append(idCarro);
		sql.append(" ) ");
		sql.append(" and al.removido = false ");
		sql.append(" and al.anoLetivo = ");
		sql.append(configuracaoService.getConfiguracao().getAnoLetivo());

		sql.append(" and (al.idaVolta = 0 or al.idaVolta = 2 )");
		Query query = em.createQuery(sql.toString());
		alunos = query.getResultList();
		return alunos;

	}

	@SuppressWarnings("unchecked")
	public List<Aluno> findAlunoTurmaBytTurma(long idTurma) {
		List<Aluno> alunos = new ArrayList<>();

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT pt from  AlunoCarro pt ");
		sql.append("where pt.carro.id =   ");
		sql.append(idTurma);

		Query query = em.createQuery(sql.toString());

		try {
			List<AlunoCarro> alunosTurmas = query.getResultList();
			for (AlunoCarro profT : alunosTurmas) {
				Aluno pro = profT.getAluno();
				alunos.add(pro);
			}

		} catch (NoResultException noResultException) {

		} catch (Exception e) {
			e.printStackTrace();
		}

		return alunos;

	}

	@SuppressWarnings("unchecked")
	public List<Aluno> findAlunoTurmaBytTurma(List<Carro> turmas) {
		List<Aluno> alunos = new ArrayList<>();

		for (Carro turma : turmas) {
			alunos.addAll(findAlunoTurmaBytTurma(turma.getId()));
		}
		return alunos;

	}

	public Aluno save(Aluno aluno, ContratoAluno contrato) {
		return saveAluno(aluno, true);
	}

	public void saveAlunoEndereco(Aluno aluno) {
		Aluno user = null;

		if (aluno.getId() != null && aluno.getId() != 0L) {
			user = findById(aluno.getId());
		}

		user.setBairroAluno(aluno.getBairroAluno());
		user.setEnderecoAluno(aluno.getEnderecoAluno());
		em.merge(user);
	}

	public void clone(Aluno aluno, Aluno user) {
		// user.setAdministrarParacetamol(aluno.isAdministrarParacetamol());
		// user.setValorMensal(aluno.getValorMensal());
		user.setDataMatricula(aluno.getDataMatricula());
		// user.setAdministrarParacetamol(aluno.isAdministrarParacetamol());
		user.setCodigo(aluno.getCodigo());

		// user.setAnuidade(aluno.getAnuidade() != null ? aluno.getAnuidade() :
		// 0);
		user.setCpfMae(aluno.getCpfMae());
		user.setCpfPai(aluno.getCpfPai());
		// user.setCpfResponsavel(aluno.getCpfResponsavel());
		// user.setRgResponsavel(aluno.getRgResponsavel());
		user.setDataMatricula(aluno.getDataMatricula());
		user.setEmailMae(aluno.getEmailMae());
		user.setEmailPai(aluno.getEmailPai());
		user.setEmpresaTrabalhaMae(aluno.getEmpresaTrabalhaMae());
		user.setEmpresaTrabalhaPai(aluno.getEmpresaTrabalhaPai());
		user.setLogin(aluno.getLogin());
		user.setNaturalidadeMae(aluno.getNaturalidadeMae());
		user.setNaturalidadePai(aluno.getNaturalidadePai());
		user.setNomeAvoHPaternoMae(aluno.getNomeAvoHPaternoMae());
		user.setNomeAvoHPaternoPai(aluno.getNomeAvoHPaternoPai());
		user.setNomeAvoPaternoMae(aluno.getNomeAvoPaternoMae());
		user.setNomeAvoPaternoPai(aluno.getNomeAvoPaternoPai());
		user.setNomeMaeAluno(aluno.getNomeMaeAluno());
		user.setNomePaiAluno(aluno.getNomePaiAluno());
		// user.setNomeResponsavel(aluno.getNomeResponsavel());
		// user.setNumeroParcelas(aluno.getNumeroParcelas());
		user.setObservacaoSecretaria(aluno.getObservacaoSecretaria());
		// user.setValorMensal(aluno.getValorMensal());
		user.setTelefoneResidencialPai(aluno.getTelefoneResidencialPai());
		user.setRgMae(aluno.getRgMae());
		user.setRgPai(aluno.getRgPai());
		user.setSenha(aluno.getSenha());
		user.setTelefone(aluno.getTelefone());
		// user.setEscola(aluno.getEscola());

		// user.setNomePaiResponsavel(aluno.getNomePaiResponsavel());
		// user.setNomeMaeResponsavel(aluno.getNomeMaeResponsavel());
		// user.setAnuidade(aluno.getAnuidade());
		// user.setBairro(aluno.getBairro());
		// user.setCep(aluno.getCep());
		// user.setCidade(aluno.getCidade());
		user.setCpfMae(aluno.getCpfMae());
		user.setCpfPai(aluno.getCpfPai());
		// user.setEndereco(aluno.getEndereco());
		// user.setRgResponsavel(aluno.getRgResponsavel());

		if (aluno.getRemovido() == null) {
			user.setRemovido(false);
		} else {
			user.setRemovido(aluno.getRemovido());
		}

	}

	public String removeCaracteresEspeciais(String texto) {
		texto = texto.replaceAll("[^aA-zZ-Z0-9 ]", "");
		return texto;
	}

	public Aluno saveAluno(Aluno aluno, boolean saveBrother) {
		Aluno user = null;
		ContratoAluno contratoPersistence;
		try {

			if (aluno.getId() != null && aluno.getId() != 0L) {
				user = findById(aluno.getId());
			} else {
				user = new Aluno();
				user.setAnoLetivo(configuracaoService.getConfiguracao().getAnoLetivo());
			}

			user.setNomeAluno(aluno.getNomeAluno().toUpperCase());
			user.setPeriodo(aluno.getPeriodo());
			user.setSerie(aluno.getSerie());

			user.setDataCancelamento(aluno.getDataCancelamento());
			user.setDataNascimento(aluno.getDataNascimento());
			user.setDataMatricula(aluno.getDataMatricula());

			if (aluno.getRemovido() == null) {
				user.setRemovido(false);
			} else {
				user.setRemovido(aluno.getRemovido());
			}
			user.setCodigo(aluno.getCodigo());
			user.setNomeAvoHPaternoMae(aluno.getNomeAvoHPaternoMae());
			user.setCpfMae(aluno.getCpfMae());
			user.setDataMatricula(aluno.getDataMatricula());
			user.setEmailMae(aluno.getEmailMae());
			user.setEmailPai(aluno.getEmailPai());
			user.setEmpresaTrabalhaMae(aluno.getEmpresaTrabalhaMae());
			user.setEmpresaTrabalhaPai(aluno.getEmpresaTrabalhaPai());
			user.setLogin(aluno.getLogin());
			user.setNaturalidadeMae(aluno.getNaturalidadeMae());
			user.setNaturalidadePai(aluno.getNaturalidadePai());
			user.setNomeAvoHPaternoMae(aluno.getNomeAvoHPaternoMae());
			user.setNomeAvoHPaternoPai(aluno.getNomeAvoHPaternoPai());
			user.setNomeAvoPaternoMae(aluno.getNomeAvoPaternoMae());
			user.setNomeAvoPaternoPai(aluno.getNomeAvoPaternoPai());
			user.setNomeMaeAluno(aluno.getNomeMaeAluno());
			user.setNomePaiAluno(aluno.getNomePaiAluno());
			user.setTelefoneCelularPai(aluno.getTelefoneCelularPai());
			user.setTelefoneEmpresaTrabalhaPai(aluno.getTelefoneEmpresaTrabalhaPai());
			user.setTelefoneResidencialPai(aluno.getTelefoneResidencialPai());
			user.setTelefoneCelularMae(aluno.getTelefoneCelularMae());
			user.setTelefoneEmpresaTrabalhaMae(aluno.getTelefoneEmpresaTrabalhaMae());
			user.setTelefoneResidencialMae(aluno.getTelefoneResidencialMae());
			user.setEmpresaTrabalhaMae(aluno.getTelefoneEmpresaTrabalhaMae());
			user.setEmpresaTrabalhaPai(aluno.getEmpresaTrabalhaPai());

			user.setTelefoneResidencialPai(aluno.getTelefoneResidencialPai());
			user.setRgMae(aluno.getRgMae());
			user.setRgPai(aluno.getRgPai());
			user.setSenha(aluno.getSenha());
			user.setTelefone(aluno.getTelefone());

			user.setObservacaoSecretaria(aluno.getObservacaoSecretaria());

			user.setRemovido(false);
			user.setAnoLetivo(configuracaoService.getConfiguracao().getAnoLetivo());
			user.setEscola(aluno.getEscola());
			
			
			if (aluno.getEnderecoAluno() != null) {
				user.setEnderecoAluno(removeCaracteresEspeciais(aluno.getEnderecoAluno()));
			}

			user.setBairroAluno(aluno.getBairroAluno());
			user.setContatoEmail1(aluno.getContatoEmail1());
			user.setContatoEmail2(aluno.getContatoEmail2());
			user.setContatoNome1(aluno.getContatoNome1());
			user.setContatoNome2(aluno.getContatoNome2());
			user.setContatoNome3(aluno.getContatoNome3());
			user.setContatoNome4(aluno.getContatoNome4());
			user.setContatoNome5(aluno.getContatoNome5());

			user.setCadastroTemporario(aluno.getCadastroTemporario());

			boolean telefoneAlterado = false;
			if (aluno.getContatoTelefone1() != null
					&& aluno.getContatoTelefone1().equalsIgnoreCase(user.getContatoTelefone1())) {
				telefoneAlterado = true;
			}
			user.setContatoTelefone1(aluno.getContatoTelefone1());

			if (aluno.getContatoTelefone2() != null
					&& aluno.getContatoTelefone2().equalsIgnoreCase(user.getContatoTelefone2())) {
				telefoneAlterado = true;
			}
			user.setContatoTelefone2(aluno.getContatoTelefone2());
			if (aluno.getContatoTelefone3() != null
					&& aluno.getContatoTelefone3().equalsIgnoreCase(user.getContatoTelefone3())) {
				telefoneAlterado = true;
			}
			user.setContatoTelefone3(aluno.getContatoTelefone3());

			if (aluno.getContatoTelefone4() != null
					&& aluno.getContatoTelefone4().equalsIgnoreCase(user.getContatoTelefone4())) {
				telefoneAlterado = true;
			}
			user.setContatoTelefone4(aluno.getContatoTelefone4());
			if (aluno.getContatoTelefone5() != null
					&& aluno.getContatoTelefone5().equalsIgnoreCase(user.getContatoTelefone5())) {
				telefoneAlterado = true;
			}
			user.setContatoTelefone5(aluno.getContatoTelefone5());

			if (aluno.getId() != null && aluno.getId() != 0L) {
				user.setJaTestousContatosWhats(false);
			} else {
				user.setJaTestousContatosWhats(!telefoneAlterado);
			}

			user.setTrocaIDA(aluno.isTrocaIDA());
			user.setTrocaVolta(aluno.isTrocaVolta());
			user.setCarroLevaParaEscola(aluno.getCarroLevaParaEscola());
			user.setCarroLevaParaEscolaTroca(aluno.getCarroLevaParaEscolaTroca());
			user.setCarroPegaEscola(aluno.getCarroPegaEscola());
			user.setCarroPegaEscolaTroca(aluno.getCarroPegaEscolaTroca());

			user.setContactado(aluno.getContactado());
			user.setDataContato(aluno.getDataContato());
			user.setQuantidadeContatos(aluno.getQuantidadeContatos());

			user.setIdaVolta(aluno.getIdaVolta());

			em.persist(user);

			if (user.getDataNascimento() != null) {
				Evento aniversario = eventoService.findByCodigo(user.getCodigo());

				if (aniversario == null) {
					aniversario = new Evento();
				}

				aniversario.setDataFim(finalizarAnoLetivo.mudarAno(user.getDataNascimento(),
						configuracaoService.getConfiguracao().getAnoLetivo()));
				aniversario.setDataInicio(finalizarAnoLetivo.mudarAno(user.getDataNascimento(),
						configuracaoService.getConfiguracao().getAnoLetivo()));
				aniversario.setCodigo(user.getCodigo());
				aniversario.setDescricao(" Aniversário do(a) aluno(a) " + user.getNomeAluno());
				aniversario.setNome(
						" Aniversário do(a) aluno(a) " + user.getNomeAluno() + " " + aluno.getSerie().getName());
				eventoService.save(aniversario);

			}

		} catch (ConstraintViolationException ce) {
			// Handle bean validation issues
			// builder = createViolationResponse(ce.getConstraintViolations());
		} catch (ValidationException e) {
			// Handle the unique constrain violation
			Map<String, String> responseObj = new HashMap<>();
			responseObj.put("email", "Email taken");

		} catch (Exception e) {
			// Handle generic exceptions
			Map<String, String> responseObj = new HashMap<>();
			responseObj.put("error", e.getMessage());

			e.printStackTrace();
		}

		if (saveBrother) {
			if (aluno.getContratos() != null)
				for (ContratoAluno cont : aluno.getContratos()) {
					if (cont.getCancelado() != null && !cont.getCancelado()) {
						em.merge(cont);
					}
				}
			if (!aluno.isRemocaoIrmao()) {
				salvarIrmaos(user, aluno);
			}
		}

		em.flush();
		return user;
	}

	private Aluno saveAluno(Aluno aluno, boolean saveBrother, ContratoAluno contrato) {
		Aluno user = null;
		ContratoAluno contratoPersistence = null;
		try {

			if (aluno.getId() != null && aluno.getId() != 0L) {
				user = findById(aluno.getId());
			} else {
				user = new Aluno();
				user.setAnoLetivo(configuracaoService.getConfiguracao().getAnoLetivo());
			}

			if (contrato.getId() != null && contrato.getId() != 0L) {
				contratoPersistence = findContratoById(contrato.getId());
			} else {
				contratoPersistence = new ContratoAluno();
			}

			user.setIdaVolta(aluno.getIdaVolta());
			// user.setAdministrarParacetamol(aluno.isAdministrarParacetamol());
			user.setNomeAluno(aluno.getNomeAluno().toUpperCase());
			user.setPeriodo(aluno.getPeriodo());
			user.setSerie(aluno.getSerie());

			if (aluno.getEnderecoAluno() != null) {
				user.setEnderecoAluno(removeCaracteresEspeciais(aluno.getEnderecoAluno()));
			}

			// user.setNacionalidade(aluno.getNacionalidade());
			user.setDataNascimento(aluno.getDataNascimento());
			user.setDataMatricula(aluno.getDataMatricula());
			// user.setAdministrarParacetamol(aluno.isAdministrarParacetamol());
			user.setCodigo(aluno.getCodigo());

			user.setCodigo(aluno.getCodigo());
			user.setCpfMae(aluno.getCpfMae());
			user.setCpfPai(aluno.getCpfPai());
			user.setDataMatricula(aluno.getDataMatricula());
			user.setEmailMae(aluno.getEmailMae());
			user.setEmailPai(aluno.getEmailPai());
			user.setEmpresaTrabalhaMae(aluno.getEmpresaTrabalhaMae());
			user.setEmpresaTrabalhaPai(aluno.getEmpresaTrabalhaPai());
			user.setLogin(aluno.getLogin());
			user.setNaturalidadeMae(aluno.getNaturalidadeMae());
			user.setNaturalidadePai(aluno.getNaturalidadePai());
			user.setNomeAvoHPaternoMae(aluno.getNomeAvoHPaternoMae());
			user.setNomeAvoHPaternoPai(aluno.getNomeAvoHPaternoPai());
			user.setNomeAvoPaternoMae(aluno.getNomeAvoPaternoMae());
			user.setNomeAvoPaternoPai(aluno.getNomeAvoPaternoPai());
			if (aluno.getNomeMaeAluno() != null) {
				user.setNomeMaeAluno(aluno.getNomeMaeAluno().toUpperCase());
			}
			if (aluno.getNomePaiAluno() != null) {
				user.setNomePaiAluno(aluno.getNomePaiAluno().toUpperCase());
			}
			user.setObservacaoSecretaria(aluno.getObservacaoSecretaria());
			// if(saveBrother){

			// }else{
			// user.setValorMensal(0);
			// }
			user.setTelefoneResidencialPai(aluno.getTelefoneResidencialPai());
			user.setRgMae(aluno.getRgMae());
			user.setRgPai(aluno.getRgPai());
			user.setSenha(aluno.getSenha());
			user.setTelefone(aluno.getTelefone());
			user.setEscola(aluno.getEscola());

			user.setBairroAluno(aluno.getBairroAluno());
			user.setContatoEmail1(aluno.getContatoEmail1());
			user.setContatoEmail2(aluno.getContatoEmail2());
			user.setContatoNome1(aluno.getContatoNome1());
			user.setContatoNome2(aluno.getContatoNome2());
			user.setContatoNome3(aluno.getContatoNome3());
			user.setContatoNome4(aluno.getContatoNome4());
			user.setContatoNome5(aluno.getContatoNome5());

			user.setContatoTelefone1(aluno.getContatoTelefone1());
			user.setContatoTelefone2(aluno.getContatoTelefone2());
			user.setContatoTelefone3(aluno.getContatoTelefone3());
			user.setContatoTelefone4(aluno.getContatoTelefone4());
			user.setContatoTelefone5(aluno.getContatoTelefone5());

			if (aluno.getRemovido() == null) {
				user.setRemovido(false);
			} else {
				user.setRemovido(aluno.getRemovido());
			}

			user.setGerarNFSe(aluno.getGerarNFSe());

			user.setTrocaIDA(aluno.isTrocaIDA());
			user.setTrocaVolta(aluno.isTrocaVolta());
			user.setCarroLevaParaEscola(aluno.getCarroLevaParaEscola());
			user.setCarroLevaParaEscolaTroca(aluno.getCarroLevaParaEscolaTroca());
			user.setCarroPegaEscola(aluno.getCarroPegaEscola());
			user.setCarroPegaEscolaTroca(aluno.getCarroPegaEscolaTroca());

			popularContrato(contrato, contratoPersistence);

			if (user.getAnoLetivo() == 0) {
				user.setAnoLetivo(configuracaoService.getConfiguracao().getAnoLetivo());
			}

			em.persist(user);

			if (user.getDataNascimento() != null) {
				Evento aniversario = eventoService.findByCodigo(user.getCodigo());

				if (aniversario == null) {
					aniversario = new Evento();
				}

				aniversario.setDataFim(finalizarAnoLetivo.mudarAno(user.getDataNascimento(),
						configuracaoService.getConfiguracao().getAnoLetivo()));
				aniversario.setDataInicio(finalizarAnoLetivo.mudarAno(user.getDataNascimento(),
						configuracaoService.getConfiguracao().getAnoLetivo()));
				aniversario.setCodigo(user.getCodigo());
				aniversario.setDescricao(" Aniversário do(a) aluno(a) " + user.getNomeAluno());
				aniversario.setNome(
						" Aniversário do(a) aluno(a) " + user.getNomeAluno() + " " + aluno.getSerie().getName());
				eventoService.save(aniversario);

			}

		} catch (ConstraintViolationException ce) {
			// Handle bean validation issues
			// builder = createViolationResponse(ce.getConstraintViolations());
		} catch (ValidationException e) {
			// Handle the unique constrain violation
			Map<String, String> responseObj = new HashMap<>();
			responseObj.put("email", "Email taken");

		} catch (Exception e) {
			// Handle generic exceptions
			Map<String, String> responseObj = new HashMap<>();
			responseObj.put("error", e.getMessage());

			e.printStackTrace();
		}

		List<Boleto> boletos = null;
		if (saveBrother) {
			salvarIrmaos(user, aluno, contratoPersistence);
			if (aluno.getId() == null || aluno.getId() == 0L) {
				boletos = gerarBoletos(user, contrato);
			}
		}
		if (aluno.getId() == null || aluno.getId() == 0L) {
			contratoPersistence.setBoletos(boletos);
		}

		em.flush();
		return user;

	}

	private void popularContrato(ContratoAluno contrato, ContratoAluno contratoPersistence) {
		if (contrato.getEndereco() != null) {
			contratoPersistence.setEndereco(removeCaracteresEspeciais(contrato.getEndereco()));
		}
		contratoPersistence.setBairro(contrato.getBairro());
		contratoPersistence.setCep(contrato.getCep());
		contratoPersistence.setCidade(contrato.getCidade());
		contratoPersistence.setValorMensal(contrato.getValorMensal());
		contratoPersistence.setAnuidade(contrato.getAnuidade() != null ? contrato.getAnuidade() : 0);
		contratoPersistence.setBairro(contrato.getBairro());
		contratoPersistence.setCep(contrato.getCep());
		contratoPersistence.setCidade(contrato.getCidade());
		if (contrato.getCpfResponsavel() != null) {
			contratoPersistence.setCpfResponsavel(contrato.getCpfResponsavel().replace(".", "").replace("-", ""));
		}
		contratoPersistence.setNumeroParcelas(contrato.getNumeroParcelas());
		contratoPersistence.setRgResponsavel(contrato.getRgResponsavel());
		if (contrato.getNomeResponsavel() != null) {
			contratoPersistence.setNomeResponsavel(contrato.getNomeResponsavel().toUpperCase());
		}
		contratoPersistence.setValorMensal(contrato.getValorMensal());
		if (contrato.getNomePaiResponsavel() != null) {
			contratoPersistence.setNomePaiResponsavel(contrato.getNomePaiResponsavel().toUpperCase());
		}
		if (contrato.getNomeMaeResponsavel() != null) {
			contratoPersistence.setNomeMaeResponsavel(contrato.getNomeMaeResponsavel().toUpperCase());
		}
		contratoPersistence.setDataCancelamento(contrato.getDataCancelamento());
		contratoPersistence.setCnabEnviado(contrato.getCnabEnviado());

	}

	public List<Boleto> gerarBoletos(Aluno user, ContratoAluno contrato) {
		return gerarBoletos(user, configuracaoService.getConfiguracao().getAnoLetivo(), contrato);
	}

	public List<Boleto> gerarBoletos(Aluno user, int ano, ContratoAluno contrato) {
		int quantidadeParcelas = 12 - contrato.getNumeroParcelas();
		return gerarBoletos(user, ano, quantidadeParcelas, contrato);
	}

	public List<Boleto> gerarBoletos(Aluno user, int ano, int quantidadeParcelas, ContratoAluno contrato) {
		List<Boleto> boletos = new ArrayList<>();
		long nossoNumero = getProximoNossoNumero();
		while (quantidadeParcelas < 12) {
			Boleto boleto = new Boleto();
			Calendar vencimento = Calendar.getInstance();
			vencimento.set(Calendar.DAY_OF_MONTH, 10);
			vencimento.set(Calendar.MONTH, quantidadeParcelas);
			vencimento.set(Calendar.YEAR, ano);
			vencimento.set(Calendar.HOUR, 0);
			vencimento.set(Calendar.MINUTE, 0);
			vencimento.set(Calendar.SECOND, 0);
			boleto.setVencimento(vencimento.getTime());
			boleto.setEmissao(new Date());
			boleto.setValorNominal(contrato.getValorMensal());
			boleto.setPagador(user);
			boleto.setNossoNumero(nossoNumero);
			boleto.setContrato(contrato);
			em.persist(boleto);
			nossoNumero++;
			boletos.add(boleto);

			quantidadeParcelas++;
		}
		em.flush();
		return boletos;
	}

	public void gerarBoletos() {
		List<Aluno> alunos = findAll();
		for (Aluno al : alunos) {
			correcaoModelagemAlunoContrato(al);
			/*
			 * for(ContratoAluno contrato :al.getContratosVigentes()){ if (al.getRemovido()
			 * != null && !al.getRemovido()) { if (contrato.getBoletos() == null ||
			 * contrato.getBoletos().size() == 0) { if (contrato.getNumeroParcelas() != null
			 * && contrato.getNumeroParcelas() > 0) { if((al.getAnoLetivo() ==
			 * configuracaoService.getConfiguracao().getAnoLetivo()) ||
			 * (al.getRematricular() != null && al.getRematricular()) ){ if
			 * (!irmaoJaTemBoleto(al)) { List<Boleto> boletos = gerarBoletos(al,contrato);
			 * contrato.setBoletos(boletos); em.persist(contrato); } } } } else if
			 * (contrato.getBoletos() != null && contrato.getBoletos().size() > 0) {
			 * List<Boleto> boletos = contrato.getBoletos();
			 * if(todosBoletosBaixados(boletos) && al.getRestaurada() != null &&
			 * al.getRestaurada()){ List<Boleto> boletosGErados = gerarBoletos(al,contrato);
			 * boletosGErados.addAll(contrato.getBoletos());
			 * contrato.setBoletos(boletosGErados); em.persist(contrato); }else{ for (Boleto
			 * b : boletos) { if(b.getAlteracaoBoletoManual() == null ||
			 * !b.getAlteracaoBoletoManual()){ b.setValorNominal(contrato.getValorMensal());
			 * Calendar c = Calendar.getInstance(); c.setTime(b.getVencimento()); if
			 * (contrato.getVencimentoUltimoDia() == null ||
			 * !contrato.getVencimentoUltimoDia()) { c.set(Calendar.DAY_OF_MONTH,
			 * contrato.getDiaVencimento()); } else { int dia =
			 * c.getActualMaximum(Calendar.DAY_OF_MONTH); c.set(Calendar.DAY_OF_MONTH, dia);
			 * } b.setVencimento(c.getTime()); em.merge(b); } } } } }
			 * //gerarBoletosRematricula(al,anoREmatricula,contratoAluno); }
			 */

		}
	}

	private List<Boleto> gerarBoletosRematricula(Aluno al, int anoRematricula, ContratoAluno contrato) {
		if (al.getRemovido() != null && !al.getRemovido()) {
			if (al.getRematricular() != null && al.getRematricular()) {
				if (!possuiBoletoNoAno(al, anoRematricula)) {
					gerarBoletos(al, anoRematricula, 0, contrato);
				}
			}
		}
		return null;
	}

	private void correcaoModelagemAlunoContrato(Aluno al) {
		if (al.getContratos() == null || al.getContratos().isEmpty()) {
			ContratoAluno contrato = new ContratoAluno();
			contrato.setAluno(al);
			contrato.setAno((short) 2018);
			contrato.setAnuidade(al.getAnuidade());
			contrato.setBairro(al.getBairro());
			contrato.setBoletos(al.getBoletos());
			contrato.setCep(al.getCep());
			contrato.setCidade(al.getCidade());
			contrato.setCnabEnviado(al.getCnabEnviado());
			contrato.setContratoTerminado(al.getContratoTerminado());
			contrato.setCpfResponsavel(al.getCpfResponsavel());
			contrato.setDataCancelamento(al.getDataCancelamento());
			contrato.setDataCriacaoContrato(al.getDataMatricula());
			contrato.setDiaVencimento(al.getDiaVencimento());
			contrato.setEndereco(al.getEndereco());
			contrato.setEnviadoParaCobrancaCDL(al.getEnviadoParaCobrancaCDL());
			contrato.setEnviadoSPC(al.getEnviadoSPC());
			contrato.setNomeMaeResponsavel(al.getNomeMaeResponsavel());
			contrato.setNomePaiResponsavel(al.getNomePaiResponsavel());
			contrato.setNomeResponsavel(al.getNomeResponsavel());

			String ano = String.valueOf(contrato.getAno());
			String finalANo = ano.substring(ano.length() - 2, ano.length());
			String numeroUltimoContrato = "01";

			String numero = finalANo + contrato.getAluno().getCodigo() + numeroUltimoContrato;
			contrato.setNumero(numero);

			contrato.setNumeroParcelas(al.getNumeroParcelas());
			contrato.setRgResponsavel(al.getRgResponsavel());
			contrato.setValorMensal(al.getValorMensal());
			contrato.setVencimentoUltimoDia(al.isVencimentoUltimoDia());
			contrato.setBoletos(findBoletoBy(al));
			em.persist(contrato);

			List<ContratoAluno> contratos = new ArrayList<>();
			contratos.add(contrato);
			al.setContratos(contratos);
			al.setBoletos(null);
			em.persist(al);
			em.flush();
		}
	}

	private List<Boleto> findBoletoBy(Aluno al) {

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT distinct(bol) from Boleto bol ");
		sql.append(" where 1 = 1");
		sql.append(" and bol.pagador.id= ");
		sql.append(al.getId());
		Query query = em.createQuery(sql.toString());
		List<Boleto> t = (List<Boleto>) query.getResultList();

		return t;
	}

	public List<Boleto> findBoletoAbertoBy(Long idcontrato) {

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT distinct(bol) from Boleto bol ");
		sql.append(" where 1 = 1");
		sql.append(" and bol.contrato.id= ");
		sql.append(idcontrato);
		sql.append(" and (bol.baixaManual is null or bol.baixaManual = false)");
		sql.append(" and (bol.cancelado is null or bol.cancelado = false)");
		sql.append(" and (bol.dividaPerdoada is null or bol.dividaPerdoada = false)");
		sql.append(" and (bol.protestado is null or bol.protestado = false)");
		sql.append(" and (bol.valorPago is null or bol.valorPago < 50)");
		sql.append(" order by bol.vencimento");
		Query query = em.createQuery(sql.toString());

		List<Boleto> t = (List<Boleto>) query.getResultList();

		return t;
	}

	public List<org.escolar.model.Boleto> findBoletosByIdAlgumContratoATivo(Long idAlgumcontrato) {

		Calendar c = Calendar.getInstance();
		c.get(Calendar.YEAR);

		ContratoAluno contrato2 = findContratoById(idAlgumcontrato);
		Aluno aluno = contrato2.getAluno();

		List<org.escolar.model.Boleto> boletosParaPagar = new ArrayList<>();
		if (aluno.getContratosVigentes() != null) {
			for (ContratoAluno contrato : aluno.getContratosVigentes()) {

				if (contrato.getAno() + 1 >= (c.get(Calendar.YEAR))) {
					if (contrato.getBoletos() != null) {
						for (org.escolar.model.Boleto b : contrato.getBoletos()) {
							if ((!Verificador.getStatusEnum(b).equals(StatusBoletoEnum.PAGO))
									&& !(Verificador.getStatusEnum(b).equals(StatusBoletoEnum.CANCELADO))) {

								boletosParaPagar.add(b);
							}
						}
					}
				}

			}
		}
		Collections.sort(boletosParaPagar, new Comparator<org.escolar.model.Boleto>() {
			@Override
			public int compare(org.escolar.model.Boleto o1, org.escolar.model.Boleto o2) {
				return o1.getVencimento().compareTo(o2.getVencimento());
			}
		});

		return boletosParaPagar;

	}

	private boolean possuiBoletoNoAno(Aluno al, int anoRematricula) {
		boolean retorno = false;
		List<Boleto> boletos = al.getBoletos();
		Calendar calendar = new GregorianCalendar();
		if (boletos != null && !boletos.isEmpty()) {
			for (Boleto boleto : boletos) {
				calendar.setTime(boleto.getVencimento());
				int anoBoleto = calendar.get(Calendar.YEAR);
				if (anoBoleto == anoRematricula) {
					retorno = true;
				}
			}
		} else {
			retorno = false;
		}
		return retorno;
	}

	private boolean todosBoletosBaixados(List<Boleto> boletos) {
		boolean todosBaixados = true;
		for (Boleto bol : boletos) {
			if (!(bol.getBaixaGerada() != null && bol.getBaixaGerada())) {
				if (!(bol.getBaixaManual() != null && bol.getBaixaManual())) {
					if (!(bol.getConciliacaoPorExtrato() != null && bol.getConciliacaoPorExtrato())) {
						if (!(bol.getValorPago() != null && bol.getValorPago() > 0)) {
							todosBaixados = false;
						}
					}
				}
			}
		}
		return todosBaixados;
	}

	private boolean irmaoJaTemBoleto(Aluno aluno) {
		Aluno irmao1 = aluno.getIrmao1();
		Aluno irmao2 = aluno.getIrmao2();
		Aluno irmao3 = aluno.getIrmao3();
		Aluno irmao4 = aluno.getIrmao4();

		if (irmao1 != null) {
			if (irmao1.getBoletos() != null && irmao1.getBoletos().size() > 0) {
				return true;
			}
		}

		if (irmao2 != null) {
			if (irmao2.getBoletos() != null && irmao2.getBoletos().size() > 0) {
				return true;
			}
		}

		if (irmao3 != null) {
			if (irmao3.getBoletos() != null && irmao3.getBoletos().size() > 0) {
				return true;
			}
		}

		if (irmao4 != null) {
			if (irmao4.getBoletos() != null && irmao4.getBoletos().size() > 0) {
				return true;
			}
		}

		return false;
	}

	public List<Boleto> gerarBoletos(Aluno user, ContratoAluno contrato, boolean setUser) {
		contrato = findContratoById(contrato.getId());
		List<Boleto> boletos = gerarBoletos(user, contrato);
		contrato.setBoletos(boletos);
		em.persist(contrato);
		em.flush();
		return boletos;
	}

	private void salvarIrmaos(Aluno aluno, Aluno unMerge) {
		Aluno irmao1 = unMerge.getIrmao1();
		boolean tem1Irmao = irmao1 != null ? true : false;
		Aluno irmao2 = unMerge.getIrmao2();
		boolean tem2Irmao = irmao2 != null ? true : false;
		Aluno irmao3 = unMerge.getIrmao3();
		boolean tem3Irmao = irmao3 != null ? true : false;
		Aluno irmao4 = unMerge.getIrmao4();
		boolean tem4Irmao = irmao4 != null ? true : false;

		if (tem1Irmao) {
			clone(aluno, irmao1);
			irmao1 = saveAluno(irmao1, false);

			aluno.setIrmao1(irmao1);
			irmao1.setIrmao1(aluno);

			if (tem2Irmao) {
				clone(aluno, irmao2);
				irmao2 = saveAluno(irmao2, false);
				aluno.setIrmao2(irmao2);
				irmao1.setIrmao1(aluno);
				irmao1.setIrmao2(irmao2);
				irmao2.setIrmao1(aluno);
				irmao2.setIrmao2(irmao1);
			}
			if (tem3Irmao) {
				clone(aluno, irmao3);
				irmao3 = saveAluno(irmao3, false);
				aluno.setIrmao3(irmao3);
				irmao3.setIrmao1(aluno);
				irmao3.setIrmao2(irmao1);
				irmao3.setIrmao3(irmao2);

				irmao1.setIrmao1(aluno);
				irmao1.setIrmao2(irmao2);
				irmao1.setIrmao3(irmao3);
				irmao2.setIrmao1(aluno);
				irmao2.setIrmao2(irmao1);
				irmao2.setIrmao3(irmao3);
			}
			if (tem4Irmao) {
				clone(aluno, irmao4);
				irmao4 = saveAluno(irmao4, false);
				aluno.setIrmao4(irmao4);
				irmao4.setIrmao1(aluno);
				irmao4.setIrmao2(irmao1);
				irmao4.setIrmao3(irmao2);
				irmao4.setIrmao4(irmao3);

				irmao1.setIrmao1(aluno);
				irmao1.setIrmao2(irmao2);
				irmao1.setIrmao3(irmao3);
				irmao1.setIrmao4(irmao4);
				irmao2.setIrmao1(aluno);
				irmao2.setIrmao2(irmao1);
				irmao2.setIrmao3(irmao3);
				irmao2.setIrmao4(irmao4);

				irmao3.setIrmao1(aluno);
				irmao3.setIrmao2(irmao1);
				irmao3.setIrmao3(irmao2);
				irmao3.setIrmao4(irmao4);
			}
		}

		if (tem2Irmao) {
			clone(aluno, irmao2);
			irmao2 = saveAluno(irmao2, false);
			aluno.setIrmao2(irmao2);
			irmao2.setIrmao1(aluno);

			if (tem3Irmao) {
				clone(aluno, irmao3);
				irmao3 = saveAluno(irmao3, false);
				aluno.setIrmao3(irmao3);
				irmao3.setIrmao1(aluno);
				irmao3.setIrmao3(irmao2);

				irmao2.setIrmao1(aluno);
				irmao2.setIrmao3(irmao3);
			}
			if (tem4Irmao) {
				clone(aluno, irmao4);
				irmao4 = saveAluno(irmao4, false);
				aluno.setIrmao4(irmao4);
				irmao4.setIrmao1(aluno);
				irmao4.setIrmao3(irmao2);
				irmao4.setIrmao4(irmao3);

				irmao2.setIrmao1(aluno);
				irmao2.setIrmao3(irmao3);
				irmao2.setIrmao4(irmao4);

				irmao3.setIrmao1(aluno);
				irmao3.setIrmao3(irmao2);
				irmao3.setIrmao4(irmao4);
			}

		}
		if (tem3Irmao) {
			clone(aluno, irmao3);
			irmao3 = saveAluno(irmao3, false);
			aluno.setIrmao3(irmao3);
			irmao3.setIrmao1(aluno);

			if (tem4Irmao) {
				clone(aluno, irmao4);
				irmao4 = saveAluno(irmao4, false);
				aluno.setIrmao4(irmao4);
				irmao4.setIrmao1(aluno);
				irmao4.setIrmao4(irmao3);
				irmao3.setIrmao1(aluno);
				irmao3.setIrmao4(irmao4);
			}
		}

		if (tem4Irmao) {
			clone(aluno, irmao4);
			irmao4 = saveAluno(irmao4, false);
			aluno.setIrmao4(irmao4);
			irmao4.setIrmao1(aluno);
		}
	}

	private void salvarIrmaos(Aluno aluno, Aluno unMerge, ContratoAluno contrato) {
		Aluno irmao1 = unMerge.getIrmao1();
		boolean tem1Irmao = irmao1 != null ? true : false;
		Aluno irmao2 = unMerge.getIrmao2();
		boolean tem2Irmao = irmao2 != null ? true : false;
		Aluno irmao3 = unMerge.getIrmao3();
		boolean tem3Irmao = irmao3 != null ? true : false;
		Aluno irmao4 = unMerge.getIrmao4();
		boolean tem4Irmao = irmao4 != null ? true : false;

		if (tem1Irmao) {
			clone(aluno, irmao1);
			irmao1 = saveAluno(irmao1, false, contrato);

			aluno.setIrmao1(irmao1);
			irmao1.setIrmao1(aluno);

			if (tem2Irmao) {
				clone(aluno, irmao2);
				irmao2 = saveAluno(irmao2, false, contrato);
				aluno.setIrmao2(irmao2);
				irmao1.setIrmao1(aluno);
				irmao1.setIrmao2(irmao2);
				irmao2.setIrmao1(aluno);
				irmao2.setIrmao2(irmao1);
			}
			if (tem3Irmao) {
				clone(aluno, irmao3);
				irmao3 = saveAluno(irmao3, false, contrato);
				aluno.setIrmao3(irmao3);
				irmao3.setIrmao1(aluno);
				irmao3.setIrmao2(irmao1);
				irmao3.setIrmao3(irmao2);

				irmao1.setIrmao1(aluno);
				irmao1.setIrmao2(irmao2);
				irmao1.setIrmao3(irmao3);
				irmao2.setIrmao1(aluno);
				irmao2.setIrmao2(irmao1);
				irmao2.setIrmao3(irmao3);
			}
			if (tem4Irmao) {
				clone(aluno, irmao4);
				irmao4 = saveAluno(irmao4, false, contrato);
				aluno.setIrmao4(irmao4);
				irmao4.setIrmao1(aluno);
				irmao4.setIrmao2(irmao1);
				irmao4.setIrmao3(irmao2);
				irmao4.setIrmao4(irmao3);

				irmao1.setIrmao1(aluno);
				irmao1.setIrmao2(irmao2);
				irmao1.setIrmao3(irmao3);
				irmao1.setIrmao4(irmao4);
				irmao2.setIrmao1(aluno);
				irmao2.setIrmao2(irmao1);
				irmao2.setIrmao3(irmao3);
				irmao2.setIrmao4(irmao4);

				irmao3.setIrmao1(aluno);
				irmao3.setIrmao2(irmao1);
				irmao3.setIrmao3(irmao2);
				irmao3.setIrmao4(irmao4);
			}
		}

		if (tem2Irmao) {
			clone(aluno, irmao2);
			irmao2 = saveAluno(irmao2, false, contrato);
			aluno.setIrmao2(irmao2);
			irmao2.setIrmao1(aluno);

			if (tem3Irmao) {
				clone(aluno, irmao3);
				irmao3 = saveAluno(irmao3, false, contrato);
				aluno.setIrmao3(irmao3);
				irmao3.setIrmao1(aluno);
				irmao3.setIrmao3(irmao2);

				irmao2.setIrmao1(aluno);
				irmao2.setIrmao3(irmao3);
			}
			if (tem4Irmao) {
				clone(aluno, irmao4);
				irmao4 = saveAluno(irmao4, false, contrato);
				aluno.setIrmao4(irmao4);
				irmao4.setIrmao1(aluno);
				irmao4.setIrmao3(irmao2);
				irmao4.setIrmao4(irmao3);

				irmao2.setIrmao1(aluno);
				irmao2.setIrmao3(irmao3);
				irmao2.setIrmao4(irmao4);

				irmao3.setIrmao1(aluno);
				irmao3.setIrmao3(irmao2);
				irmao3.setIrmao4(irmao4);
			}

		}
		if (tem3Irmao) {
			clone(aluno, irmao3);
			irmao3 = saveAluno(irmao3, false, contrato);
			aluno.setIrmao3(irmao3);
			irmao3.setIrmao1(aluno);

			if (tem4Irmao) {
				clone(aluno, irmao4);
				irmao4 = saveAluno(irmao4, false, contrato);
				aluno.setIrmao4(irmao4);
				irmao4.setIrmao1(aluno);
				irmao4.setIrmao4(irmao3);
				irmao3.setIrmao1(aluno);
				irmao3.setIrmao4(irmao4);
			}
		}

		if (tem4Irmao) {
			clone(aluno, irmao4);
			irmao4 = saveAluno(irmao4, false, contrato);
			aluno.setIrmao4(irmao4);
			irmao4.setIrmao1(aluno);
		}
	}

	public String remover(Long idAluno) {
		Aluno al = findById(idAluno);
		al.setRemovido(true);
		al.getContratoVigente().setDataCancelamento(new Date());
		em.persist(al);
		em.flush();
		return "ok";
	}

	public void setNfsEnviada(Long idBoleto) {
		Boleto bol = findBoletoById(idBoleto);
		bol.setNfsEnviada(true);
		em.flush();
		em.persist(bol);
	}

	public String restaurar(Long idAluno) {
		Aluno al = findById(idAluno);
		al.setRemovido(false);
		al.setRestaurada(true);
		al.getContratoVigente().setCnabEnviado(false);
		em.persist(al);
		em.flush();
		return "ok";
	}

	public void saveAlunoTurma(List<Aluno> target, Carro turma) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT at from  AlunoCarro at ");
		sql.append("where at.turma.id =   ");
		sql.append(turma.getId());

		Query query = em.createQuery(sql.toString());

		try {
			List<AlunoCarro> alunosTurmas = query.getResultList();
			for (AlunoCarro profT : alunosTurmas) {
				em.remove(profT);
				em.flush();
			}

			for (Aluno prof : target) {
				AlunoCarro pt = new AlunoCarro();
				pt.setAnoLetivo(configuracaoService.getConfiguracao().getAnoLetivo());
				pt.setAluno(prof);
				pt.setCarro(em.find(Carro.class, turma.getId()));
				em.persist(pt);
				em.flush();
			}

		} catch (NoResultException noResultException) {

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public Funcionario getProfessor(Long idAluno) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT at.turma from  AlunoCarro at ");
		sql.append("where 1 = 1");
		sql.append("and  at.aluno.id = ");
		sql.append(idAluno);
		Query query = em.createQuery(sql.toString());
		Carro t = (Carro) query.getResultList().get(0);

		StringBuilder sql2 = new StringBuilder();
		sql2.append("SELECT pt.professor from  FuncionarioCarro pt ");
		sql2.append("where 1 = 1");
		sql2.append("and  pt.turma.id = ");
		sql2.append(t.getId());
		sql2.append("and  pt.professor.especialidade = 0");
		Query query2 = em.createQuery(sql2.toString());
		return (Funcionario) query2.getResultList().get(0);

	}

	public float getNota(Long idAluno, DisciplinaEnum disciplina, BimestreEnum bimestre, boolean recupecacao) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT avg(av.nota) from  AlunoAvaliacao av ");
		sql.append("where 1 = 1");
		sql.append(" and  av.aluno.id = ");
		sql.append(idAluno);
		sql.append(" and  av.avaliacao.disciplina = ");
		sql.append(disciplina.ordinal());
		if (bimestre != null) {
			sql.append(" and  av.avaliacao.bimestre = ");
			sql.append(bimestre.ordinal());
		}
		sql.append(" and  av.avaliacao.recuperacao = ");
		sql.append(recupecacao);
		Query query = em.createQuery(sql.toString());

		Object valor = query.getSingleResult();
		if (valor != null) {
			valor = (double) valor;
		} else {
			return 0;
		}

		return Float.parseFloat(String.valueOf(valor));
	}

	public float getNota(Long idAluno, DisciplinaEnum disciplina, boolean recuperacao, boolean provafinal) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT avg(av.nota) from  AlunoAvaliacao av ");
		sql.append("where 1 = 1");
		sql.append(" and  av.aluno.id = ");
		sql.append(idAluno);
		sql.append(" and  av.avaliacao.disciplina = ");
		sql.append(disciplina.ordinal());
		sql.append(" and  av.avaliacao.provaFinal = ");
		sql.append(provafinal);
		sql.append(" and  av.avaliacao.recuperacao = ");
		sql.append(recuperacao);
		Query query = em.createQuery(sql.toString());

		Object valor = query.getSingleResult();
		if (valor != null) {
			valor = (double) valor;
		} else {
			return 0;
		}

		return Float.parseFloat(String.valueOf(valor));
	}

	public void saveAlunoRota(ObjetoRota objetoRota) {
		ObjetoRota obj;

		if (objetoRota.getId() != null && objetoRota.getId() != 0L) {
			obj = em.find(ObjetoRota.class, objetoRota.getId());
		} else {
			obj = new ObjetoRota();
		}
		if (objetoRota.getAluno() != null) {
			obj.setAluno(findById(objetoRota.getAluno().getId()));
		}
		obj.setNome(objetoRota.getNome());
		obj.setEscola(objetoRota.getEscola());
		obj.setPosicao(objetoRota.getPosicao() - 1);
		obj.setQuantidadeAlunos(objetoRota.getQuantidadeAlunos());
		obj.setPegarEntregar(objetoRota.getPegarEntregar());

		obj.setIdCarro(objetoRota.getIdCarro());
		obj.setPeriodo(objetoRota.getPeriodo());
		obj.setEscola(objetoRota.getEscola());
		obj.setDescricao(objetoRota.getDescricao());
		obj.setIdCarroAlvo(objetoRota.getIdCarroAlvo());
		atualizarIndices(obj);
		if (obj.getId() == null) {
			em.persist(obj);
		} else {
			em.merge(obj);
		}
		em.flush();
	}

	private void atualizarIndices(ObjetoRota obj) {
		List<ObjetoRota> objs = getObjetosRotasSeguintes(obj);
		if (objs != null && !objs.isEmpty() && objs.get(0) != null && objs.get(0).getPosicao() == obj.getPosicao()) {
			int indice = 0;
			for (ObjetoRota ob : objs) {
				ob.setPosicao(obj.getPosicao() + indice);
				indice++;
				em.merge(ob);
			}
		}
		em.flush();
	}

	private List<ObjetoRota> getObjetosRotasSeguintes(ObjetoRota obj) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT obj from ObjetoRota obj ");
		sql.append("where 1 = 1 ");
		sql.append("and obj.posicao>=");
		sql.append(obj.getPosicao());
		sql.append(" and ");
		sql.append(" obj.periodo = ");
		sql.append(obj.getPeriodo().ordinal());
		sql.append(" and ");
		sql.append(" obj.idCarro = ");
		sql.append(obj.getIdCarro());
		sql.append(" order by obj.posicao ");
		Query query = em.createQuery(sql.toString());
		List<ObjetoRota> t = new ArrayList<>();
		try {
			t = (List<ObjetoRota>) query.getResultList();

		} catch (Exception e) {
			e.printStackTrace();
			return t;
		}

		return t;

	}

	@SuppressWarnings("unchecked")
	public List<Aluno> find(int first, int size, String orderBy, String order, Map<String, Object> filtros) {
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Aluno> criteria = cb.createQuery(Aluno.class);
			Root<Aluno> member = criteria.from(Aluno.class);
			CriteriaQuery cq = criteria.select(member);

			final List<Predicate> predicates = new ArrayList<Predicate>();
			for (Map.Entry<String, Object> entry : filtros.entrySet()) {

				Predicate pred = cb.and();
				if (entry.getValue() instanceof String) {
					pred = cb.and(pred, cb.like(member.<String>get(entry.getKey()), "%" + entry.getValue() + "%"));
				} else {
					pred = cb.equal(member.get(entry.getKey()), entry.getValue());
				}
				predicates.add(pred);
				// cq.where(pred);
			}

			cq.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
			cq.orderBy((order.equals("asc") ? cb.asc(member.get(orderBy)) : cb.desc(member.get(orderBy))));
			Query q = em.createQuery(criteria);
			q.setFirstResult(first);
			q.setMaxResults(size);
			List<Aluno> alunos = (List<Aluno>) q.getResultList();
			for (Aluno al : alunos) {
				carregarLazyCarro(al);
				carregarLazyContrato(al);

			}
			return alunos;

		} catch (NoResultException nre) {
			return new ArrayList<>();
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}

	private void carregarLazyContrato(Aluno al) {
		if (al.getContratosSux() != null) {
			al.getContratosSux().size();
			for (ContratoAluno c : al.getContratosSux()) {
				c.getBoletos().size();
			}
		}
	}

	public long count(Map<String, Object> filtros) {
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
			Root<Aluno> member = countQuery.from(Aluno.class);
			countQuery.select(cb.count(member));

			final List<Predicate> predicates = new ArrayList<Predicate>();
			if (filtros != null) {
				for (Map.Entry<String, Object> entry : filtros.entrySet()) {

					Predicate pred = cb.and();
					if (entry.getValue() instanceof String) {
						pred = cb.and(pred, cb.like(member.<String>get(entry.getKey()), "%" + entry.getValue() + "%"));
					} else {
						pred = cb.equal(member.get(entry.getKey()), entry.getValue());
					}
					predicates.add(pred);

				}
				countQuery.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
			}

			Query q = em.createQuery(countQuery);
			return (long) q.getSingleResult();

		} catch (NoResultException nre) {
			return 0;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	/*
	 * public long sum(Map<String, Object> filtros) { try { CriteriaBuilder cb =
	 * em.getCriteriaBuilder(); CriteriaQuery<Double> countQuery =
	 * cb.createQuery(Double.class); Root<Aluno> member =
	 * countQuery.from(Aluno.class);
	 * countQuery.select(cb.sumAsDouble(member.get("valorMensal")));
	 * 
	 * final List<Predicate> predicates = new ArrayList<Predicate>(); if (filtros !=
	 * null) { for (Map.Entry<String, Object> entry : filtros.entrySet()) {
	 * 
	 * Predicate pred = cb.and(); if (entry.getValue() instanceof String) { pred =
	 * cb.and(pred, cb.like(member.<String> get(entry.getKey()), "%" +
	 * entry.getValue() + "%")); } else { pred =
	 * cb.equal(member.get(entry.getKey()), entry.getValue()); }
	 * predicates.add(pred);
	 * 
	 * } countQuery.where(cb.and(predicates.toArray(new
	 * Predicate[predicates.size()]))); }
	 * 
	 * Query q = em.createQuery(countQuery); return (long) q.getSingleResult();
	 * 
	 * } catch (NoResultException nre) { return 0; } catch (Exception e) {
	 * e.printStackTrace(); return 0; } }
	 */

	public List<Custo> getHistoricoAluno(Aluno aluno) {
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Custo> criteria = cb.createQuery(Custo.class);
			Root<Custo> member = criteria.from(Custo.class);
			CriteriaQuery cq = criteria.select(member);

			Predicate pred = cb.and();
			pred = cb.equal(member.get("aluno"), aluno);
			cq.where(pred);

			cq.orderBy(cb.desc(member.get("ano")));
			Query q = em.createQuery(criteria);
			return (List<Custo>) q.getResultList();

		} catch (NoResultException nre) {
			return new ArrayList<>();
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}

	public void removerHistorico(long idHistorico) {
		em.remove(findHistoricoById(idHistorico));
	}

	public boolean estaEmUmaTUrma(long idAluno) {
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
			Root<AlunoCarro> member = countQuery.from(AlunoCarro.class);
			countQuery.select(cb.count(member));

			Predicate pred = cb.and();
			pred = cb.equal(member.get("aluno").get("id"), idAluno);
			countQuery.where(pred);

			Query q = em.createQuery(countQuery);
			return ((long) q.getSingleResult()) > 0 ? true : false;

		} catch (NoResultException nre) {
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	public List<ObjetoRota> findObjetosRota(List<Aluno> alunos) {
		// TODO aqui ta loco
		List<ObjetoRota> ors = new ArrayList<>();
		try {

			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ObjetoRota> criteria = cb.createQuery(ObjetoRota.class);
			Root<ObjetoRota> member = criteria.from(ObjetoRota.class);
			CriteriaQuery cq = criteria.select(member);

			final List<Predicate> predicates = new ArrayList<Predicate>();

			for (Aluno al : alunos) {
				Predicate pred = cb.or();

				pred = cb.equal(member.get("aluno").get("id"), al.getId());
				predicates.add(pred);

			}

			try {
				cq.where(cb.or(predicates.toArray(new Predicate[predicates.size()])));
				cq.orderBy(cb.asc(member.get("posicao")));
				Query q = em.createQuery(criteria);
				ors.addAll((List<ObjetoRota>) q.getResultList());
			} catch (Exception e) {
			}

		} catch (NoResultException nre) {
			nre.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ors;
	}

	public List<ObjetoRota> findObjetosRota(Long idCarro, PerioddoEnum periodo) {
		List<ObjetoRota> ors = new ArrayList<>();
		try {

			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ObjetoRota> criteria = cb.createQuery(ObjetoRota.class);
			Root<ObjetoRota> member = criteria.from(ObjetoRota.class);
			CriteriaQuery cq = criteria.select(member);

			final List<Predicate> predicates = new ArrayList<Predicate>();

			Predicate pred = cb.equal(member.get("idCarro"), idCarro);
			predicates.add(pred);

			Predicate pred2 = cb.equal(member.get("periodo"), periodo.ordinal());
			predicates.add(pred2);

			try {
				cq.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
				cq.orderBy(cb.asc(member.get("posicao")));
				Query q = em.createQuery(criteria);
				ors.addAll((List<ObjetoRota>) q.getResultList());
			} catch (Exception e) {
			}

		} catch (NoResultException nre) {
			nre.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ors;
	}

	public ObjetoRota findObjetosRota(Long idCarro, PerioddoEnum periodo, Long idCarroAlvo,
			PegarEntregarEnun pegarEntregarEnun) {
		try {

			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ObjetoRota> criteria = cb.createQuery(ObjetoRota.class);
			Root<ObjetoRota> member = criteria.from(ObjetoRota.class);
			CriteriaQuery cq = criteria.select(member);

			final List<Predicate> predicates = new ArrayList<Predicate>();

			Predicate pred = cb.equal(member.get("idCarro"), idCarro);
			predicates.add(pred);

			Predicate pred2 = cb.equal(member.get("periodo"), periodo.ordinal());
			predicates.add(pred2);

			Predicate pred3 = cb.equal(member.get("idCarroAlvo"), idCarroAlvo);
			predicates.add(pred3);

			Predicate pred4 = cb.equal(member.get("pegarEntregar"), pegarEntregarEnun.ordinal());
			predicates.add(pred4);

			try {
				cq.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
				cq.orderBy(cb.asc(member.get("posicao")));
				Query q = em.createQuery(criteria);
				return (ObjetoRota) q.getSingleResult();
			} catch (Exception e) {
			}

		} catch (NoResultException nre) {
			nre.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<Aluno> findAlunos(List<ObjetoRota> objRts) {
		List<Aluno> ors = new ArrayList<>();
		try {

			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ObjetoRota> criteria = cb.createQuery(ObjetoRota.class);
			Root<ObjetoRota> member = criteria.from(ObjetoRota.class);
			CriteriaQuery cq = criteria.select(member);

			Predicate pred = cb.and();

			Query q = em.createQuery(criteria);
			for (ObjetoRota al : objRts) {
				pred = cb.equal(member.get("aluno").get("id"), al.getAluno().getId());
				cq.where(pred);
			}
			try {
				List<ObjetoRota> objR = (List<ObjetoRota>) q.getResultList();
				for (ObjetoRota ob : objR) {
					ors.add(ob.getAluno());
				}
			} catch (Exception e) {
			}

		} catch (NoResultException nre) {
			nre.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ors;
	}

	public List<Aluno> findAlunos(Long idCarro, PerioddoEnum periodo, EscolaEnum escola,
			PegarEntregarEnun pegarEntregar) {
		List<Aluno> ors = new ArrayList<>();
		try {
			final List<Predicate> predicates = new ArrayList<Predicate>();

			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ObjetoRota> criteria = cb.createQuery(ObjetoRota.class);
			Root<ObjetoRota> member = criteria.from(ObjetoRota.class);
			CriteriaQuery cq = criteria.select(member);

			Predicate pred = cb.equal(member.get("idCarro"), idCarro);
			predicates.add(pred);

			Predicate pred2 = cb.equal(member.get("periodo"), periodo.ordinal());
			predicates.add(pred2);

			Predicate pred3 = cb.equal(member.get("aluno").get("escola"), escola.ordinal());
			predicates.add(pred3);

			Predicate pred4 = cb.equal(member.get("pegarEntregar"), pegarEntregar);
			predicates.add(pred4);

			cq.where(pred);
			try {
				cq.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
				Query q = em.createQuery(criteria);

				List<ObjetoRota> objR = (List<ObjetoRota>) q.getResultList();
				for (ObjetoRota ob : objR) {
					ors.add(ob.getAluno());
				}
			} catch (Exception e) {
			}

		} catch (NoResultException nre) {
			nre.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ors;
	}

	public void removerAlunoRota(Long idAluno) {
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ObjetoRota> criteria = cb.createQuery(ObjetoRota.class);
			Root<ObjetoRota> member = criteria.from(ObjetoRota.class);
			CriteriaQuery cq = criteria.select(member);

			Predicate pred = cb.and();
			pred = cb.equal(member.get("aluno").get("id"), idAluno);
			cq.where(pred);

			Query q = em.createQuery(criteria);
			List<ObjetoRota> objRotas = (List<ObjetoRota>) q.getResultList();

			for (ObjetoRota obj : objRotas) {
				em.remove(obj);
			}
			em.flush();

		} catch (NoResultException nre) {
			nre.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void removerObjetoRota(Long idAluno) {
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ObjetoRota> criteria = cb.createQuery(ObjetoRota.class);
			Root<ObjetoRota> member = criteria.from(ObjetoRota.class);
			CriteriaQuery cq = criteria.select(member);

			Predicate pred = cb.and();
			pred = cb.equal(member.get("id"), idAluno);
			cq.where(pred);

			Query q = em.createQuery(criteria);
			List<ObjetoRota> objRotas = (List<ObjetoRota>) q.getResultList();

			for (ObjetoRota obj : objRotas) {
				em.remove(obj);
			}

			em.flush();
		} catch (NoResultException nre) {
			nre.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public List<Carro> findCarrosTroca(Carro carro, PerioddoEnum periodo, Boolean pego) {
		String sql = "";
		if (periodo.equals(PerioddoEnum.MANHA)) {
			if (pego) {
				sql = SQLs.getSQLCarrosTrocaManhaPego();
			} else {
				sql = SQLs.getSQLCarrosTrocaManha();
			}
		} else {
			if (pego) {
				sql = SQLs.getSQLCarrosTrocaTardePego();
			} else {
				sql = SQLs.getSQLCarrosTrocaTarde();
			}
		}

		sql = sql.replace("?1", carro.getId().toString());
		sql = sql.replace("?2", String.valueOf(periodo.ordinal()));

		Query query = em.createQuery(sql);
		List<Carro> t = new ArrayList<>();
		try {
			t = (List<Carro>) query.getResultList();

		} catch (Exception e) {
			e.printStackTrace();
			return t;
		}

		return t;
	}

	public List<Carro> findCarrosTrocaMeioDia(Carro carro, PerioddoEnum periodo, Boolean pego) {
		String sql = SQLs.getSQLTrocaMeioDia();
		sql = sql.replace("?1", carro.getId().toString());
		sql = sql.replace("?2", String.valueOf(periodo.TARDE.ordinal()));
		Query query = em.createQuery(sql.toString());
		List<Carro> t = new ArrayList<>();
		try {
			t = (List<Carro>) query.getResultList();

		} catch (Exception e) {
			e.printStackTrace();
			return t;
		}
		return t;
	}

	public List<Aluno> findAlunosVoltam(Carro carro, Carro carroTroca, PerioddoEnum periodo) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT distinct(al)  from Aluno al ");
		sql.append(" where 1 = 1");
		sql.append(" and al.carroPegaEscola.id= ");
		sql.append(carro.getId());
		sql.append(" and al.carroPegaEscolaTroca.id= ");
		sql.append(carroTroca.getId());
		sql.append(" and");
		sql.append(" ( al.periodo = ");
		sql.append(periodo.ordinal());
		sql.append(" or al.periodo = ");
		sql.append(PerioddoEnum.INTEGRAL.ordinal());
		sql.append(" )");
		Query query = em.createQuery(sql.toString());
		List<Aluno> t = (List<Aluno>) query.getResultList();

		return t;
	}

	public List<Aluno> findAlunosVao(Carro carro, Carro carroTroca, PerioddoEnum periodo, Boolean pego) {
		String sql = "";
		if (pego) {
			sql = SQLs.getSQLAlunosVaoPego();
		} else {
			sql = SQLs.getSQLAlunosVao();
		}

		sql = sql.replace("?1", carro.getId().toString());
		sql = sql.replace("?2", carroTroca.getId().toString());
		sql = sql.replace("?3", String.valueOf(periodo.ordinal()));

		Query query = em.createQuery(sql.toString());
		List<Aluno> t = (List<Aluno>) query.getResultList();

		return t;
	}

	public List<Aluno> findAlunosTrocaMeioDia(Carro carro, Carro carroTroca, PerioddoEnum periodo) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT distinct(al)  from Aluno al ");
		sql.append(" where 1 = 1");
		sql.append(" and al.carroPegaEscola.id= ");
		sql.append(carro.getId());
		sql.append(" and al.carroPegaEscolaTroca.id= ");
		sql.append(carroTroca.getId());
		sql.append(" and");
		sql.append("  al.periodo = ");
		sql.append(PerioddoEnum.MANHA.ordinal());
		sql.append(" or (");

		sql.append(" al.carroLevaParaEscola.id= ");
		sql.append(carroTroca.getId());
		sql.append(" and al.carroLevaParaEscolaTroca.id= ");
		sql.append(carro.getId());
		sql.append(" and");
		sql.append("  al.periodo = ");
		sql.append(PerioddoEnum.TARDE.ordinal());

		sql.append(" )");

		Query query = em.createQuery(sql.toString());
		List<Aluno> t = (List<Aluno>) query.getResultList();

		return t;
	}

	public List<Aluno> findAlunoPegoOutroCarroTarde(PerioddoEnum periodo, Carro carro) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT distinct(al)  from Aluno al ");
		sql.append(" where 1 = 1");
		sql.append(" and al.trocaVolta= ");
		sql.append(true);
		sql.append(" and al.carroPegaEscolaTroca.id= ");
		sql.append(carro.getId());
		sql.append(" and");
		sql.append(" ( al.periodo = ");
		sql.append(periodo.ordinal());
		sql.append(" or al.periodo = ");
		sql.append(PerioddoEnum.INTEGRAL.ordinal());
		sql.append(" )");
		sql.append(" and ( al.idaVolta = 0 or al.idaVolta = 2)");

		Query query = em.createQuery(sql.toString());
		List<Aluno> t = (List<Aluno>) query.getResultList();

		return t;
	}

	public List<Aluno> findAlunoPegaEscolaTarde(EscolaEnum escola, Carro carro) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT distinct(al)  from Aluno al ");
		sql.append(" where 1 = 1");
		sql.append(" and al.escola= ");
		sql.append(escola.ordinal());
		sql.append(" and al.carroPegaEscola.id= ");
		sql.append(carro.getId());
		sql.append(" and ( al.idaVolta = 0 or al.idaVolta = 2)");
		sql.append(" and");
		sql.append(" ( al.periodo = ");
		sql.append(PerioddoEnum.TARDE.ordinal());
		sql.append(" or al.periodo = ");
		sql.append(PerioddoEnum.INTEGRAL.ordinal());
		sql.append(" )");

		Query query = em.createQuery(sql.toString());
		List<Aluno> t = (List<Aluno>) query.getResultList();

		return t;
	}

	public List<Aluno> findAlunoPegaEscolaManha(EscolaEnum escola, Carro carro) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT distinct(al)  from Aluno al ");
		sql.append(" where 1 = 1");
		sql.append(" and al.escola= ");
		sql.append(escola.ordinal());
		sql.append(" and ( (al.carroLevaParaEscola.id= ");
		sql.append(carro.getId());
		sql.append(" and al.trocaIDA = false)");
		sql.append(" or al.carroLevaParaEscolaTroca.id= ");
		sql.append(carro.getId());
		sql.append("  ) and ( al.idaVolta = 0 or al.idaVolta = 2)");
		sql.append(" and");
		sql.append(" ( al.periodo = ");
		sql.append(PerioddoEnum.MANHA.ordinal());
		sql.append(" or al.periodo = ");
		sql.append(PerioddoEnum.INTEGRAL.ordinal());
		sql.append(" )");

		Query query = em.createQuery(sql.toString());
		List<Aluno> t = (List<Aluno>) query.getResultList();

		return t;
	}

	public List<Aluno> findAlunoPegaEscolaMeioDia(EscolaEnum escola, Carro carro) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT distinct(al)  from Aluno al ");
		sql.append(" where 1 = 1");
		sql.append(" and al.escola= ");
		sql.append(escola.ordinal());
		sql.append(" and al.carroPegaEscola.id= ");
		sql.append(carro.getId());

		Query query = em.createQuery(sql.toString());
		List<Aluno> t = (List<Aluno>) query.getResultList();

		return t;
	}

	public List<Aluno> findAlunoLevaEscolaMeioDia(EscolaEnum escola, Carro carro) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT distinct(al)  from Aluno al ");
		sql.append(" where 1 = 1");
		sql.append(" and al.escola= ");
		sql.append(escola.ordinal());
		sql.append(" and ( (al.carroLevaParaEscola.id= ");
		sql.append(carro.getId());
		sql.append(" and al.trocaIDA = false)");
		sql.append(" or al.carroLevaParaEscolaTroca.id= ");
		sql.append(carro.getId());
		sql.append("  ) and ( al.idaVolta = 0 or al.idaVolta = 1)");
		sql.append(" and");
		sql.append("  al.periodo = ");
		sql.append(PerioddoEnum.TARDE.ordinal());

		Query query = em.createQuery(sql.toString());
		List<Aluno> t = (List<Aluno>) query.getResultList();
		return t;
	}

	public void rematricularCancelado(Long id) {
		Aluno rematriculado = findById(id);
		rematriculado.setAnoLetivo(configuracaoService.getConfiguracao().getAnoLetivo());
		rematriculado.setRestaurada(true);
		rematriculado.setRemovido(false);
		rematriculado.getContratoVigente().setCnabEnviado(false);
		em.merge(rematriculado);
		em.flush();
	}

	public List<Aluno> findAlunoTrocaNoite(Long idCarro, Long idCarroTroca) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT distinct(al)  from Aluno al ");
		sql.append(" where 1 = 1");
		sql.append(" and al.carroPegaEscola.id= ");
		sql.append(idCarro);
		sql.append(" and al.trocaVolta = true)");
		sql.append("  and al.carroPegaEscolaTroca.id");
		sql.append(idCarroTroca);

		Query query = em.createQuery(sql.toString());
		List<Aluno> t = (List<Aluno>) query.getResultList();

		return t;
	}

	public Long getProximoCodigo() {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT max(al.codigo)  from Aluno al ");

		Query query = em.createQuery(sql.toString());
		String codigo = (String) query.getSingleResult();
		if (codigo == null) {
			return 1l;
		}
		return Long.parseLong(codigo) + 1;
	}

	public Long getProximoNossoNumero() {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT max(bol.nossoNumero) from Boleto bol ");

		Query query = em.createQuery(sql.toString());
		Long codigo = null;
		try {
			codigo = (Long) query.getSingleResult();

		} catch (Exception e) {
			codigo = 10000L;
		}
		if (codigo == null) {
			codigo = 10000L;
		}
		if (codigo < 10000) {
			codigo = 10000L;
		}

		return codigo + 1;
	}

	public List<Aluno> findAluno(String nome, String nomeResponsavel, String cpf, String numeroDocumento) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT distinct(cont) from  Boleto bol ");
		sql.append("left join bol.contrato cont ");

		sql.append("where 1=2 ");
		if (nome != null && !nome.equalsIgnoreCase("")) {
			sql.append(" or cont.aluno.nomeAluno like '%");
			sql.append(nome);
			sql.append("%' ");
		} else {

		}
		if (nomeResponsavel != null && !nomeResponsavel.equalsIgnoreCase("")) {
			sql.append(" or cont.nomeResponsavel like '%");
			sql.append(nomeResponsavel);
			sql.append("%' ");
		}

		if (cpf != null && !cpf.equalsIgnoreCase("")) {
			sql.append(" or cont.cpfResponsavel like '%");
			sql.append(cpf);
			sql.append("%' ");
		}

		if (numeroDocumento != null && !numeroDocumento.equalsIgnoreCase("")) {
			sql.append(" or bol.nossoNumero = ");
			sql.append(numeroDocumento);
		}

		Query query = em.createQuery(sql.toString());

		@SuppressWarnings("unchecked")
		List<ContratoAluno> cas = query.getResultList();
		List<Aluno> alunos = new ArrayList<>();
		for (ContratoAluno ca : cas) {
			ca.getId();
			ca.getBoletos().size();
			for (Boleto b : ca.getBoletos()) {
				b.getId();
			}
			for (ContratoAluno casac : ca.getAluno().getContratos()) {
				casac.getId();
				casac.getBoletos().size();
			}
			ca.getAluno();
			ca.getAluno().setNomeResponsavel(ca.getNomeResponsavel());
			ca.getAluno().setCpfResponsavel(ca.getCpfResponsavel());
			alunos.add(ca.getAluno());

		}

		return alunos;
	}

	public void removerCnabEnviado(Long id) {
		Aluno aluno = findById(id);
		aluno.getContratoVigente().setCnabEnviado(false);
		em.merge(aluno);
		em.flush();
	}

	public void enviarCDL(ContratoAluno contrato) {
		ContratoAluno contratoa = findContratoById(contrato.getId());
		contratoa.setEnviadoSPC(true);
		contratoa.setComentario(contrato.getComentario());
		em.merge(contratoa);
		em.flush();
	}

	public void enviarConfirmadoWebService(ContratoAluno contrato) {
		ContratoAluno contratoa = findContratoById(contrato.getId());
		contratoa.setConfirmadoEnvioPorWebService(true);
		em.merge(contratoa);
		em.flush();
	}

	public void saveComentarioContrato(ContratoAluno contrato) {
		ContratoAluno contratoa = findContratoById(contrato.getId());
		contratoa.setComentarioWebService(contrato.getComentarioWebService());
		em.merge(contratoa);
		em.flush();
	}

	public ContratoAluno saveComentario(ContratoAluno contrato) {
		ContratoAluno c = new ContratoAluno();
		if (contrato.getId() != null) {
			c = findContratoById(contrato.getId());
		}
		c.setComentario(contrato.getComentario());
		em.merge(c);
		em.flush();
		return c;
	}
	
	public void saveArquivoContrato(ContratoAluno contrato) {
		ContratoAluno contratoa = findContratoById(contrato.getId());
		contratoa.setContratoScaneado(contrato.getContratoScaneado());
		em.merge(contratoa);
		em.flush();
	}

	public void enviarCnab(Long id) {
		Aluno aluno = findById(id);
		aluno.getContratoVigente().setCnabEnviado(true);
		em.merge(aluno);
		em.flush();
	}

	public void removerVerificadoOk(Long id) {
		Aluno aluno = findById(id);
		aluno.setVerificadoOk(false);
		em.merge(aluno);
		em.flush();
	}

	public void verificadoOk(Long id) {
		Aluno aluno = findById(id);
		aluno.setVerificadoOk(true);
		em.merge(aluno);
		em.flush();
	}

	public void removerBoleto(Long idBoleto) {
		Boleto b = findBoletoById(idBoleto);
		b.setManterAposRemovido(false);
		b.setDataPagamento(new Date());
		;
		b.setValorPago((double) 0);
		em.merge(b);
		em.flush();
	}

	public void manterBoleto(Long idBoleto) {
		Boleto b = findBoletoById(idBoleto);

		b.setManterAposRemovido(true);
		b.setCancelado(false);
		em.merge(b);
		em.flush();
	}

	public Aluno findAlunoSemEndereco() {
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Aluno> criteria = cb.createQuery(Aluno.class);
			Root<Aluno> member = criteria.from(Aluno.class);

			Predicate whereBairroNulo = cb.isNull(member.get("bairroAluno"));
			Predicate whereRemovido = cb.equal(member.get("removido"), false);
			;
			Predicate whereAnoLetivo = cb.equal(member.get("anoLetivo"),
					configuracaoService.getConfiguracao().getAnoLetivo());
			criteria.select(member).where(whereBairroNulo, whereRemovido, whereAnoLetivo);

			criteria.select(member).orderBy(cb.asc(member.get("codigo")));
			return em.createQuery(criteria).getResultList().get(0);

		} catch (NoResultException nre) {
			return new Aluno();
		} catch (Exception e) {
			e.printStackTrace();
			return new Aluno();
		}
	}

	public Aluno adicionarContrato(Aluno aluno, ContratoAluno novoContrato) {
		if (aluno.getAnoLetivo() == 0) {
			aluno.setAnoLetivo(configuracaoService.getConfiguracao().getAnoLetivo());
		}
		if (aluno.getId() != null) {
			aluno = findById(aluno.getId());
		} else {
			saveAluno(aluno, true);
		}
		novoContrato.setAluno(aluno);
		em.persist(novoContrato);
		List<ContratoAluno> contratos = aluno.getContratos();
		if (contratos == null) {
			contratos = new ArrayList<>();
		}
		contratos.add(novoContrato);
		aluno.setContratos(contratos);
		aluno.setStatusContrato(StatusContratoEnum.ACEITO_CONTRATO_ENVIADO);
		em.merge(aluno);
		em.flush();
		return aluno;

	}

	public Aluno findAlunoBairro(int indice) {
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Aluno> criteria = cb.createQuery(Aluno.class);
			Root<Aluno> member = criteria.from(Aluno.class);

			Predicate whereBairro = cb.equal(member.get("bairroAluno"), BairroEnum.PACHECOS);
			Predicate whereRemovido = cb.equal(member.get("removido"), false);
			;
			Predicate whereAnoLetivo = cb.equal(member.get("anoLetivo"),
					configuracaoService.getConfiguracao().getAnoLetivo());
			criteria.select(member).where(whereBairro, whereRemovido, whereAnoLetivo);

			criteria.select(member).orderBy(cb.asc(member.get("codigo")));
			return em.createQuery(criteria).getResultList().get(indice);

		} catch (NoResultException nre) {
			return new Aluno();
		} catch (Exception e) {
			e.printStackTrace();
			return new Aluno();
		}
	}

	public ContratoAluno criarBoletos(Aluno aluno, short ano, Integer numeroParcelas, ContratoAluno contrato) {
		int numPa = 12 - contrato.getNumeroParcelas();
		contrato = findContratoById(contrato.getId());
		em.persist(contrato);
		List<Boleto> boletos = this.gerarBoletos(aluno, ano, numPa, contrato);
		contrato.setBoletos(boletos);
		em.merge(contrato);
		em.flush();
		return contrato;
	}

	public void remover(Aluno aluno) {
		for (Boleto b : aluno.getBoletos()) {
			if (b.getManterAposRemovido() != null && b.getManterAposRemovido()) {
				manterBoleto(b.getId());
			} else {
				removerBoleto(b.getId());
			}
		}
		em.flush();
		remover(aluno.getId());
	}

	public ContratoAluno saveContrato(ContratoAluno contrato) {
		ContratoAluno c = new ContratoAluno();
		if (contrato.getId() != null) {
			c = findContratoById(contrato.getId());
		}
		c.setAno(contrato.getAno());
		c.setAnuidade(contrato.getAnuidade());
		c.setBairro(contrato.getBairro());
		c.setCancelado(contrato.getCancelado());
		c.setCep(contrato.getCep());
		c.setCidade(contrato.getCidade());
		c.setCnabEnviado(contrato.getCnabEnviado());
		c.setContratoTerminado(contrato.getContratoTerminado());
		c.setCpfResponsavel(contrato.getCpfResponsavel());
		c.setDataCancelamento(contrato.getDataCancelamento());
		c.setDataCriacaoContrato(contrato.getDataCriacaoContrato());
		c.setDiaVencimento(contrato.getDiaVencimento());
		c.setVencimentoUltimoDia(contrato.getVencimentoUltimoDia());
		c.setValorMensal(contrato.getValorMensal());
		c.setRgResponsavel(contrato.getRgResponsavel());
		c.setNumeroParcelas(contrato.getNumeroParcelas());
		c.setNomeResponsavel(contrato.getNomeResponsavel());
		c.setNomePaiResponsavel(contrato.getNomePaiResponsavel());
		c.setNomeMaeResponsavel(contrato.getNomeMaeResponsavel());
		c.setEnviadoSPC(contrato.getEnviadoSPC());
		c.setEnviadoParaCobrancaCDL(contrato.getEnviadoSPC());
		c.setEndereco(contrato.getEndereco());

		
		c.setComentario(contrato.getComentario());
		c.setAcordo(contrato.getAcordo());
		c.setTipoBoleto(contrato.getTipoBoleto());
		
		String ano = String.valueOf(contrato.getAno());
		String finalANo = ano.substring(ano.length() - 2, ano.length());
		String numeroUltimoContrato = "01";
		int numeroNovo = 1;

		if (contrato.getAluno().getContratos() != null) {
			for (ContratoAluno contratt : contrato.getAluno().getContratos()) {
				if (contratt.getNumero() != null && !contratt.getNumero().equalsIgnoreCase("")) {
					if (contratt.getAno() == contrato.getAno()) {
						if (contrato.getId() != null && contrato.getId() != contratt.getId()) {
							String numeroContratt = contratt.getNumero();
							numeroContratt = numeroContratt.substring(numeroContratt.length() - 2,
									numeroContratt.length());
							if (Integer.parseInt(numeroContratt) > Integer.parseInt(numeroUltimoContrato)) {
								numeroUltimoContrato = numeroContratt;
							}
							numeroNovo = Integer.parseInt(numeroUltimoContrato);
							numeroNovo++;
						}

					}

					numeroUltimoContrato = String.valueOf(numeroNovo);
				}
			}
		}

		String numero = finalANo + contrato.getAluno().getCodigo() + "0" + numeroUltimoContrato;
		c.setNumero(numero);

		em.merge(c);
		em.flush();
		return c;
	}

	public void removerContrato(ContratoAluno contrat) {
		for (Boleto b : contrat.getBoletos()) {
			if (b.getManterAposRemovido() != null && b.getManterAposRemovido()) {
				manterBoleto(b.getId());
			} else {
				removerBoleto(b.getId());
			}
		}
		Aluno al = findById(contrat.getAluno().getId());
		ContratoAluno contratoPersistence = findContratoById(contrat.getId());
		contratoPersistence.setDataCancelamento(new Date());
		contratoPersistence.setCancelado(true);
		contratoPersistence.setAluno(al);
		contratoPersistence.setBoletos(contrat.getBoletos());
		try {
			em.merge(contratoPersistence);
		} catch (Exception e) {
			e.printStackTrace();
			em.persist(contratoPersistence);
		}
		em.flush();
	}

	public void cancelarAlunosSemContratoAtivo() {
		for (Aluno al : findAll()) {
			if (!temContratoAtivo(al)) {
				if (al.getIrmao1() != null && temContratoAtivo(al.getIrmao1())) {
				} else if (al.getIrmao2() != null && temContratoAtivo(al.getIrmao2())) {
				} else {
					cancelar(al);
				}
			}
		}
	}

	public boolean temContratoAtivo(Aluno al) {
		boolean ativo = false;
		if (al.getContratosSux() != null) {
			for (ContratoAluno ca : al.getContratosSux()) {

				if (ca.getCancelado() == null || !ca.getCancelado()) {
					if (Verificador.possuiBoletoAberto(ca)) {
						ativo = true;
					}
				}
			}
		}
		return ativo;
	}

	private void cancelar(Aluno al) {
		Aluno ap = findById(al.getId());
		if (ap.getRemovido() == null || !ap.getRemovido()) {
			ap.setDataCancelamento(new Date());
			ap.setRemovido(true);
			em.merge(ap);
			em.flush();
		}
	}

	public void saveContactado(Aluno al) {
		Aluno ap = findById(al.getId());

		ap.setDataContato(al.getDataContato());
		ap.setContactado(al.getContactado());
		ap.setQuantidadeContatos(al.getQuantidadeContatos());
		ap.setObservacaoAtrasado(al.getObservacaoAtrasado());
		ap.setDataPrometeuPagar(al.getDataPrometeuPagar());
		em.merge(ap);
		em.flush();
	}

	public void saveContado(Aluno al) {
		Aluno ap = findById(al.getId());
		ap.setJaTestousContatosWhats(al.isJaTestousContatosWhats());
		ap.setContato1WhatsValido(al.isContato1WhatsValido());
		ap.setContato2WhatsValido(al.isContato2WhatsValido());
		ap.setContato3WhatsValido(al.isContato3WhatsValido());
		ap.setContato4WhatsValido(al.isContato4WhatsValido());
		ap.setContato5WhatsValido(al.isContato5WhatsValido());

		em.merge(ap);
		em.flush();
	}

	public void saveStatusCntrato(Aluno al) {
		Aluno ap = findById(al.getId());
		ap.setStatusContrato(al.getStatusContrato());

		em.merge(ap);
		em.flush();
	}

	public void saveNumeroCasa(ContratoAluno contrato) {
		ContratoAluno ap = findContratoById(contrato.getId());
		ap.setEnderecoNumero(contrato.getEnderecoNumero());
		ap.setEndereco(contrato.getEndereco());

		ap.setBairro(contrato.getBairro());
		ap.setCidade(contrato.getCidade());

		em.merge(ap);
		em.flush();
	}

	public void saveCPF(ContratoAluno contrato) {
		ContratoAluno ap = findContratoById(contrato.getId());
		ap.setCpfResponsavel(contrato.getCpfResponsavel());
		em.merge(ap);
		em.flush();
	}

	public void colocarAlunosNaListaDeCobranca() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 15);
		Date diaAtualMais15 = calendar.getTime();

		boolean contactado = true;
		for (Aluno al : findAll()) {
			contactado = true;

			if ((al.getDataContato() != null && al.getDataContato().after(diaAtualMais15))
					|| (al.getDataPrometeuPagar() != null && al.getDataPrometeuPagar().after(new Date()))) {
				if (al.getContactado() != null && al.getContactado()) {
					contactado = false;
				}
			}
			if (!contactado) {
				desconectado(al);
			}
		}
	}

	private void desconectado(Aluno al) {
		Aluno ap = findById(al.getId());
		ap.setContactado(false);
		em.merge(ap);
		em.flush();
	}

	public String enviarBoletoEmail(long idContrato, int mesBoletoInt, String email) {
		mesBoletoInt += 1;
		Boleto bol = getBoletoMe(mesBoletoInt, idContrato);
		byte[] anexoPDF = byteArrayPDFBoleto(getBoletoFinanceiro(bol), bol.getPagador(), bol.getContrato());
		String corpoEmail = "<!DOCTYPE html><html><body><p><h2><center>Transporte Escolar Favo de Mel.</center></h2><center>"
				+ "<a href=\"https://ibb.co/YkYrnBd\"><img src=\"https://i.ibb.co/1qhSyGX/logo-tefamel-512-X512.png\" "
				+ "alt=\"logo\" border=\"0\" style=\"width:192px;height:192px;border:0;\" ></a><br/><br/></center>Prezado(a) #nomeResponsavel,"
				+ "<br/><p><br/><br/>Você esta recebendo o seu boleto do transporte escolar Favo de Mel referente ao mês de <b><font size=\"2\" color=\"blue\"> #mesBoleto</font>"
				+ "</b> .<h3><center><font size=\"3\" color=\"blue\">Resumo da conta</font></center>"
				+ "</h3>Vencimento  :<font size=\"3\" color=\"blue\"> #vencimentoBoleto</font>"
				+ "<br/>Valor       :<font size=\"3\" color=\"blue\"> #valorAtualBoleto</font><br/><br/><br/><br/><center><h4>"
				+ "<font size=\"3\" color=\"red\"> Caso já tenha efetuado o pagamento favor desconsiderar esse e-mail. </font></h4></center></p><br/>"
				+ "<a href=\"href=https://ibb.co/BsLmfg1\"><img src=\"https://i.ibb.co/s3jhgFB/assinatura-Tefamel.png\" alt=\"assinatura_Email\" border=\"0\" "
				+ "style=\"width:365px;height:126px;border:0;\"></a></body></html>";
		corpoEmail = corpoEmail.replace("#vencimentoBoleto",
				org.escolar.util.Formatador.formataData(bol.getVencimento()));
		corpoEmail = corpoEmail.replace("#valorAtualBoleto",
				org.escolar.util.Formatador.valorFormatado(Verificador.getValorFinal(bol)));
		corpoEmail = corpoEmail.replace("#nomeResponsavel", bol.getContrato().getNomeResponsavel());
		corpoEmail = corpoEmail.replace("#mesBoleto", org.escolar.util.Formatador.getMes(bol.getVencimento()));

		ByteArrayInputStream bais = new ByteArrayInputStream(anexoPDF);
		org.aaf.financeiro.util.EnviadorEmail.enviarEmail("Boleto - Tefamel", corpoEmail, bais, email,
				org.escolar.rotinasAutomaticas.CONSTANTES.emailFinanceiro,
				org.escolar.rotinasAutomaticas.CONSTANTES.senhaEmailFinanceiro);
		return null;
	}

	public byte[] byteArrayPDFBoleto(org.aaf.financeiro.model.Boleto boleto, Aluno aluno, ContratoAluno contrato) {
		Calendar c = Calendar.getInstance();
		c.setTime(boleto.getVencimento());
		CNAB240_SICOOB cnab = new CNAB240_SICOOB(1);

		Pagador pagador = new Pagador();
		pagador.setBairro(contrato.getBairro());
		pagador.setCep(contrato.getCep());
		pagador.setCidade(contrato.getCidade() != null ? contrato.getCidade() : "PALHOCA");
		pagador.setCpfCNPJ(contrato.getCpfResponsavel());
		pagador.setEndereco(contrato.getEndereco());
		pagador.setNome(contrato.getNomeResponsavel());
		pagador.setNossoNumero(boleto.getNossoNumero() + "");
		pagador.setUF("SC");
		List<org.aaf.financeiro.model.Boleto> boletos = new ArrayList<>();
		boletos.add(boleto);
		pagador.setBoletos(boletos);

		byte[] pdf = cnab.getBoletoPDF(pagador);

		return pdf;
	}

	public byte[] byteArrayPDFBoleto(Long idBOleto) {
		Boleto b = findBoletoById(idBOleto);
		org.aaf.financeiro.model.Boleto boletofinanceiro = getBoletoFinanceiro(b);

		byte[] bytes = byteArrayPDFBoleto(boletofinanceiro, b.getContrato());
		return bytes;
	}

	public byte[] byteArrayPDFContrato(Long idcontrato) {
		ContratoAluno contrato2 = findContratoById(idcontrato);
		Aluno aluno = contrato2.getAluno();
		ContratoAluno contrato = aluno.getUltimoContratoComBoletoEMaiorValor();

		byte[] bytes = byteArrayPDFContrato(contrato);
		return bytes;
	}
	
	
	
	private byte[] byteArrayPDFDeclaracaoIR(Aluno aluno) {
		try {
			
			DocxToPdfConverter conversorPDF = new DocxToPdfConverter();
			String localContradoDOCX = imprimirNegativoDebito(aluno, context.getRealPath("/"));
			String localContradoPDF = localContradoDOCX.replaceAll(".docx", ".pdf");

			conversorPDF.convertDocxToPdf(localContradoDOCX, localContradoPDF);
			
			return conversorPDF.convertPDFToBytes(localContradoPDF);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	private byte[] byteArrayPDFContrato(ContratoAluno contrato) {
		try {
			
			
			DocxToPdfConverter conversorPDF = new DocxToPdfConverter();
			String localContradoDOCX = imprimirContrato2(contrato, context.getRealPath("/"));
			String localContradoPDF = localContradoDOCX.replaceAll(".docx", ".pdf");

			conversorPDF.convertDocxToPdf(localContradoDOCX, localContradoPDF);
			
			return conversorPDF.convertPDFToBytes(localContradoPDF);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}
	
	

//	public InputStream imprimirContrato(ContratoAluno contrato) throws IOException {
//		String localArquivoGerado = imprimirContrato2(contrato);
//		InputStream stream = new FileInputStream(localArquivoGerado);
//
//		return stream;
//	}

	public String imprimirContrato2(ContratoAluno contrato, String path) throws IOException {
		String nomeArquivo = "";
		if (contrato != null && contrato.getId() != null) {
			nomeArquivo = contrato.getAluno().getId() + "g";
			ImpressoesUtils.imprimirInformacoesAluno(path + File.separator	+ "MODELO1-2.docx", montarContrato(contrato), path + File.separator	+ nomeArquivo);
			nomeArquivo += ".docx";
		} else {
			nomeArquivo = "modeloContrato2017.docx";
		}

		String caminho = path + File.separator	+ nomeArquivo;
		return caminho;
	}
	
	public String imprimirNegativoDebito(Aluno aluno, String path) throws IOException {
		String nomeArquivo = "";
		if (aluno != null && aluno.getId() != null) {
			nomeArquivo = aluno.getId() + "f";
			ImpressoesUtils.imprimirInformacoesAluno(aluno, path + File.separator	+"modeloNegativoDebito2017.docx",	montarAtestadoNegativoDebito(aluno), path + File.separator	+nomeArquivo);

			nomeArquivo += ".docx";
		} else {
			nomeArquivo = "modeloNegativoDebito2017.docx";
		}

		String caminho = path + File.separator	+ nomeArquivo;
		return caminho;
	}
	
	public HashMap<String, String> montarAtestadoNegativoDebito(Aluno aluno) {
		DateFormat formatador = DateFormat.getDateInstance(DateFormat.FULL, new Locale("pt", "BR"));
		String dataExtenso = formatador.format(new Date());

		HashMap<String, String> trocas = new HashMap<>();
		trocas.put("adonainomealuno", aluno.getNomeAluno());
		trocas.put("adonaiturma", aluno.getSerie().getName());
		trocas.put("adonaiperiodo", aluno.getPeriodo().getName());
		trocas.put("adonaidata", dataExtenso);
		trocas.put("adonaicpfresponsavel", aluno.getContratoVigente().getCpfResponsavel());
		trocas.put("adonainomeresponsavel", aluno.getContratoVigente().getNomeResponsavel());

		trocas.put("adonaiano", configuracaoService.getConfiguracao().getAnoLetivo()+"");
		
		trocas.put("adonaiparcelas", aluno.getContratoVigente().getNumeroParcelas()+"");
		
		trocas.put("adonaivalorparcelas", aluno.getContratoVigente().getValorMensal()+"");
		
		trocas.put("adonaianuidade", getTotalPago(configuracaoService.getConfiguracao().getAnoLetivo(), aluno.getContratoVigente()));
		
		return trocas;
	}
	
	private String getTotalPago(int ano, ContratoAluno contrato) {
		String total = (contrato.getNumeroParcelas() * contrato.getValorMensal())+"";
		
		return total;
	}
	
	
	public HashMap<String, String> montarContrato(ContratoAluno contrato) {
		
	//	CurrencyWriter cw = new CurrencyWriter();
		DateFormat formatador = DateFormat.getDateInstance(DateFormat.FULL, new Locale("pt", "BR"));
		String dataExtenso = formatador.format(new Date());
		Calendar dataLim = Calendar.getInstance();
		dataLim.add(Calendar.MONTH, 1);

		DateFormat fomatadorData = DateFormat.getDateInstance(DateFormat.DEFAULT, new Locale("pt", "BR"));
		String aniversario = "";

		HashMap<String, String> trocas = new HashMap<>();
		if (contrato.getAluno().getRematricular() != null && contrato.getAluno().getRematricular()) {
			trocas.put("#ANOCONTRATO", configuracaoService.getConfiguracao().getAnoRematricula() + "");
		} else {
			trocas.put("#ANOCONTRATO", contrato.getAno() + "");
		}
		trocas.put("#CONTRATANTECID", "Palhoça"); // TODO COLOCAR CIDADE DO
													// Contratado
		trocas.put("#DATAEXTENSO", dataExtenso);

		trocas.put("#CONTRATANTENOME", contrato.getNomeResponsavel());
		trocas.put("#CONTRATANTERG", contrato.getRgResponsavel());
		trocas.put("#CONTRATANTECPF", contrato.getCpfResponsavel());

		if (contrato.getAluno().getEnderecoAluno() != null
				&& !contrato.getAluno().getEnderecoAluno().equalsIgnoreCase("")) {
			trocas.put("transalru", contrato.getAluno().getEnderecoAluno());
		} else {
			trocas.put("transalru", contrato.getEndereco() + ", " + contrato.getBairro());
		}

		trocas.put("#CONTRATANTERUA", contrato.getEndereco() + ", " + contrato.getBairro());

		String nomeAluno = contrato.getAluno().getNomeAluno();
		if (contrato.getAluno().getIrmao1() != null) {
			nomeAluno += ", " + contrato.getAluno().getIrmao1().getNomeAluno();
		}
		if (contrato.getAluno().getIrmao2() != null) {
			nomeAluno += ", " + contrato.getAluno().getIrmao2().getNomeAluno();
		}
		if (contrato.getAluno().getIrmao3() != null) {
			nomeAluno += ", " + contrato.getAluno().getIrmao3().getNomeAluno();
		}
		if (contrato.getAluno().getIrmao4() != null) {
			nomeAluno += ", " + contrato.getAluno().getIrmao4().getNomeAluno();
		}

		trocas.put("#TRANSPORTADONOME", nomeAluno);
		trocas.put("#nomecrianca", nomeAluno);
		trocas.put("#TRANSPORTADORUA", contrato.getEndereco() + ", " + contrato.getBairro());
		trocas.put("#TRANSPORTADOESCOLA", contrato.getAluno().getEscola().getName());
		trocas.put("#escola", contrato.getAluno().getEscola().getName());

		trocas.put("#nascimento", aniversario);
		trocas.put("#CONTRATANTERUA", contrato.getEndereco() + ", " + contrato.getBairro());

		String periodo1 = "";
		if (contrato.getAluno().getPeriodo().equals(PerioddoEnum.INTEGRAL)
				|| contrato.getAluno().getPeriodo().equals(PerioddoEnum.MANHA)) {
			periodo1 = "06:30";
		} else {
			periodo1 = "11:30";
		}
		String periodo2 = "";
		if (contrato.getAluno().getPeriodo().equals(PerioddoEnum.INTEGRAL)
				|| contrato.getAluno().getPeriodo().equals(PerioddoEnum.TARDE)) {
			periodo2 = "19:30";
		} else {
			periodo2 = "13:30";
		}

		trocas.put("#DADOSGERAISHORARIO1", periodo1);
		trocas.put("#DADOSGERAISHORARIO2", periodo2);
		trocas.put("#DADOSGERAISMES1", getMesInicioPagamento(contrato.getAluno(), contrato));
		trocas.put("#DADOSGERAISMES2", "Dezembro");
		trocas.put("#DADOSGERAISPARCELAS", contrato.getNumeroParcelas() + "");
		// BigDecimal valorTotal = (new
		// BigDecimal(contrato.getValorTotal())).multiply(((new
		// BigDecimal(contrato.getParcelas()))));
		// trocas.put("#DADOSGERAISTOTAL", valorTotal.toString());
		trocas.put("#DADOSGERAISTOTAL", String.valueOf(valorTotal(contrato.getAluno()))); // TODO
		// ver
		// contrato.setValorTotal(contrato.getValorTotal().replace(",", "."));
		//trocas.put("#DADOSGERAISTOTALEXTENSO", cw.write(new BigDecimal(valorTotal(contrato.getAluno()))));
		trocas.put("#DADOSGERAISQTADEPARCELAS", contrato.getNumeroParcelas() + "");
		//trocas.put("parexten", cw.write(new BigDecimal(contrato.getValorMensal())));
		trocas.put("#DADOSGERAISPARCELA", contrato.getValorMensal() + "");/// valor
																			/// da
																			/// parcela

		String idaEVolta = "CLAUSULA 6ª – O CONTRATANTE compromete-se a deixar o TRANSPORTADO pronto e aguardando pelo CONTRATADO no endereço e hora combinada, ou seja, na rua  #CONTRATANTERUA   as #DADOSGERAISHORARIO1,  não tolerando qualquer tipo de atraso ou mudança de endereço.";
		idaEVolta = idaEVolta.replace("#DADOSGERAISHORARIO1", periodo1);
		idaEVolta = idaEVolta.replace("#CONTRATANTERUA", contrato.getEndereco());

		String ida = "CLAUSULA 6ª - O CONTRATADO SO SE RESPONSABILIZARA PELO TRANSPORTE DE IDA PARA A ESCOLA, O TRANSPORTE DE VOLTA DA ESCOLA È DE RESPONSABILIDADE DO CONTRATANTE.";
		String volta = "CLAUSULA 6ªB – O CONTRATADO SO SE RESPONSABILIZARA PELO TRANSPORTE DE VOLTA DA ESCOLA, O TRANSPORTE DE IDA PARA A ESCOLA È DE RESPONSABILIDADE DO CONTRATANTE.";

		switch (contrato.getAluno().getIdaVolta()) {
		case 0:
			trocas.put("#TIPOCONTRATO", idaEVolta);
			break;

		case 1:
			trocas.put("#TIPOCONTRATO", ida);
			break;

		case 2:
			trocas.put("#TIPOCONTRATO", volta);
			break;

		default:
			trocas.put("#TIPOCONTRATO", idaEVolta);
			break;
		}

		return trocas;
	}

	public double valorTotal(Aluno aluno) {
		if (aluno != null && aluno.getContratoVigente().getNumeroParcelas() != null) {
			return aluno.getContratoVigente().getValorMensal() * aluno.getContratoVigente().getNumeroParcelas();
		} else {
			return 0;
		}
	}

	private String getMesInicioPagamento(Aluno aluno2, ContratoAluno contrato) {
		String mes = "Janeiro";
		if (contrato != null && contrato.getNumeroParcelas() != null) {

			switch (contrato.getNumeroParcelas()) {
			case 12:
				break;

			case 11:
				mes = "Fevereiro";
				break;

			case 10:
				mes = "Março";
				break;

			case 9:
				mes = "Abril";
				break;

			case 8:
				mes = "Maio";
				break;

			case 7:
				mes = "Junho";
				break;

			case 6:
				mes = "Julho";
				break;

			case 5:
				mes = "Agosto";
				break;

			case 4:
				mes = "Setembro";
				break;

			case 3:
				mes = "Outubro";
				break;

			case 2:
				mes = "Novembro";
				break;

			case 1:
				mes = "Dezembro";
				break;

			default:
				break;
			}
		}
		return mes;
	}

	public byte[] byteArrayPDFDeclaracaoIR(Long idBOleto) {
		ContratoAluno contrato2 = findContratoById(idBOleto);
		Aluno aluno = contrato2.getAluno();
		//ContratoAluno contrato = aluno.getUltimoContratoComBoletoEMaiorValor();

		byte[] bytes = byteArrayPDFDeclaracaoIR(aluno);
		return bytes;
	}

	
	
	
	
	
	
	public byte[] byteArrayPDFBoleto(org.aaf.financeiro.model.Boleto boleto, ContratoAluno contrato) {
		Calendar c = Calendar.getInstance();
		c.setTime(boleto.getVencimento());
		CNAB240_SICOOB cnab = new CNAB240_SICOOB(1);

		Pagador pagador = new Pagador();
		pagador.setBairro(contrato.getBairro());
		pagador.setCep(contrato.getCep());
		pagador.setCidade(contrato.getCidade() != null ? contrato.getCidade() : "PALHOCA");
		pagador.setCpfCNPJ(contrato.getCpfResponsavel());
		pagador.setEndereco(contrato.getEndereco());
		pagador.setNome(contrato.getNomeResponsavel());
		pagador.setNossoNumero(boleto.getNossoNumero() + "");
		pagador.setUF("SC");
		List<org.aaf.financeiro.model.Boleto> boletos = new ArrayList<>();
		boletos.add(boleto);
		pagador.setBoletos(boletos);

		byte[] pdf = cnab.getBoletoPDF(pagador);

		return pdf;
	}

	private org.aaf.financeiro.model.Boleto getBoletoFinanceiro(Boleto boleto) {
		org.aaf.financeiro.model.Boleto boletoFinanceiro = new org.aaf.financeiro.model.Boleto();
		boletoFinanceiro.setEmissao(boleto.getEmissao());
		boletoFinanceiro.setId(boleto.getId());
		boletoFinanceiro.setValorNominal(boleto.getValorNominal());
		boletoFinanceiro.setVencimento(boleto.getVencimento());
		boletoFinanceiro.setNossoNumero(String.valueOf(boleto.getNossoNumero()));
		boletoFinanceiro.setDataPagamento(OfficeUtil.retornaDataSomenteNumeros(boleto.getDataPagamento()));
		boletoFinanceiro.setValorPago(boleto.getValorPago());
		return boletoFinanceiro;
	}

	public Boleto getBoletoMe(int mes, long idContrato) {
		if (mes >= 0) {
			try {
				Calendar c = Calendar.getInstance();
				c.set(configuracaoService.getConfiguracao().getAnoLetivo(), mes, 1, 0, 0, 0);
				Calendar c2 = Calendar.getInstance();
				c2.set(configuracaoService.getConfiguracao().getAnoLetivo(), mes, c.getMaximum(Calendar.DAY_OF_MONTH),
						23, 59, 59);

				StringBuilder sql = new StringBuilder();
				sql.append("SELECT bol from Boleto bol ");
				sql.append("where 1=1 ");
				sql.append(" and bol.vencimento >= '");
				sql.append(c.getTime());
				sql.append("'");
				sql.append(" and bol.vencimento < '");
				sql.append(c2.getTime());
				sql.append("'");
				sql.append(" AND bol.pagador.removido = false ");
				sql.append(" AND (bol.cancelado = false ");
				sql.append(" or  bol.cancelado = null ) ");
				sql.append(" AND bol.contrato.id =  ");
				sql.append(idContrato);

				System.out.println("QUERY:" + sql.toString());
				Query query = em.createQuery(sql.toString());
				Boleto boleto = (Boleto) query.getSingleResult();
				System.out.println("Boleto:" + boleto);

				return boleto;

			} catch (NoResultException nre) {
				System.out.println(nre);
				return null;
			} catch (Exception e) {
				System.out.println(e);
				return null;
			}
		}
		return null;

	}

	public void setStatusCONVITE_ENVIADO(Aluno al) {
		Aluno ap = findById(al.getId());
		ap.setStatusContrato(StatusContratoEnum.CONVITE_ENVIADO);

		em.merge(ap);
		em.flush();
	}

	public void juntar(Aluno aluno, String codIrmao) {
		StringBuilder update1 = new StringBuilder();
		update1.append("update Aluno set irmao");

		StringBuilder update2 = new StringBuilder("update Aluno set irmao");

		if (aluno.getIrmao1() == null) {
			update1.append("1");
			update2.append("1");
		} else if (aluno.getIrmao2() == null) {
			update1.append("2");
			update2.append("2");
		} else if (aluno.getIrmao3() == null) {
			update1.append("3");
			update2.append("3");
		} else if (aluno.getIrmao4() == null) {
			update1.append("4");
			update2.append("4");
		}

		update1.append("_id = (select id from Aluno where codigo like '");
		update1.append(codIrmao);
		update1.append("')  where codigo like '");
		update1.append(aluno.getCodigo());
		update1.append("'");

		update2.append("_id = (select id from Aluno where codigo like '");
		update2.append(aluno.getCodigo());
		update2.append("')  where codigo like '");
		update2.append(codIrmao);
		update2.append("'");

		Query query = em.createQuery(update1.toString());
		long updates = query.executeUpdate();
		System.out.println("Aulas atualizadas = " + updates);

		Query query2 = em.createQuery(update2.toString());
		long updates2 = query2.executeUpdate();
		System.out.println("Aulas atualizadas = " + updates2);
		em.flush();
	}

	public void separar(Aluno aluno) {
		StringBuilder update1 = new StringBuilder("update Aluno set irmao1_id = null  where codigo like '");
		update1.append(aluno.getCodigo());
		update1.append("'");

		StringBuilder update2 = new StringBuilder("update Aluno set irmao2_id = null  where codigo like '");
		update2.append(aluno.getCodigo());
		update2.append("'");

		StringBuilder update3 = new StringBuilder("update Aluno set irmao3_id = null  where codigo like '");
		update3.append(aluno.getCodigo());
		update3.append("'");

		StringBuilder update4 = new StringBuilder("update Aluno set irmao4_id = null  where codigo like '");
		update4.append(aluno.getCodigo());
		update4.append("'");

		Query query = em.createQuery(update1.toString());
		long updates = query.executeUpdate();
		System.out.println("Aulas atualizadas = " + updates);

		Query query2 = em.createQuery(update2.toString());
		long updates2 = query2.executeUpdate();
		System.out.println("Aulas atualizadas = " + updates2);

		Query query3 = em.createQuery(update3.toString());
		long updates3 = query3.executeUpdate();
		System.out.println("Aulas atualizadas = " + updates3);

		Query query4 = em.createQuery(update4.toString());
		long updates4 = query2.executeUpdate();
		System.out.println("Aulas atualizadas = " + updates4);
		em.flush();
	}

	//comentei aqui nao sei se ta certo
//	public List<ContratoAlunoDTO> findContratosAtivos(Long alunoId) {
//	    StringBuilder sql = new StringBuilder();
//	    sql.append("SELECT cont FROM ContratoAluno cont ");
//	    sql.append("WHERE cont.aluno.id = :alunoId ");
//	    sql.append("AND (cont.cancelado IS NULL or cont.cancelado = false)");
//	    sql.append("AND cont.dataCancelamento IS NULL ");
//	    sql.append("AND cont.ano = :anoAtual ");  // Filtra pelo ano atual
//
//	    // Criação da query
//	    Query query = em.createQuery(sql.toString());
//	    query.setParameter("alunoId", alunoId);
//	    query.setParameter("anoAtual", (short) Year.now().getValue()); // Ano atual
//
//	    // Obtendo a lista de ContratoAluno
//	    List<ContratoAluno> contratosAtivos = query.getResultList();
//	    
//	    // Convertendo para uma lista de ContratoAlunoDTO
//	    List<ContratoAlunoDTO> contratosAtivosDTO = new ArrayList<>();
//	    for (ContratoAluno contrato : contratosAtivos) {
//	        contratosAtivosDTO.add(contrato.getDTO()); // Usando o método getDTO()
//	    }
//
//	    return contratosAtivosDTO;
//	}
	
	public List<ContratoAlunoDTO> findContratosAtivos(Long alunoId) {
        StringBuilder sql = new StringBuilder();
        
        // 1. Consulta JPQL simplificada:
        // Removemos a condição de filtro por ano e as condições iniciais de cancelamento.
        sql.append("SELECT cont FROM ContratoAluno cont ");
        sql.append("WHERE cont.aluno.id = :alunoId ");
        
        // Criação da query
        Query query = em.createQuery(sql.toString());
        query.setParameter("alunoId", alunoId);

        // Obtendo a lista de ContratoAluno
        List<ContratoAluno> contratos = query.getResultList();
        
        // 2. Filtrando e Convertendo para DTO em Java (Java 7 Style)
        List<ContratoAlunoDTO> contratosAtivosDTO = new ArrayList<>();
        
        for (ContratoAluno contrato : contratos) {
            
            // Regra 1: Contrato Ativo (não cancelado e sem data de cancelamento)
            boolean isContratoAtivo = (contrato.getCancelado() == null || contrato.getCancelado() == false) 
                                      && contrato.getDataCancelamento() == null;
            
            // Regra 2: Contrato Cancelado MAS com Boletos Não Cancelados
            boolean isContratoCanceladoMasComBoletosValidos = false;
            
            // Verifica se o contrato NÃO está ativo (ou seja, está cancelado)
            if (!isContratoAtivo) {
                
                // Agora, verificamos os boletos
                if (contrato.getBoletos() != null && !contrato.getBoletos().isEmpty()) {
                    
                    // Condição: Precisamos encontrar *pelo menos um* boleto que NÃO esteja cancelado.
                    boolean hasBoletoNaoCancelado = false;
                    
                    // Itera sobre a lista de boletos usando um laço for tradicional
                    for (Boleto boleto : contrato.getBoletos()) {
                        // Se o boleto for NULL ou FALSE para 'cancelado'
                        if (boleto.getCancelado() == null || boleto.getCancelado() == false) {
                            hasBoletoNaoCancelado = true;
                            break; // Se encontrar um, para a iteração (otimização)
                        }
                    }

                    if (hasBoletoNaoCancelado) {
                        isContratoCanceladoMasComBoletosValidos = true;
                    }
                }
            }
            
            // O contrato entra na lista final se for Ativo OU se for Cancelado com boletos válidos.
            if (isContratoAtivo || isContratoCanceladoMasComBoletosValidos) {
                contratosAtivosDTO.add(contrato.getDTO()); // Usando o método getDTO()
            }
        }

        return contratosAtivosDTO;
    }

	public List<ContratoAlunoDTO> findContratosAtivos() {
	    StringBuilder sql = new StringBuilder();
	    sql.append("SELECT cont FROM ContratoAluno cont ");
	    sql.append("WHERE 1=1 ");
	    sql.append("AND (cont.cancelado IS NULL or cont.cancelado = false)");
	    sql.append("AND cont.dataCancelamento IS NULL ");
	    sql.append("AND cont.ano = :anoAtual ");  // Filtra pelo ano atual

	    // Criação da query
	    Query query = em.createQuery(sql.toString());
	    query.setParameter("anoAtual", (short) Year.now().getValue()); // Ano atual

	    // Obtendo a lista de ContratoAluno
	    List<ContratoAluno> contratosAtivos = query.getResultList();
	    
	    // Convertendo para uma lista de ContratoAlunoDTO
	    List<ContratoAlunoDTO> contratosAtivosDTO = new ArrayList<>();
	    for (ContratoAluno contrato : contratosAtivos) {
	        contratosAtivosDTO.add(contrato.getDTO()); // Usando o método getDTO()
	    }

	    return contratosAtivosDTO;
	}


	
}
