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
package io.tessilab.oss.openutils.hocr;

public class HTMLConstants {

    /* ******* TAGS ******* */
    public static final String TAG_HEAD = "head";
    public static final String TAG_BODY = "body";
    public static final String TAG_TITLE = "title";
    public static final String TAG_META = "meta";
    public static final String TAG_DIV = "div";
    public static final String TAG_SPAN = "span";
    public static final String TAG_P = "p";

    /* ******* ATTRIBUTES ******* */
    public static final String CONFIDENCE = "x_wconf";
    public static final String TITLE = "title";
    public static final String CLASS = "class";
    public static final String ID = "id";

    /* ******* TITLE VALUE ******* */
    public static final String BBOX = "bbox";
    public static final String EMPTY_BBOX = BBOX + " 0 0 0 0";
    public static final String PAGE_TITLE_START = "image \"\"; ";
    public static final String PAGE_TITLE_END = "; ppageno ";

    /* ******* CLASSES ******* */
    public static final String WORD_CLASS = "ocrx_word";
    public static final String LINE_CLASS = "ocr_line";
    public static final String PAR_CLASS = "ocr_par";
    public static final String AREA_CLASS = "ocr_carea";
    public static final String PAGE_CLASS = "ocr_page";
    public static final String META_CLASS = "meta";
    public static final String FRAME_CLASS = "frame";
    public static final String TABLE_CLASS = "table";
    public static final String BARCODE_CLASS = "barcode";
    public static final String OTHER_CLASS = "other";

    public static final String TRANSFORMATION_MATRIX = "transformation_matrix";

    /* ******* CLASSES VALUES PREFIX ******* */
    public static final String WORD = "word_";
    public static final String LINE = "line_";
    public static final String PAR = "par_";
    public static final String AREA = "block_";
    public static final String PAGE = "page_";

    // page attributes
    public static final String CHAR_WIDTH = "charwidth";
    public static final String AFFINE_TRANSFORM = "affine_transform";
    public static final String DPI = "dpi";

    private HTMLConstants() {
        // private dummy constructor
    }
}
