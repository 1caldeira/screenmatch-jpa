package br.com.alura.screenmatch.main;

import br.com.alura.screenmatch.model.*;
//import br.com.alura.screenmatch.service.GPTQuery;
import br.com.alura.screenmatch.repository.SeriesRepository;
import br.com.alura.screenmatch.service.RequestAPI;

import java.util.*;
import java.util.stream.Collectors;

//import static br.com.alura.screenmatch.service.GPTQuery.getTranslation;


public class Main {

    private final Scanner sc = new Scanner(System.in);
    DataConversion conversion = new DataConversion();
    private RequestAPI requestAPI = new RequestAPI();
    private final String ADDRESS = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=ad0f5b1d";
    private SeriesRepository repository;
    private List<Series> seriesList = new ArrayList<>();
    private Optional<Series> seriesSearch;
    public Main(SeriesRepository repository) {
        this.repository = repository;
    }


    public void showMenu() {
        var option = -1;

        var menu = """
                1- Search for series
                2- Search for episodes
                3- List of series that have already been searched
                4- Search series by title
                5- Search series by actor
                6- Top 5 series
                7- Search series by genre
                8- Search series by length and rating
                9- Search episodes by name
                10- Top 5 episodes from a specific series
                11- Worst episodes from a specific series
                12- Show all episodes
                
                           
                0 - Exit
                """;

        while(option != 0) {

            System.out.println(menu);
            option = sc.nextInt();
            sc.nextLine();

            switch (option) {
                case 1:
                    searchSeriesWeb();
                    break;
                case 2:
                    searchEpisodeBySeries();
                    break;
                case 3:
                    getSeriesDataGlobalList();
                    break;
                case 4:
                    searchSeriesByTitle();
                    break;
                case 5:
                    searchSeriesByActor();
                    break;
                case 6:
                    searchTop5Series();
                    break;
                case 7:
                    searchSeriesByCategory();
                    break;
                case 8:
                    filterByLenghtAndRating();
                    break;
                case 9:
                    searchEpisodeByName();
                    break;
                case 10:
                    topEpisodesBySeries();
                    break;
                case 11:
                    worstEpisodesBySeries();
                    break;
                case 12:
                    getAllEpisodes();
                case 0:
                    System.out.println("Exiting...");
                    break;
                default:
                    System.out.println("Invalid option");
            }
        }
    }

    private void getAllEpisodes() {
        getSeriesDataGlobalList();
        System.out.println("Choose series by name: ");
        var searchName = sc.nextLine();
        Optional<Series> series = repository.findByTitleContainingIgnoreCase(searchName);
        if(series.isPresent()){
            List<Episode> episodes = series.get().getEpisodes();
            episodes.forEach(System.out::println);
        }else{
            System.out.println("Series not found!");
        }
    }

    private void searchSeriesWeb() {
        SeriesData data = getSeriesData();
        Series series = new Series(data);
        repository.save(series);
        System.out.println(data);
    }

    private SeriesData getSeriesData() {
        System.out.println("Type in the name of the series you want to search: ");
        var seriesName = sc.nextLine();
        var json = requestAPI.getData(ADDRESS + seriesName.replace(" ", "+") + API_KEY+"&type=series");
        SeriesData data = conversion.getData(json, SeriesData.class);
        return data;
    }

