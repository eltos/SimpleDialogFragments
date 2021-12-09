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
package eltos.simpledialogfragments

import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import eltos.simpledialogfragment.SimpleDialog
import eltos.simpledialogfragment.SimpleDialog.OnDialogResultListener
import eltos.simpledialogfragment.SimpleProgressDialog
import eltos.simpledialogfragment.SimpleProgressTask

@Suppress("UNUSED_PARAMETER")
class KotlinActivity : AppCompatActivity(), OnDialogResultListener {

    fun showInfo(view: View?) {
        SimpleDialog.build()
                .title(R.string.message)
                .msg(R.string.hello_world)
                .show(this)
    }

    @Suppress("Unused")
    fun lintTest(){
        // The following produces a lint error
        //TestLintDialog.build().show(this)
    }


    fun showProgressTask(view: View?) {
        val task = MyProgressTask()
        task.execute()
        SimpleProgressDialog.buildBar()
                .title(R.string.login)
                .msg(R.string.creating_user_profile_wait)
                .task(task, true, false)
                .show(this, PROGRESS_DIALOG)
    }

    internal class MyProgressTask : SimpleProgressTask<Void?, Int?, Void?>() {
        override fun doInBackground(vararg params: Void?): Void? {
            SystemClock.sleep(500)
            var i = 0
            while (!isCancelled && i < 100) {
                publishProgress(i + 25, 150)
                SystemClock.sleep(10)
                i += 1
            }
            return null
        }
    }


    // ==   R E S U L T S   ==
    /**
     * Let the hosting fragment or activity implement this interface
     * to receive results from the dialog
     *
     * @param dialogTag the tag passed to [SimpleDialog.show]
     * @param which result type, one of [.BUTTON_POSITIVE], [.BUTTON_NEGATIVE],
     * [.BUTTON_NEUTRAL] or [.CANCELED]
     * @param extras the extras passed to [SimpleDialog.extra]
     * @return true if the result was handled, false otherwise
     */
    override fun onResult(dialogTag: String, which: Int, extras: Bundle): Boolean {
        Log.d("onResult", "Dialog with tag '" + dialogTag + "' has result: " + which + " (" +
                (if (which == OnDialogResultListener.BUTTON_POSITIVE) "BUTTON_POSITIVE" else if (which == OnDialogResultListener.BUTTON_NEUTRAL) "BUTTON_NEUTRAL" else if (which == OnDialogResultListener.BUTTON_NEGATIVE) "BUTTON_NEGATIVE" else if (which == OnDialogResultListener.CANCELED) "CANCELED" else "?") + ")")


        if (PROGRESS_DIALOG == dialogTag) {
            when (which) {
                SimpleProgressDialog.COMPLETED -> {
                    Toast.makeText(this, R.string.completed, Toast.LENGTH_SHORT).show()
                    return true
                }
            }
        }

        return false
    }

    companion object {
        private const val PROGRESS_DIALOG = "dialogProgress"
    }
}