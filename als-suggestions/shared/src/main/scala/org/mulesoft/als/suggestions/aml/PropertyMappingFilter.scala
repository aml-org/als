package org.mulesoft.als.suggestions.aml

import amf.core.metamodel.domain.common.{NameFieldSchema, NameFieldShacl}
import amf.core.model.document.BaseUnit
import amf.core.model.domain.{AmfObject, DomainElement}
import amf.core.parser.FieldEntry
import amf.plugins.document.vocabularies.model.document.Dialect
import amf.plugins.document.vocabularies.model.domain.{NodeMapping, PropertyMapping}
import org.mulesoft.als.common.ObjectInTree
import org.mulesoft.amfintegration.AmfImplicits._

case class PropertyMappingFilter(objectInTree: ObjectInTree, actualDialect: Dialect, nm: NodeMapping) {

  private def parentTermKey(): Seq[PropertyMapping] =
    objectInTree.stack.headOption
      .flatMap(DialectNodeFinder.find(_, None, actualDialect))
      .collectFirst({ case n: NodeMapping => n })
      .map(_.propertiesMapping())
      .getOrElse(Nil)
      .filter(p => p.mapTermKeyProperty().option().isDefined)

  private def isInDeclarations(nm: NodeMapping): Boolean = {
    actualDialect
      .documents()
      .root()
      .declaredNodes()
      .exists(_.mappedNode().option().contains(nm.id)) && objectInTree.stack.last.isInstanceOf[BaseUnit]
  }

  private val semanticNameIris: Seq[String] = Seq(NameFieldSchema.Name.value.iri(), NameFieldShacl.Name.value.iri())

  private def filterSemanticsName(props: Seq[PropertyMapping]) = {
    if (isInDeclarations(nm)) props.filterNot(p => semanticNameIris.contains(p.nodePropertyMapping().value()))
    else props
  }

  private def parentMappingsForTerm(nm: NodeMapping) = {
    parentTermKey()
      .find(pr => pr.objectRange().exists(or => or.value() == nm.id))
      .map(p => Seq(p.mapTermKeyProperty().option(), p.mapTermValueProperty().option()).flatten)
      .getOrElse(Nil)
  }

  private def filterForTerms(nm: NodeMapping): Seq[PropertyMapping] = {
    val terms = parentMappingsForTerm(nm)
    if (terms.nonEmpty)
      nm.propertiesMapping()
        .filter(p => !p.nodePropertyMapping().option().exists(terms.contains))
    else nm.propertiesMapping()
  }

  def filter(): Seq[PropertyMapping] = filterSemanticsName(filterForTerms(nm))

}

object DialectNodeFinder {
  def find(amfObject: AmfObject, fieldEntry: Option[FieldEntry], actualDialect: Dialect): Option[DomainElement] = {
    amfObject.metaURIs.flatMap { v =>
      actualDialect.declares.find {
        case s: NodeMapping =>
          s.nodetypeMapping.value() == v &&
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
    }.headOption
  }
}
