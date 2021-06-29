package org.mulesoft.amfintegration

trait AMLRegistry[T] {
  def index(voc: T): Unit
}
