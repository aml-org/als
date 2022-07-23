package org.mulesoft.als.suggestions.plugins.aml.pathnavigation

import amf.core.client.scala.model.document.DeclaresModel
import amf.core.client.scala.model.domain.NamedDomainElement
import amf.shapes.client.scala.model.document.JsonSchemaDocument
import org.mulesoft.als.suggestions.RawSuggestion

import scala.concurrent.Future

object DeclarablePathSuggestor {
  def apply(schema: DeclaresModel, prefix: String): DeclarablePathSuggestor =
    schema match {
      case json: JsonSchemaDocument => JsonSchemaSuggestor(json, prefix)
      case _                        => new DeclarablePathSuggestor(schema, prefix)
    }

}

sealed class DeclarablePathSuggestor(schema: DeclaresModel, prefix: String) extends PathSuggestor {
  override def suggest(): Future[Seq[RawSuggestion]] = {
    val names = schema.declares.flatMap {
      case named: NamedDomainElement => named.name.option()
      case _                         => None
    }
    Future.successful(buildSuggestions(names.map(buildText), prefix))
  }

  def buildText(name: String): String = name
}

sealed case class JsonSchemaSuggestor(schema: JsonSchemaDocument, prefix: String)
    extends DeclarablePathSuggestor(schema, prefix) {
  override def buildText(name: String): String =
    s"definitions/$name"
}
