package br.com.alura.screenmatch.repository;

import br.com.alura.screenmatch.model.Category;
import br.com.alura.screenmatch.model.Episode;
import br.com.alura.screenmatch.model.Series;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;
import java.util.Optional;


public interface SeriesRepository extends JpaRepository<Series, Long> {
    Optional<Series> findByTitleContainingIgnoreCase(String search);
    List<Series> findByActorsContainingIgnoreCase(String actorName);
    List<Series> findTop5ByOrderByRatingDesc();
    List<Series> findByGenre(Category category);
    List<Series> findBySeasonsIsLessThanEqualAndRatingGreaterThanEqual(Integer seasonsNumber, Double desiredRating);
    @Query("select s from Series s where s.seasons <= :seasonsNumber and s.rating >= :desiredRating")
    List<Series> seriesBySeasonAndRating(Integer seasonsNumber, Double desiredRating);

    @Query("SELECT e FROM Series s JOIN s.episodes e WHERE e.title ILIKE %:episodeName%")
    List<Episode> episodesByName(String episodeName);

    @Query("SELECT e FROM Series s JOIN s.episodes e WHERE s = :series ORDER BY e.rating DESC LIMIT 5")
    List<Episode> top5Episodes(Series series);
    @Query("SELECT e FROM Series s JOIN s.episodes e WHERE s = :series AND e.rating != 0 ORDER BY e.rating ASC LIMIT 5")
    List<Episode> worst5Episodes(Series series);
    @Query("SELECT e FROM Series s JOIN s.episodes e WHERE s = :series AND YEAR (e.releaseDate) >= :yearReleased")
    List<Episode> episodesBySeriesAndYear(Series series, int yearReleased);
}
