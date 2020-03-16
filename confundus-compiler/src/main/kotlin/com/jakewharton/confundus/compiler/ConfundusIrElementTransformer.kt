package com.jakewharton.confundus.compiler

import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe

internal object ConfundusIrElementTransformer : IrElementTransformerVoid() {
  private val unsafeCastName = FqName("com.jakewharton.confundus.unsafeCast")

  override fun visitCall(expression: IrCall): IrExpression {
    if (expression.symbol.descriptor.fqNameSafe == unsafeCastName) {
      return expression.extensionReceiver!!
    }
    return super.visitCall(expression)
  }
}
