/*
 * Copyright 2017 Andres Bel Alonso.
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
package io.tessilab.oss.openutils.designpatterns.processblock;

/**
 * An interface of a class representing a configurable object that process the entry E and transforms it to produce
 * an output O.
 * @author Andres Bel Alonso
 * @param <E> The type of entry of this block
 * @param <O> The type of output of this block
 */
public interface ConfigurableBlock<I,O> {
    
    public O process(I input);
    
}
