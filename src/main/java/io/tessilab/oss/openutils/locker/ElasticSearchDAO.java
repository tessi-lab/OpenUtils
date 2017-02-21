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
package io.tessilab.oss.openutils.locker;

import io.tessilab.oss.openutils.elasticsearch.ElasticSearchBaseData;
import io.tessilab.oss.openutils.elasticsearch.ElasticSearchHelper;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.get.GetResponse;

/**
 * The concrete DAO to write in a ElasticSearch database. 
 * <p>
 * We write the locks in givedIndex/classifierName/descriptionparameters
 *
 * @author Andres BEL ALONSO
 */
public class ElasticSearchDAO implements DAO {

    private static final Logger LOGGER = LogManager.getLogger(ElasticSearchDAO.class);
    
    private static final String endedJobName = "ended";

    private String type;
        
    private ElasticSearchHelper helper;

    public ElasticSearchDAO(String type, ElasticSearchBaseData elasticSearchData) {
        this.type = type;
        helper = new ElasticSearchHelper(elasticSearchData);
    }

    public ElasticSearchDAO(String type, ElasticSearchHelper client) {
        this.type = type;
        helper =  client;
    }

    @Override
    public DAOResponse readLockEntry(String descriptionParameters) {
        GetResponse response = helper.prepareGet(type, descriptionParameters,0);
        DAOResponse outResponse;
        if (response.isExists()) {
            String iD = (String) response.getSource().get("id");
            Boolean ended = (Boolean) response.getSource().get(endedJobName);
            Long date = (Long) response.getSource().get("date");
            outResponse = new DAOResponse(true, iD,ended, date);
        } else {
            outResponse = new DAOResponse();
        }
        return outResponse;
    }

    @Override
    public void writeEntry(String description, String iD) {
        Map<String, Object> json = new HashMap<>();
        json.put("id", iD);
        json.put(endedJobName, false);
        Date date = Calendar.getInstance(TimeZone.getDefault()).getTime();
        json.put("date", date.getTime());
        helper.prepareIndex(type,description,0,json);
    }

    /**
     * Writes the job as done
     *
     * @param description
     */
    @Override
    public void writeJobDone(String description) {
        helper.update(type,description,0,endedJobName,true);
    }

    @Override
    public void deleteEntry(String description) {
        helper.prepareDelete(type, description, 0);
        
    }
    
    @Override
    public void endResearch() {
        helper.close();
    }

}
