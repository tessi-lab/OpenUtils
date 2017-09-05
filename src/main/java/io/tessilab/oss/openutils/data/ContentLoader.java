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

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javaruntype.type.Type;

/**
 * Defines the interface of an object that is able to load some content from an identifier.
 *
 * @author david
 * @param <LOADED_TYPE>
 * @param <IDENTIFIER_TYPE>
 */
public abstract class ContentLoader<LOADED_TYPE, IDENTIFIER_TYPE> {

    private static final Logger LOGGER = LogManager.getLogger(ContentLoader.class);

    private final Map<IDENTIFIER_TYPE, LOADED_TYPE> cachedContent;
    private final boolean cacheContent;
    private final Type<IDENTIFIER_TYPE> identifierType;
    private final Type<LOADED_TYPE> loadedType;

    public ContentLoader(boolean cacheContent,Type<LOADED_TYPE> loadedType,
            Type<IDENTIFIER_TYPE> identifierType) {
        super();
        this.cacheContent = cacheContent;
        if (this.cacheContent) {
            cachedContent = new HashMap<>();
        } else {
            cachedContent = null;
        }
        this.identifierType = identifierType;
        this.loadedType = loadedType;
    }

    public final LOADED_TYPE load(IDENTIFIER_TYPE identifier ) throws LoaderError, ConsistancyException {
        if (cacheContent) {
            synchronized (cachedContent) {
                if (cachedContent.containsKey(identifier)) {
                    LOGGER.trace("Cached content returned: " + identifier.toString());
                    return cachedContent.get(identifier);
                }
            }
        }
        LOADED_TYPE content = this.loadAfterCacheMiss(identifier);
        if (cacheContent) {
            synchronized (cachedContent) {
                cachedContent.put(identifier, content);
            }
        }
        return content;
    }

    public boolean usesCacheContent() {
        return cacheContent;
    }

    public Type<IDENTIFIER_TYPE> getIdentifierType() {
        return identifierType;
    }

    public Type<LOADED_TYPE> getLoadedType() {
        return loadedType;
    }

    

    protected abstract LOADED_TYPE loadAfterCacheMiss(IDENTIFIER_TYPE identifier) throws LoaderError, ConsistancyException;

}
