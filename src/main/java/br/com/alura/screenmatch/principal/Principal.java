package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.DadosEpisodio;
import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.model.DadosTemporada;
import br.com.alura.screenmatch.services.ConsumoApi;
import br.com.alura.screenmatch.services.ConverteDados;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Principal {
    private Scanner leitura = new Scanner(System.in);
    private final String ENDERECO="https://www.omdbapi.com/?t=";
    private final String API_KEY="&apikey=cc785e9";
    private  ConsumoApi consumoApi = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();
    public void exibeMenu() throws UnsupportedEncodingException {
        System.out.println("Digite o nome da série");
        var nomeSerie = leitura.nextLine();
        var nomeSerieCodificada = URLEncoder.encode(nomeSerie, StandardCharsets.UTF_8.toString());
        //"https://www.omdbapi.com/?t=gilmore+girls&season="+i+"&apikey=cc785e9"
        var json = consumoApi.obterDados(ENDERECO + nomeSerieCodificada + API_KEY);
        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
        System.out.println(dados);
        List<DadosTemporada> temporadas = new ArrayList<>();
        if (dados.totalTemporadas() != null) {
            for (int i = 1; i <= dados.totalTemporadas(); i++) {
                json = consumoApi.obterDados(ENDERECO + nomeSerieCodificada + "&season=" + i + API_KEY);
                DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
                temporadas.add(dadosTemporada);
            }
            temporadas.forEach(t-> {
                t.episodios().forEach(e -> System.out.println(e.titulo()));
            });
        }
    }
}
