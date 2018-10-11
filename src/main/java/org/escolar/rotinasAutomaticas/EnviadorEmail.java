package org.escolar.rotinasAutomaticas;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.naming.NamingException;

import org.aaf.financeiro.model.Pagador;
import org.aaf.financeiro.sicoob.util.CNAB240_SICOOB;
import org.aaf.financeiro.util.OfficeUtil;
import org.escolar.model.Aluno;
import org.escolar.model.Boleto;
import org.escolar.service.FinanceiroService;
import org.escolar.util.Formatador;
import org.escolar.util.ServiceLocator;
import org.escolar.util.Verificador;

@Stateless
@LocalBean
public class EnviadorEmail {

	@Inject
	private FinanceiroService financeiroService;

	public EnviadorEmail() {
		if (financeiroService == null) {
			try {
				financeiroService = (FinanceiroService) ServiceLocator.getInstance().getFinanceiroService(
						FinanceiroService.class.getSimpleName(), FinanceiroService.class.getName());
			} catch (NamingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void enviarEmailBoletosMesAtual() {
		Calendar c = Calendar.getInstance();
		List<Boleto> boletos = financeiroService.getBoletoMes(c.get(Calendar.MONTH));
		for (Boleto bol : boletos) {
			enviarEmailBoletosMesAtual(bol);
		}
	}

	public void enviarEmailBoletosMesAtual(Boleto bol) {

		String destinatario = "";

		if (bol.getPagador().getContatoEmail1() != null) {
			destinatario += bol.getPagador().getContatoEmail1() + ",";
		}
		if (bol.getPagador().getContatoEmail2() != null) {
			destinatario += bol.getPagador().getContatoEmail2() + ",";
		}
		if (bol.getPagador().getEmailMae() != null) {
			destinatario += bol.getPagador().getEmailMae() + ",";
		}
		if (bol.getPagador().getEmailPai() != null) {
			destinatario += bol.getPagador().getEmailPai() + ",";
		}

		byte[] anexoPDF = byteArrayPDFBoleto(getBoletoFinanceiro(bol), bol.getPagador());

		String corpoEmail = "<!DOCTYPE html><html><body><p><h2><center>Transporte Escolar Favo de Mel.</center></h2></p><br/>"
				+ "<p>Bom dia #nomeResponsavel,<br/><br/><br/><br/>Você esta recebendo em anexo o seu boleto do Transporte Escolar Favo de Mel referente ao mês de <b>"
				+ "<font size=\"2\" color=\"blue\"> #mesBoleto</font></b> .<h3><br/>"
				+ "<center><font size=\"3\" color=\"blue\">Resumo da conta</font></center>"
				+ "</h3>Vencimento  :<font size=\"2\" color=\"blue\"> #vencimentoBoleto</font>"
				+ "<br/>Valor       :<font size=\"2\" color=\"blue\"> #valorAtualBoleto</font><br/>"
				+ "<br/><center><h4><font size=\"3\" color=\"red\"> Caso já tenha efetuado o pagamento favor desconsiderar esse e-mail. </font></h4>"
				+ "</center></p>" + "<br/>"
				+ "<a href=\"https://ibb.co/i3s83m\"><img src=\"https://preview.ibb.co/hX0Hw6/assinatura_Tefamel.png\" "
				+ "alt=\"assinatura_Tefamel\" style=\"width:365px;height:146px;border:0;\" border=\"0\"></a>"
				+ "</body></html>";

		corpoEmail = corpoEmail.replace("#vencimentoBoleto", Formatador.formataData(bol.getVencimento()));
		corpoEmail = corpoEmail.replace("#valorAtualBoleto", Formatador.valorFormatado(Verificador.getValorFinal(bol)));
		corpoEmail = corpoEmail.replace("#nomeResponsavel", bol.getPagador().getNomeResponsavel());
		corpoEmail = corpoEmail.replace("#mesBoleto", Formatador.getMes(bol.getVencimento()));

		ByteArrayInputStream bais = new ByteArrayInputStream(anexoPDF);
		org.aaf.financeiro.util.EnviadorEmail.enviarEmail("Boleto - Transporte Escolar Favo de Mel", corpoEmail, bais,
				destinatario, CONSTANTES.emailFinanceiro, CONSTANTES.senhaEmailFinanceiro);

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

	public byte[] byteArrayPDFBoleto(org.aaf.financeiro.model.Boleto boleto, Aluno aluno) {
		Calendar c = Calendar.getInstance();
		c.setTime(boleto.getVencimento());
		CNAB240_SICOOB cnab = new CNAB240_SICOOB(1);

		Pagador pagador = new Pagador();
		pagador.setBairro(aluno.getBairro());
		pagador.setCep(aluno.getCep());
		pagador.setCidade(aluno.getCidade() != null ? aluno.getCidade() : "PALHOCA");
		pagador.setCpfCNPJ(aluno.getCpfResponsavel());
		pagador.setEndereco(aluno.getEndereco());
		pagador.setNome(aluno.getNomeResponsavel());
		pagador.setNossoNumero(boleto.getNossoNumero() + "");
		pagador.setUF("SC");
		List<org.aaf.financeiro.model.Boleto> boletos = new ArrayList<>();
		boletos.add(boleto);
		pagador.setBoletos(boletos);

		byte[] pdf = cnab.getBoletoPDF(pagador);

		return pdf;
	}

	public void enviarEmailBoletoAtrasado(String remetente, String senhaRemetente) {
		Calendar c = Calendar.getInstance();
		List<Boleto> boletos = financeiroService.getBoletosAtrasados(c.get(Calendar.MONTH));
		for (Boleto bol : boletos) {
			enviarEmailBoletoAtrasado(bol, remetente, senhaRemetente);
		}
	}

	public void enviarEmailBoletoAtrasado(Boleto bol, String remetente, String senhaRemetente) {

		String destinatario = "";
		if (bol.getPagador().getContatoEmail1() != null) {
			destinatario += bol.getPagador().getContatoEmail1() + ",";
		}
		if (bol.getPagador().getContatoEmail2() != null) {
			destinatario += bol.getPagador().getContatoEmail2() + ",";
		}
		if (bol.getPagador().getEmailMae() != null) {
			destinatario += bol.getPagador().getEmailMae() + ",";
		}
		if (bol.getPagador().getEmailPai() != null) {
			destinatario += bol.getPagador().getEmailPai() + ",";
		}
		String corpoEmail = "<!DOCTYPE html><html><body><p><h2><center>Transporte Escolar Favo de Mel.</center></h2></p><br/><br/>Prezado(a) #nomeResponsavel,<br/><p>Verificamos em nosso sistema que que o boleto com vencimento em  <b>#vencimentoBoleto </b>ainda está em aberto, o boleto encontra-se anexo no e-mail, você pode paga-lo em qualquer agência da Sicoob ou diretamente no escritório.<br/><br/><br/><br/>Caso deseje um boleto atualizado para pagamento em qualquer agência bancária ou pela internet entre em contato com o escritório e solicite.<br/><br/>O valor atual do boleto é R$ <b>#valorAtualBoleto .</b><br/><h4>Caso já tenha efetuado o pagamento favor desconsiderar esse e-mail. </h4></p>"
				+ "<br/>"
				+ "<a href=\"https://ibb.co/i3s83m\"><img src=\"https://preview.ibb.co/hX0Hw6/assinatura_Tefamel.png\" "
				+ "alt=\"assinatura_Tefamel\" style=\"width:365px;height:146px;border:0;\" border=\"0\"></a>"
				+ "</body></html>";
		corpoEmail = corpoEmail.replace("#vencimentoBoleto", Formatador.formataData(bol.getVencimento()));
		corpoEmail = corpoEmail.replace("#valorAtualBoleto", Formatador.valorFormatado(Verificador.getValorFinal(bol)));
		corpoEmail = corpoEmail.replace("#nomeResponsavel", bol.getPagador().getNomeResponsavel());

		byte[] anexoPDF = byteArrayPDFBoleto(getBoletoFinanceiro(bol), bol.getPagador());

		ByteArrayInputStream bais = new ByteArrayInputStream(anexoPDF);
		org.aaf.financeiro.util.EnviadorEmail.enviarEmail("Transporte Escolar Favo de Mel - Boleto Atrasado",
				corpoEmail, bais, destinatario, remetente, senhaRemetente);
		try {
			Thread.sleep(3000);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void enviarEmailBoletosMesAtualEAtrasados(Long idAluno) {
		Boleto boletoMesAtual = getBoletoMesAtual(idAluno);
		if(boletoMesAtual != null){
			enviarEmailBoletosMesAtual(boletoMesAtual);
		}

		List<Boleto> boletosAtrasados = getBoletosAtrasados(idAluno);
		if (boletosAtrasados != null && !boletosAtrasados.isEmpty()) {
			for (Boleto boleto : boletosAtrasados) {
				enviarEmailBoletoAtrasado(boleto, CONSTANTES.emailFinanceiro, CONSTANTES.senhaEmailFinanceiro);
			}
		}
	}

	private List<Boleto> getBoletosAtrasados(Long idAluno) {
		Calendar c = Calendar.getInstance();
		List<Boleto> boletos = financeiroService.getBoletosAtrasadosAluno(c.get(Calendar.MONTH), idAluno);
		return boletos;
	}

	private Boleto getBoletoMesAtual(Long idAluno) {
		Calendar c = Calendar.getInstance();
		Boleto boleto = financeiroService.getBoletoMes(c.get(Calendar.MONTH), idAluno);
		return boleto;
	}

}
