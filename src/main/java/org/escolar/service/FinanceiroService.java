package org.escolar.service;

import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
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

import org.aaf.financeiro.model.Boleto;
import org.escolar.model.Aluno;
import org.escolar.model.ContratoAluno;
import org.escolar.util.Service;

@Stateless
public class FinanceiroService extends Service {

	@PersistenceContext(unitName = "EscolarDS")
	private EntityManager em;

	@Inject
	private AlunoService alunoService;
	
	@Inject
	private ConfiguracaoService configuracaoService;


	public Boleto findBoletoByNumero(String numeroBoleto) {
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT bol from Boleto bol ");
			sql.append("where 1=1 ");
			sql.append(" and bol.nossoNumero = '");
			sql.append(numeroBoleto);
			sql.append("'");

			Query query = em.createQuery(sql.toString());
			Boleto boleto = (Boleto) query.getSingleResult();
			return boleto;
		} catch (NoResultException nre) {
			return null;
		}
	}

	public org.escolar.model.Boleto findBoletoByNumeroModel(String numeroBoleto) {
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT bol from Boleto bol ");
			sql.append("where 1=1 ");
			sql.append(" and bol.nossoNumero = '");
			sql.append(numeroBoleto);
			sql.append("'");

			Query query = em.createQuery(sql.toString());
			org.escolar.model.Boleto boleto = (org.escolar.model.Boleto) query.getSingleResult();
			return boleto;
		} catch (NoResultException nre) {
			return null;
		}
	}

	public org.escolar.model.Boleto findBoletoByID(Long id) {
		try {
			return em.find(org.escolar.model.Boleto.class, id);
		} catch (NoResultException nre) {
			return null;
		}
	}
	
	public org.escolar.model.ContratoAluno findContratoALunoByID(Long id) {
		try {
			return em.find(org.escolar.model.ContratoAluno.class, id);
		} catch (NoResultException nre) {
			return null;
		}
	}

	public void save(org.escolar.model.Boleto boleto) {
		if (boleto.getId() != null) {
			org.escolar.model.Boleto bol = findBoletoByID(boleto.getId());
			bol.setValorPago(boleto.getValorPago());
			bol.setDataPagamento(boleto.getDataPagamento());
			bol.setBaixaManual(boleto.getBaixaManual());
			bol.setConciliacaoPorExtrato(boleto.getConciliacaoPorExtrato());
			bol.setBaixaGerada(boleto.getBaixaGerada());
			bol.setCnabCanceladoEnviado(boleto.getCnabCanceladoEnviado());
			
			em.merge(bol);
		}
	}

	public List<org.escolar.model.Boleto> getBoletoMes(int mes) {
		if (mes >= 0) {
			try {
				Calendar c = Calendar.getInstance();
				c.set(configuracaoService.getConfiguracao().getAnoLetivo(), mes, 1, 0, 0, 0);
				Calendar c2 = Calendar.getInstance();
				c2.set(configuracaoService.getConfiguracao().getAnoLetivo(), mes, c.getMaximum(Calendar.MONTH), 23, 59, 59);

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
				Query query = em.createQuery(sql.toString());
				List<org.escolar.model.Boleto> boleto = (List<org.escolar.model.Boleto>) query.getResultList();
				return boleto;
			} catch (NoResultException nre) {
				return null;
			}
		}
		return null;
	}

	public List<org.escolar.model.Boleto> getBoletosParaBaixa() {
		try {

			StringBuilder sql = new StringBuilder();
			sql.append("SELECT bol from Boleto bol ");
			sql.append("where 1=1 ");
			sql.append(" and (bol.baixaManual = true ");
			sql.append(" or bol.conciliacaoPorExtrato = true )");
			sql.append(" and (bol.baixaGerada = false or bol.baixaGerada is null) ");

			Query query = em.createQuery(sql.toString());
			List<org.escolar.model.Boleto> boleto = (List<org.escolar.model.Boleto>) query.getResultList();
			return boleto;
		} catch (NoResultException nre) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public double getPrevisto(int mes) {
		if (mes > 0) {
			try {
				Calendar c2 = Calendar.getInstance();
				c2.set(Calendar.MONTH, 1);
				c2.getActualMaximum(Calendar.DAY_OF_MONTH);
				
				StringBuilder sb = new StringBuilder();
				sb.append(configuracaoService.getConfiguracao().getAnoLetivo());
				sb.append("-");
				sb.append(mes);
				sb.append("-");
				sb.append("01");
				StringBuilder sb2 = new StringBuilder();
				sb2.append(configuracaoService.getConfiguracao().getAnoLetivo());
				sb2.append("-");
				sb2.append(mes);
				sb2.append("-");
				sb2.append(getUltimoDiaMes(mes));

				StringBuilder sql = new StringBuilder();
				sql.append("SELECT sum(bol.valorNominal) from Boleto bol ");
				sql.append("where 1=1 ");
				sql.append(" and bol.vencimento >= '");
				sql.append(sb);
				sql.append("'");
				sql.append(" and bol.vencimento < '");
				sql.append(sb2);
				sql.append("'");
				sql.append(" and (bol.cancelado = false");
				sql.append(" or  bol.cancelado is null)");

				Query query = em.createQuery(sql.toString());
				Double boleto = (Double) query.getSingleResult();
				if(boleto == null){
					return 0;
				}
				return boleto;
			} catch (NoResultException nre) {
				return 0D;
			}
		}
		return 0D;

	}
	
	private int getUltimoDiaMes(int mes){
		switch ( mes) {
		case 1:
			return 31;
		case 2:
			return 28;
		case 3:
			return 31;
		case 4:
			return 30;
		case 5:
			return 31;
		case 6:
			return 30;
		case 7:
			return 31;
		case 8:
			return 31;
		case 9:
			return 30;
		case 10:
			return 31;
		case 11:
			return 30;
		case 12:
			return 31;

		default:
			return 0;
		}
	}
	
	public int countContratos(int mes) {
		if (mes >= 0) {
			try {
				StringBuilder sql = new StringBuilder();
				sql.append("SELECT count(id) from ContratoAluno bol ");
				sql.append("where 1=1 ");
				sql.append(" and bol.ano = ");
				sql.append(configuracaoService.getConfiguracao().getAnoLetivo());
				sql.append(" and (bol.cancelado = false");
				sql.append(" or  bol.cancelado is null)");

				Query query = em.createQuery(sql.toString());
				Long boleto = (Long) query.getSingleResult();
				return boleto.intValue();
			} catch (NoResultException nre) {
				return 0;
			}
		}
		return 0;

	}

	
	public Double getPago(int mes) {
		if (mes >= 0) {
			try {
				Calendar c = Calendar.getInstance();
				c.set(configuracaoService.getConfiguracao().getAnoLetivo(), mes, 1, 0, 0, 0);
				Calendar c2 = Calendar.getInstance();
				c2.set(configuracaoService.getConfiguracao().getAnoLetivo(), mes, c.getMaximum(Calendar.MONTH), 23, 59, 59);

				StringBuilder sql = new StringBuilder();
				sql.append("SELECT sum(bol.valorPago) from Boleto bol ");
				sql.append("where 1=1 ");
				sql.append(" and bol.dataPagamento >= '");
				sql.append(c.getTime());
				sql.append("'");
				sql.append(" and bol.dataPagamento < '");
				sql.append(c2.getTime());
				sql.append("'");
				sql.append(" AND bol.pagador.removido = false ");

				Query query = em.createQuery(sql.toString());
				Object retorno = query.getSingleResult();
				Double boleto = null;
				if (retorno != null) {
					boleto = (Double) retorno;
				} else {
					boleto = 0D;
				}

				return boleto;
			} catch (NoResultException nre) {
				return 0D;
			}
		}
		return 0D;

	}

	@SuppressWarnings("unchecked")
	public List<org.escolar.model.Boleto> find(int first, int size, String orderBy, String order,
			Map<String, Object> filtros) {
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Boleto> criteria = cb.createQuery(Boleto.class);
			Root<Boleto> member = criteria.from(Boleto.class);
			CriteriaQuery cq = criteria.select(member);

			final List<Predicate> predicates = new ArrayList<Predicate>();
			for (Map.Entry<String, Object> entry : filtros.entrySet()) {

				Predicate pred = cb.and();
				if (entry.getValue() instanceof String) {
					pred = cb.and(pred, cb.like(member.<String> get(entry.getKey()), "%" + entry.getValue() + "%"));
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
			return (List<org.escolar.model.Boleto>) q.getResultList();

		} catch (NoResultException nre) {
			return new ArrayList<>();
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}

	}
	
	public long count(Map<String, Object> filtros) {
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
			Root<Boleto> member = countQuery.from(Boleto.class);
			countQuery.select(cb.count(member));

			final List<Predicate> predicates = new ArrayList<Predicate>();
			if (filtros != null) {
				for (Map.Entry<String, Object> entry : filtros.entrySet()) {

					Predicate pred = cb.and();
					if (entry.getValue() instanceof String) {
						pred = cb.and(pred, cb.like(member.<String> get(entry.getKey()), "%" + entry.getValue() + "%"));
					} else {
						pred = cb.equal(member.get(entry.getKey()), entry.getValue());
					}
					predicates.add(pred);

				}

			}
			countQuery.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
			Query q = em.createQuery(countQuery);
			return (long) q.getSingleResult();

		} catch (NoResultException nre) {
			return 0;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	public void alterarBoletoManualmente(org.escolar.model.Boleto boleto) {
		org.escolar.model.Boleto boletoMerged = em.find(org.escolar.model.Boleto.class, boleto.getId());
		boletoMerged.setAlteracaoBoletoManual(true);
		boletoMerged.setVencimento(boleto.getVencimento());
		boletoMerged.setValorNominal(boleto.getValorNominal());
		em.merge(boletoMerged);
	}

	public List<Aluno> getAlunosCNABNaoEnviado() {
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT distinct(al) from  ContratoAluno al ");
			sql.append("where 1=1 ");
			sql.append("and (al.cnabEnviado = false or al.cnabEnviado is null )");
			sql.append("and al.cancelado=false");

			Query query = em.createQuery(sql.toString());

			@SuppressWarnings("unchecked")
			List<Aluno> alunos = query.getResultList();

			return alunos;
		} catch (NoResultException nre) {
			return null;
		}
	}

	public void saveCNABENviado(Aluno aluno) {
		Aluno al = em.find(Aluno.class, aluno.getId());
		al.getContratoVigente().setCnabEnviado(true);
		em.merge(al);
	}

	public List<ContratoAluno> getAlunosRemovidos() {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT distinct(al) from  Boleto bol ");
		sql.append("left join bol.contrato al ");
		sql.append("where 1=1 ");
		sql.append(" and al.cancelado = true ");
		sql.append(" and (bol.baixaGerada = false or bol.baixaGerada is null)");

		Query query = em.createQuery(sql.toString());

		@SuppressWarnings("unchecked")
		List<ContratoAluno> alunos = query.getResultList();
			for(ContratoAluno contrato : alunos){
				contrato.getBoletos().size();
			}

		return alunos;
	}

	public List<org.escolar.model.Boleto> getBoletosAtrasados(int mes) {
		if (mes >= 0) {
			try {
				Calendar c = Calendar.getInstance();
				c.set(Calendar.YEAR, configuracaoService.getConfiguracao().getAnoLetivo());
				c.set(Calendar.MONTH, mes);
				c.set(Calendar.MINUTE, 59);
				c.set(Calendar.HOUR, 23);
				

				StringBuilder sql = new StringBuilder();
				sql.append("SELECT bol from Boleto bol ");
				sql.append("where 1=1 ");
				sql.append(" and bol.vencimento <= '");
				sql.append(c.getTime());
				sql.append("'");
				sql.append(" and (bol.valorPago = 0");
				sql.append(" or bol.valorPago is null)");
				sql.append(" and (bol.baixaManual = false");
				sql.append(" or bol.baixaManual is null)");

				
				sql.append(" and (bol.conciliacaoPorExtrato = false");
				sql.append(" or bol.conciliacaoPorExtrato is null)");
				
				
				sql.append(" and (bol.baixaGerada = false");
				sql.append(" or bol.baixaGerada is null)");
				
				
				Query query = em.createQuery(sql.toString());
				List<org.escolar.model.Boleto> boleto = (List<org.escolar.model.Boleto>) query.getResultList();
				return boleto;
			} catch (NoResultException nre) {
				return null;
			}
		}
		return null;
	}
	
	public List<org.escolar.model.Boleto> getBoletosAtrasadosAluno(int mes, Long idAluno) {
		if (mes >= 0) {
			try {
				Calendar c = Calendar.getInstance();
				c.set(Calendar.YEAR, configuracaoService.getConfiguracao().getAnoLetivo());
				c.set(Calendar.MONTH, mes);
				c.set(Calendar.MINUTE, 59);
				c.set(Calendar.HOUR, 23);
				

				StringBuilder sql = new StringBuilder();
				sql.append("SELECT bol from Boleto bol ");
				sql.append("where 1=1 ");
				sql.append(" and bol.vencimento <= '");
				sql.append(c.getTime());
				sql.append("'");
				if(idAluno != null){
					sql.append(" and bol.pagador.id = ");
					sql.append(idAluno);
				}
				sql.append(" and (bol.valorPago = 0");
				sql.append(" or bol.valorPago is null)");
				sql.append(" and (bol.baixaManual = false");
				sql.append(" or bol.baixaManual is null)");

				
				sql.append(" and (bol.conciliacaoPorExtrato = false");
				sql.append(" or bol.conciliacaoPorExtrato is null)");
				
				
				sql.append(" and (bol.baixaGerada = false");
				sql.append(" or bol.baixaGerada is null)");
				
				
				Query query = em.createQuery(sql.toString());
				List<org.escolar.model.Boleto> boleto = (List<org.escolar.model.Boleto>) query.getResultList();
				return boleto;
			} catch (NoResultException nre) {
				return null;
			}
		}
		return null;

	}

	public org.escolar.model.Boleto getBoletoMes(int mes ,Long idAluno) {
		if (mes >= 0) {
			try {
				Calendar c = Calendar.getInstance();
				c.set(configuracaoService.getConfiguracao().getAnoLetivo(), mes, 1, 0, 0, 0);
				Calendar c2 = Calendar.getInstance();
				c2.set(configuracaoService.getConfiguracao().getAnoLetivo(), mes, c.getMaximum(Calendar.MONTH), 23, 59, 59);

				StringBuilder sql = new StringBuilder();
				sql.append("SELECT bol from Boleto bol ");
				sql.append("where 1=1 ");
				sql.append(" and bol.vencimento >= '");
				sql.append(c.getTime());
				sql.append("'");
				sql.append(" and bol.vencimento < '");
				sql.append(c2.getTime());
				sql.append("'");
				if(idAluno != null){
					sql.append(" and bol.pagador.id = ");
					sql.append(idAluno);
				}
				Query query = em.createQuery(sql.toString());
				org.escolar.model.Boleto boleto = (org.escolar.model.Boleto) query.getSingleResult();
				return boleto;
			} catch (NoResultException nre) {
				return null;
			}
		}
		return null;

	}

	public void saveProtestar(org.escolar.model.Boleto boleto) {
		if (boleto.getId() != null) {
			ContratoAluno ca = findContratoALunoByID(boleto.getContrato().getId());
			ca.setProtestado(true);
			em.merge(ca);
			
			org.escolar.model.Boleto bol = findBoletoByID(boleto.getId());
			bol.setValorPago(boleto.getValorPago());
			bol.setDataPagamento(boleto.getDataPagamento());
			bol.setBaixaManual(boleto.getBaixaManual());
			bol.setConciliacaoPorExtrato(boleto.getConciliacaoPorExtrato());
			bol.setBaixaGerada(boleto.getBaixaGerada());
			bol.setCnabCanceladoEnviado(boleto.getCnabCanceladoEnviado());
			
			em.merge(bol);
		}
	}

	public int getQuantidadeBoletosAtrasadosPorMes(int mes){
		StringBuilder sql = new StringBuilder();
		sql.append("select count(*) from boleto bol left join contratoaluno contrato on bol.contrato_id = contrato.id where ");
		if(mes > 0){
			sql.append(getIntervaloMes(mes));
			sql.append(" and (bol.cancelado is null or bol.cancelado = false)");
		}else{
			sql.append("  (bol.cancelado is null or bol.cancelado = false)");
		}
		sql.append(" and bol.datapagamento is null and (contrato.protestado is  null or contrato.protestado = false )");
		Query query = em.createNativeQuery(sql.toString());
		BigInteger codigo = (BigInteger) query.getSingleResult();
		if(codigo == null){
			return 0;
		}
		return codigo.intValue();
	}
	
	public Double getValorBoletosAtrasadosPorMes(int mes){
		StringBuilder sql = new StringBuilder();
		sql.append("select sum(valornominal) from boleto bol  left join contratoaluno contrato on bol.contrato_id = contrato.id where ");
		sql.append(getIntervaloMes(mes));
		sql.append(" and (bol.cancelado is null or bol.cancelado = false)");
		sql.append(" and bol.datapagamento is null and (contrato.protestado is  null or contrato.protestado = false ) ");
		Query query = em.createNativeQuery(sql.toString());
		Double codigo = (Double) query.getSingleResult();
		if(codigo == null){
			return 0d;
		}
		return codigo;
	}
	
	public int getQuantidadeBoletosAtrasadosPorQuantidade(int quantidade){
		final DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		final String hoje = df.format(new Date());
		
		StringBuilder sql = new StringBuilder();
		sql.append("select sum(1) from ( ");
		sql.append(" select sum(1) as quantidadeAtrasadas,pagador_id from boleto bol left join contratoaluno contrato on bol.contrato_id = contrato.id ");
		sql.append(" where ");
		sql.append(" bol.vencimento >  '2021-01-01' and bol.vencimento < '");
		sql.append(hoje);
		sql.append("'");
		sql.append(" and (bol.cancelado is null or bol.cancelado = false)");
		sql.append(" and bol.datapagamento is null and (contrato.protestado is  null or contrato.protestado = false ) group by pagador_id");
		sql.append(" order by quantidadeAtrasadas) as buscaAtrasadas");
		if(quantidade != 0){
			sql.append(" where quantidadeAtrasadas  ");
			if(quantidade > 6){
				sql.append(" > ");
				sql.append(quantidade-1);
			}else{
				sql.append(" = ");
				sql.append(quantidade);	
			}
		}
		
		Query query = em.createNativeQuery(sql.toString());
		BigInteger codigo = (BigInteger) query.getSingleResult();
		if(codigo == null){
			return 0;
		}
		return codigo.intValue();
	}
	
	public int getQuantidadeBoletosAtrasados(){
		final DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		final String hoje = df.format(new Date());
		
		StringBuilder sql = new StringBuilder();
		sql.append("select sum(1) as quantidadeAtrasadas from boleto bol left join contratoaluno contrato on bol.contrato_id = contrato.id  where	 bol.vencimento >  '2021-01-01'  ");
		sql.append(" and bol.vencimento < '");
		sql.append(hoje);
		sql.append("'");
		sql.append(" and (bol.cancelado is null or bol.cancelado = false)	 and bol.datapagamento is null and (contrato.protestado is  null or contrato.protestado = false )");
		
		Query query = em.createNativeQuery(sql.toString());
		BigInteger codigo = (BigInteger) query.getSingleResult();
		if(codigo == null){
			return 0;
		}
		return codigo.intValue();
	}
	
	public Double getValorTotalAtrasado(){
		final DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		final String hoje = df.format(new Date());
		
		StringBuilder sql = new StringBuilder();
		sql.append("select sum(valornominal) as quantidadeAtrasadas from boleto bol left join contratoaluno contrato on bol.contrato_id = contrato.id where	 bol.vencimento >  '2021-01-01'  ");
		sql.append(" and bol.vencimento < '");
		sql.append(hoje);
		sql.append("'");
		sql.append(" and (bol.cancelado is null or bol.cancelado = false)	 and bol.datapagamento is null  and (contrato.protestado is  null or contrato.protestado = false )");
		
		Query query = em.createNativeQuery(sql.toString());
		Double codigo = (Double) query.getSingleResult();
		if(codigo == null){
			return 0d;
		}
		return codigo;
	}
	
	
	@SuppressWarnings("unchecked")
	public List<Aluno> findAlunoMes(int first, int size, String orderBy, String order,	Map<String, Object> filtros) {
			
			StringBuilder sql = new StringBuilder();
			sql.append("select pagador_id from boleto bol left join contratoaluno contrato on bol.contrato_id = contrato.id  where  ");
			sql.append(getIntervaloMes((Integer)filtros.get("mesAtrasado")));
			sql.append(" and (bol.cancelado is null or bol.cancelado = false)	 and bol.datapagamento is null  and (contrato.protestado is  null or contrato.protestado = false )");
			
			Query query = em.createNativeQuery(sql.toString());
			List<BigInteger> codigo = (List<BigInteger>) query.getResultList();
			if(codigo == null){
				return new ArrayList<>();
			}
			List<Aluno> alunos = new ArrayList<>();
			for(BigInteger id : codigo){
				alunos.add(alunoService.findById(id.longValue()));
			}
			
			return alunos;
	}
	
	@SuppressWarnings("unchecked")
	public List<Aluno> findAlunoQuantidade(int first, int size, String orderBy, String order,	Map<String, Object> filtros) {
		int quantidade = (Integer)filtros.get("quantidadeAtrasados");	
		final DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		final String hoje = df.format(new Date());
		
		StringBuilder sql = new StringBuilder();
		
		sql.append("select pagador_id from (");
		sql.append(" select sum(1) as quantidadeAtrasadas,pagador_id from boleto  bol left join contratoaluno contrato on bol.contrato_id = contrato.id ");
		sql.append(" where ");
		sql.append(" bol.vencimento >  '2021-01-01' and bol.vencimento < '");
		sql.append(hoje);
		sql.append("'");
		sql.append(" and (bol.cancelado is null or bol.cancelado = false)");
		sql.append(" and bol.datapagamento is null  and (contrato.protestado is  null or contrato.protestado = false ) ");
		sql.append(" group by pagador_id");
		sql.append("  order by quantidadeAtrasadas) as buscaAtrasadas");
		if(quantidade != 0){
			sql.append(" where quantidadeAtrasadas  ");
			if(quantidade > 6){
				sql.append(" > ");
				sql.append(quantidade-1);
			}else{
				sql.append(" = ");
				sql.append(quantidade);	
			}
		}
		
		Query query = em.createNativeQuery(sql.toString());
		List<BigInteger> codigo = (List<BigInteger>) query.getResultList();
		if(codigo == null){
			return new ArrayList<>();
		}
		List<Aluno> alunos = new ArrayList<>();
		for(BigInteger id : codigo){
			alunos.add(alunoService.findById(id.longValue()));
		}
		return alunos;
	}
	
	private String getIntervaloMes(int mes){
		
		String retorno = "";
		switch (mes) {
		case 1:
			retorno = " vencimento >  '2021-01-01' and vencimento < '2021-01-30' " ;
			break;
		case 2:
			retorno = " vencimento >  '2021-02-01' and vencimento < '2021-02-28' " ;
			break;
		case 3:
			retorno = " vencimento >  '2021-03-01' and vencimento < '2021-03-30' " ;
			break;
		case 4:
			retorno = " vencimento >  '2021-04-01' and vencimento < '2021-04-30' " ;
			break;
		case 5:
			retorno = " vencimento >  '2021-05-01' and vencimento < '2021-05-30' " ;
			break;
		case 6:
			retorno = " vencimento >  '2021-06-01' and vencimento < '2021-06-30' " ;
			break;
		case 7:
			retorno = " vencimento >  '2021-07-01' and vencimento < '2021-07-30' " ;
			break;
		case 8:
			retorno = " vencimento >  '2021-08-01' and vencimento < '2021-08-30' " ;
			break;
		case 9:
			retorno = " vencimento >  '2021-09-01' and vencimento < '2021-09-30' " ;
			break;
		case 10:
			retorno = " vencimento >  '2021-10-01' and vencimento < '2021-10-30' " ;
			break;
		case 11:
			retorno = " vencimento >  '2021-11-01' and vencimento < '2021-11-30' " ;
			break;
		case 12:
			retorno = " vencimento >  '2021-12-01' and vencimento < '2021-12-30' " ;
			break;
		default:
			break;
		}
		return retorno;
		
	}
	
	
}
