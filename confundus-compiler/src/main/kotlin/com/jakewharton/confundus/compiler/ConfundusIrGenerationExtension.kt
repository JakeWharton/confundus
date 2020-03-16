package com.jakewharton.confundus.compiler

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.visitors.transformChildrenVoid

internal object ConfundusIrGenerationExtension : IrGenerationExtension {
  override fun generate(
    moduleFragment: IrModuleFragment,
    pluginContext: IrPluginContext
  ) {
    moduleFragment.transformChildrenVoid(ConfundusIrElementTransformer)
  }
}
