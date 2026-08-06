
package org.escolar.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
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
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.xml.registry.infomodel.User;

import org.escolar.enums.FormaPagamentoEnum;
import org.escolar.model.Carro;
import org.escolar.model.CarroFrete;
import org.escolar.model.Contratante;
import org.escolar.model.Frete;
import org.escolar.model.Funcionario;
import org.escolar.util.Service;

@Stateless
public class FreteService extends Service {

	@PersistenceContext(unitName = "EscolarDS")
	private EntityManager em;

	public Frete findById(EntityManager em, Long id) {
		return em.find(Frete.class, id);
	}

	public Frete findById(Long id) {
		Frete f = em.find(Frete.class, id);
		f.getCarroFrete().size();
		f.getPassageiros().size();
		return f;
	}

	public Frete findByCodigo(Long id) {
		return em.find(Frete.class, id);
	}

	public String remover(Long idFrete) {
		Frete frete = findById(idFrete);
		if (frete.getCarroFrete() != null) {
			for (CarroFrete cf : frete.getCarroFrete()) {
				em.remove(cf);
			}
		}
		em.remove(frete);
		em.flush();
		return "index";
	}

	public List<Frete> findAll() {
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Frete> criteria = cb.createQuery(Frete.class);
			Root<Frete> member = criteria.from(Frete.class);
			// Swap criteria statements if you would like to try out type-safe
			// criteria queries, a new
			// feature in JPA 2.0
			// criteria.select(member).orderBy(cb.asc(member.get(Member_.name)));
			criteria.select(member).orderBy(cb.asc(member.get("id")));
			List<Frete> fretes = em.createQuery(criteria).getResultList();
			for (Frete f : fretes) {
				f.getCarroFrete().size();
				f.getPassageiros().size();
			}
			fretes.sort(Comparator.comparing(Frete::getHorarioLocalOrigem,
					Comparator.nullsLast(Comparator.<Date>reverseOrder())));
			return fretes;

		} catch (NoResultException nre) {
			return new ArrayList<>();
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}

	/** Janeiro = 1 */
	public double getTotalRecebidoMes(int mes) {
		try{
			StringBuilder sql = new StringBuilder();
			sql.append(" select sum(valorPago) from frete ");
			sql.append("where ");
			sql.append("1 = 1 ");
			sql.append("and horariolocalorigem >  ");
			sql.append(getInicioMes(mes));
			sql.append(" and horariolocalorigem < ");
			sql.append(getFimMes(mes));
			
			Query query = em.createNativeQuery(sql.toString());
			Double t = (Double) query.getSingleResult();
			
			return t;	
		}catch(NullPointerException e){
			return 0;
		}catch(Exception e){
			System.out.println(e);
			return 0;
		}
	}

	/** Janeiro = 1 */
	public double getTotalPagoMotoristaMes(int mes) {
		try{
			StringBuilder sql = new StringBuilder();
			sql.append(" select sum(valorPagoMotorista) from frete ");
			sql.append("where ");
			sql.append("1 = 1 ");
			sql.append("and horariolocalorigem >  ");
			sql.append(getInicioMes(mes));
			sql.append(" and horariolocalorigem < ");
			sql.append(getFimMes(mes));
			
			Query query = em.createNativeQuery(sql.toString());
			Double t = (Double) query.getSingleResult();
			
			return t;	
		}catch(NullPointerException e){
			return 0;
		}catch(Exception e){
			System.out.println(e);
			return 0;
		}
	}
	
	/** Janeiro = 1 */
	public double getTotalPagoMotorista() {
		try{
			StringBuilder sql = new StringBuilder();
			sql.append(" select sum(valorPagoMotorista) from frete ");
			sql.append("where ");
			sql.append("1 = 1 ");
			sql.append("and horariolocalorigem >  ");
			sql.append("'2022-01-01'");
			sql.append(" and horariolocalorigem < ");
			sql.append("'2022-12-31'");
			
			Query query = em.createNativeQuery(sql.toString());
			Double t = (Double) query.getSingleResult();
			
			return t;	
		}catch(NullPointerException e){
			return 0;
		}catch(Exception e){
			System.out.println(e);
			return 0;
		}
	}
	
	/** Janeiro = 1 */
	public double getTotalRecebidoFormaPagamento(FormaPagamentoEnum forma) {
		try{
			StringBuilder sql = new StringBuilder();
			sql.append(" select sum(valorPago) from frete ");
			sql.append("where ");
			sql.append("1 = 1 ");
			sql.append("and horariolocalorigem >  ");
			sql.append("'2022-01-01'");
			sql.append(" and horariolocalorigem < ");
			sql.append("'2022-12-31'");
			sql.append(" and formapagamento = ");
			sql.append(forma.ordinal());
			
			Query query = em.createNativeQuery(sql.toString());
			Double t = (Double) query.getSingleResult();
			
			return t;	
		}catch(NullPointerException e){
			return 0;
		}catch(Exception e){
			System.out.println(e);
			return 0;
		}
	}
	
