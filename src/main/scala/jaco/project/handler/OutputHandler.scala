package jaco.project.handler

import cats.FlatMap
import cats.effect.Concurrent
import cats.effect.std.Console
import cats.syntax.all.*
import fs2.io.file.Files
import jaco.project.FileReader
import jaco.project.OutputMessages.{clientOutput, satanCallEE, successMsg}
import jaco.project.client.GithubClient

object OutputHandler:
  
  def outputSatan[F[_] : Files : Concurrent : Console]: PartialFunction[OutputPath, F[Unit]] =
    { case Satan => FileReader.output666File }

  def outputSatanCall[F[_] : Console]: PartialFunction[OutputPath, F[Unit]] =
    { case SatanCall => Console[F].println(satanCallEE) }

  def finishProgram[F[_] : Console]: PartialFunction[OutputPath, F[Unit]] =
    { case PositiveInteger(number) => Console[F].println(successMsg(number)) }
    
  def outputGithub[F[_] : FlatMap : Console](client: GithubClient[F]): PartialFunction[OutputPath, F[Unit]] =
    { case Teapot => client.checkStatus().flatMap(response => Console[F].println(clientOutput(response))) }
