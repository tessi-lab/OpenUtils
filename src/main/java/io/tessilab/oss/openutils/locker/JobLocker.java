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

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The class that use a {@link io.tessilab.oss.openutils.locker.DAO} to lock jobs,
 * in for example a parallel context execution. 
 *
 * @author Andres BEL ALONSO
 * @param <JOBTYPE> : The job type we are locking
 */
public class JobLocker<JOBTYPE extends JobParameter> {

    private static final Logger LOGGER = LogManager.getLogger(JobLocker.class);

    private final DAO dao;
    private final String associatedID;
    private boolean lockingJob = false;
    private JOBTYPE currentParams;
    private final boolean doNonEndedJobs;
    private int waitTime = 5000;
    private final long tooOldLock;

    /**
     *
     * @param dao The object allowing to access to the database
     * @param pID The id of the process that will use the joblocker
     * @param doNonEndedJobs : true if we will do non ended job to are locked
     * for too long
     * @param tooOldLock : How much MILISECONDS must pass to consider that a job
     * is too old. A negative value will be transformed in 0
     */
    public JobLocker(DAO dao, String pID, boolean doNonEndedJobs, long tooOldLock) {
        LOGGER.info("Creating the job locker");
        this.dao = dao;
        associatedID = pID;
        this.doNonEndedJobs = doNonEndedJobs;
        this.tooOldLock = (tooOldLock < 0 ? 0 : tooOldLock);
    }

    /**
     * Sets the time to wait between each step of the process.
     *
     * @param time : The time in seconds
     */
    public void setWaitTime(int time) {
        this.waitTime = (waitTime < 0 ? 5000 : waitTime);
    }

    public boolean lockJob(JOBTYPE parameters) {
        if(this.lockingJob) {
            throw new JobLockerError("ERROR : We want to lock a new job without unlicking the locked one.");
        }
        DAOResponse response = dao.readLockEntry(parameters.getDescriptionParameters());
        if (!response.existEntry()) {
            /*We have the job*/
            LOGGER.info("Trying to lock the job {} who has not an owner", parameters.getDescriptionParameters());
            return doJobLocking(parameters);
        } else if (doNonEndedJobs && !response.isEndedJob() && isTooOld(response)) {
            LOGGER.info("The job is very old and is not ended. Trying to take it.");
            return doJobLocking(parameters);
        } else {
            LOGGER.info("The job {} was already locked.", parameters.getDescriptionParameters());
            return false;
        }
    }
    
    /**
     *
     * @return true if the locker is looking currently a job.
     */
    public boolean isLockingJob() {
        return lockingJob;
    }

    /**
     * 
     * @return The current parameters who are locked
     */
    public JOBTYPE getCurParameters() {
        if (isLockingJob()) {
            return currentParams;
        } else {
            throw new JobLockerError("There is not a blocked parameter");
        }
    }

    /**
     * Releases a job and writes it as done
     */
    public void releaseJob() {
        this.lockingJob = false;
        dao.writeJobDone(this.currentParams.getDescriptionParameters());
    }
    
    /**
     * Delete the currently locked job
     */
    public void deleteJob() {
        this.lockingJob = false;
        dao.deleteEntry(this.currentParams.getDescriptionParameters());
    }

    public void endResearch() {
        dao.endResearch();
    }
    
    /**
     * 
     * @param job The job to verify if is ended or not
     * @return True if the job passed in argument is marqued as ended or not in 
     * the database. 
     */
    public boolean isEndedJob(JOBTYPE job) {
        DAOResponse response = dao.readLockEntry(job.getDescriptionParameters());
        if (!response.existEntry()) {
            // the lock does not exist, so is not started, so is not ended
            return false;
        }
        return response.isEndedJob();
    }

    private boolean isTooOld(DAOResponse response) {
        long lockDate = response.getDate();
        Date date = Calendar.getInstance(TimeZone.getDefault()).getTime();
        long curDate = date.getTime();
        if (curDate < lockDate) {
            LOGGER.warn("The date in the lock is older than the current system date. Proceding anyways");
        }
        return curDate - lockDate > this.tooOldLock;
    }

    private boolean doJobLocking(JOBTYPE parameters) {
        DAOResponse response;
        LOGGER.debug("Locking the job {}", parameters.getDescriptionParameters());
        dao.writeEntry(parameters.getDescriptionParameters(), associatedID);
        try {
            /*We wait a litle*/
            Thread.sleep(waitTime);
        } catch (InterruptedException ex) {
            LOGGER.warn("We did not sleep enought time after writh the lock");
        }
        /*We verify if we have the job*/
        response = dao.readLockEntry(parameters.getDescriptionParameters());
        /*After waiting a random time, we verify if we really had the job*/
        if (associatedID.equals(response.getWrittenID())) {
            lockingJob = true;
            currentParams = parameters;
            LOGGER.info("The job {} has been correctly locked", parameters.getDescriptionParameters());
            return true;
        } else {
            LOGGER.info("We could not lock the job {}. It was locked by {} before.", parameters.getDescriptionParameters(), response.getWrittenID());
            return false;
        }
    }
}
