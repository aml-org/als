package org.mulesoft.amfintegration.amfconfiguration

import amf.aml.client.scala.{AMLConfiguration, AMLDialectResult}
import amf.aml.client.scala.model.document.{Dialect, DialectInstance}
import amf.apicontract.client.scala._
import amf.core.client.scala.model.document.BaseUnit
import amf.core.client.scala.parse.AMFSyntaxParsePlugin
import amf.core.client.scala.resource.ResourceLoader
import amf.core.client.scala.validation.payload.{AMFShapePayloadValidationPlugin, ValidatePayloadRequest}
import amf.core.internal.parser.Root
import amf.core.internal.remote.Spec
import amf.core.internal.unsafe.PlatformSecrets
import org.mulesoft.als.configuration.ProjectConfiguration
import org.mulesoft.amfintegration.dialect.integration.BaseAlsDialectProvider
import org.mulesoft.amfintegration.vocabularies.integration.{
  AlsVocabularyParsingPlugin,
  AlsVocabularyRegistry,
  DefaultVocabularies
}
import org.mulesoft.amfintegration.{AlsSyamlSyntaxPluginHacked, ValidationProfile}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class EditorConfiguration(
    resourceLoader: Seq[ResourceLoader],
    syntaxPlugin: Seq[AMFSyntaxParsePlugin],
    validationPlugin: Seq[AMFShapePayloadValidationPlugin],
    baseAlsDialectProvider: BaseAlsDialectProvider = BaseAlsDialectProvider()) { // todo: add hot reload

  val baseConfiguration: AMLConfiguration = AMLConfiguration
    .predefined()
    .withResourceLoaders(resourceLoader.toList)
    .withPlugins((syntaxPlugin ++ validationPlugin).toList)

  private val inMemoryDialects: Future[Seq[Dialect]] = {
    val rawClient =
      baseConfiguration.withResourceLoader(baseAlsDialectProvider.rawDialectResourceLoader).baseUnitClient()
    Future.sequence(baseAlsDialectProvider.rawDialects.map(rd => rawClient.parseDialect(rd.name).map(_.dialect)))
  }

  private var dialects: Seq[String]                     = Seq.empty // TODO: initial always contains validation profile?
  private var profiles: Seq[String]                     = Seq.empty
  private var vocabularyRegistry: AlsVocabularyRegistry = AlsVocabularyRegistry(DefaultVocabularies.all)

  private def getDialects: Future[Seq[Dialect]] =
    Future.sequence(dialects.map(baseConfiguration.baseUnitClient().parseDialect(_).map(_.dialect)))

  private def getRawAndLocalDialects: Future[Seq[Dialect]] =
    for {
      local <- getDialects
      raw   <- inMemoryDialects
    } yield local ++ raw

  private def getProfiles = {
    getRawAndLocalDialects
      .map(seq => seq.foldLeft(baseConfiguration)((c, dialect) => c.withDialect(dialect)))
      .flatMap { config =>
        Future.sequence(
          profiles.map(
            p =>
              config
                .baseUnitClient()
                .parse(p)
                .map(bu =>
                  ValidationProfile(p, bu.baseUnit.raw.getOrElse(""), bu.baseUnit.asInstanceOf[DialectInstance]))))
      }
  }

  def getState(): Future[EditorConfigurationState] =
    for {
      d <- getRawAndLocalDialects
      p <- getProfiles
    } yield EditorConfigurationState(resourceLoader, d, p, vocabularyRegistry, syntaxPlugin, validationPlugin)
}

case class EditorConfigurationState(resourceLoader: Seq[ResourceLoader],
                                    dialects: Seq[Dialect],
                                    profiles: Seq[ValidationProfile],
                                    vocabularyRegistry: AlsVocabularyRegistry = AlsVocabularyRegistry(
                                      DefaultVocabularies.all),
                                    syntaxPlugin: Seq[AMFSyntaxParsePlugin],
                                    validationPlugin: Seq[AMFShapePayloadValidationPlugin]) {

  def getParsePlugin: Seq[AMFSyntaxParsePlugin] = AlsVocabularyParsingPlugin(vocabularyRegistry)
}

