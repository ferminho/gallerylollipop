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

import com.unifstudios.gallerylollipop.R;

import android.graphics.Bitmap;

public class ImageFilterExposure extends SimpleImageFilter {
    private static final String SERIALIZATION_NAME = "EXPOSURE";
    public ImageFilterExposure() {
        mName = "Exposure";
    }

    public FilterRepresentation getDefaultRepresentation() {
        FilterBasicRepresentation representation =
                (FilterBasicRepresentation) super.getDefaultRepresentation();
        representation.setName("Exposure");
        representation.setSerializationName(SERIALIZATION_NAME);
        representation.setFilterClass(ImageFilterExposure.class);
        representation.setTextId(R.string.exposure);
        representation.setMinimum(-100);
        representation.setMaximum(100);
        representation.setDefaultValue(0);
        representation.setSupportsPartialRendering(true);
        return representation;
    }

    native protected void nativeApplyFilter(Bitmap bitmap, int w, int h, float bright);

    @Override
    public Bitmap apply(Bitmap bitmap, float scaleFactor, int quality) {
        if (getParameters() == null) {
            return bitmap;
        }
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        float value = getParameters().getValue();
        nativeApplyFilter(bitmap, w, h, value);
        return bitmap;
    }
}
