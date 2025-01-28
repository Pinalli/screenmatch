package br.com.pinalli.screenmatch;

import br.com.pinalli.screenmatch.model.DataSerie;
import br.com.pinalli.screenmatch.model.EpisodesData;
import br.com.pinalli.screenmatch.model.SeasonData;
import br.com.pinalli.screenmatch.services.APIconsume;
import br.com.pinalli.screenmatch.services.DataConvert;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@SpringBootApplication
public class ScreenmatchApplication implements CommandLineRunner {

    // Constantes para configurar a API base e a chave de acesso
    private static final String BASE_URL = "https://www.omdbapi.com/?t=gilmore+girls&season=";
    private static final String API_KEY = "&apikey=6585022c";

    public ScreenmatchApplication(BeanFactoryPostProcessor forceAutoProxyCreatorToUseClassProxying) {
        // Construtor da aplicação, atualmente não executa nenhuma lógica.
    }

    public static void main(String[] args) {
        SpringApplication.run(ScreenmatchApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        var consumeApi = new APIconsume();
        var convert = new DataConvert();

        // Obtém dados gerais da série
        DataSerie data = fetchSeriesData(consumeApi, convert);
        System.out.println("Dados da Série: " + data);

        // Obtém episódios da série
        EpisodesData episode = fetchEpisodeData(consumeApi, convert);
        System.out.println("Dados do Episódio: " + episode);

        // Obtém dados por temporada e exibe
        List<SeasonData> seasons = fetchAllSeasons(consumeApi, convert, data.totalSeasons());
        System.out.println("Lista de Temporadas e Episódios:");
        seasons.forEach(System.out::println);
    }

    /**
     * Método auxiliar para buscar os dados gerais da série.
     */
    private DataSerie fetchSeriesData(APIconsume consumeApi, DataConvert convert) throws Exception {
        String json = consumeApi.getData("https://www.omdbapi.com/?t=gilmore+girls&apikey=6585022c");
        return convert.getData(json, DataSerie.class);
    }

    /**
     * Método auxiliar para buscar os dados de um episódio específico.
     */
    private EpisodesData fetchEpisodeData(APIconsume consumeApi, DataConvert convert) throws Exception {
        String json = consumeApi.getData("https://www.omdbapi.com/?t=gilmore+girls&season=1&episode=2&apikey=6585022c");
        return convert.getData(json, EpisodesData.class);
    }

    /**
     * Método auxiliar para buscar os dados de todas as temporadas da série.
     */
    private List<SeasonData> fetchAllSeasons(APIconsume consumeApi, DataConvert convert, int totalSeasons) throws Exception {
        List<SeasonData> seasons = new ArrayList<>();
        for (int i = 1; i <= totalSeasons; i++) {
            String json = consumeApi.getData(BASE_URL + i + API_KEY);
            SeasonData seasonData = convert.getData(json, SeasonData.class);
            seasons.add(seasonData);
        }
        return seasons;
    }
}
