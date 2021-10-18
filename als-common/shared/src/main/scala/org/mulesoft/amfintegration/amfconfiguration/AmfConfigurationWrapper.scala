package org.mulesoft.amfintegration.amfconfiguration

import amf.aml.client.scala.AMLConfiguration
import amf.aml.client.scala.model.document.Dialect
import amf.apicontract.client.scala._
import amf.core.client.common.remote.Content
import amf.core.client.common.transform.PipelineId
import amf.core.client.scala.AMFResult
import amf.core.client.scala.config.{RenderOptions, UnitCache}
import amf.core.client.scala.model.document.BaseUnit
import amf.core.client.scala.model.domain.DomainElement
import amf.core.client.scala.parse.AMFSyntaxParsePlugin
import amf.core.client.scala.resource.ResourceLoader
import amf.core.client.scala.validation.AMFValidationReport
import amf.core.client.scala.validation.payload.AMFShapePayloadValidationPlugin
import amf.core.internal.remote._
import amf.core.internal.unsafe.PlatformSecrets
import amf.shapes.client.scala.model.domain.AnyShape
import amf.shapes.client.scala.render.JsonSchemaShapeRenderer
import org.mulesoft.als.configuration.WithWorkspaceConfiguration
import org.mulesoft.amfintegration.AlsSyamlSyntaxPluginHacked
import org.mulesoft.amfintegration.AmfImplicits.BaseUnitImp
import org.mulesoft.amfintegration.dialect.integration.BaseAlsDialectProvider
import org.mulesoft.amfintegration.vocabularies.integration.{
  AlsVocabularyParsingPlugin,
  AlsVocabularyRegistry,
  DefaultVocabularies
}
import org.yaml.builder.DocBuilder
import org.yaml.model.YNode

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * @param initialConfig instance of the current AMF Configuration, will mutate during lifespan with each new Dialect Registration
  * @param alsVocabularyRegistry Vocabularies captured during this instances lifespan
  * @param alsDialectProvider Dialects initialized with the ALS Server
  * @param platform
  */
