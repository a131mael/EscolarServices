package org.escolar.service;

import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;

import javax.ejb.Stateless;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.transform.dom.DOMResult;

import org.escolar.model.Carro;
import org.escolar.model.Configuracao;
import org.escolar.model.Contratante;
import org.escolar.model.Frete;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import br.com.swconsultoria.certificado.Certificado;
import br.com.swconsultoria.certificado.CertificadoService;
import br.com.swconsultoria.certificado.exception.CertificadoException;
import br.com.swconsultoria.cte.Cte;
import br.com.swconsultoria.cte.dom.ConfiguracoesCte;
import br.com.swconsultoria.cte.dom.enuns.AmbienteEnum;
import br.com.swconsultoria.cte.dom.enuns.EstadosEnum;
import br.com.swconsultoria.cte.exception.CteException;
import br.com.swconsultoria.cte.schema_400.RodoOS;
import br.com.swconsultoria.cte.schema_400.TCTeOS;
import br.com.swconsultoria.cte.schema_400.TEndeEmi;
import br.com.swconsultoria.cte.schema_400.TEndereco;
import br.com.swconsultoria.cte.schema_400.TImpOS;
import br.com.swconsultoria.cte.schema_400.TProtCTeOS;
import br.com.swconsultoria.cte.schema_400.TRetCTeOS;
import br.com.swconsultoria.cte.schema_400.TUFSemEX;
import br.com.swconsultoria.cte.schema_400.TUf;
import br.com.swconsultoria.cte.schema_400_eventos.EvCancCTe;
import br.com.swconsultoria.cte.schema_400_eventos.TEvento;
import br.com.swconsultoria.cte.schema_400_eventos.TRetEvento;
import br.com.swconsultoria.cte.util.ObjetoCTeUtil;
import br.com.swconsultoria.cte.util.XmlCteUtil;

/**
 * Monta, assina e transmite o CT-e OS (modelo 67) a partir de uma Viagem (Frete)
 * e da Configuracao fiscal do emitente, usando o certificado digital A1
 * configurado em {@link Configuracao#getCertificadoPath()}.
 */
@Stateless
public class CTeOSService {

    private static final String VERSAO = "4.00";

    /** Texto exigido pela SEFAZ no tomador quando tpAmb = 2 (Homologacao) */
    private static final String LITERAL_HOMOLOGACAO = "CT-E EMITIDO EM AMBIENTE DE HOMOLOGACAO - SEM VALOR FISCAL";

    /** Reserva o proximo numero/serie de CT-e na Configuracao, se a viagem ainda nao tiver um. */
    public void prepararNumeracao(Frete frete, Configuracao config) {
        // Um nCT ja AUTORIZADO esta consumido na SEFAZ (mesmo CNPJ+serie+nCT+tpAmb com chave
        // diferente == "duplicidade", cStat 539). Reemissao apos AUTORIZADO precisa de nCT novo.
        if (frete.getNumeroCte() == null || "AUTORIZADO".equals(frete.getStatusCte())) {
            long proximo = config.getProximoNumeroCte() == null ? 1L : config.getProximoNumeroCte();
            frete.setNumeroCte((int) proximo);
            frete.setSerieCte(config.getSerieCte() == null ? 1 : config.getSerieCte());
            config.setProximoNumeroCte(proximo + 1);
        }
    }

    /** Gera o XML do CT-e OS (nao assinado) para conferencia, ja calculando a chave de acesso. */
    public String gerarXmlPreview(Frete frete, Configuracao config)
            throws JAXBException, CteException, CertificadoException, FileNotFoundException {
        ConfiguracoesCte configCte = criarConfiguracoesCte(config);
        return XmlCteUtil.objectToXml(montarCteOS(frete, config, configCte));
    }

    /** Monta a ConfiguracoesCte (UF, ambiente e certificado) usada para montar/assinar/transmitir o CT-e OS. */
    private ConfiguracoesCte criarConfiguracoesCte(Configuracao config) throws CertificadoException, FileNotFoundException {
        Certificado certificado = CertificadoService.certificadoPfx(config.getCertificadoPath(), config.getCertificadoSenha());
        AmbienteEnum ambiente = "1".equals(config.getTpAmb()) ? AmbienteEnum.PRODUCAO : AmbienteEnum.HOMOLOGACAO;
        return ConfiguracoesCte.criarConfiguracoes(EstadosEnum.SC, ambiente, certificado, null);
    }

