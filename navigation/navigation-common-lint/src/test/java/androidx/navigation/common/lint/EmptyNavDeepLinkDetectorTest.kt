/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package androidx.navigation.common.lint

import com.android.tools.lint.checks.infrastructure.LintDetectorTest
import com.android.tools.lint.checks.infrastructure.TestFile
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Issue
import org.junit.Test

class EmptyNavDeepLinkDetectorTest : LintDetectorTest() {
    override fun getDetector(): Detector = EmptyNavDeepLinkDetector()

    override fun getIssues(): MutableList<Issue> =
        mutableListOf(EmptyNavDeepLinkDetector.EmptyNavDeepLink)

    @Test
    fun testNoErrors() {
        lint().files(
            kotlin(
                """
                package com.example

                import androidx.navigation.navDeepLink

                fun createDeepLink() {
                    navDeepLink { uriPattern = "" }
                    navDeepLink {
                        action = ""
                        mimeType = ""
                    }
                    navDeepLink<TestClass>("basePath")
                    navDeepLink {
                        uriPattern = ""
                        action = ""
                        mimeType = ""
                    }
                }
                """
            ).indented(),
            navDeepLinkStub
        ).run().expectClean()
    }

    @Test
    fun testErrors() {
        lint().files(
            kotlin(
                """
                package com.example

                import androidx.navigation.navDeepLink

                fun createDeepLink() {
                    navDeepLink { }
                }
                """
            ).indented(),
            navDeepLinkStub
        ).run().expect(
            """
src/com/example/test.kt:6: Error: Creation of empty NavDeepLink [EmptyNavDeepLink]
    navDeepLink { }
    ~~~~~~~~~~~
1 errors, 0 warnings
            """
        )
    }

    @Test
    fun testErrorsWithComment() {
        lint().files(
            kotlin(
                """
                package com.example

                import androidx.navigation.navDeepLink

                fun createDeepLink() {
                    navDeepLink { /* comment */ }
                }
                """
            ).indented(),
            navDeepLinkStub
        ).run().expect(
            """
src/com/example/test.kt:6: Error: Creation of empty NavDeepLink [EmptyNavDeepLink]
    navDeepLink { /* comment */ }
    ~~~~~~~~~~~
1 errors, 0 warnings
            """
        )
    }

    private val navDeepLinkStub: TestFile = kotlin(
        """
package androidx.navigation

public fun navDeepLink(deepLinkBuilder: NavDeepLinkDslBuilder.() -> Unit): NavDeepLink {}
        """
    ).indented()
}
