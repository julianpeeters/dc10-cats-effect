package dc10.cats.effect

import cats.data.StateT
import cats.Eval
import cats.free.Cofree
import dc10.scala.Statement
import dc10.scala.ctx.{ErrorF, ext}
import org.tpolecat.sourcepos.SourcePos
import dc10.scala.Statement.ValueExpr
import dc10.scala.Statement.{ObjectDef, ValueDef}
import dc10.scala.Symbol.{Object, Term}
import dc10.scala.Symbol.Term.TypeLevel.__
import dc10.scala.Statement.TypeExpr

trait CatsEffect[F[_]]:
  type IO[_]
  type IOAPP
  def IO: F[TypeExpr[IO[__]]]
  def Io[A]: F[ValueExpr[IO[A] => IO[A]]]
  extension [A] (io: F[ValueExpr[IO[A] => IO[A]]])
    @scala.annotation.targetName("appVIO")
    def apply(arg: F[ValueExpr[A]]): F[ValueExpr[IO[A]]]
  def IOAPP(name: String, run: F[ValueExpr[IO[Unit]]])(using sp: SourcePos): F[ValueExpr[IO[Unit]]]
  def RUN(program: F[ValueExpr[IO[Unit]]]): F[ValueExpr[IO[Unit]]]
  extension (io: F[TypeExpr[IO[__]]])
    def PRINTLN(msg: F[ValueExpr[String]]): F[ValueExpr[IO[Unit]]]

object CatsEffect:

  trait Mixins extends CatsEffect[[A] =>> StateT[ErrorF, List[Statement], A]]:
    def IO: StateT[ErrorF, List[Statement], TypeExpr[IO[__]]] =
      StateT.pure(TypeExpr(Cofree((), Eval.now(Term.TypeLevel.Var.UserDefinedType(None, "IO", None)))))
    def Io[A]: StateT[ErrorF, List[Statement], ValueExpr[IO[A] => IO[A]]] =
      ???
    extension [A] (io: StateT[ErrorF, List[Statement], ValueExpr[IO[A] => IO[A]]])
      @scala.annotation.targetName("appVIO")
      def apply(arg: StateT[ErrorF, List[Statement], ValueExpr[A]]): StateT[ErrorF, List[Statement], ValueExpr[IO[A]]] =
        ???
    def IOAPP(
      name: String,
      run: StateT[ErrorF, List[Statement], ValueExpr[IO[Unit]]]
    )(using sp: SourcePos): StateT[ErrorF, List[Statement], ValueExpr[IO[Unit]]] =
      for
        (c, r) <- StateT.liftF[ErrorF, List[Statement], (List[Statement], ValueExpr[IO[Unit]])](run.runEmpty)
        o <- StateT.pure[ErrorF, List[Statement], Object[IOAPP]](Object(None, name, Some(Cofree((), Eval.now(Term.TypeLevel.Var.UserDefinedType(None, "cats.effect.IOApp.Simple", None)))), c.map(s => s.addIndent)))
        d <- StateT.pure[ErrorF, List[Statement], ObjectDef](ObjectDef(o, 0))
        _ <- StateT.modifyF[ErrorF, List[Statement]](ctx => ctx.ext(d))
      yield r

    def RUN(program: StateT[ErrorF, List[Statement], ValueExpr[IO[Unit]]]): StateT[ErrorF, List[Statement], ValueExpr[IO[Unit]]] =
      for
        i <- StateT.liftF(program.runEmptyA)
        v <- StateT.pure(Term.ValueLevel.Var.UserDefinedValue(None, "run", i.value.tail.value.tpe, Some(i.value)))
        d <- StateT.pure[ErrorF, List[Statement], ValueDef](ValueDef.Val(0, v))
        _ <- StateT.modifyF[ErrorF, List[Statement]](ctx => ctx.ext(d))
      yield i
  
    extension (io: StateT[ErrorF, List[Statement], TypeExpr[IO[__]]])
      def PRINTLN(msg: StateT[ErrorF, List[Statement], ValueExpr[String]]): StateT[ErrorF, List[Statement], ValueExpr[IO[Unit]]] =
        for
          s <- StateT.liftF(msg.runEmptyA)
          v <- StateT.pure[ErrorF, List[Statement], Term.Value[IO[Unit]]](Cofree((), Eval.now(Term.ValueLevel.Var.Println(None, s.value))))
        yield ValueExpr(v)