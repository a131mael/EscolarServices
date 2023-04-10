package org.escolar.service;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.escolar.enums.CustoEnum;
import org.escolar.model.Custo;
import org.escolar.model.extrato.ItemExtrato;
import org.escolar.rotinasAutomaticas.CONSTANTES;
import org.escolar.util.Service;
import org.hibernate.validator.group.GroupSequenceProvider;

import br.com.aaf.base.constantes.ConstantesEXTRATO;
import br.com.aaf.base.importacao.extrato.ExtratoGruposPagamentoRecebimentoEnum;
import br.com.aaf.base.importacao.extrato.ExtratoTiposEntradaEnum;
import br.com.aaf.base.importacao.extrato.ExtratoTiposEntradaSaidaEnum;
import br.com.aaf.base.util.OfficeUtil;

@Stateless
public class ExtratoBancarioService extends Service {

	@PersistenceContext(unitName = "EscolarDS")
	private EntityManager em;

	public ItemExtrato findById(EntityManager em, Long id) {
		return em.find(ItemExtrato.class, id);
	}

	public ItemExtrato findById(Long id) {
		return em.find(ItemExtrato.class, id);
	}
	
	public void removerExtrato(ItemExtrato ie){
		for(ItemExtrato iee : ie.getItensFilhos()){
			iee.setItensFilhos(null);
			em.remove(findById(iee.getId()));
		}
		findById(ie.getId()).setItensFilhos(null);
		em.merge(findById(ie.getId()));
		removeVinculoPai(ie.getId());
		em.remove(findById(ie.getId()));
		em.flush();
	}
	
	
	private void removeVinculoPai(Long id){
		StringBuilder sql = new StringBuilder();
		sql.append("DELETE from itemextrato_itemextrato  ");
		sql.append(" where 1 = 1");
		sql.append(" and itensfilhos_id = ");
		sql.append(id);

		Query query = em.createNativeQuery(sql.toString());
		query.executeUpdate();
	}

	public void save(ItemExtrato id) {
		if (id.getId() != null) {
			edit(id);
		} else {
			
			int mes = getMesArquivoExtrato(id.getDataEvento()) + 1;
			int ano = getAnoArquivoExtrato(id.getDataEvento());
			id.setAno(ano);
			id.setMes(mes);
			if(ExtratoGruposPagamentoRecebimentoEnum.CONSERTO.equals(id.getGrupoRecebimento())
			   || ExtratoGruposPagamentoRecebimentoEnum.MANUTENCAO.equals(id.getGrupoRecebimento())		
			   || ExtratoGruposPagamentoRecebimentoEnum.PECA.equals(id.getGrupoRecebimento())
			){
				Custo c = new Custo();
				c.setData(id.getDataEvento());
				c.setDescricao(id.getObservacao());
				c.setTipoCusto(CustoEnum.MECANICA);
			}
			
			if(ExtratoGruposPagamentoRecebimentoEnum.SEGURO.equals(id.getGrupoRecebimento())){
				Custo c = new Custo();
				c.setData(id.getDataEvento());
				c.setDescricao(id.getObservacao());
				c.setTipoCusto(CustoEnum.SEGURO);
			}
			
			if(ExtratoGruposPagamentoRecebimentoEnum.LIMPEZA.equals(id.getGrupoRecebimento())){
				Custo c = new Custo();
				c.setData(id.getDataEvento());
				c.setDescricao(id.getObservacao());
				c.setTipoCusto(CustoEnum.LIMPEZA);
			}
			
			if(ExtratoGruposPagamentoRecebimentoEnum.SALARIO.equals(id.getGrupoRecebimento())){
				Custo c = new Custo();
				c.setData(id.getDataEvento());
				c.setDescricao(id.getObservacao());
				c.setTipoCusto(CustoEnum.SALARIO_MOTORISTA);
			}
			
			em.persist(id);
		}
		em.flush();
	}
	
