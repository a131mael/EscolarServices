package org.escolar.service;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

/** Envia e-mail (com anexo) pela conta financeiro@tefamel.com via API REST do Zoho Mail.
 *  Reusa o mesmo token OAuth do canal Zoho do app secretaria-virtual — os dois rodam no
 *  mesmo host/usuário, então dá pra ler/atualizar o arquivo direto em vez de duplicar
 *  client_id/refresh_token aqui (fonte única evita os dois refrescarem o token juntos). */
public class ZohoEmailService {

	private static final Logger LOG = Logger.getLogger(ZohoEmailService.class.getName());
	private static final String TOKEN_PATH = "/home/servidor/secretaria-virtual/secrets/zoho_token.json";
	private static final String FROM_ADDRESS = "financeiro@tefamel.com";

	public boolean enviarEmailComAnexo(String destinatario, String assunto, String corpoHtml,
			String nomeArquivo, byte[] anexo) {
		try {
			JSONObject tokens = carregarTokens();
			String token = obterAccessToken(tokens);
			String accountId = obterAccountId(token);
			if (accountId == null) {
				LOG.warning("Zoho: não achou accountId da conta " + FROM_ADDRESS);
				return false;
			}

			JSONObject dadosAnexo = uploadAnexo(token, accountId, nomeArquivo, anexo);
			if (dadosAnexo == null) return false;

			return enviarMensagem(token, accountId, destinatario, assunto, corpoHtml, dadosAnexo);
		} catch (Exception e) {
			LOG.warning("Zoho: exceção ao enviar pra " + destinatario + ": " + e.getMessage());
			return false;
		}
	}

	private JSONObject carregarTokens() throws Exception {
		String conteudo = new String(Files.readAllBytes(Paths.get(TOKEN_PATH)), StandardCharsets.UTF_8);
		return new JSONObject(conteudo);
	}

	private void salvarTokens(JSONObject tokens) throws Exception {
		Files.write(Paths.get(TOKEN_PATH), tokens.toString(2).getBytes(StandardCharsets.UTF_8));
	}

	private String obterAccessToken(JSONObject tokens) throws Exception {
		String accessToken = tokens.getString("access_token");
		HttpURLConnection teste = (HttpURLConnection) new URL("https://mail.zoho.com/api/accounts").openConnection();
		teste.setRequestMethod("GET");
		teste.setRequestProperty("Authorization", "Zoho-oauthtoken " + accessToken);
		teste.setConnectTimeout(15000);
		teste.setReadTimeout(15000);
		if (teste.getResponseCode() == 200) {
			return accessToken;
		}

		HttpURLConnection refresh = (HttpURLConnection) new URL("https://accounts.zoho.com/oauth/v2/token").openConnection();
		refresh.setRequestMethod("POST");
		refresh.setDoOutput(true);
		refresh.setConnectTimeout(15000);
		refresh.setReadTimeout(15000);
		String corpo = "grant_type=refresh_token"
				+ "&client_id=" + URLEncoder.encode(tokens.getString("client_id"), "UTF-8")
				+ "&client_secret=" + URLEncoder.encode(tokens.getString("client_secret"), "UTF-8")
				+ "&refresh_token=" + URLEncoder.encode(tokens.getString("refresh_token"), "UTF-8");
		try (OutputStream os = refresh.getOutputStream()) {
			os.write(corpo.getBytes(StandardCharsets.UTF_8));
		}
		JSONObject novosTokens = lerResposta(refresh);
		String novoAccessToken = novosTokens.getString("access_token");
		tokens.put("access_token", novoAccessToken);
		salvarTokens(tokens);
		return novoAccessToken;
	}

	private String obterAccountId(String token) throws Exception {
		HttpURLConnection conn = (HttpURLConnection) new URL("https://mail.zoho.com/api/accounts").openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Authorization", "Zoho-oauthtoken " + token);
		conn.setConnectTimeout(15000);
		conn.setReadTimeout(15000);
		JSONObject resp = lerResposta(conn);
		JSONArray contas = resp.optJSONArray("data");
		if (contas == null || contas.length() == 0) return null;
		return String.valueOf(contas.getJSONObject(0).get("accountId"));
	}

	private JSONObject uploadAnexo(String token, String accountId, String nomeArquivo, byte[] conteudo) throws Exception {
		String url = "https://mail.zoho.com/api/accounts/" + accountId + "/messages/attachments?fileName="
				+ URLEncoder.encode(nomeArquivo, "UTF-8");
		HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Authorization", "Zoho-oauthtoken " + token);
		conn.setRequestProperty("Content-Type", "application/octet-stream");
		conn.setDoOutput(true);
		conn.setConnectTimeout(15000);
		conn.setReadTimeout(30000);
		try (OutputStream os = conn.getOutputStream()) {
			os.write(conteudo);
		}
		if (conn.getResponseCode() >= 300) {
			LOG.warning("Zoho: falha ao anexar " + nomeArquivo + ": HTTP " + conn.getResponseCode());
			return null;
		}
		JSONObject resp = lerResposta(conn);
		return resp.getJSONObject("data");
	}

	private boolean enviarMensagem(String token, String accountId, String destinatario, String assunto,
			String corpoHtml, JSONObject dadosAnexo) throws Exception {
		JSONObject anexo = new JSONObject();
		anexo.put("storeName", dadosAnexo.getString("storeName"));
		anexo.put("attachmentPath", dadosAnexo.getString("attachmentPath"));
		anexo.put("attachmentName", dadosAnexo.getString("attachmentName"));

		JSONObject corpo = new JSONObject();
		corpo.put("fromAddress", FROM_ADDRESS);
		corpo.put("toAddress", destinatario);
		corpo.put("subject", assunto);
		corpo.put("content", corpoHtml);
		corpo.put("mailFormat", "html");
		corpo.put("attachments", new JSONArray().put(anexo));

		HttpURLConnection conn = (HttpURLConnection) new URL(
				"https://mail.zoho.com/api/accounts/" + accountId + "/messages").openConnection();
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Authorization", "Zoho-oauthtoken " + token);
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setDoOutput(true);
		conn.setConnectTimeout(15000);
		conn.setReadTimeout(30000);
		try (OutputStream os = conn.getOutputStream()) {
			os.write(corpo.toString().getBytes(StandardCharsets.UTF_8));
		}
		int codigo = conn.getResponseCode();
		if (codigo >= 300) {
			LOG.warning("Zoho: falha ao enviar pra " + destinatario + ": HTTP " + codigo);
			return false;
		}
		return true;
	}

	private JSONObject lerResposta(HttpURLConnection conn) throws Exception {
		InputStream is = conn.getResponseCode() >= 300 ? conn.getErrorStream() : conn.getInputStream();
		String texto = new String(is.readAllBytes(), StandardCharsets.UTF_8);
		return new JSONObject(texto);
	}
}
