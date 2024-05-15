package jaco.project.handler

import cats.effect.std.Console
import cats.syntax.EitherOps
import cats.{Apply, FlatMap, Functor, Show}
import jaco.project.OutputMessages.failureMsg
import jaco.project.error.InputError
import jaco.project.simpleMain.ErrorOr
import jaco.project.validation.InputValidation.Validator

object InputHandler:

  def greetAndRead[F[_] : Apply : Console](msg: String): F[String] =
    Apply[F].productR(Console[F].println(msg))(Console[F].readLine)

  def readUntilValid[F[_] : FlatMap : Console](msg: String, validator: Validator[ErrorOr]): F[OutputPath] =
    FlatMap[F].tailRecM(greetAndRead(msg))(Functor[F].map(_)( msgInput =>
      EitherOps[Throwable, OutputPath](validator(msgInput)).leftMap {
        case err: InputError => greetAndRead(failureMsg(err))
        case _ => greetAndRead(failureMsg("aconteceu alguma loucura, mas não explodi"))
      }
    ))
