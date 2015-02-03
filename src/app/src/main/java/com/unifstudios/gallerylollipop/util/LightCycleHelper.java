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

package com.unifstudios.gallerylollipop.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.xmp.XmpDirectory;
import com.unifstudios.photos.data.PhotoProvider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class LightCycleHelper {
    public static class PanoramaMetadata {
        // Whether a panorama viewer should be used
        public final boolean mUsePanoramaViewer;
        // Whether a panorama is 360 degrees
        public final boolean mIsPanorama360;

        public PanoramaMetadata(boolean usePanoramaViewer, boolean isPanorama360) {
            mUsePanoramaViewer = usePanoramaViewer;
            mIsPanorama360 = isPanorama360;
        }
    }

    public static final PanoramaMetadata NOT_PANORAMA = new PanoramaMetadata(false, false);

    private static final int PanoramaExifTag = 0x9207;
    private static final int PanoramaExifTagValue = 65535;

    public static PanoramaMetadata getPanoramaMetadata(Context context, Uri uri) {
        PanoramaMetadata panoMetadata = NOT_PANORAMA;
        try {
            Cursor cursor = context.getContentResolver().query(
                    uri, new String[]{ MediaStore.Images.Media.DATA }, null, null, null);
            int colIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(colIndex);
            cursor.close();

            Metadata metadata = ImageMetadataReader.readMetadata(new File(path));
            if (metadata.containsDirectory(ExifIFD0Directory.class)) {
                ExifIFD0Directory exifDir = metadata.getDirectory(ExifIFD0Directory.class);
                if (exifDir.containsTag(PanoramaExifTag)) {
                    int exifTagValue = exifDir.getInt(PanoramaExifTag);
                    boolean isPanorama = exifTagValue == PanoramaExifTagValue;
                    boolean is360 = metadata.containsDirectory(XmpDirectory.class);
                    panoMetadata = new PanoramaMetadata(is360, isPanorama);
                }
            }

        } catch (IOException e) {
        } catch (ImageProcessingException e) {
        } catch (MetadataException e) {
        }

        return panoMetadata;
    }

    /**
     * Get the file path from a Media storage URI.
     */
    public static String getPathFromURI(ContentResolver contentResolver, Uri contentUri) {
        return null;
    }

    /**
     * Get the modified time from a Media storage URI.
     */
    public static long getModifiedTimeFromURI(ContentResolver contentResolver, Uri contentUri) {
        return 0;
    }
}
