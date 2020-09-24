package org.mulesoft.als.suggestions.plugins.aml.webapi.raml.raml10.structure

import amf.core.model.domain.Shape
import amf.plugins.document.vocabularies.model.domain.NodeMapping
import amf.plugins.domain.webapi.metamodel.OperationModel
import amf.plugins.domain.webapi.models.{Parameter, Payload, Request}
import amf.plugins.domain.webapi.models.security.{SecurityScheme, Settings}
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.ResolveIfApplies
import org.mulesoft.als.suggestions.plugins.aml._
import org.mulesoft.amfintegration.dialect.dialects.raml.RamlDialectNodes
import org.mulesoft.amfintegration.AmfImplicits._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object ResolveShapeAndSecurity extends ResolveIfApplies {
  override def resolve(request: AmlCompletionRequest): Option[Future[Seq[RawSuggestion]]] = {
    request.amfObject match {
      case _: Shape | _: SecurityScheme | _: Settings => applies(Future.successful(Seq()))
      case p: Parameter if p.name.option().isEmpty    => applies(Future.successful(Seq()))
      case p: Payload if p.mediaType.option().isEmpty => applies(Future.successful(Seq()))
      case _: Request =>
        applies(
          Future {
            request.actualDialect
              .findNodeMappingByTerm(OperationModel.`type`.head.iri())
              .map(n => n.propertiesRaw(d = request.actualDialect))
              .getOrElse(Nil)
          }
        )
      case _ => notApply
    }
  }
}
