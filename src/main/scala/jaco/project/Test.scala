package jaco.project

import cats.{Semigroup, Show}
import cats.data.Ior
import monocle.{Getter, Lens}
import cats.syntax.all.*
import cats.syntax.all.toFunctorOps
import io.circe.syntax.*
import io.circe.generic.auto.*

import scala.Function
import scala.annotation.tailrec

object Test {

  trait MappingKey(val str: String)
  case object X extends MappingKey("x")
  case object Y extends MappingKey("y")
  object MappingKey {
    def fromStr(str: String): Either[String, MappingKey] =
      List(X, Y).find(_.str == str).toRight(s"value '$str' isn't mapped!")
  }

  trait Mappable[A, B] {
    type M
    val getA: Getter[A, M]
    val lenB: Lens[B, M]
  }

  type Decider[T <: MappingKey, A, B] = PartialFunction[T, Mappable[A, B]]

  private def deciderNotFound[A, B](key: MappingKey)(classA: Class[A], classB: Class[B]): String =
    s"Can't find a Decider for field '${key.str}' between ${classA.getSimpleName} and ${classB.getSimpleName}"

  type IgnoreOr[B] = Ior[List[String], B]

  def mapKey[T <: MappingKey, A, B](a: A, b: B)(using mappable: Mappable[A, B]): B =
    mappable.lenB.replace(mappable.getA.get(a))(b)

  def mapStringKeys[A, B](keys: List[String])(a: A, b: B)(using decider: Decider[MappingKey, A, B]): IgnoreOr[B] = {
    given Semigroup[B] = Semigroup.first
    @tailrec def recursiveMap(keys: List[String])(a: A, updating: IgnoreOr[B]): IgnoreOr[B] = keys match
      case ::(keyStr, tail) =>
        val updated = updating.flatMap(up =>
            Ior.fromEither(mapStringKey(keyStr)(a, up).leftMap(List(_)))
              .addRight(up)
          )
        recursiveMap(tail)(a, updated)
      case Nil => updating
    recursiveMap(keys)(a, Ior.Right(b))
  }

  def mapStringKey[A, B](key: String)(a: A, b: B)(using decider: Decider[MappingKey, A, B]): Either[String, B] = {
    MappingKey.fromStr(key).flatMap { mappingKey =>
      decider.lift.apply(mappingKey)
        .map(mappable => mapKey(a, b)(using mappable))
        .toRight(deciderNotFound(mappingKey)(a.getClass, b.getClass))
    }
  }
}

@main def main(): Unit =
  import Test.*
  case class X1(x: String, y: String)
  object X1 {
    val decideX: Decider[MappingKey, X1, X2] = {
      case X =>
        new Mappable[X1, X2]:
          override type M = String
          override val getA: Getter[X1, String] = _.x
          override val lenB: Lens[X2, String] = Lens.apply[X2, String](_.x)(x => x2 => x2.copy(x = x))
    }
//    val decideY: Decider[MappingKey, X1, X2] = {
//      case Y =>
//        new Mappable[Y.type, X1, X2]:
//          override type M = String
//          override val getA: Getter[X1, String] = _.y
//          override val lenB: Lens[X2, String] = Lens.apply[X2, String](_.y)(y => x2 => x2.copy(y = y))
//    }

    given Decider[MappingKey, X1, X2] = List(decideX).reduce(_ orElse _)
  }
  case class X2(x: String, y: String)

  val x1 = X1("x1", "y1")
  val x2 = X2("x2", "y2")

  val l = List("key", "x", "y")
  mapStringKeys(l)(x1, x2)
    .bimap(Show[List[String]].show, _.asJson)
    .bimap(println, println)
