package org.mulesoft.als.declarations

import amf.aml.client.scala.model.document.Dialect
import amf.core.client.scala.model.document.BaseUnit
import amf.core.client.scala.model.domain.AmfObject
import org.mulesoft.amfintegration.AmfImplicits.{AmfAnnotationsImp, AmfObjectImp, BaseUnitImp}
import amf.core.internal.utils.InflectorBase.Inflector
import org.yaml.model.{YDocument, YMap, YMapEntry, YNode, YPart}

import scala.annotation.tailrec

trait DeclarationCreator {

  def declarationName(amfObject: AmfObject, dialect: Dialect): Option[String] =
    amfObject
      .declarableKey(dialect)
      .map(_.singularize)
      .map(t => s"new$t")

  def declarationPath(fdp: AmfObject, dialect: Dialect): Seq[String] =
    Seq(fdp.declarableKey(dialect), declarationPathForDialect(dialect)).flatten

  def declarationPathForDialect(dialect: Dialect): Option[String] =
    dialect.documents().declarationsPath().option()

  def findExistingKeyPart(bu: BaseUnit, uri: String, keyPath: Seq[String]): Seq[YMapEntry] = {
    val maybePart = bu.references
      .find(_.location().contains(uri))
      .getOrElse(bu)
      .objWithAST
      .flatMap(_.annotations.ast())
    val entries = getExistingParts(maybePart, keyPath)
    entries
  }

  /** Secuential list for each node in the AST that already exists for the destiny
    *
    * @param maybePart
    * @param keys
    * @return
    */
  def getExistingParts(maybePart: Option[YPart], keys: Seq[String]): Seq[YMapEntry] =
    maybePart match {
      case Some(yNode: YNode) => getExistingParts(Some(yNode.value), keys)
      case Some(n: YMap)      => getExistingParts(YNode(n), keys.reverse, Seq.empty)
      case Some(d: YDocument) =>
        getExistingParts(d.node, keys.reverse, Seq.empty)
      case _ => Seq.empty
    }

  @tailrec
  private def getExistingParts(node: YNode, keys: Seq[String], acc: Seq[YMapEntry] = Seq.empty): Seq[YMapEntry] =
    keys match {
      case head :: _ =>
        node.value match {
          case m: YMap =>
            val maybeEntry = m.entries
              .find(_.key.asScalar.exists(_.text == head))
            maybeEntry match { // with match instead of map for tailrec optimization
              case Some(v) => getExistingParts(v.value, keys.tail, acc :+ v)
              case None    => acc
            }
          case _ => acc
        }
      case _ => acc
    }

  @tailrec
  final def nameNotInList(baseName: String, existing: Set[String], c: Option[Int] = None): String = {
    val maybeName = s"$baseName${c.getOrElse("")}"
    if (existing.contains(maybeName))
      nameNotInList(baseName, existing, Some(c.getOrElse(0) + 1))
    else maybeName
  }
}
