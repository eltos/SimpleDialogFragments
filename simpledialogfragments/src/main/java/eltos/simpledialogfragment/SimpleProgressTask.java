package eltos.simpledialogfragment;

import android.os.AsyncTask;
import android.util.Pair;

import androidx.annotation.CallSuper;

/**
 * An {@link AsyncTask} for use with {@link SimpleProgressDialog}
 *
 * Automatically reflects the task's states in the dialog.
 *
 * Created by eltos on 27.05.21.
 */
public abstract class SimpleProgressTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {

    protected SimpleProgressDialog mDialog;

    protected void registerDialog(SimpleProgressDialog dialog){
        mDialog = dialog;
    }

    @Override
    @CallSuper
    protected void onPreExecute() {
        if (mDialog != null) {
            mDialog.updateIndeterminate();
        }
    }

    @Override
    @CallSuper
    protected void onPostExecute(Result result) {
        if (mDialog != null) {
            mDialog.updateFinished();
        }
    }

    /**
     * Updates the progress dialog by trying to guess the meaning of the supplied parameter(s):
     *
     * - if values is of numeric type
     *   - if values[0] &lt; 0, then progress is indeterminate
     *   - if values[0] &gt;= 0, then (int) values[0] is set as progress
     *   - if values[1] &gt; 0, then (int) values[1] is set as max, otherwise max defaults to 100
     *   - if values[2] &gt;= 0, then (int) values[2] is set as secondary progress
     * - if values is of CharSequence type, then values[0] is set as info text and progress to indeterminate
     * - if values is a {@link Pair} of a {@link Number} and a {@link String}, the above is applied to either value of the pair
     */
    @Override
    protected void onProgressUpdate(Progress... values) {
        if (mDialog != null && values.length > 0){
            int v0 = -1, v1 = -1, v2 = -1;
            String s0 = null;

            if (values[0] instanceof Number){
                v0 = (int) values[0];
                if (values.length > 1) v1 = (int) values[1];
                if (values.length > 2) v2 = (int) values[2];
            }
            if (values[0] instanceof String){
                s0 = (String) values[0];
                mDialog.updateIndeterminate();
            }
            if (values[0] instanceof Pair && ((Pair<?, ?>) values[0]).first instanceof Number && ((Pair<?, ?>) values[0]).second instanceof String){
                v0 = (int) ((Pair<?, ?>) values[0]).first;
                s0 = (String) ((Pair<?, ?>) values[0]).second;
                if (values.length > 1) v1 = (int) ((Pair<?, ?>) values[1]).first;
                if (values.length > 2) v2 = (int) ((Pair<?, ?>) values[2]).first;
            }

            if (v0 >= 0) mDialog.updateProgress(v0);
            if (v0 < 0) mDialog.updateIndeterminate();
            if (v1 > 0) mDialog.updateMax(v1);
            if (v2 >= 0) mDialog.updateSecondaryProgress(v2);
            mDialog.updateInfoText(s0);

        }
    }

}
