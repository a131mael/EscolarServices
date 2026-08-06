package org.escolar.service;

import br.com.aaf.base.base.Constantes;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.ejb.Stateless;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Stateless
public class WhatsappMonitorService {

    private static final Logger LOG = Logger.getLogger(WhatsappMonitorService.class.getName());

    private static final String WATI_URL  = "https://live-server-8289.wati.io";
    private static final String ABIMAEL   = "5548999484089";
    private static final String ARIEL     = "5548999664943";
    private static final String ARIEL_TEL = "(48) 99966-4943";
    private static final int    HORAS_LIMITE = 5;

    // ── palavras-chave por categoria ─────────────────────────────────────────

    private static final String[] KW_TURISMO = {
        "frete", "fretamento", "viagem", "turismo", "passeio", "excurs",
        "festa", "evento", "formatura", "transfer", "ônibus para", "onibus para", "van para"
    };
    private static final String[] KW_BOLETO = {
        "boleto", "2ª via", "segunda via", "mensalidade", "pagamento",
        "pagar", "vencimento", "debito", "débito", "atrasado", "conta", "cobrança", "cobranca"
    };
    private static final String[] KW_ESCOLAR = {
        "escola", "colégio", "colegio", "criança", "crianca", "filho", "filha",
        "transporte escolar", "buscar filho", "orçamento", "orcamento",
        "busca escolar", "manhã", "tarde", "matutino", "vespertino"
    };

    // ─────────────────────────────────────────────────────────────────────────

    public void executar() {
        LOG.info("=== Monitor WhatsApp iniciado ===");
        try {
            JSONObject resp = watiGet("/api/v1/getContacts?pageSize=100&pageNumber=1");
            JSONArray contacts = resp.optJSONArray("contact_list");
            if (contacts == null) { LOG.warning("Nenhum contato retornado"); return; }

            Instant agora    = Instant.now();
            Instant limite   = agora.minus(HORAS_LIMITE, ChronoUnit.HOURS);
            Instant cutoff48 = agora.minus(48, ChronoUnit.HOURS);
            int processados  = 0;

            for (int i = 0; i < contacts.length(); i++) {
                JSONObject c = contacts.getJSONObject(i);
                String phone = c.optString("phone", "");
                String name  = c.optString("firstName", "");
                if (name.isEmpty()) name = c.optString("fullName", phone);

                // ignora nossos números
                if (phone.endsWith("999484089") || phone.endsWith("999664943")) continue;

                String lastUpdStr = c.optString("lastUpdated", "");
                if (lastUpdStr.isEmpty()) continue;
                try {
                    Instant lastUpd = Instant.parse(lastUpdStr);
                    if (lastUpd.isBefore(cutoff48)) continue;
                } catch (Exception e) { continue; }

                try {
                    JSONObject msgsResp = watiGet("/api/v1/getMessages/" + phone + "?pageSize=25&pageNumber=1");
                    JSONObject msgsPag  = msgsResp.optJSONObject("messages");
                    if (msgsPag == null) continue;
                    JSONArray items = msgsPag.optJSONArray("items");
                    if (items == null || items.length() == 0) continue;

                    // ordena por data asc e encontra última do cliente e nossa
                    List<JSONObject> msgs = toList(items);
                    msgs.sort((a, b) -> a.optString("created").compareTo(b.optString("created")));

                    JSONObject lastCli = null, lastNos = null;
                    for (JSONObject m : msgs) {
                        int type = m.optInt("type", -1);
                        if (type == 1) lastCli = m;
                        else if (type == 2) lastNos = m;
                    }

                    if (lastCli == null) continue;

                    Instant tCli = parseInstant(lastCli.optString("created"));
                    if (tCli == null || tCli.isAfter(limite)) continue; // < 5h

                    // já respondemos depois?
                    if (lastNos != null) {
                        Instant tNos = parseInstant(lastNos.optString("created"));
                        if (tNos != null && !tNos.isBefore(tCli)) continue;
                    }

                    long horasEsperando = ChronoUnit.HOURS.between(tCli, agora);
                    LOG.info("Processando: " + name + " (" + phone + ") — " + horasEsperando + "h sem resposta");
                    processarConversa(phone, name, msgs, horasEsperando);
                    processados++;

                } catch (Exception e) {
                    LOG.warning("Erro ao processar " + phone + ": " + e.getMessage());
                }
            }
            LOG.info("Monitor WhatsApp finalizado. Processados: " + processados);

        } catch (Exception e) {
            LOG.severe("Erro geral no monitor: " + e.getMessage());
        }
    }

    // ── lógica de resposta ───────────────────────────────────────────────────

