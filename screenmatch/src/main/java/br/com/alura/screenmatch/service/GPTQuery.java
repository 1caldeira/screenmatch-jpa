//package br.com.alura.screenmatch.service;
//
//
//import com.theokanning.openai.completion.CompletionRequest;
//import com.theokanning.openai.service.OpenAiService;
//
//public class GPTQuery {
//    public static String getTranslation(String text, String language) {
//        OpenAiService service = new OpenAiService("insert api key");
//
//        CompletionRequest request = CompletionRequest.builder()
//                .model("gpt-3.5-turbo-instruct")
//                .prompt("translate this text to " + language + ": " + text)
//                .maxTokens(1000)
//                .temperature(0.7)
//                .stream(true)
//                .build();
//
//        var answer = service.createCompletion(request);
//        return answer.getChoices().get(0).getText();
//    }
//}
