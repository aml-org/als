package org.mulesoft.amfintegration.dialect.integration

import amf.core.client.common.remote.Content
import amf.core.client.scala.lexer.CharSequenceStream
import amf.core.client.scala.resource.ResourceLoader
import amf.custom.validation.internal.report.loaders.ProfileDialectLoader
import org.mulesoft.amfintegration.amfconfiguration.DocumentDefinition
import org.mulesoft.amfintegration.dialect.InMemoryDocument
import org.mulesoft.amfintegration.dialect.dialects.InMemoryDialect
import org.mulesoft.amfintegration.dialect.dialects.asyncapi20.AsyncApi20Dialect
import org.mulesoft.amfintegration.dialect.dialects.asyncapi26.AsyncApi26Dialect
import org.mulesoft.amfintegration.dialect.dialects.avro.AvroDialect
import org.mulesoft.amfintegration.dialect.dialects.graphql.GraphQLDialect
import org.mulesoft.amfintegration.dialect.dialects.jsonschema.draft2019.JsonSchemaDraft2019Dialect
import org.mulesoft.amfintegration.dialect.dialects.jsonschema.draft4.JsonSchemaDraft4Dialect
import org.mulesoft.amfintegration.dialect.dialects.jsonschema.draft7.JsonSchemaDraft7Dialect
import org.mulesoft.amfintegration.dialect.dialects.metadialect.MetaDialect
import org.mulesoft.amfintegration.dialect.dialects.oas.{OAS20Dialect, OAS30Dialect}
import org.mulesoft.amfintegration.dialect.dialects.raml.raml08.Raml08TypesDialect
import org.mulesoft.amfintegration.dialect.dialects.raml.raml10.Raml10TypesDialect
import org.mulesoft.amfintegration.dialect.jsonschemas.InMemoryJsonSchema

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/** @param dialects
  *   initialized with the server on startup (for example Web API dialects)
  */
object BaseAlsDefinitionsProvider {
  lazy val apiDefinitions: Set[DocumentDefinition] = Set(
    Raml08TypesDialect(),
    Raml10TypesDialect(),
    OAS20Dialect(),
    OAS30Dialect(),
    AsyncApi20Dialect(),
    AsyncApi26Dialect(),
    GraphQLDialect(),
    JsonSchemaDraft4Dialect(),
    JsonSchemaDraft7Dialect(),
    JsonSchemaDraft2019Dialect(),
    AvroDialect()
  ).map(DocumentDefinition(_))

  lazy val allBaseDefinitions: Set[DocumentDefinition] = apiDefinitions + DocumentDefinition(MetaDialect())

  val rawDefinitions: Seq[Future[DocumentDefinition]] = Seq(ProfileDialectLoader.dialect.map(DocumentDefinition(_)))

  val globalDefinitionsResourceLoader: ResourceLoader = new ResourceLoader {
    override def fetch(resource: String): Future[Content] = Future {
      indexedDefinitions
        .get(resource)
        .map(_.content)
        .getOrElse(Content(new CharSequenceStream(""), resource))
    }

    override def accepts(resource: String): Boolean =
      indexedDefinitions.contains(resource)
  }

  private var indexedDefinitions: Map[String, InMemoryDocument] = Map()

  /** Indexes a global dialect
    */
  def indexDialect(uri: String, content: String): Unit =
    indexedDefinitions = indexedDefinitions + (uri -> indexedInMemoryDocument(uri, content))

  private def indexedInMemoryDocument(uri: String, content: String): InMemoryDocument = {
    // todo: this method to determine if the file is json is used everywhere, should be moved to a common place
    if (uri.toLowerCase().endsWith(".json")) IndexedJsonSchema(uri, content)
    else IndexedDialect(uri, content)
  }

  private sealed case class IndexedDialect(override val uri: String, fileContent: String) extends InMemoryDialect {
    override val name: String = uri
  }
  private sealed case class IndexedJsonSchema(override val uri: String, fileContent: String) extends InMemoryJsonSchema {
    override val name: String = uri
  }
}
