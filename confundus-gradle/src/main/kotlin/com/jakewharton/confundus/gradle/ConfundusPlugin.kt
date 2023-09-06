package com.jakewharton.confundus.gradle

import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin.IMPLEMENTATION_CONFIGURATION_NAME
import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption

private const val confundusRuntime = "com.jakewharton.confundus:confundus-api:$ConfundusVersion"

@Suppress("unused") // Created by Gradle.
class ConfundusPlugin : KotlinCompilerPluginSupportPlugin {
  override fun apply(target: Project) {
    super.apply(target)

    target.plugins.withId("org.jetbrains.kotlin.jvm") {
      target.dependencies.add(IMPLEMENTATION_CONFIGURATION_NAME, confundusRuntime)
    }
    target.plugins.withId("org.jetbrains.kotlin.multiplatform") {
      val kotlin = target.extensions.getByName("kotlin") as KotlinMultiplatformExtension
      kotlin.sourceSets.getByName("commonMain").dependencies {
        implementation(confundusRuntime)
      }
    }
  }

  override fun isApplicable(kotlinCompilation: KotlinCompilation<*>): Boolean {
    return when (kotlinCompilation.platformType) {
      KotlinPlatformType.common -> true
      KotlinPlatformType.jvm -> true
      KotlinPlatformType.js -> true
      KotlinPlatformType.androidJvm -> true
      KotlinPlatformType.native -> false
      KotlinPlatformType.wasm -> false
    }
  }

  override fun getCompilerPluginId() = "com.jakewharton.confundus"

  override fun getPluginArtifact() = SubpluginArtifact(
      "com.jakewharton.confundus",
      "confundus-compiler",
      ConfundusVersion
  )

  override fun applyToCompilation(kotlinCompilation: KotlinCompilation<*>): Provider<List<SubpluginOption>> {
    return kotlinCompilation.project.provider { emptyList() }
  }
}
