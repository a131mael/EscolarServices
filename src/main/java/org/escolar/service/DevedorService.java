
package org.escolar.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
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

import org.aaf.financeiro.util.Util;
import org.escolar.enums.StatusBoletoEnum;
import org.escolar.model.Aluno;
import org.escolar.model.Boleto;
import org.escolar.model.ContratoAluno;
import org.escolar.model.Devedor;
import org.escolar.util.Service;
import org.escolar.util.UtilFinalizarAnoLetivo;
import org.escolar.util.Verificador;

@Stateless
public class DevedorService extends Service {

	@Inject
	private EventoService eventoService;

	@Inject
	private UtilFinalizarAnoLetivo finalizarAnoLetivo;

	@Inject
	private ConfiguracaoService configuracaoService;
	
	private Date dataInicio;
	
	private Date dataFim;

	@PersistenceContext(unitName = "EscolarDS")
	private EntityManager em;

	public Devedor findById(EntityManager em, Long id) {
		return em.find(Devedor.class, id);
	}

	public Aluno findById(Long id) {
		Aluno dev = em.find(Aluno.class, id);
		dev.getBoletos().size();
		return dev;
	}

	public ContratoAluno findByIdContratoAluno(Long id) {
		ContratoAluno dev = em.find(ContratoAluno.class, id);
		if(dev != null) {
			dev.getBoletos().size();
		}
		return dev;
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
			criteria.select(member).orderBy(cb.asc(member.get("nome")));

			List<Aluno> dvs = new ArrayList<>();
			for (Aluno dev : em.createQuery(criteria).getResultList()) {
				dev.getBoletos().size();
				dvs.add(dev);
			}

			return dvs;

		} catch (NoResultException nre) {
			return new ArrayList<>();
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}

