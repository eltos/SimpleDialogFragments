/*
 *  Copyright 2021 Philipp Niedermayer (github.com/eltos)
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

package eltos.simpledialogfragments;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewStub;
import android.widget.Toast;

import eltos.simpledialogfragment.CustomViewDialog;

public class ClipboardCopyDialog extends CustomViewDialog<ClipboardCopyDialog> {

    public static ClipboardCopyDialog build(){
        return new ClipboardCopyDialog()
                .neut(R.string.copy);
    }

    @Override
    protected void onNeutralButtonClick() {
        ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip;
        if (getArgs().getBoolean(HTML)){
            // HTML dialog message
            String plainText = Html.fromHtml(getMessage()).toString();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                clip = ClipData.newHtmlText(getTitle(), plainText, getMessage());
            } else {
                clip = ClipData.newPlainText(getTitle(), plainText);
            }
        } else {
            // Plain text dialog message
            clip = ClipData.newPlainText(getTitle(), getMessage());
        }
        clipboard.setPrimaryClip(clip);
        Toast.makeText(getContext(), R.string.copied, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected View onCreateContentView(Bundle savedInstanceState) {
        return new ViewStub(getContext()); // no special content required
    }
}
