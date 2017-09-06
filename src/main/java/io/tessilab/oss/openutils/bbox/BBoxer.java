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

import org.json.JSONObject;

/*
 * The whole purpose of this class is to create a dummy object containing only a bbox, and thus implementing the interface BBoxable.
 */
public class BBoxer implements BBoxable {
    
    public static final String X = "x";
    public static final String Y = "y";
    public static final String WIDTH = "width";
    public static final String HEIGHT = "height";
    public static final String UNIT = "unit";

    private Rectangle2D bbox;

    public BBoxer(BBoxable bboxable) {
        this.bbox = new Rectangle2D.Double();
        this.bbox.setRect(bboxable.getBBox());
    }

    public BBoxer(Rectangle2D bbox) {
        this.bbox = new Rectangle2D.Double();
        this.bbox.setRect(bbox);
    }

    public BBoxer(double x, double y, double width, double height) {
        this.bbox = new Rectangle2D.Double(x, y, width, height);
    }

    // json {x,y,width,height}
    public BBoxer(JSONObject rectJson) {
        double x = rectJson.getDouble(X);
        double y = rectJson.getDouble(Y);
        double width = rectJson.getDouble(WIDTH);
        double height = rectJson.getDouble(HEIGHT);
        this.bbox = new Rectangle2D.Double(x, y, width, height);
    }

    @Override
    public Rectangle2D getBBox() {
        return bbox;
    }

}
