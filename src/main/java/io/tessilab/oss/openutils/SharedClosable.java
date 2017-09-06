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
package io.tessilab.oss.openutils;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A class that allows to many threads to share an object and close it if no one
 * is using it
 *
 * @author Andres BEL ALONSO
 * @param <T>
 */
public class SharedClosable<T extends AutoCloseable> {

    public static class SharedClosableRuntimeException extends RuntimeException {

        private static final long serialVersionUID = 1L;

        public SharedClosableRuntimeException(String message) {
            super(message);
        }

        public SharedClosableRuntimeException(Throwable cause) {
            super(cause);
        }

    }

    private static final Logger LOGGER = LogManager.getLogger(SharedClosable.class);

    private static final ScheduledExecutorService threadPool = Executors.newScheduledThreadPool(1, new ThreadFactory() {

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = Executors.defaultThreadFactory().newThread(r);
            thread.setDaemon(true);
            return thread;
        }
    });

    private int nbUsers = 0;
    private Supplier<T> supplier;
    private T object;

    /**
     *
     * @param sup
     *            The supplier to get the object
     * @param time
     *            The amount of time to verify if we most close the object
     * @param timeUnit
     *            The unit of time
     */
    public SharedClosable(Supplier<T> sup, long time, TimeUnit timeUnit) {
        this.supplier = sup;
        threadPool.schedule(() -> {
            synchronized (this) {
                if (nbUsers == 0 && object != null) {
                    try {
                        LOGGER.debug("Closing " + object.toString());
                        object.close();
                        object = null;
                    } catch (Exception ex) {
                        throw new SharedClosableRuntimeException(ex);
                    }
                }
            }
        }, time, timeUnit);
    }

    /**
     * Note that each minute, this class verifies if the object can be close
     *
     * @param sup
     *            The supplier to get the object
     */
    public SharedClosable(Supplier<T> sup) {
        this(sup, 1, TimeUnit.MINUTES);
    }

    /**
     *
     * @return The specify object
     */
    public synchronized T get() {
        if (nbUsers == 0 && object == null) {
            object = supplier.get();
        }
        nbUsers++;
        return object;
    }

    /**
     * Inidicates that this thread has stoped to use the SharedClosable
     *
     * @throws IOException
     */
    public synchronized void release() throws SharedClosableRuntimeException {
        if (nbUsers == 0) {
            throw new SharedClosableRuntimeException("The object was already used");
        }
        nbUsers--;
        // if(nbUsers ==0) {
        // object.close();
        // }
    }
}
