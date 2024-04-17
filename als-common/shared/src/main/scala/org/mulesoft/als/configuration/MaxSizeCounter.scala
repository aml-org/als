package org.mulesoft.als.configuration

class MaxSizeCounter(val maxSize: Int) {
  private var reminder: Int = maxSize

  def sum(size: Int): Boolean = synchronized {
    reminder -= size
    reminder > 0
  }
}
