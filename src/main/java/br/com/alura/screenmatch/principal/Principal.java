package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.DadosEpisodio;
import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.model.DadosTemporada;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.services.ConsumoApi;
import br.com.alura.screenmatch.services.ConverteDados;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class Principal {
    private Scanner leitura = new Scanner(System.in);
    private final String ENDERECO="https://www.omdbapi.com/?t=";
    private final String API_KEY="&apikey=cc785e9";
    private  ConsumoApi consumoApi = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();
    public void exibeMenu() throws UnsupportedEncodingException {
        System.out.println("Digite o nome da série");
        var nomeSerie = leitura.nextLine();
        var nomeSerieCodificada = URLEncoder.encode(nomeSerie, StandardCharsets.UTF_8);
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
            //temporadas.forEach(t-> t.episodios().forEach(e -> System.out.println(e.titulo())));
//            System.out.println("****Top 5 episódios *******");
//            temporadas.stream()
//                    .flatMap(t->t.episodios().stream())
//                    .filter(e-> !e.avaliacao().equalsIgnoreCase("N/A"))
//                    .sorted(Comparator.comparing(DadosEpisodio::avaliacao).reversed())
//                    .limit(5)
//                    .forEach(System.out::println);
            List<Episodio> episodios = temporadas.stream()
                                    .flatMap(t->t.episodios().stream()
                                    .map(d-> new Episodio(t.numero(),d))
                                    ).collect(Collectors.toList());


            System.out.println("A partir de que ano você deseja ver espisódios?");
            var ano = leitura.nextInt();
            leitura.nextLine();
            LocalDate dataBusca = LocalDate.of(ano,1,1);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            episodios.stream()
                    .filter(e-> e.getDataLancamento() !=null && e.getDataLancamento().isAfter(dataBusca))
                    .forEach(e->{
                        System.out.println(
                                "Temporada: "+e.getTemporada()+
                                ", Episódio:"+e.getTitulo()+
                                ", Data de lançamento:"+e.getDataLancamento().format(formatter));
                    });
        }
//        List<String> nomes = Arrays.asList("Jacque", "Iasmin", "Paulo", "Rodrigo", "Nico");
//        String reduce = nomes.stream()
//                .sorted()
//                .map(String::toUpperCase)
//                .filter(n -> n.length() > 4)
//                .reduce("", (a, b) -> b + " " + a);
//        System.out.println(reduce);

    }
}
