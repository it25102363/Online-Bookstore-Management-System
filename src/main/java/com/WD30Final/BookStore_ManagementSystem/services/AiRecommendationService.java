package com.WD30Final.BookStore_ManagementSystem.services;

import com.WD30Final.BookStore_ManagementSystem.entity.Book;
import com.WD30Final.BookStore_ManagementSystem.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AiRecommendationService {

    private final BookRepository bookRepository;

    @Value("${gemini.api.key:}")
    private String apiKey;

    public List<Book> getRecommendations(String userPrompt) {
        List<Book> allBooks = bookRepository.findAll();
        if (allBooks.isEmpty()) {
            return Collections.emptyList();
        }

        if (apiKey == null || apiKey.trim().isEmpty() || apiKey.contains("YOUR_GEMINI_API_KEY")) {
            System.err.println(">>> GEMINI KEY NOT FOUND. Using strict keyword search.");
            return keywordSearch(allBooks, userPrompt);
        }

        // Format compact catalog for prompt
        StringBuilder catalog = new StringBuilder();
        for (Book b : allBooks) {
            catalog.append(String.format("ID: %d | Title: \"%s\" | Category: \"%s\" | Description: \"%s\"\n",
                    b.getId(),
                    b.getTitle(),
                    b.getCategory() != null ? b.getCategory() : "General",
                    b.getDescription() != null ? b.getDescription() : ""));
        }

        String prompt = String.format("""
            You are a bookstore recommender.
            Catalog:
            %s

            User wants: "%s"

            Task: Identify the matching book IDs from the catalog.
            If NONE match, return: NONE
            If matches exist, return ONLY comma-separated IDs (e.g. 1, 3).
            Do not output any words or markdown.
            """, catalog, userPrompt);

        // Models to try in sequence if one experiences a 503 spike
        String[] candidateModels = new String[]{
                "gemini-3.8-flash",
                "gemini-3.5-flash",
                "gemini-2.5-pro",
                "gemini-2.5-flash"
        };

        RestClient restClient = RestClient.create();
        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(
                                Map.of("text", prompt)
                        ))
                )
        );

        for (String modelName : candidateModels) {
            String url = "https://generativelanguage.googleapis.com/v1beta/models/" + modelName + ":generateContent?key=" + apiKey.trim();

            for (int attempt = 1; attempt <= 2; attempt++) {
                try {
                    Map<?, ?> res = restClient.post()
                            .uri(url)
                            .header("Content-Type", "application/json")
                            .body(requestBody)
                            .retrieve()
                            .body(Map.class);

                    if (res != null && res.containsKey("candidates")) {
                        List<?> candidates = (List<?>) res.get("candidates");
                        if (!candidates.isEmpty()) {
                            Map<?, ?> first = (Map<?, ?>) candidates.get(0);
                            Map<?, ?> content = (Map<?, ?>) first.get("content");
                            List<?> parts = (List<?>) content.get("parts");
                            Map<?, ?> part0 = (Map<?, ?>) parts.get(0);
                            String rawText = ((String) part0.get("text")).trim();

                            System.out.println(">>> GEMINI SUCCESS (" + modelName + "): " + rawText);

                            if (rawText.equalsIgnoreCase("NONE") || rawText.isEmpty()) {
                                return Collections.emptyList();
                            }

                            List<Long> matchedIds = new ArrayList<>();
                            Matcher matcher = Pattern.compile("\\d+").matcher(rawText);
                            while (matcher.find()) {
                                matchedIds.add(Long.parseLong(matcher.group()));
                            }

                            if (!matchedIds.isEmpty()) {
                                List<Book> foundBooks = bookRepository.findAllById(matchedIds);
                                Map<Long, Book> map = foundBooks.stream().collect(Collectors.toMap(Book::getId, b -> b));

                                List<Book> result = new ArrayList<>();
                                for (Long id : matchedIds) {
                                    if (map.containsKey(id)) {
                                        result.add(map.get(id));
                                    }
                                }
                                return result;
                            }
                        }
                    }
                    break; // If call returned without error, exit retry loop
                } catch (Exception ex) {
                    System.err.println(">>> Model " + modelName + " (attempt " + attempt + ") failed: " + ex.getMessage());
                    if (attempt == 1) {
                        try {
                            Thread.sleep(600); // Brief pause before retry
                        } catch (InterruptedException ignored) {}
                    }
                }
            }
        }

        // Only genuine keyword matches if API fails completely
        return keywordSearch(allBooks, userPrompt);
    }

    private List<Book> keywordSearch(List<Book> allBooks, String prompt) {
        String[] words = prompt.toLowerCase().split("\\s+");
        return allBooks.stream()
                .filter(b -> {
                    String combined = (b.getTitle() + " " + (b.getCategory() != null ? b.getCategory() : "") + " " + (b.getDescription() != null ? b.getDescription() : "")).toLowerCase();
                    for (String w : words) {
                        if (w.length() > 3 && combined.contains(w)) {
                            return true;
                        }
                    }
                    return false;
                })
                .limit(4)
                .toList();
    }
}