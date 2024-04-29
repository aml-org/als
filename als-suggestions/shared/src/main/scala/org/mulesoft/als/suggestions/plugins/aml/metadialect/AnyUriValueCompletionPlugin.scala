package org.mulesoft.als.suggestions.plugins.aml.metadialect

import amf.aml.client.scala.model.domain._
import amf.core.client.scala.model.document.{BaseUnit, DeclaresModel}
import amf.core.client.scala.model.domain.{AmfObject, AmfScalar}
import amf.core.client.scala.vocabulary.Namespace.XsdTypes
import amf.core.internal.parser.domain.{FieldEntry, Value}
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.{PlainText, RawSuggestion, SuggestionStructure}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object AnyUriValueCompletionPlugin extends AMLCompletionPlugin {
  override def id: String = "AnyUriValueCompletionPlugin"

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] =
    if (isInNpReference(request))
      Future {
        val name = currentName(request.amfObject)
        nodeMappings(request.baseUnit)
          .filterNot(_ == name)
          .map(RawSuggestion.apply(_, isAKey = false)) ++ includeSuggestion
      }
    else emptySuggestion

  private def isInNpReference(request: AmlCompletionRequest) =
    isInUriField(request.fieldEntry) || isInReferenceValueFromAst(request)

  private def isInReferenceValueFromAst(request: AmlCompletionRequest) =
    request.amfObject match {
      case x if isNodeMappable(x) => request.astPartBranch.parentEntryIs("extends") && request.astPartBranch.isValue
      case _: PropertyLikeMapping[_] =>
        request.astPartBranch.parentEntryIs("range") && request.astPartBranch.isValue
      case pn: PublicNodeMapping =>
        request.astPartBranch.isValue && pn.mappedNode().value() == "http://amferror.com/#errorNodeMappable/"
      case dm: DocumentMapping =>
        request.astPartBranch.isValue && dm.encoded().value() == "http://amferror.com/#errorNodeMappable/"
      case _ => false
    }

  private def isInUriField(fieldEntry: Option[FieldEntry]) =
    fieldEntry.exists(_.field.`type`.`type`.headOption.exists(_.iri() == XsdTypes.xsdUri.iri()))

  private val includeSuggestion = Seq(
    RawSuggestion("!include ", "!include", "inclusion tag", Seq(), options = SuggestionStructure(rangeKind = PlainText))
  )

  private def currentName(current: AmfObject) =
    current.fields.fields().find(_.field.value.name.toLowerCase() == "name") match {
      case Some(FieldEntry(_, Value(AmfScalar(v, _), _))) => v.toString
      case _                                              => ""
    }

  private def nodeMappings(bu: BaseUnit) =
    bu match {
      case d: DeclaresModel =>
        d.declares
          .collect({
            case nm: UnionNodeMapping => nm.name.option()
            case nm: NodeMapping      => nm.name.option()
//            case nm: AnnotationMapping => nm.name.option() // covered by AMLRamlStyleDeclarationsReferences
          })
          .flatten
      case _ => Nil
    }
}
