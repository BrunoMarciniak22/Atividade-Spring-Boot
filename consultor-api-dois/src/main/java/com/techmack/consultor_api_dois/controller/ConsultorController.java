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
    //Responsavel pelas estatisticas
    private static Map<String, Integer> contadores = new HashMap<>();
    //Responsavel pelo Historico
    private static List<String> historico = new ArrayList<>();


    static{
        contadores.put("cep", 0);
        contadores.put("fato-gato", 0);
        contadores.put("piada", 0);
    }

    public ConsultorController() {
    }


    @GetMapping("/")
    public String home() {
        return """
                <h1> consultor APIS - Spring Boot </h1>
                <h2>Endpoints Disponiveis: </h2>
                <ul>
                    <li><a href=""></a> - Buscar CEP</li>
                    <li><a href=""></a> - Fatos de Gatos</li>
                    <li><a href=""></a> - Piada</li>
                    <li><a href=""></a> - Alguma opcao...</li>
                </ul>
                """;       
    }







    //Método responsavel reutilizado do ConsultorApi original
    private String fazerRequisicao(String urlString)throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conexao = (HttpURLConnection)url.openConnection();
        conexao.setRequestMethod("GET");
        conexao.setRequestProperty("User-Agent", "Mozilla/5.0");
        BufferedReader leitor = new BufferedReader(new InputStreamReader(conexao.getInputStream()));
        StringBuilder resposta = new StringBuilder();
        String linha;


        while((linha = leitor.readLine()) != null){
            resposta.append(linha);
        }
        leitor.close();
        return resposta.toString();
    }




    
    @GetMapping("/cep/{cep}")
    public String consultarCep(@PathVariable String sCep){
        try {



            String sUrl = "https://viacep.com.r/ws/" + sCep + "/json/";
            String sJasonResposta = fazerRequisicao(sUrl);
            return "";
        } 

        catch (Exception erro) {
            return "Aconteceu algum erro" + erro.getMessage();
        }
    }


    private String extrairValorJSON(String sJson, String sChave){
        try {
            String sBusca = "\"" + sChave + "\":\"";
            int iInicio = sJson.indexOf(sBusca);

            if(iInicio == -1){
                 sBusca = "\"" + sChave + "\":\"";
                iInicio = sJson.indexOf(sBusca);
            if (iInicio == -1){
                return "Não existe esse campo!";
                
            }
            iInicio += sBusca.length();
            int iFim = sJson.indexOf(",",iInicio);
            
            if(iFim == -1){
                iFim = sJson.indexOf("}", iInicio);

            }
                return sJson.substring(iInicio, iFim).trim();
        }
        
        iInicio = sBusca.length();
        int iFim = sJson.indexOf("\"", iInicio);
        return sJson.substring(iInicio, iFim).trim();

        }catch (Exception error){
            return "Não encontrado";
        }
   }
}


    