class AmfConfigurationWrapper private[amfintegration] (private val initialConfig: AMFConfiguration,
                                                       val alsVocabularyRegistry: AlsVocabularyRegistry,
                                                       val alsDialectProvider: BaseAlsDialectProvider,
                                                       val resourceLoaders: Seq[ResourceLoader],
                                                       amfConfigurationState: Option[AMFConfigurationStateManager] =
                                                         None)
    extends PlatformSecrets
    with WithWorkspaceConfiguration {

  private var customValidationProfiles: Map[String, BaseUnit] = Map()

  def profiles(): Map[String, BaseUnit] = customValidationProfiles

  def cleanValidationProfiles(): Unit =
    customValidationProfiles = Map()

  def registerValidationProfile(unit: BaseUnit): Unit =
    customValidationProfiles = customValidationProfiles + ((unit.identifier, unit))

  def withProfiles(units: Seq[BaseUnit]): this.type = {
    units.foreach(registerValidationProfile)
    this
  }

  private implicit var configuration: AMFConfiguration = initialConfig

  private var innerAmfConfigurationState: AMFConfigurationStateManager =
    amfConfigurationState.getOrElse(
      AMFConfigurationStateManager(Nil, Nil, configurationState.getDialects(), resourceLoaders))

  def buildJsonSchema(shape: AnyShape): String =
    JsonSchemaShapeRenderer.buildJsonSchema(shape, configuration)

  def withValidators(plugins: Seq[AMFShapePayloadValidationPlugin]): Unit = {
    innerAmfConfigurationState = innerAmfConfigurationState.withValidators(plugins)
    configuration = configuration.withPlugins(plugins.toList)
  }

  def withSyntax(plugins: Seq[AMFSyntaxParsePlugin]): Unit = {
    innerAmfConfigurationState = innerAmfConfigurationState.withSyntaxes(plugins)
    configuration = configuration.withPlugins(plugins.toList)
  }

  def getConfiguration: AMFConfiguration = configuration

  private val alsCustomPlugins = List(AlsSyamlSyntaxPluginHacked, AlsVocabularyParsingPlugin(alsVocabularyRegistry))

  def init(): Future[AmfConfigurationWrapper] = {
    configuration = configuration
      .withPlugins(alsCustomPlugins)
    Future
      .sequence(alsDialectProvider.rawDialects.map(raw => {
        innerAmfConfigurationState
          .configForSpec(Spec.AML)
          .baseUnitClient()
          .parseDialect(raw.uri)
          .map(d => registerDialect(d.dialect))
      }))
      .map(_ => {
        this
      })
  }

  def useCache(cache: UnitCache): Unit =
    configuration = configuration.withUnitCache(cache)

  private implicit def configurationState: AMFConfigurationState = configuration.configurationState()

  def dialects: Set[Dialect] =
    configurationState.getDialects().toSet ++ alsDialectProvider.dialects

  def semanticKeysFor(uri: String): Seq[String] =
    configurationState
      .findSemanticByTarget(uri)
      .flatMap(_._2)
      .flatMap(_.extensionName().option())
      .toSeq

  def definitionFor(bu: BaseUnit): Option[Dialect] = alsDialectProvider.definitionFor(bu)
  def definitionFor(spec: Spec): Option[Dialect]   = alsDialectProvider.definitionFor(spec)

  def definitionsFor(nameAndVersion: String): Option[Dialect] = alsDialectProvider.definitionFor(nameAndVersion)

  def baseUnitClient: AMFBaseUnitClient = configuration.baseUnitClient()
  def elementClient: AMFElementClient   = configuration.elementClient()

  def fetchContent(uri: String): Future[Content] =
    platform.fetchContent(uri, configuration)

  def parse(url: String): Future[AmfParseResult] =
    baseUnitClient.parse(url).map { r =>
      new AmfParseResult(r,
                         alsDialectProvider
                           .definitionFor(r.baseUnit)
                           .getOrElse(throw new NoDefinitionFoundException(r.baseUnit.id)),
                         this.branch)
    }

  def report(baseUnit: BaseUnit): Future[AMFValidationReport] =
    innerAmfConfigurationState.configForUnit(baseUnit).baseUnitClient().validate(baseUnit.cloneUnit())

  def resolve(baseUnit: BaseUnit): AMFResult =
    innerAmfConfigurationState
      .configForUnit(baseUnit)
      .baseUnitClient()
      .transform(baseUnit.cloneUnit(), PipelineId.Cache)

  def asJsonLD(resolved: BaseUnit,
               builder: DocBuilder[_],
               renderOptions: RenderOptions = RenderOptions().withCompactUris.withoutSourceMaps): Unit =
    configuration
      .withRenderOptions(renderOptions)
      .baseUnitClient()
      .renderGraphToBuilder(resolved.cloneUnit(), builder)

  def convertTo(model: BaseUnit, target: Spec, syntax: Option[String]): String = {
    val client = innerAmfConfigurationState.configForSpec(target).baseUnitClient()
    val result = client.transform(model.cloneUnit(), PipelineId.Compatibility)
    syntax.map(s => s"application/$s").fold(client.render(result.baseUnit))(s => client.render(result.baseUnit, s))
  }

  def serialize(target: Spec, syntax: String, unit: BaseUnit): String =
    innerAmfConfigurationState.configForSpec(target).baseUnitClient().render(unit, syntax)

  def fullResolution(unit: BaseUnit): AMFResult = {
    innerAmfConfigurationState
      .configForUnit(unit)
      .baseUnitClient() match {
      case amf: AMFBaseUnitClient =>
        amf
          .transform(unit.cloneUnit(), PipelineId.Editing)
      case aml =>
        aml
          .transform(unit.cloneUnit(), PipelineId.Default)
    }
  }

  def emit(de: DomainElement, definedBy: Dialect): YNode =
    innerAmfConfigurationState.configForDialect(definedBy).elementClient().renderElement(de)

  def emit(de: DomainElement, spec: Spec): YNode =
    innerAmfConfigurationState.configForSpec(spec).elementClient().renderElement(de)

  /**
    * mutates AMF Configuration in order to register a new dialect
    */
  def registerDialect(d: Dialect): Unit = configuration = {
    val c = configuration.withDialect(d)
    innerAmfConfigurationState = innerAmfConfigurationState.setDialects(c.configurationState().getDialects())
    c
  }

  /**
    * mutates AMF Configuration in order to register a new dialect
    */
  def withResourceLoader(rl: ResourceLoader): Unit = {
    innerAmfConfigurationState = innerAmfConfigurationState.withResourceLoaders(Seq(rl))
    configuration = configuration.withResourceLoader(rl)
  }

  def branch: AmfConfigurationWrapper =
    new AmfConfigurationWrapper(configuration,
                                alsVocabularyRegistry.branch,
                                alsDialectProvider,
                                resourceLoaders,
                                Some(innerAmfConfigurationState))
      .withWorkspaceConfiguration(workspaceConfiguration)
      .withProfiles(profiles().values.toSeq)
}

