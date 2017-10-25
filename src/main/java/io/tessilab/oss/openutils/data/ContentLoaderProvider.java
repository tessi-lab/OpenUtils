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
package io.tessilab.oss.openutils.data;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.javaruntype.type.Type;

/**
 * A class that contains all the content loaders used your application. 
 * @author Andres Bel Alonso
 */
public class ContentLoaderProvider {
    
    private final Map<TwoTypesIndex<?,?>,ContentLoader<?,?>> contentLoaderMap;

    public ContentLoaderProvider() {
        contentLoaderMap = new HashMap<>();
    }
    
    /**
     * 
     * @param <LOADED_TYPE> The loaded type in the content loader
     * @param <IDENTIFIER_TYPE> The identifier type in the content loader
     * @param loadType The type to load
     * @param idType The identifier type. 
     * @return The wanted content loader or null if it doesn't exist
     */
    public<LOADED_TYPE,IDENTIFIER_TYPE> ContentLoader<LOADED_TYPE,IDENTIFIER_TYPE> getContentLoader(Type<LOADED_TYPE> 
            loadType,Type<IDENTIFIER_TYPE> idType) {
        return (ContentLoader<LOADED_TYPE,IDENTIFIER_TYPE>) contentLoaderMap.get(new TwoTypesIndex<>(loadType,idType));
    }
    
    /**
     * Adds a content loader ot the content loaders manage by this object. Note that if there is already a content 
     * loader for these types, the the new added will replace the old one. 
     * @param <LOADED_TYPE> The loaded type in the content loader
     * @param <IDENTIFIER_TYPE> The identifier type in the content loader
     * @param contentLoader The content loader to add. 
     */
    public<LOADED_TYPE,IDENTIFIER_TYPE> void addContentLoader(ContentLoader<LOADED_TYPE,IDENTIFIER_TYPE> contentLoader) {
        contentLoaderMap.put((new TwoTypesIndex<>(contentLoader.getLoadedType(),contentLoader.getIdentifierType())),
                contentLoader);
    }

    /**
     * Gets a stream of contentLoaders that verifies a criterium.
     * @param filter The filter to apply to the content loaders
     * @return The stream of content loaders contained in this provider that matches the filter
     */
    public Stream<ContentLoader<?,?>> getContentLoaderStream(Predicate< ? super ContentLoader<?,?>> filter) {
        return this.contentLoaderMap.values().stream().filter(filter);
    }
    
}
