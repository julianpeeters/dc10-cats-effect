# dc10-cats-effect
Library for use with the `dc10-scala` code generator

### Getting Started
 - Library for Scala @SCALA@ (JVM only)

```scala
"com.julianpeeters" %% "dc10-cats-effect" % "@VERSION@"
```

### Usage

Use the dsl to define scala code that depends on cats-effect:

```scala mdoc
import dc10.cats.effect.dsl.*
import dc10.scala.dsl.given               // for strings, e.g., "Hello, World!"
import scala.language.implicitConversions // for literals, e.g., "Hello, World!"

val snippet =
  IOAPP("HelloWorld",
    RUN(
      IO.PRINTLN("Hello, World!")
    )
  )
```

Use the compiler in `dc10-scala` to render code `toString` or `toVirtualFile`:

```scala mdoc
import dc10.scala.compiler.{compile, toString}
import dc10.scala.version.`3.3.1`

val result: String = snippet.compile.toString["scala-3.3.1"]
```