	public Double count(Map<String, Object> filtros) {
		try {

			StringBuilder sql = new StringBuilder();
			sql.append("SELECT SUM(item.valor) from  ItemExtrato item ");
			sql.append("where 1=1 ");
			sql.append(" and item.mes = ");
			sql.append(filtros.get("mes"));
			sql.append(" and item.ano = ");
			sql.append(filtros.get("ano"));
			
			if(filtros.get("tipoEntradaSaida")!= null){
				sql.append(" and item.tipoEntradaSaida = ");
				ExtratoTiposEntradaSaidaEnum tipo =  (ExtratoTiposEntradaSaidaEnum) filtros.get("tipoEntradaSaida");
				sql.append(tipo.ordinal());
			}
			
			if(filtros.get("tipoEntrada")!= null){
				sql.append(" and item.tipoEntrada = ");
				ExtratoTiposEntradaEnum tipo =  (ExtratoTiposEntradaEnum) filtros.get("tipoEntrada");
				sql.append(tipo.ordinal());
			}
			
			if(filtros.get("grupoRecebimento")!= null){
				sql.append(" and item.grupoRecebimento = ");
				ExtratoGruposPagamentoRecebimentoEnum tipo =  (ExtratoGruposPagamentoRecebimentoEnum) filtros.get("grupoRecebimento");
				sql.append(tipo.ordinal());
			}
			
			if(filtros.get("dataEvento")!= null){
				sql.append(" and item.dataEvento < ");
				String dataFormata = OfficeUtil.dataFormatadaBanco((int)filtros.get("dia"),((int)filtros.get("mes")),(int)filtros.get("ano"));				
				sql.append("'");
				sql.append(dataFormata);
				sql.append("'");
			}
			
			Query query = em.createQuery(sql.toString());
			Object retorno = query.getSingleResult();
			if(retorno == null){
				return 0D;
			}else{
				return	(Double) retorno; 
				
			}
		} catch (NoResultException nre) {
			return 0D;
		} catch (Exception e) {
			e.printStackTrace();
			return 0D;
		}
	}

	public ItemExtrato getItemExtrato(int mes, int ano, String codigo) {
		ItemExtrato item;

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT item from  ItemExtrato item ");
		sql.append("where 1=1 ");
		sql.append(" and item.mes = ");
		sql.append(mes);
		sql.append(" and item.ano = ");
		sql.append(ano);
		sql.append(" and item.codigoEntrada = '");
		sql.append(codigo);
		sql.append("'");
		Query query = em.createQuery(sql.toString());

		item = (ItemExtrato) query.getSingleResult();

		return item;
	}

	public List<ItemExtrato> getItemExtrato(int mes, int ano) {
		List<ItemExtrato> item;

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT item from  ItemExtrato item ");
		sql.append("where 1=1 ");
		sql.append(" and item.mes = ");
		sql.append(mes);
		sql.append(" and item.ano = ");
		sql.append(ano);
		sql.append(" order by item.codigoEntrada asc");
		
		Query query = em.createQuery(sql.toString());

		item = (List<ItemExtrato>) query.getResultList();

		return item;
	}
	
	public List<ItemExtrato> getItemExtrato(int mes, int ano, int first, int size, Map<String, Object> filtros) {
		List<ItemExtrato> item;

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT item from  ItemExtrato item ");
		sql.append("where 1=1 ");
		sql.append(" and item.mes = ");
		sql.append(mes);
		sql.append(" and item.ano = ");
		sql.append(ano);
		if(filtros.get("tipoEntradaSaida")!= null){
			sql.append(" and item.tipoEntradaSaida = ");
			String tipo =  (String) filtros.get("tipoEntradaSaida");
			if(tipo.equalsIgnoreCase(ExtratoTiposEntradaSaidaEnum.ENTRADA.name())){
				sql.append(ExtratoTiposEntradaSaidaEnum.ENTRADA.ordinal());
			
			}else if(tipo.equalsIgnoreCase(ExtratoTiposEntradaSaidaEnum.SAIDA.name())){
				sql.append(ExtratoTiposEntradaSaidaEnum.SAIDA.ordinal());
			
			}else if(tipo.equalsIgnoreCase(ExtratoTiposEntradaSaidaEnum.ENTRADA_EXTRAORDINARIA.name())){
				sql.append(ExtratoTiposEntradaSaidaEnum.ENTRADA_EXTRAORDINARIA.ordinal());
			
			}else if(tipo.equalsIgnoreCase(ExtratoTiposEntradaSaidaEnum.APLICACAO.name())){
				sql.append(ExtratoTiposEntradaSaidaEnum.APLICACAO.ordinal());
			
			}else if(tipo.equalsIgnoreCase(ExtratoTiposEntradaSaidaEnum.ESTORNO.name())){
				sql.append(ExtratoTiposEntradaSaidaEnum.ESTORNO.ordinal());
			}
			
		}
		
		if(filtros.get("tipoEntrada")!= null){
			sql.append(" and item.tipoEntrada = ");
			String tipo =  (String) filtros.get("tipoEntrada");
			for(ExtratoTiposEntradaEnum tipoEnum : ExtratoTiposEntradaEnum.values()){
				if(tipoEnum.name().equalsIgnoreCase(tipo)){
					sql.append(tipoEnum.ordinal());
				}
			}
		}
		
		if(filtros.get("grupoRecebimento")!= null){
			sql.append(" and item.grupoRecebimento = ");
			String tipo =  (String) filtros.get("grupoRecebimento");
			for(ExtratoGruposPagamentoRecebimentoEnum tipoEnum : ExtratoGruposPagamentoRecebimentoEnum.values()){
				if(tipoEnum.name().equalsIgnoreCase(tipo)){
					sql.append(tipoEnum.ordinal());
				}
			}
		}
		
		
		sql.append(" order by item.codigoEntrada asc");
		
		Query query = em.createQuery(sql.toString());
		query.setFirstResult(first);
		query.setMaxResults(size+first);

		item = (List<ItemExtrato>) query.getResultList();

		return item;
	}

