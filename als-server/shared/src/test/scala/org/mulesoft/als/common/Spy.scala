package org.mulesoft.als.common

abstract class Spy[T] {
  private var flag       = false
  def passed(): Unit     = flag = true
  def hasPassed: Boolean = flag

  def innerEvaluate(param: T): Boolean
  def evaluate(param: T): T = {
    if (innerEvaluate(param)) passed()
    param
  }
}
