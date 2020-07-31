package org.mulesoft.als.suggestions.aml

import amf.core.model.domain.{AmfObject, AmfScalar}
import amf.core.parser.{FieldEntry, Value}
import amf.plugins.document.vocabularies.model.document.Dialect
import amf.plugins.document.vocabularies.model.domain.{NodeMapping, PropertyMapping}
import org.mulesoft.als.common.YPartBranch
import org.mulesoft.amfintegration.AmfImplicits._

import scala.collection.immutable

case class FieldEntrySearcher(amfObject: AmfObject,
                              currentNode: Option[NodeMapping],
                              yPartBranch: YPartBranch,
                              actualDialect: Dialect) {
  val currentIds: immutable.Seq[String] =
    amfObject.metaURIs.flatMap(uri => actualDialect.termsForId.find(_._2 == uri).map(_._1))

  private def findRefferingProperty(mappings: Seq[PropertyMapping]) = {
    mappings.filter(np => currentIds.exists(np.objectRange().map(_.value()).contains)) match {
      case Nil         => filterByName(mappings)
      case head :: Nil => Some(head)
      case many        => filterByName(many).orElse(many.headOption)
    }
  }

  private def filterByName(mappings: Seq[PropertyMapping]) = {
    mappings.find(_.name().value() == yPartBranch.parentEntry.flatMap(_.key.asScalar.map(_.text)).getOrElse(""))
  }

  private def findValueFromTerm(refferingProperty: PropertyMapping) =
    refferingProperty.mapTermKeyProperty().option().flatMap(currentFieldFromTerm)

  private def currentFieldFromTerm(term: String) =
    if (amfObject.fields.fields().exists(_.field.value.iri() == term))
      None // if value ==null anyway should be in objectInTree.FieldEntry
    else currentNode.flatMap(cn => cn.propertiesMapping().find(_.nodePropertyMapping().value() == term))

  def search(father: Option[AmfObject]): Option[(FieldEntry, Boolean)] = {
    for {
      parent <- father
      nm     <- DialectNodeFinder.find(parent, None, actualDialect)
      rp     <- findRefferingProperty(nm.propertiesMapping())
      mm     <- findValueFromTerm(rp)
    } yield (FieldEntry(mm.toField, Value(AmfScalar(""), amfObject.annotations)), true)
  }
}
