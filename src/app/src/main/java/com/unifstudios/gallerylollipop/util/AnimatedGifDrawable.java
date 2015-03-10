package com.unifstudios.gallerylollipop.util;

import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.io.InputStream;

public class AnimatedGifDrawable extends AnimationDrawable {

    private int mCurrentIndex = 0;
    private UpdateListener mListener;

    public AnimatedGifDrawable(InputStream source, UpdateListener listener, int scale) {
        mListener = listener;
        GifDecoder decoder = new GifDecoder();
        decoder.read(source);

        // Iterate through the gif frames, add each as animation frame
        for (int i = 0; i < decoder.getFrameCount(); i++) {
            Bitmap bitmap = decoder.getFrame(i);
            BitmapDrawable drawable = new BitmapDrawable(null, bitmap);
            // Explicitly set the bounds in order for the frames to display
            drawable.setBounds(0, 0, bitmap.getWidth() * scale, bitmap.getHeight() * scale);
            addFrame(drawable, decoder.getDelay(i));
            if (i == 0) {
                // Also set the bounds for this container drawable
                setBounds(0, 0, bitmap.getWidth() * scale, bitmap.getHeight() * scale);
            }
        }
    }

    public void setUpdateListener(UpdateListener listener) {
        mListener = listener;
    }

    /**
     * Naive method to proceed to next frame. Also notifies listener.
     */
    public void nextFrame() {
        mCurrentIndex = (mCurrentIndex + 1) % getNumberOfFrames();
        if (mListener != null)
            mListener.update();
    }

    /**
     * Return display duration for current frame
     */
    public int getFrameDuration() {
        return getDuration(mCurrentIndex);
    }

    /**
     * Return drawable for current frame
     */
    public Drawable getDrawable() {
        return getFrame(mCurrentIndex);
    }

    /**
     * Interface to notify listener to update/redraw Can't figure out how to
     * invalidate the drawable (or span in which it sits) itself to force redraw
     */
    public interface UpdateListener {
        void update();
    }
}
