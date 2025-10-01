package com.techmack.consultor_api_dois.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ConsultorController {

    // Responsável pelas estatísticas
    private static Map<String, Integer> contadores = new HashMap<>();
    // Responsável pelo histórico
    private static List<String> historico = new ArrayList<>();

    static {
        contadores.put("cep", 0);
        contadores.put("fato-gato", 0);
        contadores.put("piada", 0);
    }

    public ConsultorController() {
    }

    @GetMapping("/")
    public String home() {
        return """
            <h1>Consultor APIS - Spring Boot</h1>
            <h2>Endpoints Disponíveis:</h2>
            <ul>
                <li><a href="/api/cep/01001000">Buscar CEP (ex: 01002-020)</a></li>
                <li><a href="/api/fato-gato">Fatos de Gatos</a></li>
                <li><a href="/api/piada">Piada</a></li>
                <li><a href="#">Alguma opção...</a></li>
            </ul>
            """;
    }

    // Método responsável por fazer requisições HTTP
    private String fazerRequisicao(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conexao = (HttpURLConnection) url.openConnection();
        conexao.setRequestMethod("GET");
        conexao.setRequestProperty("User-Agent", "Mozilla/5.0");

        BufferedReader leitor = new BufferedReader(new InputStreamReader(conexao.getInputStream()));
        StringBuilder resposta = new StringBuilder();
        String linha;

        while ((linha = leitor.readLine()) != null) {
            resposta.append(linha);
        }
        leitor.close();
        return resposta.toString();
    }

    // Método para consultar CEP
    @GetMapping("/cep/{cep}")
    public String consultarCep(@PathVariable("cep") String sCep) {
        contadores.put("cep", contadores.get("cep") + 1);
        historico.add("Consulta CEP: " + sCep);

        try {
            String sUrl = "https://viacep.com.br/ws/" + sCep + "/json/";
            String sJsonResposta = fazerRequisicao(sUrl);

            String sLogradouro = extrairValorJSON(sJsonResposta, "logradouro");
            String sBairro = extrairValorJSON(sJsonResposta, "bairro");
            String sLocalidade = extrairValorJSON(sJsonResposta, "localidade");
            String sUf = extrairValorJSON(sJsonResposta, "uf");

            return String.format("""
                - Consulta de CEP -
                Logradouro: %s
                Bairro: %s
                Localidade: %s
                UF: %s
                """, sLogradouro, sBairro, sLocalidade, sUf);

        } catch (Exception erro) {
            return "Aconteceu algum erro: " + erro.getMessage();
        }
    }

    // Método para consultar fato de gato
    @GetMapping("/fato-gato")
    public String consultarFatoGato() {
        contadores.put("fato-gato", contadores.get("fato-gato") + 1);
        historico.add("Consulta Fato de Gato");

        try {
            String sUrl = "https://catfact.ninja/fact";
            String sJsonResposta = fazerRequisicao(sUrl);
            String sFact = extrairValorJSON(sJsonResposta, "fact");

            return String.format("""
                - Consulta Fato de Gato -
                Fato: %s
                """, sFact);

        } catch (Exception erro) {
            return "Aconteceu algum erro: " + erro.getMessage();
        }
    }

    // Método para consultar piada
    @GetMapping("/piada")
    public String consultarPiada() {
        contadores.put("piada", contadores.get("piada") + 1);
        historico.add("Consulta Piada");

        try {
            String sUrl = "https://api.chucknorris.io/jokes/random";
            String sJsonResposta = fazerRequisicao(sUrl);
            String id = extrairValorJSON(sJsonResposta, "id");
            String value = extrairValorJSON(sJsonResposta, "value");

            return String.format("""
                - Consulta Piada -
                ID: %s
                Piada: %s
                """, id, value);

        } catch (Exception erro) {
            return "Aconteceu algum erro: " + erro.getMessage();
        }
    }
private String extrairValorJSON(String sJson, String sChave) {
    try {
        String sBusca = "\"" + sChave + "\":";
        int iInicio = sJson.indexOf(sBusca);

        if (iInicio == -1) {
            return "(campo ausente)";
        }

        iInicio += sBusca.length();

        // Verifica se o valor é nulo
        if (sJson.startsWith("null", iInicio)) {
            return "(nulo)";
        }

        // Verifica se o valor é uma string
        if (sJson.charAt(iInicio) == '\"') {
            iInicio++;
            int iFim = sJson.indexOf("\"", iInicio);
            return sJson.substring(iInicio, iFim).trim();
        }

        // Se for outro tipo (número, booleano, etc.)
        int iFim = sJson.indexOf(",", iInicio);
        if (iFim == -1) {
            iFim = sJson.indexOf("}", iInicio);
        }
        return sJson.substring(iInicio, iFim).trim();

    } catch (Exception error) {
        return "(erro ao extrair)";
    }
}
}