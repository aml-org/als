package org.mulesoft.als.suggestions.aml

import amf.core.annotations.{LexicalInformation, SourceAST, SynthesizedField}
import amf.core.model.document.{BaseUnit, Document}
import amf.core.model.domain.{AmfArray, AmfObject, DomainElement}
import amf.core.parser.FieldEntry
import amf.core.remote.Platform
import amf.plugins.document.vocabularies.model.document.Dialect
import amf.plugins.document.vocabularies.model.domain.{NodeMapping, PropertyMapping}
import org.mulesoft.als.common.AmfSonElementFinder._
import org.mulesoft.als.common.{DirectoryResolver, NodeBranchBuilder, YPartBranch}
import org.mulesoft.als.common.dtoTypes.Position
import org.mulesoft.als.common.{NodeBranchBuilder, ObjectInTree, ObjectInTreeBuilder, YPartBranch}
import org.mulesoft.als.suggestions.aml.declarations.DeclarationProvider
import org.mulesoft.als.suggestions.interfaces.Suggestion
import org.yaml.model.{YDocument, YNode, YType}
class AmlCompletionRequest(val baseUnit: BaseUnit,
                           val position: Position,
                           val actualDialect: Dialect,
                           val styler: Boolean => Seq[Suggestion] => Seq[Suggestion],
                           val yPartBranch: YPartBranch,
                           private val objectInTree: ObjectInTree,
                           val inheritedProvider: Option[DeclarationProvider] = None) {

class AmlCompletionRequest(override val baseUnit: BaseUnit,
                           override val position: Position,
                           override val actualDialect: Dialect,
                           override val platform: Platform,
                           override val directoryResolver: DirectoryResolver,
                           override val styler: Boolean => Seq[Suggestion] => Seq[Suggestion],
  val yPartBranch: YPartBranch,
  private val objectInTree: ObjectInTree,
  val inheritedProvider: Option[DeclarationProvider] = None) {

  lazy val branchStack: Seq[AmfObject] = objectInTree.stack

  lazy val amfObject: AmfObject = objectInTree.obj

  val prefix: String = {
    yPartBranch.node match {
      case node: YNode =>
        node.tagType match {
          case YType.Str =>
            val lines: Iterator[String] = node
              .as[String]
              .lines
              .drop(position.line - node.range.lineFrom)
            if (lines.hasNext)
              lines
                .next()
                .substring(0, position.column - node.range.columnFrom - {
                  if (node.asScalar.exists(_.mark.plain)) 0 else 1 // if there is a quotation mark, adjust the range according
                })
            else ""
          case _ => ""
        }
      case _ => ""
    }
  }

  lazy val fieldEntry: Option[FieldEntry] = { // todo: maybe this should be a seq and not an option
    objectInTree.obj.fields
      .fields()
      .find(f =>
        f.value.value match {
          case _: AmfArray =>
            f.value.annotations
              .find(classOf[LexicalInformation])
              .exists(_.containsCompletely(position))
          case v =>
            v.position()
              .exists(_.contains(position)) &&
              f.value.annotations
                .find(classOf[LexicalInformation])
                .forall(_.containsCompletely(position)) && !f.value.value.annotations
              .contains(classOf[SynthesizedField])
      })
  }

  val propertyMapping: Seq[PropertyMapping] = {
    val mappings = getDialectNode(objectInTree.obj, fieldEntry) match {
      case Some(nm: NodeMapping) => nm.propertiesMapping()
      case _                     => Nil
    }
    fieldEntry match {
      case Some(e) =>
        if (e.value.value
              .position()
              .exists(li => li.contains(position))) {
          val maybeMappings = mappings
            .find(
              pm =>
                pm.fields
                  .fields()
                  .exists(f => f.value.toString == e.field.value.iri()))
            .map(Seq(_))
          maybeMappings
            .getOrElse(mappings)
        } else mappings
      case _ => mappings
    }
  }

  private def getDialectNode(amfObject: AmfObject, fieldEntry: Option[FieldEntry]): Option[DomainElement] =
    actualDialect.declares.find {
      case s: NodeMapping =>
        s.nodetypeMapping.value() == amfObject.meta.`type`.head.iri() &&
          fieldEntry.forall(f => {
            s.propertiesMapping()
              .find(
                pm =>
                  pm.fields
                    .fields()
                    .exists(_.value.toString == f.field.value.iri()))
              .exists(_.mapTermKeyProperty().isNullOrEmpty)
          })
      case _ => false
    }

  lazy val declarationProvider: DeclarationProvider = {
    inheritedProvider.getOrElse(DeclarationProvider(baseUnit, Some(actualDialect)))
  }
}

object AmlCompletionRequestBuilder {

  def build(baseUnit: BaseUnit,
            position: Position,
            dialect: Dialect,
            platform: Platform,
            directoryResolver: DirectoryResolver,
            styler: Boolean => Seq[Suggestion] => Seq[Suggestion]): AmlCompletionRequest = {
    val yPartBranch: YPartBranch = {
      val ast = baseUnit match {
        case d: Document =>
          d.encodes.annotations.find(classOf[SourceAST]).map(_.ast)
        case bu => bu.annotations.find(classOf[SourceAST]).map(_.ast)
      }

      NodeBranchBuilder.build(ast.getOrElse(YDocument(IndexedSeq.empty, "")), position)
    }

    val objectInTree = ObjectInTreeBuilder.fromUnit(baseUnit, position)
    new AmlCompletionRequest(baseUnit, position, dialect, styler, yPartBranch, objectInTree)
  }

  def forElement(element: DomainElement,
                 filterProvider: DeclarationProvider,
                 parent: AmlCompletionRequest): AmlCompletionRequest = {

    val objectInTree = ObjectInTreeBuilder.fromSubTree(
      element,
      parent.position,
      parent.branchStack.splitAt(parent.branchStack.indexOf(element))._2)
    new AmlCompletionRequest(
      parent.baseUnit,
      parent.position,
      parent.actualDialect,
      parent.styler,
      parent.yPartBranch,
      objectInTree,
      Some(filterProvider)
    )
  }
}
