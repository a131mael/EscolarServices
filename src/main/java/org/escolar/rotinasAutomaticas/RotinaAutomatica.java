package org.escolar.rotinasAutomaticas;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import org.escolar.service.AlunoService;

@Singleton
@Startup
public class RotinaAutomatica {

	@Inject
	private AlunoService alunoService;

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


}
