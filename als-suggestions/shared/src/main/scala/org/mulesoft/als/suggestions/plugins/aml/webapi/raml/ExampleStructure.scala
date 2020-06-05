package org.mulesoft.als.suggestions.plugins.aml.webapi.raml

import amf.core.model.domain.ObjectNode
import amf.plugins.domain.shapes.models.Example
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml._
import org.mulesoft.amfintegration.dialect.dialects.raml.raml10.Raml10DialectNodes

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object ExampleStructure extends AMLCompletionPlugin {
  override def id: String = "ExampleStructure"

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    Future {
      request.amfObject match {
        case o: ObjectNode if request.branchStack.headOption.exists(_.isInstanceOf[Example]) && withoutProperties(o) =>
          Raml10DialectNodes.ExampleNode.propertiesRaw()
        // ugly hack. How i can know that the only property that exists is from the k: added in the patch?
        case _ => Nil
      }
    }
  }

  private def withoutProperties(o: ObjectNode): Boolean = {
    val names = o.allPropertiesWithName().keys
    names.size == 1 && names.head == "k"
  }
}
