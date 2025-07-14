package main;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class Hyponyms {
    private HashMap<Integer, HashSet<String>> synsetWords;
    private HashMap<Integer, HashSet<Integer>> graph;

    public Hyponyms(HashMap<Integer, HashSet<String>> synsetWords, HashMap<Integer, HashSet<Integer>> graph) {
        this.synsetWords = synsetWords;
        this.graph = graph;
    }

    public HashSet<String> getHyponyms(String word) {
        HashSet<Integer> synsetIds = new HashSet<>();
        for (int id : synsetWords.keySet()) {
            if (synsetWords.get(id).contains(word)) {
                synsetIds.add(id);
            }
        }

        if (synsetIds.isEmpty()) {
            return new HashSet<>();
        }

        HashSet<Integer> allHyponymSynsets = new HashSet<>();
        for (int synsetId : synsetIds) {
            dfs(synsetId, allHyponymSynsets);
        }

        allHyponymSynsets.addAll(synsetIds);

        HashSet<String> hyponyms = new HashSet<>();
        for (int hyponymId : allHyponymSynsets) {
            hyponyms.addAll(synsetWords.getOrDefault(hyponymId, new HashSet<>()));
        }

        return hyponyms;
    }

    private void dfs(int synsetId, HashSet<Integer> visited) {
        if (!graph.containsKey(synsetId)) {
            return;
        }

        HashSet<Integer> children = graph.get(synsetId);
        for (int child : children) {
            if (!visited.contains(child)) {
                visited.add(child);
                dfs(child, visited);
            }
        }
    }

    public HashSet<String> getHyponyms(List<String> words) {
        if (words.isEmpty()) {
            return new HashSet<>();
        }

        if (words.size() == 1) {
            return getHyponyms(words.get(0));
        }

        HashSet<String> result = null;
        for (String word : words) {
            HashSet<String> currentHyponyms = getHyponyms(word);
            if (currentHyponyms.isEmpty()) {
                return new HashSet<>();
            }
            if (result == null) {
                result = new HashSet<>(currentHyponyms);
            } else {
                result.retainAll(currentHyponyms);

                if (result.isEmpty()) {
                    return result;
                }
            }
        }
        return result;
    }
}
