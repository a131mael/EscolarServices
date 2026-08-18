package org.escolar.rotinasAutomaticas;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Lock;
import javax.ejb.LockType;
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

// LockType.READ pra cada @Schedule poder rodar em paralelo com os outros — o padrão
// (WRITE) faz o EJB travar a instância inteira do singleton por invocação, então uma
// rotina lenta (ex: e-mail de boleto, várias chamadas de rede sequenciais por boleto)
// bloqueava os outros jobs agendados até terminar (ImportarExtratoBancario chegou a
// dar timeout de lock esperando, achado 15/ago/2026). Seguro aqui porque nenhum método
// desta classe muta estado da própria instância — só usam os services @Inject, que são
// setados uma vez pelo container e nunca reatribuídos.
@Lock(LockType.READ)
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
	public void removerAlunosSemContratoAtivo() {
		try {
			System.out.println("Cancelando crianças sem contrato ativo ");
			alunoService.cancelarAlunosSemContratoAtivo();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Schedule(hour="*/02",  persistent = false)
	public void colocarAlunosNaListaCobranca() {
		try {
			System.out.println("Colocar alunos na Lista de cobrança ");
			alunoService.colocarAlunosNaListaDeCobranca();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Schedule(hour="*/2", persistent = false)
	public void atualizarStatusProtesto() {
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
	public void ImportarExtratoBancario() {
		try {
			System.out.println("Importando Extrato 2");
			extratoBancarioService.lerExtrato(CONSTANTES.PATH_EXTRATO_BANCARIO_ENVIAR);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/** Lembretes automáticos de boleto por e-mail (financeiro@tefamel.com via Zoho, ver
	 *  EnviadorEmail): dia 5 avisa que vence dia 10, dia 10 avisa que vence hoje, dias
	 *  15/20/25 avisam que está em atraso (se ainda não tiver sido pago — a checagem é
	 *  sempre em tempo real na query, então um boleto pago entre uma rodada e outra
	 *  simplesmente some da lista). Cada etapa só é enviada uma vez por boleto (flags
	 *  emailXxxEnviado) — rodar 3x no dia é só reforço caso a 1ª tentativa falhe (ex:
	 *  container reiniciando), não gera reenvio duplicado. Roda às 9h, 13h e 17h
	 *  horário Brasília (= 12h, 16h, 20h UTC) do dia relevante. */
	@Schedule(hour = "12,16,20", minute = "0", dayOfMonth = "5", persistent = false)
	public void enviarAvisoVencimentoBoletoEmail() {
		try {
			System.out.println("E-mail: aviso de vencimento (dia 5)");
			new EnviadorEmail().enviarAvisosVencimento();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Schedule(hour = "12,16,20", minute = "0", dayOfMonth = "10", persistent = false)
	public void enviarVenceHojeBoletoEmail() {
		try {
			System.out.println("E-mail: vence hoje (dia 10)");
			new EnviadorEmail().enviarVenceHoje();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Schedule(hour = "12,16,20", minute = "0", dayOfMonth = "15", persistent = false)
	public void enviarAtrasado15BoletoEmail() {
		try {
			System.out.println("E-mail: boleto em atraso (dia 15)");
			new EnviadorEmail().enviarAtrasado15();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Schedule(hour = "12,16,20", minute = "0", dayOfMonth = "20", persistent = false)
	public void enviarAtrasado20BoletoEmail() {
		try {
			System.out.println("E-mail: boleto em atraso (dia 20)");
			new EnviadorEmail().enviarAtrasado20();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Schedule(hour = "12,16,20", minute = "0", dayOfMonth = "25", persistent = false)
	public void enviarAtrasado25BoletoEmail() {
		try {
			System.out.println("E-mail: boleto em atraso (dia 25)");
			new EnviadorEmail().enviarAtrasado25();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** Limpeza de e-mail inválido: roda todo dia à noite (22h UTC = 19h Brasília, depois
	 *  de todas as janelas de envio do dia) e confere os bounces acumulados na caixa
	 *  financeiro@tefamel.com, corrigindo ou limpando o cadastro do aluno. Roda todo dia
	 *  (não só nos dias de envio) porque um bounce pode demorar a chegar. */
	@Schedule(hour = "22", minute = "0", persistent = false)
	public void limparEmailsInvalidosBoletoEmail() {
		try {
			System.out.println("E-mail: limpeza de endereços inválidos (bounces)");
			new EnviadorEmail().limparEmailsInvalidos();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** Monitor WhatsApp: verifica mensagens sem resposta há +5h e responde automaticamente.
	 *  Roda às 8h, 11h, 15h e 18h horário Brasília (= 11h, 14h, 18h, 21h UTC). */
	@Schedule(hour = "11,14,18,21", persistent = false)
	public void monitorWhatsapp() {
		try {
			whatsappMonitorService.executar();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** Consulta o scmobi-automation-service para os Fretes com geracao de licenca em andamento
	 *  e, quando concluido (ou com erro), baixa os PDFs / salva a mensagem de erro. */
	@Schedule(minute = "*/1", persistent = false)
	public void verificarLicencasScmobiPendentes() {
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
