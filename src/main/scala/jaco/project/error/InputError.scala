package jaco.project.error

import cats.Show

case class InputError(input: String, reason: String) extends CustomError:
  override def getMessage: String = s"$input tá errado. Motivo: $reason"


object InputError:
  
  given Show[InputError] = Show.show(_.getMessage)

  def onlyNumbers(input: String): InputError = 
    InputError(input, "deveria ser só um número inteiro")
    
  def onlyPositive(number: String): InputError =
    InputError(number, "use só números maiores que 0")

