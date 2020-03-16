package com.jakewharton.confundus.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget

private const val confundusRuntime = "com.jakewharton.confundus:confundus-api:$confundusVersion"

class ConfundusPlugin : Plugin<Project> {
  override fun apply(project: Project) {
    val plugins = project.plugins
    val extensions = project.extensions

    plugins.withId("org.jetbrains.kotlin.jvm") {
      val kotlin = extensions.getByType(KotlinJvmProjectExtension::class.java)
      kotlin.target.addConfundusDependency()
    }

    plugins.withId("org.jetbrains.kotlin.multiplatform") {
      val kotlin = extensions.getByType(KotlinMultiplatformExtension::class.java)
      kotlin.targets.all { target ->
        target.addConfundusDependency()
      }
    }
  }

  private fun KotlinTarget.addConfundusDependency() {
    compilations.all { compilation ->
      compilation.dependencies {
        compileOnly(confundusRuntime)
      }
    }
  }
}
