package org.mulesoft.als.suggestions.plugins.aml.webapi.raml

import amf.aml.client.scala.model.domain.NodeMapping
import amf.apicontract.client.scala.model.domain.{Operation, Request}
import amf.apicontract.internal.metamodel.domain.OperationModel
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.PropertyMappingWrapper
import org.mulesoft.als.suggestions.plugins.aml.categories.CategoryRegistry

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object OperationRequest extends AMLCompletionPlugin {
  override def id: String = "OperationRequestCompletionPlugin"

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    Future {
      request.amfObject match {
        case _: Request
            if request.branchStack.headOption.exists(_.isInstanceOf[Operation]) && request.fieldEntry.isEmpty =>
          val nodeMapping: Option[NodeMapping] = request.actualDialect.declares
            .collect({ case d: NodeMapping => d })
            .find(n =>
              n.nodetypeMapping.option().exists(uri => OperationModel.`type`.headOption.map(_.iri()).contains(uri))
            )
          nodeMapping
            .map(n =>
              n.propertiesMapping()
                .map(p =>
                  p.toRaw(
                    CategoryRegistry(OperationModel.`type`.head.iri(), p.name().value(), request.actualDialect.id)
                  )
                )
            )
            .getOrElse(Nil) // todo use node mapping implicit when category registry is fixed.
        case _ => Nil
      }
    }
  }
}
