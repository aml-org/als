package org.mulesoft.amfintegration.amfconfiguration
import amf.aml.client.scala.model.document.{Dialect, DialectInstanceUnit, Vocabulary}
import amf.aml.client.scala.model.domain.{AnnotationMapping, SemanticExtension}
import amf.aml.client.scala.{AMLConfiguration, AMLConfigurationState}
import amf.apicontract.client.scala._
import amf.core.client.common.remote.Content
import amf.core.client.scala.config.{CachedReference, RenderOptions, UnitCache}
import amf.core.client.scala.model.document.{BaseUnit, ExternalFragment}
import amf.core.client.scala.resource.ResourceLoader
import amf.core.client.scala.{AMFParseResult => AMFParsingResult}
import amf.core.internal.remote.{AmlDialectSpec, Spec}
import amf.core.internal.unsafe.PlatformSecrets
import amf.shapes.client.scala.model.domain.AnyShape
import amf.shapes.client.scala.render.JsonSchemaShapeRenderer
import org.mulesoft.amfintegration.ValidationProfile
import org.mulesoft.amfintegration.dialect.dialects.ExternalFragmentDialect
import org.mulesoft.amfintegration.dialect.dialects.metadialect.{MetaDialect, VocabularyDialect}
import org.mulesoft.amfintegration.dialect.integration.BaseAlsDialectProvider
import org.yaml.builder.DocBuilder

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Capable of building different AMF configurations from a frozen state
  * @param state the frozen state
  */
