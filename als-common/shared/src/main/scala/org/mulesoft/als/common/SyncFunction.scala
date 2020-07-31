package org.mulesoft.als.common

trait SyncFunction {
  def sync[T](fn: () => T): T = synchronized(fn())
}
