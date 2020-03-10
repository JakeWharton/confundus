Confundus: Unsafe Cast for Kotlin/JVM
=====================================

A Kotlin compiler plugin which brings Kotlin/JS's `unsafeCast` to Kotlin/JVM.

The `unsafeCast` method bypasses the normal safety of the type system allowing you to reinterpret
a nullable reference as non-nullable or a type as a subtype without the overhead of Kotlin's
regular check.


Okay but why?
-------------

Sometimes you know more than the compiler. If you know a nullable reference is actually non-null
you can use `!!` for a non-null reference. If you know an `Any` is actually a `String` you can cast
with `as String` for a string reference.

Both of these are runtime-checked operations. This means that there is bytecode which validates the
conversion. You'll get a `NullPointerException` from `!!` when the reference is null because of the
`IFNONNULL` bytecode and a call to `Instrinsics.throwNpe`. Similarly, you'll may get a
`TypeCastException` from `as String` because Kotlin checks the reference is non-null using that
`IFNONNULL` bytecode, even if the Kotlin type is already non-null! Both of these behaviors are
desired because they help maintain Kotlin's type system at runtime.
 
When writing performance-sensitive code these runtime-checks can sometimes have a prohibitive cost.

For example, in a doubly-linked structure, invariants mean that if `self.next` is non-null then
`self.next.prev` is also non-null. These `next` and `prev` properties are _required_ to be marked as
nullable to support leaf nodes (such as the head or tail in a list). Thus, we have to use `!!` even
after checking that `self.next` is non-null. This requires an additional method call and null check
which will always succeed.

`unsafeCast` allows you to perform the same operation as `!!` but with zero runtime overhead. You
can use your invariants as a guarantee to bypass the safety the compiler otherwise provides.
Similarly, for `as` casts, `unsafeCast` produces minimal bytecode (just the `CHECKCAST` bytecode)
to reinterpret a reference as a new type.

With `unsafeCast`, you can take performance-sensitive code where the overhead of `!!` or `as` has
been measured to produce overhead and reclaim that performance at the expense of safety. Basically,
you go back to the reduced safety of Java.


Supported conversions
---------------------

### Nullable to non-null type

```diff
 val o1: String? = someFunction()
-val o2 = o1!!
+val o2 = o1.unsafeCast<String>()
```
```diff
  ALOAD 0
- DUP
- IFNONNULL L1
- INVOKESTATIC kotlin/jvm/internal/Intrinsics.throwNpe ()V
-L1
  ASTORE 1
```

### Non-null type to non-null subtype

```diff
 val o1: Any = someFunction()
-val o2 = o1 as String
+val o2 = o1.unsafeCast<String>()
```
```diff
  ALOAD 0
- DUP
- IFNONNULL L1
- NEW kotlin/TypeCastException
- DUP
- LDC "null cannot be cast to non-null type kotlin.String"
- INVOKESPECIAL kotlin/TypeCastException.<init> (Ljava/lang/String;)V
- ATHROW
-L1
  CHECKCAST java/lang/String
  ASTORE 1
```

### Nullable type to non-null subtype

```diff
 val o1: Any? = someFunction()
-val o2 = o1 as String
+val o2 = o1.unsafeCast<String>()
```
```diff
  ALOAD 0
- DUP
- IFNONNULL L1
- NEW kotlin/TypeCastException
- DUP
- LDC "null cannot be cast to non-null type kotlin.String"
- INVOKESPECIAL kotlin/TypeCastException.<init> (Ljava/lang/String;)V
- ATHROW
-L1
  CHECKCAST java/lang/String
  ASTORE 1
```

### Nullable type to nullable subtype

Note: No change in bytecode for this case!

```diff
 val o1: Any? = someFunction()
-val o2: String? = o1 as String?
+val o2: String? = o1.unsafeCast<String?>()
```
```diff
 ALOAD 0
 CHECKCAST java/lang/String
 ASTORE 1
```


Usage
-----

```groovy
buildscript {
  dependencies {
    classpath 'com.jakewharton.confundus:confundus-gradle:1.0.0'
  }
}

apply plugin: 'org.jetbrains.kotlin.jvm' // or .multiplatform
apply plugin: 'com.jakewharton.confundus'
```


License
=======

    Copyright 2020 Jake Wharton

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