    /**
     * Monta, assina com o certificado A1 configurado e transmite o CT-e OS para a SEFAZ.
     * Atualiza no {@code frete} o status, protocolo, motivo e o XML assinado retornados.
     */
    public TRetCTeOS emitirCteOS(Frete frete, Configuracao config)
            throws CteException, CertificadoException, JAXBException, FileNotFoundException {

        prepararNumeracao(frete, config);
        ConfiguracoesCte configCte = criarConfiguracoesCte(config);
        TCTeOS cteOS = montarCteOS(frete, config, configCte);

        TCTeOS cteOSAssinado = Cte.montaCteOS(configCte, cteOS, false);
        TRetCTeOS retorno = Cte.enviarCteOS(configCte, cteOSAssinado);

        if ("539".equals(retorno.getCStat())) {
            // nCT atual ja foi AUTORIZADO antes (em outra tentativa) com chave diferente.
            // Pula pro proximo nCT disponivel e tenta novamente.
            long proximo = Math.max(frete.getNumeroCte() + 1,
                    config.getProximoNumeroCte() == null ? 1L : config.getProximoNumeroCte());
            frete.setNumeroCte((int) proximo);
            config.setProximoNumeroCte(proximo + 1);

            cteOS = montarCteOS(frete, config, configCte);
            cteOSAssinado = Cte.montaCteOS(configCte, cteOS, false);
            retorno = Cte.enviarCteOS(configCte, cteOSAssinado);
        }

        frete.setXmlCteOS(XmlCteUtil.objectToXml(cteOSAssinado));
        frete.setMotivoCte(retorno.getCStat() + " - " + retorno.getXMotivo());

        if ("100".equals(retorno.getCStat()) && retorno.getProtCTe() != null) {
            TProtCTeOS.InfProt infProt = retorno.getProtCTe().getInfProt();
            frete.setStatusCte("AUTORIZADO");
            frete.setProtocoloCte(infProt.getNProt());
            frete.setDataAutorizacaoCte(infProt.getDhRecbto());
        } else {
            frete.setStatusCte("REJEITADO");
            frete.setProtocoloCte(null);
            frete.setDataAutorizacaoCte(null);
        }

        return retorno;
    }

    /**
     * Envia o evento de cancelamento (tpEvento 110111) do CT-e OS ja autorizado para a SEFAZ.
     * Em caso de homologacao (cStat 135/136), atualiza o status do {@code frete} para CANCELADO.
     */
    public TRetEvento cancelarCteOS(Frete frete, Configuracao config, String justificativa)
            throws CteException, CertificadoException, JAXBException, FileNotFoundException {

        ConfiguracoesCte configCte = criarConfiguracoesCte(config);

        EvCancCTe evCancCTe = new EvCancCTe();
        evCancCTe.setDescEvento("Cancelamento");
        evCancCTe.setNProt(frete.getProtocoloCte());
        evCancCTe.setXJust(justificativa);

        Element detEventoEl = ObjetoCTeUtil.objectToElement(evCancCTe, EvCancCTe.class, "evCancCTe");

        TEvento.InfEvento.DetEvento detEvento = new TEvento.InfEvento.DetEvento();
        detEvento.setVersaoEvento(VERSAO);
        detEvento.setAny(detEventoEl);

        String chave = frete.getChaveCte();

        TEvento.InfEvento infEvento = new TEvento.InfEvento();
        infEvento.setId("ID110111" + chave + "001");
        infEvento.setCOrgao(configCte.getEstado().getCodigoUF());
        infEvento.setTpAmb(config.getTpAmb());
        infEvento.setCNPJ(somenteNumeros(config.getCnpj()));
        infEvento.setChCTe(chave);
        infEvento.setDhEvento(formatarDataHora(new Date()));
        infEvento.setTpEvento("110111");
        infEvento.setNSeqEvento("01");
        infEvento.setDetEvento(detEvento);

        TEvento evento = new TEvento();
        evento.setVersao(VERSAO);
        evento.setInfEvento(infEvento);

        TRetEvento retorno = Cte.cancelarCte(configCte, evento, false);
        TRetEvento.InfEvento retInfEvento = retorno.getInfEvento();

        frete.setMotivoCte(retInfEvento.getCStat() + " - " + retInfEvento.getXMotivo());

        if ("135".equals(retInfEvento.getCStat()) || "136".equals(retInfEvento.getCStat())) {
            frete.setStatusCte("CANCELADO");
            frete.setProtocoloCancelamentoCte(retInfEvento.getNProt());
            frete.setDataCancelamentoCte(retInfEvento.getDhRegEvento());
            frete.setJustificativaCancelamentoCte(justificativa);
        }

        return retorno;
    }

