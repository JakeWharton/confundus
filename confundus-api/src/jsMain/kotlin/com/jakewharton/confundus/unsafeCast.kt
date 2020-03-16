package com.jakewharton.confundus

import kotlin.DeprecationLevel.HIDDEN
import kotlin.js.unsafeCast as kotlinUnsafeCast

@Deprecated("Use kotlin.js.unsafeCast in JS modules", level = HIDDEN)
@Suppress("NOTHING_TO_INLINE") // Alias to stdlib function.
actual inline fun <T> Any?.unsafeCast(): T = kotlinUnsafeCast<T>()
