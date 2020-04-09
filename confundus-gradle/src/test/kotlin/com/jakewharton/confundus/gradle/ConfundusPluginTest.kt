package com.jakewharton.confundus.gradle

import com.google.common.truth.Truth.assertThat
import java.io.File
import org.gradle.testkit.runner.GradleRunner
import org.junit.Test

class ConfundusPluginTest {
  private val fixturesDir = File("src/test/fixture")

  private fun versionProperty() = "-PconfundusVersion=$confundusVersion"

  @Test fun jvm() {
    val fixtureDir = File(fixturesDir, "jvm")
    val gradleRoot = File(fixtureDir, "gradle").also { it.mkdir() }
    File("../gradle/wrapper").copyRecursively(File(gradleRoot, "wrapper"), true)

    val result = GradleRunner.create()
        .withProjectDir(fixtureDir)
        .withArguments(
            "clean", "compileKotlin", "compileTestKotlin", "--stacktrace", versionProperty()
        )
        .build()
    assertThat(result.output).contains("BUILD SUCCESSFUL")
  }

  @Test fun jvmIr() {
    val fixtureDir = File(fixturesDir, "jvm-ir")
    val gradleRoot = File(fixtureDir, "gradle").also { it.mkdir() }
    File("../gradle/wrapper").copyRecursively(File(gradleRoot, "wrapper"), true)

    val result = GradleRunner.create()
        .withProjectDir(fixtureDir)
        .withArguments(
            "clean", "compileKotlin", "compileTestKotlin", "--stacktrace", versionProperty()
        )
        .build()
    assertThat(result.output).contains("BUILD SUCCESSFUL")
  }

  @Test fun mpp() {
    val fixtureDir = File(fixturesDir, "mpp")
    val gradleRoot = File(fixtureDir, "gradle").also { it.mkdir() }
    File("../gradle/wrapper").copyRecursively(File(gradleRoot, "wrapper"), true)

    val result = GradleRunner.create()
        .withProjectDir(fixtureDir)
        .withArguments(
            "clean",
            "compileKotlinJvm", "compileTestKotlinJvm",
            "compileKotlinJvm2", "compileTestKotlinJvm2",
            "--stacktrace", versionProperty()
        )
        .build()
    assertThat(result.output).contains("BUILD SUCCESSFUL")
  }
}
