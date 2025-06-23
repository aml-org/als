package org.mulesoft.als.suggestions.aml

import amf.aml.client.scala.model.domain.{NodeMapping, PropertyMapping}
import amf.core.client.scala.model.document.BaseUnit
import amf.core.client.scala.model.domain.AmfObject
import amf.core.internal.metamodel.domain.common.{NameFieldSchema, NameFieldShacl}
import amf.core.internal.parser.domain.FieldEntry
import org.mulesoft.als.common.ObjectInTree
import org.mulesoft.amfintegration.AmfImplicits.AmfObjectImp
import org.mulesoft.amfintegration.amfconfiguration.DocumentDefinition

case class PropertyMappingFilter(objectInTree: ObjectInTree, actualDocumentDefinition: DocumentDefinition, nm: NodeMapping) {

  private def parentTermKey(): Seq[PropertyMapping] =
    objectInTree.stack.headOption
      .flatMap(DialectNodeFinder.find(_, None, actualDocumentDefinition))
      .collectFirst({ case n: NodeMapping => n })
      .map(_.propertiesMapping())
      .getOrElse(Nil)
      .filter(p => p.mapTermKeyProperty().option().isDefined)

  private def isInDeclarations(nm: NodeMapping): Boolean =
    actualDocumentDefinition
      .documents()
      .map(_.root())
      .exists(root =>
        root.declaredNodes().exists(_.mappedNode().option().contains(nm.id)) && objectInTree.stack.last
          .isInstanceOf[BaseUnit]
      )

  private val semanticNameIris: Seq[String] = Seq(NameFieldSchema.Name.value.iri(), NameFieldShacl.Name.value.iri())

  private def filterSemanticsName(props: Seq[PropertyMapping]) =
    if (isInDeclarations(nm)) props.filterNot(p => semanticNameIris.contains(p.nodePropertyMapping().value()))
    else props

  private def filterNonExplicit(props: Seq[PropertyMapping]): Seq[PropertyMapping] =
    props.filterNot(_.name().isNullOrEmpty)

  private def parentMappingsForTerm(nm: NodeMapping) =
    parentTermKey()
      .find(pr => pr.objectRange().exists(or => or.value() == nm.id))
      .map(p => Seq(p.mapTermKeyProperty().option(), p.mapTermValueProperty().option()).flatten)
      .getOrElse(Nil)

  private def filterForTerms(nm: NodeMapping): Seq[PropertyMapping] = {
    val terms = parentMappingsForTerm(nm)
    if (terms.nonEmpty)
      nm.propertiesMapping()
        .filter(p => !p.nodePropertyMapping().option().exists(terms.contains))
    else nm.propertiesMapping()
  }

  def filter(): Seq[PropertyMapping] = filterNonExplicit(filterSemanticsName(filterForTerms(nm)))

}

object DialectNodeFinder {
  def find(amfObject: AmfObject, fieldEntry: Option[FieldEntry], actualDocumentDefinition: DocumentDefinition): Option[NodeMapping] = {
    val elements = amfObject.metaURIs
      .flatMap { v =>
        actualDocumentDefinition.declares.find {
          case s: NodeMapping =>
            s.nodetypeMapping.value() == v && containsAllFields(s, fieldEntry)
          case _ => false
        }
      }
    elements
      .collectFirst({ case nm: NodeMapping => nm })
      .orElse(findById(amfObject, fieldEntry, actualDocumentDefinition))
  }

  private def containsAllFields(nodeMapping: NodeMapping, fieldEntry: Option[FieldEntry]): Boolean = {
    fieldEntry.forall(f => {
      nodeMapping
        .propertiesMapping()
        .find(pm =>
          pm.fields
            .fields()
            .exists(_.value.toString == f.field.value.iri())
        )
        .exists(_.mapTermKeyProperty().isNullOrEmpty)
    })
  }

  /** Handles the case where the AML definition has no classTerm defined
    */
  private def findById(
                        amfObject: AmfObject,
                        fieldEntry: Option[FieldEntry],
                        actualDocumentDefinition: DocumentDefinition
  ): Option[NodeMapping] = {
    amfObject.metaURIs
      .flatMap { v =>
        actualDocumentDefinition.declares.find {
          case s: NodeMapping =>
            s.id == v && containsAllFields(s, fieldEntry)
          case _ => false
        }
      }
      .collectFirst({ case nm: NodeMapping => nm })
  }

  def find(metaUri: String, actualDocumentDefinition: DocumentDefinition): Option[NodeMapping] =
    actualDocumentDefinition.declares
      .find {
        case s: NodeMapping =>
          s.nodetypeMapping.value() == metaUri
        case _ => false
      }
      .collectFirst({ case nm: NodeMapping => nm })
}
