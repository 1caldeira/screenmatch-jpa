package br.com.alura.screenmatch.model;


import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;

@Entity
@Table(name="series")
public class Series {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String title;
    private Integer seasons;
    private Double rating;
    @Enumerated(EnumType.STRING)
    private Category genre;
    private String actors;
    private String synopsis;
    private String poster;
    @OneToMany(mappedBy = "series", cascade = CascadeType.ALL, fetch=FetchType.EAGER)
    private List<Episode> episodes = new ArrayList<>();

    public Series() {
    }

    public Series(SeriesData seriesData) {
        this.title = seriesData.title();
        this.seasons = seriesData.seasons();
        this.rating = OptionalDouble.of(Double.valueOf(seriesData.rating())).orElse(0);
        this.genre = Category.fromString(seriesData.genre().split(",")[0].trim());
        this.actors = seriesData.actors();
        this.synopsis = seriesData.synopsis();
        this.poster = seriesData.poster();
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getSeasons() {
        return seasons;
    }

    public void setSeasons(Integer seasons) {
        this.seasons = seasons;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Category getGenre() {
        return genre;
    }

    public void setGenre(Category genre) {
        this.genre = genre;
    }

    public String getActors() {
        return actors;
    }

    public void setActors(String actors) {
        this.actors = actors;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public List<Episode> getEpisodes() {
        return episodes;
    }

    public void setEpisodes(List<Episode> episodes) {
        episodes.forEach(e -> e.setSeries(this));
        this.episodes = episodes;
    }

    @Override
    public String toString() {
        return "\nGenre: "+ genre +'\n'+
                "Series title: "+title + '\n' +
                "Number of seasons: " + seasons + '\n'+
                "Rating: " + rating + '\n' +
                "Actors: " + actors + '\n' +
                "Synopsis: " + synopsis + '\n' +
                "Poster URL: " + poster + '\n'+
                "Episodes: "+ episodes + '\n';
    }
}
