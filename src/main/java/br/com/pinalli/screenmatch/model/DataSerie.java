package br.com.pinalli.screenmatch.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DataSerie(@JsonAlias("Title") String title, @JsonAlias("totalSeasons")Integer totalSeasons,@JsonAlias("imdbRating") String assessment) {

}
