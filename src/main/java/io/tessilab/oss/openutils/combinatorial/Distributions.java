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
package io.tessilab.oss.openutils.combinatorial;

import java.util.List;

public class Distributions {

    private List<List<Integer>> distribs;

    public Distributions(List<List<Integer>> distribs) {
        this.distribs = distribs;
    }

    public List<List<Integer>> getDistribs() {
        return distribs;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < distribs.size(); i++) {
            builder.append("[");
            for (int j = 0; j < distribs.get(i).size(); j++) {
                builder.append(distribs.get(i).get(j));
                if (j < distribs.get(i).size() - 1)
                    builder.append(",");
            }
            builder.append("]");
            if (i < distribs.size() - 1)
                builder.append("\n");
        }

        return builder.toString();
    }
}
