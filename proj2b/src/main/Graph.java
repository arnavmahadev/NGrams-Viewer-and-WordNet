package main;

import edu.princeton.cs.algs4.In;

import java.util.HashMap;
import java.util.HashSet;

public class Graph {
    HashMap<Integer, HashSet<Integer>> graph;
    HashMap<Integer, HashSet<String>> synsetWords;

    public Graph(String synsetsFileName, String hyponymsFileName) {
        graph = new HashMap<>();
        synsetWords = new HashMap<>();
        In synsets = new In(synsetsFileName);
        In hyponyms = new In(hyponymsFileName);
        while (synsets.hasNextLine()) {
            String nextLine = synsets.readLine();
            String[] splitLine = nextLine.split(",");
            int id = Integer.parseInt(splitLine[0]);
            String synset = splitLine[1];
            String[] words = synset.split(" ");
            HashSet<String> wordSet = new HashSet<>();
            for (String word : words) {
                wordSet.add(word);
            }
            synsetWords.put(id, wordSet);
        }

        while (hyponyms.hasNextLine()) {
            String nextLine = hyponyms.readLine();
            String[] splitLine = nextLine.split(",");
            int id = Integer.parseInt(splitLine[0]);
            HashSet<Integer> hyponymSet = graph.getOrDefault(id, new HashSet<>());
            for (int i = 1; i < splitLine.length; i++) {
                int hyponymId = Integer.parseInt(splitLine[i]);
                hyponymSet.add(hyponymId);
            }
            graph.put(id, hyponymSet);
        }
    }
}
