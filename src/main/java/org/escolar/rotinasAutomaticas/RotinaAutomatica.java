package org.escolar.rotinasAutomaticas;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import org.escolar.service.AlunoService;
import org.escolar.service.ExtratoBancarioService;

@Singleton
@Startup
public class RotinaAutomatica {

	@Inject
	private AlunoService alunoService;
	
	@Inject
	private ExtratoBancarioService extratoBancarioService;

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
	
	@Schedule(hour="*/3",minute="*",  persistent = false)
	public void ImportarExtratoBancario() {
		try {
			System.out.println("Importando Extrato ");
			extratoBancarioService.lerExtrato(CONSTANTES.PATH_EXTRATO_BANCARIO_ENVIAR);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Schedule(hour="*",minute="*/33", dayOfMonth="5-7",  persistent = false)
	public void enviarBoletoEmail() {
		try {
			System.out.println("Enviando Boleto por email");
			EnviadorEmail enviador = new EnviadorEmail();
			enviador.enviarEmailBoletosMesAtual();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}
