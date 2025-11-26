package com.jakewharton.confundus.compiler

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CommonConfigurationKeys
import org.jetbrains.kotlin.config.CompilerConfiguration

@OptIn(ExperimentalCompilerApi::class)
class ConfundusCompilerPluginRegistrar : CompilerPluginRegistrar() {
  override val supportsK2: Boolean get() = true
  override val pluginId: String get() = "com.jakewharton.confundus"

  override fun ExtensionStorage.registerExtensions(configuration: CompilerConfiguration) {
    val messageCollector = configuration.get(
      CommonConfigurationKeys.MESSAGE_COLLECTOR_KEY,
      MessageCollector.NONE
    )
    IrGenerationExtension.registerExtension(ConfundusIrGenerationExtension(messageCollector))
  }
}
