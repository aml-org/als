package org.mulesoft.als.suggestions.plugins.aml.webapi.raml.raml10.structure

import amf.plugins.domain.shapes.models.Example
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.ResolveIfApplies
import org.mulesoft.als.suggestions.plugins.aml._
import org.mulesoft.amfintegration.dialect.dialects.raml.raml10.Raml10DialectNodes
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
// TODO: new traverse check deletion
//object DisplayNameExampleNode extends ResolveIfApplies {
//  override def resolve(request: AmlCompletionRequest): Option[Future[Seq[RawSuggestion]]] = {
//    if (request.amfObject.isInstanceOf[Example] && request.yPartBranch.isKeyDescendantOf("example"))
//      Option(Future(Raml10DialectNodes.ExampleNode.propertiesRaw(d = request.actualDialect)))
//    else notApply
//  }
//}
