package org.mulesoft.amfintegration.amfconfiguration

import amf.aml.client.scala.AMLConfiguration
import amf.aml.client.scala.model.document.{Dialect, DialectInstanceUnit}
import amf.apicontract.client.scala._
import amf.core.client.common.remote.Content
import amf.core.client.common.transform.PipelineId
import amf.core.client.scala.config.{RenderOptions, UnitCache}
import amf.core.client.scala.model.document.BaseUnit
import amf.core.client.scala.model.domain.DomainElement
import amf.core.client.scala.parse.AMFSyntaxParsePlugin
import amf.core.client.scala.resource.ResourceLoader
import amf.core.client.scala.validation.AMFValidationReport
import amf.core.client.scala.validation.payload.AMFShapePayloadValidationPlugin
import amf.core.client.scala.{AMFGraphConfiguration, AMFResult}
import amf.core.internal.remote._
import amf.core.internal.unsafe.PlatformSecrets
import amf.shapes.client.scala.model.domain.AnyShape
import amf.shapes.client.scala.render.JsonSchemaShapeRenderer
import org.mulesoft.amfintegration.AlsSyamlSyntaxPluginHacked
import org.mulesoft.amfintegration.dialect.integration.BaseAlsDialectProvider
import org.mulesoft.amfintegration.vocabularies.integration.{AlsVocabularyParsingPlugin, AlsVocabularyRegistry, DefaultVocabularies}
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
                                                       val resourceLoaders: Seq[ResourceLoader])
    extends PlatformSecrets {
  private implicit var configuration: AMFConfiguration = initialConfig

  def buildJsonSchema(shape: AnyShape): String =
    JsonSchemaShapeRenderer.buildJsonSchema(shape, configuration)

  def withValidators(plugins: Seq[AMFShapePayloadValidationPlugin]): Unit =
    configuration = configuration.withPlugins(plugins.toList)

  def withSyntax(plugins: Seq[AMFSyntaxParsePlugin]): Unit =
    configuration = configuration.withPlugins(plugins.toList)

  def getConfiguration: AMFConfiguration = configuration

  private val alsCustomPlugins = List(AlsSyamlSyntaxPluginHacked, AlsVocabularyParsingPlugin(alsVocabularyRegistry))
  def init(): Unit = {
    configuration = configuration
      .withPlugins(alsCustomPlugins)
    // should init preset init dialects from DialectProvider?
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
        this.branch
      )
    }

  def report(baseUnit: BaseUnit): Future[AMFValidationReport] = {
    configForUnit(baseUnit).baseUnitClient().validate(baseUnit.cloneUnit())
  }

  def configForUnit(unit: BaseUnit, spec: Spec): AMFGraphConfiguration = {
    spec match {
      case Spec.RAML10  => RAMLConfiguration.RAML10()
      case Spec.RAML08  => RAMLConfiguration.RAML08()
      case Spec.OAS30   => OASConfiguration.OAS30()
      case Spec.OAS20   => OASConfiguration.OAS20()
      case Spec.ASYNC20 => AsyncAPIConfiguration.Async20()
      case Spec.AML if unit.isInstanceOf[DialectInstanceUnit] => // TODO change when Dialect name and version be spec
        definitionFor(unit).map(AMLConfiguration.predefined().withDialect).getOrElse(AMLConfiguration.predefined())
      case Spec.AML => AMLConfiguration.predefined()
      case _        => AMFGraphConfiguration.predefined()
    }
  }

  def configForSpec(spec: Spec): AMLConfiguration = {
    spec match {
      case Spec.RAML10  => RAMLConfiguration.RAML10()
      case Spec.RAML08  => RAMLConfiguration.RAML08()
      case Spec.OAS30   => OASConfiguration.OAS30()
      case Spec.OAS20   => OASConfiguration.OAS20()
      case Spec.ASYNC20 => AsyncAPIConfiguration.Async20()
      case Spec.AML => // TODO change when Dialect name and version be spec
        APIConfiguration.API()
      case instanceSpec: AmlDialectSpec =>
        definitionsFor(instanceSpec.id)
          .map(AMLConfiguration.predefined().withDialect)
          .getOrElse(AMLConfiguration.predefined())
      case _ => AMLConfiguration.predefined()
    }
  }

  def configForDialect(d: Dialect): AMLConfiguration = {
    ProfileMatcher.spec(d) match {
      case Some(Spec.RAML10)  => RAMLConfiguration.RAML10()
      case Some(Spec.RAML08)  => RAMLConfiguration.RAML08()
      case Some(Spec.OAS30)   => OASConfiguration.OAS30()
      case Some(Spec.OAS20)   => OASConfiguration.OAS20()
      case Some(Spec.ASYNC20) => AsyncAPIConfiguration.Async20()
      case Some(Spec.AML)
          if d.location().contains("file://vocabularies/dialects/metadialect.yaml") => // TODO change when Dialect name and version be spec
        APIConfiguration.API()
      case _ => AMLConfiguration.predefined().withDialect(d)
    }
  }

  def configForUnit(unit: BaseUnit): AMFGraphConfiguration = {
    configForUnit(unit, unit.sourceSpec.getOrElse(Spec.AML))
  }

  def resolve(baseUnit: BaseUnit): AMFResult =
    configForUnit(baseUnit).baseUnitClient().transform(baseUnit.cloneUnit(), PipelineId.Cache)

  def asJsonLD(resolved: BaseUnit,
               builder: DocBuilder[_],
               renderOptions: RenderOptions = RenderOptions().withCompactUris.withoutSourceMaps): Unit =
    configuration
      .withRenderOptions(renderOptions)
      .baseUnitClient()
      .renderGraphToBuilder(resolved.cloneUnit(), builder)

  def convertTo(model: BaseUnit, target: Spec, syntax: Option[String]): String = {
    val client = configForUnit(model, target).baseUnitClient()
    val result = client.transform(model.cloneUnit(), PipelineId.Compatibility)
    syntax.map(s => s"application/$s").fold(client.render(result.baseUnit))(s => client.render(result.baseUnit, s))
  }

  def serialize(target: Spec, syntax: String, unit: BaseUnit): String = {
    configForUnit(unit.cloneUnit(), target).baseUnitClient().render(unit, syntax)
  }

  def fullResolution(unit: BaseUnit): AMFResult =
    configForUnit(unit).baseUnitClient().transform(unit.cloneUnit(), PipelineId.Editing)

  def emit(de: DomainElement, definedBy: Dialect): YNode =
    configForDialect(definedBy).elementClient().renderElement(de)

  def emit(de: DomainElement, spec: Spec): YNode = configForSpec(spec).elementClient().renderElement(de)

  /**
    * mutates AMF Configuration in order to register a new dialect
    */
  def registerDialect(d: Dialect): Unit = configuration = configuration.withDialect(d)

  /**
    * mutates AMF Configuration in order to register a new dialect
    */
  def withResourceLoader(rl: ResourceLoader): Unit =
    configuration = configuration.withResourceLoader(rl)

  def branch: AmfConfigurationWrapper =
    new AmfConfigurationWrapper(configuration, alsVocabularyRegistry.branch, alsDialectProvider, resourceLoaders)
}

object AmfConfigurationWrapper {
  def apply(resourceLoaders: Seq[ResourceLoader] = Seq.empty): AmfConfigurationWrapper = {
    val wrapper = new AmfConfigurationWrapper(
      createConfigurations(resourceLoaders),
      AlsVocabularyRegistry(DefaultVocabularies.all),
      BaseAlsDialectProvider(),
      resourceLoaders
    )
    wrapper.init()
    wrapper
  }

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
