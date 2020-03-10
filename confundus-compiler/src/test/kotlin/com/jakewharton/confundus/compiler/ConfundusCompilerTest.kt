package com.jakewharton.confundus.compiler

import com.google.common.truth.Truth.assertThat
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.KotlinCompilation.ExitCode
import com.tschuchort.compiletesting.KotlinCompilation.Result
import com.tschuchort.compiletesting.SourceFile
import java.io.PrintWriter
import java.io.StringWriter
import java.lang.reflect.Method
import org.intellij.lang.annotations.Language
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes.ASM7
import org.objectweb.asm.util.Textifier
import org.objectweb.asm.util.TraceMethodVisitor

@RunWith(Parameterized::class)
class ConfundusCompilerTest(private val useIr: Boolean) {
  companion object {
    @JvmStatic
    @Parameters(name = "useIr={0}")
    fun parameters() = arrayOf(true, false)
  }

  @Test fun nullableToNonNull() {
    val result = compile("""
      import com.jakewharton.confundus.unsafeCast

      fun subject(o: String?): String {
        return o.unsafeCast<String>()
      }

      fun test() = buildString {
        appendln(subject("hey").length)
        try {
          throw AssertionError(subject(null).length)
        } catch (e: NullPointerException) {
          appendln("NPE")
        }
      }
    """.trimIndent())

    assertThat(result.exitCode).isEqualTo(ExitCode.OK)
    val mainKt = result.generatedClassFile("MainKt")
    assertThat(mainKt.methodBytecode("subject")).isEqualTo("""
      // access flags 0x19
      public final static subject(Ljava/lang/String;)Ljava/lang/String;
      @Lorg/jetbrains/annotations/NotNull;() // invisible
        // annotable parameter count: 1 (invisible)
        @Lorg/jetbrains/annotations/Nullable;() // invisible, parameter 0
       L0
        LINENUMBER 4 L0
        ALOAD 0
        ARETURN
       L1
        LOCALVARIABLE o Ljava/lang/String; L0 L1 0
        MAXSTACK = 1
        MAXLOCALS = 1
    """.trimIndent())

    assertThat(mainKt.method("test").invoke(null)).isEqualTo("""
      |3
      |NPE
      |""".trimMargin())
  }

  @Test fun nonNullToSubtype() {
    val result = compile("""
      import com.jakewharton.confundus.unsafeCast

      fun subject(o: Any): String {
        return o.unsafeCast<String>()
      }

      fun test() = buildString {
        appendln(subject("hey").length)
      }
    """.trimIndent())

    assertThat(result.exitCode).isEqualTo(ExitCode.OK)
    val mainKt = result.generatedClassFile("MainKt")
    assertThat(mainKt.methodBytecode("subject")).isEqualTo("""
      // access flags 0x19
      public final static subject(Ljava/lang/Object;)Ljava/lang/String;
      @Lorg/jetbrains/annotations/NotNull;() // invisible
        // annotable parameter count: 1 (invisible)
        @Lorg/jetbrains/annotations/NotNull;() // invisible, parameter 0
       L0
        ALOAD 0
        LDC "o"
        INVOKESTATIC kotlin/jvm/internal/Intrinsics.checkParameterIsNotNull (Ljava/lang/Object;Ljava/lang/String;)V
       L1
        LINENUMBER 4 L1
        ALOAD 0
        CHECKCAST java/lang/String
        ARETURN
       L2
        LOCALVARIABLE o Ljava/lang/Object; L0 L2 0
        MAXSTACK = 2
        MAXLOCALS = 1
    """.trimIndent())

    assertThat(mainKt.method("test").invoke(null)).isEqualTo("""
      |3
      |""".trimMargin())
  }

