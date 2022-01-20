/*
 * Copyright 2019-2021 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/dev/LICENSE
 */

package net.mamoe.mirai.console.gradle

import org.junit.jupiter.api.Test
import java.util.zip.ZipFile
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TestBuildPlugin : AbstractTest() {

    @Test
    fun `can build plugin`() {
        tempDir.resolve("build.gradle").appendText(
            """
            dependencies {
                api "com.zaxxer:SparseBitSet:1.2"
                implementation "com.google.code.gson:gson:2.8.9"
            }
        """.trimIndent()
        )
        gradleRunner()
            .withArguments("buildPlugin", "--stacktrace")
            .build()
        val jar = tempDir.resolve("build/libs").listFiles()!!.first { it.name.endsWith(".mirai.jar") }
        ZipFile(jar).use { zipFile ->
            val dpPrivate = zipFile.getInputStream(
                zipFile.getEntry("META-INF/mirai-console-plugin/dependencies-private.txt")
            ).use { it.readBytes().decodeToString() }
            val dpShared = zipFile.getInputStream(
                zipFile.getEntry("META-INF/mirai-console-plugin/dependencies-shared.txt")
            ).use { it.readBytes().decodeToString() }

            assertTrue { dpShared.contains("com.zaxxer:SparseBitSet:1.2") }
            assertFalse { dpShared.contains("com.google.code.gson:gson") }

            assertTrue { dpPrivate.contains("com.zaxxer:SparseBitSet:1.2") }
            assertTrue { dpPrivate.contains("com.google.code.gson:gson:2.8.9") }
        }
    }

}