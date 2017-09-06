
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

import io.tessilab.oss.openutils.distance.words.Distance;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * This class in an implementation of a Burkhard-Keller tree in Java. The
 * BK-Tree is a tree structure to quickly finding close matches to any defined
 * object.
 * 
 * The BK-Tree was first described in the paper:
 * "Some Approaches to Best-Match File Searching" by W. A. Burkhard and R. M.
 * Keller It is available in the ACM archives.
 * 
 * Another good explanation can be found here:
 * http://blog.notdot.net/2007/4/Damn-Cool-Algorithms-Part-1-BK-Trees
 * 
 * Searching the tree yields O(logn), which is a huge upgrade over brute force
 * 
 * @author Josh Clemm
 *
 */
public class BKTree<E> {

    private Node root;
    private HashMap<E, Double> matches;
    private Distance<E> distance;
    protected E bestTerm;

    protected BKTree() {

    }

    public BKTree(Distance<E> distance) {
        root = null;
        this.distance = distance;
    }

    public void add(E term) {
        if (root != null) {
            root.add(term);
        } else {
            root = new Node(term);
        }
    }

    /**
     * This method will find all the close matching Objects within a certain
     * threshold. For instance, for search for similar strings, threshold set to
     * 1 will return all the strings that are off by 1 edit distance.
     * 
     * @param searchObject
     * @param threshold
     * @return
     */
    public Map<E, Double> query(E searchObject, double threshold) {
        matches = new HashMap<>();
        root.query(searchObject, threshold, matches);
        return matches;
    }

    /**
     * Attempts to find the closest match to the search term.
     * 
     * @param term
     * @return the edit distance of the best match
     */
    public double find(E term) {
        return root.findBestMatch(term, Double.MAX_VALUE);
    }

    /**
     * Attempts to find the closest match to the search term.
     * 
     * @param term
     * @return a match that is within the best edit distance of the search term.
     */
    public E findBestWordMatchWithinDistance(E term, double threshold) {
        bestTerm = null;
        root.findBestMatch(term, threshold);
        return root.getBestTerm();
    }

    /**
     * Attempts to find the closest match to the search term.
     * 
     * @param term
     * @return a match that is within the best edit distance of the search term.
     */
    public E findBestWordMatch(E term) {
        root.findBestMatch(term, Integer.MAX_VALUE);
        return root.getBestTerm();
    }

    /**
     * Attempts to find the closest match to the search term.
     * 
     * @param term
     * @return a match that is within the best edit distance of the search term.
     */
    public Map<E, Double> findBestWordMatchWithDistance(E term) {
        double d = root.findBestMatch(term, Double.MAX_VALUE);
        Map<E, Double> returnMap = new HashMap<>();
        returnMap.put(root.getBestTerm(), d);
        return returnMap;
    }

    private class Node {

        E term;
        TreeMap<Double, Node> children;

        public Node(E term) {
            this.term = term;
            children = new TreeMap<>();
        }

        public void add(E term) {
            double score = distance.getDistance(term, this.term);

            Node child = children.get(score);
            if (child != null) {
                child.add(term);
            } else {
                children.put(score, new Node(term));
            }
        }

        public double findBestMatch(E term, double bestDistance) {
            double distanceAtNode = distance.getDistance(term, this.term);

            double bestDistance2 = bestDistance;
            if (distanceAtNode <= bestDistance2) {
                bestDistance2 = distanceAtNode;
                bestTerm = this.term;
            }

            double possibleBest;

            for (Map.Entry<Double, Node> entry : children.subMap(distanceAtNode - bestDistance2, true, distanceAtNode + bestDistance2, true).entrySet()) {
                Double score = entry.getKey();
                if (score < distanceAtNode - bestDistance2)
                    continue;
                if (score > distanceAtNode + bestDistance2)
                    break;
                possibleBest = entry.getValue().findBestMatch(term, bestDistance2);
                if (possibleBest < bestDistance2) {
                    bestDistance2 = possibleBest;

                    // return early if perfect match
                    if (bestDistance2 < 1e-4) {
                        return 0;
                    }
                }
            }
            return bestDistance2;
        }

        public E getBestTerm() {
            return bestTerm;
        }

        public void query(E term, double threshold, Map<E, Double> collected) {
            double distanceAtNode = distance.getDistance(term, this.term);

            if (distanceAtNode <= threshold) {
                collected.put(this.term, distanceAtNode);
            }

            for (Map.Entry<Double, Node> entry : children.entrySet()) {
                double dist = entry.getKey();
                if (dist <= distanceAtNode + threshold && dist >= distanceAtNode - threshold)
                    entry.getValue().query(term, threshold, collected);
            }
        }
    }

}