case class ALSConfigurationState(editorState: EditorConfigurationState,
                                 projectState: ProjectConfigurationState,
                                 editorResourceLoader: Option[ResourceLoader])
    extends PlatformSecrets {

  lazy val amfParseContext: AmfParseContext = AmfParseContext(getAmfConfig, this)

  def configForUnit(unit: BaseUnit): AMLSpecificConfiguration =
    configForSpec(unit.sourceSpec.getOrElse(Spec.AML))

  def configForDialect(d: Dialect): AMLSpecificConfiguration =
    ProfileMatcher.spec(d) match {
      case Some(Spec.AML)
          if d.location().contains("file://vocabularies/dialects/metadialect.yaml") => // TODO change when Dialect name and version be spec
        AMLSpecificConfiguration(APIConfiguration.API())
      case Some(spec) => configForSpec(spec)
      case _          => AMLSpecificConfiguration(predefinedWithDialects)
    }

  def configForSpec(spec: Spec): AMLSpecificConfiguration =
    AMLSpecificConfiguration(getAmlConfig(spec match {
      case Spec.RAML10  => RAMLConfiguration.RAML10()
      case Spec.RAML08  => RAMLConfiguration.RAML08()
      case Spec.OAS30   => OASConfiguration.OAS30()
      case Spec.OAS20   => OASConfiguration.OAS20()
      case Spec.ASYNC20 => AsyncAPIConfiguration.Async20()
      case _            => predefinedWithDialects
    }))

  def getAmfConfig: AMFConfiguration = getAmfConfig(APIConfiguration.API())

  def allDialects: Seq[Dialect]        = dialects ++ BaseAlsDialectProvider.allBaseDialects
  def dialects: Seq[Dialect]           = projectState.extensions ++ editorState.dialects
  def profiles: Seq[ValidationProfile] = projectState.profiles ++ editorState.profiles

  private def predefinedWithDialects: AMLConfiguration =
    dialects.foldLeft(AMLConfiguration.predefined())((c, d) => c.withDialect(d))

  val cache: UnitCache = new UnitCache {
    val map: Map[String, BaseUnit] = projectState.cache.map(bu => bu.location().getOrElse(bu.id) -> bu).toMap
    override def fetch(url: String): Future[CachedReference] = map.get(url) match {
      case Some(bu) => Future.successful(CachedReference(url, bu))
      case _        => throw new Exception("Unit not found")
    }
  }

  private def getAmlConfig(base: AMLConfiguration): AMLConfiguration = {
    val configuration = base
      .withResourceLoaders(
        editorResourceLoader
          .map(_ +: editorState.resourceLoader)
          .getOrElse(editorState.resourceLoader)
          .toList ++ projectState.resourceLoaders)
      .withPlugins(editorState.alsParsingPlugins ++ editorState.syntaxPlugin ++ editorState.validationPlugin)
      .withUnitCache(cache)
    dialects.foldLeft(configuration)((c, dialect) => c.withDialect(dialect))
  }

  def getAmfConfig(base: AMFConfiguration): AMFConfiguration =
    projectState.customSetUp(getAmlConfig(base).asInstanceOf[AMFConfiguration])

  def findSemanticByName(name: String): Option[(SemanticExtension, Dialect)] =
    configForSpec(Spec.AML).config.configurationState().findSemanticByName(name)

  def parse(url: String): Future[AmfParseResult] =
    parse(getAmfConfig, url)

  private def parse(amfConfiguration: AMFConfiguration, uri: String) = {
    amfConfiguration.baseUnitClient().parse(uri).map { r =>
      toResult(uri, r)
    }
  }

  def toResult(uri: String, r: AMFParsingResult): AmfParseResult = new AmfParseResult(
    r,
    definitionFor(r.baseUnit)
      .getOrElse(throw new NoDefinitionFoundException(r.baseUnit.id)),
    amfParseContext,
    uri
  )

  /**
    * @param uri
    * @return (name, isScalar)
    */
  def semanticKeysFor(uri: String): Seq[(String, Boolean)] =
    findSemanticFor(uri)
      .flatMap { t =>
        for {
          name              <- t._1.extensionName().option()
          annotationMapping <- findAnnotationMappingFor(t._2, t._1)
        } yield {
          (name, annotationMapping.objectRange().isEmpty)
        }
      }

  // this should be provided from AML because we don't want to replicate logic on our side to choose which dialect we are referring to
  def findAnnotationMappingFor(dialect: Dialect, extension: SemanticExtension): Option[AnnotationMapping] = {
    extension
      .extensionMappingDefinition()
      .option()
      .flatMap { mappingStr =>
        dialect.annotationMappings().find(am => am.id == mappingStr)
      }
  }

  def findSemanticFor(uri: String): Seq[(SemanticExtension, Dialect)] =
    getAmfConfig
      .configurationState()
      .findSemanticByTarget(uri)

  def asJsonLD(resolved: BaseUnit,
               builder: DocBuilder[_],
               renderOptions: RenderOptions = RenderOptions().withCompactUris.withoutSourceMaps): Unit =
    getAmfConfig
      .withRenderOptions(renderOptions)
      .baseUnitClient()
      .renderGraphToBuilder(resolved.cloneUnit(), builder)

  private def allDialects(configurationState: AMLConfigurationState) =
    configurationState.getDialects().toSet ++ BaseAlsDialectProvider.allBaseDialects

  def definitionFor(bu: BaseUnit): Option[Dialect] = {
    val configurationState = predefinedWithDialects.configurationState()
    bu match {
      case di: DialectInstanceUnit =>
        allDialects(configurationState).find(d => di.definedBy().option().contains(d.id))
      case _: Dialect =>
        Some(MetaDialect.dialect)
      case _: Vocabulary =>
        Some(VocabularyDialect.dialect)
      case _: ExternalFragment =>
        Some(ExternalFragmentDialect.dialect)
      case _ =>
        allDialects(configurationState).find(d => ProfileMatcher.spec(d).contains(bu.sourceSpec.getOrElse(Spec.AMF)))
    }
  }

  def definitionFor(spec: Spec): Option[Dialect] = {
    val configurationState = predefinedWithDialects.configurationState()
    spec match {
      case AmlDialectSpec(id) =>
        allDialects(configurationState).find(_.id == id)
      case _ => allDialects(configurationState).find(d => ProfileMatcher.spec(d).contains(spec))
    }
  }

  def definitionFor(nameAndVersion: String): Option[Dialect] =
    predefinedWithDialects.configurationState().getDialects().find(_.nameAndVersion() == nameAndVersion)

  def fetchContent(uri: String): Future[Content] =
    platform.fetchContent(uri, getAmfConfig)

  def buildJsonSchema(shape: AnyShape): String =
    JsonSchemaShapeRenderer.buildJsonSchema(shape, getAmfConfig)
}
