package org.mulesoft.als.suggestions.plugins.aml.webapi

import amf.aml.client.scala.model.domain.NodeMapping
import amf.core.client.platform.model.DataTypes
import amf.core.client.scala.model.domain.{AmfObject, AmfScalar}
import amf.core.internal.parser.domain.FieldEntry
import amf.shapes.internal.domain.metamodel.ScalarShapeModel
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait ShapeNumberShapeFormatValues extends AMLCompletionPlugin {

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    Future {
      request.fieldEntry match {
        case Some(FieldEntry(ScalarShapeModel.Format, _)) if request.astPartBranch.isValue =>
          val formats: Seq[String] = dataTypeNodeMapping(getDataType(request.amfObject))
            .propertiesMapping()
            .find(_.name().option().contains("format"))
            .map(p => p.enum().flatMap(v => v.option().map(_.toString)))
            .getOrElse(Nil)
          formats.map(RawSuggestion(_, isAKey = false))
        case _ => Nil

      }
    }
  }

  def getDataType(obj: AmfObject): String =
    obj.fields
      .getValueAsOption(ScalarShapeModel.DataType)
      .map(_.value.asInstanceOf[AmfScalar].value.toString)
      .getOrElse(DataTypes.Integer)

  def dataTypeNodeMapping(dataType: String): NodeMapping
}
