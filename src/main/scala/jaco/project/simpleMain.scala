package jaco.project

import cats.arrow.{Compose, FunctionK}
import cats.data.Const
import cats.{Applicative, Bifunctor, FlatMap, Functor, Id, Monoid, MonoidK, SemigroupK, Show, ~>}
import cats.effect.{IO, IOApp}
import fs2.{Pipe, Stream}
import jaco.project.client.GithubClient
import jaco.project.handler.{InputHandler, OutputHandler}
import jaco.project.validation.InputValidation
import org.http4s.ember.client.EmberClientBuilder

import scala.concurrent.duration.DurationInt

object simpleMain extends IOApp.Simple:
  type ErrorOr[T] = Either[Throwable, T]


  private val bizarreCasePipe: Pipe[IO, String, Unit] = _.filter(_.toIntOption.isEmpty)
    .map { str =>
      str.dropRight(1).foldRight(str.last.toString){
        (c, s) => s.appended(if (s.last.isUpper) c.toLower else c.toUpper)
      }
    }.printlns

  def concat[F[_] : SemigroupK : FlatMap, G[_] : Applicative, T](x1: G[F[T]], x2: F[G[T]])(using nt: G ~> F): G[F[T]] =
    Applicative[G].pure(
      SemigroupK[F].combineK(
        FlatMap[F].flatten(nt.apply(x1)),
        FlatMap[F].flatMap(x2)(nt.apply)
      )
    )

  def x(v: Functor[λ[A => A]]) = ???
  def y(v: Functor[[A] =>> A]) = ???

  case class BiComp[BF[_, _] : Bifunctor, F[_] : Functor, G[_] : Functor, A, B](v: BF[F[A], G[B]])
  given[BF[_, _] : Bifunctor, F[_] : Functor, G[_] : Functor]: Bifunctor[[a, b] =>> BiComp[BF, F, G, a, b]] =
    new Bifunctor[[a, b] =>> BiComp[BF, F, G, a, b]]:
      override def bimap[A, B, C, D](fab: BiComp[BF, F, G, A, B])(f: A => C, g: B => D): BiComp[BF, F, G, C, D] =
        BiComp(Bifunctor[BF].bimap(fab.v)(Functor[F].map(_)(f), Functor[G].map(_)(g)))

  case class XY[X, Y](x: X, y: Y)
  given Bifunctor[XY] = new Bifunctor[XY]:
    override def bimap[A, B, C, D](fab: XY[A, B])(f: A => C, g: B => D): XY[C, D] =
      XY(f(fab.x), g(fab.y))

  val a = Bifunctor[Either].compose[XY]
  val b = Bifunctor[Tuple2].compose[XY]
  val c: Functor[[α] =>> () => Id[α]] = Functor[Function0].compose[Id]


  override def run: IO[Unit] =
    val cmd = IO.consoleForIO

    val program = EmberClientBuilder.default[IO]
      .build
      .use { client =>
        val githubClient = GithubClient(client)
        val validator = InputValidation.validate[IO]
        val output = OutputHandler.outputSatanCall[IO]
          .orElse(OutputHandler.outputSatan[IO])
          .orElse(OutputHandler.finishProgram[IO])
          .orElse(OutputHandler.outputGithub(githubClient))

        val outputPipe: Pipe[IO, String, Unit] =
          _.evalMap(
            validator(_)
              .flatMap(output)
              .voidError
          )

        InputHandler.readUntilTimeout[IO](3.seconds)
          .map(Stream.chunk)
          .flatMap {
            _.covary[IO]
              .broadcastThrough(
              bizarreCasePipe(_).printlns,
              outputPipe
            )
              .compile
              .drain
          }
      }

    given FunctionK[Option, Seq] = new ~>[Option, Seq]:
      override def apply[A](fa: Option[A]): Seq[A] = fa.toSeq

    val x1: Option[Seq[Int]] = Option(Seq(2))
    val x2: Seq[Option[Int]] = Seq(Option(1))
    val res: Option[Seq[Int]] = concat(x1, x2)

    IO.println(
      ""
    )