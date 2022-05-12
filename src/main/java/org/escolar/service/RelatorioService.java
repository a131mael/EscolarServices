
package org.escolar.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.escolar.enums.TipoMobilidadeEnum;
import org.escolar.model.Aluno;
import org.escolar.model.Carro;
import org.escolar.util.Service;


@Stateless
public class RelatorioService extends Service {


	@PersistenceContext(unitName = "EscolarDS")
	private EntityManager em;

	public double getTotalNotasEmitidas(int mes){
		try{
			StringBuilder sql = new StringBuilder();
			sql.append(" select sum(valorPago) from boleto ");
			sql.append("where ");
			sql.append("nfsEnviada = true ");
			sql.append("and vencimento >  ");
			sql.append(getInicioMes(mes));
			sql.append(" and vencimento < ");
			sql.append(getFimMes(mes));
			
			Query query = em.createNativeQuery(sql.toString());
			Double t = (Double) query.getSingleResult();
			
			return t;	
		}catch(NullPointerException e){
			return 0;
			
		}catch(Exception e){
			System.out.println(e);
			return 0d;
		}
	}
	
	public List<String> getResponsaveisNotasEnviadas(int mes){
		try{
			StringBuilder sql = new StringBuilder();
			sql.append(" select contrato.nomeresponsavel || ' (' || aluno.nomealuno  || ' )'  ");
			sql.append(" from boleto bol ");
			sql.append("left join contratoaluno contrato ");
			sql.append("on contrato.id = bol.contrato_id ");
			sql.append("left join aluno aluno ");
			sql.append("on bol.pagador_id = aluno.id ");
			
			sql.append("where ");
			sql.append("nfsEnviada = true ");
			sql.append("and vencimento >  ");
			sql.append(getInicioMes(mes));
			sql.append(" and vencimento < ");
			sql.append(getFimMes(mes));
			
			Query query = em.createNativeQuery(sql.toString());
			List<String> t = (List<String>) query.getResultList();
			
			return t;	
		}catch(Exception e){
			System.out.println(e);
			return new ArrayList<>();
		}
	}
	
	private String getInicioMes(int mesDoAno) {
		String mes = "2022-01-01";
		switch (mesDoAno) {

		case 12:
			mes = "'2022-12-01'";
			break;

		case 11:
			mes = "'2022-11-01'";
			break;

		case 10:
			mes = "'2022-10-01'";
			break;

		case 9:
			mes = "'2022-09-01'";
			break;

		case 8:
			mes = "'2022-08-01'";
			break;

		case 7:
			mes = "'2022-07-01'";
			break;

		case 6:
			mes = "'2022-06-01'";
			break;

		case 5:
			mes = "'2022-05-01'";
			break;

		case 4:
			mes = "'2022-04-01'";
			break;

		case 3:
			mes = "'2022-03-01'";
			break;

		case 2:
			mes = "'2022-02-01'";
			break;

		case 1:
			mes = "'2022-01-01'";
			break;

		default:
			mes = "'2022-12-01'";
			break;
		}

		return mes;
	}
	
