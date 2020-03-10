@file:JvmName("Api")

package com.jakewharton.confundus

@Suppress("unused")
fun <T> Any?.unsafeCast(): T = throw AssertionError()
