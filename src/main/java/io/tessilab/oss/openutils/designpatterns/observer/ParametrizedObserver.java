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
package io.tessilab.oss.openutils.designpatterns.observer;

/**
 * The interface to represent the Observer of the Observer design pattern.In
 * this version the observer recives the data to be update from the subject.
 *
 * @author Andres BEL ALONSO
 * @param <T> the data send by the observer.
 */
public interface ParametrizedObserver<T> {

    /**
     * @author Andres BEL ALONSO
     *
     *
     * /**
     * This method is called by the subject to utpdate the state of the observer
     * @param obj
     */
    public void updateObserver(T obj);
}
