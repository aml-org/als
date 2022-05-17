package org.mulesoft.als.server.modules.workspace

import amf.aml.client.scala.AMLDialectInstanceResult
import amf.aml.client.scala.model.document.{Dialect, DialectInstance}
import amf.apicontract.client.scala.{AMFConfiguration, APIConfiguration}
import amf.core.client.common.validation.SeverityLevels
import amf.core.client.scala.config.{CachedReference, UnitCache}
import amf.core.client.scala.model.document.BaseUnit
import amf.core.client.scala.resource.ResourceLoader
import amf.core.client.scala.validation.AMFValidationResult
import amf.core.client.scala.{AMFParseResult, AMFResult}
import org.mulesoft.als.common.URIImplicits.StringUriImplicits
import org.mulesoft.als.configuration.ProjectConfiguration
import org.mulesoft.als.logger.Logger
import org.mulesoft.als.server.textsync.EnvironmentProvider
import org.mulesoft.als.server.workspace.ProjectConfigurationProvider
import org.mulesoft.amfintegration.AmfImplicits.BaseUnitImp
import org.mulesoft.amfintegration.ValidationProfile
import org.mulesoft.amfintegration.amfconfiguration._

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ProjectConfigurationNotFound(folder: String) extends Exception(s"Couldn't find configuration for folder: $folder")

sealed class ConfigurationMap {
  case class ConfigurationContainer(
      configuration: Future[DefaultProjectConfiguration],
      projectConfig: ProjectConfiguration
  )
  var configurations: Map[String, ConfigurationContainer] = Map()

  def get(folder: String): Option[ConfigurationContainer] = synchronized {
    configurations.get(folder)
  }

  def update(configuration: Future[DefaultProjectConfiguration], projectConfiguration: ProjectConfiguration): Unit =
    synchronized {
      configurations =
        configurations + (projectConfiguration.folder -> ConfigurationContainer(configuration, projectConfiguration))
    }
}

class DefaultProjectConfigurationProvider(
    environmentProvider: EnvironmentProvider,
    editorConfiguration: EditorConfigurationProvider,
    logger: Logger
) extends ProjectConfigurationProvider {

  val configurationMap = new ConfigurationMap

  override def getProjectInfo(folder: String): Option[Future[ProjectConfigurationState]] =
    configurationMap.get(folder).map(_.configuration)

  override def getProfiles(folder: String): Future[Seq[ValidationProfile]] =
    configurationMap.get(folder).map(_.configuration.map(_.profiles)).getOrElse(Future.successful(Nil))

  override def getMainFile(folder: String): Option[Future[String]] =
    configurationMap.get(folder).flatMap(_.projectConfig.mainFile).map(Future.successful)

  override def getProjectRoot(folder: String): Option[Future[String]] =
    configurationMap.get(folder).flatMap(_.projectConfig.rootUri).map(Future.successful)

  override def newProjectConfiguration(
      projectConfiguration: ProjectConfiguration
  ): Future[ProjectConfigurationState] = {
    val c = for {
      (dialects, dialectParseResult) <- parseDialects(
        projectConfiguration.metadataDependency ++ projectConfiguration.extensionDependency
      )
      (profiles, profilesParseResult) <- parseValidationProfiles(projectConfiguration.validationDependency)
    } yield {
      DefaultProjectConfiguration(
        dialects.toSeq,
        profiles.toSeq,
        (dialectParseResult ++ profilesParseResult).toSeq,
        projectConfiguration,
        environmentProvider,
        Seq.empty, // todo: add projects errors such as "file.yaml" is not a profile
        editorConfiguration,
        logger
      )
    }
    configurationMap.update(c, projectConfiguration)
    c
  }

  private def amfConfiguration: Future[AMFConfiguration] =
    editorConfiguration.getState.map(state => {
      val base = APIConfiguration
        .API()
        .withResourceLoaders(state.resourceLoader.toList)
        .withResourceLoader(environmentProvider.getResourceLoader)
        .withPlugins((state.syntaxPlugin ++ state.alsParsingPlugins ++ state.validationPlugin).toList)
      state.dialects.foldLeft(base)((b, d) => b.withDialect(d))
    })

  // todo: log something? time the parse?
  private def parse(uri: String): Future[AMFParseResult] = amfConfiguration.flatMap(_.baseUnitClient().parse(uri))

  private def parseProfile(uri: String): Future[(AMLDialectInstanceResult, Dialect)] = amfConfiguration.flatMap { c =>
    c.baseUnitClient().parseDialectInstance(uri).map { instance =>
      (instance, c.configurationState().findDialectFor(instance.dialectInstance).get)
    }
  }

  /** Seeks new extensions in configuration, parses and registers
    */
  private def parseDialects(dialects: Set[String]): Future[(Set[Dialect], Set[AMFParseResult])] = {
    val newDialects = dialects
      .map(e => {
        if (e.isValidUri) e // full URI received
        else {
          logger.warning(s"Invalid dialect uri: $e", "DefaultProjectConfigurationProvider", "parseDialects")
          e
        }
      })
    newDialects.foreach(e =>
      logger
        .debug(s"Parsing & registering $e as dialect", "DefaultProjectConfigurationProvider", "registerNewDialects")
    )
    Future
      .sequence(
        newDialects
          .map(parse)
          .map(
            _.map(r =>
              r.baseUnit match {
                case d: Dialect =>
                  Some((d, r))
                case b =>
                  logger.error(
                    s"The following dialect: ${b.identifier} is not valid",
                    "DefaultProjectConfigurationProvider",
                    "registerNewDialects"
                  )
                  None
              }
            )
          )
      )
      .map(s => s.flatten)
      .map(_.unzip)
  }

  private def parseValidationProfiles(
      validationProfiles: Set[String]
  ): Future[(Set[ValidationProfile], Set[AMFParseResult])] = {
    Future
      .sequence(
        validationProfiles.map(parseProfile)
      )
      .map(_.flatMap(r => {
        if (r._1.baseUnit.isValidationProfile && r._1.conforms) {
//          updateUnit(uuid, r, isDependency = true) //todo: cache
          r._1.dialectInstance match {
            case instance: DialectInstance =>
              logger.debug(
                "Adding validation profile: " + instance.identifier,
                "DefaultProjectConfigurationProvider",
                "registerNewValidationProfiles"
              )
              Some(ValidationProfile(r._1.baseUnit.identifier, instance.raw.getOrElse(""), instance, r._2), r._1)
            case _ => None
          }
        } else {
          logger.error(
            s"The following validation profile: ${r._1.baseUnit.identifier} is not valid",
            "DefaultProjectConfigurationProvider",
            "registerNewValidationProfiles"
          )
          None
        }
      }))
      .map(_.unzip)
  }

  override def afterNewTree(folder: String, tree: MainFileTree): Future[Unit] =
    tree match {
      case tree: ParsedMainFileTree =>
        configurationMap
          .get(folder)
          .map(_.configuration.flatMap(_.cacheBuilder.updateCache(tree.main, tree.parsedUnits)).map(_ => {}))
          .getOrElse(Future.unit)
      case _ => Future.successful()
    }
}

