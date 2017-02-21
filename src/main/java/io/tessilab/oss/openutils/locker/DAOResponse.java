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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The class to encapsulate the answer of a {@link io.tessilab.oss.openutils.locker.DAO}
 * @author Andres BEL ALONSO
 */
public class DAOResponse {

    private final boolean existsEntry;

    private final String writtenID;

    private final boolean endedJob;

    /**
     * In form of date.getTime, otherwise, the number of miliseconds that have
     * pase since 1 janury 1970
     */
    private final long date;

    private static final Logger LOGGER = LogManager.getLogger(DAOResponse.class);

    /**
     * Constructor for when the entry does not exists
     * it calls the other contructor with (false, "", false, 0)
     */
    public DAOResponse() {
        this(false, "", false, 0);
    }
    
    public DAOResponse(boolean existsEntry, String writtenID, boolean endedJob, long date) {
        this.existsEntry = existsEntry;
        this.writtenID = writtenID;
        this.endedJob = endedJob;
        this.date = date;
    }

    public boolean existEntry() {
        return existsEntry;
    }

    public String getWrittenID() {
        if (!existsEntry) {
            LOGGER.warn("The entry does not exists, the result will be empty");
        }
        return writtenID;
    }

    public boolean isEndedJob() {
        return endedJob;
    }

    public long getDate() {
        return date;
    }

}