object AmfConfigurationWrapper {
  // Should only be used when something else will await the init() future (TextDocumentContainer typically)
  def apply(resourceLoaders: Seq[ResourceLoader], withRawDialects: Boolean = true): AmfConfigurationWrapper = {
    val provider = BaseAlsDialectProvider()
    val loaders  = if (withRawDialects) resourceLoaders :+ provider.rawDialectResourceLoader else resourceLoaders
    val wrapper = new AmfConfigurationWrapper(
      createConfigurations(loaders),
      AlsVocabularyRegistry(DefaultVocabularies.all),
      provider,
      loaders,
      None
    )
    wrapper.init()
    wrapper
  }

  def apply(): Future[AmfConfigurationWrapper] =
    apply(Seq.empty).init()

  private def createConfigurations(resourceLoaders: Seq[ResourceLoader]): AMFConfiguration =
    configurationWithResourceLoaders(APIConfiguration.API(), resourceLoaders)

  private def configurationWithResourceLoaders(configuration: AMFConfiguration,
                                               resourceLoaders: Seq[ResourceLoader]): AMFConfiguration =
    resourceLoaders.foldLeft(configuration)((conf, rl) => conf.withResourceLoader(rl))

  def resourceLoaderForFile(fileUrl: String, content: String): ResourceLoader = new ResourceLoader {
    override def accepts(resource: String): Boolean = resource == fileUrl

    override def fetch(resource: String): Future[Content] =
      Future.successful(new Content(content, fileUrl))
  }
}

case class AMFConfigurationStateManager(validators: Seq[AMFShapePayloadValidationPlugin],
                                        syntaxes: Seq[AMFSyntaxParsePlugin],
                                        dialects: Seq[Dialect],
                                        resourceLoaders: Seq[ResourceLoader]) {

  def withValidators(v: Seq[AMFShapePayloadValidationPlugin]): AMFConfigurationStateManager =
    AMFConfigurationStateManager(validators ++ v, syntaxes, dialects, resourceLoaders)

  def withSyntaxes(s: Seq[AMFSyntaxParsePlugin]): AMFConfigurationStateManager =
    AMFConfigurationStateManager(validators, syntaxes ++ s, dialects, resourceLoaders)

  def setDialects(d: Seq[Dialect]): AMFConfigurationStateManager =
    AMFConfigurationStateManager(validators, syntaxes, d, resourceLoaders)

  def withResourceLoaders(r: Seq[ResourceLoader]): AMFConfigurationStateManager =
    AMFConfigurationStateManager(validators, syntaxes, dialects, resourceLoaders ++ r)

  private def predefinedWithDialects: AMLConfiguration = {
    var conf = AMLConfiguration.predefined()
    dialects.foreach(d => conf = conf.withDialect(d))
    conf
  }

  def configForSpec(spec: Spec): AMLConfiguration =
    (spec match {
      case Spec.RAML10  => RAMLConfiguration.RAML10()
      case Spec.RAML08  => RAMLConfiguration.RAML08()
      case Spec.OAS30   => OASConfiguration.OAS30()
      case Spec.OAS20   => OASConfiguration.OAS20()
      case Spec.ASYNC20 => AsyncAPIConfiguration.Async20()
      case _            => predefinedWithDialects
    }).withPlugins((syntaxes ++ validators).toList).withLoaders(resourceLoaders.toList)

  def configForDialect(d: Dialect): AMLConfiguration =
    (ProfileMatcher.spec(d) match {
      case Some(Spec.RAML10)  => RAMLConfiguration.RAML10()
      case Some(Spec.RAML08)  => RAMLConfiguration.RAML08()
      case Some(Spec.OAS30)   => OASConfiguration.OAS30()
      case Some(Spec.OAS20)   => OASConfiguration.OAS20()
      case Some(Spec.ASYNC20) => AsyncAPIConfiguration.Async20()
      case Some(Spec.AML)
          if d.location().contains("file://vocabularies/dialects/metadialect.yaml") => // TODO change when Dialect name and version be spec
        APIConfiguration.API()
      case _ =>
        predefinedWithDialects
    }).withPlugins((syntaxes ++ validators).toList).withLoaders(resourceLoaders.toList)

  def configForUnit(unit: BaseUnit): AMLConfiguration =
    configForSpec(unit.sourceSpec.getOrElse(Spec.AML))

  implicit class AMLConfigurationImp(c: AMLConfiguration) {
    def withLoaders(l: Seq[ResourceLoader]): AMLConfiguration = {
      var r = c
      l.foreach(rl => r = r.withResourceLoader(rl))
      r
    }
  }
}
