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

import java.util.Objects;
import org.javaruntype.type.Type;

/**
 * A single object representing two types. It is used by the content loader provider to index content loader providers
 * usinng the loaded type and the value type V.
 * @author Andres Bel Alonso
 * @param <LOADED_TYPE> The class of the first type
 * @param <IDENTIFIER_TYPE> The class of the second type
 */
public class TwoTypesIndex<LOADED_TYPE,IDENTIFIER_TYPE> {
    
    private final Type<LOADED_TYPE> LoadType;
    private final Type<IDENTIFIER_TYPE> ValueType;

    public TwoTypesIndex(Type<LOADED_TYPE> LoadType, Type<IDENTIFIER_TYPE> IdentifierType) {
        this.LoadType = LoadType;
        this.ValueType = IdentifierType;
    }

    public Type<LOADED_TYPE> getLoadType() {
        return LoadType;
    }

    public Type<IDENTIFIER_TYPE> getValueType() {
        return ValueType;
    }
    
    

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 17 * hash + Objects.hashCode(this.LoadType);
        hash = 17 * hash + Objects.hashCode(this.ValueType);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TwoTypesIndex<?, ?> other = (TwoTypesIndex<?, ?>) obj;
        if (!Objects.equals(this.LoadType, other.LoadType)) {
            return false;
        }
        if (!Objects.equals(this.ValueType, other.ValueType)) {
            return false;
        }
        return true;
    }


    
    
    
    
}
