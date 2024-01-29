package br.com.alura.screenmatch.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)

public record EpisodeData(@JsonAlias("Title")String title,
                          @JsonAlias("Episode")Integer number,
                          @JsonAlias("imdbRating") String rating,
                          @JsonAlias("Released") String releaseDate,
                          @JsonAlias("Director") String director,
                          @JsonAlias("Plot") String synopsis) {


}
