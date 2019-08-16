package org.mulesoft.high.level.dialect

import amf.ProfileName
import amf.client.remote.Content
import amf.core.client.ParserConfig
import amf.core.lexer.CharSequenceStream
import amf.core.unsafe.PlatformSecrets
import amf.internal.environment.Environment
import amf.internal.resource.ResourceLoader
import amf.plugins.document.vocabularies.AMLPlugin
import amf.plugins.document.vocabularies.model.document.Dialect
import org.mulesoft.high.level.InitOptions
import org.mulesoft.high.level.amfmanager.AmfInitializationHandler
import org.mulesoft.high.level.dialect.dialects.AsyncAPIDialect
import org.mulesoft.typesystem.nominal_interfaces.IDialectUniverse

import scala.collection.mutable
import scala.collection.mutable.Map
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
object DialectUniversesProvider {

  private val map: mutable.Map[String, mutable.Map[String, IDialectUniverse]] =
    mutable.Map()

  def getUniverse(d: Dialect): IDialectUniverse = {

    val name    = d.name().value()
    val version = d.version().value()

    var m = map.get(name)
    if (m.isEmpty) {
      m = Some(mutable.Map[String, IDialectUniverse]())
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

  def buildAndLoadDialects(initOptions: InitOptions): Future[Unit] = {
    map.clear()
    AmfInitializationHandler.init().flatMap { _ =>
      val rl = new ResourceLoader {
        override def fetch(resource: String): Future[Content] =
          Future(
            initOptions.customDialects
              .find(cd => cd.url == resource || cd.customVocabulary.exists(_.url == resource)) match {
              case Some(cd) if cd.url == resource => Content(new CharSequenceStream(cd.content), cd.url, None)
              case Some(cd) =>
                Content(new CharSequenceStream(cd.customVocabulary.get.content), cd.customVocabulary.get.url, None)
            })

        override def accepts(resource: String): Boolean =
          initOptions.customDialects.exists(_.url == resource) || initOptions.customDialects.exists(
            _.customVocabulary.exists(_.url == resource))
      }
      val newEnv = LoaderForDialects.env.add(rl)

      val dialectsOpts =
        (LoaderForDialects.rootDialects.filter(t => initOptions.contains(t._1)) ++ initOptions.customDialects
          .map(c => c.name.profile -> c.url)
          .toMap).map {
          case (_, rd) =>
            var dialectCfg = new ParserConfig(
              Some(ParserConfig.PARSE),
              Some(rd),
              Some("AML 1.0"),
              Some("application/yaml"),
              None,
              Some("AMF"),
              Some("application/json+ld")
            )

            AMLPlugin.registry.registerDialect(rd, newEnv)
        }

      Future.sequence(dialectsOpts).map(_.foreach(getUniverse))
    }
  }

}

// todo makes inheris from amf file resource loader (or create one for shared)
object LoaderForDialects extends ResourceLoader with PlatformSecrets {
  // todo: use directly dialect file resource location (better for edit) Delete interface and objects.
  private val dialects: Seq[DialectConf] = Seq(AsyncAPIDialect)

  private val dialectsMap: Predef.Map[String, String] =
    dialects.flatMap(d => d.files).toMap

  val rootDialects: Predef.Map[ProfileName, String] =
    dialects.map(d => d.profileName -> d.rootUrl).toMap

  override def fetch(resource: String): Future[Content] =
    Future(Content(new CharSequenceStream(dialectsMap(resource)), resource))

  override def accepts(resource: String): Boolean =
    dialectsMap.contains(resource)

  val env = new Environment(Seq(this))
}
