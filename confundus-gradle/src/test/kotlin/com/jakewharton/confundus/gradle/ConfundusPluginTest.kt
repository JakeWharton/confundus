package com.jakewharton.confundus.gradle

import com.google.common.truth.Truth.assertThat
import java.io.File
import org.gradle.testkit.runner.GradleRunner
import org.junit.Test

class ConfundusPluginTest {
  private val fixturesDir = File("src/test/fixture")

  @Test fun jvm() {
    val fixtureDir = File(fixturesDir, "jvm")
    val gradleRoot = File(fixtureDir, "gradle").also { it.mkdir() }
    File("../gradle/wrapper").copyRecursively(File(gradleRoot, "wrapper"), true)

    val result = GradleRunner.create()
        .withProjectDir(fixtureDir)
        .withPluginClasspath()
        .withArguments("clean", "compileKotlin", "--stacktrace")
        .build()
    assertThat(result.output).contains("BUILD SUCCESSFUL")
  }

  @Test fun mpp() {
    val fixtureDir = File(fixturesDir, "mpp")
    val gradleRoot = File(fixtureDir, "gradle").also { it.mkdir() }
    File("../gradle/wrapper").copyRecursively(File(gradleRoot, "wrapper"), true)

    val result = GradleRunner.create()
        .withProjectDir(fixtureDir)
        .withPluginClasspath()
        .withArguments("clean", "compileKotlinJvm", "compileKotlinJvm2", "--stacktrace")
        .build()
    assertThat(result.output).contains("BUILD SUCCESSFUL")
  }
}
