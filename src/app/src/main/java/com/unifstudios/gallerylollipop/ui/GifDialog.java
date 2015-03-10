package com.unifstudios.gallerylollipop.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.unifstudios.gallerylollipop.R;
import com.unifstudios.gallerylollipop.util.AnimatedGifDrawable;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class GifDialog implements AnimatedGifDrawable.UpdateListener {

    private Activity mActivity;
    private AlertDialog mDialog;

    private String mGifPath;
    private ProgressBar mProgressBar;
    private ImageView mImageView;
    private AnimatedGifDrawable mDrawable;

    public GifDialog(Activity activity, String gifPath) {
        mActivity = activity;
        mGifPath = gifPath;
        createDialog();
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mDialog.show();
                mLoadGifTask.execute();
            }
        });
    }

    private void createDialog() {
        LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.gif_view, null);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        mImageView = (ImageView) view.findViewById(R.id.imageViewGif);

        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setView(view).setCancelable(true);
        mDialog = builder.create();
        mDialog.setCanceledOnTouchOutside(true);

        // make it full width
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = mDialog.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
    }

    private AsyncTask<Void, Void, Void> mLoadGifTask = new AsyncTask<Void, Void, Void>() {
        @Override
        protected Void doInBackground(Void... params) {
            InputStream is = null;
            try {
                is = new FileInputStream(mGifPath);
                mDrawable = new AnimatedGifDrawable(is, GifDialog.this, 1);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mProgressBar.setVisibility(View.INVISIBLE);
            if (mDrawable != null) {
                mImageView.setImageDrawable(mDrawable);
            } else {
                mImageView.setImageResource(android.R.drawable.stat_notify_error);
            }
        }
    };

    @Override
    public void update() {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mImageView.invalidate();
                mImageView.invalidateDrawable(mDrawable);
            }
        });
    }
}