  @Test fun nullableToNonNullSubtype() {
    val result = compile("""
      import com.jakewharton.confundus.unsafeCast

      fun subject(o: Any?): String {
        return o.unsafeCast<String>()
      }

      fun test() = buildString {
        appendln(subject("hey").length)
        try {
          throw AssertionError(subject(null).length)
        } catch (e: NullPointerException) {
          appendln("NPE")
        }
      }
    """.trimIndent())

    assertThat(result.exitCode).isEqualTo(ExitCode.OK)
    val mainKt = result.generatedClassFile("MainKt")
    assertThat(mainKt.methodBytecode("subject")).isEqualTo("""
      // access flags 0x19
      public final static subject(Ljava/lang/Object;)Ljava/lang/String;
      @Lorg/jetbrains/annotations/NotNull;() // invisible
        // annotable parameter count: 1 (invisible)
        @Lorg/jetbrains/annotations/Nullable;() // invisible, parameter 0
       L0
        LINENUMBER 4 L0
        ALOAD 0
        CHECKCAST java/lang/String
        ARETURN
       L1
        LOCALVARIABLE o Ljava/lang/Object; L0 L1 0
        MAXSTACK = 1
        MAXLOCALS = 1
    """.trimIndent())

    assertThat(mainKt.method("test").invoke(null)).isEqualTo("""
      |3
      |NPE
      |""".trimMargin())
  }

  @Test fun nullableToNullableSubtype() {
    val result = compile("""
      import com.jakewharton.confundus.unsafeCast

      fun subject(o: Any?): String? {
        return o.unsafeCast<String?>()
      }

      fun test() = buildString {
        appendln(subject("hey")?.length)
        appendln(subject(null)?.length)
      }
    """.trimIndent())

    assertThat(result.exitCode).isEqualTo(ExitCode.OK)
    val mainKt = result.generatedClassFile("MainKt")
    assertThat(mainKt.methodBytecode("subject")).isEqualTo("""
      // access flags 0x19
      public final static subject(Ljava/lang/Object;)Ljava/lang/String;
      @Lorg/jetbrains/annotations/Nullable;() // invisible
        // annotable parameter count: 1 (invisible)
        @Lorg/jetbrains/annotations/Nullable;() // invisible, parameter 0
       L0
        LINENUMBER 4 L0
        ALOAD 0
        CHECKCAST java/lang/String
        ARETURN
       L1
        LOCALVARIABLE o Ljava/lang/Object; L0 L1 0
        MAXSTACK = 1
        MAXLOCALS = 1
    """.trimIndent())

    assertThat(mainKt.method("test").invoke(null)).isEqualTo("""
      |3
      |null
      |""".trimMargin())
  }

  private fun compile(@Language("kotlin") source: String): Result {
    return KotlinCompilation().apply {
      sources = listOf(SourceFile.kotlin("main.kt", source))
      messageOutputStream = System.out
      compilerPlugins = listOf(ConfundusComponentRegistrar())
      inheritClassPath = true
      useIR = useIr
    }.compile()
  }

  private fun Result.generatedClassFile(name: String): ClassFile {
    val suffix = "$name.class"
    val bytes = generatedFiles.single { it.endsWith(suffix) }.readBytes()
    val cls = classLoader.loadClass(name)
    return ClassFile(cls, bytes)
  }

  class ClassFile(private val cls: Class<*>, private val bytes: ByteArray) {
    fun method(name: String): Method {
      return cls.methods.filter { it.name == name }
          .ifEmpty { throw IllegalArgumentException("No method named '$name' found") }
          .singleOrNull()
          ?: throw IllegalStateException("Multiple methods named '$name'")
    }

    fun methodBytecode(name: String, descriptor: String? = null): String {
      val textifier = Textifier()

      val targetName = name
      val targetDescriptor = descriptor
      var found = false
      ClassReader(bytes).accept(object : ClassVisitor(ASM7) {
        override fun visitMethod(
          access: Int,
          name: String,
          descriptor: String,
          signature: String?,
          exceptions: Array<out String>?
        ): MethodVisitor? {
          if (targetName == name && (targetDescriptor == null || targetDescriptor == descriptor)) {
            check(!found) { "Multiple methods named '$name'. Use descriptor to disambiguate." }
            found = true
            textifier.visitMethod(access, name, descriptor, signature, exceptions)
            return TraceMethodVisitor(textifier)
          }
          return null
        }
      }, 0)
      check(found) { "Method $name not found" }

      val writer = StringWriter()
      textifier.print(PrintWriter(writer))
      return writer.toString().trimEnd().trimIndent()
    }
  }
}
