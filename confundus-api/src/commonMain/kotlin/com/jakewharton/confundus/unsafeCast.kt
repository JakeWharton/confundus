package com.jakewharton.confundus

/**
 * Reinterprets this value as a value of the specified type [T] without any actual type checking.
 *
 * There are three useful conversions that this function can make:
 *
 *  - Nullable to non-null type
 *
 *    ```kotlin
 *    val o1: String? = someFunction()
 *    val o2: String = o1.unsafeCast<String>()
 *    ```
 *
 *  - Non-null to non-null subtype
 *
 *    ```kotlin
 *    val o1: Any = someFunction()
 *    val o2: String = o1.unsafeCast<String>()
 *    ```
 *
 *  - Nullable to non-null subtype
 *
 *    ```kotlin
 *    val o1: Any? = someFunction()
 *    val o2: String = o1.unsafeCast<String>()
 *    ```
 *
 * On the JVM there is one additional useful conversions:
 *
 *  - Platform type to non-null type
 *
 *     ```kotlin
 *     val o1: String! = platformFunction()
 *     val o2: String = o2.unsafeCast<String>()
 *     ```
 *
 * Note: This function will never actually be invoked and will be removed by the compiler plugin.
 */
expect fun <T> Any?.unsafeCast(): T
