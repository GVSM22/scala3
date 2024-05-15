package jaco.project.handler

sealed trait OutputPath
case class PositiveInteger(number: Int) extends OutputPath
object Satan extends OutputPath
object SatanCall extends OutputPath
object Teapot extends OutputPath
