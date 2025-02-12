package jaco.project

object simpleMain extends App:

  trait Functor[F[_]]:
    def fmap[A, B](f: A => B): F[A] => F[B]

  trait Monad[F[_]]:
    def map[A, B](fa: F[A])(f: A => B): F[B]

    def flatMap[A, B](fa: F[A])(f: A => F[B]): F[B]

  given [X]: Monad[[Y] =>> X => Y] with
    def map[A, B](fa: X => A)(f: A => B): X => B =
      fa.andThen(f)

    def flatMap[A, B](fa: X => A)(f: A => X => B): X => B =
      x => f(fa(x))(x)

  extension [A, B](f: A => B)(using m: Monad[[X] =>> A => X])
    def map[C](f2: B => C): A => C = m.map(f)(f2)
    def flatMap[C](f2: B => A => C): A => C = m.flatMap(f)(f2)