    private void searchEpisodeBySeries(){
        getSeriesDataGlobalList();
        System.out.println("Choose series by name: ");
        var searchName = sc.nextLine();
        Optional<Series> series = repository.findByTitleContainingIgnoreCase(searchName);

        if(series.isPresent()) {
            Series seriesFound = series.get();
            List<SeasonData> seasons = new ArrayList<>();

            for (int i = 1; i <= seriesFound.getSeasons(); i++) {
                var json = requestAPI.getData(ADDRESS + seriesFound.getTitle().replace(" ", "+") + "&season=" + i + API_KEY + "&type=series");
                SeasonData seasonData = conversion.getData(json, SeasonData.class);
                seasons.add(seasonData);
            }
            seasons.forEach(System.out::println);

            List<Episode> episodes = seasons.stream()
                    .flatMap(seasonData -> seasonData.episodes().stream()
                            .map(episodeData -> new Episode(seasonData.number(), episodeData)))
                    .collect(Collectors.toList());

            List<Integer> numberOfEpisodesPerSeason = seasons.stream()
                    .map(seasonData -> (seasonData.episodes().size()))
                    .collect(Collectors.toList());

            int listElement = 0;
            for (int seasonNumber = 1; seasonNumber <= seriesFound.getSeasons(); seasonNumber++) {
                for (int episodeNumber = 1; episodeNumber <= numberOfEpisodesPerSeason.get(seasonNumber-1); episodeNumber++) {
                    var json = requestAPI.getData(ADDRESS + seriesFound.getTitle().replace(" ", "+") + "&season=" + seasonNumber + API_KEY + "&type=series" + "&episode="+episodeNumber);
                    EpisodeData episodeData = conversion.getData(json, EpisodeData.class);
                    episodes.get(listElement).setSynopsis(episodeData.synopsis());
                    episodes.get(listElement).setDirector(episodeData.director());
                    System.out.println("temporada: "+seasonNumber+" episodio: "+episodeNumber+" elemento da lista: "+listElement);
                    listElement++;
                }
                System.out.println(listElement);
            }
            seriesFound.setEpisodes(episodes);
            repository.save(seriesFound);
        }else{
                System.out.println("Series not found!");
            }
}
    private void getSeriesDataGlobalList(){
        seriesList = repository.findAll();
        seriesList.stream()
                .sorted(Comparator.comparing(Series::getGenre))
                .forEach(System.out::println);
    }
    private void searchSeriesByTitle() {
        System.out.println("Choose series by name: ");
        var searchName = sc.nextLine();
        seriesSearch = repository.findByTitleContainingIgnoreCase(searchName);
        if(seriesSearch.isPresent()){
            System.out.println("Series data: "+seriesSearch.get());
        }else{
            System.out.println("Series not found!");
        }
    }
    private void searchSeriesByActor() {
        System.out.println("Which actor do you wish to search? ");
        var actorName = sc.nextLine();
        List<Series> seriesFound = repository.findByActorsContainingIgnoreCase(actorName);
        System.out.println("Series in which "+actorName+" worked on: ");
        seriesFound.forEach(System.out::println);
    }
    private void searchTop5Series() {
        List<Series> top5 = repository.findTop5ByOrderByRatingDesc();
        top5.forEach(System.out::println);
    }

    private void searchSeriesByCategory(){
        System.out.println("Which genre do you wish to filter by? ");
        var genre = sc.nextLine();
        Category category = Category.fromString(genre);
        List<Series> seriesByGenre = repository.findByGenre(category);
        seriesByGenre.forEach(System.out::println);
    }

    private void filterByLenghtAndRating(){
        System.out.println("How many seasons do you wish the series to have at maximum? ");
        int numberOfSeasons = sc.nextInt();
        System.out.println("What is the minimum rating?");
        double minimumRating = sc.nextDouble();
        List<Series> query = repository.seriesBySeasonAndRating(numberOfSeasons, minimumRating);
        query.forEach(System.out::println);

    }

    private void searchEpisodeByName() {
        System.out.println("What's the episode name?");
        var episodeName = sc.nextLine();
        List<Episode> foundEpisodes = repository.episodesByName(episodeName);
        foundEpisodes.forEach(e -> System.out.println("'"+e.getTitle() +"'   -  "+e.getSeries().getTitle()+"  -  Season: "+e.getSeason()));
    }

    private void topEpisodesBySeries() {
        searchSeriesByTitle();
        if(seriesSearch.isPresent()){
            Series series = seriesSearch.get();
            List<Episode> topEpisodes = repository.top5Episodes(series);
            topEpisodes.forEach(System.out::println);
        }

    }

    private void worstEpisodesBySeries() {
        searchSeriesByTitle();
        if(seriesSearch.isPresent()){
            Series series = seriesSearch.get();
            List<Episode> worstEpisodes = repository.worst5Episodes(series);
            worstEpisodes.forEach(System.out::println);
        }
    }

}

