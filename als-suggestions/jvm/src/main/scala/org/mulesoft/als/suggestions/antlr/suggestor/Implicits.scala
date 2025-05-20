package org.mulesoft.als.suggestions.antlr.suggestor

import scala.collection.mutable

object Implicits {

  implicit class MutMapImpl[K, T](map: mutable.Map[K, T]) {
    def getOrInitialize(k: K, initialize: () => T): T =
      map.get(k) match {
        case Some(value) => value
        case None =>
          val t = initialize()
          map.put(k, t)
          t
      }
  }

  implicit class MutListImpl[T](list: mutable.ListBuffer[T]) {
    def push(element: T): Unit = list.append(element)

    def pop(): T = {
      list match {
        case stack :+ last =>
          list.remove(stack.size)
          last
      }
    }
  }
}
