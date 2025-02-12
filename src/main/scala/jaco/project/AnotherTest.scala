package jaco.project

import cats.effect.IO
import cats.effect.IOApp.Simple

import java.util.concurrent.Executors
import scala.concurrent.duration.DurationInt

object AnotherTest extends Simple:
  override def run: IO[Unit] =
    def rec(n: Int = 1): Int =
      if (n % 43175719 == 0) println(s"${Thread.currentThread().getName}: divisivel") else ()
      if (n == Int.MaxValue) 0
      else rec(n + 1)

    val acordei = IO.sleep(500.millis) >> IO.println(s"${Thread.currentThread().getName}: acordei!")

    IO(rec())
      .both(acordei)
      .timeout(800.millis)
      .void

object SomeTest extends App:

  def rec(n: Int = 1): Int =
    if (n % 43175719 == 0) println(s"${Thread.currentThread().getName}: divisivel") else ()
    if (n == Int.MaxValue) 0
    else rec(n + 1)

  val runnable: Runnable = () => {
    Thread.sleep(1000)
    println(s"${Thread.currentThread().getName}: acordei")
  }

//  import scala.concurrent.ExecutionContext.Implicits.global
//  Await.result(
//    Future(Thread.sleep(1000)).map(_ => println("acordei")),
//    1500.millis
//  )
  Executors.newCachedThreadPool().execute(runnable)
  rec()