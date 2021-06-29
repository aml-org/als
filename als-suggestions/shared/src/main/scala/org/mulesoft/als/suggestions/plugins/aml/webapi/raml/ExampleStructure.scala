package org.mulesoft.als.suggestions.plugins.aml.webapi.raml

import amf.shapes.client.scala.model.domain.Example
import amf.shapes.internal.domain.metamodel.ExampleModel
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
        case e: Example
            if request.fieldEntry.exists(_.field == ExampleModel.Name) && request.yPartBranch.stringValue != e.name
              .option()
              .getOrElse("") && request.yPartBranch.isKey =>
          Raml10DialectNodes.ExampleNode.propertiesRaw(fromDialect = request.actualDialect)
        case _ => Nil
      }
    }
  }
}
