package org.mulesoft.amfintegration.amfconfiguration
import amf.aml.client.scala.model.document.{Dialect, DialectInstanceUnit, Vocabulary}
import amf.aml.client.scala.model.domain.{AnnotationMapping, SemanticExtension}
import amf.aml.client.scala.{AMLConfiguration, AMLConfigurationState}
import amf.apicontract.client.scala._
import amf.apicontract.client.scala.configuration.OasComponentConfiguration
import amf.core.client.common.remote.Content
import amf.core.client.scala.config.{RenderOptions, UnitCache}
import amf.core.client.scala.model.document.{BaseUnit, ExternalFragment}
import amf.core.client.scala.resource.ResourceLoader
import amf.core.client.scala.{AMFParseResult => AMFParsingResult}
import amf.core.internal.remote.Spec.{AMF, GRAPHQL}
import amf.core.internal.remote.{AmlDialectSpec, Spec}
import amf.core.internal.unsafe.PlatformSecrets
import amf.graphql.client.scala.GraphQLConfiguration
import amf.shapes.client.scala.config.JsonSchemaConfiguration
import amf.shapes.client.scala.model.document.JsonSchemaDocument
import amf.shapes.client.scala.model.domain.AnyShape
import amf.shapes.client.scala.render.JsonSchemaShapeRenderer
import org.mulesoft.amfintegration.AmfImplicits._
import org.mulesoft.amfintegration.ValidationProfile
import org.mulesoft.amfintegration.dialect.dialects.ExternalFragmentDialect
import org.mulesoft.amfintegration.dialect.dialects.metadialect.{MetaDialect, VocabularyDialect}
import org.mulesoft.amfintegration.dialect.integration.BaseAlsDialectProvider
import org.yaml.builder.DocBuilder

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/** Capable of building different AMF configurations from a frozen state
  * @param state
  *   the frozen state
  */
