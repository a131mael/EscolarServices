
package org.escolar.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;

import org.escolar.enums.BimestreEnum;
import org.escolar.enums.DisciplinaEnum;
import org.escolar.enums.PerioddoEnum;
import org.escolar.enums.Serie;
import org.escolar.model.Aluno;
import org.escolar.model.AlunoAvaliacao;
import org.escolar.model.Avaliacao;
import org.escolar.model.Carro;
import org.escolar.model.Funcionario;
import org.escolar.model.FuncionarioCarro;
import org.escolar.model.Member;
import org.escolar.util.Service;

@Stateless
public class AvaliacaoService extends Service {



	@Inject
	private TurmaService turmaService;
	
	@Inject
	private AlunoService alunoService;
	
	@PersistenceContext(unitName = "EscolarDS")
	private EntityManager em;

	public Avaliacao findById(EntityManager em, Long id) {
		return em.find(Avaliacao.class, id);
	}

	public Avaliacao findById(Long id) {
		return em.find(Avaliacao.class, id);
	}

	public List<Avaliacao> findAll() {
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Avaliacao> criteria = cb.createQuery(Avaliacao.class);
			Root<Avaliacao> member = criteria.from(Avaliacao.class);
			// Swap criteria statements if you would like to try out type-safe
			// criteria queries, a new
			// feature in JPA 2.0
			// criteria.select(member).orderBy(cb.asc(member.get(Member_.name)));
			criteria.select(member).orderBy(cb.asc(member.get("nome")));
			return em.createQuery(criteria).getResultList();

		} catch (NoResultException nre) {
			return new ArrayList<>();
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}

	public List<Avaliacao> findAll(Serie serie, PerioddoEnum periodo) {
		List<Avaliacao> alunos = new ArrayList<>();
		alunos.addAll(find(serie, periodo));
		if (periodo != null) {
			alunos.addAll(find(serie, PerioddoEnum.INTEGRAL));
		}
		return alunos;
	}

	public List<Avaliacao> find(Serie serie, PerioddoEnum periodo) {
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Avaliacao> criteria = cb.createQuery(Avaliacao.class);
			Root<Avaliacao> member = criteria.from(Avaliacao.class);

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

			criteria.select(member).orderBy(cb.asc(member.get("nome")));
			return em.createQuery(criteria).getResultList();

		} catch (NoResultException nre) {
			return new ArrayList<>();
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}

