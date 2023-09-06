@file:JvmName("Api")

package com.jakewharton.confundus

@Suppress("unused")
actual fun <T> Any?.unsafeCast(): T = throw AssertionError()
