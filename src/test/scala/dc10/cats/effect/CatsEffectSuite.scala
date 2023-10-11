package dc10.cats.effect

import dc10.cats.effect.dsl.*
import dc10.scala.compiler.{compile, toString}
import dc10.scala.dsl.given
import dc10.scala.version.`3.3.1`
import scala.language.implicitConversions

import munit.FunSuite

class CatsEffectSuite extends FunSuite:

  test("ioapp val"):

    def ast = IOAPP("HelloWorld", RUN(IO.PRINTLN("Hello, World!")))
    
    val obtained: String =
      ast.compile.toString["scala-3.3.1"]
      
    val expected: String =
      """object HelloWorld extends cats.effect.IOApp.Simple:
        |
        |  val run: cats.effect.IO[Unit] = IO.println("Hello, World!")""".stripMargin
      
    assertEquals(obtained, expected)