	private String getFimMes(int mesDoAno) {
		String mes = "2022-01-31";
		switch (mesDoAno) {

		case 12:
			mes = "'2022-12-31'";
			break;

		case 11:
			mes = "'2022-11-30'";
			break;

		case 10:
			mes = "'2022-10-31'";
			break;

		case 9:
			mes = "'2022-09-30'";
			break;

		case 8:
			mes = "'2022-08-31'";
			break;

		case 7:
			mes = "'2022-07-31'";
			break;

		case 6:
			mes = "'2022-06-30'";
			break;

		case 5:
			mes = "'2022-05-31'";
			break;

		case 4:
			mes = "'2022-04-30'";
			break;

		case 3:
			mes = "'2022-03-31'";
			break;

		case 2:
			mes = "'2022-02-28'";
			break;

		case 1:
			mes = "'2022-01-31'";
			break;

		default:
			mes = "'2022-12-31'";
			break;
		}

		return mes;
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
						pred = cb.and(pred, cb.like(member.<String> get(entry.getKey()), "%" + entry.getValue() + "%"));
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


	public long countCriancasCarro(Map<String, Object> filtros) {
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
						pred = cb.and(pred, cb.like(member.<String> get(entry.getKey()), "%" + entry.getValue() + "%"));
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
	
	@SuppressWarnings("unchecked")
	public long getValorTotalMensalidade(Map<String, Object> filtros) {
		long total = 0;
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
				//cq.where(pred);
			}
			

			cq.where(cb.or(predicates.toArray(new Predicate[predicates.size()])));
			Query q = em.createQuery(criteria);
			List<Aluno> alunos = (List<Aluno>) q.getResultList();
			
			for(Aluno al :alunos){
				if(al.getIdaVolta() == 0){
					if(!al.isTrocaIDA()){
						if(al.getCarroLevaParaEscola() != null && al.getCarroLevaParaEscola().equals(filtros.get("carroLevaParaEscola"))){
							total+= (al.getContratoVigente().getValorMensal())/2;
						}
					}else{
						if(al.getCarroLevaParaEscola()!= null && al.getCarroLevaParaEscola().equals(filtros.get("carroLevaParaEscola"))){
							total+= (al.getContratoVigente().getValorMensal())/4;
						}
						
						if(al.getCarroLevaParaEscolaTroca() != null && al.getCarroLevaParaEscolaTroca().equals(filtros.get("carroLevaParaEscolaTroca"))){
							total+= (al.getContratoVigente().getValorMensal())/4;
						}
					}
					
					if(!al.isTrocaVolta()){
						if(al.getCarroPegaEscola() != null && al.getCarroPegaEscola().equals(filtros.get("carroPegaEscola"))){
							total+= (al.getContratoVigente().getValorMensal())/2;
						}
					}else{
						if(al.getCarroPegaEscola() != null && al.getCarroPegaEscola().equals(filtros.get("carroPegaEscola"))){
							total+= (al.getContratoVigente().getValorMensal())/4;
						}
						
						if(al.getCarroPegaEscolaTroca() != null && al.getCarroPegaEscolaTroca().equals(filtros.get("carroPegaEscolaTroca"))){
							total+= (al.getContratoVigente().getValorMensal())/4;
						}
					}
					
							
					
				//SOMENTE IDA
				}else if(al.getIdaVolta() == 1){
					if(!al.isTrocaIDA()){
						if(al.getCarroLevaParaEscola() != null && al.getCarroLevaParaEscola().equals(filtros.get("carroLevaParaEscola"))){
							total+= (al.getContratoVigente().getValorMensal());
						}
					}else{
						if(al.getCarroLevaParaEscola() != null && al.getCarroLevaParaEscola().equals(filtros.get("carroLevaParaEscola"))){
							total+= (al.getContratoVigente().getValorMensal())/2;
						}
						
						if(al.getCarroLevaParaEscolaTroca() != null && al.getCarroLevaParaEscolaTroca().equals(filtros.get("carroLevaParaEscolaTroca"))){
							total+= (al.getContratoVigente().getValorMensal())/2;
						}
					}
				//SOMENTE Volta
				}else{
					if(!al.isTrocaVolta()){
						if(al.getCarroPegaEscola() != null && al.getCarroPegaEscola().equals(filtros.get("carroPegaEscola"))){
							total+= (al.getContratoVigente().getValorMensal());
						}
					}else{
						if(al.getCarroPegaEscola() != null && al.getCarroPegaEscola().equals(filtros.get("carroPegaEscola"))){
							total+= (al.getContratoVigente().getValorMensal())/2;
						}
						
						if(al.getCarroPegaEscolaTroca() != null && al.getCarroPegaEscolaTroca().equals(filtros.get("carroPegaEscolaTroca"))){
							total+= (al.getContratoVigente().getValorMensal())/2;
						}
					}
				}
			}
			
			return total;

		} catch (NoResultException nre) {
			return 0;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}

	}
	
	@SuppressWarnings("unchecked")
	public long getValorTotal(Map<String, Object> filtros) {
		long total = 0;
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
				//cq.where(pred);
			}

			cq.where(cb.or(predicates.toArray(new Predicate[predicates.size()])));
			Query q = em.createQuery(criteria);
			List<Aluno> alunos = (List<Aluno>) q.getResultList();
			
