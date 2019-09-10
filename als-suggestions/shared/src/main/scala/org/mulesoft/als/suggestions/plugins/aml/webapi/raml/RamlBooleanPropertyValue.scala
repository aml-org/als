package org.mulesoft.als.suggestions.plugins.aml.webapi.raml

import amf.core.model.domain.extensions.PropertyShape
import amf.core.vocabulary.Namespace.XsdTypes
import amf.plugins.document.vocabularies.model.domain.{NodeMapping, PropertyMapping}
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait RamlBooleanPropertyValue extends AMLCompletionPlugin {
  override def id: String = "RamlBooleanPropertyValue"

  protected def propertyShapeNode: Option[NodeMapping]

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    Future {
      request.branchStack.headOption match {
        case Some(_: PropertyShape) =>
          val maybeMapping: Option[PropertyMapping] = propertyShapeNode
            .flatMap(_.propertiesMapping().find(pm =>
              request.yPartBranch.parentEntry.exists(_.key.asScalar.exists(_.text == pm.name().value()))))
          if (maybeMapping.exists(_.literalRange().option().contains(XsdTypes.xsdBoolean.iri())))
            Seq("true", "false").map(RawSuggestion(_, isAKey = false))
          else Nil
        case _ => Nil
      }
    }
  }
}
