package org.escolar.util;

import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Resolve o codigo IBGE (7 digitos) de um municipio a partir do nome e da UF,
 * usado para preencher cMunIni/cMunFim/cMunEnv no CT-e OS.
 * Consulta a API publica do IBGE uma vez por UF e mantem em cache em memoria.
 */
public class MunicipioIBGEUtil {

    private static final Map<String, Map<String, String>> CACHE_POR_UF = new ConcurrentHashMap<>();

    private MunicipioIBGEUtil() {
    }

    /** Mapa nome do municipio (com acentos, para exibicao) -> codigo IBGE (7 digitos), ordenado por nome. */
    public static Map<String, String> listarMunicipios(String uf) {
        return CACHE_POR_UF.computeIfAbsent(uf.toUpperCase(), MunicipioIBGEUtil::carregarMunicipios);
    }

    public static String buscarCodigoMunicipio(String nomeMunicipio, String uf) {
        if (nomeMunicipio == null || uf == null) {
            return null;
        }
        String alvo = normalizar(nomeMunicipio);
        for (Map.Entry<String, String> entry : listarMunicipios(uf).entrySet()) {
            if (normalizar(entry.getKey()).equals(alvo)) {
                return entry.getValue();
            }
        }
        return null;
    }

    private static Map<String, String> carregarMunicipios(String uf) {
        Map<String, String> mapa = new LinkedHashMap<>();
        try {
            URL url = new URL("https://servicodados.ibge.gov.br/api/v1/localidades/estados/" + uf + "/municipios");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.setRequestMethod("GET");

            String json = IOUtils.toString(conn.getInputStream(), StandardCharsets.UTF_8);
            JSONArray array = new JSONArray(json);

            List<JSONObject> municipios = new ArrayList<>();
            for (int i = 0; i < array.length(); i++) {
                municipios.add(array.getJSONObject(i));
            }
            municipios.sort(Comparator.comparing(m -> normalizar(m.getString("nome"))));

            for (JSONObject municipio : municipios) {
                mapa.put(municipio.getString("nome"), String.valueOf(municipio.getLong("id")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mapa;
    }

    private static String normalizar(String nome) {
        String semAcento = Normalizer.normalize(nome, Normalizer.Form.NFD)
                .replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        return semAcento.trim().toUpperCase();
    }
}
