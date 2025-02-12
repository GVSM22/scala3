package jaco.project.data.structure

import cats.Eq

import scala.annotation.tailrec

object SearchOperations:

  def linear[T : Eq](arr: Array[T], target: T): Option[Int] =
    @tailrec
    def unsafeLoop(position: Int): Int =
      if Eq[T].eqv(arr(position), target) then position
      else unsafeLoop(position + 1)

    try Some(unsafeLoop(0)) catch case _ => None
  end linear

  def binary[T : Eq : Numeric](arr: Array[T], target: T, start: Int, end: Int): Option[Int] =
    val mid = (n1: Int, n2: Int) => (n1 + n2) / 2
    @tailrec
    def loop(first: Int, last: Int): Int =
      val position = mid(first, last)
      val value = arr(position)
      if Eq[T].eqv(value, target) then position
      else
        val (nFirst, nLast) = if Numeric[T].gt(target, value) then position + 1 -> last else first -> (position - 1)
        if nFirst > end || nLast < start then throw new IndexOutOfBoundsException()
        else loop(nFirst, nLast)
    try Some(loop(start, end)) catch case _ => None
  end binary
