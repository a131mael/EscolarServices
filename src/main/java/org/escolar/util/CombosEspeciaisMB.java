/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.escolar.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.LocalBean;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.model.SelectItem;
import javax.inject.Inject;

import org.escolar.enums.BairroEnum;
import org.escolar.enums.BimestreEnum;
import org.escolar.enums.CustoEnum;
import org.escolar.enums.DisciplinaEnum;
import org.escolar.enums.EscolaEnum;
import org.escolar.enums.EspecialidadeEnum;
import org.escolar.enums.PerioddoEnum;
import org.escolar.enums.Serie;
import org.escolar.enums.TipoMembro;
import org.escolar.model.Carro;
import org.escolar.model.Funcionario;
import org.escolar.service.ProfessorService;
import org.escolar.service.TurmaService;

import br.com.aaf.base.importacao.extrato.ExtratoGruposPagamentoRecebimentoEnum;
import br.com.aaf.base.importacao.extrato.ExtratoTiposEntradaEnum;
import br.com.aaf.base.importacao.extrato.ExtratoTiposEntradaSaidaEnum;

/**
 *
 * @author abimael Fidencio Combos na aplicação.
 */
@ManagedBean(name = "CombosEspeciaisMB")
@LocalBean
@ApplicationScoped
public class CombosEspeciaisMB implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Inject
	private ProfessorService professorService;
	
	@Inject
	private TurmaService carroServeice;

	public ArrayList<SelectItem> getFuncionarios() {
		ArrayList<SelectItem> items = new ArrayList<SelectItem>();
		try {
			// items.add(new SelectItem(null, "Selecione um País"));

			List<Funcionario> professores = professorService.findAll();
			for (Funcionario m : professores) {
				items.add(new SelectItem(m, m.getNome()));
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return items;
	}
	
	public ArrayList<SelectItem> getCarros() {
		ArrayList<SelectItem> items = new ArrayList<SelectItem>();
		try {
			 items.add(new SelectItem(null, "Selecione um Carro"));

			List<Carro> professores = carroServeice.findAll();
			for (Carro m : professores) {
				items.add(new SelectItem(m, m.getNome()));
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return items;
	}

	public ArrayList<SelectItem> getCarrosSelectItem() {
		ArrayList<SelectItem> items = new ArrayList<SelectItem>();
		try {
			 items.add(new SelectItem(null, ""));

			List<Carro> professores = carroServeice.findAll();
			for (Carro m : professores) {
				items.add(new SelectItem(m, m.getNome()));
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return items;
	}
	
	public ArrayList<SelectItem> getSimNao() {
		ArrayList<SelectItem> items = new ArrayList<SelectItem>();
		try {
			 items.add(new SelectItem(false, "Não"));
			 items.add(new SelectItem(true, "Sim"));

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return items;
	}
	
	public ArrayList<SelectItem> getMeses() {
		ArrayList<SelectItem> items = new ArrayList<SelectItem>();
		try {
			 items.add(new SelectItem(-1, "Selecione"));
			 items.add(new SelectItem(0, "Janeiro"));
			 items.add(new SelectItem(1, "Fevereiro"));
			 items.add(new SelectItem(2, "Março"));
			 items.add(new SelectItem(3, "Abril"));
			 items.add(new SelectItem(4, "Maio"));
			 items.add(new SelectItem(5, "junho"));
			 items.add(new SelectItem(6, "Julho"));
			 items.add(new SelectItem(7, "Agosto"));
			 items.add(new SelectItem(8, "Setembro"));
			 items.add(new SelectItem(9, "Outubro"));
			 items.add(new SelectItem(10, "Novembro"));
			 items.add(new SelectItem(11, "Dezembro"));

		} catch (Exception e) {
			e.printStackTrace();
		}
		return items;
	}
	
	public ArrayList<SelectItem> getMesesValorInteiroCorreto() {
		ArrayList<SelectItem> items = new ArrayList<SelectItem>();
		try {
			 items.add(new SelectItem(-1, "Selecione"));
			 items.add(new SelectItem(1, "Janeiro"));
			 items.add(new SelectItem(2, "Fevereiro"));
			 items.add(new SelectItem(3, "Março"));
			 items.add(new SelectItem(4, "Abril"));
			 items.add(new SelectItem(5, "Maio"));
			 items.add(new SelectItem(6, "junho"));
			 items.add(new SelectItem(7, "Julho"));
			 items.add(new SelectItem(8, "Agosto"));
			 items.add(new SelectItem(9, "Setembro"));
			 items.add(new SelectItem(10, "Outubro"));
			 items.add(new SelectItem(11, "Novembro"));
			 items.add(new SelectItem(12, "Dezembro"));

		} catch (Exception e) {
			e.printStackTrace();
		}
		return items;
	}

	public ArrayList<SelectItem> getSimNaoNull() {
		ArrayList<SelectItem> items = new ArrayList<SelectItem>();
		try {
			items.add(new SelectItem(null, ""));
			 items.add(new SelectItem(Boolean.FALSE, "Não"));
			 items.add(new SelectItem(Boolean.TRUE, "Sim"));

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return items;
	}

	
	public ArrayList<SelectItem> getPeriodosSelectIItem() {
		ArrayList<SelectItem> items = new ArrayList<SelectItem>();
		try {
			 items.add(new SelectItem(null, " "));
			for (PerioddoEnum m : PerioddoEnum.values()) {
				items.add(new SelectItem(m, m.getName()));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return items;
	}

	public ArrayList<SelectItem> getEsolasSelectIItem() {
		ArrayList<SelectItem> items = new ArrayList<SelectItem>();
		try {
			 items.add(new SelectItem(null, " "));
			for (EscolaEnum m : EscolaEnum.values()) {
				items.add(new SelectItem(m, m.getName()));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return items;
	}

	
	public ArrayList<SelectItem> getSeriesSelectIItem() {
		ArrayList<SelectItem> items = new ArrayList<SelectItem>();
		try {
			 items.add(new SelectItem(null, " "));
			for (Serie m : Serie.values()) {
				items.add(new SelectItem(m, m.getName()));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return items;
	}
	
	public ArrayList<SelectItem> getBairroSelectIItem() {
		ArrayList<SelectItem> items = new ArrayList<SelectItem>();
		try {
			 items.add(new SelectItem(null, " "));
			for (BairroEnum m : BairroEnum.values()) {
				items.add(new SelectItem(m, m.getName()));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return items;
	}
	
	public ArrayList<SelectItem> getEscolaSelectIItem() {
		ArrayList<SelectItem> items = new ArrayList<SelectItem>();
		try {
			 items.add(new SelectItem(null, " "));
			for (EscolaEnum m : EscolaEnum.values()) {
				items.add(new SelectItem(m, m.getName()));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return items;
	}

	
	public static Serie[] getSeries() {

		return Serie.values();
	}

	public static PerioddoEnum[] getPeriodos() {

		return PerioddoEnum.values();
	}
	
	public static EscolaEnum[] getEscolas() {

		return EscolaEnum.values();
	}
	
	public static Integer[] getAnos() {
		Integer[] anos = {2020,2021,2022,2023,2024,2025};
		return anos;
	}
	
	public static int getUltimaDiaMes(int mes) {
		if(mes ==0){
			return 31;
		}
		if(mes ==1){
			return 28;
		}
		if(mes ==2){
			return 31;
		}
		if(mes ==3){
			return 30;
		}
		if(mes ==4){
			return 31;
		}
		if(mes ==5){
			return 30;
		}
		if(mes ==6){
			return 31;
		}
		if(mes ==7){
			return 31;
		}
		if(mes ==8){
			return 30;
		}
		if(mes ==9){
			return 31;
		}
		if(mes ==10){
			return 30;
		}
		if(mes ==11){
			return 31;
		}
		return 28;
	}
	
	public static BairroEnum[] getBairros() {

		return BairroEnum.values();
	}

	public static EspecialidadeEnum[] getEspecialidades() {

		return EspecialidadeEnum.values();
	}

	public static DisciplinaEnum[] getDisciplinas() {

		return DisciplinaEnum.values();
	}

	public static BimestreEnum[] getBimestres() {

		return BimestreEnum.values();
	}
	
	public static TipoMembro[] getTipoMembro() {

		return TipoMembro.values();
	}
	
	public static ExtratoTiposEntradaSaidaEnum[] getExtratoTiposEntradaSaidaEnum() {
		ExtratoTiposEntradaSaidaEnum[] extratos = ExtratoTiposEntradaSaidaEnum.values();
		
		return extratos;
	}
	
	public static ExtratoTiposEntradaEnum[] getExtratoTiposEntradaEnum() {

		return ExtratoTiposEntradaEnum.values();
	}
	
	public static ExtratoGruposPagamentoRecebimentoEnum[] getExtratoGruposPagamentoRecebimentoEnum() {
		ExtratoGruposPagamentoRecebimentoEnum[] grupos =  ExtratoGruposPagamentoRecebimentoEnum.values();
		
		return grupos;
	}

	public static CustoEnum[] getTipoCusto() {

		return CustoEnum.values();
	}
	
	public ArrayList<SelectItem> getExtratoGruposPagamentoRecebimentoEnumSelectItem() {
		ArrayList<SelectItem> items = new ArrayList<SelectItem>();
		try {
			 items.add(new SelectItem(null, " "));
			for (ExtratoGruposPagamentoRecebimentoEnum m : ExtratoGruposPagamentoRecebimentoEnum.values()) {
				items.add(new SelectItem(m, m.getNome()));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return items;
	}
	
	public ArrayList<SelectItem> getExtratoTiposEntradaSelectItem() {
		ArrayList<SelectItem> items = new ArrayList<SelectItem>();
		try {
			 items.add(new SelectItem(null, " "));
			for (ExtratoTiposEntradaEnum m : ExtratoTiposEntradaEnum.values()) {
				items.add(new SelectItem(m, m.getNameReal()));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return items;
	}
	
	public ArrayList<SelectItem> getExtratoTiposEntradaSaidaSelectItem() {
		ArrayList<SelectItem> items = new ArrayList<SelectItem>();
		try {
			 items.add(new SelectItem(null, " "));
			for (ExtratoTiposEntradaSaidaEnum m : ExtratoTiposEntradaSaidaEnum.values()) {
				items.add(new SelectItem(m, m.getNome()));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return items;
	}

	
}
