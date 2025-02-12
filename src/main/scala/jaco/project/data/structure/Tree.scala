package jaco.project.data.structure

import cats.syntax.all.catsSyntaxOptionId
import monocle.syntax.all.*
import scala.annotation.tailrec

trait Tree[+T]

case class Branch[+T](value: T, leftBranch: Tree[T] = Leaf, rightBranch: Tree[T] = Leaf) extends Tree[T]

case object Leaf extends Tree[Nothing]

object Tree:
  def toTree[T: Ordering](list: List[T]): Tree[T] = fill(list.tail, Branch(list.head))

  private def findRotationStrategy[T](tree: Tree[T]): Option[Either[Branch[T] => Branch[T], Branch[T] => Branch[T]]] =
    val floor = 0
    val (depthL, depthR) = tree match
      case Branch(_, Leaf, Leaf) => floor -> floor
      case Branch(_, branch: Branch[T], Leaf) => countFloor(branch, floor) -> floor
      case Branch(_, Leaf, branch: Branch[T]) => floor -> countFloor(branch, floor)
      case Branch(_, left: Branch[T], right: Branch[T]) => countFloor(left, floor) -> countFloor(right, floor)

    if depthL > (depthR) then Left(rightRotation[T]).some
    else if depthR > (depthL) then Right(leftRotation[T]).some
    else None
  end findRotationStrategy

  private def countFloor[T](tree: Tree[T], floor: Int): Int = tree match
    case Branch(_, Leaf, Leaf) => 1
    case Branch(_, branch: Branch[T], Leaf) => floor + countFloor(branch, floor + 1)
    case Branch(_, Leaf, branch: Branch[T]) => floor + countFloor(branch, floor + 1)
    case Branch(_, left: Branch[T], right: Branch[T]) => floor + Ordering.Int.max(countFloor(left, floor + 1), countFloor(right, floor + 1))
  end countFloor

  def putOnTree[T: Ordering](t: T, inputTree: Tree[T]): Branch[T] =
    inputTree match
      case Leaf => Branch(t)
      case tree: Branch[T] =>
        val resTree: Branch[T] =
          if Ordering[T].gt(t, tree.value) then tree.rightBranch match
            case value: Branch[T] =>
              tree
                .focus(_.rightBranch)
                .replace(putOnTree(t, value))
            case Leaf =>
              tree
                .focus(_.rightBranch)
                .replace(Branch(t))
          else if Ordering[T].lt(t, tree.value) then tree.leftBranch match
            case value: Branch[T] =>
              tree
                .focus(_.leftBranch)
                .replace(putOnTree(t, value))
            case Leaf =>
              tree
                .focus(_.leftBranch)
                .replace(Branch(t))
          else tree
        findRotationStrategy(resTree)
          .fold(resTree)
          (_.fold(_.apply(resTree), _.apply(resTree)))
  end putOnTree

  private def rightRotation[T](tree: Branch[T]): Branch[T] = tree match
    case node@Branch(_, n1: Branch[T], _) =>
      n1
        .focus(_.rightBranch)
        .replace(
          node
            .focus(_.leftBranch)
            .replace(n1.rightBranch)
        )
    case _ => tree
  end rightRotation

  private def leftRotation[T](tree: Branch[T]): Branch[T] = tree match
    case node@Branch(_, _, n2: Branch[T]) =>
      n2
        .focus(_.leftBranch)
        .replace(
          node
            .focus(_.rightBranch)
            .replace(n2.leftBranch)
        )
    case _ => tree
  end leftRotation

  @tailrec private def fill[T: Ordering](list: List[T], tree: Branch[T]): Branch[T] = list match
    case ::(head, next) =>
      fill(next, putOnTree(head, tree))
    case Nil => tree
  end fill

  def printTree[T](tree: Tree[T]): Unit =
    def recursivePrint(tree: Tree[T], depth: Int): Unit =
      val tab = "\t" * depth
      tree match
        case Branch(v, Leaf, Leaf) => println(s"$tab$v")
        case Branch(v, n1: Branch[T], n2: Branch[T]) => println(s"$tab-$v-"); recursivePrint(n1, depth + 1); recursivePrint(n2, depth + 1)
        case Branch(v, n1: Branch[T], Leaf) => println(s"$tab-$v"); recursivePrint(n1, depth + 1)
        case Branch(v, Leaf, n2: Branch[T]) => println(s"$tab$v-"); recursivePrint(n2, depth + 1)

    recursivePrint(tree, 0)
  end printTree

  def findParent[T: Ordering](v1: T, v2: T, tree: Tree[T]): Option[Tree[T]] =
    @tailrec def recursiveSearch(v1: T, v2: T, tree: Tree[T], parent: Tree[T] = Leaf): Option[Tree[T]] =
      val max = Ordering[T].max(v1, v2)
      val min = Ordering[T].min(v1, v2)
      val gt = Ordering[T].gt
      val lt = Ordering[T].lt
      tree match
        case branch@Branch(value, _, _) if gt(max, value) & lt(min, value) => branch.some
        case branch@Branch(value, _, rightBranch) if gt(max, value) & gt(min, value) => recursiveSearch(v1, v2, rightBranch, branch)
        case branch@Branch(value, leftBranch, _) if lt(max, value) & lt(min, value) => recursiveSearch(v1, v2, leftBranch, branch)
        case branch@Branch(value, _, _) => parent.some
        case Leaf => None
    end recursiveSearch

    recursiveSearch(v1, v2, tree)
  end findParent

  @tailrec
  private def findChild[T: Ordering](v: T, tree: Tree[T]): Option[Tree[T]] = tree match
    case branch@Branch(value, leftBranch, rightBranch) if Ordering[T].equiv(value, v) => branch.some
    case branch@Branch(value, leftBranch, rightBranch) if Ordering[T].gt(value, v) => findChild(v, leftBranch)
    case branch@Branch(value, leftBranch, rightBranch) if Ordering[T].lt(value, v) => findChild(v, leftBranch)
    case Leaf => None
  end findChild

end Tree