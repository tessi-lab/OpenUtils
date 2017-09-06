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
package io.tessilab.oss.openutils.bbox;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

public class BBoxableGroup<T extends BBoxable> implements BBoxable {

    private List<T> elements;
    private Rectangle2D bounds;

    public BBoxableGroup() {
        this(new ArrayList<T>());
    }

    public BBoxableGroup(List<T> elements) {
        this.elements = elements;
        updateBounds();
    }

    public List<T> getElements() {
        return elements;
    }

    public void addElement(T w) {
        this.elements.add(w);
        updateBounds();
    }

    private void updateBounds() {
        this.bounds = BBoxableUtils.bBoxUnion(elements);
    }

    @Override
    public Rectangle2D getBBox() {
        return bounds;
    }

}