	private void edit(ItemExtrato itemAlterado) {
		ItemExtrato persistido = findById(itemAlterado.getId());
		
		if(persistido.getGrupoRecebimento() != null && !persistido.getGrupoRecebimento().equals(itemAlterado.getGrupoRecebimento())){
			if(isItemCriadorDeCusto(persistido)){
				persistido.setCusto(null);
			}
		}
		
		if(isItemCriadorDeCusto(itemAlterado)){
			persistido.setCusto(criaCustoCarro(itemAlterado));
		}
		
		persistido.setAno(itemAlterado.getAno());
		persistido.setCodigoEntrada(itemAlterado.getCodigoEntrada());
		persistido.setDataEvento(itemAlterado.getDataEvento());
		persistido.setGrupoRecebimento(itemAlterado.getGrupoRecebimento());
		persistido.setMes(itemAlterado.getMes());
		
		persistido.setObservacao(itemAlterado.getObservacao());
		persistido.setTipoEntrada(itemAlterado.getTipoEntrada());
		
		persistido.setTipoEntradaSaida(itemAlterado.getTipoEntradaSaida());
		persistido.setValor(itemAlterado.getValor());
		persistido.setAtualizado(true);
		persistido.setItensFilhos(itemAlterado.getItensFilhos());
		
		
		em.merge(persistido);
		em.flush();
	}
	
	private boolean isItemCriadorDeCusto(ItemExtrato id){
		if(ExtratoGruposPagamentoRecebimentoEnum.CONSERTO.equals(id.getGrupoRecebimento())
				   || ExtratoGruposPagamentoRecebimentoEnum.MANUTENCAO.equals(id.getGrupoRecebimento())		
				   || ExtratoGruposPagamentoRecebimentoEnum.PECA.equals(id.getGrupoRecebimento())
				){
					return true;
				}
				
				if(ExtratoGruposPagamentoRecebimentoEnum.SEGURO.equals(id.getGrupoRecebimento())){
					return true;
				}
				
				if(ExtratoGruposPagamentoRecebimentoEnum.LIMPEZA.equals(id.getGrupoRecebimento())){
					return true;
				}
				
				if(ExtratoGruposPagamentoRecebimentoEnum.SALARIO.equals(id.getGrupoRecebimento())){
					return true;
				}
				
				if(ExtratoGruposPagamentoRecebimentoEnum.DOCUMENTACAO.equals(id.getGrupoRecebimento())){
					return true;
				}
				
				if(ExtratoGruposPagamentoRecebimentoEnum.COMBUSTIVEL.equals(id.getGrupoRecebimento())){
					return true;
				}
				
				if(ExtratoGruposPagamentoRecebimentoEnum.INTERNET.equals(id.getGrupoRecebimento())){
					return true;
				}
				
				
				return false;
	}
	
