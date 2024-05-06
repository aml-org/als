package org.mulesoft.amfintegration.dialect.integration

import amf.aml.client.scala.model.document.Dialect
import amf.core.client.common.remote.Content
import amf.core.client.scala.lexer.CharSequenceStream
import amf.core.client.scala.resource.ResourceLoader
import amf.custom.validation.internal.report.loaders.ProfileDialectLoader
import org.mulesoft.amfintegration.dialect.dialects.InMemoryDialect
import org.mulesoft.amfintegration.dialect.dialects.asyncapi20.AsyncApi20Dialect
import org.mulesoft.amfintegration.dialect.dialects.asyncapi26.AsyncApi26Dialect
import org.mulesoft.amfintegration.dialect.dialects.graphql.GraphQLDialect
import org.mulesoft.amfintegration.dialect.dialects.jsonschema.draft2019.JsonSchemaDraft2019Dialect
import org.mulesoft.amfintegration.dialect.dialects.jsonschema.draft4.JsonSchemaDraft4Dialect
import org.mulesoft.amfintegration.dialect.dialects.jsonschema.draft7.JsonSchemaDraft7Dialect
import org.mulesoft.amfintegration.dialect.dialects.metadialect.MetaDialect
import org.mulesoft.amfintegration.dialect.dialects.oas.{OAS20Dialect, OAS30Dialect}
import org.mulesoft.amfintegration.dialect.dialects.raml.raml08.Raml08TypesDialect
import org.mulesoft.amfintegration.dialect.dialects.raml.raml10.Raml10TypesDialect

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/** @param dialects
  *   initialized with the server on startup (for example Web API dialects)
  */
object BaseAlsDialectProvider {
  lazy val apiDialects: Set[Dialect] = Set(
    Raml08TypesDialect(),
    Raml10TypesDialect(),
    OAS20Dialect(),
    OAS30Dialect(),
    AsyncApi20Dialect(),
    AsyncApi26Dialect(),
    GraphQLDialect(),
    JsonSchemaDraft4Dialect(),
    JsonSchemaDraft7Dialect(),
    JsonSchemaDraft2019Dialect()
  )

  lazy val allBaseDialects: Set[Dialect] = apiDialects + MetaDialect()

  val rawDialects: Seq[Future[Dialect]] = Seq(ProfileDialectLoader.dialect)

  val globalDialectResourceLoader: ResourceLoader = new ResourceLoader {
    override def fetch(resource: String): Future[Content] = Future {
      indexedDialects
        .get(resource)
        .map(_.content)
        .getOrElse(Content(new CharSequenceStream(""), resource))
    }

    override def accepts(resource: String): Boolean =
      indexedDialects.contains(resource)
  }

  private var indexedDialects: Map[String, InMemoryDialect] = Map()

  /** Indexes a global dialect
    */
  def indexDialect(uri: String, content: String): Unit =
    indexedDialects = indexedDialects + (uri -> IndexedDialect(uri, content))

  sealed case class IndexedDialect(override val uri: String, yaml: String) extends InMemoryDialect {
    override val name: String = uri
  }
}
