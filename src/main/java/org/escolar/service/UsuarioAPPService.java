package org.escolar.service;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.aaf.escolar.MemberDTO;

import java.util.List;

@Singleton
@Startup // Garante que será inicializado ao subir a aplicação
public class UsuarioAPPService {

    private List<MemberDTO> usuariosValidosManha;
    private List<MemberDTO> usuariosValidosMeioDIa;
    private List<MemberDTO> usuariosValidosNoite;
    
    @EJB
    private MemberRegistration memberRegistration;

    @PostConstruct
    public void inicializar() {
        this.setUsuariosValidosManha(carregarUsuariosValidos("manha"));
        this.setUsuariosValidosMeioDIa(carregarUsuariosValidos("meio_dia"));
        this.setUsuariosValidosNoite(carregarUsuariosValidos("tarde"));
    }

    public void atualizarUsuariosValidos() {
    	 this.setUsuariosValidosManha(carregarUsuariosValidos("manha"));
         this.setUsuariosValidosMeioDIa(carregarUsuariosValidos("meio_dia"));
         this.setUsuariosValidosNoite(carregarUsuariosValidos("tarde"));
    }

    @Schedule(hour = "0", minute = "0", second = "0", persistent = false)
    public void rotinaNoturnaAtualizarUsuarios() {
        System.out.println("Rotina noturna: atualizando lista de usuários válidos...");
        atualizarUsuariosValidos();
    }

    private List<MemberDTO> carregarUsuariosValidos(String periodo) {
        return memberRegistration.findMemberValidoAPPDTO(periodo);
    }

	public List<MemberDTO> getUsuariosValidosManha() {
		return usuariosValidosManha;
	}

	public void setUsuariosValidosManha(List<MemberDTO> usuariosValidosManha) {
		this.usuariosValidosManha = usuariosValidosManha;
	}

	public List<MemberDTO> getUsuariosValidosMeioDIa() {
		return usuariosValidosMeioDIa;
	}

	public void setUsuariosValidosMeioDIa(List<MemberDTO> usuariosValidosMeioDIa) {
		this.usuariosValidosMeioDIa = usuariosValidosMeioDIa;
	}

	public List<MemberDTO> getUsuariosValidosNoite() {
		return usuariosValidosNoite;
	}

	public void setUsuariosValidosNoite(List<MemberDTO> usuariosValidosNoite) {
		this.usuariosValidosNoite = usuariosValidosNoite;
	}
}
