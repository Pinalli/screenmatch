package br.com.pinalli.screenmatch.model;

import br.com.pinalli.screenmatch.model.EpisodesData;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@Getter
@Setter
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
        try {
            this.assessment = Double.valueOf(episodesData.assessment());
        } catch (NumberFormatException e) {
            this.assessment = 0.0;
        }
        try{
            this.dateRelease = LocalDate.parse(episodesData.releaseDate());
        }catch (DateTimeParseException e){
            this.dateRelease = null;
        }

    }
    @Override
    public String toString() {
        return "Season=" + season +
                ", Title='" + title + '\'' +
                ", Episode Number=" + episodeNumber +
                ", Assessment=" + assessment +
                ", Date Release=" + dateRelease;
    }
}
