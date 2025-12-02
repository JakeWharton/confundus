package com.jakewharton.confundus.gradle

import assertk.assertThat
import assertk.assertions.contains
import com.google.testing.junit.testparameterinjector.TestParameter
import com.google.testing.junit.testparameterinjector.TestParameterInjector
import java.io.File
import org.gradle.testkit.runner.GradleRunner
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(TestParameterInjector::class)
class ConfundusPluginTest(
  @param:TestParameter(LATEST_GRADLE_VERSION, MINIMUM_GRADLE_VERSION)
  private val gradleVersion: String,
) {

  @Test fun jvm() {
    val result = createRunner(File(fixturesDir, "jvm")).build()
    assertThat(result.output).contains("BUILD SUCCESSFUL")
  }

  @Test fun mpp() {
    val result = createRunner(File(fixturesDir, "mpp")).build()
    assertThat(result.output).contains("BUILD SUCCESSFUL")
  }

  private fun createRunner(fixtureDir: File): GradleRunner {
    File("../gradle").copyRecursively(File(fixtureDir, "gradle"), true)
    return GradleRunner.create()
      .apply {
        if (gradleVersion != LATEST_GRADLE_VERSION) {
          withGradleVersion(gradleVersion)
        }
      }
      .withProjectDir(fixtureDir)
      .withDebug(true) // Run in-process
      .withArguments(
        "clean",
        "assemble",
        "--stacktrace",
        "--continue",
        "--no-build-cache",
        "--no-configuration-cache", // KGP's problem.
        VERSION_PROPERTY,
        VALIDATE_KOTLIN_METADATA,
      )
      .forwardOutput()
  }
}

private val fixturesDir = File("src/test/fixture")
private const val VERSION_PROPERTY = "-PconfundusVersion=$ConfundusVersion"
private const val LATEST_GRADLE_VERSION = "latest"
private const val VALIDATE_KOTLIN_METADATA = "-Porg.gradle.kotlin.dsl.skipMetadataVersionCheck=false"
