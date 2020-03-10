package com.jakewharton.confundus.gradle

import com.google.auto.service.AutoService
import org.gradle.api.Project
import org.gradle.api.tasks.compile.AbstractCompile
import org.jetbrains.kotlin.gradle.dsl.KotlinCommonOptions
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinGradleSubplugin
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption

@Suppress("unused") // Created via SerivceLoader.
@AutoService(KotlinGradleSubplugin::class)
class ConfundusGradleSubplugin : KotlinGradleSubplugin<AbstractCompile> {
  override fun isApplicable(
    project: Project,
    task: AbstractCompile
  ) = project.plugins.hasPlugin(ConfundusPlugin::class.java)

  override fun getCompilerPluginId() = "com.jakewharton.confundus"

  override fun getPluginArtifact() = SubpluginArtifact(
      "com.jakewharton.confundus",
      "confundus-compiler",
      confundusVersion
  )

  override fun apply(
    project: Project,
    kotlinCompile: AbstractCompile,
    javaCompile: AbstractCompile?,
    variantData: Any?,
    androidProjectHandler: Any?,
    kotlinCompilation: KotlinCompilation<KotlinCommonOptions>?
  ): List<SubpluginOption> = emptyList()
}
