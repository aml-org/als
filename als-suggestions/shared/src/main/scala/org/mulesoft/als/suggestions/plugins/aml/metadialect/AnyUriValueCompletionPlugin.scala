package org.mulesoft.als.suggestions.plugins.aml.metadialect

import amf.core.model.document.{BaseUnit, DeclaresModel}
import amf.core.model.domain.{AmfObject, AmfScalar}
import amf.core.parser.{FieldEntry, Value}
import amf.core.vocabulary.Namespace.XsdTypes
import amf.plugins.document.vocabularies.model.domain.{
  DocumentMapping,
  NodeMappable,
  PropertyMapping,
  PublicNodeMapping
}
import org.mulesoft.als.suggestions.{PlainText, RawSuggestion, SuggestionStructure}
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object AnyUriValueCompletionPlugin extends AMLCompletionPlugin {
  override def id: String = "AnyUriValueCompletionPlugin"

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    if (isInNpReference(request)) {
      Future {
        val name = currentName(request.amfObject)
        nodeMappings(request.baseUnit)
          .filterNot(_ == name)
          .map(RawSuggestion.apply(_, isAKey = false)) ++ includeSuggestion
      }
    } else emptySuggestion
  }

  private def isInNpReference(request: AmlCompletionRequest) = {
    isInUriField(request.fieldEntry) || isInReferenceValueFromAst(request)
  }

  private def isInReferenceValueFromAst(request: AmlCompletionRequest) = {
    request.amfObject match {
      case nm: NodeMappable    => request.yPartBranch.parentEntryIs("extends") && request.yPartBranch.isValue
      case pm: PropertyMapping => request.yPartBranch.parentEntryIs("range") && request.yPartBranch.isValue
      case pn: PublicNodeMapping =>
        request.yPartBranch.isValue && pn.mappedNode().value() == "http://amferror.com/#errorNodeMappable/"
      case dm: DocumentMapping =>
        request.yPartBranch.isValue && dm.encoded().value() == "http://amferror.com/#errorNodeMappable/"
      case _ => false
    }
  }
  private def isInUriField(fieldEntry: Option[FieldEntry]) =
    fieldEntry.exists(_.field.`type`.`type`.headOption.exists(_.iri() == XsdTypes.xsdUri.iri()))
  private val includeSuggestion = Seq(
    RawSuggestion("!include ",
                  "!include",
                  "inclusion tag",
                  Seq(),
                  options = SuggestionStructure(rangeKind = PlainText)))
  private def currentName(current: AmfObject) = {
    current.fields.fields().find(_.field.value.name.toLowerCase() == "name") match {
      case Some(FieldEntry(_, Value(AmfScalar(v, _), _))) => v.toString
      case _                                              => ""
    }
  }

  private def nodeMappings(bu: BaseUnit) = {
    bu match {
      case d: DeclaresModel => d.declares.collect({ case nm: NodeMappable => nm.name.option() }).flatten
      case _                => Nil
    }
  }
}
