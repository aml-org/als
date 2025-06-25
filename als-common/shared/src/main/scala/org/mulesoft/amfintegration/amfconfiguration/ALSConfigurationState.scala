package org.mulesoft.amfintegration.amfconfiguration
import amf.aml.client.scala.{AMLConfiguration, AMLConfigurationState}
import amf.aml.client.scala.model.document.{Dialect, DialectInstanceUnit, Vocabulary}
import amf.aml.client.scala.model.domain.{AnnotationMapping, SemanticExtension}
import amf.apicontract.client.scala._
import amf.core.client.common.remote.Content
import amf.core.client.scala.config.{RenderOptions, UnitCache}
import amf.core.client.scala.model.document.{BaseUnit, ExternalFragment}
import amf.core.client.scala.resource.ResourceLoader
import amf.core.client.scala.{AMFParseResult => AMFParsingResult}
import amf.core.internal.remote.Spec.{AMF, GRAPHQL}
import amf.core.internal.remote.{AmlDialectSpec, Spec}
import amf.graphql.client.scala.GraphQLConfiguration
import amf.shapes.client.scala.ShapesConfiguration
import amf.shapes.client.scala.config.JsonSchemaConfiguration
import amf.shapes.client.scala.model.document.JsonSchemaDocument
import amf.shapes.client.scala.model.domain.AnyShape
import amf.shapes.client.scala.render.JsonSchemaShapeRenderer
import org.mulesoft.als.configuration.{MaxSizeCounter, MaxSizeResourceLoader}
import org.mulesoft.amfintegration.AmfImplicits._
import org.mulesoft.amfintegration.ValidationProfile
import org.mulesoft.amfintegration.dialect.dialects.ExternalFragmentDialect
import org.mulesoft.amfintegration.dialect.dialects.metadialect.{MetaDialect, VocabularyDialect}
import org.mulesoft.amfintegration.dialect.integration.BaseAlsDefinitionsProvider
import org.mulesoft.amfintegration.dialect.jsonschemas.MCPJsonSchema
import org.mulesoft.amfintegration.platform.AlsPlatformSecrets
import org.yaml.builder.DocBuilder
import amf.mcp.internal.plugins.parse.schema.MCPSchemaLoader

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
) extends AlsPlatformSecrets {

  lazy val amfParseContext: AmfParseContext = AmfParseContext(getAmfConfig(false), this)

  def configForUnit(unit: BaseUnit): AMLSpecificConfiguration =
    configForSpec(unit.sourceSpec.getOrElse(Spec.AML))

  private def rootConfiguration(asMain: Boolean): ShapesConfiguration = projectState.rootProjectConfiguration(asMain)

  def configForDefinition(d: DocumentDefinition): AMLSpecificConfiguration =
    ProfileMatcher.spec(d) match {
      case Some(Spec.AML)
          if d.baseUnit
            .location()
            .contains(
              "file://vocabularies/dialects/metadialect.yaml"
            ) => // TODO change when Dialect name and version be spec
        AMLSpecificConfiguration(rootConfiguration(false))
      case Some(spec) => configForSpec(spec)
      case _          => AMLSpecificConfiguration(predefinedWithDialects)
    }

  def configForSpec(spec: Spec): AMLSpecificConfiguration = // todo: check if asMain = true has any inconvenience
    AMLSpecificConfiguration(
      getAmlConfig(
        apiConfigurationForSpec(spec).map(projectState.customSetUp(_, asMain = true)).getOrElse(predefinedWithDialects)
      )
    )

  private def apiConfigurationForSpec(spec: Spec): Option[ShapesConfiguration] =
    spec match {
      case Spec.RAML10       => Some(RAMLConfiguration.RAML10())
      case Spec.RAML08       => Some(RAMLConfiguration.RAML08())
      case Spec.OAS30        => Some(OASConfiguration.OAS30())
      case Spec.OAS20        => Some(OASConfiguration.OAS20())
      case Spec.ASYNC20      => Some(AsyncAPIConfiguration.Async20())
      case Spec.ASYNC26      => Some(AsyncAPIConfiguration.Async20())
      case Spec.GRAPHQL      => Some(ConfigurationAdapter.adapt(GraphQLConfiguration.GraphQL()))
      case Spec.JSONSCHEMA   => Some(ConfigurationAdapter.adapt(JsonSchemaConfiguration.JsonSchema()))
      case Spec.AVRO_SCHEMA  => Some(AvroConfiguration.Avro())
      case Spec.MCP          => Some(projectState.getMCPProjectConfig(true))
      case _ if spec.isAsync => Some(AsyncAPIConfiguration.Async20())
      case _                 => None
    }

  def getAmfConfig(url: String, asMain: Boolean): ShapesConfiguration = {
    val base: ShapesConfiguration =
      if (url.endsWith("graphql"))
        projectState.getGraphQLProjectConfig(asMain)
      else if (url.endsWith("avsc"))
        projectState.getAvroProjectConfig(asMain).withPlugins(editorState.alsParsingPlugins)
      else if (MCPJsonSchema.isMcpFile(url))
        projectState.getMCPProjectConfig(asMain).withPlugins(editorState.alsParsingPlugins)
      else getAmfConfig(asMain).withPlugins(editorState.alsParsingPlugins)
    getAmfConfig(base, asMain)
  }

  def getAmfConfig(asMain: Boolean): ShapesConfiguration = getAmfConfig(rootConfiguration(asMain), asMain)

  def getAmfConfig(spec: Spec): ShapesConfiguration = {
    val base = spec match {
      case GRAPHQL => GraphQLConfiguration.GraphQL()
      // case GRPC =>
      case _ =>
        APIConfiguration.fromSpec(spec)
    }
    getAmfConfig(base, asMain = true) // todo: check if asMain = true has any inconvenience
  }

  def allDefinitions: Seq[DocumentDefinition]        = dialects.map(DocumentDefinition(_)) ++ BaseAlsDefinitionsProvider.allBaseDefinitions
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
      .withPlugins((editorState.syntaxPlugin ++ editorState.validationPlugin).toList)
      .withUnitCache(cache)
    dialects.foldLeft(configuration)((c, dialect) => c.withDialect(dialect))
  }


  def getAmfConfig(base: ShapesConfiguration, asMain: Boolean): ShapesConfiguration =
    projectState.customSetUp(getAmlConfig(base).asInstanceOf[ShapesConfiguration], asMain) // todo: this .asInstanceOf[ShapesConfiguration] is horrible, look for a better way

  def findSemanticByName(name: String): Option[(SemanticExtension, Dialect)] =
    configForSpec(Spec.AML).config.configurationState().findSemanticByName(name)

  def parse(url: String, asMain: Boolean = false, maxFileSize: Option[Int] = None): Future[AmfParseResult] =
    parse(getAmfConfig(url, asMain), url, maxFileSize)

  private def parse(amfConfiguration: ShapesConfiguration, uri: String, maxFileSize: Option[Int]): Future[AmfParseResult] =
    wrapLoadersIfNeeded(amfConfiguration, maxFileSize)
      .baseUnitClient()
      .parse(uri)
      .map { r =>
        toResult(uri, r)
      }

  private def wrapLoadersIfNeeded(amfConfiguration: ShapesConfiguration, maxFileSize: Option[Int]) =
    maxFileSize match {
      case Some(maxSize) if maxSize > 0 =>
        val counter = new MaxSizeCounter(maxSize)
        amfConfiguration
          .withResourceLoaders(
            amfConfiguration
              .configurationState()
              .getResourceLoaders()
              .map(MaxSizeResourceLoader(_, counter))
              .toList
          )
      case _ =>
        amfConfiguration
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
    getAmfConfig(uri, asMain = true) // todo: check if asMain = true has any inconvenience
      .configurationState()
      .findSemanticByTarget(uri)

  def findSemanticForName(name: String): Option[(SemanticExtension, Dialect)] =
    getAmfConfig(false)
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

  private def allDialects(configurationState: AMLConfigurationState): Set[DocumentDefinition] =
    (configurationState.getDialects().map(DocumentDefinition(_)).toSet ++ BaseAlsDefinitionsProvider.allBaseDefinitions)

  def definitionFor(bu: BaseUnit): Option[DocumentDefinition] = {
    val configurationState = predefinedWithDialects.configurationState()

    def defaultDefinitionSearch =
      allDialects(configurationState).find(d => ProfileMatcher.spec(d).contains(bu.sourceSpec.getOrElse(Spec.AMF)))

    def overwrittenSpecs: Option[DocumentDefinition] =
      if (bu.sourceSpec.exists(_.isAsync))
        allDialects(configurationState).find(d => ProfileMatcher.spec(d).contains(Spec.ASYNC20))
      else if (bu.sourceSpec.exists(_.toString == "Mcp")) // ask for AMF to expose a way to identify this better, or wrap `editorState.schemas` so it has a (spec -> DD)
        Some(DocumentDefinition(MCPSchemaLoader.doc))// editorState.schemas.headOption.map(DocumentDefinition(_)) // todo: implement a search mechanism to support more than one def
      else None

    bu match {
      case di: DialectInstanceUnit =>
        allDialects(configurationState).find(d => di.definedBy().option().contains(d.baseUnit.id))
      case _: Dialect =>
        Some(DocumentDefinition(MetaDialect.dialect))
      case _: Vocabulary =>
        Some(DocumentDefinition(VocabularyDialect.dialect))
      case _: ExternalFragment =>
        Some(DocumentDefinition(ExternalFragmentDialect.dialect))
      case jsonSchema: JsonSchemaDocument if jsonSchema.schemaVersion.nonEmpty =>
        // all drafts use same Spec, so we must differentiate with version
        allDialects(configurationState)
          .find(d => d.version().contains(jsonSchema.schemaVersion.value()))
          .orElse(defaultDefinitionSearch)
      case _ =>
        defaultDefinitionSearch.orElse(overwrittenSpecs)
    }
  }

  def definitionFor(spec: Spec): Option[DocumentDefinition] = {
    val configurationState = predefinedWithDialects.configurationState()
    spec match {
      case AmlDialectSpec(id) =>
        allDialects(configurationState).find(_.baseUnit.id == id)
      case _ => allDialects(configurationState).find(d => ProfileMatcher.spec(d).contains(spec))
    }
  }

  def definitionFor(nameAndVersion: String): Option[Dialect] =
    predefinedWithDialects.configurationState().getDialects().find(_.nameAndVersion() == nameAndVersion)

  def fetchContent(uri: String): Future[Content] = try {
    platform.fetchContent(uri, getAmfConfig(false))
  } catch {
    case e: Exception =>
      Future.failed(e)
  }

  def buildJsonSchema(shape: AnyShape): String =
    JsonSchemaShapeRenderer.buildJsonSchema(shape, getAmfConfig(false))
}
