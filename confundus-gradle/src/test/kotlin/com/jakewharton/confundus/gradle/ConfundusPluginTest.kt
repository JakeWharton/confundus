package com.jakewharton.confundus.gradle

import assertk.assertThat
import assertk.assertions.contains
import java.io.File
import org.gradle.testkit.runner.GradleRunner
import org.junit.Test

class ConfundusPluginTest {
  private val fixturesDir = File("src/test/fixture")

  private fun versionProperty() = "-PconfundusVersion=$ConfundusVersion"

  @Test fun jvm() {
    val fixtureDir = File(fixturesDir, "jvm")
    val gradleRoot = File(fixtureDir, "gradle").also { it.mkdir() }
    File("../gradle").copyRecursively(gradleRoot, true)

    val result = GradleRunner.create()
        .withProjectDir(fixtureDir)
        .withArguments("clean", "assemble", "--stacktrace", versionProperty())
        .build()
    assertThat(result.output).contains("BUILD SUCCESSFUL")
  }

  @Test fun mpp() {
    val fixtureDir = File(fixturesDir, "mpp")
    val gradleRoot = File(fixtureDir, "gradle").also { it.mkdir() }
    File("../gradle").copyRecursively(gradleRoot, true)

    val result = GradleRunner.create()
        .withProjectDir(fixtureDir)
        .withArguments("clean", "assemble", "--stacktrace", versionProperty())
        .build()
    assertThat(result.output).contains("BUILD SUCCESSFUL")
  }
}
