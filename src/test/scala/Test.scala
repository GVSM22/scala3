import cats.laws.discipline.FunctorTests
import cats.{Eq, Functor, Monoid}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatestplus.scalacheck.Checkers
import org.typelevel.discipline.scalatest.FunSuiteDiscipline

class Test extends AnyFunSuite with FunSuiteDiscipline with Checkers {

  object T:
    given eq[A: Monoid, B]: Eq[A => B] = new cats.Eq[A => B]:
      override def eqv(x: A => B, y: A => B): Boolean =
        x(Monoid[A].empty) == y(Monoid[A].empty)

    given f[Y]: Functor[[X] =>> Y => X] = new Functor[[X] =>> Y => X]:
      override def map[A, B](fa: Y => A)(f: A => B): Y => B = fa andThen f

  import T.given

  checkAll("Function", FunctorTests[[A] =>> Int => A].functor[Int, Int, String])


}
