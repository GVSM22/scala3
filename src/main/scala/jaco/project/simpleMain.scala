package jaco.project

import cats.data.State

import scala.concurrent.duration.DurationInt
import cats.effect.{IO, IOApp}
import fs2.Stream
import jaco.project.client.GithubClient
import jaco.project.handler.{InputHandler, OutputHandler}
import jaco.project.validation.InputValidation
import jaco.project.validation.InputValidation.Validator
import org.http4s.ember.client.EmberClientBuilder

object simpleMain extends IOApp.Simple:
  type ErrorOr[T] = Either[Throwable, T]

  override def run: IO[Unit] =
    val cmd = IO.consoleForIO

//    EmberClientBuilder.default[IO]
//      .build
//      .use { client =>
//        val githubClient = GithubClient(client)
//        val validator: Validator[ErrorOr] = InputValidation.validate[ErrorOr]
//        val output = OutputHandler.outputSatanCall[IO]
//          .orElse(OutputHandler.outputSatan[IO])
//          .orElse(OutputHandler.finishProgram[IO])
//          .orElse(OutputHandler.outputGithub(githubClient))
//
//        InputHandler
//          .readUntilValid[IO](OutputMessages.greetMessage, validator)
//          .flatMap(output)
//      }

    
    val list = scala.collection.mutable.ListBuffer.empty[String]
    IO.race(IO.consoleForIO.readLine.map(list.addOne), IO.sleep(1.second))
      .iterateWhile(_.isLeft)
      .map(_ => list)
      .flatMap(IO.println)
