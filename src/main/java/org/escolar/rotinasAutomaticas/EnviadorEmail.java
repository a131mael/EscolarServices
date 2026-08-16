package org.escolar.rotinasAutomaticas;

import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Base64;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.naming.NamingException;

import org.aaf.financeiro.model.Pagador;
import org.aaf.financeiro.sicoob.util.CNAB240_SICOOB;
import org.aaf.financeiro.util.OfficeUtil;
import org.escolar.model.Boleto;
import org.escolar.model.ContratoAluno;
import org.escolar.service.ConfiguracaoService;
import org.escolar.service.FinanceiroService;
import org.escolar.service.SicoobBoletoService;
import org.escolar.service.ZohoEmailService;
import org.escolar.util.Formatador;
import org.escolar.util.ServiceLocator;
import org.escolar.util.Verificador;

import java.util.Date;
import java.util.logging.Logger;

/** Lembretes automáticos de boleto por e-mail (aviso dia 5, vencimento dia 10, atraso dias
 *  15/20/25 — ver RotinaAutomatica). Manda pela conta financeiro@tefamel.com via Zoho Mail
 *  API (ZohoEmailService) — substituiu o envio antigo por SMTP direto (conta Gmail
 *  tefameltur@gmail.com, defasada) em 14/ago/2026. */
@Stateless
@LocalBean
public class EnviadorEmail {

	private static final Logger LOG = Logger.getLogger(EnviadorEmail.class.getName());

	@Inject
	private FinanceiroService financeiroService;

	@Inject
	private SicoobBoletoService sicoobBoletoService;

	@Inject
	private ConfiguracaoService configuracaoService;

	private final ZohoEmailService zohoEmailService = new ZohoEmailService();

	/** Assinatura embutida em base64 (data URI) no corpo do e-mail em vez de linkada num
	 *  host externo (i.ibb.co) — imagem hotlinkada de fora é padrão clássico de phishing
	 *  e o Gmail marcava a mensagem como suspeita/escondia as imagens por causa disso. */
	private static final String ASSINATURA_BASE64 = carregarAssinaturaBase64();

