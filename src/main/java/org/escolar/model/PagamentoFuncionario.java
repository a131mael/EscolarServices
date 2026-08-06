package org.escolar.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class PagamentoFuncionario implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "GENERATE_pgto_func", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "GENERATE_pgto_func", sequenceName = "pagamento_funcionario_pk_seq", allocationSize = 1)
    private Long id;

    @ManyToOne
    private Funcionario funcionario;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataPagamento;

    @Column
    private Double valor;

    @Column
    private String endToEndId;

    @Column
    private String nomeProprietarioSicoob;

    @Column
    private String chavePix;

    @Column
    private String tipoChavePix;

    @Column
    private String usuarioAutorizou;

    @Column
    private String status;

    @Column(length = 1000)
    private String observacao;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Funcionario getFuncionario() { return funcionario; }
    public void setFuncionario(Funcionario funcionario) { this.funcionario = funcionario; }

    public Date getDataPagamento() { return dataPagamento; }
    public void setDataPagamento(Date dataPagamento) { this.dataPagamento = dataPagamento; }

    public Double getValor() { return valor; }
    public void setValor(Double valor) { this.valor = valor; }

    public String getEndToEndId() { return endToEndId; }
    public void setEndToEndId(String endToEndId) { this.endToEndId = endToEndId; }

    public String getNomeProprietarioSicoob() { return nomeProprietarioSicoob; }
    public void setNomeProprietarioSicoob(String nomeProprietarioSicoob) { this.nomeProprietarioSicoob = nomeProprietarioSicoob; }

    public String getChavePix() { return chavePix; }
    public void setChavePix(String chavePix) { this.chavePix = chavePix; }

    public String getTipoChavePix() { return tipoChavePix; }
    public void setTipoChavePix(String tipoChavePix) { this.tipoChavePix = tipoChavePix; }

    public String getUsuarioAutorizou() { return usuarioAutorizou; }
    public void setUsuarioAutorizou(String usuarioAutorizou) { this.usuarioAutorizou = usuarioAutorizou; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }
}