	public Avaliacao save(Avaliacao aluno, Long idProf) {
		Avaliacao user = null;
		try {
			boolean editar = false;

			if (aluno.getId() != null && aluno.getId() != 0L) {
				user = findById(aluno.getId());
				editar = true;
			} else {
				user = new Avaliacao();
			}

			user.setNome(aluno.getNome());
			user.setBimestral(aluno.isBimestral());
			user.setBimestre(aluno.getBimestre());
			user.setData(aluno.getData());
			user.setDisciplina(aluno.getDisciplina());
			user.setPeso(aluno.getPeso());
			user.setProva(aluno.isProva());
			user.setRecuperacao(aluno.isRecuperacao());
			user.setTrabalho(aluno.isTrabalho());
			user.setSerie(aluno.getSerie());
			if (idProf != null) {
				user.setProfessor(em.find(Funcionario.class, idProf));
			}

			em.persist(user);
			if (!editar && idProf != null) {
				createAlunoAvaliacao(user);
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

		return user;
	}

	private void createAlunoAvaliacao(Avaliacao avaliacao) {
		Set<Aluno> alunos = new HashSet<>();
		
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT at from  FuncionarioCarro at ");
		sql.append("where at.professor.id = ");
		sql.append(avaliacao.getProfessor().getId());
		sql.append(" and at.turma.serie = ");
		sql.append(avaliacao.getSerie().ordinal());

		Query query = em.createQuery(sql.toString());

		try {
			List<FuncionarioCarro> turmas = query.getResultList();
			for (FuncionarioCarro t : turmas) {
				alunos.addAll(alunoService.findAlunoTurmaBytTurma(t.getTurma().getId()));
			}

		} catch (NoResultException noResultException) {

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		for (Aluno al : alunos) {
			createAlunoAvaliacao(al, avaliacao);
		}
	}

	public void createAlunoAvaliacao(Aluno al, Avaliacao avaliacao){
		AlunoAvaliacao aa = new AlunoAvaliacao();
		aa.setAluno(al);
		aa.setAvaliacao(avaliacao);
		aa.setAnoLetivo(al.getAnoLetivo());
		em.persist(aa);
	}
	
	public String remover(Long idAvaliacao) {
		Avaliacao av =findById(idAvaliacao);
		List<AlunoAvaliacao> alavs = findAlunoAvaliacaoby(null,av.getId(),null,null,null);
		for(AlunoAvaliacao alav :alavs){
			em.remove(alav);
		}
		
		em.remove(av);
		return "ok";
	}

	public List<AlunoAvaliacao> findAlunoAvaliacaoby(Long idAluno, Long idAvaliavao, DisciplinaEnum disciplina, BimestreEnum bimestre, Serie serie) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT al from  AlunoAvaliacao al ");
		sql.append("where 1=1 ");
		if(idAluno != null ){
			sql.append(" and al.aluno.id =   ");
			sql.append(idAluno);
		}
		if(idAvaliavao != null ){
			sql.append(" and al.avaliacao.id =   ");
			sql.append(idAvaliavao);
		}
		
		if(disciplina != null){
			sql.append(" and al.avaliacao.disciplina =   ");
			sql.append(disciplina.ordinal());
		}
		if(bimestre != null){
			sql.append(" and al.avaliacao.bimestre =   ");
			sql.append(bimestre.ordinal());
		}
		
		if(serie != null){
			sql.append(" and al.avaliacao.serie =   ");
			sql.append(serie.ordinal());
		}
		
		Query query = em.createQuery(sql.toString());

		try {
			return (List<AlunoAvaliacao>)query.getResultList();

		} catch (NoResultException noResultException) {
			noResultException.printStackTrace();
			return new ArrayList<>();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
		
	}

	public List<Avaliacao> findAvaliacaoby(DisciplinaEnum disciplina, BimestreEnum bimestre, Serie serie) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT av from  Avaliacao av ");
		sql.append("where 1=1 ");
		if(disciplina != null){
			sql.append(" and av.disciplina =   ");
			sql.append(disciplina.ordinal());
		}
		if(bimestre != null){
			sql.append(" and av.bimestre =   ");
			sql.append(bimestre.ordinal());
		}
		
		if(serie != null){
			sql.append(" and av.serie =   ");
			sql.append(serie.ordinal());
		}
		
		Query query = em.createQuery(sql.toString());

		try {
			return (List<Avaliacao>)query.getResultList();

		} catch (NoResultException noResultException) {
			return new ArrayList<>();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
		
	}

	public void saveAlunoAvaliacao(AlunoAvaliacao alunoAvaliacao) {
		alunoAvaliacao.setAluno(em.find(Aluno.class, alunoAvaliacao.getAluno().getId()));
		alunoAvaliacao.setAvaliacao(em.find(Avaliacao.class, alunoAvaliacao.getAvaliacao().getId()));
		float  n = alunoAvaliacao.getNota();
		AlunoAvaliacao al = em.find(AlunoAvaliacao.class, alunoAvaliacao.getId()); 
		al.setNota(n);
		em.persist(al);
	}

	public void saveAlunoAvaliacao(Long  idAluAv, Float nota) {
		AlunoAvaliacao al = em.find(AlunoAvaliacao.class, idAluAv); 
		al.setNota(nota);
		em.persist(al);
	}

	public void save(AlunoAvaliacao alunoAvaliacao) {
		float nota = alunoAvaliacao.getNota();
		AlunoAvaliacao al = em.find(AlunoAvaliacao.class, alunoAvaliacao.getId()); 
		al.setNota(nota);
		em.persist(al);
	}

	public Map<Aluno, List<AlunoAvaliacao>> findAlunoAvaliacaoMap(Long idAluno, Long idAvaliavao, DisciplinaEnum disciplina, BimestreEnum bimestre, Serie serie) {
		Map<Aluno,List<AlunoAvaliacao>> alunosAvaliacoes = new LinkedHashMap<>();
		
		
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT al from  AlunoAvaliacao al ");
		sql.append("where 1=1 ");
		if(idAluno != null ){
			sql.append(" and al.aluno.id =   ");
			sql.append(idAluno);
		}
		if(idAvaliavao != null ){
			sql.append(" and al.avaliacao.id =   ");
			sql.append(idAvaliavao);
		}
		
		if(disciplina != null){
			sql.append(" and al.avaliacao.disciplina =   ");
			sql.append(disciplina.ordinal());
		}
		if(bimestre != null){
			sql.append(" and al.avaliacao.bimestre =   ");
			sql.append(bimestre.ordinal());
		}
		
		if(serie != null){
			sql.append(" and al.avaliacao.serie =   ");
			sql.append(serie.ordinal());
		}
		sql.append(" order by  al.aluno.nomeAluno");
		
		Query query = em.createQuery(sql.toString());
		
		

		try {
			List<AlunoAvaliacao> alunosAva = query.getResultList();
			for(AlunoAvaliacao aa :alunosAva){
				if(!alunosAvaliacoes.containsKey(aa.getAluno())){
					alunosAvaliacoes.put(aa.getAluno(), getAvaliacoesAluno(aa.getAluno(),alunosAva));
				}
			}
			
			

		} catch (NoResultException noResultException) {
			noResultException.printStackTrace();
			return alunosAvaliacoes;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return alunosAvaliacoes;
	}

	private List<AlunoAvaliacao> getAvaliacoesAluno(Aluno aluno, List<AlunoAvaliacao> avaliacoes){
		List<AlunoAvaliacao> avaliacoesAluno = new ArrayList<>();
		for(AlunoAvaliacao aav: avaliacoes){
			if(aav.getAluno().equals(aluno)){
				avaliacoesAluno.add(aav);
			}
		}
		return avaliacoesAluno;
	}
	public Set<Avaliacao> findAllAvaliacao(Member loggedUser) {
		
		Set<Avaliacao> avaliacoes = new LinkedHashSet<>();
		List<Carro> turmasProf = turmaService.findAll(loggedUser.getId());
		for(Carro turma :turmasProf){
		}
		return avaliacoes;
	}

	/*
	 * public Usuario findMaiorPontuadorSemana() { StringBuilder sql = new
	 * StringBuilder(); sql.append("SELECT u from  Usuario u "); sql.append(
	 * "where pontosSemana =  (SELECT MAX(pontosSemana) FROM Usuario) ");
	 * 
	 * Query query = em.createQuery(sql.toString()); return (Usuario)
	 * query.getResultList().get(0); }
	 * 
	 * public Usuario findMaiorPontuadorMes() { StringBuilder sql = new
	 * StringBuilder(); sql.append("SELECT u from  Usuario u "); sql.append(
	 * "where pontosMes =  (SELECT MAX(pontosMes) FROM Usuario) ");
	 * 
	 * Query query = em.createQuery(sql.toString()); return (Usuario)
	 * query.getResultList().get(0);
	 * 
	 * }
	 * 
	 * public Usuario findMaiorPontuadorGeral() { StringBuilder sql = new
	 * StringBuilder(); sql.append("SELECT u from  Usuario u "); sql.append(
	 * "where pontosGeral =  (SELECT MAX(pontosGeral) FROM Usuario) ");
	 * 
	 * Query query = em.createQuery(sql.toString()); return (Usuario)
	 * query.getResultList().get(0); }
	 */
}
