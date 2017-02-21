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

import io.tessilab.oss.openutils.FileUtils;
import io.tessilab.oss.openutils.elasticsearch.ElasticSearchHelperError;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.junit.Test;


import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Writes on a file in DAO_ADRESS/job_description a file containing : id/done?
 *
 * @author Andres BEL ALONSO
 */
public class TestJobLocker {

    class LocalDAO implements DAO {

        public final String daoAddress;

        private static final String DUMMY_NAME = "dummy.txt";

        public LocalDAO() {
            String ressourcePath = TestJobLocker.class.getClassLoader().getResource(DUMMY_NAME).getPath();
            ressourcePath = ressourcePath.substring(0, ressourcePath.length() - DUMMY_NAME.length());
            daoAddress = ressourcePath + "locker/";
        }

        @Override
        public DAOResponse readLockEntry(String descriptionParameters) {
            File file = new File(daoAddress + descriptionParameters);
            if (file.exists()) {
                try {
                    String filePath = daoAddress + descriptionParameters;
                    String fileContent = FileUtils.fileToString(filePath);
                    String owner = fileContent.split("/")[0];
                    long date;
                    filePath += "_date";
                    date = Long.parseLong(FileUtils.fileToString(filePath));
                    DAOResponse response = new DAOResponse(true, owner, false, date);
                    return response;
                } catch (IOException ex) {
                    throw new ElasticSearchHelperError(ex);
                }
            } else {
                return new DAOResponse();
            }
        }

        @Override
        public void writeEntry(String description, String iD) {
            try {
                String filePath = daoAddress + description;
                File file = new File(filePath);
                file.createNewFile();
                FileOutputStream outStream = new FileOutputStream(file);
                outStream.write(iD.getBytes());
                outStream.write("/".getBytes());
                outStream.close();
                // now we write the date
                file = new File(filePath + "_date");
                file.createNewFile();
                outStream = new FileOutputStream(file);
                Date date = Calendar.getInstance(TimeZone.getDefault()).getTime();
                outStream.write(Long.toString(date.getTime()).getBytes());
                outStream.close();
            } catch (FileNotFoundException ex) {
                throw new ElasticSearchHelperError(ex);
            } catch (IOException ex) {
                throw new ElasticSearchHelperError(ex);
            }

        }

        @Override
        public void writeJobDone(String description) {
            try {
                @SuppressWarnings("resource")
                FileOutputStream outStream = new FileOutputStream(daoAddress + description);
                outStream.write("done".getBytes());
            } catch (FileNotFoundException ex) {
                throw new ElasticSearchHelperError();
            } catch (IOException ex) {
                throw new ElasticSearchHelperError();
            }

        }

        @Override
        public void deleteEntry(String description) {
            String filePath = daoAddress + description;
            File file = new File(filePath);
            if (file.exists())
                file.delete();
            file = new File(filePath + "_date");
            if (file.exists())
                file.delete();
        }

        @Override
        public void endResearch() {
            // Nothing to do
        }
    }

    private class TestThread extends Thread {

        private String threadName;

        private Thread t = null;

        private boolean ownship = false;

        private boolean jobEnd = false;

        private boolean writeLockedJobs = false;

        private long tooOldLock = 10000;

        public TestThread(String name) {
            this.threadName = name;
        }

        public TestThread(String name, boolean writeOldJobs, long tooOldLock) {
            this.threadName = name;
            this.writeLockedJobs = writeOldJobs;
            this.tooOldLock = tooOldLock;
        }

        private LocalDAO dao;

        @Override
        public void run() {
            StringJobParameter params = new StringJobParameter("param1");
            dao = new LocalDAO();
            JobLocker jobLocker = new JobLocker(dao, threadName, writeLockedJobs, tooOldLock);
            ownship = jobLocker.lockJob(params);
            System.out.println(threadName + " : Job Done");
            jobEnd = true;
        }

        @Override
        public void start() {
            System.out.println("Starting " + threadName);
            if (t == null) {
                t = new Thread(this, threadName);
                t.start();
            }
        }

    }

