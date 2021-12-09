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

import com.android.tools.lint.detector.api.Category;
import com.android.tools.lint.detector.api.Detector;
import com.android.tools.lint.detector.api.Implementation;
import com.android.tools.lint.detector.api.Issue;
import com.android.tools.lint.detector.api.JavaContext;
import com.android.tools.lint.detector.api.Scope;
import com.android.tools.lint.detector.api.Severity;
import com.android.tools.lint.detector.api.TypeEvaluator;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiType;

import org.jetbrains.uast.UCallExpression;
import org.jetbrains.uast.UExpression;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * This code checks calls on SimpleDialogs methods for the issues listed below
 */
@SuppressWarnings("UnstableApiUsage")
public class DialogMethodCallLintDetector extends Detector implements Detector.UastScanner {

    private static String BUILD_CALL_MESSAGE = "%1$s does not implement a `build` method. " +
            "A **%2$s will be created** instead!\n" +
            "Implement the method in %1$s or call `%2$s.build()` instead.";
    public static final Issue BUILD_CALL = Issue.create("BuildNotImplemented",
            "Calling not implemented build",
            "This check checks for calls to static build methods on classes extending " +
                    "`SimpleDialog` that do not implement the build method itself.\n" +
                    "\n" +
                    "This will create an instance of the superclass instead of the intended dialog.\n",
            Category.CORRECTNESS, 6, Severity.ERROR,
            new Implementation(DialogMethodCallLintDetector.class, Scope.JAVA_FILE_SCOPE));



    @Override
    public List<String> getApplicableMethodNames() {
        return Collections.singletonList("build");
    }

    @Override
    public void visitMethod(JavaContext context, UCallExpression node, PsiMethod method) {

        if (context.getEvaluator().isMemberInSubClassOf(method,
                "eltos.simpledialogfragment.SimpleDialog", false)) {

            PsiClass definingClass = method.getContainingClass();
            UExpression callingExpression = node.getReceiver();

            if (definingClass != null && callingExpression != null) {

                PsiType type = TypeEvaluator.evaluate(callingExpression);
                if (type instanceof PsiClassType) {
                    // when called on instance of a class
                    PsiClass callingClass = ((PsiClassType) type).resolve();

                    if (!definingClass.equals(callingClass)) {

                        context.report(BUILD_CALL, context.getLocation(node), String.format(
                                BUILD_CALL_MESSAGE, callingClass.getName(), definingClass.getName()));
                    }

                } else {
                    // when called as static reference
                    if (!callingExpression.toString().equals(definingClass.getName())) {
                        context.report(BUILD_CALL, context.getLocation(node), String.format(
                                BUILD_CALL_MESSAGE, callingExpression, definingClass.getName()));
                    }
                }

            }



        }

    }
}
