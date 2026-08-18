package org.escolar.service;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

	private static final String INBOX_FOLDER_ID = "6130778000000008014";
	private static final String LIDOS_FOLDER_ID = "6130778000000021001";
	private static final String BOUNCE_SENDER = "mailer-daemon@mail.zoho.com";

	/** Um bounce (mensagem de "não entregue") já com o e-mail falho e o motivo extraídos
	 *  do corpo — usado pela limpeza automática de e-mail inválido. */
	public static class Bounce {
		public String messageId;
		public String enderecoFalho;
		public String motivo;
	}

	/** Lista bounces não lidos vindos do próprio Zoho (mailer-daemon@mail.zoho.com) na
	 *  caixa de entrada — só esses, não os de terceiros (ex: postmaster@outlook.com),
	 *  porque só esses são resposta direta a um e-mail que ESSA conta mandou. */
	public List<Bounce> listarBouncesNaoLidos() {
		List<Bounce> bounces = new ArrayList<>();
		try {
			JSONObject tokens = carregarTokens();
			String token = obterAccessToken(tokens);
			String accountId = obterAccountId(token);
			if (accountId == null) return bounces;

			String url = "https://mail.zoho.com/api/accounts/" + accountId
					+ "/messages/view?folderId=" + INBOX_FOLDER_ID + "&limit=100";
			HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Authorization", "Zoho-oauthtoken " + token);
			conn.setConnectTimeout(15000);
			conn.setReadTimeout(20000);
			JSONObject resp = lerResposta(conn);
			JSONArray data = resp.optJSONArray("data");
			if (data == null) return bounces;

			for (int i = 0; i < data.length(); i++) {
				JSONObject m = data.getJSONObject(i);
				if (!BOUNCE_SENDER.equalsIgnoreCase(m.optString("fromAddress"))) continue;
				if (!"0".equals(m.optString("status"))) continue; // já lido, já processado antes

				String messageId = m.getString("messageId");
				String corpo = obterCorpoCompleto(token, accountId, messageId);
				if (corpo == null) corpo = m.optString("summary");

				Bounce b = extrairBounce(messageId, corpo);
				if (b != null) bounces.add(b);
				else {
					// não deu pra extrair nada útil (formato inesperado) — arquiva mesmo
					// assim pra não ficar reprocessando pra sempre
					arquivarMensagem(messageId);
				}
			}
		} catch (Exception e) {
			LOG.warning("Zoho: falha ao listar bounces: " + e.getMessage());
		}
		return bounces;
	}

	private String obterCorpoCompleto(String token, String accountId, String messageId) {
		try {
			String url = "https://mail.zoho.com/api/accounts/" + accountId
					+ "/folders/" + INBOX_FOLDER_ID + "/messages/" + messageId + "/content";
			HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Authorization", "Zoho-oauthtoken " + token);
			conn.setConnectTimeout(15000);
			conn.setReadTimeout(20000);
			if (conn.getResponseCode() >= 300) return null;
			JSONObject resp = lerResposta(conn);
			return resp.optJSONObject("data") != null ? resp.getJSONObject("data").optString("content") : null;
		} catch (Exception e) {
			return null;
		}
	}

	// "...could not be delivered to one or more of its recipients. This is a permanent
	// error. fulano@dominio.com, ERROR CODE :550 - 5.1.1 The email account..." — formato
	// padrao dos bounces gerados pelo proprio Zoho.
	private static final Pattern PADRAO_BOUNCE = Pattern.compile(
			"permanent error\\.?\\s*<?([\\w.+-]+@[\\w.-]+\\.[A-Za-z]{2,})>?\\s*,?\\s*ERROR CODE\\s*:?\\s*(\\d{3}[^<\\r\\n]{0,200})",
			Pattern.CASE_INSENSITIVE);

	private Bounce extrairBounce(String messageId, String corpoHtml) {
		if (corpoHtml == null) return null;
		String texto = corpoHtml.replaceAll("<[^>]+>", " ").replaceAll("&nbsp;", " ")
				.replaceAll("\\s+", " ").trim();
		Matcher matcher = PADRAO_BOUNCE.matcher(texto);
		if (!matcher.find()) return null;

		Bounce b = new Bounce();
		b.messageId = messageId;
		b.enderecoFalho = matcher.group(1);
		b.motivo = matcher.group(2).trim();
		return b;
	}

	/** true só quando o motivo indica claramente que o endereço em si não existe/não é
	 *  válido — nunca pra bloqueio de remetente, caixa cheia ou erro temporário, onde
	 *  mexer no cadastro seria errado (endereço pode continuar válido). */
	public boolean indicaEnderecoInvalido(String motivo) {
		if (motivo == null) return false;
		String m = motivo.toLowerCase();
		boolean invalido = m.contains("does not exist") || m.contains("no such user")
				|| m.contains("user unknown") || m.contains("recipient rejected")
				|| m.contains("domain not found") || m.contains("nxdomain")
				|| m.contains("invalid recipient") || m.contains("invalid mailbox")
				|| m.contains("mailbox unavailable") || m.contains("recipient address rejected");
		boolean ignorar = m.contains("blocked") || m.contains("spam") || m.contains("reputation")
				|| m.contains("mailbox is full") || m.contains("over quota") || m.contains("inbox is full")
				|| m.contains("try again later") || m.contains("temporarily deferred")
				|| m.contains("greylist");
		return invalido && !ignorar;
	}

	/** Tenta corrigir erros óbvios de digitação (letra sobrando depois do domínio, @
	 *  duplicado, @ faltando antes de um provedor conhecido). Devolve null se não achar
	 *  um padrão claro o suficiente pra confiar — nesse caso o chamador deve limpar o
	 *  campo em vez de arriscar um palpite errado. */
	public String tentarCorrigirEmail(String enderecoFalho) {
		if (enderecoFalho == null) return null;
		String email = enderecoFalho.trim();

		// @ duplicado: fulano@@dominio.com -> fulano@dominio.com
		if (email.contains("@@")) {
			return email.replace("@@", "@");
		}

		// letra sobrando logo depois do TLD conhecido: ...gmail.comg -> ...gmail.com
		Matcher letraSobrando = Pattern.compile(
				"^(.+@(?:gmail|hotmail|outlook|yahoo)\\.com)[a-z]$", Pattern.CASE_INSENSITIVE).matcher(email);
		if (letraSobrando.find()) {
			return letraSobrando.group(1);
		}
		Matcher letraSobrandoBr = Pattern.compile(
				"^(.+@(?:yahoo|uol|bol)\\.com\\.br)[a-z]$", Pattern.CASE_INSENSITIVE).matcher(email);
		if (letraSobrandoBr.find()) {
			return letraSobrandoBr.group(1);
		}

		// @ faltando antes de um provedor conhecido: fulanogmail.com -> fulano@gmail.com
		Matcher arrobaFaltando = Pattern.compile(
				"^([\\w.+-]+?)(gmail|hotmail|outlook|yahoo)\\.com$", Pattern.CASE_INSENSITIVE).matcher(email);
		if (!email.contains("@") && arrobaFaltando.find()) {
			return arrobaFaltando.group(1) + "@" + arrobaFaltando.group(2) + ".com";
		}

		return null;
	}

	/** Move pra pasta LIDOS e marca como lido — "arquivar" nesse cofre de e-mail, já que
	 *  o Zoho não tem uma pasta Archive nativa configurada. */
	public void arquivarMensagem(String messageId) {
		try {
			JSONObject tokens = carregarTokens();
			String token = obterAccessToken(tokens);
			String accountId = obterAccountId(token);
			if (accountId == null) return;

			JSONArray ids = new JSONArray().put(messageId);

			JSONObject moverBody = new JSONObject();
			moverBody.put("mode", "moveMessage");
			moverBody.put("messageId", ids);
			moverBody.put("destfolderId", LIDOS_FOLDER_ID);
			executarUpdateMessage(token, accountId, moverBody);

			JSONObject lerBody = new JSONObject();
			lerBody.put("mode", "markAsRead");
			lerBody.put("messageId", ids);
			executarUpdateMessage(token, accountId, lerBody);
		} catch (Exception e) {
			LOG.warning("Zoho: falha ao arquivar mensagem " + messageId + ": " + e.getMessage());
		}
	}

	private void executarUpdateMessage(String token, String accountId, JSONObject body) throws Exception {
		HttpURLConnection conn = (HttpURLConnection) new URL(
				"https://mail.zoho.com/api/accounts/" + accountId + "/updatemessage").openConnection();
		conn.setRequestMethod("PUT");
		conn.setRequestProperty("Authorization", "Zoho-oauthtoken " + token);
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setDoOutput(true);
		conn.setConnectTimeout(15000);
		conn.setReadTimeout(20000);
		try (OutputStream os = conn.getOutputStream()) {
			os.write(body.toString().getBytes(StandardCharsets.UTF_8));
		}
		conn.getResponseCode();
	}

	private JSONObject lerResposta(HttpURLConnection conn) throws Exception {
		InputStream is = conn.getResponseCode() >= 300 ? conn.getErrorStream() : conn.getInputStream();
		String texto = new String(is.readAllBytes(), StandardCharsets.UTF_8);
		return new JSONObject(texto);
	}
}
