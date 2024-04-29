package org.mulesoft.amfintegration.amfconfiguration

import amf.aml.client.scala.AMLConfiguration
import amf.aml.client.scala.model.document.Dialect
import amf.apicontract.client.scala.{AsyncAPIConfiguration, OASConfiguration, RAMLConfiguration}
import amf.core.client.scala.model.document.BaseUnit
import amf.core.client.scala.parse.AMFSyntaxParsePlugin
import amf.core.client.scala.resource.ResourceLoader
import amf.core.client.scala.validation.payload.AMFShapePayloadValidationPlugin
import amf.core.internal.remote.Spec
import amf.core.internal.unsafe.PlatformSecrets
import amf.graphql.client.scala.GraphQLConfiguration
import amf.shapes.client.scala.config.JsonSchemaConfiguration
import org.mulesoft.als.logger.Logger
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

case class EditorConfiguration(
    resourceLoaders: Seq[ResourceLoader],
    syntaxPlugins: Seq[AMFSyntaxParsePlugin],
    validationPlugin: Seq[AMFShapePayloadValidationPlugin]
) extends EditorConfigurationProvider { // todo: add hot reload

  val baseConfiguration: AMLConfiguration = AMLConfiguration
    .predefined()
    .withResourceLoaders(resourceLoaders.toList)
    .withPlugins(validationPlugin.toList)

  // laziness is necessary when communicating through socket as the initialization must be done for that.
  private lazy val inMemoryDialects: Future[Seq[Dialect]] =
    Future.sequence(BaseAlsDialectProvider.rawDialects)

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
          .map(_.dialect)
      )
    )

  private def parseProfiles: Future[Seq[ValidationProfile]] =
    getRawAndLocalDialects
      .map(seq => seq.foldLeft(baseConfiguration)((c, dialect) => c.withDialect(dialect)))
      .flatMap { config =>
        Future.sequence(
          profiles.map(uri =>
            config
              .baseUnitClient()
              .parseDialectInstance(uri)
              .filter(_.dialectInstance.isValidationProfile)
              .map(result => {
                ValidationProfile(
                  uri,
                  result.baseUnit.raw.getOrElse(""),
                  result.dialectInstance,
                  config.configurationState().findDialectFor(result.dialectInstance).get
                )
              })
          )
        )
      }

  override def getState: Future[EditorConfigurationState] =
    for {
      dialects <- getRawAndLocalDialects
      profiles <- parsedProfiles
    } yield EditorConfigurationState(
      resourceLoaders,
      dialects,
      profiles,
      vocabularyRegistry,
      syntaxPlugins,
      validationPlugin
    )

  def withDialect(uri: String): EditorConfiguration = synchronized {
    dialects = uri +: dialects
    parsedDialects = parseDialects
    Logger.debug(s"New editor dialect: $uri", "EditorConfiguration", "indexDialect")
    this
  }

  def withProfile(uri: String): EditorConfiguration = synchronized {
    profiles = uri +: profiles
    parsedProfiles = parseProfiles
    Logger.debug(s"New editor validation profile: $uri", "EditorConfiguration", "indexProfile")
    this
  }
}

case class EditorConfigurationState(
    resourceLoader: Seq[ResourceLoader],
    dialects: Seq[Dialect],
    profiles: Seq[ValidationProfile],
    vocabularyRegistry: AlsVocabularyRegistry = AlsVocabularyRegistry(DefaultVocabularies.all),
    syntaxPlugin: Seq[AMFSyntaxParsePlugin],
    validationPlugin: Seq[AMFShapePayloadValidationPlugin]
) {

  def getAmlConfig: AMLConfiguration = {
    dialects
      .foldLeft(AMLConfiguration.predefined())((c, d) => c.withDialect(d))
      .withResourceLoaders(resourceLoader.toList)
  }

  lazy val alsParsingPlugins = List(AlsSyamlSyntaxPluginHacked, AlsVocabularyParsingPlugin(vocabularyRegistry))

}

object EditorConfigurationState {
  def empty: EditorConfigurationState =
    EditorConfigurationState(Nil, Nil, Nil, syntaxPlugin = Nil, validationPlugin = Nil)
}

object EditorConfiguration extends PlatformSecrets {
  def apply(): EditorConfiguration = withPlatformLoaders(Seq.empty)

  def withPlatformLoaders(rls: Seq[ResourceLoader]): EditorConfiguration =
    EditorConfiguration(rls ++ platform.loaders(), Seq.empty, Seq.empty)

  def withoutPlatformLoaders(rls: Seq[ResourceLoader]): EditorConfiguration =
    EditorConfiguration(rls, Seq.empty, Seq.empty)

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
      case Spec.RAML10     => RAMLConfiguration.RAML10()
      case Spec.RAML08     => RAMLConfiguration.RAML08()
      case Spec.OAS30      => OASConfiguration.OAS30()
      case Spec.OAS20      => OASConfiguration.OAS20()
      case Spec.ASYNC20    => AsyncAPIConfiguration.Async20()
      case Spec.GRAPHQL    => GraphQLConfiguration.GraphQL()
      case Spec.JSONSCHEMA => JsonSchemaConfiguration.JsonSchema()
      case _               => AMLConfiguration.predefined()
    })
  }
}