	public Devedor save(Aluno aluno) {
		Aluno user = null;
		try {

			if (aluno.getId() != null && aluno.getId() != 0L) {
				user = findById(aluno.getId());
			}
			// user.setEnviadoParaCobrancaCDL(aluno.getEnviadoParaCobrancaCDL());
			// user.setNomeAluno(aluno.getNomeAluno());
			// user.setEndereco(aluno.getEndereco());
			// user.setBairro(aluno.getBairro());
			// user.setCep(aluno.getCep());
			// user.setCidade(aluno.getCidade());
			// user.setBairro(aluno.getBairro());
			// user.setCep(aluno.getCep());
			// user.setCidade(aluno.getCidade());
			// user.setCpf(aluno.getCpf());
			// user.setTelefoneCelular(aluno.getTelefoneCelular());
			// user.setTelefoneCelular2(aluno.getTelefoneCelular2());
			// user.setTelefoneResidencial(aluno.getTelefoneResidencial());
			// user.setEnviadoSPC(aluno.getEnviadoSPC());
			// user.setEnviadoParaCobrancaCDL(aluno.getEnviadoParaCobrancaCDL());
			// user.setContratoTerminado(aluno.getContratoTerminado());
			// user.setObservacao(aluno.getObservacao());
			/*
			 * List<Boleto> bs = new ArrayList<>(); if(aluno.getBoletos() !=
			 * null){ for(Boleto b : aluno.getBoletos()){ if(b.getNumero() !=
			 * null && !b.getNumero().equalsIgnoreCase("")){
			 * bs.add(getBoletoAttached(b)); } } }
			 * 
			 * if (aluno.getRemovido() == null) { user.setRemovido(false); }
			 * else { user.setRemovido(aluno.getRemovido()); }
			 * 
			 * 
			 * em.persist(user); user.setBoletos(bs);
			 */

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
		em.flush();
		return null;
	}

	private Boleto getBoletoAttached(Boleto boleto) {
		/*
		 * String numero = boleto.getNumero(); String numeroContrato =
		 * boleto.getNumeroContrato(); Date dataGeracao
		 * =boleto.getDataGeracao(); Double valor = boleto.getValor();
		 * 
		 * if(boleto.getId() != null){ boleto =em.find(Boleto.class,
		 * boleto.getId()); boleto.setDataGeracao(dataGeracao);
		 * boleto.setNumeroContrato(numeroContrato);
		 * boleto.setDataGeracao(dataGeracao); boleto.setValor(valor);
		 */
		/*
		 * } return boleto;
		 */
		return null;
	}

	public String remover(Long idDevedor) {
		/*
		 * Aluno al = findById(idDevedor); al.setRemovido(true); em.persist(al);
		 */
		return "ok";
	}

	public String restaurar(Long idDevedor) {
		/*
		 * Devedor al = findById(idDevedor); al.setRemovido(false);
		 * em.persist(al);
		 */
		return "ok";
	}
	
	public List<Aluno> findDevedor(Date dataInicio,Date dataFim){
		StringBuilder sql = new StringBuilder();
		sql.append("select al.* from aluno al ");   
		sql.append("left join boleto bol ");
		sql.append("on bol.pagador_id = al.id ");
		sql.append("where ");
		sql.append("bol.vencimento < '");
		sql.append(new Date());
		sql.append("'");
		if(dataInicio != null){
			sql.append(" and bol.vencimento > '");
			sql.append(dataInicio);
			sql.append("'");
		}
		
		if(dataFim != null){
			sql.append(" and bol.vencimento < '");
			sql.append(dataFim);
			sql.append("'");
		}
		sql.append(" and (bol.valorpago<bol.valornominal -20 or bol.valorpago is null)");
		sql.append(" and (bol.baixagerada is null or bol.baixagerada = false)");
		sql.append(" and (bol.baixamanual is null or bol.baixamanual = false)");
		sql.append(" and (bol.cancelado is null or bol.cancelado = false)");
		sql.append(" and (bol.dividaPerdoada is null or bol.dividaPerdoada = false)");
		
		
		Query query = em.createNativeQuery(sql.toString(),Aluno.class);
		List<Aluno> boletos = query.getResultList();
		if(boletos == null){
			boletos = new ArrayList<Aluno>();
		}
		boletos.size();

		List<Aluno> aux = new LinkedList<>();
		Set<Aluno> ds = new LinkedHashSet();
		for (Aluno d : (List<Aluno>) boletos) {
			d.getId();
			d.getNomeAluno();
			d.getContratos().size();
			if (d.getIrmao1() != null) {
				d.getIrmao1().getId();
			}
			if (d.getIrmao2() != null) {
				d.getIrmao2().getId();
			}
			if (d.getIrmao3() != null) {
				d.getIrmao3().getId();
			}
			if (d.getIrmao4() != null) {
				d.getIrmao4().getId();
			}

			boolean devedor = possuiContratoComBoletoAtrasado(d.getContratos(), dataInicio, dataFim);
			if (devedor) {
				ds.add(d);
			}
		}

		if (ds != null && !ds.isEmpty()) {
			aux.addAll(ds);
		}

		return aux;
	}
	
	public List<Aluno> findAtrasados(Date dataIncio,Date dataFim, String orderByParam, String orderParam, int first, int pageSize, Map<String, Object> where){
		StringBuilder sql = new StringBuilder();
		sql.append("select al.* from aluno al ");   
		sql.append("left join boleto bol ");
		sql.append("on bol.pagador_id = al.id ");
		sql.append("where ");
		sql.append("bol.vencimento < '");
		sql.append(new Date());
		sql.append("'");
		if(dataInicio != null){
			sql.append(" and bol.vencimento > '");
			sql.append(dataInicio);
			sql.append("'");
		}
		
		if(dataFim != null){
			sql.append(" and bol.vencimento < '");
			sql.append(dataFim);
			sql.append("'");
		}
		sql.append(" and (bol.valorpago<bol.valornominal -20 or bol.valorpago is null)");
		sql.append(" and (bol.baixagerada is null or bol.baixagerada = false)");
		sql.append(" and (bol.baixamanual is null or bol.baixamanual = false)");
		sql.append(" and (bol.cancelado is null or bol.cancelado = false)");
		sql.append(" and (bol.dividaPerdoada is null or bol.dividaPerdoada = false)");
		sql.append(" and (al.contactado is null or al.contactado = false) ");
		
		
		Query query = em.createNativeQuery(sql.toString(),Aluno.class);
		List<Aluno> boletos = query.getResultList();
		if(boletos == null){
			boletos = new ArrayList<Aluno>();
		}
		boletos.size();

		List<Aluno> aux = new LinkedList<>();
		Set<Aluno> ds = new LinkedHashSet();
		
		Set<Aluno> sAux = new LinkedHashSet();
		sAux.addAll(boletos);
		List<Aluno> mAux = new LinkedList<>();
		mAux.addAll(sAux);
		
		for (Aluno d :  sAux) {
			d.getId();
			d.getNomeAluno();
			d.getContratos().size();
			if (d.getIrmao1() != null) {
				d.getIrmao1().getId();
			}
			if (d.getIrmao2() != null) {
				d.getIrmao2().getId();
			}
			if (d.getIrmao3() != null) {
				d.getIrmao3().getId();
			}
			if (d.getIrmao4() != null) {
				d.getIrmao4().getId();
			}
			
			boolean devedor = possuiContratoComBoletoAtrasado(d, d.getContratos(), dataInicio, dataFim);
			if(d.getId()==10118){
				System.out.println("a");
			}
			if (devedor) {
				ds.add(d);
			}
		}

		if (ds != null && !ds.isEmpty()) {
			aux.addAll(ds);
			Collections.sort(aux, new Comparator<Aluno>() {
			    @Override
			    public int compare(Aluno o1, Aluno o2) {
			        return Integer.valueOf(o2.getQuantidadeMesAtrasado()).compareTo( o1.getQuantidadeMesAtrasado());
			    }
			});
		}
		
		int ultimoIndice = first+pageSize;
		if(ultimoIndice > aux.size()){
			ultimoIndice = aux.size();
		}
		return aux.subList(first, ultimoIndice);
	}
	
	public List<Aluno> findAtrasadosContactado(Date dataInicio,Date dataFim, String orderByParam, String orderParam, int first, int pageSize, Map<String, Object> where){
		StringBuilder sql = new StringBuilder();
		sql.append("select al.* from aluno al ");   
		sql.append("left join boleto bol ");
		sql.append("on bol.pagador_id = al.id ");
		sql.append("where ");
		sql.append("bol.vencimento < '");
		sql.append(new Date());
		sql.append("'");
		if(dataInicio != null){
			sql.append(" and bol.vencimento > '");
			sql.append(dataInicio);
			sql.append("'");
		}
		
		if(dataFim != null){
			sql.append(" and bol.vencimento < '");
			sql.append(dataFim);
			sql.append("'");
		}
		sql.append(" and (bol.valorpago<bol.valornominal -20 or bol.valorpago is null)");
		sql.append(" and (bol.baixagerada is null or bol.baixagerada = false)");
		sql.append(" and (bol.baixamanual is null or bol.baixamanual = false)");
		sql.append(" and (bol.cancelado is null or bol.cancelado = false)");
		sql.append(" and (bol.dividaPerdoada is null or bol.dividaPerdoada = false)");
		sql.append(" and (al.contactado = true) ");
		
		
		Query query = em.createNativeQuery(sql.toString(),Aluno.class);
		List<Aluno> boletos = query.getResultList();
		if(boletos == null){
			boletos = new ArrayList<Aluno>();
		}
		boletos.size();

		List<Aluno> aux = new LinkedList<>();
		Set<Aluno> ds = new LinkedHashSet();
		
		Set<Aluno> sAux = new LinkedHashSet();
		sAux.addAll(boletos);
		List<Aluno> mAux = new LinkedList<>();
		mAux.addAll(sAux);
		
		for (Aluno d :  sAux) {
			d.getId();
			d.getNomeAluno();
			d.getContratos().size();
			if (d.getIrmao1() != null) {
				d.getIrmao1().getId();
			}
			if (d.getIrmao2() != null) {
				d.getIrmao2().getId();
			}
			if (d.getIrmao3() != null) {
				d.getIrmao3().getId();
			}
			if (d.getIrmao4() != null) {
				d.getIrmao4().getId();
			}
			
			boolean devedor = possuiContratoComBoletoAtrasado(d, d.getContratos(), dataInicio, dataFim);
			if (devedor) {
				ds.add(d);
			}
		}

		if (ds != null && !ds.isEmpty()) {
			aux.addAll(ds);
			Collections.sort(aux, new Comparator<Aluno>() {
			    @Override
			    public int compare(Aluno o1, Aluno o2) {
			        return Integer.valueOf(o2.getQuantidadeMesAtrasado()).compareTo( o1.getQuantidadeMesAtrasado());
			    }
			});
		}
		
		int ultimoIndice = first+pageSize;
		if(ultimoIndice > aux.size()){
			ultimoIndice = aux.size();
		}
		return aux.subList(first, ultimoIndice);
	}
	
	private boolean possuiContratoComBoletoAtrasado(List<ContratoAluno> contratos, Date dataInicio2, Date dataFim2) {
		return possuiContratoComBoletoAtrasado(null,contratos,dataInicio2,dataFim2);
	}

	
	private boolean possuiContratoComBoletoAtrasado(Aluno aluno ,List<ContratoAluno> contratos, Date dataInicio2, Date dataFim2) {
		
		boolean atrasadoB = false;
		boolean verdade = false;
		
		for (ContratoAluno contrato : contratos) {
			contrato.getBoletos().size();

			if (!(contrato.getProtestado() != null && contrato.getProtestado())) {
				atrasadoB = possuiBoletoAtrasado(aluno,contrato, dataInicio, dataFim);
			}
			if(atrasadoB){
				verdade = true;
			}
		}

		return verdade;
	}

	private boolean possuiBoletoAtrasado(ContratoAluno contrato, Date dataInicio2, Date dataFim2) {
		return possuiBoletoAtrasado(null,contrato,dataFim2,dataFim2);
	}

	private boolean possuiBoletoAtrasado(Aluno aluno, ContratoAluno contrato, Date dataInicio, Date dataFim) {
		boolean atrasado = false;
		
		for (Boleto b : contrato.getBoletos()) {

			if (dataInicio != null && dataFim != null) {
				if (dataInicio.before(b.getVencimento()) && dataFim.after(b.getVencimento())) {
					if (Verificador.getStatusEnum(b).equals(StatusBoletoEnum.ATRASADO)) {
						b.setAtrasado(true);
						atrasado = true;
						contrato.setAtrasado(true);
					} else {
						b.setAtrasado(false);
					}

				}

			} else if (dataInicio == null && dataFim != null) {
				if (dataFim.after(b.getVencimento())) {
					if (Verificador.getStatusEnum(b).equals(StatusBoletoEnum.ATRASADO)) {

						b.setAtrasado(true);
						atrasado = true;
						contrato.setAtrasado(true);
					} else {
						b.setAtrasado(false);
					}

				}

			} else if (dataInicio != null && dataFim == null) {
				if (dataInicio.before(b.getVencimento())) {
					if (Verificador.getStatusEnum(b).equals(StatusBoletoEnum.ATRASADO)) {

						b.setAtrasado(true);
						atrasado = true;
						contrato.setAtrasado(true);
					} else {
						b.setAtrasado(false);
					}

				}
			} else {
				if (Verificador.getStatusEnum(b).equals(StatusBoletoEnum.ATRASADO)) {

					b.setAtrasado(true);
					atrasado = true;
					contrato.setAtrasado(true);
				} else {
					b.setAtrasado(false);
				}
			}
			if(aluno != null){
				if(b.getAtrasado()){
					if(aluno.getValorTotalDevido() == null){
						aluno.setValorTotalDevido(0D);
					}
					if(aluno.getMesesAtrasados() == null){
						aluno.setMesesAtrasados("");
					}

					double valorTotalDevido = aluno.getValorTotalDevido() + Verificador.getValorFinal(b);
					String mesesAtrasados = aluno.getMesesAtrasados() + (b.getVencimento().getMonth() +1) +", ";
					int quantidadeMesAtrasado = aluno.getQuantidadeMesAtrasado() + 1;
					
					aluno.setValorTotalDevido(valorTotalDevido);
					aluno.setMesesAtrasados(mesesAtrasados);
					aluno.setQuantidadeMesAtrasado(quantidadeMesAtrasado);
					aluno.setNomeResponsavel(contrato.getNomeResponsavel());
					initIrmaos(aluno);
				}				
			}
			
		}
		
		
		return atrasado;
	}

	private void initIrmaos(Aluno al){
		if(al.getIrmao1() != null){
			al.getIrmao1().getId();
		}
		if(al.getIrmao2() != null){
			al.getIrmao2().getId();
		}
		if(al.getIrmao4() != null){
			al.getIrmao4().getId();
		}
		if(al.getIrmao4() != null){
			al.getIrmao4().getId();
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<Aluno> find(int first, int size, String orderBy, String order, Map<String, Object> filtros,Date dataInicio,Date dataFim) {
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Aluno> criteria = cb.createQuery(Aluno.class);
			Root<Aluno> member = criteria.from(Aluno.class);
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
			// q.setMaxResults(size);
			boolean atrasado = false;
			List<Aluno> ds = new LinkedList<>();
			for (Aluno d : (List<Aluno>) q.getResultList()) {
				for (ContratoAluno contrato : d.getContratos()) {
					
					contrato.getBoletos().size();
					for (Boleto b : contrato.getBoletos()) {
						
						if (dataInicio != null  && dataFim != null) {
							if(dataInicio.before(b.getVencimento()) && dataFim.after(b.getVencimento())){
								if (Verificador.getStatusEnum(b).equals(StatusBoletoEnum.ATRASADO)) {
									b.setAtrasado(true);
									atrasado = true;
									contrato.setAtrasado(true);
								} else {
									b.setAtrasado(false);
								}
	
							}
							
						}else if (dataInicio == null  && dataFim != null) {
							if(dataFim.after(b.getVencimento())){
								if (Verificador.getStatusEnum(b).equals(StatusBoletoEnum.ATRASADO)) {

									b.setAtrasado(true);
									atrasado = true;
									contrato.setAtrasado(true);
								} else {
									b.setAtrasado(false);
								}
	
							}
							
						}else if (dataInicio != null  && dataFim == null) {
							if(dataInicio.before(b.getVencimento())){
								if (Verificador.getStatusEnum(b).equals(StatusBoletoEnum.ATRASADO)) {

									b.setAtrasado(true);
									atrasado = true;
									contrato.setAtrasado(true);
								} else {
									b.setAtrasado(false);
								}
	
							}
						}else{
							if (Verificador.getStatusEnum(b).equals(StatusBoletoEnum.ATRASADO)) {

								b.setAtrasado(true);
								atrasado = true;
								contrato.setAtrasado(true);
							} else {
								b.setAtrasado(false);
							}
						}
					}

					if(contrato.getProtestado() != null && contrato.getProtestado()){
						atrasado = false;
					}
				}
				if (atrasado) {
					
					ds.add(d);
				}
				atrasado = false;
			}

			return ds;

		} catch (NoResultException nre) {
			return new ArrayList<>();
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}

	}

	@SuppressWarnings("unchecked")
	public List<Aluno> findAnoLetivo(int first, int size, String orderBy, String order, Map<String, Object> filtros) {
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Aluno> criteria = cb.createQuery(Aluno.class);
			Root<Aluno> member = criteria.from(Aluno.class);
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
			// q.setMaxResults(size);
			boolean atrasado = false;
			List<Aluno> ds = new LinkedList<>();
			for (Aluno d : (List<Aluno>) q.getResultList()) {
				for (ContratoAluno contrato : d.getContratos()) {
					contrato.getBoletos().size();
					if (contrato.getAno() == configuracaoService.getConfiguracao().getAnoLetivo()) {
						contrato.getBoletos().size();
						for (Boleto b : contrato.getBoletos()) {
							if (Verificador.getStatusEnum(b).equals(StatusBoletoEnum.ATRASADO)) {
								b.setAtrasado(true);
								atrasado = true;
								contrato.setAtrasado(true);

							} else {
								b.setAtrasado(false);
							}
						}
					}
				}
				if (atrasado) {

					ds.add(d);
				}
				atrasado = false;
			}

			return ds;

		} catch (NoResultException nre) {
			return new ArrayList<>();
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}

	}

	@SuppressWarnings("unchecked")
	public List<Aluno> findAnoPassado(int first, int size, String orderBy, String order, Map<String, Object> filtros) {
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Aluno> criteria = cb.createQuery(Aluno.class);
			Root<Aluno> member = criteria.from(Aluno.class);
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
			// q.setMaxResults(size);
			boolean atrasado = false;
			List<Aluno> ds = new LinkedList<>();
			for (Aluno d : (List<Aluno>) q.getResultList()) {
				for (ContratoAluno contrato : d.getContratos()) {
					if (contrato.getAno() - 1 == configuracaoService.getConfiguracao().getAnoLetivo()) {
						contrato.getBoletos().size();
						for (Boleto b : contrato.getBoletos()) {
							if (Verificador.getStatusEnum(b).equals(StatusBoletoEnum.ATRASADO)) {
								b.setAtrasado(true);
								atrasado = true;
								contrato.setAtrasado(true);

							} else {
								b.setAtrasado(false);
							}
						}
					}

				}
				if (atrasado) {

					ds.add(d);
				}
				atrasado = false;
			}

			return ds;

		} catch (NoResultException nre) {
			return new ArrayList<>();
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}

	}

