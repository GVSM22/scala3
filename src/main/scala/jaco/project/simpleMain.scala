package jaco.project

import cats.implicits.*
import cats.effect.{IO, IOApp}
import jaco.project.client.GithubClient
import jaco.project.handler.{InputHandler, OutputHandler}
import jaco.project.validation.InputValidation
import jaco.project.validation.InputValidation.Validator
import org.http4s.ember.client.EmberClientBuilder

object simpleMain extends IOApp.Simple:
  type ErrorOr[T] = Either[Throwable, T]

  override def run: IO[Unit] =
    val cmd = IO.consoleForIO

    EmberClientBuilder.default[IO]
      .build
      .use { client =>
        val githubClient = GithubClient(client)
        val validator: Validator[ErrorOr] = InputValidation.validate[ErrorOr]
        val output = OutputHandler.outputSatanCall[IO]
          .orElse(OutputHandler.outputSatan[IO])
          .orElse(OutputHandler.finishProgram[IO])
          .orElse(OutputHandler.outputGithub(githubClient))

        InputHandler
          .readUntilValid[IO](OutputMessages.greetMessage, validator)
          .flatMap(output)
      }
