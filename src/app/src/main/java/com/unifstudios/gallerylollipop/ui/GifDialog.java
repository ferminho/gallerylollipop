package com.unifstudios.gallerylollipop.ui;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.unifstudios.gallerylollipop.R;
import com.unifstudios.gallerylollipop.util.AnimatedGifDrawable;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class GifDialog extends DialogFragment implements AnimatedGifDrawable.UpdateListener {

    private Dialog mDialog;

    private String mGifPath;
    private ProgressBar mProgressBar;
    private ImageView mImageView;
    private AnimatedGifDrawable mDrawable;

    private boolean isPlaying = true;

    public GifDialog() {
    }

    public void setGifPath(String gifPath) {
        mGifPath = gifPath;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_GifDialog);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = createDialog();
        mLoadGifTask.execute();
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mDrawable != null) {
            mDrawable.stop();
        }
    }

    private View createDialog() {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.gif_view, null);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        mImageView = (ImageView) view.findViewById(R.id.imageViewGif);
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDrawable != null) {
                    if (isPlaying) {
                        mDrawable.stop();
                        isPlaying = false;
                    } else {
                        mDrawable.start();
                        isPlaying = true;
                    }
                }
            }
        });

        mDialog = getDialog();
        mDialog.setCancelable(true);
        mDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        return view;
    }

    private AsyncTask<Void, Void, Void> mLoadGifTask = new AsyncTask<Void, Void, Void>() {
        @Override
        protected Void doInBackground(Void... params) {
            if (mGifPath != null) {
                InputStream is = null;
                try {
                    is = new FileInputStream(mGifPath);
                    mDrawable = new AnimatedGifDrawable(is, GifDialog.this, 1);
                    mDrawable.setOneShot(false);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mProgressBar.setVisibility(View.INVISIBLE);
            if (mDrawable != null) {
                mImageView.setImageDrawable(mDrawable);
                mDrawable.start();
            } else {
                mImageView.setImageResource(android.R.drawable.stat_notify_error);
            }
        }
    };

    @Override
    public void update() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mImageView.invalidate();
                mImageView.invalidateDrawable(mDrawable);
            }
        });
    }
}
