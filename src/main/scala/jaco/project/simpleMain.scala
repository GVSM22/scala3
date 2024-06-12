package jaco.project

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
    str.dropRight(1).foldRight(str.last.toString)((c, s) => s.appended(if (s.last.isUpper) c.toLower else c.toUpper) )
  }.printlns

  override def run: IO[Unit] =
    val cmd = IO.consoleForIO

    EmberClientBuilder.default[IO]
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