case class GlobalAmfConfiguration(resourceLoaders: Seq[ResourceLoader],
                                  dialectProvider: BaseAlsDialectProvider = BaseAlsDialectProvider(),
                                  vocabularyRegistry: AlsVocabularyRegistry = AlsVocabularyRegistry(
                                    DefaultVocabularies.all))
    extends PlatformSecrets {

  private implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global

  def initialize(): Future[GlobalAmfConfiguration] = {

    global = global
      .withResourceLoaders(resourceLoaders :+ dialectProvider.rawDialectResourceLoader)
      .withSyntaxes(Seq(AlsSyamlSyntaxPluginHacked))
      .withParsePlugin(AlsVocabularyParsingPlugin(vocabularyRegistry))

    Future
      .sequence(
        dialectProvider.rawDialects.map(
          raw =>
            builder()
              .configForSpec(Spec.AML)
              .config
              .baseUnitClient()
              .parseDialect(raw.uri)
              .map(d => synchronized(global = global.withDialect(d.dialect)))))
      .map(_ => this)
  }

  private var global = AMFConfigurationStateManager(Seq.empty, Seq.empty, Seq.empty, Map.empty, Seq.empty, Seq.empty)

  def state(): AMFConfigurationStateManager = global

  def withValidators(plugins: Seq[AMFShapePayloadValidationPlugin]): GlobalAmfConfiguration = {
    global = global.withValidators(plugins)
    this
  }

  def withSyntax(plugins: Seq[AMFSyntaxParsePlugin]): GlobalAmfConfiguration = {
    global = global.withSyntaxes(plugins)
    this
  }

  def withDialect(d: Dialect): GlobalAmfConfiguration = {
    global = global.withDialect(d)
    this
  }

  def withResourceLoader(rl: ResourceLoader): GlobalAmfConfiguration =
    withResourceLoaders(Seq(rl))

  def withResourceLoaders(rl: Seq[ResourceLoader]): GlobalAmfConfiguration = {
    global = global.withResourceLoaders(rl)
    this
  }

  def globalInfo: ProjectConfigurationState = GlobalProjectConfigurationState(global)

  def amfConfiguration: AMFConfiguration = builder().getAmfConfiguration

  def builder(): AmfConfigurationBuilder =
    AmfConfigurationBuilder(global, globalInfo, dialectProvider, vocabularyRegistry)

  def definitionFor(spec: Spec): Option[Dialect] =
    dialectProvider.definitionFor(spec)(amfConfiguration.configurationState())
}

object GlobalAmfConfiguration extends PlatformSecrets {
  implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global

  def apply(): GlobalAmfConfiguration = new GlobalAmfConfiguration(platform.loaders())

  def apply(rls: Seq[ResourceLoader]): GlobalAmfConfiguration = new GlobalAmfConfiguration(rls)
}

sealed class GlobalProjectConfigurationState(override val extensions: Seq[Dialect],
                                             override val profiles: Seq[ValidationProfile],
                                             override val config: ProjectConfiguration)
    extends ProjectConfigurationState(extensions, profiles, config) {
  val cache: Seq[BaseUnit] = Seq.empty
}

object GlobalProjectConfigurationState {
  def apply(g: AMFConfigurationStateManager): ProjectConfigurationState = {
    val projectConfiguration = ProjectConfiguration("", None, Set.empty, g.profiles().keySet, Set.empty, Set.empty)
    val profiles             = g.profiles().map(t => ValidationProfile(t._1, t._2.raw.getOrElse(""), t._2)).toSeq
    new GlobalProjectConfigurationState(g.dialects, profiles, projectConfiguration)
  }
}

// used only in tests?
case class GlobalConfigurationWrapper(global: GlobalAmfConfiguration) {
  implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global
  def parse(url: String): Future[AmfParseResult] = {
    val amfConfig = global.amfConfiguration
    amfConfig.baseUnitClient().parse(url).map { r =>
      new AmfParseResult(
        r,
        global.dialectProvider
          .definitionFor(r.baseUnit)(amfConfig.configurationState())
          .getOrElse(throw new NoDefinitionFoundException(r.baseUnit.id)),
        AmfParseContext(amfConfig, GlobalProjectConfigurationState(global.state()), global.builder())
      )
    }
  }
}
