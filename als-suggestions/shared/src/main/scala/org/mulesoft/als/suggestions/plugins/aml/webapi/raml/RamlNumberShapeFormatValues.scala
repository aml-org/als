package org.mulesoft.als.suggestions.plugins.aml.webapi.raml

import amf.core.parser.FieldEntry
import amf.plugins.domain.shapes.metamodel.ScalarShapeModel
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object RamlNumberShapeFormatValues extends AMLCompletionPlugin {
  override def id: String = "RamlNumberShapeFormatValues"

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    Future {
      request.fieldEntry match {
        case Some(FieldEntry(ScalarShapeModel.Format, _)) if request.yPartBranch.isValue =>
          val formats: Seq[String] = Raml10TypesDialect.NumberShapeNode
            .propertiesMapping()
            .find(_.name().option().contains("format"))
            .map(p => p.enum().flatMap(v => v.option().map(_.toString)))
            .getOrElse(Nil)
          formats.map(RawSuggestion(_, isAKey = false))
        case _ => Nil

      }
    }
  }
}
