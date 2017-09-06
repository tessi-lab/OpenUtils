/*
 * Copyright 2017 Tessi lab.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.tessilab.oss.openutils.dictionary;

import io.tessilab.oss.openutils.FileUtils;
import io.tessilab.oss.openutils.distance.words.LevenshteinDistance;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * <b>Dictionary is a class representing a lexicon of words, that can be
 * modified and searched. It can also look for the closest known word of any
 * other given word.</b>
 * 
 * @author galaad
 * @see LevenshteinDistance
 * @see WordCount
 */
public class Dictionary {

    private static final Logger LOGGER = LogManager.getLogger(Dictionary.class);

    protected BKTree<String> wordTree;
    protected Set<String> wordSet;

    // -------Constructors
    /**
     * Basic Constructor
     */
    public Dictionary() {
        wordSet = new HashSet<>();
        initWordTree();
    }

    /**
     * Advanced Constructor. Import a whole dictionary from a text file.s
     * 
     * @param path
     *            The path of the file.
     */
    public Dictionary(String path) {
        this();
        initFromFile(path);
    }

    protected void initWordTree() {
        wordTree = new BKTree<>(new LevenshteinDistance(1e-3));
    }

    protected void initFromFile(String path) {
        try {
            List<String> lines = FileUtils.getFileLines(path);
            for (String line : lines) {
                String[] splits = line.split("\\s+");
                for (String word : splits) {
                    addNewWord(word);
                }
            }
        } catch (IOException ioe) {
            LOGGER.error("Exception while loading the dictionary", ioe);
        }
    }

    // ----------------

    /**
     * @return The size of the dictionary. (ie: the number of different words)
     */
    public Integer size() {
        return this.wordSet.size();
    }

    /**
     * @return TRUE if the dictionary does not contains any words.
     */
    public boolean isEmpty() {
        return this.wordSet.isEmpty();
    }

    // -------------------

    /**
     * Add a new word in the dictionary.
     * 
     * @param str
     *            The word to add.
     * @return TRUE if the word was properly added. FALSE if the word already
     *         existed.
     */
    public boolean addNewWord(String str) {
        if (!wordSet.contains(str)) {
            wordTree.add(str);
            wordSet.add(str);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Check whether or not a word is in the dictionary (Complexity= O(1)).
     * 
     * @param word
     *            A word.
     * @return TRUE if the dictionary contains the word.
     */
    public boolean contains(String word) {
        return wordSet.contains(word);
    }

    /**
     * Find one of the closest word from a given word in the dictionary
     * (Complexity: O(log(dico.size()))) .
     * 
     * @param word
     *            A word.
     * @return The closest word found (null if the dictionary is empty).
     */
    public String findClosestWord(String word) {
        if (isEmpty()) {
            return null;
        }
        return wordTree.findBestWordMatch(word);
    }

    /**
     * Find one of the closest word from a given word in the dictionary, within
     * a given distance (Complexity: O(log(dico.size()))) .
     * 
     * @param word
     *            A word
     * @param distance
     *            The maximum distance from the given word we accept results.
     * 
     * @return The closest word found, within the given distance (null if the
     *         dictionary is empty).
     */
    public String findClosestWord(String word, double distance) {
        return wordTree.findBestWordMatchWithinDistance(word, distance);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        Iterator<String> ite = wordSet.iterator();
        while (ite.hasNext()) {
            String s = ite.next();
            stringBuilder.append(s + " ");
        }
        return stringBuilder.toString();
    }

    /**
     * Write the words and their count in a file (the name of the file should
     * end with .dico for more convenience).
     * 
     * @param path
     *            The ".dico" file path.
     */
    public void writeInFile(String path) {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(path, "UTF-8");
            writer.print(this.toString());
        } catch (Exception e) {
            LOGGER.error("Exception while writing the dictionary in a file", e);
        } finally {
            if (writer != null)
                writer.close();
        }
    }

    /**
     * @return A stream of all the words.
     */
    public Stream<String> getWordStream() {
        return wordSet.stream();
    }

    /**
     * @return A stream of all the words, sorted.
     */
    public Stream<String> getWordStreamSorted() {
        return wordSet.stream().sorted();
    }

}
