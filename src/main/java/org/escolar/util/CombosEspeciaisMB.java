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

	public static CustoEnum[] getTipoCusto() {

		return CustoEnum.values();
	}

	
}
