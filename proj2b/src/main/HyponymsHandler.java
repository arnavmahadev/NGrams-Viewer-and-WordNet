package main;

import browser.NgordnetQuery;
import browser.NgordnetQueryHandler;
import ngrams.NGramMap;
import ngrams.TimeSeries;

import java.util.*;

public class HyponymsHandler extends NgordnetQueryHandler {
    private Hyponyms hyponyms;
    private NGramMap ngrams;

    public HyponymsHandler(HashMap<Integer, HashSet<String>> synsetWords, HashMap<Integer, HashSet<Integer>> graph,
                           NGramMap ngrams) {
        this.hyponyms = new Hyponyms(synsetWords, graph);
        this.ngrams = ngrams;
    }
    @Override
    public String handle(NgordnetQuery q) {
        List<String> words = q.words();
        int k = q.k();
        int startYear = q.startYear();
        int endYear = q.endYear();

        if (words.isEmpty()) {
            return "[]";
        }

        HashSet<String> hypos = hyponyms.getHyponyms(words);
        if (hypos.isEmpty()) {
            return "[]";
        }

        if (k == 0) {
            ArrayList<String> sortedHypos = new ArrayList<>(hypos);
            Collections.sort(sortedHypos);
            return "[" + String.join(", ", sortedHypos) + "]";
        }

        HashMap<String, Double> wordCounts = new HashMap<>();

        for (String word : hypos) {
            TimeSeries ts = ngrams.countHistory(word, startYear, endYear);
            double totalCount = 0;
            for (int year : ts.years()) {
                totalCount += ts.get(year);
            }

            if (totalCount > 0) {
                wordCounts.put(word, totalCount);
            }
        }

        if (wordCounts.isEmpty()) {
            return "[]";
        }

        List<Map.Entry<String, Double>> entries = new ArrayList<>(wordCounts.entrySet());
        Collections.sort(entries, Collections.reverseOrder(Map.Entry.comparingByValue()));
        List<String> topWords = new ArrayList<>();
        int count = 0;
        for (Map.Entry<String, Double> entry : entries) {
            if (count >= k) {
                break;
            }
            topWords.add(entry.getKey());
            count++;
        }
        Collections.sort(topWords);

        return "[" + String.join(", ", topWords) + "]";
    }
}
