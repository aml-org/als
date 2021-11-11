package org.mulesoft.amfintegration.amfconfiguration

import amf.aml.client.scala.AMLConfiguration
import amf.aml.client.scala.model.document.Dialect
import amf.apicontract.client.scala.{APIConfiguration, AsyncAPIConfiguration, OASConfiguration, RAMLConfiguration}
import amf.core.client.scala.AMFParseResult
import amf.core.client.scala.model.document.BaseUnit
import amf.core.client.scala.parse.AMFSyntaxParsePlugin
import amf.core.client.scala.resource.ResourceLoader
import amf.core.client.scala.validation.payload.AMFShapePayloadValidationPlugin
import amf.core.internal.remote.Spec
import amf.core.internal.unsafe.PlatformSecrets
import org.mulesoft.als.logger.{EmptyLogger, Logger}
import org.mulesoft.amfintegration.AmfImplicits.DialectInstanceImp
import org.mulesoft.amfintegration.dialect.integration.BaseAlsDialectProvider
import org.mulesoft.amfintegration.vocabularies.integration.{
  AlsVocabularyParsingPlugin,
  AlsVocabularyRegistry,
  DefaultVocabularies
}
import org.mulesoft.amfintegration.{AlsSyamlSyntaxPluginHacked, ValidationProfile}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait EditorConfigurationProvider {
  def getState: Future[EditorConfigurationState]
}

case class EditorConfiguration(resourceLoaders: Seq[ResourceLoader],
                               syntaxPlugins: Seq[AMFSyntaxParsePlugin],
                               validationPlugin: Seq[AMFShapePayloadValidationPlugin],
                               logger: Logger)
    extends EditorConfigurationProvider { // todo: add hot reload

  val baseConfiguration: AMLConfiguration = AMLConfiguration
    .predefined()
    .withResourceLoaders(resourceLoaders.toList)
    .withPlugins(validationPlugin.toList)

  // laziness is necessary because APID RL uses same socket than server for comunication, init must be done for that.
  private lazy val inMemoryDialects: Future[Seq[Dialect]] = {
    val rawClient =
      AMLConfiguration
        .predefined()
        .withResourceLoader(BaseAlsDialectProvider.globalDialectResourceLoader)
        .baseUnitClient()
    Future.sequence(BaseAlsDialectProvider.rawDialects.map(rd => rawClient.parseDialect(rd.uri).map(_.dialect)))
  }

  private var dialects: Seq[String]                = Seq.empty
  private var parsedDialects: Future[Seq[Dialect]] = parseDialects

  private var profiles: Seq[String]                          = Seq.empty
  private var parsedProfiles: Future[Seq[ValidationProfile]] = parseProfiles

  private val vocabularyRegistry: AlsVocabularyRegistry = AlsVocabularyRegistry(DefaultVocabularies.all)

  private def getDialects: Future[Seq[Dialect]] = parsedDialects

  private def getRawAndLocalDialects: Future[Seq[Dialect]] =
    for {
      local <- getDialects
      raw   <- inMemoryDialects
    } yield local ++ raw

  private def parseDialects: Future[Seq[Dialect]] =
    Future.sequence(
      dialects.map(
        baseConfiguration
          .withResourceLoader(BaseAlsDialectProvider.globalDialectResourceLoader)
          .baseUnitClient()
          .parseDialect(_)
          .map(_.dialect)))

  private def parseProfiles: Future[Seq[ValidationProfile]] =
    getRawAndLocalDialects
      .map(seq => seq.foldLeft(baseConfiguration)((c, dialect) => c.withDialect(dialect)))
      .flatMap { config =>
        Future.sequence(
          profiles.map(
            uri =>
              config
                .baseUnitClient()
                .parseDialectInstance(uri)
                .filter(_.dialectInstance.isValidationProfile)
                .map(result => {
                  ValidationProfile(uri, result.baseUnit.raw.getOrElse(""), result.dialectInstance)
                })
          )
        )
      }

  override def getState: Future[EditorConfigurationState] =
    for {
      dialects <- getRawAndLocalDialects
      profiles <- parsedProfiles
    } yield
      EditorConfigurationState(resourceLoaders,
                               dialects,
                               profiles,
                               vocabularyRegistry,
                               syntaxPlugins,
                               validationPlugin)

  def withDialect(uri: String): EditorConfiguration = synchronized {
    dialects = uri +: dialects
    parsedDialects = parseDialects
    logger.debug(s"New editor dialect: $uri", "EditorConfiguration", "indexDialect")
    this
  }

  def withProfile(uri: String): EditorConfiguration = synchronized {
    profiles = uri +: profiles
    parsedProfiles = parseProfiles
    logger.debug(s"New editor validation profile: $uri", "EditorConfiguration", "indexProfile")
    this
  }
}

case class EditorConfigurationState(resourceLoader: Seq[ResourceLoader],
                                    dialects: Seq[Dialect],
                                    profiles: Seq[ValidationProfile],
                                    vocabularyRegistry: AlsVocabularyRegistry = AlsVocabularyRegistry(
                                      DefaultVocabularies.all),
                                    syntaxPlugin: Seq[AMFSyntaxParsePlugin],
                                    validationPlugin: Seq[AMFShapePayloadValidationPlugin]) {

  lazy val alsParsingPlugins = List(AlsSyamlSyntaxPluginHacked, AlsVocabularyParsingPlugin(vocabularyRegistry))

}

object EditorConfiguration extends PlatformSecrets {
  def apply(): EditorConfiguration = withPlatformLoaders(Seq.empty)
  def apply(logger: Logger): EditorConfiguration =
    EditorConfiguration(platform.loaders(), Seq.empty, Seq.empty, logger)

  def withPlatformLoaders(rls: Seq[ResourceLoader]): EditorConfiguration =
    EditorConfiguration(rls ++ platform.loaders(), Seq.empty, Seq.empty, EmptyLogger)

  def withoutPlatformLoaders(rls: Seq[ResourceLoader]): EditorConfiguration =
    EditorConfiguration(rls, Seq.empty, Seq.empty, EmptyLogger)

}

case class EditorConfigurationStateWrapper(state: EditorConfigurationState) {

  private def configure(amlConfiguration: AMLConfiguration): AMLConfiguration = {
    val base = amlConfiguration
      .withResourceLoaders(state.resourceLoader.toList)
      .withPlugins(state.alsParsingPlugins ++ state.syntaxPlugin ++ state.validationPlugin)
    state.dialects.foldLeft(base)((b, d) => b.withDialect(d))
  }

  def configForUnit(bu: BaseUnit): AMLConfiguration = {
    configure(bu.sourceSpec.getOrElse(Spec.AML) match {
      case Spec.RAML10  => RAMLConfiguration.RAML10()
      case Spec.RAML08  => RAMLConfiguration.RAML08()
      case Spec.OAS30   => OASConfiguration.OAS30()
      case Spec.OAS20   => OASConfiguration.OAS20()
      case Spec.ASYNC20 => AsyncAPIConfiguration.Async20()
      case _            => AMLConfiguration.predefined()
    })
  }

  def parse(url: String): Future[AMFParseResult] =
    configure(APIConfiguration.API()).baseUnitClient().parse(url)
}
