package org.escolar.util;

import java.io.IOException;
import java.util.HashMap;

import javax.faces.context.FacesContext;

import org.escolar.controller.OfficeDOCUtil;
import org.escolar.model.Aluno;

public class ImpressoesUtils {

	private static OfficeDOCUtil officeDOCUtil = new OfficeDOCUtil();

	public static void imprimirInformacoesAluno(Aluno aluno, String modelo, HashMap<String, String> trocas,
			String nomeArquivoSaida) throws IOException {

		try {
			officeDOCUtil.editDoc2(modelo, trocas, nomeArquivoSaida);

			/*
			 * String caminho =
			 * FacesContext.getCurrentInstance().getExternalContext().
			 * getRealPath("/") + "\\"+nomeArquivoSaida + ".doc"; Process pro =
			 * Runtime.getRuntime().exec("cmd.exe /c  " + caminho);
			 * pro.waitFor(); System.out.println("ccc");
			 */

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void gerarArquivoFisico(String modelo, HashMap<String, String> trocas, String nomeArquivoSaida) throws IOException {

		try {
			officeDOCUtil.editDoc2(modelo, trocas, nomeArquivoSaida);

			/*
			 * String caminho =
			 * FacesContext.getCurrentInstance().getExternalContext().
			 * getRealPath("/") + "\\"+nomeArquivoSaida + ".doc"; Process pro =
			 * Runtime.getRuntime().exec("cmd.exe /c  " + caminho);
			 * pro.waitFor(); System.out.println("ccc");
			 */

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void imprimirInformacoesAluno(String modelo, HashMap<String, String> trocas,
			String nomeArquivoSaida) throws IOException {

		try {
			officeDOCUtil.editDoc2(modelo, trocas, nomeArquivoSaida);

			/*
			 * String caminho =
			 * FacesContext.getCurrentInstance().getExternalContext().
			 * getRealPath("/") + "\\"+nomeArquivoSaida + ".doc"; Process pro =
			 * Runtime.getRuntime().exec("cmd.exe /c  " + caminho);
			 * pro.waitFor(); System.out.println("ccc");
			 */

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
}
