package org.mulesoft.als.suggestions

import amf.plugins.document.vocabularies.model.document.Dialect

import scala.collection.mutable

object DialectRegistry {

  private val set: mutable.Set[Dialect] = mutable.Set[Dialect]()

  def update(dialect: Dialect) =
    set += dialect

  def get() =
    set.toSeq

  def get(nameAndVersion: String) =
    set.filter(_.nameAndVersion() == nameAndVersion).toSeq
}
