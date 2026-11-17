package org.escolar.util;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.aaf.financeiro.util.OfficeUtil;
import org.escolar.model.Boleto;

public class Formatador {

	public static String formataData(Date data){
		SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
		return formato.format(data);
	}

	public static Date formatDateSomenteDiaMesAno(Date data){
		SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
		Date dataFormatada = null;
		try {
			dataFormatada = formato.parse(formataData(data));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dataFormatada;
	}
	
	public static String valorFormatado(Double valorF){
		BigDecimal valor = new BigDecimal (valorF);
		NumberFormat nf = NumberFormat.getCurrencyInstance();
		String formatado = nf.format (valor);
		return formatado;
	}

	// Numero no padrao BR (virgula decimal, ponto de milhar), sem simbolo de
	// moeda -- usado em contratos onde o "R$" ja vem fixo no texto/template.
	public static String formataValorBR(double valor){
		NumberFormat nf = NumberFormat.getNumberInstance(new Locale("pt", "BR"));
		nf.setMinimumFractionDigits(2);
		nf.setMaximumFractionDigits(2);
		return nf.format(valor);
	}

	public static String formataCpf(String cpf){
		if (cpf == null) {
			return cpf;
		}
		String digitos = cpf.replaceAll("[^0-9]", "");
		if (digitos.length() != 11) {
			return cpf;
		}
		return digitos.substring(0, 3) + "." + digitos.substring(3, 6) + "." + digitos.substring(6, 9) + "-"
				+ digitos.substring(9, 11);
	}
	
	public static CharSequence getMes(Date vencimento) {
		System.out.println(vencimento + " :Vencimento do boleto");
		Calendar c = Calendar.getInstance();
		c.setTime(vencimento);
		int mes = c.get(Calendar.MONTH);
		String mesString = "";
		switch (mes) {
		case 0:
			mesString = "Janeiro";
			break;

		case 1:
			mesString = "Fevereiro";
			break;

		case 2:
			mesString = "Março";
			break;

		case 3:
			mesString = "Abril";
			break;

		case 4:
			mesString = "Maio";
			break;

		case 5:
			mesString = "Junho";
			break;

		case 6:
			mesString = "Julho";
			break;

		case 7:
			mesString = "Agosto";
			break;

		case 8:
			mesString = "Setembro";
			break;
		case 9:
			mesString = "Outubro";
			break;
		case 10:
			mesString = "Novembro";
			break;
		case 11:
			mesString = "Dezembro";
			break;

		default:
			break;
		}

		return mesString;
	}

	public static List<org.aaf.financeiro.model.Boleto> getBoletosFinanceiro(List<Boleto> boletos) {
		List<org.aaf.financeiro.model.Boleto> boletosFinanceiro = new ArrayList<>();
		if(boletos!= null){
			for(Boleto boleto : boletos){
				org.aaf.financeiro.model.Boleto boletoFinanceiro = new org.aaf.financeiro.model.Boleto();
				boletoFinanceiro.setEmissao(boleto.getEmissao());
				boletoFinanceiro.setId(boleto.getId());
				boletoFinanceiro.setValorNominal(boleto.getValorNominal());
				boletoFinanceiro.setVencimento(boleto.getVencimento());
				boletoFinanceiro.setNossoNumero(String.valueOf(boleto.getNossoNumero()));
				boletoFinanceiro.setDataPagamento(OfficeUtil.retornaDataSomenteNumeros(boleto.getDataPagamento()));
				boletoFinanceiro.setValorPago(boleto.getValorPago());
				boletosFinanceiro.add(boletoFinanceiro);
			}
		}
		return boletosFinanceiro;
	}
	
}