	private Custo criaCustoCarro(ItemExtrato id){
		Custo c = new Custo();
		
		if(ExtratoGruposPagamentoRecebimentoEnum.CONSERTO.equals(id.getGrupoRecebimento())
		   || ExtratoGruposPagamentoRecebimentoEnum.MANUTENCAO.equals(id.getGrupoRecebimento())		
		   || ExtratoGruposPagamentoRecebimentoEnum.PECA.equals(id.getGrupoRecebimento())
		){
			c.setValor(id.getValor());
			c.setData(id.getDataEvento());
			c.setDescricao(id.getObservacao());
			c.setTipoCusto(CustoEnum.MECANICA);
		}
		
		if(ExtratoGruposPagamentoRecebimentoEnum.SEGURO.equals(id.getGrupoRecebimento())){
			c.setValor(id.getValor());
			c.setData(id.getDataEvento());
			c.setDescricao(id.getObservacao());
			c.setTipoCusto(CustoEnum.SEGURO);
		}
		
		if(ExtratoGruposPagamentoRecebimentoEnum.LIMPEZA.equals(id.getGrupoRecebimento())){
			c.setValor(id.getValor());
			c.setData(id.getDataEvento());
			c.setDescricao(id.getObservacao());
			c.setTipoCusto(CustoEnum.LIMPEZA);
		}
		
		if(ExtratoGruposPagamentoRecebimentoEnum.SALARIO.equals(id.getGrupoRecebimento())){
			c.setValor(id.getValor());
			c.setData(id.getDataEvento());
			c.setDescricao(id.getObservacao());
			c.setTipoCusto(CustoEnum.SALARIO_MOTORISTA);
		}
		
		if(ExtratoGruposPagamentoRecebimentoEnum.DOCUMENTACAO.equals(id.getGrupoRecebimento())){
			c.setValor(id.getValor());
			c.setData(id.getDataEvento());
			c.setDescricao(id.getObservacao());
			c.setTipoCusto(CustoEnum.DOCUMENTACAO);
		}
		
		if(ExtratoGruposPagamentoRecebimentoEnum.COMBUSTIVEL.equals(id.getGrupoRecebimento())){
			c.setValor(id.getValor());
			c.setData(id.getDataEvento());
			c.setDescricao(id.getObservacao());
			c.setTipoCusto(CustoEnum.COMBUSTIVEL);
		}
		
		if(ExtratoGruposPagamentoRecebimentoEnum.INTERNET.equals(id.getGrupoRecebimento())){
			c.setValor(id.getValor());
			c.setData(id.getDataEvento());
			c.setDescricao(id.getObservacao());
			c.setTipoCusto(CustoEnum.INTERNET);
		}
		
		return c;
	}

	public int getMesArquivoExtrato(Date data) {
		Calendar c = Calendar.getInstance();
		c.setTime(data);

		return c.get(Calendar.MONTH);
	}

	public int getAnoArquivoExtrato(Date data) {
		Calendar c = Calendar.getInstance();
		c.setTime(data);

		return c.get(Calendar.YEAR);
	}