    public TCTeOS montarCteOS(Frete frete, Configuracao config, ConfiguracoesCte configCte) throws CteException {
        validarEnderecoContratante(frete.getContratante());
        validarVeiculo(frete);

        Date agora = new Date();

        TCTeOS.InfCte.Ide ide = montarIde(frete, config, agora);

        String chave = calcularChaveAcesso(ide, config, agora);
        ide.setCDV(chave.substring(43));
        frete.setChaveCte(chave);

        TCTeOS.InfCte infCte = new TCTeOS.InfCte();
        infCte.setVersao(VERSAO);
        infCte.setId("CTe" + chave);
        infCte.setIde(ide);
        infCte.setEmit(montarEmit(config));
        infCte.setToma(montarToma(frete.getContratante(), config));
        infCte.setVPrest(montarVPrest(frete));
        infCte.setImp(montarImp(config, frete));
        infCte.setInfCTeNorm(montarInfCTeNorm(frete, config));

        TCTeOS.InfCTeSupl infCTeSupl = new TCTeOS.InfCTeSupl();
        infCTeSupl.setQrCodCTe(ObjetoCTeUtil.criaQRCode(chave, configCte));

        TCTeOS cteOS = new TCTeOS();
        cteOS.setVersao(VERSAO);
        cteOS.setInfCte(infCte);
        cteOS.setInfCTeSupl(infCTeSupl);
        return cteOS;
    }

    /**
     * Confere se o endereco do contratante tem os campos exigidos pelo schema do CT-e OS
     * (cte:enderToma: xLgr, nro, xBairro, xMun, UF), evitando uma rejeicao 215 da SEFAZ
     * com mensagem tecnica de schema XML.
     */
    private void validarEnderecoContratante(Contratante contratante) {
        if (contratante == null) {
            throw new IllegalStateException("Viagem sem contratante cadastrado.");
        }

        List<String> faltando = new ArrayList<>();
        if (vazio(contratante.getLogradouro())) {
            faltando.add("Logradouro");
        }
        if (vazio(contratante.getNumeroEndereco())) {
            faltando.add("Numero");
        }
        if (vazio(contratante.getBairro())) {
            faltando.add("Bairro");
        }
        if (vazio(contratante.getMunicipio())) {
            faltando.add("Municipio");
        }
        if (vazio(contratante.getUf())) {
            faltando.add("UF");
        }
        if (vazio(contratante.getCep())) {
            faltando.add("CEP");
        }

        if (!faltando.isEmpty()) {
            throw new IllegalStateException("Endereco do contratante incompleto. Preencha na aba Contratante: "
                    + String.join(", ", faltando) + ".");
        }
    }

