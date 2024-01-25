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
                case 4:
                    searchSeriesByTitle();
                case 0:
                    System.out.println("Exiting...");
                    break;
                default:
                    System.out.println("Invalid option");
            }
        }
    }

    private void searchSeriesByTitle() {
        System.out.println("Choose series by name: ");
        var searchName = sc.nextLine();
        Optional<Series> seriesSearch = repository.findByTitleContainingIgnoreCase(searchName);
        if(seriesSearch.isPresent()){
            System.out.println("Series data: "+seriesSearch.get());
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


}

