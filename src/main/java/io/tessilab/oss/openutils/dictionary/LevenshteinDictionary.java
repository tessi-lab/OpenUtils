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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class LevenshteinDictionary extends Dictionary {

    private Node root;
    private String bestTerm;
    private int maxWordLength;
    protected int[][] mat = null;
    private int[][] cache = null;
    private Character[] wordChar;
    private int wordLength;

    public LevenshteinDictionary() {
        super();
    }

    public LevenshteinDictionary(String path) {
        super(path);
    }

    @Override
    protected void initWordTree() {
        // do nothing
    }

    @Override
    public boolean addNewWord(String str) {
        if (str.isEmpty())
            return false;
        maxWordLength = Math.max(maxWordLength, str.length());

        if (!wordSet.contains(str)) {
            wordSet.add(str);
            if (root == null)
                root = new Node();
            root.add(str, str);
            return true;
        } else {
            return false;
        }
    }

    public boolean removeWord(String str) {
        if (str.isEmpty())
            return false;
        if (!wordSet.contains(str))
            return false;
        root.remove(str);
        return wordSet.remove(str);
    }

    private void initWord(String word) {
        wordLength = word.length();
        if (cache == null) {
            root.finalizeTree();
            cache = new int[maxWordLength + 1][maxWordLength + 1];

            for (int i = 0; i <= maxWordLength; i++) {
                cache[i][0] = i;
                cache[0][i] = i;
            }
        }
        if (word.length() > maxWordLength) {
            mat = new int[maxWordLength + 1][word.length() + 1];

            for (int i = 0; i <= maxWordLength; i++)
                mat[i][0] = i;
            for (int j = 0; j <= wordLength; j++)
                mat[0][j] = j;
        } else {
            mat = cache;
        }

        wordChar = new Character[wordLength];
        for (int i = 0; i < wordLength; i++) {
            wordChar[i] = word.charAt(i);
        }
    }

    @Override
    public String findClosestWord(String word) {
        return findClosestWord(word, Integer.MAX_VALUE);
    }

    @Override
    public synchronized String findClosestWord(String word, double distance) {
        if (contains(word))
            return word;
        else if ((int) distance == 0)
            return null;

        bestTerm = null;
        initWord(word);

        root.startSearch((int) distance);
        return bestTerm;
    }

    private class Node {

        private String value = null;
        private Map<Character, Node> childrens = new HashMap<>();
        private Node[] childrensArray = null;
        private Character[] childrensArrayKey = null;
        private int childrensSize = -1;

        private void add(String str, String word) {
            if (str.isEmpty()) {
                value = word;
                return;
            }
            Character first = str.charAt(0);
            Node n = childrens.get(first);
            if (n == null) {
                n = new Node();
                childrens.put(first, n);
            }
            n.add(str.substring(1), word);
        }

        private void finalizeTree() {
            childrensSize = childrens.size();
            childrensArray = new Node[childrensSize];
            childrensArrayKey = new Character[childrensSize];
            int i = 0;
            for (Entry<Character, Node> entry : childrens.entrySet()) {
                childrensArray[i] = entry.getValue();
                childrensArrayKey[i] = entry.getKey();
                entry.getValue().finalizeTree();
                i++;
            }
        }

        private void remove(String str) {
            if (str.isEmpty())
                value = null;
            else
                childrens.get(str.charAt(0)).remove(str.substring(1));
        }

        public int startSearch(int maxScore) {
            int currScore = maxScore;

            for (int i = 0; i < childrensSize; i++) {
                int temp = childrensArray[i].search(0, childrensArrayKey[i], currScore);
                if (temp < currScore) {
                    currScore = temp;
                    if (currScore == 0)
                        return 0;
                }
            }
            return currScore;
        }

        protected int search(int currIndex, Character letter, int maxScore) {
            int[] previousRow = mat[currIndex];
            int currentIndex = currIndex + 1;
            int[] currentRow = mat[currentIndex];
            int currentRowMin = currentIndex;
            int firstCurrentRowMinIndex = 0;
            int lastCurrentRowMinIndex = 0;
            for (int i = 0; i < wordLength; i++) {
                int insertCost = currentRow[i] + 1;
                int deleteCost = previousRow[i + 1] + 1;
                int replaceCost = previousRow[i];
                if (!wordChar[i].equals(letter))
                    replaceCost++;

                int min = insertCost;
                if (deleteCost < min)
                    min = deleteCost;
                if (replaceCost < min)
                    min = replaceCost;

                currentRow[i + 1] = min;

                if (min == currentRowMin) {
                    lastCurrentRowMinIndex = i + 1;
                } else if (min < currentRowMin) {
                    currentRowMin = min;
                    lastCurrentRowMinIndex = firstCurrentRowMinIndex = i + 1;
                }

            }

            int result = maxScore;
            // found a valid result
            if (currentRow[wordLength] <= result && value != null) {
                result = currentRow[wordLength];
                bestTerm = value;
                if (result == 0)
                    return 0;
            }

            if (currentRowMin > result)
                return result;

            // we can stop as we have already found a result with the current
            // value
            if (currentRowMin == result && bestTerm != null)
                return result;

            // we check if the rest of the word is in the trie (perfect match)
            for (int i = firstCurrentRowMinIndex; i <= lastCurrentRowMinIndex; i++) {
                if (currentRow[i] == currentRowMin) {
                    String found = find(i);
                    if (found != null) {
                        bestTerm = found;
                        return currentRowMin;
                    }
                }
            }

            // if a solution is already found we can return when current min is
            // result - 1 because we already checked for exact ending
            if (currentRowMin == result - 1 && bestTerm != null)
                return result;

            if (currentRowMin < result) {
                for (int i = 0; i < childrensSize; i++) {
                    int temp = childrensArray[i].search(currentIndex, childrensArrayKey[i], result);
                    if (temp < result) {
                        result = temp;
                        if (result - 1 == currentRowMin)
                            return result;
                    }
                }
            }
            return result;
        }

        private String find(int index) {
            if (index == wordLength)
                return value;
            Node n = childrens.get(wordChar[index]);
            if (n != null)
                return n.find(index + 1);
            return null;
        }

    }
}
