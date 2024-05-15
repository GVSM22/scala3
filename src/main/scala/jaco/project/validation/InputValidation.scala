package jaco.project.validation

import cats.data.Kleisli
import cats.implicits.catsSyntaxApplicativeError
import cats.syntax.all.{catsSyntaxFunction1FlatMap, toFunctorOps}
import cats.{ApplicativeError, ApplicativeThrow, Functor, MonadThrow}
import jaco.project.error.{InputError, NotEE}
import jaco.project.handler.*

object InputValidation:

  type Validator[F[_]] = String => F[OutputPath]

  def onlyIntNumber[F[_]](userInput: String)(using ae: ApplicativeError[F, Throwable]): F[Int] =
    ae.catchNonFatal(userInput.toInt)
      .adaptErr(_ => InputError.onlyNumbers(userInput))

  def onlyPositiveInteger[F[_]](number: Int)(using ae: ApplicativeError[F, Throwable]): F[Int] =
    ae.raiseWhen(number <= 0)(InputError.onlyPositive(number.toString))
      .as(number)

  val isSatan: [F[_]] => ApplicativeThrow[F] ?=> Int => F[Int] =
    [F[_]] => (ae: ApplicativeThrow[F]) ?=> (number: Int) =>
      if (number == 666) ae.pure(number)
      else ae.raiseError(NotEE)

  val isSatanCall: [F[_]] => ApplicativeThrow[F] ?=> String => F[String] =
    [F[_]] => (ae: ApplicativeThrow[F]) ?=> (userInput: String) =>
      if (userInput == "SATAAAAAAN") ae.pure(userInput)
      else ae.raiseError(NotEE)

  def `teapot?`[F[_]](number: Int)(using ae: ApplicativeError[F, Throwable]): F[Int] =
    if (number == 418) ae.pure(number)
    else ae.raiseError(NotEE)

  def asValidator[F[_], R, S](validate: R => F[S])(validOutput: OutputPath)(using f: Functor[F]): R => F[OutputPath] =
    validate.apply(_).as(validOutput)

  def mapValidator[F[_], R, S](validate: R => F[S])(toValidOutput: S => OutputPath)(using f: Functor[F]): R => F[OutputPath] =
    validate.apply(_).map(toValidOutput)

  def validate[F[_] : MonadThrow]: Validator[F] =
    val positiveInteger = onlyIntNumber[F] >=> onlyPositiveInteger[F]

    val satanCall = asValidator(isSatanCall[F].apply)(SatanCall)
    val satan = asValidator(positiveInteger.andThenF(isSatan[F].apply))(Satan)
    val teapot = asValidator(positiveInteger.andThenF(`teapot?`[F]))(Teapot)
    val positive = mapValidator(positiveInteger)(PositiveInteger.apply)

    Kleisli(satanCall)
      .orElse(Kleisli(satan))
      .orElse(Kleisli(teapot))
      .orElse(Kleisli(positive))
      .run
