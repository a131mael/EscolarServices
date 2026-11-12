
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
import org.escolar.model.PedidoCancelamento;
import org.escolar.util.Service;


@Stateless
public class RelatorioService extends Service {


	@PersistenceContext(unitName = "EscolarDS")
	private EntityManager em;

	public List<PedidoCancelamento> getPedidosCancelamentoPendentes(){
		try{
			StringBuilder sql = new StringBuilder();
			sql.append(" select pc.id, a.nomealuno, ca.numero, ca.ano, ca.nomeresponsavel, pc.motivo, pc.aluno_id, ");
			sql.append(" to_char(pc.data_pedido, 'DD/MM/YYYY'), pc.data_ultimo_uso, pc.valor_multa, bm.vencimento, ");
			sql.append(" (select string_agg( ");
			sql.append("    (case when x.posicao = 1 then 'Mes atual' else 'Aviso' end) || ': R$ ' || ");
			sql.append("    to_char(b.valornominal, 'FM999999990.00') || ' (' || to_char(b.vencimento,'MM/YYYY') || ')', ");
			sql.append("    ' + ' order by x.posicao) ");
			sql.append("  from unnest(pc.boletos_mensalidade_ids) with ordinality as x(bid, posicao) ");
			sql.append("  join boleto b on b.id = x.bid) as detalhe_mensalidades ");
			sql.append(" from pedido_cancelamento_contrato pc ");
			sql.append(" join contratoaluno ca on ca.id = pc.contrato_id ");
			sql.append(" join aluno a on a.id = pc.aluno_id ");
			sql.append(" left join boleto bm on bm.id = pc.boleto_multa_id ");
			sql.append(" where coalesce(ca.cancelado, false) = false ");
			sql.append(" order by pc.data_pedido desc ");

			Query query = em.createNativeQuery(sql.toString());
			List<Object[]> linhas = query.getResultList();

			List<PedidoCancelamento> pedidos = new ArrayList<>();
			for (Object[] l : linhas) {
				PedidoCancelamento p = new PedidoCancelamento();
				p.setId(l[0] == null ? null : ((Number) l[0]).longValue());
				p.setNomeAluno((String) l[1]);
				p.setNumeroContrato(l[2] == null ? "" : String.valueOf(l[2]));
				p.setAno(l[3] == null ? "" : String.valueOf(l[3]));
				p.setNomeResponsavel((String) l[4]);
				p.setMotivo((String) l[5]);
				p.setAlunoId(l[6] == null ? null : ((Number) l[6]).longValue());
				p.setDataPedido(l[7] == null ? "" : l[7].toString());
				p.setDataUltimoUso(l[8] == null ? "" : l[8].toString());
				String vencMulta = l[10] == null ? "" : " (" + l[10].toString().substring(0, 7) + ")";
				p.setValorMulta(l[9] == null ? "-" : ("R$ " + l[9].toString() + vencMulta));
				p.setDetalheMensalidades(l[11] == null ? "-" : (String) l[11]);
				pedidos.add(p);
			}
			return pedidos;
		}catch(Exception e){
			System.out.println(e);
			return new ArrayList<>();
		}
	}

	/** Histórico completo de pedidos de cancelamento (log_pedido_cancelamento, nunca é
	 * apagado) — mostra tanto os já confirmados (contrato cancelado de verdade) quanto os
	 * ainda em aberto, com todos os detalhes (motivo, multa, datas). */
	public List<PedidoCancelamento> getHistoricoCancelamentos(){
		try{
			StringBuilder sql = new StringBuilder();
			sql.append(" select lpc.id, lpc.nome_aluno, ca.numero, ca.ano, lpc.nome_responsavel, lpc.motivo, lpc.aluno_id, ");
			sql.append(" to_char(lpc.data_pedido, 'DD/MM/YYYY'), coalesce(lpc.data_ultimo_uso, to_char(lpc.pode_usar_ate,'DD/MM/YYYY')), lpc.valor_multa, bm.vencimento, ");
			sql.append(" (select string_agg( ");
			sql.append("    (case when x.posicao = 1 then 'Mes atual' else 'Aviso' end) || ': R$ ' || ");
			sql.append("    to_char(b.valornominal, 'FM999999990.00') || ' (' || to_char(b.vencimento,'MM/YYYY') || ')', ");
			sql.append("    ' + ' order by x.posicao) ");
			sql.append("  from unnest(lpc.boletos_mensalidade_ids) with ordinality as x(bid, posicao) ");
			sql.append("  join boleto b on b.id = x.bid) as detalhe_mensalidades, ");
			sql.append(" coalesce(ca.cancelado, false) as confirmado, to_char(ca.datacancelamento, 'DD/MM/YYYY') ");
			sql.append(" from log_pedido_cancelamento lpc ");
			sql.append(" join contratoaluno ca on ca.id = lpc.contrato_id ");
			sql.append(" left join boleto bm on bm.id = lpc.boleto_multa_id ");
			sql.append(" order by lpc.data_pedido desc ");

			Query query = em.createNativeQuery(sql.toString());
			List<Object[]> linhas = query.getResultList();

			List<PedidoCancelamento> pedidos = new ArrayList<>();
			for (Object[] l : linhas) {
				PedidoCancelamento p = new PedidoCancelamento();
				p.setId(l[0] == null ? null : ((Number) l[0]).longValue());
				p.setNomeAluno((String) l[1]);
				p.setNumeroContrato(l[2] == null ? "" : String.valueOf(l[2]));
				p.setAno(l[3] == null ? "" : String.valueOf(l[3]));
				p.setNomeResponsavel((String) l[4]);
				p.setMotivo((String) l[5]);
				p.setAlunoId(l[6] == null ? null : ((Number) l[6]).longValue());
				p.setDataPedido(l[7] == null ? "" : l[7].toString());
				p.setDataUltimoUso(l[8] == null ? "" : l[8].toString());
				String vencMulta = l[10] == null ? "" : " (" + l[10].toString().substring(0, 7) + ")";
				p.setValorMulta(l[9] == null ? "-" : ("R$ " + l[9].toString() + vencMulta));
				p.setDetalheMensalidades(l[11] == null ? "-" : (String) l[11]);
				boolean confirmado = l[12] != null && (Boolean) l[12];
				p.setStatus(confirmado ? "Confirmado" : "Em aberto");
				p.setDataConfirmacao(confirmado && l[13] != null ? l[13].toString() : "-");
				pedidos.add(p);
			}
			return pedidos;
		}catch(Exception e){
			System.out.println(e);
			return new ArrayList<>();
		}
	}

	/** Remove o pedido de cancelamento pendente (ex: cliente se arrependeu). Não mexe no
	 * histórico permanente (log_pedido_cancelamento, no Portal), só tira da fila pendente. */
	public void descancelarPedido(Long pedidoId){
		try{
			Query query = em.createNativeQuery("delete from pedido_cancelamento_contrato where id = :id");
			query.setParameter("id", pedidoId);
			query.executeUpdate();
		}catch(Exception e){
			System.out.println(e);
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

