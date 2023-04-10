
package org.escolar.service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;

import org.escolar.model.Aluno;
import org.escolar.model.Boleto;
import org.escolar.model.Configuracao;
import org.escolar.model.ContratoAluno;
import org.escolar.util.Service;
import org.escolar.util.Util;

@Stateless
public class ConfiguracaoService extends Service {

	@PersistenceContext(unitName = "EscolarDS")
	private EntityManager em;

	public Configuracao findById(EntityManager em, Long id) {
		return em.find(Configuracao.class, id);
	}

	public Configuracao findById(Long id) {
		return em.find(Configuracao.class, id);
	}

	public Configuracao findByCodigo(Long id) {
		return em.find(Configuracao.class, id);
	}

	public String remover(Long idEvento) {
		em.remove(findById(idEvento));
		em.flush();
		return "index";
	}

	public Configuracao getConfiguracao() {
		return findAll().get(0);
	}

	public long getSequencialArquivo() {
		return getConfiguracao().getSequencialArquivoCNAB();
	}

	public void incrementaSequencialArquivoCNAB() {
		long sequecial = getSequencialArquivo();
		sequecial++;
		Configuracao conf = getConfiguracao();
		conf.setSequencialArquivoCNAB(sequecial);
		save(conf);
	}

	public List<Configuracao> findAll() {
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Configuracao> criteria = cb.createQuery(Configuracao.class);
			Root<Configuracao> member = criteria.from(Configuracao.class);
			// Swap criteria statements if you would like to try out type-safe
			// criteria queries, a new
			// feature in JPA 2.0
			// criteria.select(member).orderBy(cb.asc(member.get(Member_.name)));
			criteria.select(member).orderBy(cb.asc(member.get("id")));
			return em.createQuery(criteria).getResultList();

		} catch (NoResultException nre) {
			return new ArrayList<>();
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}

	public Configuracao save(Configuracao configuracao) {
		Configuracao user = null;
		try {

			if (configuracao.getId() != null && configuracao.getId() != 0L) {
				user = findById(configuracao.getId());
			} else {
				user = new Configuracao();
			}

			user.setAnoLetivo(configuracao.getAnoLetivo());
			user.setValordinheiroEmcaixa(configuracao.getValordinheiroEmcaixa());
			
			user.setSequencialArquivoCNAB(configuracao.getSequencialArquivoCNAB());
			user.setAnoRematricula(configuracao.getAnoRematricula());

			em.persist(user);

		} catch (ConstraintViolationException ce) {
			// Handle bean validation issues
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
		em.flush();
		return user;
	}

	public void gerarCnabDoMes(int mes) {

	}

	public List<Boleto> findBoletosMes(int mes, int ano) {

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT distinct(bol) from Boleto bol ");
		sql.append(" where 1 = 1");
		sql.append(" and (bol.baixaGerada = false or bol.baixaGerada is null)");
		sql.append(" and (bol.cancelado = false or bol.cancelado is null)");
		sql.append(" and (bol.cnabEnviado = false or bol.cnabEnviado is null)");
		sql.append(" and (bol.valorPago = 0 or bol.valorPago is null)");
		sql.append(" and bol.vencimento > '" + Util.getDataInicioMesString(mes, ano) + "'");
		sql.append(" and bol.vencimento < '" + Util.getDataFimMesString(mes, ano) + "'");

		Query query = em.createQuery(sql.toString());
		List<Boleto> t = (List<Boleto>) query.getResultList();

		return t;
	}
	
	public List<Boleto> findBoletosCanceladosMes(int mes, int ano) {

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT distinct(bol) from Boleto bol ");
		sql.append(" where 1 = 1");
		sql.append(" and (bol.cancelado = true)");
		sql.append(" and (bol.manterAposRemovido = false or manterAposRemovido is null)");
		sql.append(" and (bol.cnabCanceladoEnviado = false or cnabCanceladoEnviado is null)");
		sql.append(" and (bol.valorPago = 0 or bol.valorPago is null)");
		sql.append(" and bol.vencimento > '" + Util.getDataInicioMesString(mes, ano) + "'");
		sql.append(" and bol.vencimento < '" + Util.getDataFimMesString(mes, ano) + "'");

		Query query = em.createQuery(sql.toString());
		List<Boleto> t = (List<Boleto>) query.getResultList();

		return t;
	}

	public ContratoAluno findContrato(Long idBoleto) {

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT distinct(contratoaluno_id) from contratoaluno_boleto ");
		sql.append(" where 1 = 1");
		sql.append(" and boletos_id  = ");
		sql.append(idBoleto);
		Query query = em.createNativeQuery(sql.toString());
		BigInteger t = (BigInteger) query.getSingleResult();
		
		ContratoAluno c = em.find(ContratoAluno.class, t.longValue());
		c.getBoletos().size();

		return c;
	}

	public void mudarStatusParaCNABEnviado(Boleto b) {
		b = em.find(Boleto.class, b.getId());
		b.setCnabEnviado(true);
		em.merge(b);
		em.flush();
	}

	public void mudarStatusParaProtestoEnviadoPorEmail(ContratoAluno ca) {
		ca = em.find(ContratoAluno.class, ca.getId());
		ca.setEnviadoPorEmailProtesto(true);
		em.merge(ca);
		em.flush();
	}

	public List<ContratoAluno> findContratosProtestados(boolean jaEnviadoPorEmail) {
		
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT distinct(ca) from ContratoAluno ca");
		sql.append(" where 1 = 1");
		sql.append(" and ca.enviadoProtestoDefinitivo  = true");
		
		if(jaEnviadoPorEmail){
			sql.append(" and ca.enviadoPorEmailProtesto = true");
		}else{
			sql.append(" and (ca.enviadoPorEmailProtesto = false or ca.enviadoPorEmailProtesto is null)");
		}
		
		Query query = em.createQuery(sql.toString());
		List<ContratoAluno> cas = (List<ContratoAluno>) query.getResultList();
		
		for(ContratoAluno ca: cas){
			ca.getBoletos().size();
		}

		return cas;
	}

}