case class ALSConfigurationState(
    editorState: EditorConfigurationState,
    projectState: ProjectConfigurationState,
    editorResourceLoader: Option[ResourceLoader]
) extends PlatformSecrets {

  lazy val amfParseContext: AmfParseContext = AmfParseContext(getAmfConfig, this)

  def configForUnit(unit: BaseUnit): AMLSpecificConfiguration =
    configForSpec(unit.sourceSpec.getOrElse(Spec.AML))

  def configForDialect(d: Dialect): AMLSpecificConfiguration =
    ProfileMatcher.spec(d) match {
      case Some(Spec.AML)
          if d
            .location()
            .contains(
              "file://vocabularies/dialects/metadialect.yaml"
            ) => // TODO change when Dialect name and version be spec
        AMLSpecificConfiguration(APIConfiguration.APIWithJsonSchema())
      case Some(spec) => configForSpec(spec)
      case _          => AMLSpecificConfiguration(predefinedWithDialects)
    }

  def configForSpec(spec: Spec): AMLSpecificConfiguration =
    AMLSpecificConfiguration(getAmlConfig(spec match {
      case Spec.RAML10     => projectState.customSetUp(RAMLConfiguration.RAML10())
      case Spec.RAML08     => projectState.customSetUp(RAMLConfiguration.RAML08())
      case Spec.OAS30      => projectState.customSetUp(OASConfiguration.OAS30())
      case Spec.OAS20      => projectState.customSetUp(OASConfiguration.OAS20())
      case Spec.ASYNC20    => projectState.customSetUp(AsyncAPIConfiguration.Async20())
      case Spec.JSONSCHEMA => projectState.customSetUp(ConfigurationAdapter.adapt(JsonSchemaConfiguration.JsonSchema()))
      case _               => predefinedWithDialects
    }))

  def getAmfConfig(url: String): AMFConfiguration = {
    val base =
      if (url.endsWith("graphql")) GraphQLConfiguration.GraphQL()
      else getAmfConfig
    getAmfConfig(base)
  }

  def getAmfConfig: AMFConfiguration = getAmfConfig(APIConfiguration.APIWithJsonSchema())

  def getAmfConfig(spec: Spec): AMFConfiguration = {
    val base = spec match {
      case GRAPHQL => GraphQLConfiguration.GraphQL()
      // case GRPC =>
      case _ => APIConfiguration.fromSpec(spec)
    }
    getAmfConfig(base)
  }

  def allDialects: Seq[Dialect]        = dialects ++ BaseAlsDialectProvider.allBaseDialects
  def dialects: Seq[Dialect]           = projectState.extensions ++ editorState.dialects
  def profiles: Seq[ValidationProfile] = projectState.profiles ++ editorState.profiles

  private def predefinedWithDialects: AMLConfiguration =
    dialects.foldLeft(AMLConfiguration.predefined())((c, d) => c.withDialect(d))

  val cache: UnitCache = projectState.cache

  private def getAmlConfig(base: AMLConfiguration): AMLConfiguration = {
    val configuration = base
      .withResourceLoaders(
        editorResourceLoader
          .map(_ +: editorState.resourceLoader)
          .getOrElse(editorState.resourceLoader)
          .toList ++ projectState.resourceLoaders
      )
      .withPlugins(editorState.alsParsingPlugins ++ editorState.syntaxPlugin ++ editorState.validationPlugin)
      .withUnitCache(cache)
    dialects.foldLeft(configuration)((c, dialect) => c.withDialect(dialect))
  }

  def getAmfConfig(base: AMFConfiguration): AMFConfiguration =
    projectState.customSetUp(getAmlConfig(base).asInstanceOf[AMFConfiguration])

  def findSemanticByName(name: String): Option[(SemanticExtension, Dialect)] =
    configForSpec(Spec.AML).config.configurationState().findSemanticByName(name)

  def parse(url: String): Future[AmfParseResult] =
    parse(getAmfConfig(url), url)

  private def parse(amfConfiguration: AMFConfiguration, uri: String) =
    amfConfiguration.baseUnitClient().parse(uri).map { r =>
      toResult(uri, r)
    }

  def toResult(uri: String, r: AMFParsingResult): AmfParseResult = new AmfParseResult(
    r,
    definitionFor(r.baseUnit)
      .getOrElse(throw new NoDefinitionFoundException(r.baseUnit.id)),
    amfParseContext,
    uri
  )

  /** @param uri
    * @return
    *   (name, isScalar)
    */
  def semanticKeysFor(uri: String, excludedDialects: Seq[Dialect] = Seq.empty): Seq[(String, Boolean)] = {
    val excludedLocations =
      excludedDialects.map(ed => ed.identifier) // hack because final id adoption changes reference id
    findSemanticFor(uri)
      .filterNot(t => excludedLocations.contains(t._2.location().getOrElse(t._2.id)))
      .flatMap { t =>
        for {
          name              <- t._1.extensionName().option()
          annotationMapping <- findAnnotationMappingFor(t._2, t._1)
        } yield {
          (name, annotationMapping.objectRange().isEmpty)
        }
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
    getAmfConfig(uri)
      .configurationState()
      .findSemanticByTarget(uri)

  def findSemanticForName(name: String): Option[(SemanticExtension, Dialect)] =
    getAmfConfig
      .configurationState()
      .findSemanticByName(name)

  def asJsonLD(
      resolved: BaseUnit,
      builder: DocBuilder[_],
      renderOptions: RenderOptions = RenderOptions().withCompactUris.withoutSourceMaps
  ): Unit =
    getAmfConfig(resolved.sourceSpec.getOrElse(AMF))
      .withRenderOptions(renderOptions)
      .baseUnitClient()
      .renderGraphToBuilder(resolved.cloneUnit(), builder)

  private def allDialects(configurationState: AMLConfigurationState) =
    configurationState.getDialects().toSet ++ BaseAlsDialectProvider.allBaseDialects

  def definitionFor(bu: BaseUnit): Option[Dialect] = {
    val configurationState = predefinedWithDialects.configurationState()

    def defaultDefinitionSearch = {
      allDialects(configurationState).find(d => ProfileMatcher.spec(d).contains(bu.sourceSpec.getOrElse(Spec.AMF)))
    }

    bu match {
      case di: DialectInstanceUnit =>
        allDialects(configurationState).find(d => di.definedBy().option().contains(d.id))
      case _: Dialect =>
        Some(MetaDialect.dialect)
      case _: Vocabulary =>
        Some(VocabularyDialect.dialect)
      case _: ExternalFragment =>
        Some(ExternalFragmentDialect.dialect)
      case jsonSchema: JsonSchemaDocument if jsonSchema.schemaVersion.nonEmpty =>
        // all drafts use same Spec, so we must differentiate with version
        allDialects(configurationState)
          .find(d => d.version().option().contains(jsonSchema.schemaVersion.value()))
          .orElse(defaultDefinitionSearch)
      case _ =>
        defaultDefinitionSearch
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