    private boolean vazio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }

    /**
     * Confere se a viagem tem um veiculo com placa cadastrada, exigida pelo
     * elemento cte:veic do modal rodoviario (rejeicao 580 da SEFAZ se ausente).
     */
    private void validarVeiculo(Frete frete) {
        if (frete.getCarroFrete() == null || frete.getCarroFrete().isEmpty()) {
            throw new IllegalStateException("Viagem sem veiculo cadastrado. Selecione um veiculo na aba Geral.");
        }

        Carro carro = frete.getCarroFrete().get(0).getCarro();
        if (carro == null || vazio(carro.getPlaca())) {
            String nomeCarro = carro != null && !vazio(carro.getNome()) ? " \"" + carro.getNome() + "\"" : "";
            throw new IllegalStateException("Veiculo" + nomeCarro
                    + " sem placa cadastrada. Preencha o campo Placa no cadastro da Turma (veiculo).");
        }
    }

    private TCTeOS.InfCte.Ide montarIde(Frete frete, Configuracao config, Date agora) {
        TCTeOS.InfCte.Ide ide = new TCTeOS.InfCte.Ide();

        String cMunEmit = config.getCodMunicipio();
        ide.setCUF(cMunEmit.substring(0, 2));
        ide.setCFOP(config.getCfop());
        ide.setNatOp(config.getNaturezaOperacao());
        ide.setMod("67");
        ide.setSerie(String.valueOf(frete.getSerieCte()));
        ide.setNCT(String.valueOf(frete.getNumeroCte()));
        ide.setDhEmi(formatarDataHora(agora));
        ide.setTpImp("1");
        ide.setTpEmis("1");
        ide.setCCT(gerarCodigoNumerico());
        ide.setTpAmb(config.getTpAmb() == null ? "2" : config.getTpAmb());
        ide.setTpCTe("0");
        ide.setProcEmi("0");
        ide.setVerProc("EscolarServices");
        ide.setCMunEnv(cMunEmit);
        ide.setXMunEnv(config.getMunicipio());
        ide.setUFEnv(uf(config.getUf()));
        ide.setModal("01");
        ide.setTpServ("6");

        Contratante contratante = frete.getContratante();
        boolean temIE = contratante != null && contratante.getIe() != null && !contratante.getIe().trim().isEmpty();
        ide.setIndIEToma(temIE ? "1" : "9");

        ide.setCMunIni(frete.getCodMunOrigem());
        ide.setXMunIni(frete.getLocalOrigem());
        ide.setUFIni(TUf.SC);
        ide.setCMunFim(frete.getCodMunDestino());
        ide.setXMunFim(frete.getLocalDestino());
        ide.setUFFim(TUf.SC);

        return ide;
    }

    private TCTeOS.InfCte.Emit montarEmit(Configuracao config) {
        TCTeOS.InfCte.Emit emit = new TCTeOS.InfCte.Emit();
        emit.setCNPJ(somenteNumeros(config.getCnpj()));
        emit.setIE(somenteNumeros(config.getIe()));
        emit.setXNome(config.getRazaoSocial());
        emit.setXFant(config.getNomeFantasia());
        emit.setCRT(config.getCrt());

        TEndeEmi end = new TEndeEmi();
        end.setXLgr(config.getLogradouro());
        end.setNro(config.getNumeroEndereco());
        end.setXCpl(config.getComplementoEndereco());
        end.setXBairro(config.getBairro());
        end.setCMun(config.getCodMunicipio());
        end.setXMun(config.getMunicipio());
        end.setCEP(somenteNumeros(config.getCep()));
        end.setUF(ufSemEX(config.getUf()));
        end.setFone(somenteNumeros(config.getTelefone()));
        emit.setEnderEmit(end);

        return emit;
    }

    private TCTeOS.InfCte.Toma montarToma(Contratante contratante, Configuracao config) {
        TCTeOS.InfCte.Toma toma = new TCTeOS.InfCte.Toma();

        String cpfCnpj = somenteNumeros(contratante.getCPF_CNPJ());
        if (cpfCnpj != null && cpfCnpj.length() > 11) {
            toma.setCNPJ(cpfCnpj);
        } else {
            toma.setCPF(cpfCnpj);
        }

        if (contratante.getIe() != null && !contratante.getIe().trim().isEmpty()) {
            toma.setIE(somenteNumeros(contratante.getIe()));
        }

        boolean homologacao = !"1".equals(config.getTpAmb());
        toma.setXNome(homologacao ? LITERAL_HOMOLOGACAO : contratante.getNome());

        String fone = somenteNumeros(contratante.getTelefone1());
        if (fone != null && !fone.isEmpty()) {
            toma.setFone(fone);
        }

        String email = contratante.getEmail();
        if (email != null && !email.trim().isEmpty()) {
            toma.setEmail(email.trim());
        }

        TEndereco end = new TEndereco();
        end.setXLgr(contratante.getLogradouro());
        end.setNro(contratante.getNumeroEndereco());
        end.setXBairro(contratante.getBairro());
        end.setCMun(contratante.getCodMunicipio());
        end.setXMun(contratante.getMunicipio());
        end.setCEP(somenteNumeros(contratante.getCep()));
        end.setUF(uf(contratante.getUf()));
        toma.setEnderToma(end);

        return toma;
    }

    private TCTeOS.InfCte.VPrest montarVPrest(Frete frete) {
        TCTeOS.InfCte.VPrest vPrest = new TCTeOS.InfCte.VPrest();
        String valor = formatarValor(frete.getValor());
        vPrest.setVTPrest(valor);
        vPrest.setVRec(valor);
        return vPrest;
    }

    private TCTeOS.InfCte.Imp montarImp(Configuracao config, Frete frete) {
        TCTeOS.InfCte.Imp imp = new TCTeOS.InfCte.Imp();

        TImpOS icms = new TImpOS();
        if (config.getAliquotaIcms() == null || config.getAliquotaIcms() == 0d) {
            TImpOS.ICMSSN icmsSN = new TImpOS.ICMSSN();
            icmsSN.setCST("90");
            icmsSN.setIndSN("1");
            icms.setICMSSN(icmsSN);
        } else {
            double base = frete.getValor() == null ? 0d : frete.getValor();
            double aliquota = config.getAliquotaIcms();

            TImpOS.ICMS00 icms00 = new TImpOS.ICMS00();
            icms00.setCST("00");
            icms00.setVBC(formatarValor(base));
            icms00.setPICMS(formatarValor(aliquota));
            icms00.setVICMS(formatarValor(base * aliquota / 100));
            icms.setICMS00(icms00);
        }
        imp.setICMS(icms);
        imp.setVTotTrib("0.00");

        return imp;
    }

    private TCTeOS.InfCte.InfCTeNorm montarInfCTeNorm(Frete frete, Configuracao config) {
        TCTeOS.InfCte.InfCTeNorm.InfServico infServico = new TCTeOS.InfCte.InfCTeNorm.InfServico();
        String descricao = frete.getDescricao() == null || frete.getDescricao().trim().isEmpty()
                ? "TRANSPORTE DE PASSAGEIROS"
                : "TRANSPORTE - " + frete.getDescricao().toUpperCase();
        // xDescServ tem limite de 30 caracteres no schema do CT-e OS
        if (descricao.length() > 30) {
            descricao = descricao.substring(0, 30);
        }
        infServico.setXDescServ(descricao);

        TCTeOS.InfCte.InfCTeNorm.InfModal infModal = new TCTeOS.InfCte.InfCTeNorm.InfModal();
        infModal.setVersaoModal(VERSAO);
        infModal.setAny(rodoOSParaElement(montarRodoOS(frete, config)));

        TCTeOS.InfCte.InfCTeNorm infCTeNorm = new TCTeOS.InfCte.InfCTeNorm();
        infCTeNorm.setInfServico(infServico);
        infCTeNorm.setInfModal(infModal);

        return infCTeNorm;
    }

    private RodoOS montarRodoOS(Frete frete, Configuracao config) {
        RodoOS rodoOS = new RodoOS();

        // TAF e NroRegEstadual exigem string numerica de tamanho fixo (12 e 25 digitos)
        if (config.getTaf() != null && !config.getTaf().trim().isEmpty()) {
            rodoOS.setTAF(padNumerico(config.getTaf(), 12));
        } else {
            rodoOS.setNroRegEstadual(padNumerico(config.getNroRegEstadual(), 25));
        }

        if (frete.getCarroFrete() != null && !frete.getCarroFrete().isEmpty()) {
            Carro carro = frete.getCarroFrete().get(0).getCarro();
            RodoOS.Veic veic = new RodoOS.Veic();
            veic.setPlaca(carro.getPlaca());
            veic.setRENAVAM(carro.getRenavam());
            rodoOS.setVeic(veic);
        }

        RodoOS.InfFretamento infFretamento = new RodoOS.InfFretamento();
        infFretamento.setTpFretamento(frete.getTpFretamento());
        infFretamento.setDhViagem(formatarDataHora(frete.getHorarioLocalOrigem()));
        rodoOS.setInfFretamento(infFretamento);

        return rodoOS;
    }

    /**
     * Extrai apenas os digitos do valor e completa com zeros a esquerda
     * até atingir o tamanho exigido pelo schema (TAF=12, NroRegEstadual=25).
     */
    private String padNumerico(String valor, int tamanho) {
        String digitos = valor == null ? "" : valor.replaceAll("[^0-9]", "");
        if (digitos.length() > tamanho) {
            digitos = digitos.substring(digitos.length() - tamanho);
        }
        StringBuilder sb = new StringBuilder();
        for (int i = digitos.length(); i < tamanho; i++) {
            sb.append('0');
        }
        sb.append(digitos);
        return sb.toString();
    }

    private Element rodoOSParaElement(RodoOS rodoOS) {
        try {
            JAXBContext context = JAXBContext.newInstance(RodoOS.class);
            DOMResult resultado = new DOMResult();
            context.createMarshaller().marshal(rodoOS, resultado);
            Document documento = (Document) resultado.getNode();
            return documento.getDocumentElement();
        } catch (JAXBException e) {
            throw new RuntimeException("Erro ao converter RodoOS para XML", e);
        }
    }

    /**
     * Chave de acesso do CT-e: cUF(2) + AAMM(4) + CNPJ(14) + mod(2) + serie(3) + nCT(9)
     * + tpEmis(1) + cCT(8) + cDV(1) = 44 digitos.
     */
    private String calcularChaveAcesso(TCTeOS.InfCte.Ide ide, Configuracao config, Date agora) {
        StringBuilder chave43 = new StringBuilder();
        chave43.append(ide.getCUF());
        chave43.append(formatarAAMM(agora));
        chave43.append(somenteNumeros(config.getCnpj()));
        chave43.append(ide.getMod());
        chave43.append(String.format("%03d", Integer.parseInt(ide.getSerie())));
        chave43.append(String.format("%09d", Long.parseLong(ide.getNCT())));
        chave43.append(ide.getTpEmis());
        chave43.append(ide.getCCT());

        return chave43.toString() + calcularDV(chave43.toString());
    }

    private String calcularDV(String chave) {
        int soma = 0;
        int peso = 2;
        for (int i = chave.length() - 1; i >= 0; i--) {
            soma += Character.getNumericValue(chave.charAt(i)) * peso;
            peso = peso == 9 ? 2 : peso + 1;
        }
        int resto = soma % 11;
        int dv = (resto == 0 || resto == 1) ? 0 : 11 - resto;
        return String.valueOf(dv);
    }

    private TUf uf(String sigla) {
        return sigla == null || sigla.trim().isEmpty() ? null : TUf.valueOf(sigla.trim().toUpperCase());
    }

    private TUFSemEX ufSemEX(String sigla) {
        return sigla == null || sigla.trim().isEmpty() ? null : TUFSemEX.valueOf(sigla.trim().toUpperCase());
    }

    private String somenteNumeros(String valor) {
        return valor == null ? null : valor.replaceAll("[^0-9]", "");
    }

    private String formatarValor(Double valor) {
        return String.format(Locale.US, "%.2f", valor == null ? 0d : valor);
    }

    private String gerarCodigoNumerico() {
        return String.format("%08d", new Random().nextInt(100000000));
    }

    private String formatarDataHora(Date data) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        sdf.setTimeZone(TimeZone.getTimeZone("America/Sao_Paulo"));
        return sdf.format(data == null ? new Date() : data);
    }

    private String formatarAAMM(Date data) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyMM");
        sdf.setTimeZone(TimeZone.getTimeZone("America/Sao_Paulo"));
        return sdf.format(data);
    }
}
