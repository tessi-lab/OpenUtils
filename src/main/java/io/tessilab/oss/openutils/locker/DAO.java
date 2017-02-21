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

/**
 * The class than a Data Acess Object of a {@link io.tessilab.oss.openutils.locker.JobLocker} 
 * must implement
 * @author Andres BEL ALONSO
 */
public interface DAO {

    /**
     * Reads a lock entry identify by the parameter.
     * @param lockID
     * @return 
     */
    public DAOResponse readLockEntry(String lockID);

    /**
     * Writes the entry in order to lock the job.
     * @param description : The description of the entry
     * @param iD : The id of the the lock.
     */
    public void writeEntry(String description, String iD);

    /**
     * Marks the entry identify by the parameter as done.
     * @param lockID 
     */
    public void writeJobDone(String lockID);

    /**
     * Notifies the DAO that the work is done.
     */
    public void endResearch();

    /**
     * Deletes the lock.
     * @param lockID 
     */
    public void deleteEntry(String lockID);
    
}
