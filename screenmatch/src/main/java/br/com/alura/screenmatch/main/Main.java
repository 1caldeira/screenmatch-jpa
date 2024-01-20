package br.com.alura.screenmatch.main;

import br.com.alura.screenmatch.model.*;
//import br.com.alura.screenmatch.service.GPTQuery;
import br.com.alura.screenmatch.service.RequestAPI;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

//import static br.com.alura.screenmatch.service.GPTQuery.getTranslation;


public class Main {

    private final Scanner sc = new Scanner(System.in);
    DataConversion conversion = new DataConversion();
    private RequestAPI requestAPI = new RequestAPI();
    private final String ADDRESS = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=ad0f5b1d";

    private List<SeriesData> seriesDataGlobalList = new ArrayList<>();


    public void showMenu() {
        var option = -1;

        var menu = """
                1- Search for series
                2- Search for episodes
                3- List of series that have already been searched
                
                           
                0 - Exit
                """;
//        "9- Translate synopsis of the series in the list to any language"
//        Translation feature not enabled because of the new OpenAI policies, which removed the free $5 credit.
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
//                case 9:
//                    System.out.println("What is your language of choice?");
//                    String language = sc.nextLine();
//                    sc.nextLine();
//                    translateSeriesSynopsis(language);
                case 0:
                    System.out.println("Exiting...");
                    break;
                default:
                    System.out.println("Invalid option");
            }
        }
    }


    private void searchSeriesWeb() {
        SeriesData data = getSeriesData();
        seriesDataGlobalList.add(data);
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
        SeriesData seriesData = getSeriesData();
        List<SeasonData> seasons = new ArrayList<>();

        for (int i = 1; i <= seriesData.seasons(); i++) {
            var json = requestAPI.getData(ADDRESS + seriesData.title().replace(" ", "+") + "&season=" + i + API_KEY+"&type=series");
            SeasonData seasonData = conversion.getData(json, SeasonData.class);
            seasons.add(seasonData);
        }
        seasons.forEach(System.out::println);
}
    private void getSeriesDataGlobalList(){
        List<Series> seriesList;
        seriesList = seriesDataGlobalList.stream()
                        .map(s -> new Series(s))
                                .collect(Collectors.toList());
        seriesList.stream()
                .sorted(Comparator.comparing(Series::getGenre))
                .forEach(System.out::println);
    }

//    private void translateSeriesSynopsis(String language){
//        seriesDataGlobalList.stream()
//                .map(s -> getTranslation(language, s.synopsis()).trim())
//                .forEach(System.out::println);
//    }
}

