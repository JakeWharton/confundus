package com.jakewharton.confundus.compiler

import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrTypeOperator
import org.jetbrains.kotlin.ir.expressions.impl.IrTypeOperatorCallImpl
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

class ConfundusElementTransformer(
  pluginContext: IrPluginContext,
) : IrElementTransformerVoidWithContext() {
  private val unsafeCast = pluginContext.referenceFunctions(
    callableId = CallableId(
        packageName = FqName("com.jakewharton.confundus"),
        callableName = Name.identifier("unsafeCast")
    )
  ).single()

  override fun visitCall(expression: IrCall): IrExpression {
    if (expression.symbol == unsafeCast) {
      return IrTypeOperatorCallImpl(
        startOffset = expression.startOffset,
        endOffset = expression.endOffset,
        type = expression.type,
        operator = IrTypeOperator.IMPLICIT_CAST,
        typeOperand = expression.type,
        argument = expression.arguments[0]!!,
      )
    }
    return super.visitCall(expression)
  }
}
