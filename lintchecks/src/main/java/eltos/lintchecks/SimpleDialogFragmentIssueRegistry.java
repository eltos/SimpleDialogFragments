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

package eltos.lintchecks;

import com.android.tools.lint.client.api.IssueRegistry;
import com.android.tools.lint.detector.api.Issue;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

/*
 * The list of issues that will be checked when running <code>lint</code>.
 */
@SuppressWarnings("UnstableApiUsage")
public class SimpleDialogFragmentIssueRegistry extends IssueRegistry {
    @NotNull
    @Override
    public List<Issue> getIssues() {
        return Arrays.asList(
                DialogExtendLintDetector.BUILD_OVERWRITE,
                DialogExtendLintDetector.TAG,
                DialogMethodCallLintDetector.BUILD_CALL);
    }
}
