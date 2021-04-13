package org.mulesoft.als.server.workspace

import scala.collection.mutable.ListBuffer

class SynchronizedList[T] {
  private val internal: ListBuffer[T] = ListBuffer()

  def filter(fn: T => Boolean): Seq[T] = internal.filter(fn)

  def +=(item: T): SynchronizedList[T] = {
    this.synchronized {
      internal += item
    }
    this
  }

  def -=(item: T): SynchronizedList[T] = {
    this.synchronized {
      internal -= item
    }
    this
  }

  def clear(): SynchronizedList[T] = {
    this.synchronized {
      internal.clear()
    }
    this
  }

  def :+(item: T): Seq[T] = internal :+ item

  def foreach[U](fn: T => U): Unit = internal.foreach(fn)

  def forall(fn: T => Boolean): Boolean = internal.forall(fn)

  def exists(fn: T => Boolean): Boolean = internal.exists(fn)

  def find(fn: T => Boolean): Option[T] = internal.find(fn)

}

object SynchronizedList {
  def apply[T]() = new SynchronizedList[T]
}
