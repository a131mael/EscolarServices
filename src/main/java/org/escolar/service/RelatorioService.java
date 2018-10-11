
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
							total+= (al.getValorMensal())/2;
						}
					}else{
						if(al.getCarroLevaParaEscola()!= null && al.getCarroLevaParaEscola().equals(filtros.get("carroLevaParaEscola"))){
							total+= (al.getValorMensal())/4;
						}
						
						if(al.getCarroLevaParaEscolaTroca() != null && al.getCarroLevaParaEscolaTroca().equals(filtros.get("carroLevaParaEscolaTroca"))){
							total+= (al.getValorMensal())/4;
						}
					}
					
					if(!al.isTrocaVolta()){
						if(al.getCarroPegaEscola() != null && al.getCarroPegaEscola().equals(filtros.get("carroPegaEscola"))){
							total+= (al.getValorMensal())/2;
						}
					}else{
						if(al.getCarroPegaEscola() != null && al.getCarroPegaEscola().equals(filtros.get("carroPegaEscola"))){
							total+= (al.getValorMensal())/4;
						}
						
						if(al.getCarroPegaEscolaTroca() != null && al.getCarroPegaEscolaTroca().equals(filtros.get("carroPegaEscolaTroca"))){
							total+= (al.getValorMensal())/4;
						}
					}
					
							
					
				//SOMENTE IDA
				}else if(al.getIdaVolta() == 1){
					if(!al.isTrocaIDA()){
						if(al.getCarroLevaParaEscola() != null && al.getCarroLevaParaEscola().equals(filtros.get("carroLevaParaEscola"))){
							total+= (al.getValorMensal());
						}
					}else{
						if(al.getCarroLevaParaEscola() != null && al.getCarroLevaParaEscola().equals(filtros.get("carroLevaParaEscola"))){
							total+= (al.getValorMensal())/2;
						}
						
						if(al.getCarroLevaParaEscolaTroca() != null && al.getCarroLevaParaEscolaTroca().equals(filtros.get("carroLevaParaEscolaTroca"))){
							total+= (al.getValorMensal())/2;
						}
					}
				//SOMENTE Volta
				}else{
					if(!al.isTrocaVolta()){
						if(al.getCarroPegaEscola() != null && al.getCarroPegaEscola().equals(filtros.get("carroPegaEscola"))){
							total+= (al.getValorMensal());
						}
					}else{
						if(al.getCarroPegaEscola() != null && al.getCarroPegaEscola().equals(filtros.get("carroPegaEscola"))){
							total+= (al.getValorMensal())/2;
						}
						
						if(al.getCarroPegaEscolaTroca() != null && al.getCarroPegaEscolaTroca().equals(filtros.get("carroPegaEscolaTroca"))){
							total+= (al.getValorMensal())/2;
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
							}else if(al.getNumeroParcelas() == null){
								System.out.println("O aluno : " + al.getNomeAluno() +al.getCodigo() + "  Nao tem o valor do contrato"  + "- " + al.getId());
							}else{
								total+= (al.getValorMensal()*al.getNumeroParcelas())/2;
							}
							
						}
					}else{
						if(al.getCarroLevaParaEscola()!= null && al.getCarroLevaParaEscola().equals(filtros.get("carroLevaParaEscola"))){
							if(al == null){
								System.out.println("al ta nulo");
							}else if(al.getNumeroParcelas() == null){
								System.out.println("O aluno : " + al.getNomeAluno() +al.getCodigo() + "  Nao tem o valor do contrato"  + "- " + al.getId());
							}else{
								total+= (al.getValorMensal()*al.getNumeroParcelas())/4;
							}
						}
						
						if(al.getCarroLevaParaEscolaTroca() != null && al.getCarroLevaParaEscolaTroca() != null && al.getCarroLevaParaEscolaTroca().equals(filtros.get("carroLevaParaEscolaTroca"))){
							if(al == null){
								System.out.println("al ta nulo");
							}else if(al.getNumeroParcelas() == null){
								System.out.println("O aluno : " + al.getNomeAluno() +al.getCodigo() + "  Nao tem o valor do contrato"  + "- " + al.getId());
							}else{
								total+= (al.getValorMensal()*al.getNumeroParcelas())/4;
							}
						}
					}
					
					if(!al.isTrocaVolta()){
						if(al.getCarroPegaEscola() != null && al.getCarroPegaEscola().equals(filtros.get("carroPegaEscola"))){
							if(al == null){
								System.out.println("al ta nulo");
							}else if(al.getNumeroParcelas() == null){
								System.out.println("O aluno : " + al.getNomeAluno() +al.getCodigo() + "  Nao tem o valor do contrato"  + "- " + al.getId());
							}else{
								total+= (al.getValorMensal()*al.getNumeroParcelas())/2;
							}
						}
					}else{
						if(al.getCarroPegaEscola() != null && al.getCarroPegaEscola().equals(filtros.get("carroPegaEscola"))){
							if(al == null){
								System.out.println("al ta nulo");
							}else if(al.getNumeroParcelas() == null){
								System.out.println("O aluno : " + al.getNomeAluno() +al.getCodigo() + "  Nao tem o valor do contrato"  + "- " + al.getId());
							}else{
								total+= (al.getValorMensal()*al.getNumeroParcelas())/4;
								
							}
						}
						
						if(al.getCarroPegaEscolaTroca() != null && al.getCarroPegaEscolaTroca().equals(filtros.get("carroPegaEscolaTroca"))){
							if(al == null){
								System.out.println("al ta nulo");
							}else if(al.getNumeroParcelas() == null){
								System.out.println("O aluno : " + al.getNomeAluno() +al.getCodigo() + "  Nao tem o valor do contrato");
							}else{
								total+= (al.getValorMensal()*al.getNumeroParcelas())/4;
							}
						}
					}
					
							
					
				//SOMENTE IDA
				}else if(al.getIdaVolta() == 1){
					if(!al.isTrocaIDA()){
						if(al.getCarroLevaParaEscola() != null && al.getCarroLevaParaEscola().equals(filtros.get("carroLevaParaEscola"))){
							total+= (al.getValorMensal()*al.getNumeroParcelas());
						}
					}else{
						if(al.getCarroLevaParaEscola() != null && al.getCarroLevaParaEscola().equals(filtros.get("carroLevaParaEscola"))){
							total+= (al.getValorMensal()*al.getNumeroParcelas())/2;
						}
						
						if(al.getCarroLevaParaEscolaTroca() != null && al.getCarroLevaParaEscolaTroca().equals(filtros.get("carroLevaParaEscolaTroca"))){
							total+= (al.getValorMensal()*al.getNumeroParcelas())/2;
						}
					}
				//SOMENTE Volta
				}else{
					if(!al.isTrocaVolta()){
						if(al.getCarroPegaEscola() != null && al.getCarroPegaEscola().equals(filtros.get("carroPegaEscola"))){
							total+= (al.getValorMensal()*al.getNumeroParcelas());
						}
					}else{
						if(al.getCarroPegaEscola() != null && al.getCarroPegaEscola().equals(filtros.get("carroPegaEscola"))){
							total+= (al.getValorMensal()*al.getNumeroParcelas())/2;
						}
						
						if(al.getCarroPegaEscolaTroca() != null && al.getCarroPegaEscolaTroca().equals(filtros.get("carroPegaEscolaTroca"))){
							total+= (al.getValorMensal()*al.getNumeroParcelas())/2;
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

