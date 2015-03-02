/*
 * Copyright (C) 2012 The Android Open Source Project
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

package com.unifstudios.gallerylollipop.filtershow.filters;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;

import com.unifstudios.gallerylollipop.R;
import com.unifstudios.gallerylollipop.filtershow.editors.BasicEditor;


public class ImageFilterSharpenNoRs extends ImageFilter {
    private static final String SERIALIZATION_NAME = "SHARPEN";
    private static final String LOGTAG = "ImageFilterSharpenNoRs";
    private FilterBasicRepresentation mParameters;

    public ImageFilterSharpenNoRs() {
        mName = "Sharpen";
    }

    public FilterRepresentation getDefaultRepresentation() {
        FilterRepresentation representation = new FilterBasicRepresentation("Sharpen", 0, 0, 100);
        representation.setSerializationName(SERIALIZATION_NAME);
        representation.setShowParameterValue(true);
        representation.setFilterClass(ImageFilterSharpenNoRs.class);
        representation.setTextId(R.string.sharpness);
        representation.setOverlayId(R.drawable.filtershow_button_colors_sharpen);
        representation.setEditorId(BasicEditor.ID);
        representation.setSupportsPartialRendering(false);
        return representation;
    }

    public void useRepresentation(FilterRepresentation representation) {
        FilterBasicRepresentation parameters = (FilterBasicRepresentation) representation;
        mParameters = parameters;
        mParameters.setMinimum(0);
        mParameters.setMaximum(100);
    }

    private void applyHelper(Canvas canvas, int w, int h) {
    }

    @Override
    public Bitmap apply(Bitmap bitmap, float scaleFactor, int quality) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        int color;
        int colorTop, colorBottom, colorLeft, colorRight;
        int or, og, ob;
        int nr, ng, nb;
        int value = mParameters.getValue();
        if (value == 0) return bitmap;
        int[] pixels = new int[w * h];
        int[] destPixels = new int[w * h];
        bitmap.getPixels(pixels, 0, w, 0, 0, w, h);
        int offset;

        for (int y = 1; y < (h - 1); y++) {
            offset = y * w + 1;
            for (int x = 1; x < (w - 1); x++) {
                color = pixels[offset];
                or = (color >> 16) & 0xFF;
                og = (color >> 8) & 0xFF;
                ob = color & 0xFF;
                nr = ng = nb = 0;

                colorTop = pixels[offset - w];
                colorBottom = pixels[offset + w];
                colorLeft = pixels[offset - 1];
                colorRight = pixels[offset + 1];

                nr = or * 5 - ((colorTop >> 16) & 0xFF) - ((colorBottom >> 16) & 0xFF) -
                        ((colorLeft >> 16) & 0xFF) - ((colorRight >> 16) & 0xFF);
                ng = og * 5 - ((colorTop >> 8) & 0xFF) - ((colorBottom >> 8) & 0xFF) -
                        ((colorLeft >> 8) & 0xFF) - ((colorRight >> 8) & 0xFF);
                nb = ob * 5 - (colorTop & 0xFF) - (colorBottom & 0xFF) -
                        (colorLeft & 0xFF) - (colorRight & 0xFF);
                nr = or + (((nr - or) * value) / 100);
                ng = og + (((ng - og) * value) / 100);
                nb = ob + (((nb - ob) * value) / 100);
                if (nr < 0) nr = 0; if (nr > 255) nr = 255;
                if (ng < 0) ng = 0; if (ng > 255) ng = 255;
                if (nb < 0) nb = 0; if (nb > 255) nb = 255;

                destPixels[offset] = (255 << 24) | (nr << 16) | (ng << 8) | nb;
                offset++;
            }
        }

        bitmap.setPixels(destPixels, 0, w, 0, 0, w, h);
        return bitmap;
    }


}
