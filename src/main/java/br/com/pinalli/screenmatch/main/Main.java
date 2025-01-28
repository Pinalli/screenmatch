package br.com.pinalli.screenmatch.main;

import br.com.pinalli.screenmatch.model.DataSerie;
import br.com.pinalli.screenmatch.model.SeasonData;
import br.com.pinalli.screenmatch.services.APIconsume;
import br.com.pinalli.screenmatch.services.DataConvert;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    private final Scanner reader = new Scanner(System.in);
    private final APIconsume api = new APIconsume();
    private final DataConvert convert = new DataConvert();

    private static final String BASE_URL = "https://www.omdbapi.com/?t=";
    private static final String API_KEY = "&apikey=6585022c";

    public void displayMenu() throws JsonProcessingException {
        System.out.println("Welcome to ScreenMatch");
        System.out.print("Search for a series: ");
        var serieName = reader.nextLine();

        // Busca os dados da série
        var json = api.getData(BASE_URL + serieName.replace(" ", "+") + API_KEY);
        DataSerie dataSerie = convert.getData(json, DataSerie.class);
        System.out.printf("Title: %s\n", dataSerie);

        // Busca todas as temporadas da série
        int totalSeasons = dataSerie.totalSeasons();
        try {
            List<SeasonData> seasons = fetchAllSeasons(api, convert, totalSeasons);
            System.out.println("Seasons:");
            seasons.forEach(System.out::println);
        } catch (Exception e) {
            System.err.println("Error fetching seasons: " + e.getMessage());
        }
    }

    /**
     * Método auxiliar para buscar os dados de todas as temporadas da série.
     *
     * @param consumeApi Instância de APIconsume para consumir a API.
     * @param convert    Instância de DataConvert para converter os dados JSON.
     * @param totalSeasons Número total de temporadas da série.
     * @return Lista de objetos SeasonData contendo os dados das temporadas.
     * @throws Exception Se ocorrer algum erro na busca ou conversão dos dados.
     */
    private List<SeasonData> fetchAllSeasons(APIconsume consumeApi, DataConvert convert, int totalSeasons) throws Exception {
        List<SeasonData> seasons = new ArrayList<>();
        for (int i = 1; i <= totalSeasons; i++) {
            String json = consumeApi.getData(BASE_URL + "gilmore+girls&season=" + i + API_KEY);
            SeasonData seasonData = convert.getData(json, SeasonData.class);
            seasons.add(seasonData);
        }
        return seasons;
    }

}