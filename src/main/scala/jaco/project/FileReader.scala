package jaco.project

import cats.FlatMap
import cats.implicits.*
import cats.effect.Concurrent
import cats.effect.std.Console
import fs2.io.file.{Files, Path}
import fs2.text

object FileReader:

  def readFile[F[_]: Files : Concurrent](fileName: String): F[String] =
    val path = Path(s"src/main/resources/$fileName")
    Files[F].readAll(path)
      .through(text.utf8.decode)
      .through(text.lines)
      .compile
      .foldMonoid

  def output666File[F[_] : Files : Concurrent : FlatMap : Console]: F[Unit] =
    readFile[F]("easteregg.txt").flatMap(Console[F].println)
