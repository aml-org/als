package org.mulesoft.als.suggestions.plugins.aml.webapi.oas.oas30.agenttopic

import amf.core.client.scala.model.domain.{AmfObject, ObjectNode, ScalarNode}
import amf.core.client.scala.model.domain.extensions.DomainExtension
import org.mulesoft.als.suggestions.{ObjectRange, RangeKind, RawSuggestion, SuggestionStructure}
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin

import scala.concurrent.Future

// TODO: this whole completion plugin should be in `anypoint-als` when done
//  (anything specific to agent topic or the platform should go there, ALS should be agnostic)
object AgentTopicCompletionRegistry {
  def all: Seq[AgentTopicCompletionPlugin] = Seq(
    OperationAgentTopicCompletionPlugin // todo: add other nodes (paramaters/root)
  )
}

trait AgentTopicCompletionPlugin extends AMLCompletionPlugin {
  protected def structure: StructureSuggestionNode

  protected val targetIri: String

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    val fullList = request.amfObject +: request.branchStack
    val targetIndex = fullList.indexWhere(o => o.meta.`type`.headOption.map(_.iri()).contains(targetIri))
    if(targetIndex < 0) emptySuggestion
    else Future.successful(resolveTarget(fullList.splitAt(targetIndex)._1))
  }

  private def resolveTarget(objects: Seq[AmfObject]): Seq[RawSuggestion] =
    checkNext(objects.reverse, structure).map(_.toRawSuggestion)

  private def checkNext(objects: Seq[AmfObject], structure: StructureSuggestionNode): Seq[StructureSuggestionNode] =
    (objects, structure.nested) match {
      case (Nil, _) => Seq(structure) // no more objects written, the current structure is valid as it is
      case (objects, suggestions) =>
        objects.head match {
          case _: ScalarNode => Seq(structure) // writing the last node (still incomplete)
          case extension: DomainExtension =>
            if (extension.name.option().contains(structure.value.stripPrefix("x-")))
              suggestions.flatMap(checkNext(objects.tail, _))
            else Seq.empty // structure does not coincide
          case objectNode: ObjectNode =>
            if(objectNode.propertyFields().map(_.value.name).exists(objectName => objectName == structure.value))
              suggestions.flatMap(checkNext(objects.tail, _))
            else Seq.empty // structure does not coincide
          case _ => checkNext(objects.tail, structure) // still coinciding, searching for most specific
        }
    }

  case class StructureSuggestionNode(value: String, nested: Seq[StructureSuggestionNode] = Seq.empty, range: RangeKind = ObjectRange, isKey: Boolean = true) {
    // todo: make a simple template builder
    // todo: add category

    // todo: in case of wanting to generate CodeActions for this inserts, extract this structure+target to a common module
    def toRawSuggestion: RawSuggestion = RawSuggestion(value, SuggestionStructure(range, isKey))
  }
}