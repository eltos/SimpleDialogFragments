/*
 *  Copyright 2018 Philipp Niedermayer (github.com/eltos)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package eltos.simpledialogfragment;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.lang.ref.WeakReference;

/**
 * A dialog that displays an image
 *
 * Created by eltos on 13.02.17.
 */
public class SimpleImageDialog extends CustomViewDialog<SimpleImageDialog> {


    public static final String TAG = "SimpleImageDialog.";


    public static SimpleImageDialog build(){
        return new SimpleImageDialog();
    }


    private interface Creator<T> {
        /**
         * Create and return your image here.
         * NOTE: make sure your class is public and has a public default constructor!
         *       Also, nested classes should be static.
         *
         * @param tag The dialog-fragments tag
         * @param extras The extras supplied to {@link SimpleImageDialog#extra(Bundle)}
         * @return the image to be shown
         */
        T create(@Nullable String tag, @NonNull Bundle extras);
    }
    public interface BitmapCreator extends Creator<Bitmap>{}
    public interface DrawableCreator extends Creator<Drawable>{}
    public interface IconCreator extends Creator<Icon>{}


    /**
     * Sets the image drawable to be displayed
     *
     * @param resourceId the android resource id of the drawable
     */
    public SimpleImageDialog image(@DrawableRes int resourceId){
        return setArg(DRAWABLE_RESOURCE, resourceId);
    }

    /**
     * Sets the bitmap to be displayed
     *
     * Deprecated: To avoid {@link android.os.TransactionTooLargeException}, use any of the
     * following (save the bitmap to the app's private storage if needed)
     * – {@link SimpleImageDialog#image(int)}
     * – {@link SimpleImageDialog#image(Uri)}
     * – {@link SimpleImageDialog#image(Class)}
     *
     * @param image the bitmap to display
     */
    @Deprecated
    public SimpleImageDialog image(Bitmap image){
        getArguments().putParcelable(BITMAP, image);
        return this;
    }

    /**
     * Sets a Creator that can be used to create the image.
     *
     * @param builderClass A class implementing one of {@link SimpleImageDialog.BitmapCreator},
     *                     {@link SimpleImageDialog.DrawableCreator} or
     *                     {@link SimpleImageDialog.IconCreator}
     */
    public SimpleImageDialog image(Class<? extends Creator> builderClass){
        getArguments().putSerializable(CREATOR_CLASS, builderClass);
        return this;
    }

    /**
     * Sets the image uri to be displayed
     *
     * @param imageUri Uri of the image
     */
    public SimpleImageDialog image(Uri imageUri){
        getArguments().putParcelable(IMAGE_URI, imageUri);
        return this;
    }

    public enum Scale {
        /**
         * scales the image down ensuring the image is fully visible
         */
        FIT  (3),

        /**
         * scales the image up, allowing horizontal scrolling ("panorama")
         */
        SCROLL_HORIZONTAL (10),

        /**
         * scales the image up, allowing vertical scrolling
         */
        SCROLL_VERTICAL (11);


        Scale(int ni) {
            nativeInt = ni;
        }
        final int nativeInt;
    }

    /**
     * Sets the images scale and scroll type to one of {@link Scale}
     *
     * @param scale the scale type used for scaling
     */
    public SimpleImageDialog scaleType(Scale scale){
        return setArg(SCALE_TYPE, scale.nativeInt);
    }



    protected static final String
            DRAWABLE_RESOURCE = TAG + "drawableRes",
            BITMAP = TAG + "bitmap",
            IMAGE_URI = TAG + "uri",
            SCALE_TYPE = TAG + "scale";
    private static final String CREATOR_CLASS = TAG + "creatorClass";

    private boolean customTheme = false;



    public SimpleImageDialog(){
        pos(null); // no default button
    }




    @Override
    public SimpleImageDialog theme(@StyleRes int theme) {
        customTheme = true;
        return super.theme(theme);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        if (!customTheme){
            // theme specified by the imageDialogTheme attribute or default
            TypedValue outValue = new TypedValue();
            getContext().getTheme().resolveAttribute(R.attr.imageDialogTheme, outValue, true);
            if (outValue.type == TypedValue.TYPE_REFERENCE) {
                theme(outValue.resourceId);
            } else {
                theme(R.style.ImageDialogTheme);
            }
        }

        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    protected View onCreateContentView(Bundle savedInstanceState) {

        View view;

        int scale = getArguments().getInt(SCALE_TYPE, Scale.FIT.nativeInt);

        if (scale == Scale.FIT.nativeInt){
            view = inflate(R.layout.simpledialogfragment_image);

        } else if (scale == Scale.SCROLL_VERTICAL.nativeInt){
            view = inflate(R.layout.simpledialogfragment_image_vert_scroll);

        } else if (scale == Scale.SCROLL_HORIZONTAL.nativeInt) {
            view = inflate(R.layout.simpledialogfragment_image_hor_scroll);

        } else {
            return null;
        }


        ImageView imageView = (ImageView) view.findViewById(R.id.image);
        ProgressBar loading = (ProgressBar) view.findViewById(R.id.progressBar);

        if (getArguments().containsKey(IMAGE_URI)) {
            imageView.setImageURI((Uri) getArguments().getParcelable(IMAGE_URI));
        } else if (getArguments().containsKey(DRAWABLE_RESOURCE)) {
            imageView.setImageResource(getArguments().getInt(DRAWABLE_RESOURCE));
        } else if (getArguments().containsKey(CREATOR_CLASS)) {
            Bundle args = getArguments();
            args.putString(TAG, getTag());
            new ImageCreator(imageView, loading).execute(args);
        } else if (getArguments().containsKey(BITMAP)) {
            imageView.setImageBitmap((Bitmap) getArguments().getParcelable(BITMAP));
        }

        return view;
    }

    private static class ImageCreator extends AsyncTask<Bundle, Void, Object> {
        WeakReference<ImageView> mView;
        WeakReference<View> mLoading;

        ImageCreator(ImageView view, View loading){
            mView = new WeakReference<>(view);
            mLoading = new WeakReference<>(loading);
        }

        @Override
        protected void onPreExecute() {
            mView.get().setVisibility(View.GONE);
            mLoading.get().setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected Object doInBackground(Bundle... args) {
            if (args.length == 0 || args[0] == null) return null;

            @SuppressWarnings("unchecked")
            Class<Creator> c = (Class<Creator>) args[0].getSerializable(CREATOR_CLASS);
            if (c != null) {
                try {
                    Bundle extras = args[0].getBundle(BUNDLE);
                    if (extras == null) extras = new Bundle();
                    String tag = args[0].getString(TAG);

                    Creator builder = c.getConstructor().newInstance();
                    return builder.create(tag, extras);

                } catch (Exception e) {
                    Log.e(TAG, "Error: Instantiation of "+c.getName()+" failed. " +
                            "Make sure the class is public and has a public default constructor. " +
                            "Also, nested classes should be static", e);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object image) {
            if (image != null) {
                if (image instanceof Bitmap){
                    mView.get().setImageBitmap((Bitmap) image);
                } else if (image instanceof Drawable){
                    mView.get().setImageDrawable((Drawable) image);
                } else if (image instanceof Icon && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    mView.get().setImageIcon((Icon) image);
                }
            }
            mView.get().setVisibility(View.VISIBLE);
            mLoading.get().setVisibility(View.GONE);
            super.onPostExecute(image);
        }
    }

}