	public double getTotalGastoDinheiro(FormaPagamentoEnum forma) {
		try{
			StringBuilder sql = new StringBuilder();
			sql.append(" select sum(valor) from custo ");
			sql.append("where ");
			sql.append("1 = 1 ");
			sql.append("and data >  ");
			sql.append("'2022-01-01'");
			sql.append(" and data < ");
			sql.append("'2022-12-31'");
			sql.append(" and formapagamento = ");
			sql.append(forma.ordinal());
			
			Query query = em.createNativeQuery(sql.toString());
			Double t = (Double) query.getSingleResult();
			
			return t;	
		}catch(NullPointerException e){
			return 0;
		}catch(Exception e){
			System.out.println(e);
			return 0;
		}
	}

	/** Janeiro = 1 */
	public double getTotalRecebidoMesFormaPagamento(int mes,FormaPagamentoEnum forma) {
		try{
			StringBuilder sql = new StringBuilder();
			sql.append(" select sum(valorPago) from frete ");
			sql.append("where ");
			sql.append("1 = 1 ");
			sql.append("and horariolocalorigem >  ");
			sql.append(getInicioMes(mes));
			sql.append(" and horariolocalorigem < ");
			sql.append(getFimMes(mes));
			sql.append(" and formapagamento = ");
			sql.append(forma.ordinal());
			
			Query query = em.createNativeQuery(sql.toString());
			Double t = (Double) query.getSingleResult();
			
			return t;	
		}catch(NullPointerException e){
			return 0;
		}catch(Exception e){
			System.out.println(e);
			return 0;
		}
	}

	
	public Frete save(Frete evento) {
		Frete user = null;
		try {

			if (evento.getId() != null && evento.getId() != 0L) {
				user = findById(evento.getId());
			} else {
				user = new Frete();
			}
			
			if(evento.getContratante() != null) {
				Contratante c = saveContratante(evento.getContratante());
				user.setContratante(c);
			}

			if(evento.getMotorista() != null && evento.getMotorista().getId() != null) {
				Funcionario motorista = em.find(Funcionario.class, evento.getMotorista().getId());
				user.setMotorista(motorista);
			} else {
				user.setMotorista(null);
			}

			user.setDescricao(evento.getDescricao());
			user.setHorarioLocalOrigem(evento.getHorarioLocalOrigem());
			user.setHorarioParaRetorno(evento.getHorarioParaRetorno());
			user.setLocalDestino(evento.getLocalDestino());
			user.setLocalOrigem(evento.getLocalOrigem());
			user.setValor(evento.getValor());
			user.setValorPago(evento.getValorPago());
			user.setFormaPagamento(evento.getFormaPagamento());
			user.setValorPagoMotorista(evento.getValorPagoMotorista());
			user.setTpFretamento(evento.getTpFretamento());
			user.setCodMunOrigem(evento.getCodMunOrigem());
			user.setCodMunDestino(evento.getCodMunDestino());
			user.setNumeroCte(evento.getNumeroCte());
			user.setSerieCte(evento.getSerieCte());
			user.setChaveCte(evento.getChaveCte());
			user.setStatusCte(evento.getStatusCte());
			user.setXmlCteOS(evento.getXmlCteOS());
			user.setProtocoloCte(evento.getProtocoloCte());
			user.setMotivoCte(evento.getMotivoCte());
			user.setDataAutorizacaoCte(evento.getDataAutorizacaoCte());
			user.setQuilometragem(evento.getQuilometragem());
			user.setStatusLicencaScmobi(evento.getStatusLicencaScmobi());
			user.setNumeroLicencaScmobi(evento.getNumeroLicencaScmobi());
			user.setErroLicencaScmobi(evento.getErroLicencaScmobi());
			user.setJobIdScmobi(evento.getJobIdScmobi());
			user.setDataGeracaoLicencaScmobi(evento.getDataGeracaoLicencaScmobi());
			user.setLicencaScmobiPdf(evento.getLicencaScmobiPdf());
			user.setPassageirosScmobiPdf(evento.getPassageirosScmobiPdf());
			user.gerarToken();
			
			em.persist(user);
			em.flush();
			if (evento.getCarroFrete() != null) {
				for (CarroFrete cf : evento.getCarroFrete()) {
					Carro c = em.find(Carro.class, cf.getCarro().getId());
					cf.setFrete(user);
					cf.setCarro(c);
					if (cf.getId() == null) {
						em.persist(cf);
					} else {
						em.merge(cf);
					}
				}
			}

			user.setCarroFrete(evento.getCarroFrete());
			user.getPassageiros().size();

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
		return user;
	}

	public Frete findByCodigo(String codigo) {
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Frete> criteria = cb.createQuery(Frete.class);
			Root<Frete> member = criteria.from(Frete.class);

			Predicate whereSerie = null;

			whereSerie = cb.equal(member.get("codigo"), codigo);
			criteria.select(member).where(whereSerie);

			criteria.select(member);
			return em.createQuery(criteria).getSingleResult();

		} catch (NoResultException nre) {
			return null;
		} catch (Exception e) {
			return null;
		}
	}

