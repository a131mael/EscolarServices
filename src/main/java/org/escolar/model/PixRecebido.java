package org.escolar.model;

import java.io.Serializable;

public class PixRecebido implements Serializable {

    private static final long serialVersionUID = 1L;

    private String endToEndId;
    private String txid;
    private String valor;
    private String horario;
    private String nomePagador;
    private String cpfPagador;
    private String infoPagador;

    public String getEndToEndId() { return endToEndId; }
    public void setEndToEndId(String endToEndId) { this.endToEndId = endToEndId; }

    public String getTxid() { return txid; }
    public void setTxid(String txid) { this.txid = txid; }

    public String getValor() { return valor; }
    public void setValor(String valor) { this.valor = valor; }

    public String getHorario() { return horario; }
    public void setHorario(String horario) { this.horario = horario; }

    public String getNomePagador() { return nomePagador; }
    public void setNomePagador(String nomePagador) { this.nomePagador = nomePagador; }

    public String getCpfPagador() { return cpfPagador; }
    public void setCpfPagador(String cpfPagador) { this.cpfPagador = cpfPagador; }

    public String getInfoPagador() { return infoPagador; }
    public void setInfoPagador(String infoPagador) { this.infoPagador = infoPagador; }
}
