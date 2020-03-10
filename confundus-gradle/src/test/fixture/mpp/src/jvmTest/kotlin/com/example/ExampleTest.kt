package com.example

import com.jakewharton.confundus.unsafeCast

fun main() {
  println(null.unsafeCast<String>().length)
}
