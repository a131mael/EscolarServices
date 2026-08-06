package org.escolar.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.*;

@Entity
@Table(name = "abastecimento")
public class Abastecimento implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    private Date data;

    private BigDecimal litros;

    @Column(name = "valor_por_litro")
    private BigDecimal valorPorLitro;

    @Column(name = "valor_total")
    private BigDecimal valorTotal;

    @Column(name = "end_to_end_id")
    private String endToEndId;

    @Column(name = "nome_destinatario")
    private String nomeDestinatario;

    @Column(name = "status_pix", length = 20)
    private String statusPix;

    @Column(name = "erro_pix", length = 2000)
    private String erroPix;

    @Column(name = "whatsapp_enviado")
    private boolean whatsappEnviado;

    @Lob
    @Column(name = "comprovante_pdf")
    private byte[] comprovantePdf;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Date getData() { return data; }
    public void setData(Date data) { this.data = data; }

    public BigDecimal getLitros() { return litros; }
    public void setLitros(BigDecimal litros) { this.litros = litros; }

    public BigDecimal getValorPorLitro() { return valorPorLitro; }
    public void setValorPorLitro(BigDecimal valorPorLitro) { this.valorPorLitro = valorPorLitro; }

    public BigDecimal getValorTotal() { return valorTotal; }
    public void setValorTotal(BigDecimal valorTotal) { this.valorTotal = valorTotal; }

    public String getEndToEndId() { return endToEndId; }
    public void setEndToEndId(String endToEndId) { this.endToEndId = endToEndId; }

    public String getNomeDestinatario() { return nomeDestinatario; }
    public void setNomeDestinatario(String nomeDestinatario) { this.nomeDestinatario = nomeDestinatario; }

    public String getStatusPix() { return statusPix; }
    public void setStatusPix(String statusPix) { this.statusPix = statusPix; }

    public String getErroPix() { return erroPix; }
    public void setErroPix(String erroPix) { this.erroPix = erroPix; }

    public boolean isWhatsappEnviado() { return whatsappEnviado; }
    public void setWhatsappEnviado(boolean whatsappEnviado) { this.whatsappEnviado = whatsappEnviado; }

    public byte[] getComprovantePdf() { return comprovantePdf; }
    public void setComprovantePdf(byte[] comprovantePdf) { this.comprovantePdf = comprovantePdf; }
}
