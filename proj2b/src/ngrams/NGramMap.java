package ngrams;

import edu.princeton.cs.algs4.In;

import java.util.Collection;
import java.util.HashMap;


/**
 * An object that provides utility methods for making queries on the
 * Google NGrams dataset (or a subset thereof).
 *
 * An NGramMap stores pertinent data from a "words file" and a "counts
 * file". It is not a map in the strict sense, but it does provide additional
 * functionality.
 *
 * @author Josh Hug
 */
public class NGramMap {

    HashMap<String, TimeSeries> ngram = new HashMap<>();
    TimeSeries totalCounts = new TimeSeries();
    /**
     * Constructs an NGramMap from WORDSFILENAME and COUNTSFILENAME.
     */
    public NGramMap(String wordsFilename, String countsFilename) {
        In words = new In(wordsFilename);
        In counts = new In(countsFilename);
        int i = 0;
        while (words.hasNextLine()) {
            String nextLine = words.readLine();
            String[] splitLine = nextLine.split("\t");
            String word = splitLine[0];
            int year = Integer.parseInt(splitLine[1]);
            double count = Double.parseDouble(splitLine[2]);

            if (!ngram.containsKey(word)) {
                ngram.put(word, new TimeSeries());
            }
            ngram.get(word).put(year, count);
        }

        while (counts.hasNextLine()) {
            String nextLine = counts.readLine();
            String[] splitLine = nextLine.split(",");

            int year = Integer.parseInt(splitLine[0]);
            double totalCount = Double.parseDouble(splitLine[1]);

            totalCounts.put(year, totalCount);
        }
    }

    /**
     * Provides the history of WORD between STARTYEAR and ENDYEAR, inclusive of both ends. The
     * returned TimeSeries should be a copy, not a link to this NGramMap's TimeSeries. In other
     * words, changes made to the object returned by this function should not also affect the
     * NGramMap. This is also known as a "defensive copy". If the word is not in the data files,
     * returns an empty TimeSeries.
     */
    public TimeSeries countHistory(String word, int startYear, int endYear) {
        if (!ngram.containsKey(word)) {
            return new TimeSeries();
        }
        return new TimeSeries(ngram.get(word), startYear, endYear);
    }

    /**
     * Provides the history of WORD. The returned TimeSeries should be a copy, not a link to this
     * NGramMap's TimeSeries. In other words, changes made to the object returned by this function
     * should not also affect the NGramMap. This is also known as a "defensive copy". If the word
     * is not in the data files, returns an empty TimeSeries.
     */
    public TimeSeries countHistory(String word) {
        if (!ngram.containsKey(word)) {
            return new TimeSeries();
        }
        TimeSeries history = new TimeSeries();
        history.putAll(ngram.get(word));

        return history;
    }

    /**
     * Returns a defensive copy of the total number of words recorded per year in all volumes.
     */
    public TimeSeries totalCountHistory() {
        TimeSeries totalCountsCopy = new TimeSeries();
        totalCountsCopy.putAll(totalCounts);
        return totalCountsCopy;
    }

    /**
     * Provides a TimeSeries containing the relative frequency per year of WORD between STARTYEAR
     * and ENDYEAR, inclusive of both ends. If the word is not in the data files, returns an empty
     * TimeSeries.
     */
    public TimeSeries weightHistory(String word, int startYear, int endYear) {
        TimeSeries result = new TimeSeries();
        if (!ngram.containsKey(word)) {
            return result;
        }
        TimeSeries wordCount = new TimeSeries(ngram.get(word), startYear, endYear);
        for (int year : wordCount.years()) {
            if (totalCounts.containsKey(year)) {
                result.put(year, wordCount.get(year) / totalCounts.get(year));
            }
        }
        return result;
    }

    /**
     * Provides a TimeSeries containing the relative frequency per year of WORD compared to all
     * words recorded in that year. If the word is not in the data files, returns an empty
     * TimeSeries.
     */
    public TimeSeries weightHistory(String word) {
        TimeSeries result = new TimeSeries();
        if (!ngram.containsKey(word)) {
            return result;
        }
        TimeSeries wordCount = ngram.get(word);
        for (int year : wordCount.years()) {
            if (totalCounts.containsKey(year)) {
                result.put(year, wordCount.get(year) / totalCounts.get(year));
            }
        }
        return result;
    }

    /**
     * Provides the summed relative frequency per year of all words in WORDS between STARTYEAR and
     * ENDYEAR, inclusive of both ends. If a word does not exist in this time frame, ignore it
     * rather than throwing an exception.
     */
    public TimeSeries summedWeightHistory(Collection<String> words,
                                          int startYear, int endYear) {
        TimeSeries result = new TimeSeries();
        for (String word : words) {
            result = result.plus(weightHistory(word, startYear, endYear));
        }
        return result;
    }

    /**
     * Returns the summed relative frequency per year of all words in WORDS. If a word does not
     * exist in this time frame, ignore it rather than throwing an exception.
     */
    public TimeSeries summedWeightHistory(Collection<String> words) {
        TimeSeries result = new TimeSeries();
        for (String word : words) {
            result = result.plus(weightHistory(word));
        }
        return result;
    }
}