    private void processarConversa(String phone, String name, List<JSONObject> msgs, long horasEsperando) {
        List<String> cliTexts = new ArrayList<>();
        for (JSONObject m : msgs) {
            if (m.optInt("type", -1) == 1) {
                String t = m.optString("text", "");
                if (!t.isEmpty()) cliTexts.add(t);
            }
        }

        String intent = classificar(cliTexts);
        LOG.info("  Intent: " + intent);

        try {
            switch (intent) {

                case "TURISMO":
                    if (jaEnviamos(msgs, "ariel")) break;
                    enviarMensagem(phone,
                        "Olá! 😊 Obrigado pelo contato com a Favo de Mel.\n\n"
                        + "Para serviços de fretamento e turismo, o responsável é o Ariel.\n"
                        + "Entre em contato com ele: *" + ARIEL_TEL + "* 🚌\n\n"
                        + "Qualquer outra dúvida estamos à disposição!");
                    break;

                case "BOLETO":
                    String cpf = extrairCpf(cliTexts);
                    if (cpf != null) {
                        enviarMensagem(phone,
                            "Obrigado! Estou verificando seu boleto no sistema, "
                            + "te retorno em instantes! 😊");
                        notificarAbimael(
                            "📋 *Monitor WhatsApp — Boleto*\n"
                            + "Cliente: " + name + "\nTelefone: " + phone + "\n"
                            + "CPF informado: *" + cpf + "*\n"
                            + "⚠️ Por favor verifique no sistema e envie o boleto.");
                    } else if (jaEnviamos(msgs, "cpf")) {
                        notificarAbimael(
                            "📋 *Monitor WhatsApp — Boleto sem CPF*\n"
                            + "Cliente: " + name + " (" + phone + ")\n"
                            + "Esperando há " + horasEsperando + "h sem informar CPF.\n"
                            + "⚠️ Por favor verifique.");
                    } else {
                        enviarMensagem(phone,
                            "Olá! 😊 Para verificarmos seu boleto, preciso do *CPF do responsável* "
                            + "pelo contrato.\nPode me informar? 📄");
                    }
                    break;

                case "ESCOLAR":
                    String[] faltando = infoFaltandoEscolar(msgs, cliTexts);
                    if (faltando.length > 0) {
                        if (jaEnviamos(msgs, "precisamos de algumas informações") ||
                            jaEnviamos(msgs, "pode me informar")) {
                            if (horasEsperando > 10) {
                                notificarAbimael(
                                    "📋 *Monitor — Orçamento Escolar parado*\n"
                                    + "Cliente " + name + " (" + phone + ") não completou as informações.\n"
                                    + "Aguardando há " + horasEsperando + "h. Falta: " + String.join(", ", faltando));
                            }
                            break;
                        }
                        String itens = "• " + String.join("\n• ", faltando);
                        enviarMensagem(phone,
                            "Olá! 😊 Para calcularmos o orçamento do transporte escolar, "
                            + "precisamos de algumas informações:\n\n"
                            + itens + "\n\n"
                            + "Pode nos informar? 🚌");
                    } else {
                        // Tem tudo — envia para Ariel
                        String[] info = extrairInfoEscolar(msgs, cliTexts);
                        String arielMsg =
                            "📋 *Orçamento Escolar — " + name + "*\n"
                            + "Escola: " + info[0] + "\n"
                            + "Endereço: " + info[1] + "\n"
                            + "Período: " + info[2] + "\n"
                            + "Crianças: " + info[3] + "\n"
                            + "Contato: " + phone + "\n\n"
                            + "Qual seria o valor? 💰";
                        enviarMensagem(ARIEL, arielMsg);
                        enviarMensagem(phone,
                            "Ótimo! Já encaminhei as informações para nossa equipe. "
                            + "Te retorno com o valor assim que possível! 😊🚌");
                    }
                    break;

                default: // DESCONHECIDO
                    if (!jaEnviamos(msgs, "estou verificando")) {
                        enviarMensagem(phone,
                            "Olá! 😊 Recebi sua mensagem e já estou verificando. "
                            + "Te retorno em breve!");
                    }
                    String ultima = cliTexts.isEmpty() ? "(sem texto)"
                        : cliTexts.get(cliTexts.size() - 1).substring(0, Math.min(120, cliTexts.get(cliTexts.size() - 1).length()));
                    notificarAbimael(
                        "📋 *Monitor WhatsApp — Verificar*\n"
                        + "Cliente: " + name + " (" + phone + ")\n"
                        + "Aguardando há: " + horasEsperando + "h\n"
                        + "Última mensagem: \"" + ultima + "\"\n"
                        + "⚠️ Não consegui classificar, por favor verifique.");
                    break;
            }
        } catch (Exception e) {
            LOG.warning("Erro ao responder " + phone + ": " + e.getMessage());
        }
    }

    // ── classificação por palavras-chave ─────────────────────────────────────

    private String classificar(List<String> textos) {
        String joined = String.join(" ", textos).toLowerCase();
        int turismo = 0, boleto = 0, escolar = 0;
        for (String kw : KW_TURISMO)  if (joined.contains(kw)) turismo++;
        for (String kw : KW_BOLETO)   if (joined.contains(kw)) boleto++;
        for (String kw : KW_ESCOLAR)  if (joined.contains(kw)) escolar++;
        if (turismo == 0 && boleto == 0 && escolar == 0) return "DESCONHECIDO";
        if (turismo >= boleto && turismo >= escolar) return "TURISMO";
        if (boleto  >= turismo && boleto  >= escolar) return "BOLETO";
        return "ESCOLAR";
    }

