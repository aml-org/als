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
import org.mulesoft.als.suggestions.interfaces.{CompletionRequest, Suggestion}
import org.yaml.model.YDocument

class AmlCompletionRequest(override val baseUnit: BaseUnit,
                           override val position: Position,
                           override val actualDialect: Dialect,
                           override val platform: Platform,
                           override val directoryResolver: DirectoryResolver,
                           override val styler: Boolean => Seq[Suggestion] => Seq[Suggestion])
    extends CompletionRequest {

  override lazy val objectInTree: ObjectInTree = ObjectInTreeBuilder.fromUnit(baseUnit, position)

  override lazy val yPartBranch: YPartBranch = {
    val ast = baseUnit match {
      case d: Document =>
        d.encodes.annotations.find(classOf[SourceAST]).map(_.ast)
      case bu => bu.annotations.find(classOf[SourceAST]).map(_.ast)
    }

    NodeBranchBuilder.build(ast.getOrElse(YDocument(IndexedSeq.empty, "")), position)
  }

  override lazy val fieldEntry: Option[FieldEntry] = { // todo: maybe this should be a seq and not an option
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

  override val propertyMapping: Seq[PropertyMapping] = {
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
}

object AmlCompletionRequest {
  def apply(amfPosition: Position,
            bu: BaseUnit,
            dialect: Dialect,
            platform: Platform,
            directoryResolver: DirectoryResolver,
            styler: Boolean => Seq[Suggestion] => Seq[Suggestion]): AmlCompletionRequest =
    new AmlCompletionRequest(bu, amfPosition, dialect, platform, directoryResolver, styler)
}
