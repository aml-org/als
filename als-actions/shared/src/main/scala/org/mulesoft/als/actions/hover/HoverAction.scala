package org.mulesoft.als.actions.hover

import amf.core.metamodel.{Field, Obj}
import amf.core.model.document.BaseUnit
import amf.core.model.domain.{AmfObject, DataNode}
import amf.core.parser
import amf.core.parser.{FieldEntry, Range}
import amf.core.vocabulary.ValueType
import amf.plugins.document.vocabularies.model.document.Dialect
import org.mulesoft.als.common.ObjectInTree
import org.mulesoft.als.common.cache.{ObjectInTreeCached, YPartBranchCached}
import org.mulesoft.als.common.dtoTypes.{Position, PositionRange}
import org.mulesoft.als.convert.LspRangeConverter
import org.mulesoft.amfintegration.AmfInstance
import org.mulesoft.lsp.feature.hover.Hover
import org.yaml.model.YMapEntry
import org.mulesoft.amfintegration.AmfImplicits._
import org.mulesoft.als.common.YamlWrapper._

case class HoverAction(bu: BaseUnit,
                       tree: ObjectInTreeCached,
                       yPartBranchCached: YPartBranchCached,
                       dtoPosition: Position,
                       location: String,
                       amfInstance: AmfInstance,
                       definedBy: Dialect) {

  private val objectInTree: ObjectInTree = tree.getCachedOrNew((dtoPosition, location))

  private val yPartBranch = yPartBranchCached.getCachedOrNew(dtoPosition, location)

  def getHover: Hover = {
    getSemantic
      .map(s => Hover(s._1, s._2.map(r => LspRangeConverter.toLspRange(PositionRange(r))))) // if sequence, we could show all the semantic hierarchy?
      .getOrElse(Hover.empty)
  }

  private def getSemantic: Option[(Seq[String], Option[parser.Range])] = {
    if (objectInTree.obj.isInstanceOf[DataNode]) hackFromNonDynamic()
    else if (isInDeclarationKey) fromDeclarationKey()
    else getPatchedHover.orElse(fromTree())
  }

  private def hackFromNonDynamic(): Option[(Seq[String], Option[parser.Range])] =
    objectInTree.stack.collectFirst({ case obj if !obj.isInstanceOf[DataNode] => obj }).flatMap(classTerm)

  private def fromTree(): Option[(Seq[String], Option[parser.Range])] =
    objectInTree.fieldEntry
      .orElse(objectInTree.fieldValue)
      .flatMap(f => fieldEntry(f))
      .orElse(classTerm(objectInTree.obj))

  private def fieldEntry(f: FieldEntry): Option[(Seq[String], Option[parser.Range])] = {
    propertyTerm(f.field).map(s => (Seq(s), f.value.annotations.range().orElse(f.value.value.annotations.range())))
  }
  private def propertyTerm(field: Field): Option[String] = {
    amfInstance.alsAmlPlugin
      .getSemanticDescription(field.value)
      .orElse({
        if (field.doc.description.nonEmpty) Some(field.doc.description) else None
      })
    // TODO: inherits from another???
  }

  private def getSemanticForMeta(meta: Obj): Seq[String] = {
    val classSemantic = meta.`type`.flatMap { v =>
      amfInstance.alsAmlPlugin.getSemanticDescription(v)
    }
    if (classSemantic.isEmpty && meta.doc.description.nonEmpty) Seq(meta.doc.description)
    else classSemantic
  }

  private def classTerm(obj: AmfObject): Option[(Seq[String], Option[amf.core.parser.Range])] = {
    val finalSemantics = getSemanticForMeta(obj.meta)
    if (finalSemantics.nonEmpty) Some((finalSemantics, obj.annotations.range()))
    else None
  }

  private def getDeclarationValueType(entry: YMapEntry): Option[ValueType] = {
    definedBy.declarationsMapTerms
      .find(_._2 == entry.key.value.toString)
      .map(a => {
        ValueType(a._1)
      })
  }

  def isInDeclarationKey: Boolean =
    bu.annotations.declarationKeys().exists(k => k.entry.key.range.contains(dtoPosition.toAmfPosition))

  private def buildDeclarationKeyUri(name: String): ValueType =
    ValueType(s"http://als.declarationKeys/#${name}DeclarationKey")

  def fromDeclarationKey(): Option[(Seq[String], Option[parser.Range])] =
    bu.annotations
      .declarationKeys()
      .find(k => k.entry.key.range.contains(dtoPosition.toAmfPosition))
      .map(key => {
        val valueType = getDeclarationValueType(key.entry)
        val description = valueType
          .map(
            v =>
              amfInstance.alsAmlPlugin
                .getSemanticDescription(buildDeclarationKeyUri(v.name))
                .getOrElse(s"Contains declarations of reusable ${v.name} objects"))
          .getOrElse({
            s"Contains declarations for ${key.entry.key.value.toString}"
          })

        (Seq(description), Some(Range(key.entry.range)))
      })

  def getPatchedHover: Option[(Seq[String], Option[parser.Range])] =
    patchedHover.getHover(objectInTree.obj, yPartBranch, definedBy)

  private lazy val patchedHover = new PatchedHover(amfInstance.alsAmlPlugin)
}
