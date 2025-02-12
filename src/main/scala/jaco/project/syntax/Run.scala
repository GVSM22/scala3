package jaco.project.syntax

object Run:
  extension [A](a: A)
    def run[B](f: A => B): B = f(a)