	public void lerExtrato(String localArquivo) {
		try {
			List<ItemExtrato> itensExtratoCadastrados = new ArrayList<>();

			File arquivos[];
			File diretorio = new File(localArquivo);
			arquivos = diretorio.listFiles();
			if (arquivos != null) {
				for (int i = 0; i < arquivos.length; i++) {
					if(!arquivos[i].isDirectory()){
						List<String> arquivo = OfficeUtil.lerArquivo(localArquivo  + arquivos[i].getName());

						if(arquivo != null && arquivo.size() > 3){
							String linha2 = OfficeUtil.getLinha(arquivo, 3);
							String data2 = OfficeUtil.getValor(linha2, ConstantesEXTRATO.INICIO_DATA, ConstantesEXTRATO.FIM_DATA);

							Date datax = OfficeUtil.retornaData(data2);

							int mes = getMesArquivoExtrato(datax) + 1;
							int ano = getAnoArquivoExtrato(datax);

							itensExtratoCadastrados = getItemExtrato(mes, ano);

							for (int x = 2; x < arquivo.size() - 2; x++) {
								String linha = OfficeUtil.getLinha(arquivo, x);
								String data = OfficeUtil.getValor(linha, ConstantesEXTRATO.INICIO_DATA,
										ConstantesEXTRATO.FIM_DATA);
								String creditoDebito = OfficeUtil.getValor(linha, ConstantesEXTRATO.INICIO_CREDITO_DEBITO,
										ConstantesEXTRATO.FIM_CREDITO_DEBITO);
								String valor = OfficeUtil.getValor(linha, ConstantesEXTRATO.INICIO_VALOR,
										ConstantesEXTRATO.FIM_VALOR);
								String COMENTARIO = OfficeUtil.getValor(linha, ConstantesEXTRATO.INICIO_COMENTARIO,
										ConstantesEXTRATO.FIM_COMENTARIO);
								String tipo = OfficeUtil.getValor(linha, ConstantesEXTRATO.INICIO_TIPO,
										ConstantesEXTRATO.FIM_TIPO);
								String codigoEnrada = OfficeUtil.getValor(linha, ConstantesEXTRATO.INICIO_CODIGO_ENTRADA,ConstantesEXTRATO.FIM_CODIGO_ENTRADA);

								
								ItemExtrato item = new ItemExtrato();
								item.setDataEvento(OfficeUtil.retornaData(data));
								item.setAno(getAnoArquivoExtrato(item.getDataEvento()));
								item.setMes(getMesArquivoExtrato(item.getDataEvento()) + 1);

								itensExtratoCadastrados = getItemExtrato(item.getMes(), item.getAno());

								item.setValor(Double.parseDouble(valor) / 100);
								if (creditoDebito.equalsIgnoreCase("C")) {
									item.setTipoEntradaSaida(ExtratoTiposEntradaSaidaEnum.ENTRADA);
								} else {
									item.setTipoEntradaSaida(ExtratoTiposEntradaSaidaEnum.SAIDA);
								}
								item.setObservacao(COMENTARIO);
								item.setCodigoEntrada(codigoEnrada);

								List<ExtratoTiposEntradaEnum> list = Arrays.asList(ExtratoTiposEntradaEnum.values());
								tipo = tipo.trim();
								for (ExtratoTiposEntradaEnum extratoTipo : list) {
									if (tipo.equalsIgnoreCase(extratoTipo.getNameExtrato().trim())) {
										item.setTipoEntrada(extratoTipo);
									}
								}
								if (item.getTipoEntrada() == null) {
									item.setTipoEntrada(ExtratoTiposEntradaEnum.NAO_IDENTIFICADO);
								}
								//Grupos automaticos
								if(item.getTipoEntrada().equals(ExtratoTiposEntradaEnum.DEBITO_TELECOMUNICACAO)){
									item.setGrupoRecebimento(ExtratoGruposPagamentoRecebimentoEnum.INTERNET);
								}
								if(item.getTipoEntrada().equals(ExtratoTiposEntradaEnum.TARIFA_REN)){
									item.setGrupoRecebimento(ExtratoGruposPagamentoRecebimentoEnum.TARIFA_BANCO);
								}
								
								if(item.getTipoEntrada().equals(ExtratoTiposEntradaEnum.DEBITO_SEGURO)){
									item.setGrupoRecebimento(ExtratoGruposPagamentoRecebimentoEnum.SEGURO);
								}
								if(item.getTipoEntrada().equals(ExtratoTiposEntradaEnum.CREDITO_LIQUIDACAO_COBRANCA)){
									item.setGrupoRecebimento(ExtratoGruposPagamentoRecebimentoEnum.ENTRADA_BOLETO);
								}
								
								if(item.getTipoEntrada().equals(ExtratoTiposEntradaEnum.PACOTE_SERVICOS)){
									item.setGrupoRecebimento(ExtratoGruposPagamentoRecebimentoEnum.TARIFA_BANCO);
								}
								
								if(item.getTipoEntrada().equals(ExtratoTiposEntradaEnum.DEBITO_ORGAO_FEDERAL)){
									item.setGrupoRecebimento(ExtratoGruposPagamentoRecebimentoEnum.TARIFA_GERAL);
								}
								if(item.getTipoEntrada().equals(ExtratoTiposEntradaEnum.TARIFA)){
									item.setGrupoRecebimento(ExtratoGruposPagamentoRecebimentoEnum.TARIFA_GERAL);
								}
								if(item.getTipoEntrada().equals(ExtratoTiposEntradaEnum.SAQUE)){
									item.setGrupoRecebimento(ExtratoGruposPagamentoRecebimentoEnum.SALARIO);
								}
								
								if(item.getTipoEntrada().equals(ExtratoTiposEntradaEnum.TED)){
									item.setGrupoRecebimento(ExtratoGruposPagamentoRecebimentoEnum.TARIFA_BANCO);
								}
								
								if(item.getTipoEntrada().equals(ExtratoTiposEntradaEnum.DEBITO_CASAM)){
									item.setGrupoRecebimento(ExtratoGruposPagamentoRecebimentoEnum.OUTROS);
								}
								
								if(item.getTipoEntrada().equals(ExtratoTiposEntradaEnum.DEBITO_FGTS)){
									item.setGrupoRecebimento(ExtratoGruposPagamentoRecebimentoEnum.TARIFA_GERAL);
								}
								
								if(item.getGrupoRecebimento() == null) {
									item.setGrupoRecebimento(ExtratoGruposPagamentoRecebimentoEnum.NAO_SELECIONADO);
								}
								
								
								if (!itensExtratoCadastrados.contains(item)) {
									save(item);
								}
							}

							Date hj = new Date();

							OfficeUtil.moveFile(localArquivo + arquivos[i].getName(),
									CONSTANTES.PATH_EXTRATO_BANCARIO_ENVIADO + OfficeUtil.retornaDataSomenteNumeros(hj)
											+ arquivos[i].getName() + hj.getTime());
							
						}
					
						
					}
					
				}
			}

		} catch (ParseException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
