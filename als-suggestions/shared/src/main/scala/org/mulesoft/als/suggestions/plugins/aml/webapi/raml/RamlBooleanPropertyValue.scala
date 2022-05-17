package org.mulesoft.als.suggestions.plugins.aml.webapi.raml

import amf.aml.client.scala.model.domain.{NodeMapping, PropertyMapping}
import amf.core.client.scala.model.domain.extensions.PropertyShape
import amf.core.client.scala.vocabulary.Namespace.XsdTypes
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait RamlBooleanPropertyValue extends AMLCompletionPlugin with BooleanSuggestions {
  override def id: String = "RamlBooleanPropertyValue"

  protected def propertyShapeNode: Option[NodeMapping]

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    Future {
      request.branchStack.headOption match {
        case Some(_: PropertyShape) =>
          val maybeMapping: Option[PropertyMapping] = propertyShapeNode
            .flatMap(
              _.propertiesMapping().find(pm =>
                request.yPartBranch.isValue &&
                  request.yPartBranch.parentEntry.exists(_.key.asScalar.exists(_.text == pm.name().value()))
              )
            )
          if (maybeMapping.exists(_.literalRange().option().contains(XsdTypes.xsdBoolean.iri())))
            booleanSuggestions
          else Nil
        case _ => Nil
      }
    }
  }
}