    private void cleanFiles(StringJobParameter params, LocalDAO dao) {
        File file = new File(dao.daoAddress + params.getDescriptionParameters());
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    public void testLockJob() {
        StringJobParameter params = new StringJobParameter("param1");
        LocalDAO dao = new LocalDAO();
        JobLocker locker = new JobLocker(dao, "test", false, 100000);
        cleanFiles(params, dao);
        Boolean res = locker.lockJob(params);
        assertEquals(true, res);
        assertEquals(true, locker.isLockingJob());
        assertEquals(params, locker.getCurParameters());
    }

    @Test(expected = JobLockerError.class)
    public void testUnlockedLoad() {
        JobLocker locker = new JobLocker(new LocalDAO(), "test2", false, 100000);
        locker.getCurParameters();
    }

    @Test
    public void testMultiThreadLock() throws InterruptedException, IOException {
        System.out.println("*************** Begin multithread lock");
        LocalDAO dao = new LocalDAO();
        TestThread thread1 = new TestThread("thread1");
        TestThread thread2 = new TestThread("thread2");
        TestThread thread3 = new TestThread("thread3");
        File file = new File(dao.daoAddress + "param1");
        if (file.exists()) {
            file.delete();
        }
        thread1.start();
        Thread.sleep(1000);
        thread2.start();
        Thread.sleep(2000);
        thread3.start();
        Thread.sleep(1000);
        while (!thread1.jobEnd || !thread2.jobEnd || !thread3.jobEnd) {
            Thread.sleep(500);
        }
        System.out.println("*************** End multithread lock");
        String owner = FileUtils.fileToString(thread1.dao.daoAddress + "param1").split("/")[0];
        if (!thread1.ownship && !thread2.ownship && !thread3.ownship) {
            fail("Famine! Any thread has the job");
        }
        if (thread1.ownship) {
            assertEquals("thread1", owner);
        }
        if (thread2.ownship) {
            assertEquals("thread2", owner);
        }
        if (thread3.ownship) {
            assertEquals("thread3", owner);
        }
    }

    @Test
    public void testOldLock1() throws InterruptedException, IOException {
        System.out.println("*************** Begin test old lock multithread lock 1");
        TestThread thread1 = new TestThread("thread1", true, 1000);
        TestThread thread2 = new TestThread("thread2", true, 1000);
        LocalDAO dao = new LocalDAO();
        File file = new File(dao.daoAddress + "param1");
        if (file.exists()) {
            file.delete();
        }
        thread1.start();
        while (!thread1.jobEnd) {
            Thread.sleep(500);
        }
        Thread.sleep(2000);
        thread2.start();
        while (!thread2.jobEnd) {
            Thread.sleep(500);
        }
        String owner = FileUtils.fileToString(thread1.dao.daoAddress + "param1").split("/")[0];
        assertEquals("thread2", owner);
        System.out.println("*************** End test old lock 1");
    }

    @Test
    public void testOldLock2() throws IOException, InterruptedException {
        System.out.println("*************** Begin test old lock multithread lock 2");
        TestThread thread1 = new TestThread("thread1", true, 100000);
        TestThread thread2 = new TestThread("thread2", true, 100000);
        LocalDAO dao = new LocalDAO();
        File file = new File(dao.daoAddress + "param1");
        if (file.exists()) {
            file.delete();
        }
        thread1.start();
        while (!thread1.jobEnd) {
            Thread.sleep(500);
        }
        Thread.sleep(500);
        thread2.start();
        while (!thread2.jobEnd) {
            Thread.sleep(500);
        }
        String owner = FileUtils.fileToString(thread1.dao.daoAddress + "param1").split("/")[0];
        assertEquals("thread1", owner);
        System.out.println("*************** End test old lock 2");
    }

    @Test
    public void testDeleteLock() {
        LocalDAO dao = new LocalDAO();
        dao.deleteEntry("test");
        
        
        DAOResponse daoResponse = dao.readLockEntry("test");
        assertEquals(false, daoResponse.existEntry());
        dao.writeEntry("test", "id");
        daoResponse = dao.readLockEntry("test");
        assertEquals(true, daoResponse.existEntry());
        dao.deleteEntry("test");
        daoResponse = dao.readLockEntry("test");
        assertEquals(false, daoResponse.existEntry());
    }

}
