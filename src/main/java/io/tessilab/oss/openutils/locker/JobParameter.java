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
 * The interface that a parameter locked by {@link io.tessilab.oss.openutils.locker.JobLocker}
 * must implement. 
 * @author Andres BEL ALONSO
 */
public interface JobParameter {
    
    /**
     * 
     * @return The unique String for each different parameters of the problem.
     */
    public String getDescriptionParameters();
}