	private static String carregarAssinaturaBase64() {
		try (InputStream is = EnviadorEmail.class.getResourceAsStream("/img/assinatura-favo.jpg")) {
			if (is == null) return null;
			return Base64.getEncoder().encodeToString(is.readAllBytes());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public EnviadorEmail() {
		try {
			if (financeiroService == null) {
				financeiroService = (FinanceiroService) ServiceLocator.getInstance().getFinanceiroService(
						FinanceiroService.class.getSimpleName(), FinanceiroService.class.getName());
			}
			if (sicoobBoletoService == null) {
				sicoobBoletoService = (SicoobBoletoService) ServiceLocator.getInstance().getEjbGeneric(
						SicoobBoletoService.class.getSimpleName(), SicoobBoletoService.class.getName());
			}
			if (configuracaoService == null) {
				configuracaoService = (ConfiguracaoService) ServiceLocator.getInstance().getEjbGeneric(
						ConfiguracaoService.class.getSimpleName(), ConfiguracaoService.class.getName());
			}
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}

	/** Confere a situação do boleto direto na API do Sicoob antes de mandar o lembrete —
	 *  o campo statusSicoob salvo no banco pode estar desatualizado (só é sincronizado
	 *  periodicamente), então aqui é sempre uma consulta ao vivo. Se a consulta falhar
	 *  (Sicoob fora do ar, etc), não bloqueia o envio — só segue com o que já sabíamos
	 *  pela query (dataPagamento/statusSicoob salvos). */
	private boolean aindaEmAberto(Boleto bol) {
		try {
			String situacao = sicoobBoletoService.consultarSituacaoBoleto(
					configuracaoService.getConfiguracao(), bol.getNossoNumero());
			financeiroService.atualizarStatusSicoob(bol.getId(), situacao, new Date());
			return situacao == null || !situacao.equalsIgnoreCase("Liquidado");
		} catch (Exception e) {
			LOG.warning("Sicoob: não deu pra confirmar a situação do boleto " + bol.getId()
					+ " antes de enviar o lembrete, seguindo com o que já sabíamos: " + e.getMessage());
			return true;
		}
	}

	public void enviarAvisosVencimento() {
		for (Boleto bol : financeiroService.getBoletosAvisoVencimento()) {
			if (!aindaEmAberto(bol)) continue;
			if (enviar(bol, "Seu boleto vence em breve", "#1a73e8",
					"Passando pra avisar que o boleto do transporte escolar da <b>" + nomeAluno(bol)
							+ "</b> vence em <b>" + Formatador.formataData(bol.getVencimento()) + "</b>.")) {
				bol.setEmailAvisoVencimentoEnviado(true);
				financeiroService.save(bol);
			}
		}
	}

	public void enviarVenceHoje() {
		for (Boleto bol : financeiroService.getBoletosVenceHoje()) {
			if (!aindaEmAberto(bol)) continue;
			if (enviar(bol, "Seu boleto vence hoje", "#1a73e8",
					"O boleto do transporte escolar da <b>" + nomeAluno(bol) + "</b> vence <b>hoje</b>.")) {
				bol.setEmailVenceHojeEnviado(true);
				financeiroService.save(bol);
			}
		}
	}

	public void enviarAtrasado15() {
		for (Boleto bol : financeiroService.getBoletosAtrasados15()) {
			if (!aindaEmAberto(bol)) continue;
			if (enviar(bol, "Boleto em atraso", "#e8710a",
					"O boleto do transporte escolar da <b>" + nomeAluno(bol)
							+ "</b> venceu em <b>" + Formatador.formataData(bol.getVencimento())
							+ "</b> e ainda consta em aberto por aqui.")) {
				bol.setEmailAtrasado15Enviado(true);
				financeiroService.save(bol);
			}
		}
	}

	public void enviarAtrasado20() {
		for (Boleto bol : financeiroService.getBoletosAtrasados20()) {
			if (!aindaEmAberto(bol)) continue;
			if (enviar(bol, "Boleto em atraso", "#e8710a",
					"O boleto do transporte escolar da <b>" + nomeAluno(bol)
							+ "</b> venceu em <b>" + Formatador.formataData(bol.getVencimento())
							+ "</b> e ainda consta em aberto por aqui."
							+ " Entre em contato o quanto antes pra regularizar a situação e evitar a suspensão do serviço de transporte.")) {
				bol.setEmailAtrasado20Enviado(true);
				financeiroService.save(bol);
			}
		}
	}

	public void enviarAtrasado25() {
		for (Boleto bol : financeiroService.getBoletosAtrasados25()) {
			if (!aindaEmAberto(bol)) continue;
			if (enviar(bol, "Boleto em atraso", "#d93025",
					"O boleto do transporte escolar da <b>" + nomeAluno(bol)
							+ "</b> venceu em <b>" + Formatador.formataData(bol.getVencimento())
							+ "</b> e segue em aberto. Entre em contato com urgência pra regularizar a situação —"
							+ " a manutenção do serviço de transporte depende do pagamento em dia.")) {
				bol.setEmailAtrasado25Enviado(true);
				financeiroService.save(bol);
			}
		}
	}

	/** Reenvio manual pontual (botão no admin) — mesma lógica das rotinas automáticas,
	 *  mas só pro aluno informado, sem esperar o horário agendado. */
	public void enviarEmailBoletosMesAtualEAtrasados(Long idAluno) {
		Calendar c = Calendar.getInstance();
		Boleto boletoMesAtual = financeiroService.getBoletoMes(c.get(Calendar.MONTH), idAluno);
		if (boletoMesAtual != null && (boletoMesAtual.getDataPagamento() == null)) {
			enviar(boletoMesAtual, "Seu boleto do transporte escolar", "#1a73e8",
					"Segue em anexo o boleto do transporte escolar da <b>" + nomeAluno(boletoMesAtual) + "</b>.");
		}

		List<Boleto> boletosAtrasados = financeiroService.getBoletosAtrasadosAluno(c.get(Calendar.MONTH), idAluno);
		if (boletosAtrasados != null) {
			for (Boleto bol : boletosAtrasados) {
				enviar(bol, "Boleto em atraso", "#e8710a",
						"O boleto do transporte escolar da <b>" + nomeAluno(bol)
								+ "</b> venceu em <b>" + Formatador.formataData(bol.getVencimento())
								+ "</b> e ainda consta em aberto por aqui.");
			}
		}
	}

	private boolean enviar(Boleto bol, String tituloDestaque, String corDestaque, String mensagem) {
		String destinatario = enderecosPagador(bol);
		if (destinatario.isEmpty()) return false;

		byte[] anexoPDF = byteArrayPDFBoleto(getBoletoFinanceiro(bol), bol.getContrato());
		String corpoEmail = montarCorpoEmail(tituloDestaque, corDestaque, mensagem, bol);

		return zohoEmailService.enviarEmailComAnexo(
				destinatario, tituloDestaque + " — Transporte Escolar Favo de Mel", corpoEmail,
				"boleto_" + bol.getId() + ".pdf", anexoPDF);
	}

	private String enderecosPagador(Boleto bol) {
		StringBuilder destinatario = new StringBuilder();
		if (bol.getPagador().getContatoEmail1() != null && !bol.getPagador().getContatoEmail1().trim().isEmpty()) {
			destinatario.append(bol.getPagador().getContatoEmail1().trim()).append(",");
		}
		if (bol.getPagador().getContatoEmail2() != null && !bol.getPagador().getContatoEmail2().trim().isEmpty()) {
			destinatario.append(bol.getPagador().getContatoEmail2().trim()).append(",");
		}
		if (destinatario.length() > 0) destinatario.setLength(destinatario.length() - 1);
		return destinatario.toString();
	}

	private String nomeAluno(Boleto bol) {
		return bol.getPagador() != null && bol.getPagador().getNomeAluno() != null
				? bol.getPagador().getNomeAluno() : "seu filho(a)";
	}

	// Formatador.valorFormatado usa NumberFormat.getCurrencyInstance() sem locale
	// explícito, então no servidor (locale padrão não é pt-BR) sai "$" em vez de "R$"
	// (achado em produção 15/ago/2026). Formata direto em pt-BR aqui pra não depender
	// do locale da JVM.
	private String formatarValorReal(double valor) {
		DecimalFormatSymbols simbolos = new DecimalFormatSymbols(new Locale("pt", "BR"));
		DecimalFormat formatador = new DecimalFormat("R$ #,##0.00", simbolos);
		return formatador.format(valor);
	}

	private String montarCorpoEmail(String tituloDestaque, String corDestaque, String mensagem, Boleto bol) {
		return "<!DOCTYPE html><html><body style=\"font-family:Arial,sans-serif; background:#f4f4f4; padding:20px;\">"
				+ "<div style=\"max-width:520px; margin:0 auto; background:#fff; border-radius:8px; overflow:hidden; border:1px solid #e0e0e0;\">"
				+ "<div style=\"background:" + corDestaque + "; padding:20px; text-align:center;\">"
				+ "<h2 style=\"color:#fff; margin:0; font-size:20px;\">" + tituloDestaque + "</h2>"
				+ "</div>"
				+ "<div style=\"padding:24px;\">"
				+ "<p style=\"font-size:15px; color:#333;\">Olá, <b>" + bol.getContrato().getNomeResponsavel() + "</b>!</p>"
				+ "<p style=\"font-size:15px; color:#333; line-height:1.5;\">" + mensagem + "</p>"
				+ "<table style=\"width:100%; border-collapse:collapse; margin:20px 0; font-size:14px;\">"
				+ "<tr><td style=\"padding:8px 0; color:#666;\">Vencimento</td>"
				+ "<td style=\"padding:8px 0; text-align:right; font-weight:bold; color:#333;\">"
				+ Formatador.formataData(bol.getVencimento()) + "</td></tr>"
				+ "<tr><td style=\"padding:8px 0; color:#666;\">Valor</td>"
				+ "<td style=\"padding:8px 0; text-align:right; font-weight:bold; color:#333;\">"
				+ formatarValorReal(Verificador.getValorFinal(bol)) + "</td></tr>"
				+ "</table>"
				+ "<p style=\"font-size:13px; color:#666;\">O boleto está em anexo neste e-mail.</p>"
				+ "<div style=\"background:#f0f7ff; border-radius:6px; padding:12px 16px; margin-top:20px;\">"
				+ "<p style=\"font-size:13px; color:#555; margin:0;\">Se você já pagou esse boleto, "
				+ "pode ignorar esta mensagem — os pagamentos levam um tempo pra atualizar no nosso sistema.</p>"
				+ "</div>"
				+ "</div>"
				+ "<div style=\"background:#fafafa; padding:16px; text-align:center; border-top:1px solid #eee;\">"
				+ (ASSINATURA_BASE64 != null
					? "<img src=\"data:image/jpeg;base64," + ASSINATURA_BASE64 + "\" alt=\"Transporte Escolar Favo de Mel\" "
						+ "style=\"max-width:260px; height:auto;\">"
					: "<p style=\"margin:0; font-size:13px; color:#888;\">Transporte Escolar Favo de Mel<br>(48) 3093-0042</p>")
				+ "</div>"
				+ "</div>"
				+ "</body></html>";
	}

	private org.aaf.financeiro.model.Boleto getBoletoFinanceiro(Boleto boleto) {
		org.aaf.financeiro.model.Boleto boletoFinanceiro = new org.aaf.financeiro.model.Boleto();
		boletoFinanceiro.setEmissao(boleto.getEmissao());
		boletoFinanceiro.setId(boleto.getId());
		boletoFinanceiro.setValorNominal(boleto.getValorNominal());
		boletoFinanceiro.setVencimento(boleto.getVencimento());
		boletoFinanceiro.setNossoNumero(String.valueOf(boleto.getNossoNumero()));
		boletoFinanceiro.setDataPagamento(OfficeUtil.retornaDataSomenteNumeros(boleto.getDataPagamento()));
		boletoFinanceiro.setValorPago(boleto.getValorPago());
		return boletoFinanceiro;
	}

	public byte[] byteArrayPDFBoleto(org.aaf.financeiro.model.Boleto boleto, ContratoAluno contrato) {
		Calendar c = Calendar.getInstance();
		c.setTime(boleto.getVencimento());
		CNAB240_SICOOB cnab = new CNAB240_SICOOB(1);

		Pagador pagador = new Pagador();
		pagador.setBairro(contrato.getBairro());
		pagador.setCep(contrato.getCep());
		pagador.setCidade(contrato.getCidade() != null ? contrato.getCidade() : "PALHOCA");
		pagador.setCpfCNPJ(contrato.getCpfResponsavel());
		pagador.setEndereco(contrato.getEndereco());
		pagador.setNome(contrato.getNomeResponsavel());
		pagador.setNossoNumero(boleto.getNossoNumero() + "");
		pagador.setUF("SC");
		List<org.aaf.financeiro.model.Boleto> boletos = new java.util.ArrayList<>();
		boletos.add(boleto);
		pagador.setBoletos(boletos);

		return cnab.getBoletoPDF(pagador);
	}

}
