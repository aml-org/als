package org.mulesoft.als.declarations

import amf.apicontract.internal.metamodel.domain.api.BaseApiModel
import amf.core.client.scala.model.document.{BaseUnit, EncodesModel, Module}
import amf.core.client.scala.model.domain.AmfObject
import amf.core.internal.metamodel.document.ModuleModel
import amf.core.internal.utils.InflectorBase.Inflector
import org.mulesoft.als.common.NodeBranchBuilder
import org.mulesoft.als.common.dtoTypes.{Position, PositionRange}
import org.mulesoft.amfintegration.AmfImplicits.{AmfAnnotationsImp, AmfObjectImp, BaseUnitImp}
import org.mulesoft.amfintegration.amfconfiguration.DocumentDefinition
import org.mulesoft.common.client.lexical.ASTElement
import org.yaml.model._

import scala.annotation.tailrec

trait DeclarationCreator {

  def declarationName(amfObject: AmfObject, documentDefinition: DocumentDefinition): Option[String] =
    amfObject
      .declarableKey(documentDefinition)
      .map(_.singularize)
      .map(t => s"new$t")

  def declarationPath(fdp: AmfObject, documentDefinition: DocumentDefinition): Seq[String] =
    Seq(fdp.declarableKey(documentDefinition), declarationPathForDialect(documentDefinition)).flatten

  def declarationPathForDialect(documentDefinition: DocumentDefinition): Option[String] =
    documentDefinition.documents().declarationsPath().option()

  def findExistingKeyPart(bu: BaseUnit, uri: String, keyPath: Seq[String]): Seq[YMapEntry] = {
    val maybePart = bu.references
      .find(_.location().contains(uri))
      .getOrElse(bu)
      .objWithAST
      .flatMap(_.annotations.astElement())
    val entries = getExistingParts(maybePart, keyPath)
    entries
  }

  /** Secuential list for each node in the AST that already exists for the destiny
    *
    * @param maybePart
    * @param keys
    * @return
    */
  def getExistingParts(maybePart: Option[ASTElement], keys: Seq[String]): Seq[YMapEntry] =
    maybePart match {
      case Some(yNode: YNode) => getExistingParts(Some(yNode.value), keys)
      case Some(n: YMap)      => getExistingParts(YNode(n), keys.reverse, Seq.empty)
      case Some(d: YDocument) =>
        getExistingParts(d.node, keys.reverse, Seq.empty)
      case _ => Seq.empty
    }

  /** End position for relevant information elements (Name, Version or Usage)
    * @param baseUnit
    * @return
    */
  def afterInfoNode(baseUnit: BaseUnit, isJson: Boolean): Option[Position] =
    endOfInfo(baseUnit).map { range =>
      val branch = NodeBranchBuilder.build(baseUnit, range.start.toAmfPosition, isJson)
      if (branch.stack.length > 3) // the root level node
        PositionRange(branch.stack(branch.stack.length - 3).location.range).end
      else range.end
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

  private def endOfInfo(baseUnit: BaseUnit): Option[PositionRange] =
    baseUnit match {
      case encodesModel: EncodesModel if Option(encodesModel.encodes).isDefined =>
        encodesModel.encodes.fields
          .fields()
          .collect {
            case f if f.field == BaseApiModel.Version || f.field == BaseApiModel.Name => f
          }
          .map(_.value.annotations)
          .flatMap(_.astElement())
          .map(_.location.range)
          .map(PositionRange(_))
          .reduceOption { (a, b) =>
            if (a.end > b.end)
              a
            else b
          }
      case module: Module =>
        module.fields
          .fields()
          .find(_.field == ModuleModel.Usage)
          .map(_.value.annotations)
          .flatMap(_.astElement())
          .map(_.location.range)
          .map(PositionRange(_))
      case _ => None
    }
}