case class DefaultProjectConfiguration(
    override val extensions: Seq[Dialect],
    override val profiles: Seq[ValidationProfile],
    override val results: Seq[AMFParseResult],
    override val config: ProjectConfiguration,
    private val environmentProvider: EnvironmentProvider,
    override val projectErrors: Seq[AMFValidationResult],
    private val editorConfiguration: EditorConfigurationProvider,
    private val logger: Logger
) extends ProjectConfigurationState {

  val cacheBuilder: CacheBuilder =
    new CacheBuilder(config.folder, config.designDependency, environmentProvider, editorConfiguration, logger)
  override def cache: UnitCache = cacheBuilder.buildUnitCache

  override val resourceLoaders: Seq[ResourceLoader] = Nil
}

class CacheBuilder(
    folder: String,
    cacheables: Set[String],
    private val environmentProvider: EnvironmentProvider,
    private val editorConfiguration: EditorConfigurationProvider,
    logger: Logger
) {

  private val cache: mutable.Map[String, BaseUnit] = mutable.Map.empty

  def buildUnitCache: UnitCache =
    (url: String) =>
      cache.get(url) match {
        case Some(bu) => Future.successful(CachedReference(url, bu))
        case _        => Future.failed(new Exception("Unit not found"))
      }
  def cachedUnits: Seq[BaseUnit] = cache.values.toSeq

  def updateCache(main: AMFResult, units: Map[String, ParsedUnit]): Future[Unit] = {
    Future
      .sequence(units.map({ case (_, result) =>
        cacheIfCorresponds(main, result.parsedResult.result.baseUnit)
      }))
      .map(_ => {})
  }

  def cacheIfCorresponds(main: AMFResult, bu: BaseUnit): Future[Unit] =
    if (cacheables.contains(bu.identifier) && !hasErrors(main, bu))
      cache(bu)
    else Future.unit

  private def hasErrors(main: AMFResult, unit: BaseUnit): Boolean =
    main.results.exists(e =>
      e.location
        .contains(unit.identifier) && e.severityLevel == SeverityLevels.VIOLATION
    )

  private def configForUnit(unit: BaseUnit, state: EditorConfigurationState): AMLSpecificConfiguration = {
    AMLSpecificConfiguration(
      EditorConfigurationStateWrapper(state)
        .configForUnit(unit)
        .withResourceLoader(environmentProvider.getResourceLoader)
    )
  }

  private def cache(bu: BaseUnit): Future[Unit] = {
    val eventualUnit: Future[Unit] = editorConfiguration.getState
      .map(state => {
        configForUnit(bu, state)
      })
      .flatMap(amfConfiguration => {
        val resolved = amfConfiguration.resolve(bu)
        if (resolved.conforms)
          amfConfiguration
            .report(resolved.baseUnit)
            .map { r =>
              if (r.conforms) {
                logger.debug(s"Caching ${bu.identifier}", "CacheBuilder", "cache")
                cache.put(bu.identifier, resolved.baseUnit)
              } else {
                logger.debug(s"Skipping ${bu.identifier} from cache as it does not conform", "CacheBuilder", "cache")
              }
              Unit
            }
        else Future.unit
      })
    eventualUnit
      .recoverWith { case e: Throwable => // ignore
        logger.error(
          s"Error while resolving cachable unit: ${bu.identifier}. Message ${e.getMessage} at $folder",
          "CacheBuilder",
          "Cache unit"
        )
        Future.successful(Unit)
      }
  }

}
