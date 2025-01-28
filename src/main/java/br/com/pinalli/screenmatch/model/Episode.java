package br.com.pinalli.screenmatch.model;


import ch.qos.logback.classic.joran.sanity.IfNestedWithinSecondPhaseElementSC;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Episode {
    private Integer season;
    private String title;
    private Integer episodeNumber;
    private Double assessment;
    private LocalDate dateRelease;

    public Episode(Integer numberSeason, EpisodesData episodesData) {
        this.season = numberSeason;
        this.title = episodesData.title();
        this.episodeNumber = episodesData.number();
        this.assessment = Double.valueOf(episodesData.assessment());
        this.dateRelease = LocalDate.parse(episodesData.releaseDate());
    }

    /**
     * Finds and prints the top 5 episodes based on rating.
     *
     * @param seasons List of seasons containing episode data
     */
    public void findTop5Episodes(List<SeasonData> seasons) {
        System.out.println("\nTop 5 Episodes:");

        seasons.stream()
                .flatMap(season -> season.episodes().stream())
                .filter(episode -> !episode.assessment().equals("N/A"))
                .sorted(Comparator.comparing(EpisodesData::assessment).reversed())
                .limit(5)
                .forEach(episode -> System.out.println(episode.toString()));
    }

    @Override
    public String toString() {
        return "season=" + season +
                ", title='" + title + '\'' +
                ", episodeNumber=" + episodeNumber +
                ", assessment=" + assessment +
                ", dateRelease=" + dateRelease;
    }

}

