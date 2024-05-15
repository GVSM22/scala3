package jaco.project

import cats.Show

object OutputMessages:
  
  val greetMessage: String = "oi, eu sou um programa, digite um número inteiro: "
  
  def successMsg[T : Show](result: T): String =
    s"eu sei ler esse número! é o número: ${Show[T].show(result)}"
  
  def failureMsg[T : Show](error: T): String = 
    s"${Show[T].show(error)}\nDigite novamente, mas lembre-se de inserir apenas um número inteiro!"
    
  def satanCallEE: String = "VOCÊ É UM DE NÓS"

  def clientOutput[T : Show](body: T): String =
    s"I'm a teapot:\n${Show[T].show(body)}"