			for(Aluno al :alunos){
				if(al != null){
				if(al.getIdaVolta() == 0){
					if(!al.isTrocaIDA()){
						if(al.getCarroLevaParaEscola() != null && al.getCarroLevaParaEscola().equals(filtros.get("carroLevaParaEscola"))){
							if(al == null){
								System.out.println("al ta nulo");
							}else if(al.getContratoVigente().getNumeroParcelas() == null){
								System.out.println("O aluno : " + al.getNomeAluno() +al.getCodigo() + "  Nao tem o valor do contrato"  + "- " + al.getId());
							}else{
								total+= (al.getContratoVigente().getValorMensal()*al.getContratoVigente().getNumeroParcelas())/2;
							}
							
						}
					}else{
						if(al.getCarroLevaParaEscola()!= null && al.getCarroLevaParaEscola().equals(filtros.get("carroLevaParaEscola"))){
							if(al == null){
								System.out.println("al ta nulo");
							}else if(al.getContratoVigente().getNumeroParcelas() == null){
								System.out.println("O aluno : " + al.getNomeAluno() +al.getCodigo() + "  Nao tem o valor do contrato"  + "- " + al.getId());
							}else{
								total+= (al.getContratoVigente().getValorMensal()*al.getContratoVigente().getNumeroParcelas())/4;
							}
						}
						
						if(al.getCarroLevaParaEscolaTroca() != null && al.getCarroLevaParaEscolaTroca() != null && al.getCarroLevaParaEscolaTroca().equals(filtros.get("carroLevaParaEscolaTroca"))){
							if(al == null){
								System.out.println("al ta nulo");
							}else if(al.getContratoVigente().getNumeroParcelas() == null){
								System.out.println("O aluno : " + al.getNomeAluno() +al.getCodigo() + "  Nao tem o valor do contrato"  + "- " + al.getId());
							}else{
								total+= (al.getContratoVigente().getValorMensal()*al.getContratoVigente().getNumeroParcelas())/4;
							}
						}
					}
					
					if(!al.isTrocaVolta()){
						if(al.getCarroPegaEscola() != null && al.getCarroPegaEscola().equals(filtros.get("carroPegaEscola"))){
							if(al == null){
								System.out.println("al ta nulo");
							}else if(al.getContratoVigente().getNumeroParcelas() == null){
								System.out.println("O aluno : " + al.getNomeAluno() +al.getCodigo() + "  Nao tem o valor do contrato"  + "- " + al.getId());
							}else{
								total+= (al.getContratoVigente().getValorMensal()*al.getContratoVigente().getNumeroParcelas())/2;
							}
						}
					}else{
						if(al.getCarroPegaEscola() != null && al.getCarroPegaEscola().equals(filtros.get("carroPegaEscola"))){
							if(al == null){
								System.out.println("al ta nulo");
							}else if(al.getContratoVigente().getNumeroParcelas() == null){
								System.out.println("O aluno : " + al.getNomeAluno() +al.getCodigo() + "  Nao tem o valor do contrato"  + "- " + al.getId());
							}else{
								total+= (al.getContratoVigente().getValorMensal()*al.getContratoVigente().getNumeroParcelas())/4;
								
							}
						}
						
						if(al.getCarroPegaEscolaTroca() != null && al.getCarroPegaEscolaTroca().equals(filtros.get("carroPegaEscolaTroca"))){
							if(al == null){
								System.out.println("al ta nulo");
							}else if(al.getContratoVigente().getNumeroParcelas() == null){
								System.out.println("O aluno : " + al.getNomeAluno() +al.getCodigo() + "  Nao tem o valor do contrato");
							}else{
								total+= (al.getContratoVigente().getValorMensal()*al.getContratoVigente().getNumeroParcelas())/4;
							}
						}
					}
					
							
					
				//SOMENTE IDA
				}else if(al.getIdaVolta() == 1){
					if(!al.isTrocaIDA()){
						if(al.getCarroLevaParaEscola() != null && al.getCarroLevaParaEscola().equals(filtros.get("carroLevaParaEscola"))){
							total+= (al.getContratoVigente().getValorMensal()*al.getContratoVigente().getNumeroParcelas());
						}
					}else{
						if(al.getCarroLevaParaEscola() != null && al.getCarroLevaParaEscola().equals(filtros.get("carroLevaParaEscola"))){
							total+= (al.getContratoVigente().getValorMensal()*al.getContratoVigente().getNumeroParcelas())/2;
						}
						
						if(al.getCarroLevaParaEscolaTroca() != null && al.getCarroLevaParaEscolaTroca().equals(filtros.get("carroLevaParaEscolaTroca"))){
							total+= (al.getContratoVigente().getValorMensal()*al.getContratoVigente().getNumeroParcelas())/2;
						}
					}
				//SOMENTE Volta
				}else{
					if(!al.isTrocaVolta()){
						if(al.getCarroPegaEscola() != null && al.getCarroPegaEscola().equals(filtros.get("carroPegaEscola"))){
							total+= (al.getContratoVigente().getValorMensal()*al.getContratoVigente().getNumeroParcelas());
						}
					}else{
						if(al.getCarroPegaEscola() != null && al.getCarroPegaEscola().equals(filtros.get("carroPegaEscola"))){
							total+= (al.getContratoVigente().getValorMensal()*al.getContratoVigente().getNumeroParcelas())/2;
						}
						
						if(al.getCarroPegaEscolaTroca() != null && al.getCarroPegaEscolaTroca().equals(filtros.get("carroPegaEscolaTroca"))){
							total+= (al.getContratoVigente().getValorMensal()*al.getContratoVigente().getNumeroParcelas())/2;
						}
					}
				}
			}
			}
			
