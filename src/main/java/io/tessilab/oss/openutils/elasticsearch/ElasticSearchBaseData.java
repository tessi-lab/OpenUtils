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
package io.tessilab.oss.openutils.elasticsearch;

/**
 *  Simple data wrapper that contains the base data needed for an
 *         elastic search request.
 * @author David Goncalves
 */
public class ElasticSearchBaseData {

    private final String elasticSearchIndex;
    private final String hostname;
    private final int port;
    private final String clusterName;
    private final boolean sniff;

    public ElasticSearchBaseData(String elasticSearchIndex, String hostname, int port, String clusterName) {
        this(elasticSearchIndex, hostname, port, clusterName, false);
    }

    public ElasticSearchBaseData(String elasticSearchIndex, String hostname, int port, String clusterName, boolean sniff) {
        super();
        this.elasticSearchIndex = elasticSearchIndex.toLowerCase();
        this.hostname = hostname;
        this.port = port;
        this.clusterName = clusterName;
        this.sniff = sniff;
    }

    public String getIndex() {
        return elasticSearchIndex;
    }

    public String getHostname() {
        return hostname;
    }

    public int getPort() {
        return port;
    }

    public String getClusterName() {
        return clusterName;
    }

    public boolean isSniff() {
        return sniff;
    }
}