    private String extrairCpf(List<String> textos) {
        String joined = String.join(" ", textos);
        Matcher m = Pattern.compile("\\d{3}[.\\- ]?\\d{3}[.\\- ]?\\d{3}[\\- ]?\\d{2}").matcher(joined);
        return m.find() ? m.group(0) : null;
    }

    private boolean jaEnviamos(List<JSONObject> msgs, String trecho) {
        String lower = trecho.toLowerCase();
        for (JSONObject m : msgs) {
            if (m.optInt("type", -1) == 2) {
                String t = m.optString("text", "").toLowerCase();
                if (t.contains(lower)) return true;
            }
        }
        return false;
    }

    private String[] infoFaltandoEscolar(List<JSONObject> msgs, List<String> cliTexts) {
        String joined = String.join(" ", cliTexts).toLowerCase();
        List<String> faltando = new ArrayList<>();

        boolean temEscola   = joined.contains("escola ") || joined.contains("colégio ") || joined.contains("colegio ");
        boolean temEndereco = joined.contains("rua ") || joined.contains("av.") || joined.contains("avenida ") || joined.contains("bairro ");
        boolean temPeriodo  = joined.contains("manhã") || joined.contains("manha") || joined.contains("tarde") || joined.contains("integral");
        boolean temCriancas = Pattern.compile("\\d+\\s*(criança|crianca|filho|filha|aluno)").matcher(joined).find()
                           || joined.contains("uma criança") || joined.contains("dois filho");

        if (!temEscola)   faltando.add("nome da escola");
        if (!temEndereco) faltando.add("endereço da criança (rua e bairro)");
        if (!temPeriodo)  faltando.add("período (manhã ou tarde)");
        if (!temCriancas) faltando.add("quantas crianças");
        return faltando.toArray(new String[0]);
    }

    private String[] extrairInfoEscolar(List<JSONObject> msgs, List<String> cliTexts) {
        String joined = String.join(" ", cliTexts);
        String joinedL = joined.toLowerCase();

        String escola   = "a verificar";
        String endereco = "a verificar";
        String periodo  = "a verificar";
        String criancas = "a verificar";

        Matcher m;
        m = Pattern.compile("(?:escola|colégio|colegio)\\s+([A-Za-zÀ-ú0-9 ]{3,40})", Pattern.CASE_INSENSITIVE).matcher(joined);
        if (m.find()) escola = m.group(1).trim();

        m = Pattern.compile("(?:rua|av\\.?|avenida|alameda|r\\. )\\s*[A-Za-zÀ-ú0-9 ,\\-]{5,60}", Pattern.CASE_INSENSITIVE).matcher(joined);
        if (m.find()) endereco = m.group(0).trim();

        if (joinedL.contains("manhã") || joinedL.contains("manha") || joinedL.contains("matutino")) periodo = "manhã";
        else if (joinedL.contains("tarde") || joinedL.contains("vespertino")) periodo = "tarde";
        else if (joinedL.contains("integral")) periodo = "integral";

        m = Pattern.compile("(\\d+)\\s*(criança|crianca|filho|filha|aluno)").matcher(joinedL);
        if (m.find()) criancas = m.group(1);

        return new String[]{escola, endereco, periodo, criancas};
    }

    // ── HTTP / WATI ──────────────────────────────────────────────────────────

    private JSONObject watiGet(String path) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) new URL(WATI_URL + path).openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Authorization", Constantes.TOKEN);
        conn.setRequestProperty("Accept", "application/json");
        conn.setConnectTimeout(15000);
        conn.setReadTimeout(15000);
        int code = conn.getResponseCode();
        if (code != 200) throw new RuntimeException("WATI GET " + path + " → HTTP " + code);
        try (InputStream is = conn.getInputStream()) {
            return new JSONObject(new String(is.readAllBytes(), StandardCharsets.UTF_8));
        }
    }

    private void enviarMensagem(String phone, String texto) throws Exception {
        String url  = WATI_URL + "/api/v1/sendSessionMessage/" + phone + "?whatsappNumber=" + phone;
        String body = new JSONObject().put("messageText", texto).toString();
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", Constantes.TOKEN);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        conn.setConnectTimeout(15000);
        conn.setReadTimeout(15000);
        try (OutputStream os = conn.getOutputStream()) {
            os.write(body.getBytes(StandardCharsets.UTF_8));
        }
        conn.getResponseCode();
    }

    private void notificarAbimael(String msg) {
        try { enviarMensagem(ABIMAEL, msg); }
        catch (Exception e) { LOG.warning("Erro ao notificar Abimael: " + e.getMessage()); }
    }

    // ── utilidades ───────────────────────────────────────────────────────────

    private Instant parseInstant(String s) {
        if (s == null || s.isEmpty()) return null;
        try { return Instant.parse(s.endsWith("Z") ? s : s + "Z"); }
        catch (Exception e) { return null; }
    }

    private List<JSONObject> toList(JSONArray arr) {
        List<JSONObject> list = new ArrayList<>();
        for (int i = 0; i < arr.length(); i++) list.add(arr.getJSONObject(i));
        return list;
    }
}
