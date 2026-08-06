package org.escolar.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;

import javax.ejb.Stateless;

import org.escolar.model.Configuracao;
import org.escolar.model.Frete;
import org.escolar.model.PassageiroViagem;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Integra com o scmobi-automation-service (microsservico Python/Selenium) para
 * gerar a Licenca de Fretamento Eventual no scMOBI a partir de uma Viagem (Frete),
 * e baixar os PDFs resultantes (Licenca + Lista de Passageiros).
 */
@Stateless
public class ScmobiService {

    private static final String BASE_URL = baseUrl();

    private static String baseUrl() {
        String env = System.getenv("SCMOBI_SERVICE_URL");
        return env == null || env.trim().isEmpty() ? "http://scmobi-automation-service:8000" : env;
    }

    /** Monta o JSON com os dados da viagem e do contratante e solicita a geracao da licenca. Retorna o jobId. */
    public String solicitarLicenca(Frete frete, Configuracao config) throws IOException {
        SimpleDateFormat formatoHora = new SimpleDateFormat("HH");

        JSONObject corpo = new JSONObject();
        corpo.put("chaveCte", frete.getChaveCte());
        corpo.put("quilometragem", frete.getQuilometragem());
        corpo.put("valorTotal", frete.getValor());
        corpo.put("origem", frete.getLocalOrigem());
        corpo.put("destino", frete.getLocalDestino());
        corpo.put("horaIda", formatoHora.format(frete.getHorarioLocalOrigem()));
        if (frete.getHorarioParaRetorno() != null) {
            corpo.put("horaVolta", formatoHora.format(frete.getHorarioParaRetorno()));
        }
        corpo.put("veiculoPlaca", frete.getCarroFrete().get(0).getCarro().getPlaca());
        corpo.put("motoristaCnh", frete.getMotorista().getCnhNumero());
        corpo.put("motoristaCpf", frete.getMotorista().getCpf());
        corpo.put("motoristaNome", frete.getMotorista().getNome());
        corpo.put("contratanteDocumento", frete.getContratante().getCPF_CNPJ());

        JSONArray passageiros = new JSONArray();
        for (PassageiroViagem passageiro : frete.getPassageiros()) {
            JSONObject p = new JSONObject();
            p.put("nome", passageiro.getNome());
            p.put("cpf", passageiro.getCpf());
            passageiros.put(p);
        }
        corpo.put("passageiros", passageiros);

        String resposta = post("/licencas", corpo.toString());
        return new JSONObject(resposta).getString("jobId");
    }

    /** Consulta o status atual do job: {"status", "numeroContrato", "erro"}. */
    public JSONObject consultarStatus(String jobId) throws IOException {
        return new JSONObject(get("/licencas/" + jobId));
    }

    public byte[] baixarLicencaPdf(String jobId) throws IOException {
        return getBytes("/licencas/" + jobId + "/licenca.pdf");
    }

    public byte[] baixarPassageirosPdf(String jobId) throws IOException {
        return getBytes("/licencas/" + jobId + "/passageiros.pdf");
    }

    private String post(String caminho, String corpoJson) throws IOException {
        HttpURLConnection conn = abrirConexao(caminho, "POST");
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        conn.setDoOutput(true);
        try (OutputStream out = conn.getOutputStream()) {
            out.write(corpoJson.getBytes(StandardCharsets.UTF_8));
        }
        return lerResposta(conn);
    }

    private String get(String caminho) throws IOException {
        return lerResposta(abrirConexao(caminho, "GET"));
    }

    private byte[] getBytes(String caminho) throws IOException {
        HttpURLConnection conn = abrirConexao(caminho, "GET");
        int status = conn.getResponseCode();
        if (status >= 400) {
            throw new IOException("scmobi-automation-service " + caminho + " retornou HTTP " + status
                    + ": " + lerStream(conn.getErrorStream()));
        }
        try (InputStream in = conn.getInputStream()) {
            return lerBytes(in);
        }
    }

    private HttpURLConnection abrirConexao(String caminho, String metodo) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(BASE_URL + caminho).openConnection();
        conn.setRequestMethod(metodo);
        conn.setConnectTimeout(10_000);
        conn.setReadTimeout(120_000);
        return conn;
    }

    private String lerResposta(HttpURLConnection conn) throws IOException {
        int status = conn.getResponseCode();
        String corpo = lerStream(status >= 400 ? conn.getErrorStream() : conn.getInputStream());
        if (status >= 400) {
            throw new IOException("scmobi-automation-service retornou HTTP " + status + ": " + corpo);
        }
        return corpo;
    }

    private String lerStream(InputStream in) throws IOException {
        if (in == null) {
            return "";
        }
        try (InputStream stream = in) {
            return new String(lerBytes(stream), StandardCharsets.UTF_8);
        }
    }

    private byte[] lerBytes(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int lidos;
        while ((lidos = in.read(buffer)) != -1) {
            out.write(buffer, 0, lidos);
        }
        return out.toByteArray();
    }
}
