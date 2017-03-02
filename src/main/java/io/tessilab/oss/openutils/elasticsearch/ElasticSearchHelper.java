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

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.ActionRequestValidationException;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetRequestBuilder;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

/**
 *  Helper class for elastic search client manipulation A helper
 *         helps a class to connect to one elasticsearch database and to write
 *         in only one index
 * @author David Goncalves
 */
public class ElasticSearchHelper implements AutoCloseable {

    private static final Logger LOGGER = LogManager.getLogger(ElasticSearchHelper.class);

    private static final int MAX_NUMBER_ATTEMPTS = 720; // The max waith time is
                                                        // near an hour

    private TransportClient helperClient;

    private String index;

    public ElasticSearchHelper(ElasticSearchBaseData data) {
        try {
            helperClient = openConnection(data.getClusterName(), data.getHostname(), data.getPort(), data.isSniff());
            index = data.getIndex();
        } catch (UnknownHostException ex) {
            throw new ElasticSearchHelperError(ex);
        }
    }
 
    private TransportClient openConnection(String clusterName, String hostname, int port, boolean sniff) throws UnknownHostException {
        TransportClient client;
        Settings settings = Settings.settingsBuilder().put("cluster.name", clusterName).put("client.transport.sniff", sniff).build();
        client = new TransportClient.Builder().settings(settings).build();
        client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(hostname), port));
        return client;
    }

    /**
     * Performs a multiget request to the ElasticSearch database.
     * 
     * @param multiGetRequestBuilder : The builder to create the multirequest
     * @return The answer of the database
     * @throws HelperMissingContentException This exeception is throw when the 
     * multiget fails
     */
    public MultiGetResponse getMultiGetResponse(MultiGetRequestBuilder multiGetRequestBuilder) throws HelperMissingContentException {
        boolean done = false;
        MultiGetResponse multiGetResponse = null;
        while (!done) {
            try {
                multiGetResponse = multiGetRequestBuilder.get();
                done = true;
            } catch (ElasticsearchException ex) {
                LOGGER.trace(ex);
                LOGGER.warn("A node was not available. Trying to reconnect.");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ex1) {
                    LOGGER.error(ex1);
                }
            } catch (ActionRequestValidationException ex) {
                // Here the multiget failed
                LOGGER.trace(ex);
                LOGGER.trace("The multi get request failed");
                throw new HelperMissingContentException();
            }

        }
        return multiGetResponse;

    }

    /**
     * Performs a prepare index to the ElasticSearch database. This action creates 
     * a document in {index that has been defined to this helper}/type/id 
     * <p>
     * If this request fails one time, the helper will wait and retry to do it.
     * It will retry almost an hour before throwing a {@link io.tessilab.oss.openutils.elasticsearch.ElasticSearchHelperError}
     * @param type The type of document in the index
     * @param id The id of the document
     * @param attemptNumber The number attempt number to do this request. Out of this
     * class, this value should be 0
     * @param json A json with the information that will be add to the document
     * @return The answer of the database.
     */
    public IndexResponse prepareIndex(String type, String id, int attemptNumber, Map<String, Object> json) {
        try {
            IndexResponse response = helperClient.prepareIndex(index, type, id).setSource(json).get();
            return response;
        } catch (ElasticsearchException e) {
            LOGGER.warn(e);
            if (attemptNumber < MAX_NUMBER_ATTEMPTS) {
                LOGGER.warn("The get could not be done, because there was a problem in the db. Retrying to load it");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ex) {
                    LOGGER.trace(ex);
                }
                return prepareIndex(type, id, attemptNumber++, json);
            }
            throw new ElasticSearchHelperError(e);
        }
    }

    /**
     * Performs a prepare index to the ElasticSearch database. This action creates 
     * a document in {index that has been defined to this helper}/type/id 
     * <p>
     * If this request fails one time, the helper will wait and retry to do it.
     * It will retry almost an hour before throwing a {@link io.tessilab.oss.openutils.elasticsearch.ElasticSearchHelperError}
     * @param type The type of document in the index
     * @param id The id of the document
     * @param attemptNumber The number attempt number to do this request. Out of this
     * class, this value should be 0
     * @param xb A json with the information that will be add to the document
     * @return The answer of the database.
     */    
    public IndexResponse prepareIndex(String type, String id, int attemptNumber, XContentBuilder xb) {
        try {
            IndexResponse response = helperClient.prepareIndex(index, type, id).setSource(xb).get();
            return response;
        } catch (ElasticsearchException e) {
            LOGGER.warn(e);
            if (attemptNumber < MAX_NUMBER_ATTEMPTS) {
                LOGGER.warn("The get could not be done, because there was a problem in the db. Retrying to load it");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ex) {
                    LOGGER.trace(ex);
                }
                return prepareIndex(type, id, attemptNumber++, xb);
            }
            throw new ElasticSearchHelperError(e);
        }
    }
    
    /**
     * Performs a prepare index to the ElasticSearch database. This action creates 
     * a document in {index that has been defined to this helper}/type/{some_id} 
     * <p>
     * The database will give an identification to the document
     * <p>
     * If this request fails one time, the helper will wait and retry to do it.
     * It will retry almost an hour before throwing a {@link io.tessilab.oss.openutils.elasticsearch.ElasticSearchHelperError}
     * @param type The type of document in the index
     * @param attemptNumber The number attempt number to do this request. Out of this
     * class, this value should be 0
     * @param json A json with the information that will be add to the document
     * @return The answer of the database.
     */
    public IndexResponse prepareIndex(String type, int attemptNumber, Map<String, Object> json) {
        try {
            IndexResponse response = helperClient.prepareIndex(index, type).setSource(json).get();
            return response;
        } catch (ElasticsearchException e) {
            LOGGER.trace(e);
            if (attemptNumber < MAX_NUMBER_ATTEMPTS) {
                LOGGER.warn("The index could not be query, because there was a problem in the db. Retrying to load it");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ex) {
                    LOGGER.trace(ex);
                }
                return prepareIndex(type, attemptNumber++, json);
            }
            throw new ElasticSearchHelperError(e);
        }
    }

    /**
     * Performs a get action on the ElasticSearchDatabase. This action will retrive
     * the document in  {index that has been defined to this helper}/type/id .
     * <p>
     * If this request fails one time, the helper will wait and retry to do it.
     * It will retry almost an hour before throwing a {@link io.tessilab.oss.openutils.elasticsearch.ElasticSearchHelperError}
     * 
     * @param type The type of document in the index
     * @param id The id of the document
     * @param attemptNumber The number attempt number to do this request. Out of this
     * class, this value should be 0
     * @return The answer of the database with the informations of the requested document
     */
    public GetResponse prepareGet(String type, String id, int attemptNumber) {
        try {
            GetResponse response = helperClient.prepareGet(index, type, id).get();
            return response;
        } catch (ElasticsearchException e) {
            LOGGER.warn(e);
            if (attemptNumber < MAX_NUMBER_ATTEMPTS) {
                LOGGER.warn("The get could not be done, because there was a problem in the db. Retrying to load it");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ex) {
                    LOGGER.trace(ex);
                }
                return prepareGet(type, id, attemptNumber++);
            }
            throw new ElasticSearchHelperError(e);
        }
    }

    /**
     * Performs an update action on the ElasticSearch database. This will change
     * the values over an existing document, and in a precise field. The change document will be : 
     * {index that has been defined to this helper}/type/id .
     * <p>
     * If this request fails one time, the helper will wait and retry to do it.
     * It will retry almost an hour before throwing a {@link io.tessilab.oss.openutils.elasticsearch.ElasticSearchHelperError}
     * 
     * 
     * @param type The type of document in the index
     * @param iD The id of the document
     * @param attemptNumber The number attempt number to do this request. Out of this
     * class, this value should be 0
     * @param fieldName The name of the field to change
     * @param value The new value to the field
     * @return The answer of the database, describing if the change has been done or not. 
     */
    public UpdateResponse update(String type, String iD, int attemptNumber, String fieldName, Object value) {
        try {
            UpdateRequest updateRequest = new UpdateRequest();
            updateRequest.index(index);
            updateRequest.type(type);
            updateRequest.id(iD);
            updateRequest.doc(jsonBuilder().startObject().field(fieldName, value).endObject());
            UpdateResponse response = helperClient.update(updateRequest).get();
            return response;
        } catch (ElasticsearchException e) {
            LOGGER.warn(e);
            if (attemptNumber < MAX_NUMBER_ATTEMPTS) {
                LOGGER.warn("The update could not be done , because there was a problem in the db. Retrying to load it");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ex) {
                    LOGGER.trace(ex);
                }
                return update(type, iD, attemptNumber++, fieldName, value);
            }
            throw new ElasticSearchHelperError(e);
        } catch (InterruptedException | ExecutionException | IOException ex) {
            LOGGER.error("Not able to update the document {}/{}/{} in the field", this.index, type, iD, fieldName);
            LOGGER.error(ex);
            LOGGER.catching(ex);
            throw new ElasticSearchHelperError("Not able to update the document" + iD);
        }
    }

    /**
     * 
     * @return The index in wich this helper is writing 
     */
    public String getSavingIndex() {
        return index;
    }

    /**
     * 
     * @return A multiget object to perform a multiget action
     */
    public MultiGetRequestBuilder prepareMultiGet() {
        return helperClient.prepareMultiGet();
    }

    /**
     * 
     * @return An object to do a bulk
     */
    public BulkRequestBuilder prepareBulk() {
        return helperClient.prepareBulk();
    }

    /**
     * Simply created an index on {index that has been defined to this helper}/type/id.
     * @param type The type of document in the index
     * @param iD The id of the document
     * @return The answer of the database
     */
    public IndexRequestBuilder prepareIndex(String type, String iD) {
        return helperClient.prepareIndex(this.index, type, iD);
    }

    /**
     * Performs a bulk action on the database. 
     * <p>
     * If this request fails one time, the helper will wait and retry to do it.
     * It will retry almost an hour before throwing a {@link io.tessilab.oss.openutils.elasticsearch.ElasticSearchHelperError}
     * @param bulkRequest The object that build the request
     * @param attemptNumber The number attempt number to do this request. Out of this
     * class, this value should be 0
     * @return The answer of the database
     */
    public BulkResponse executeBulkRequest(BulkRequestBuilder bulkRequest, int attemptNumber) {
        try {
            return bulkRequest.execute().actionGet();
        } catch (ElasticsearchException e) {
            LOGGER.warn(e);
            if (attemptNumber < MAX_NUMBER_ATTEMPTS) {
                LOGGER.warn("The update could not be done , because there was a problem in the db. Retrying to load it");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ex) {
                    LOGGER.trace(ex);
                }
                return executeBulkRequest(bulkRequest, attemptNumber++);
            }
            throw new ElasticSearchHelperError(e);
        }
    }

    @Override
    public void close() {
        helperClient.close();
    }
    
    /**
     * Performs a delete action on the ElasticSearch database. This will delete the
     * document on  {index that has been defined to this helper}/type/id.
     * <p>
     * If this request fails one time, the helper will wait and retry to do it.
     * It will retry almost an hour before throwing a {@link io.tessilab.oss.openutils.elasticsearch.ElasticSearchHelperError}
     * @param type The type of document in the index
     * @param description The id of the document
     * @param attemptNumber The number attempt number to do this request. Out of this
     * class, this value should be 0
     * @return The answer of the database
     */
    public DeleteResponse prepareDelete(String type, String description, int attemptNumber) {
        try {
            return helperClient.prepareDelete(this.index, type, description).get();
        } catch (ElasticsearchException e) {
            LOGGER.warn(e);
            if (attemptNumber < MAX_NUMBER_ATTEMPTS) {
                LOGGER.warn("The delete could not be done, because there was a problem in the db. Retrying to load it");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ex) {
                    LOGGER.trace(ex);
                }
                return prepareDelete(type, description, attemptNumber++);
            }
            throw new ElasticSearchHelperError(e);
        }

    }

}
