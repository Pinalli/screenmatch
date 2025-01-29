
/**
 * Author: Alberto Rocha Pinalli
 * Date: January 28, 2025
 * Description:
 * Implemented new methods to enhance code modularity and user interaction:
 * - Added method to search for episodes by title excerpt.
 * - Introduced functionality to filter and display episodes based on user-inputted year.
 * - Separated functions into distinct methods for improved readability and maintainability.
 */

package br.com.pinalli.screenmatch.main;

import br.com.pinalli.screenmatch.model.DataSerie;
import br.com.pinalli.screenmatch.model.Episode;
import br.com.pinalli.screenmatch.model.EpisodesData;
import br.com.pinalli.screenmatch.model.SeasonData;
import br.com.pinalli.screenmatch.services.APIconsume;
import br.com.pinalli.screenmatch.services.DataConvert;

import java.time.format.DateTimeFormatter;
import java.util.*;
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
                //get data from API
                var json = api.getData(BASE_URL + serieName.replace(" ", "+") + API_KEY);
                DataSerie dataSerie = convert.getData(json, DataSerie.class);

                // verify if the series was found
                if (dataSerie == null || dataSerie.title() == null ||
                        "N/A".equals(dataSerie.title()) || dataSerie.totalSeasons() == null) {
                    System.out.println("Series not found. Please try again.\n");
                    continue;
                }
                System.out.println("\nSeries found!");
                System.out.println(dataSerie);

                // search for all seasons
                List<SeasonData> seasons = fetchAllSeasons(api, convert, dataSerie.totalSeasons(), serieName);
                System.out.println("\nALL SEASONS:");
                seasons.forEach(System.out::println);

                findEpisodeByTitle(transformEpisodes(seasons));

                findTop5Episodes(seasons);

                allEpisodes(transformEpisodes(seasons));

                averageAssessmentBySeason(transformEpisodes(seasons));

                filterEpisodesByYearFromUserInput(transformEpisodes(seasons), reader);
                break;

            } catch (Exception e) {
                // System.out.println("An error occurred. Please try again.\n");
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

    public List<Episode> transformEpisodes(List<SeasonData> seasonsDate) {
        return seasonsDate.stream()
                .flatMap(t -> t.episodes().stream()
                        .map(e -> new Episode(t.number(), e)))
                .collect(Collectors.toList());
    }

    public void  averageAssessmentBySeason(List<Episode> episodes) {
        System.out.println("\nAVARAGE ASSESSMENT BY SEASON");
        Map<Integer, Double> assessmentForSeason = episodes.stream()
                .filter(e -> e.getAssessment() > 0.0)
                .collect(Collectors.groupingBy(Episode::getSeason,
                        Collectors.averagingDouble(Episode::getAssessment)));
        System.out.println(assessmentForSeason);

        DoubleSummaryStatistics stats = episodes.stream()
                .filter(e -> e.getAssessment() > 0.0)
                .collect(Collectors.summarizingDouble(Episode::getAssessment));
        System.out.println("\nAVARAGE " + stats.getAverage() + "\nWORSE EPISODE " + stats.getMin() + " \nBEST EPISODE " + stats.getMax() + "\nTOTAL " + stats.getCount());

    }

    public void allEpisodes(List<Episode> episodes) {
        System.out.println("\nALL SEASONS AND THEIR RESPECTIVE EPISODES:");
        episodes.forEach(System.out::println);
    }

    public void findEpisodeByTitle(List<Episode> episodes) {
        System.out.println("\nEnter an excerpt from the title of the episode you want to search for:");
        String titleExcerpt = reader.nextLine();

        Optional<Episode> foundEpisode = episodes.stream()
                .filter(e -> e.getTitle().toUpperCase().contains(titleExcerpt.toUpperCase()))
                .findFirst();

        if (foundEpisode.isPresent()) {
            Episode episode = foundEpisode.get();
            System.out.println("EPISODE FOUND:\n" +
                    "Season: " + episode.getSeason() +
                    ", Title: '" + episode.getTitle() + '\'' +
                    ", Episode Number: " + episode.getEpisodeNumber() +
                    ", Assessment: " + episode.getAssessment() +
                    ", Date Release: " + episode.getDateRelease());
        } else {
            System.out.println("EPISODE NOT FOUND!.");
        }
    }

    /**
     * Finds and prints the top 5 episodes based on rating.
     *
     * @param seasonsDate List of seasons containing episode data
     */
    public void findTop5Episodes(List<SeasonData> seasonsDate) {
        List<EpisodesData> episodeDataList = seasonsDate.stream()
                .flatMap(t -> t.episodes().stream())
                .toList();

        System.out.println("\nTOP FIVE EPISODES:");
        episodeDataList.stream()
                .filter(e -> !e.assessment().equalsIgnoreCase("N/A "))
                //      .peek(e -> System.out.println("FIRST FILTER (N/A) " + e))
                .sorted(Comparator.comparing(EpisodesData::assessment).reversed())
                //      .peek(e -> System.out.println("ORDERING " + e))
                .limit(5)
                //      .peek(e -> System.out.println("LIMIT " + e))
                .map(e -> e.title().toUpperCase())
                //      .peek(e -> System.out.println("MAP " + e))
                .forEach(System.out::println);
    }

    public void filterEpisodesByYearFromUserInput(List<Episode> episodes, Scanner reader) {
        System.out.println("\nWhat year do you want to watch the episodes from?");
        var year = reader.nextInt();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        episodes.stream()
                .filter(e -> e.getDateRelease() != null && e.getDateRelease().getYear() == year)
                .sorted(Comparator.comparing(Episode::getDateRelease))
                .forEach(e -> System.out.println("SEASON: " +
                        e.getSeason() + " EPISODE:" + e.getTitle() + " " + e.getEpisodeNumber() +
                        " ASSESSMENT:" + e.getAssessment() + " DATE RELEASE:" + e.getDateRelease().format(formatter)
                ));
    }
}