			return total;

		} catch (NoResultException nre) {
			return 0;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}

	}

	public double getValor(Map<String, Object> filtros){
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT sum(al.valorMensal) from  Aluno al ");
		sql.append("where 1=1 ");
		sql.append("and al.removido != true");
		
//		if(filtros.size()>0){
//			sql.append("where 1=1 and ");
//			
//		}
//		
//		if(filtros.containsKey("carroLevaParaEscola")){
//			sql.append(" al.carroLevaParaEscola.id = ");
//			sql.append(((Carro)filtros.get("carroLevaParaEscola")).getId());
//			
//		}

		
		/*filtros.put("carroLevaParaEscola", carro);
		filtros.put("carroLevaParaEscolaTroca", carro);
		filtros.put("carroPegaEscola", carro);
		filtros.put("carroPegaEscolaTroca", carro);*/
		
		Query query = em.createQuery(sql.toString());

		try {
			 return (double) query.getSingleResult();
		}catch(Exception e){
			
		}
		return 0;
	}
	
	public double countAlunos(Map<String, Object> filtros){
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT count(al) from  Aluno al ");
//		if(filtros.size()>0){
//			sql.append("where 1=1 and ");
//			
//		}
//		
//		if(filtros.containsKey("carroLevaParaEscola")){
//			sql.append(" al.carroLevaParaEscola.id = ");
//			sql.append(((Carro)filtros.get("carroLevaParaEscola")).getId());
//			
//		}

		
		/*filtros.put("carroLevaParaEscola", carro);
		filtros.put("carroLevaParaEscolaTroca", carro);
		filtros.put("carroPegaEscola", carro);
		filtros.put("carroPegaEscolaTroca", carro);*/
		
		Query query = em.createQuery(sql.toString());

		try {
			 return (double) query.getSingleResult();
		}catch(Exception e){
			
		}
		return 0;
	}


	public double getValorTotalMensalidade(Carro carro,TipoMobilidadeEnum tipo) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT sum(al.valorMensal) from  Aluno al ");
		sql.append("where 1=1 ");
		
		switch (tipo) {
		case SO_VAI:
			sql.append(" and carrolevaparaescola_id = ");
			sql.append(carro.getId());
			sql.append(" and carrolevaparaescolatroca_id is ");
			sql.append(" null ");
			sql.append(" and carropegaescola_id is ");
			sql.append(" null ");
			sql.append(" and carropegaescolatroca_id is ");
			sql.append(" null ");
			break;
			
		case SO_VAI_TROCA_IDA:
			sql.append(" and ((");
			
			sql.append(" carrolevaparaescola_id = ");
			sql.append(carro.getId());
			sql.append(" and carrolevaparaescolatroca_id != ");
			sql.append(carro.getId());
			sql.append(" and carropegaescola_id is ");
			sql.append(" null ");
			sql.append(" and carropegaescolatroca_id is ");
			sql.append(" null ) ");
			
			sql.append(" or (");
			sql.append(" carrolevaparaescola_id != ");
			sql.append(carro.getId());
			sql.append(" and carrolevaparaescolatroca_id = ");
			sql.append(carro.getId());
			sql.append(" and carropegaescola_id is ");
			sql.append(" null ");
			sql.append(" and carropegaescolatroca_id is ");
			sql.append(" null ");			
			sql.append(" ))");
			break;

		default:
			break;
		}
		
		
		if(tipo.equals(TipoMobilidadeEnum.SO_VOLTA)){
			sql.append(" and carrolevaparaescola_id is ");
			sql.append(" null");
			sql.append(" and carrolevaparaescolatroca_id is ");
			sql.append(" null ");
			sql.append(" and  carropegaescola_id = ");
			sql.append(carro.getId());
			sql.append(" and carropegaescolatroca_id is ");
			sql.append(" null ");
		}
		
		if(tipo.equals(TipoMobilidadeEnum.SO_VOLTA_TROCA_VOLTA)){
			sql.append(" and carrolevaparaescola_id is ");
			sql.append(" null");
			sql.append(" and carrolevaparaescolatroca_id is ");
			sql.append(" null ");
			sql.append(" and carropegaescola_id = ");
			sql.append(carro.getId());
			sql.append(" and carropegaescolatroca_id is not");
			sql.append(" null ");
		}
		
		if(tipo.equals(TipoMobilidadeEnum.SO_VOLTA_TROCA_VOLTA)){
			sql.append(" and carrolevaparaescola_id is ");
			sql.append(" null");
			sql.append(" and carrolevaparaescolatroca_id is ");
			sql.append(" null ");
			sql.append(" and carropegaescola_id is not ");
			sql.append(" null");
			sql.append(" and carropegaescolatroca_id = ");
			sql.append(carro.getId());
		}
		
		if(tipo.equals(TipoMobilidadeEnum.VAI_VOLTA_MESMO_CARRO)){
			sql.append(" and carrolevaparaescola_id = ");
			sql.append(carro.getId());
			sql.append(" and carrolevaparaescolatroca_id is ");
			sql.append(" null ");
			sql.append(" and carropegaescola_id = ");
			sql.append(carro.getId());
			sql.append(" and carropegaescolatroca_id is ");
			sql.append(" null ");
		}
		
		if(tipo.equals(TipoMobilidadeEnum.VAI_VOLTA_IDA_CARRO_DIFERENTE)){
			sql.append(" and carrolevaparaescola_id = ");
			sql.append(carro.getId());
			sql.append(" and carrolevaparaescolatroca_id is ");
			sql.append(" null ");
			sql.append(" and carropegaescola_id != ");
			sql.append(carro.getId());
			sql.append(" and carropegaescolatroca_id is ");
			sql.append(" null ");
		}
		
		if(tipo.equals(TipoMobilidadeEnum.VAI_VOLTA_IDA_COM_TROCA_IDA)){
			sql.append(" and carrolevaparaescola_id = ");
			sql.append(carro.getId());
			sql.append(" and carrolevaparaescolatroca_id is not");
			sql.append(" null ");
			sql.append(" and carropegaescola_id != ");
			sql.append(carro.getId());
			sql.append(" and carropegaescolatroca_id is ");
			sql.append(" null ");
		}
		
		if(tipo.equals(TipoMobilidadeEnum.VAI_VOLTA_IDA_COM_TROCA_IDA)){
			sql.append(" and carrolevaparaescola_id is not");
			sql.append(" null" );
			sql.append(" and carrolevaparaescolatroca_id = ");
			sql.append(carro.getId());
			sql.append(" and carropegaescola_id != ");
			sql.append(carro.getId());
			sql.append(" and carropegaescolatroca_id is ");
			sql.append(" null ");
		}
		
		if(tipo.equals(TipoMobilidadeEnum.VAI_VOLTA_IDA_COM_TROCA_VOLTA)){
			sql.append(" and carrolevaparaescola_id =");
			sql.append(carro.getId());
			sql.append(" and carrolevaparaescolatroca_id is ");
			sql.append(" null");
			sql.append(" and carropegaescola_id != ");
			sql.append(carro.getId());
			sql.append(" and carropegaescolatroca_id != ");
			sql.append(carro.getId());
		}
		
		if(tipo.equals(TipoMobilidadeEnum.VAI_VOLTA_IDA_COM_TROCA_VOLTA)){
			sql.append(" and carrolevaparaescola_id =");
			sql.append(carro.getId());
			sql.append(" and carrolevaparaescolatroca_id is ");
			sql.append(" null");
			sql.append(" and carropegaescola_id = ");
			sql.append(carro.getId());
			sql.append(" and carropegaescolatroca_id != ");
			sql.append(carro.getId());
		}
		
		if(tipo.equals(TipoMobilidadeEnum.VAI_VOLTA_IDA_COM_TROCA_VOLTA)){
			sql.append(" and carrolevaparaescola_id =");
			sql.append(carro.getId());
			sql.append(" and carrolevaparaescolatroca_id is ");
			sql.append(" null");
			sql.append(" and carropegaescola_id != ");
			sql.append(carro.getId());
			sql.append(" and carropegaescolatroca_id = ");
			sql.append(carro.getId());
		}
		
		if(tipo.equals(TipoMobilidadeEnum.VAI_VOLTA_IDA_COM_TROCA_IDA_E_COM_TROCA_VOLTA)){
			sql.append(" and carrolevaparaescola_id =");
			sql.append(carro.getId());
			sql.append(" and carrolevaparaescolatroca_id is not ");
			sql.append(" null");
			sql.append(" and carropegaescola_id != ");
			sql.append(carro.getId());
			sql.append(" and carropegaescolatroca_id != ");
			sql.append(carro.getId());
		}
		
		if(tipo.equals(TipoMobilidadeEnum.VAI_VOLTA_IDA_COM_TROCA_IDA_E_COM_TROCA_VOLTA)){
			sql.append(" and carrolevaparaescola_id is not ");
			sql.append(" null");
			sql.append(" and carrolevaparaescolatroca_id =  ");
			sql.append(carro.getId());
			sql.append(" and carropegaescola_id != ");
			sql.append(carro.getId());
			sql.append(" and carropegaescolatroca_id != ");
			sql.append(carro.getId());
		}
		
		if(tipo.equals(TipoMobilidadeEnum.VAI_VOLTA_VOLTA_CARRO_DIFERENTE)){
			sql.append(" and carrolevaparaescola_id != ");
			sql.append(carro.getId());
			sql.append(" and carrolevaparaescolatroca_id is  ");
			sql.append(" null");
			sql.append(" and carropegaescola_id = ");
			sql.append(carro.getId());
			sql.append(" and carropegaescolatroca_id is ");
			sql.append(" null");
		}
		
		if(tipo.equals(TipoMobilidadeEnum.VAI_VOLTA_VOLTA_COM_TROCA_IDA)){
			sql.append(" and carrolevaparaescola_id != ");
			sql.append(carro.getId());
			sql.append(" and carrolevaparaescolatroca_id != ");
			sql.append(carro.getId());
			sql.append(" and carropegaescola_id = ");
			sql.append(carro.getId());
			sql.append(" and carropegaescolatroca_id is ");
			sql.append(" null");
		}
		
		if(tipo.equals(TipoMobilidadeEnum.VAI_VOLTA_VOLTA_COM_TROCA_IDA)){
			sql.append(" and carrolevaparaescola_id = ");
			sql.append(carro.getId());
			sql.append(" and carrolevaparaescolatroca_id != ");
			sql.append(carro.getId());
			sql.append(" and carropegaescola_id = ");
			sql.append(carro.getId());
			sql.append(" and carropegaescolatroca_id is ");
			sql.append(" null");
		}
		
		if(tipo.equals(TipoMobilidadeEnum.VAI_VOLTA_VOLTA_COM_TROCA_IDA)){
			sql.append(" and carrolevaparaescola_id != ");
			sql.append(carro.getId());
			sql.append(" and carrolevaparaescolatroca_id = ");
			sql.append(carro.getId());
			sql.append(" and carropegaescola_id = ");
			sql.append(carro.getId());
			sql.append(" and carropegaescolatroca_id is ");
			sql.append(" null");
		}
		
		if(tipo.equals(TipoMobilidadeEnum.VAI_VOLTA_VOLTA_COM_TROCA_VOLTA)){
			sql.append(" and carrolevaparaescola_id != ");
			sql.append(carro.getId());
			sql.append(" and carrolevaparaescolatroca_id is  ");
			sql.append(" null");
			sql.append(" and carropegaescola_id = ");
			sql.append(carro.getId());
			sql.append(" and carropegaescolatroca_id != ");
			sql.append(carro.getId());
		}
		
		if(tipo.equals(TipoMobilidadeEnum.VAI_VOLTA_VOLTA_COM_TROCA_VOLTA)){
			sql.append(" and carrolevaparaescola_id != ");
			sql.append(carro.getId());
			sql.append(" and carrolevaparaescolatroca_id is  ");
			sql.append(" null");
			sql.append(" and carropegaescola_id != ");
			sql.append(carro.getId());
			sql.append(" and carropegaescolatroca_id = ");
			sql.append(carro.getId());
		}
		
		if(tipo.equals(TipoMobilidadeEnum.VAI_VOLTA_VOLTA_COM_TROCA_IDA_E_COM_TROCA_VOLTA)){
			sql.append(" and carrolevaparaescola_id != ");
			sql.append(carro.getId());
			sql.append(" and carrolevaparaescolatroca_id !=  ");
			sql.append(carro.getId());
			sql.append(" and carropegaescola_id != ");
			sql.append(carro.getId());
			sql.append(" and carropegaescolatroca_id = ");
			sql.append(carro.getId());
		}
		
		if(tipo.equals(TipoMobilidadeEnum.VAI_VOLTA_VOLTA_COM_TROCA_IDA_E_COM_TROCA_VOLTA)){
			sql.append(" and carrolevaparaescola_id != ");
			sql.append(carro.getId());
			sql.append(" and carrolevaparaescolatroca_id !=  ");
			sql.append(carro.getId());
			sql.append(" and carropegaescola_id = ");
			sql.append(carro.getId());
			sql.append(" and carropegaescolatroca_id != ");
			sql.append(carro.getId());
		}
		if(tipo.equals(TipoMobilidadeEnum.VAI_VOLTA_VOLTA_COM_TROCA_IDA_E_COM_TROCA_VOLTA)){
			sql.append(" and carrolevaparaescola_id = ");
			sql.append(carro.getId());
			sql.append(" and carrolevaparaescolatroca_id !=  ");
			sql.append(carro.getId());
			sql.append(" and carropegaescola_id = ");
			sql.append(carro.getId());
			sql.append(" and carropegaescolatroca_id != ");
			sql.append(carro.getId());
		}
		
		if(tipo.equals(TipoMobilidadeEnum.VAI_VOLTA_VOLTA_COM_TROCA_IDA_E_COM_TROCA_VOLTA)){
			sql.append(" and carrolevaparaescola_id = ");
			sql.append(carro.getId());
			sql.append(" and carrolevaparaescolatroca_id !=  ");
			sql.append(carro.getId());
			sql.append(" and carropegaescola_id != ");
			sql.append(carro.getId());
			sql.append(" and carropegaescolatroca_id = ");
			sql.append(carro.getId());
		}
		
		if(tipo.equals(TipoMobilidadeEnum.VAI_VOLTA_VOLTA_COM_TROCA_IDA_E_COM_TROCA_VOLTA)){
			sql.append(" and carrolevaparaescola_id != ");
			sql.append(carro.getId());
			sql.append(" and carrolevaparaescolatroca_id =  ");
			sql.append(carro.getId());
			sql.append(" and carropegaescola_id != ");
			sql.append(carro.getId());
			sql.append(" and carropegaescolatroca_id = ");
			sql.append(carro.getId());
		}
		
		if(tipo.equals(TipoMobilidadeEnum.VAI_VOLTA_VOLTA_COM_TROCA_IDA_E_COM_TROCA_VOLTA)){
			sql.append(" and carrolevaparaescola_id != ");
			sql.append(carro.getId());
			sql.append(" and carrolevaparaescolatroca_id =  ");
			sql.append(carro.getId());
			sql.append(" and carropegaescola_id = ");
			sql.append(carro.getId());
			sql.append(" and carropegaescolatroca_id != ");
			sql.append(carro.getId());
		}
		
		sql.append(" and al.removido != true");
		
		Query query = em.createQuery(sql.toString());

		try {
			 return (double) query.getSingleResult();
		}catch(Exception e){
			
		}
		return 0;
	}
	
}

