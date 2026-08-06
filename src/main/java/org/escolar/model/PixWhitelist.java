package org.escolar.model;

import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name = "pix_whitelist")
public class PixWhitelist implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "chave_pix", unique = true, nullable = false, length = 100)
    private String chavePix;

    @Column(length = 200)
    private String descricao;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getChavePix() { return chavePix; }
    public void setChavePix(String chavePix) { this.chavePix = chavePix; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
}
