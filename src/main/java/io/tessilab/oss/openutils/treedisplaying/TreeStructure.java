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
package io.tessilab.oss.openutils.treedisplaying;

import java.util.List;

/**
 * An interface to represent a relation between several classes as a tree relation.
 * @author Andres BEL ALONSO
 */
public interface TreeStructure {
    
    public String getNodeName();
    
    public List<TreeStructure> getChilds();
   
    public boolean isLeaf();
}
