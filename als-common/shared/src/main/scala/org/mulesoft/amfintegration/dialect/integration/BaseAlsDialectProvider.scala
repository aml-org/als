package org.mulesoft.amfintegration.dialect.integration

import amf.aml.client.scala.model.document.Dialect
import amf.core.client.common.remote.Content
import amf.core.client.scala.lexer.CharSequenceStream
import amf.core.client.scala.resource.ResourceLoader
import org.mulesoft.amfintegration.dialect.dialects.InMemoryDialect
import org.mulesoft.amfintegration.dialect.dialects.asyncapi20.AsyncApi20Dialect
import org.mulesoft.amfintegration.dialect.dialects.metadialect.MetaDialect
import org.mulesoft.amfintegration.dialect.dialects.oas.{OAS20Dialect, OAS30Dialect}
import org.mulesoft.amfintegration.dialect.dialects.raml.raml08.Raml08TypesDialect
import org.mulesoft.amfintegration.dialect.dialects.raml.raml10.Raml10TypesDialect
import org.mulesoft.amfintegration.dialect.dialects.validations.RawValidationProfileDialect

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * @param dialects initialized with the server on startup (for example Web API dialects)
  */
object BaseAlsDialectProvider {
  val apiDialects: Set[Dialect] = Set(
    Raml08TypesDialect(),
    Raml10TypesDialect(),
    OAS20Dialect(),
    OAS30Dialect(),
    AsyncApi20Dialect()
  )

  val allBaseDialects: Set[Dialect] = apiDialects + MetaDialect()

  val rawDialects: Seq[InMemoryDialect] = Seq(RawValidationProfileDialect)

  val globalDialectResourceLoader: ResourceLoader = new ResourceLoader {
    override def fetch(resource: String): Future[Content] = Future {
      rawDialects
        .find(_.uri == resource)
        .orElse(indexedDialects.get(resource))
        .map(_.content)
        .getOrElse(Content(new CharSequenceStream(""), resource))
    }

    override def accepts(resource: String): Boolean =
      rawDialects.exists(_.uri == resource) || indexedDialects.contains(resource)
  }

  private var indexedDialects: Map[String, InMemoryDialect] = Map()

  /**
    * Indexes a global dialect
    */
  def indexDialect(uri: String, content: String): Unit = {
    indexedDialects = indexedDialects + (uri -> IndexedDialect(uri, content))
  }

  sealed case class IndexedDialect(override val uri: String, yaml: String) extends InMemoryDialect {
    override val name: String = uri
  }
}
