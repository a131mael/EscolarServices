/*

 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.escola.util;

import org.escola.enums.TipoMembro;

/**
 *Constantes utilizadas no modulo EJB
 * 
 * @author Abimael Fidencio
 */
public class Constant {
    
    private String ContextoGlobalEJB = "java:global";
    private String Projeto = "Escolar";
    private String ProjetoEJB = "";
    private String barra = "/";
    
    public static int anoLetivoAtual = 2018;
    
    private static TipoMembro SECRETARIA = TipoMembro.SECRETARIA; 
    
    private static TipoMembro PROFESSOR = TipoMembro.MOTORISTA;
    
    private static TipoMembro ALUNO = TipoMembro.ALUNO;
    
    private static TipoMembro ADMINISTRADOR = TipoMembro.ADMIM;
    
    
    public static long idCarro1 = 21;
    public static long idCarro2 = 23;
    public static long idCarro3 = 24;
    public static long idCarro4 = 27;
    public static long idCarro5 = 32;
    
    
    static{
    	 SECRETARIA = TipoMembro.SECRETARIA; 
    	 PROFESSOR = TipoMembro.MONITOR;
    	 ALUNO = TipoMembro.ALUNO;
    	 ADMINISTRADOR = TipoMembro.ADMIM;
    }
    
    public String getContextoGlobalEJB() {
		return ContextoGlobalEJB;
	}
	public void setContextoGlobalEJB(String contextoGlobalEJB) {
		ContextoGlobalEJB = contextoGlobalEJB;
	}
	public String getProjeto() {
		return Projeto;
	}
	public void setProjeto(String projeto) {
		Projeto = projeto;
	}
	public String getProjetoEJB() {
		return ProjetoEJB;
	}
	public void setProjetoEJB(String projetoEJB) {
		ProjetoEJB = projetoEJB;
	}
	public String getBarra() {
		return barra;
	}
	public void setBarra(String barra) {
		this.barra = barra;
	}
	public static TipoMembro getSECRETARIA() {
		return SECRETARIA;
	}
	public static void setSECRETARIA(TipoMembro sECRETARIA) {
		SECRETARIA = sECRETARIA;
	}
	public static TipoMembro getPROFESSOR() {
		return PROFESSOR;
	}
	public static void setPROFESSOR(TipoMembro pROFESSOR) {
		PROFESSOR = pROFESSOR;
	}
	public static TipoMembro getALUNO() {
		return ALUNO;
	}
	public static void setALUNO(TipoMembro aLUNO) {
		ALUNO = aLUNO;
	}
	public static TipoMembro getADMINISTRADOR() {
		return ADMINISTRADOR;
	}
	public static void setADMINISTRADOR(TipoMembro aDMINISTRADOR) {
		ADMINISTRADOR = aDMINISTRADOR;
	}
    
}
