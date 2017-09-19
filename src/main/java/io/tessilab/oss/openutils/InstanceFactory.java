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

import java.util.LinkedList;
import java.util.List;

/**
 *
 * Class that searches to provide multiple object instantiation when convenient, for example, in parallelizing 
 * scenarios. 
 * 
 * @author nicolas
 */
public abstract class InstanceFactory<T> {

    /**
     * List of available instances
     */
    private final List<T> listOfInstances;

    /**
     * Factory constructor
     */
    public InstanceFactory() {
        listOfInstances = new LinkedList<>();
    }
    
    /**
     * 
     * Provide one instance of the Generic Type T, if available in listOfInstances it tooks it from there,
     * otherwise a new instance is created.
     * 
     * @return instance of the Generic Type T
     */
    public synchronized T grant() {
        int listSize = listOfInstances.size();
        if(listSize > 0){
            return listOfInstances.remove(listSize - 1);
        }
        else return createInstance();
    }
    
    /**
     * 
     * When finishing with an instance, return it to the list of availables.S
     * 
     * @param usedInstance 
     */
    public synchronized void free(T usedInstance){
        listOfInstances.add(usedInstance);
    }
    
    /**
     * Abstract method to implement the instance creation depending on the application of this class. In the base this
     * method is not Synchronized but it depends on the implementation.
     * 
     * @return new instance of T
     */
    protected abstract T createInstance();
    
}
