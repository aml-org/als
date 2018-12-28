package org.mulesoft.high.level.dialect

import amf.client.remote.Content
import amf.core.client.ParserConfig
import amf.core.lexer.CharSequenceStream
import amf.internal.environment.Environment
import amf.internal.resource.ResourceLoader
import amf.plugins.document.vocabularies.AMLPlugin
import amf.plugins.document.vocabularies.model.document.Dialect
import org.mulesoft.amf.AmfInitializationHandler
import org.mulesoft.high.level.dialect.dialects.AsyncAPIDialect
import org.mulesoft.typesystem.nominal_interfaces.IDialectUniverse

import scala.collection.mutable.Map
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
object DialectUniversesProvider {

  private val map: Map[String, Map[String, IDialectUniverse]] = Map()

  def getUniverse(d: Dialect): IDialectUniverse = {

    val name    = d.name().value()
    val version = d.version().value()

    var m = map.get(name)
    if (m.isEmpty) {
      m = Some(Map[String, IDialectUniverse]())
      map.put(name, m.get)
    }
    var resultOpt = m.get.get(version)
    if (resultOpt.isEmpty) {
      val u = DialectUniverseBuilder.buildUniverse(d)
      m.get.put(version, u)
      resultOpt = Some(u)
    }
    resultOpt.get
  }

  def buildAndLoadDialects(): Future[Unit] = {
    map.clear()
    AmfInitializationHandler.init().flatMap { _ =>
      {
        val dialectsOpts = LoaderForDialects.rootDialects.map { rd =>
          var dialectCfg = new ParserConfig(
            Some(ParserConfig.PARSE),
            Some(rd),
            Some("AML 1.0"),
            Some("application/yaml"),
            None,
            Some("AMF"),
            Some("application/json+ld")
          )

          AMLPlugin.registry.registerDialect(rd, LoaderForDialects.env)
        }
        Future.sequence(dialectsOpts).map(_.foreach(getUniverse))
      }
    }
  }

}

object LoaderForDialects extends ResourceLoader {
  // todo: use directly dialect file resource location (better for edit) Delete interface and objects.
  private val dialects: Seq[DialectProject] = Seq(AsyncAPIDialect)

  private val dialectsMap: Predef.Map[String, String] = dialects.flatMap(d => d.files).toMap

  val rootDialects: Seq[String] = dialects.map(_.rootUrl)

  override def fetch(resource: String): Future[Content] =
    Future(Content(new CharSequenceStream(dialectsMap(resource)), resource))

  override def accepts(resource: String): Boolean = dialectsMap.contains(resource)

  val env = new Environment(Seq(this))
}
