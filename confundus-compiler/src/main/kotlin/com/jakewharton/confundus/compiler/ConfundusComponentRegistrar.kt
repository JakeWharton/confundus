package com.jakewharton.confundus.compiler

import com.google.auto.service.AutoService
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CommonConfigurationKeys
import org.jetbrains.kotlin.config.CompilerConfiguration

@OptIn(ExperimentalCompilerApi::class)
@AutoService(CompilerPluginRegistrar::class)
class ConfundusCompilerPluginRegistrar : CompilerPluginRegistrar() {
  override val supportsK2: Boolean get() = true

  override fun ExtensionStorage.registerExtensions(configuration: CompilerConfiguration) {
    val messageCollector = configuration.get(
      CommonConfigurationKeys.MESSAGE_COLLECTOR_KEY,
      MessageCollector.NONE
    )
    IrGenerationExtension.registerExtension(ConfundusIrGenerationExtension(messageCollector))
  }
}