	@SuppressWarnings("unchecked")
	public List<Aluno> find2AnosOuMais(int first, int size, String orderBy, String order, Map<String, Object> filtros) {
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Aluno> criteria = cb.createQuery(Aluno.class);
			Root<Aluno> member = criteria.from(Aluno.class);
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
			// q.setMaxResults(size);
			boolean atrasado = false;
			List<Aluno> ds = new LinkedList<>();
			for (Aluno d : (List<Aluno>) q.getResultList()) {
				for (ContratoAluno contrato : d.getContratos()) {
					if (contrato.getAno() < configuracaoService.getConfiguracao().getAnoLetivo() - 1) {
						contrato.getBoletos().size();
						for (Boleto b : contrato.getBoletos()) {
							if (Verificador.getStatusEnum(b).equals(StatusBoletoEnum.ATRASADO)) {
								b.setAtrasado(true);
								atrasado = true;
								contrato.setAtrasado(true);

							} else {
								b.setAtrasado(false);
							}
						}
					}

				}
				if (atrasado) {

					ds.add(d);
				}
				atrasado = false;
			}

			return ds;

		} catch (NoResultException nre) {
			return new ArrayList<>();
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}

	}
	
	
	
	
	@SuppressWarnings("unchecked")
	public List<ContratoAluno> findProtesto(int first, int size, String orderBy, String order, Map<String, Object> filtros) {
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("select bol.* from boleto bol ");   
			sql.append("left join contratoaluno ca ");
			sql.append("on bol.contrato_id = ca.id ");
			sql.append("where ");
			sql.append("bol.vencimento < '");
			sql.append(new Date());
			sql.append("'");
			if(dataInicio != null){
				sql.append(" and bol.vencimento > '");
				sql.append(dataInicio);
				sql.append("'");
			}
			
			if(dataFim != null){
				sql.append(" and bol.vencimento < '");
				sql.append(dataFim);
				sql.append("'");
			}
			if(filtros != null){
				if(filtros.get("nomeResponsavel") != null){
					sql.append(" and ca.nomeResponsavel like '%");
					sql.append(filtros.get("nomeResponsavel"));
					sql.append("%'");
				}
				if(filtros.get("enviadoParaCobrancaCDL") != null){
					sql.append(" and ca.enviadoParaCobrancaCDL = ");
					sql.append(filtros.get("enviadoParaCobrancaCDL"));
				}
			}
			
			sql.append(" and (ca.confirmadoEnvioPorWebService is null or ca.confirmadoEnvioPorWebService = false)");
			
			sql.append(" and (bol.valorpago<bol.valornominal -20 or bol.valorpago is null)");
			sql.append(" and (bol.baixagerada is null or bol.baixagerada = false)");
			sql.append(" and (bol.baixamanual is null or bol.baixamanual = false)");
			sql.append(" and (bol.cancelado is null or bol.cancelado = false)");
			sql.append(" and (bol.dividaPerdoada is null or bol.dividaPerdoada = false)");
			sql.append(" and ca.protestado = true");
			sql.append(" and (ca.enviadoProtestoDefinitivo is null or ca.enviadoProtestoDefinitivo = false)");
			
			Query query = em.createNativeQuery(sql.toString(),Boleto.class);
			query.setFirstResult(first);
			List<Boleto> boletos = query.getResultList();
			if(boletos == null){
				boletos = new ArrayList<Boleto>();
			}
			
			Set<ContratoAluno> ds = new LinkedHashSet(); 
			List<ContratoAluno> aux = new ArrayList<>(); 
			for(Boleto b : boletos){
				b.getContrato().getId();
				b.getContrato().getBoletos().size();
				ds.add(b.getContrato());
				b.setAtrasado(true);
				
			}
			if(ds != null && !ds.isEmpty()){
				 aux.addAll(ds);
			}
			return aux;
		}catch(Exception e){
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<ContratoAluno> findProtestoConfirmado(int first, int size, String orderBy, String order, Map<String, Object> filtros) {
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("select bol.* from boleto bol ");   
			sql.append("left join contratoaluno ca ");
			sql.append("on bol.contrato_id = ca.id ");
			sql.append("where ");
			sql.append("bol.vencimento < '");
			sql.append(new Date());
			sql.append("'");
			if(dataInicio != null){
				sql.append(" and bol.vencimento > '");
				sql.append(dataInicio);
				sql.append("'");
			}
			
			if(dataFim != null){
				sql.append(" and bol.vencimento < '");
				sql.append(dataFim);
				sql.append("'");
			}
			if(filtros != null){
				if(filtros.get("nomeResponsavel") != null){
					sql.append(" and ca.nomeResponsavel like '%");
					sql.append(filtros.get("nomeResponsavel"));
					sql.append("%'");
				}
				if(filtros.get("enviadoParaCobrancaCDL") != null){
					sql.append(" and ca.enviadoParaCobrancaCDL = ");
					sql.append(filtros.get("enviadoParaCobrancaCDL"));
				}
			}
			
			sql.append(" and (ca.confirmadoEnvioPorWebService = true)");
			
			sql.append(" and (bol.valorpago<bol.valornominal -20 or bol.valorpago is null)");
			sql.append(" and (bol.baixagerada is null or bol.baixagerada = false)");
			sql.append(" and (bol.baixamanual is null or bol.baixamanual = false)");
			sql.append(" and (bol.cancelado is null or bol.cancelado = false)");
			sql.append(" and (bol.dividaPerdoada is null or bol.dividaPerdoada = false)");
			sql.append(" and ca.protestado = true");
			sql.append(" and (ca.enviadoProtestoDefinitivo is null or ca.enviadoProtestoDefinitivo = false)");
			
			Query query = em.createNativeQuery(sql.toString(),Boleto.class);
			query.setFirstResult(first);
			List<Boleto> boletos = query.getResultList();
			if(boletos == null){
				boletos = new ArrayList<Boleto>();
			}
			
			Set<ContratoAluno> ds = new LinkedHashSet(); 
			List<ContratoAluno> aux = new ArrayList<>(); 
			for(Boleto b : boletos){
				b.getContrato().getId();
				b.getContrato().getBoletos().size();
				ds.add(b.getContrato());
				b.setAtrasado(true);
				
			}
			if(ds != null && !ds.isEmpty()){
				 aux.addAll(ds);
			}
			return aux;
		}catch(Exception e){
			return null;
		}
	}
	

	@SuppressWarnings("unchecked")
	public List<ContratoAluno> find2(int first, int size, String orderBy, String order, Map<String, Object> filtros) {
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ContratoAluno> criteria = cb.createQuery(ContratoAluno.class);
			Root<ContratoAluno> member = criteria.from(ContratoAluno.class);
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
			// q.setMaxResults(size);
			boolean atrasado = false;
			List<ContratoAluno> ds = new LinkedList<>();
			for (ContratoAluno contrato : (List<ContratoAluno>) q.getResultList()) {
				contrato.getBoletos().size();
				for (Boleto b : contrato.getBoletos()) {
					if (Verificador.getStatusEnum(b).equals(StatusBoletoEnum.ATRASADO)) {
						b.setAtrasado(true);
						atrasado = true;

					} else {
						b.setAtrasado(false);
					}
				}
				if (atrasado) {
					ds.add(contrato);
				}
				atrasado = false;

			}

			return ds;

		} catch (NoResultException nre) {
			return new ArrayList<>();
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}

	}

	private boolean possuiBoletoAtrasado(Aluno al) {
		boolean atrasado = false;
		for(ContratoAluno ca : al.getContratos()){
			for (Boleto b : ca.getBoletos()) {
				if (Verificador.getStatusEnum(b).equals(StatusBoletoEnum.ATRASADO)) {
					b.setAtrasado(true);
					atrasado = true;

				} else {
					b.setAtrasado(false);
				}
			}
		}
	
		return atrasado;
	}

	public long count(Map<String, Object> filtros) {
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
			Root<Aluno> member = countQuery.from(Aluno.class);
			countQuery.select(cb.count(member));

			if (filtros != null) {
				for (Map.Entry<String, Object> entry : filtros.entrySet()) {

					Predicate pred = cb.and();
					if (entry.getValue() instanceof String) {
						pred = cb.and(pred, cb.like(member.<String> get(entry.getKey()), "%" + entry.getValue() + "%"));
					} else {
						pred = cb.equal(member.get(entry.getKey()), entry.getValue());
					}
					countQuery.where(pred);
				}

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
	
	public long countAtrasados(Map<String, Object> filtros) {
		return 100;
	}
	
	public long countContratoAluno(Map<String, Object> filtros) {
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
			Root<ContratoAluno> member = countQuery.from(ContratoAluno.class);
			countQuery.select(cb.count(member));

			if (filtros != null) {
				for (Map.Entry<String, Object> entry : filtros.entrySet()) {

					Predicate pred = cb.and();
					if (entry.getValue() instanceof String) {
						pred = cb.and(pred, cb.like(member.<String> get(entry.getKey()), "%" + entry.getValue() + "%"));
					} else {
						pred = cb.equal(member.get(entry.getKey()), entry.getValue());
					}
					countQuery.where(pred);
				}

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

	public void saveObservacao(Aluno alunod) {
		Aluno aluno = findById(alunod.getId());
		aluno.setObservacaoSecretaria(alunod.getObservacaoSecretaria());
		em.merge(aluno);
		em.flush();
	}

	public boolean isdevedor(Aluno aluno) {
		boolean atrasado = possuiBoletoAtrasado(aluno);
		return atrasado;
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public Date getDataFim() {
		return dataFim;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}

	public void enviarParaProtesto(ContratoAluno ca) {
		ContratoAluno caa = findByIdContratoAluno(ca.getId());
		caa.setProtestado(true);
		caa.setEnviadoParaCobrancaCDL(true);
		em.merge(caa);
		em.flush();
	}

	public void enviarPodeEnviarProtestoFina(ContratoAluno ca) {
		ContratoAluno caa = findByIdContratoAluno(ca.getId());
		caa.setPodeProtestarFinal(true);
		em.merge(caa);
		em.flush();
	}

	public void enviadoProtestoDefinitivo(ContratoAluno ca) {
		ContratoAluno caa = findByIdContratoAluno(ca.getId());
		caa.setEnviadoProtestoDefinitivo(true);
		em.merge(caa);
		em.flush();
	}

	public void saveComentario(ContratoAluno ca) {
		ContratoAluno ca2 = findByIdContratoAluno(ca.getId());
		ca2.setComentario(ca.getComentario());
		em.merge(ca2);
		em.flush();
		
	}

	@SuppressWarnings("unchecked")
	public List<ContratoAluno> findProtestoEnviado(int first, int size, String orderBy, String order, Map<String, Object> filtros) {
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("select bol.* from boleto bol ");   
			sql.append("left join contratoaluno ca ");
			sql.append("on bol.contrato_id = ca.id ");
			sql.append("where ");
			sql.append("bol.vencimento < '");
			sql.append(new Date());
			sql.append("'");
			if(dataInicio != null){
				sql.append(" and bol.vencimento > '");
				sql.append(dataInicio);
				sql.append("'");
			}
			
			if(dataFim != null){
				sql.append(" and bol.vencimento < '");
				sql.append(dataFim);
				sql.append("'");
			}
			sql.append(" and (bol.valorpago<bol.valornominal -20 or bol.valorpago is null)");
			sql.append(" and (bol.baixagerada is null or bol.baixagerada = false)");
			sql.append(" and (bol.baixamanual is null or bol.baixamanual = false)");
			sql.append(" and (bol.cancelado is null or bol.cancelado = false)");
			sql.append(" and ca.protestado = true");
			sql.append(" and (ca.enviadoProtestoDefinitivo = true)");
			
			Query query = em.createNativeQuery(sql.toString(),Boleto.class);
			query.setFirstResult(first);
			List<Boleto> boletos = query.getResultList();
			if(boletos == null){
				boletos = new ArrayList<Boleto>();
			}
			
			Set<ContratoAluno> ds = new LinkedHashSet(); 
			List<ContratoAluno> aux = new ArrayList<>(); 
			for(Boleto b : boletos){
				b.getContrato().getId();
				b.getContrato().getBoletos().size();
				ds.add(b.getContrato());
				b.setAtrasado(true);
				
			}
			if(ds != null && !ds.isEmpty()){
				 aux.addAll(ds);
			}
			return aux;
		}catch(Exception e){
			return null;
		}
	}

	public void enviarParaProtesto(Aluno al) {
		for(ContratoAluno ca : al.getContratos()){
			boolean atras = possuiBoletoAtrasado(ca, null, null);
			if(atras){
				ContratoAluno caa = findByIdContratoAluno(ca.getId());
				caa.setProtestado(true);
				caa.setEnviadoParaCobrancaCDL(true);
				em.merge(caa);
			}
		}
		em.flush();
	}

}
