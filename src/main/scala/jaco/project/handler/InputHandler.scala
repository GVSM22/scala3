package jaco.project.handler

import cats.effect.Temporal
import cats.effect.std.Console
import cats.syntax.all.catsSyntaxEither
import cats.syntax.functor.toFunctorOps
import cats.{Apply, FlatMap, Functor, Show}
import fs2.Chunk
import jaco.project.OutputMessages.failureMsg
import jaco.project.error.InputError
import jaco.project.simpleMain.ErrorOr
import jaco.project.validation.InputValidation.Validator

import scala.concurrent.duration.Duration

object InputHandler:

  def greetAndRead[F[_] : Apply : Console](msg: String): F[String] =
    Apply[F].productR(Console[F].println(msg))(Console[F].readLine)

  def readUntilValid[F[_] : FlatMap : Console](msg: String, validator: Validator[ErrorOr]): F[OutputPath] =
    FlatMap[F].tailRecM(greetAndRead(msg))(
      _.map(validator(_).leftMap {
        case err: InputError => greetAndRead(failureMsg(err))
        case _ => greetAndRead(failureMsg("aconteceu alguma loucura, mas não explodi"))
      })
    )

  private def readLineToChunk[F[_] : Console : Functor](): F[Chunk[String]] =
    Console[F].readLine.map(Chunk(_))

  def readUntilTimeout[F[_] : Console : FlatMap : Temporal](timeout: Duration): F[Chunk[String]] =
    FlatMap[F].tailRecM(Chunk.empty[String]) { chunks =>
      val timeoutWithChunks = Temporal[F].sleep(timeout).as(chunks)
      Temporal[F].race(readLineToChunk().map(_ ++ chunks), timeoutWithChunks)
    }
