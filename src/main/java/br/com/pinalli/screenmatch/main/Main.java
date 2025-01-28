package br.com.pinalli.screenmatch.main;

import br.com.pinalli.screenmatch.model.DataSerie;
import br.com.pinalli.screenmatch.model.EpisodesData;
import br.com.pinalli.screenmatch.model.SeasonData;
import br.com.pinalli.screenmatch.services.APIconsume;
import br.com.pinalli.screenmatch.services.DataConvert;
import io.micrometer.common.KeyValues;

import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {
    private final Scanner reader = new Scanner(System.in);
    private final APIconsume api = new APIconsume();
    private final DataConvert convert = new DataConvert();

    private static final String BASE_URL = "https://www.omdbapi.com/?t=";
    private static final String API_KEY = "&apikey=6585022c";

    public void displayMenu() {
        while (true) {
            try {
                System.out.println("Welcome to ScreenMatch");
                System.out.print("Search for a series: ");
                var serieName = reader.nextLine();

                if (serieName.trim().isEmpty()) {
                    System.out.println("Series name cannot be empty. Please try again.\n");
                    continue;
                }

                // Busca os dados da série
                var json = api.getData(BASE_URL + serieName.replace(" ", "+") + API_KEY);
                DataSerie dataSerie = convert.getData(json, DataSerie.class);

                // Verifica se a série existe e tem dados válidos
                if (dataSerie == null || dataSerie.title() == null ||
                        "N/A".equals(dataSerie.title()) || dataSerie.totalSeasons() == null) {
                    System.out.println("Series not found. Please try again.\n");
                    continue;
                }

                System.out.println("\nSeries found!");
                System.out.printf("Title: %s\n", dataSerie);

                // Busca todas as temporadas da série
                List<SeasonData> seasons = fetchAllSeasons(api, convert, dataSerie.totalSeasons(), serieName);
                System.out.println("\nSeasons:");
                seasons.forEach(System.out::println);

                findTop5Episodes(seasons);
                break;


            } catch (Exception e) {
                System.out.println("An error occurred. Please try again.\n");
            }

        }

    }

    /**
     * Fetches data for all seasons of a specified TV series from the OMDB API.
     * This method uses Java Streams to concurrently retrieve information for each season
     * of the series, converting the JSON responses into SeasonData objects.
     *
     * @param consumeApi   The API consumer instance used to make HTTP requests
     * @param convert      The converter instance used to parse JSON responses
     * @param totalSeasons The total number of seasons to fetch (must be positive)
     * @param serieName    The name of the TV series to search for
     * @return A List of SeasonData objects containing information for each season
     * @throws IllegalArgumentException if totalSeasons is null or less than or equal to 0
     * @throws RuntimeException         if any error occurs while fetching or parsing season data
     */
    private List<SeasonData> fetchAllSeasons(APIconsume consumeApi, DataConvert convert,
                                             Integer totalSeasons, String serieName) {
        if (totalSeasons == null || totalSeasons <= 0) {
            throw new IllegalArgumentException("Invalid number of seasons");
        }

        return IntStream.rangeClosed(1, totalSeasons)
                .mapToObj(i -> {
                    try {
                        String json = consumeApi.getData(BASE_URL +
                                serieName.replace(" ", "+") + "&season=" + i + API_KEY);
                        return convert.getData(json, SeasonData.class);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }

    /**
     * Finds and prints the top 5 episodes based on rating.
     *
     * @param seasons List of seasons containing episode data
     */
    private void findTop5Episodes(List<SeasonData> seasons) {
        System.out.println("\nTop 5 Episodes:");

        seasons.stream()
                .flatMap(season -> season.episodes().stream())
                .filter(episode -> !episode.assessment().equals("N/A"))
                .sorted(Comparator.comparing(EpisodesData::assessment).reversed())
                .limit(5)
                .forEach(episode -> System.out.printf("Title: %s, Rating: %s\n",
                        episode.title(),
                        episode.assessment()));
    }
}





