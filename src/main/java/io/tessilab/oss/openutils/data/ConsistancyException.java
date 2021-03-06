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
package io.tessilab.oss.openutils.data;

/**
 * Throw when a content loader has loaded the content, but this content is malformed and does not respect the 
 * constraints that must respect
 * @author david
 */
public class ConsistancyException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = -3423434797053706826L;

    public ConsistancyException() {
        super();
    }

    public ConsistancyException(String message) {
        super(message);
    }

    public ConsistancyException(Throwable cause) {
        super(cause);
    }
    
    

}
