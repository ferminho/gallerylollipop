/*
 * Copyright (C) 2013 The Android Open Source Project
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

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.panorama.Panorama;
import com.google.android.gms.panorama.PanoramaApi;

public class PanoramaViewHelper implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final String TAG = "Gallery Lollipop";
    private Activity mActivity = null;
    private GoogleApiClient mClient;
    private boolean connected = false;

    public PanoramaViewHelper(Activity activity) {
        mActivity = activity;
        mClient = new GoogleApiClient.Builder(activity, this, this)
                .addApi(Panorama.API)
                .build();
    }

    public void onStart() {
        mClient.connect();
    }

    public void onCreate() {
        /* Do nothing */
    }

    public void onStop() {
        mClient.disconnect();
    }

    public void showPanorama(Uri uri) {
        Log.e(TAG, "Loading panorama " + uri.getPath());
        Panorama.PanoramaApi.loadPanoramaInfo(mClient, uri).setResultCallback(
                new ResultCallback<PanoramaApi.PanoramaResult>() {
                    @Override
                    public void onResult(PanoramaApi.PanoramaResult result) {
                        Log.e(TAG, "Result is " + result.getStatus().getStatusMessage());
                        if (result.getStatus().isSuccess()) {
                            Intent viewerIntent = result.getViewerIntent();
                            if (viewerIntent != null) {
                                mActivity.startActivity(viewerIntent);
                            }
                        } else {
                            Log.e(TAG, "error loading panorama: " + result);
                        }
                    }
                });
    }

    @Override
    public void onConnected(Bundle bundle) {
        connected = true;
    }

    @Override
    public void onConnectionSuspended(int i) {
        connected = false;
        Log.i(TAG, "Panorama connection suspended due to " +
                (i == GoogleApiClient.ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED ?
                        "service disconnected" : "network lost"));
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        connected = false;
        Log.e(TAG, "Cannot connect to panorama service (" + connectionResult.getErrorCode() + ")");
    }
}
