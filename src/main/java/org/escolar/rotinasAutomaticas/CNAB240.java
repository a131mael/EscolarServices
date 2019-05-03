/*
d * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.escolar.rotinasAutomaticas;

import java.io.File;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.aaf.financeiro.model.Pagador;
import org.aaf.financeiro.sicoob.util.CNAB240_REMESSA_SICOOB;
import org.aaf.financeiro.sicoob.util.CNAB240_RETORNO_SICOOB;
import org.aaf.financeiro.util.ImportadorArquivo;
import org.aaf.financeiro.util.OfficeUtil;
import org.aaf.financeiro.util.constantes.Constante;
import org.escolar.enums.StatusBoletoEnum;
import org.escolar.model.Aluno;
import org.escolar.model.Boleto;
import org.escolar.model.ContratoAluno;
import org.escolar.service.ConfiguracaoService;
import org.escolar.service.FinanceiroService;
import org.escolar.util.FileUtils;
import org.escolar.util.Verificador;

@Stateless
@LocalBean
public class CNAB240 {

	@Inject
	private ConfiguracaoService configuracaoService;

	@Inject
	private FinanceiroService financeiroService;

	public byte[] gerarCNB240(int projeto, Aluno aluno, ContratoAluno contrato) {
		try {

			String sequencialArquivo = configuracaoService.getSequencialArquivo() + "";

			Pagador pagador = new Pagador();
			pagador.setBairro(contrato.getBairro());
			pagador.setCep(contrato.getCep());
			pagador.setCidade(contrato.getCidade() != null ? contrato.getCidade() : "PALHOCA");
			pagador.setCpfCNPJ(contrato.getCpfResponsavel());
			pagador.setEndereco(contrato.getEndereco());
			pagador.setNome(contrato.getNomeResponsavel());
			pagador.setNossoNumero(contrato.getNumero());
			pagador.setUF("SC");
			pagador.setBoletos(contrato.getBoletosFinanceiro());
			CNAB240_REMESSA_SICOOB cnbRemessa = new CNAB240_REMESSA_SICOOB(projeto);
			byte[] arquivo = cnbRemessa.geraRemessa(pagador, sequencialArquivo);

			try {
				configuracaoService.incrementaSequencialArquivoCNAB();
				return arquivo;

			} catch (Exception e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public byte[] gerarCNB240Baixa(int projeto, Boleto b) {
		try {
			ContratoAluno contrato = b.getContrato();

			if (contrato == null) {
				contrato = configuracaoService.findContrato(b.getId());

				if (contrato.getCpfResponsavel() != null && !contrato.getCpfResponsavel().equalsIgnoreCase("")) {
					if (contrato.getNomeResponsavel() != null && !contrato.getNomeResponsavel().equalsIgnoreCase("")) {
						if (contrato.getEndereco() != null && !contrato.getEndereco().equalsIgnoreCase("")) {
							if (contrato.getCep() == null || contrato.getCep().equalsIgnoreCase("")) {
								contrato.setCep("88132700");
							}
							if (contrato.getBairro() == null || contrato.getBairro().equalsIgnoreCase("")) {
								contrato.setBairro("Bela Vista");
							}
							if (contrato.getCidade() == null || contrato.getCidade().equalsIgnoreCase("")) {
								contrato.setCidade("Palhoca");
							}
						}
					}
				}
			}

			String sequencialArquivo = configuracaoService.getSequencialArquivo() + "";

			Pagador pagador = new Pagador();
			pagador.setBairro(contrato.getBairro());
			pagador.setCep(contrato.getCep());
			pagador.setCidade(contrato.getCidade() != null ? contrato.getCidade() : "PALHOCA");
			pagador.setCpfCNPJ(contrato.getCpfResponsavel());
			pagador.setEndereco(contrato.getEndereco());
			pagador.setNome(contrato.getNomeResponsavel());
			pagador.setNossoNumero(contrato.getNumero());
			pagador.setUF("SC");
			ArrayList<Boleto> boletos = new ArrayList<>();
			boletos.add(b);
			pagador.setBoletos(getBoletosFinanceiro(boletos));
			CNAB240_REMESSA_SICOOB cnbRemessa = new CNAB240_REMESSA_SICOOB(projeto);
			byte[] arquivo = cnbRemessa.geraBaixa(pagador, sequencialArquivo);

			try {
				configuracaoService.incrementaSequencialArquivoCNAB();
				return arquivo;

			} catch (Exception e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void gerarBaixaBoletosPagos(Boleto b, String path) {
		byte[] arquivo = gerarCNB240Baixa(CONSTANTES.projeto, b);
		String nomeArquivo = "CNAB240_" + b.getNossoNumero() + ".txt";
		ImportadorArquivo.geraArquivoFisico(arquivo, path + nomeArquivo);
		b.setBaixaGerada(true);
		financeiroService.save(b);
	}

	public void gerarBaixaBoletosCancelados(Boleto b, String path) {
			byte[] arquivo = gerarCNB240Baixa(CONSTANTES.projeto, b);
			String nomeArquivo = "CNAB240_" + b.getNossoNumero() + ".txt";
			ImportadorArquivo.geraArquivoFisico(arquivo, path + nomeArquivo);
			b.setBaixaGerada(true);
			b.setCancelado(true);
			b.setValorPago((double) 0);
			b.setCnabCanceladoEnviado(true);
			financeiroService.save(b);
	}

	public void gerarCNABAlunos() {
		List<Aluno> alunosSemCNABENVIADO = financeiroService.getAlunosCNABNaoEnviado();
		for (Aluno al : alunosSemCNABENVIADO) {
			for (ContratoAluno contrato : al.getContratosVigentes()) {
				byte[] arquivo = gerarCNB240(CONSTANTES.projeto, al, contrato);
				String nomeArquivo = "CNAB240_" + al.getCodigo() + ".txt";
				ImportadorArquivo.geraArquivoFisico(arquivo, CONSTANTES.PATH_ENVIAR_CNAB + nomeArquivo);
				financeiroService.saveCNABENviado(al);
			}
		}
	}

	public void gerarBaixaBoletosPagos() {
		List<Boleto> boletoParaBaixa = financeiroService.getBoletosParaBaixa();
		for (Boleto b : boletoParaBaixa) {
			gerarBaixaBoletosPagos(b, CONSTANTES.PATH_ENVIAR_BAIXA);
		}
	}

	public void gerarBaixaBoletoAlunosCancelados() {
		List<ContratoAluno> contratos = financeiroService.getAlunosRemovidos();
		for (ContratoAluno contrato : contratos) {
			for (Boleto b : contrato.getBoletos()) {
				gerarBaixaBoletosCancelados(b, CONSTANTES.PATH_ENVIAR_BAIXA_CANCELADOS);
			}
		}
	}

	private List<org.aaf.financeiro.model.Boleto> getBoletosFinanceiro(List<Boleto> boletos) {
		List<org.aaf.financeiro.model.Boleto> boletosFinanceiro = new ArrayList<>();
		if (boletos != null) {
			for (Boleto boleto : boletos) {
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

	public void importarPagamentosCNAB240() {
		try {
			List<Pagador> boletosImportados = CNAB240_RETORNO_SICOOB.imporCNAB240(Constante.LOCAL_ARMAZENAMENTO_REMESSA);
			importarBoletos(boletosImportados, false);

		} catch (Exception e) {

		}
		/*try {
			ImportadorArquivo arquivo = new ImportadorArquivo();

			List<Pagador> boletosImportados = arquivo.importarExtratoBancarioConciliacao(1);
			importarBoletos(boletosImportados, true);

		} catch (Exception e) {

		}*/

	}

	public void importarBoletos(List<Pagador> boletosImportados, boolean extratoBancario) throws ParseException {
		for (Pagador pagador : boletosImportados) {
			org.aaf.financeiro.model.Boleto boletoCNAB = pagador.getBoletos().get(0);
			String numeroDocumento = boletoCNAB.getNossoNumero();
			if (numeroDocumento != null && !numeroDocumento.equalsIgnoreCase("") && !numeroDocumento.contains("-")) {
				try {
					numeroDocumento = numeroDocumento.trim().replace(" ", "").replace("/",
							"".replace("-", "").replace(".", ""));
					if (numeroDocumento.matches("^[0-9]*$")) {
						Long numeroDocumentoLong = Long.parseLong(numeroDocumento);
						org.escolar.model.Boleto boleto = null;
						if (!extratoBancario) {
							numeroDocumentoLong -= 10000;
							boleto = financeiroService.findBoletoByID(numeroDocumentoLong);
						} else {
							String numeroDocumentoExtrato = String.valueOf(numeroDocumentoLong);
							boleto = financeiroService.findBoletoByNumeroModel(
									numeroDocumentoExtrato.substring(0, numeroDocumentoExtrato.length() - 1));
						}
						if (boleto != null) {
							if (!Verificador.getStatusEnum(boleto).equals(StatusBoletoEnum.PAGO)) {
								if (!(boletoCNAB.isDecurso() != null && boletoCNAB.isDecurso())) {
									boleto.setValorPago(boletoCNAB.getValorPago());
									boleto.setDataPagamento(OfficeUtil.retornaData(boletoCNAB.getDataPagamento()));
									boleto.setConciliacaoPorExtrato(extratoBancario);
									financeiroService.save(boleto);
								}else{
									if ((boletoCNAB.isDecurso() != null && boletoCNAB.isDecurso())) {
										boleto.setValorPago(0D);
										boleto.setDataPagamento(null);
										financeiroService.saveProtestar(boleto);
									}
								}

							}
						}
					}

				} catch (ClassCastException cce) {
					cce.printStackTrace();
				}
			}
		}
	}

}
