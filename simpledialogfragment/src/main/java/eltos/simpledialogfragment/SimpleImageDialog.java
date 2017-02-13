/*
 *  Copyright 2017 Philipp Niedermayer (github.com/eltos)
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
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;

/**
 * A dialog that displays an image
 *
 * Created by eltos on 13.02.17.
 */

public class SimpleImageDialog extends CustomViewDialog<SimpleImageDialog> {


    private static final String TAG = SimpleImageDialog.class.getSimpleName();
    private static final String DRAWABLE_RESOURCE = TAG + "drawableRes";
    private static final String BITMAP = TAG + "bitmap";
    private static final String IMAGE_URI = TAG + "uri";
    private static final String SCALE_TYPE = TAG + "scale";
    private boolean customTheme = false;

    public SimpleImageDialog(){
        pos(null); // no default button
    }


    public static SimpleImageDialog build(){
        return new SimpleImageDialog();
    }


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
     * @param image the bitmap to display
     */
    public SimpleImageDialog image(Bitmap image){
        getArguments().putParcelable(BITMAP, image);
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
            view = inflate(R.layout.dialog_image);

        } else if (scale == Scale.SCROLL_VERTICAL.nativeInt){
            view = inflate(R.layout.dialog_image_vert_scroll);

        } else if (scale == Scale.SCROLL_HORIZONTAL.nativeInt) {
            view = inflate(R.layout.dialog_image_hor_scroll);

        } else {
            return null;
        }


        ImageView imageView = (ImageView) view.findViewById(R.id.image);
        if (getArguments().containsKey(IMAGE_URI)) {
            imageView.setImageURI((Uri) getArguments().getParcelable(IMAGE_URI));
        }
        if (getArguments().containsKey(DRAWABLE_RESOURCE)) {
            imageView.setImageResource(getArguments().getInt(DRAWABLE_RESOURCE));
        }
        if (getArguments().containsKey(BITMAP)) {
            imageView.setImageBitmap((Bitmap) getArguments().getParcelable(BITMAP));
        }

        return view;
    }

}
