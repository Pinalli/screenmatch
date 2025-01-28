package br.com.pinalli.screenmatch.main;

import br.com.pinalli.screenmatch.model.DataSerie;
import br.com.pinalli.screenmatch.model.SeasonData;
import br.com.pinalli.screenmatch.services.APIconsume;
import br.com.pinalli.screenmatch.services.DataConvert;

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
                break;

            } catch (Exception e) {
                System.out.println("An error occurred. Please try again.\n");
            }
        }
    }

    private List<SeasonData> fetchAllSeasons(APIconsume consumeApi, DataConvert convert,
                                             Integer totalSeasons, String serieName) throws Exception {
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
}