	public Frete findByTokenPublico(String token) {
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Frete> criteria = cb.createQuery(Frete.class);
			Root<Frete> member = criteria.from(Frete.class);

			Predicate where = cb.equal(member.get("tokenPublico"), token);
			criteria.select(member).where(where);

			Frete f = em.createQuery(criteria).getSingleResult();
			f.getPassageiros().size();
			return f;

		} catch (NoResultException nre) {
			return null;
		} catch (Exception e) {
			return null;
		}
	}

	/** Fretes com geracao de licenca scMOBI em andamento (aguardando o microsservico concluir o job). */
	public List<Frete> findFretesComLicencaScmobiProcessando() {
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Frete> criteria = cb.createQuery(Frete.class);
			Root<Frete> member = criteria.from(Frete.class);

			Predicate where = cb.equal(member.get("statusLicencaScmobi"), "PROCESSANDO");
			criteria.select(member).where(where);

			return em.createQuery(criteria).getResultList();

		} catch (NoResultException nre) {
			return new ArrayList<>();
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}

	/** Marca a licenca scMOBI como concluida, salvando o numero do contrato e os PDFs gerados. */
	public void atualizarLicencaScmobiConcluida(Long freteId, Integer numeroContrato, byte[] licencaPdf,
			byte[] passageirosPdf, String dataGeracao) {
		Frete frete = em.find(Frete.class, freteId);
		frete.setStatusLicencaScmobi("CONCLUIDO");
		frete.setNumeroLicencaScmobi(numeroContrato);
		frete.setLicencaScmobiPdf(licencaPdf);
		frete.setPassageirosScmobiPdf(passageirosPdf);
		frete.setDataGeracaoLicencaScmobi(dataGeracao);
		frete.setErroLicencaScmobi(null);
		em.merge(frete);
		em.flush();
	}

	/** Marca a licenca scMOBI como erro, salvando a mensagem retornada pelo microsservico. */
	public void atualizarLicencaScmobiErro(Long freteId, String erro) {
		Frete frete = em.find(Frete.class, freteId);
		frete.setStatusLicencaScmobi("ERRO");
		frete.setErroLicencaScmobi(erro);
		em.merge(frete);
		em.flush();
	}

	public Contratante findContratanteByCPF(String cpf_CNPJ) {
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Contratante> criteria = cb.createQuery(Contratante.class);
			Root<Contratante> member = criteria.from(Contratante.class);

			Predicate whereSerie = null;

			whereSerie = cb.equal(member.get("CPF_CNPJ"), cpf_CNPJ);
			criteria.select(member).where(whereSerie);

			criteria.select(member);
			return em.createQuery(criteria).getSingleResult();

		} catch (NoResultException nre) {
			return null;
		} catch (Exception e) {
			return null;
		}

	}

	public Contratante saveContratante(Contratante contratante) {
		Contratante user = null;
		try {

			if (contratante.getId() != null && contratante.getId() != 0L) {
				user = em.find(Contratante.class, contratante.getId());
			} else {
				user = new Contratante();
			}

			user.setDataCadastro(new Date());
			user.setCPF_CNPJ(contratante.getCPF_CNPJ());
			user.setDescricao(contratante.getDescricao());
			user.setEmail(contratante.getEmail());
			user.setNome(contratante.getNome());
			user.setTelefone1(contratante.getTelefone1());
			user.setTelefone2(contratante.getTelefone2());
			user.setIe(contratante.getIe());
			user.setLogradouro(contratante.getLogradouro());
			user.setNumeroEndereco(contratante.getNumeroEndereco());
			user.setBairro(contratante.getBairro());
			user.setCodMunicipio(contratante.getCodMunicipio());
			user.setMunicipio(contratante.getMunicipio());
			user.setUf(contratante.getUf());
			user.setCep(contratante.getCep());

			if (user.getId() == null) {
				em.persist(user);
			} else {
				em.merge(user);
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

		em.flush();
		return user;
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
}
