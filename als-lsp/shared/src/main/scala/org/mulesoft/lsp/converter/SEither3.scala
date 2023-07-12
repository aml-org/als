package org.mulesoft.lsp.converter

sealed abstract class SEither3[+T1, +T2, +T3]

case class Left[+T1](value: T1)   extends SEither3[T1, Nothing, Nothing]
case class Middle[+T2](value: T2) extends SEither3[Nothing, T2, Nothing]
case class Right[+T3](value: T3)  extends SEither3[Nothing, Nothing, T3]

object Either3 {
  def left[T1](value: T1): SEither3[T1, Nothing, Nothing]   = Left(value)
  def middle[T2](value: T2): SEither3[Nothing, T2, Nothing] = Middle(value)
  def right[T3](value: T3): SEither3[Nothing, Nothing, T3]  = Right(value)
}
