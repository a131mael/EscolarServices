package org.escolar.rotinasAutomaticas;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import org.aaf.escolar.RetornoEnvioContratoDTO;
import org.escolar.model.ContratoAluno;
import org.escolar.model.Frete;
import org.escolar.service.AlunoService;
import org.escolar.service.DevedorService;
import org.escolar.service.ExtratoBancarioService;
import org.escolar.service.FreteService;
import org.escolar.service.ScmobiService;
import org.escolar.service.WhatsappMonitorService;
import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.aaf.base.base.ConstantesEscolaApi;
import br.com.aaf.base.comunicadores.EnviadorJson;
import br.com.aaf.base.whats.model.Parametro;

@Singleton
@Startup
public class RotinaAutomatica {

	@Inject
	private AlunoService alunoService;
	
	@Inject
	private DevedorService devedorService;
	
	@Inject
	private ExtratoBancarioService extratoBancarioService;

	@Inject
	private FreteService freteService;

	@Inject
	private ScmobiService scmobiService;

	@Inject
	private WhatsappMonitorService whatsappMonitorService;

	@Schedule(hour="*/03",  persistent = false)
	public synchronized void removerAlunosSemContratoAtivo() {
		try {
			System.out.println("Cancelando crianças sem contrato ativo ");
			alunoService.cancelarAlunosSemContratoAtivo();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Schedule(hour="*/02",  persistent = false)
	public synchronized void colocarAlunosNaListaCobranca() {
		try {
			System.out.println("Colocar alunos na Lista de cobrança ");
			alunoService.colocarAlunosNaListaDeCobranca();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Schedule(hour="*/2", persistent = false)
	public synchronized void atualizarStatusProtesto() {
		try {
			List<ContratoAluno> contratosProtestados = devedorService.findProtesto(0, 5000, null, null, null);
			for(ContratoAluno contrato : contratosProtestados) {
				
				RetornoEnvioContratoDTO statusCartorio=	getStatus(contrato);
				if(statusCartorio != null && statusCartorio.getCodigo() != null) {
					if(statusCartorio.getCodigo().equalsIgnoreCase("CONFIRMADO")) {
						contrato.setConfirmadoEnvioPorWebService(true);
						alunoService.enviarConfirmadoWebService(contrato);	
					}else {
						String resposta = statusCartorio.getCodigo()+ statusCartorio.getMensagem();
						byte[] decode = resposta.getBytes();
						contrato.setComentarioWebService(decode);
						alunoService.saveComentarioContrato(contrato);	
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public RetornoEnvioContratoDTO getStatus(ContratoAluno contrato) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
			String endpoint = ConstantesEscolaApi.URL_CARTORIO + ConstantesEscolaApi.STATATUS_CONTRATO;

			Parametro p1 = new Parametro("idContrato", contrato.getNumero());
			List<Parametro> parametros = new ArrayList<>();
			parametros.add(p1);
			String retornoJson = EnviadorJson.get2(endpoint, null, parametros);
			RetornoEnvioContratoDTO retorno = new RetornoEnvioContratoDTO();

			if(retornoJson == null || retornoJson.equalsIgnoreCase("")) {
				return new RetornoEnvioContratoDTO();
			}
			retorno = mapper.readValue(retornoJson, RetornoEnvioContratoDTO.class);

			return retorno;

		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return  new RetornoEnvioContratoDTO();
	}

	
	@Schedule(hour="*",minute="*/4",  persistent = false)
	public synchronized void ImportarExtratoBancario() {
		try {
			System.out.println("Importando Extrato 2");
			extratoBancarioService.lerExtrato(CONSTANTES.PATH_EXTRATO_BANCARIO_ENVIAR);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Schedule(hour="*",minute="*/33", dayOfMonth="5-7",  persistent = false)
	public synchronized void  enviarBoletoEmail() {
		try {
			System.out.println("Enviando Boleto por email");
			EnviadorEmail enviador = new EnviadorEmail();
			enviador.enviarEmailBoletosMesAtual();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** Monitor WhatsApp: verifica mensagens sem resposta há +5h e responde automaticamente.
	 *  Roda às 8h, 11h, 15h e 18h horário Brasília (= 11h, 14h, 18h, 21h UTC). */
	@Schedule(hour = "11,14,18,21", persistent = false)
	public synchronized void monitorWhatsapp() {
		try {
			whatsappMonitorService.executar();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** Consulta o scmobi-automation-service para os Fretes com geracao de licenca em andamento
	 *  e, quando concluido (ou com erro), baixa os PDFs / salva a mensagem de erro. */
	@Schedule(minute = "*/1", persistent = false)
	public synchronized void verificarLicencasScmobiPendentes() {
		try {
			for (Frete frete : freteService.findFretesComLicencaScmobiProcessando()) {
				try {
					JSONObject status = scmobiService.consultarStatus(frete.getJobIdScmobi());
					String situacao = status.getString("status");

					if ("concluido".equals(situacao)) {
						byte[] licenca = scmobiService.baixarLicencaPdf(frete.getJobIdScmobi());
						byte[] passageiros = scmobiService.baixarPassageirosPdf(frete.getJobIdScmobi());
						String dataGeracao = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date());
						freteService.atualizarLicencaScmobiConcluida(frete.getId(), status.optInt("numeroContrato"),
								licenca, passageiros, dataGeracao);

					} else if ("erro".equals(situacao)) {
						String erro = status.optString("erro", "Erro desconhecido no scmobi-automation-service");
						freteService.atualizarLicencaScmobiErro(frete.getId(), erro);
					}
					// "processando" -> nada a fazer, tenta novamente no proximo ciclo

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
