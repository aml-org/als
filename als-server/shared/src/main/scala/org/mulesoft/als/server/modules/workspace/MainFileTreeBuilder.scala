package org.mulesoft.als.server.modules.workspace

import amf.aml.client.scala.model.document.Dialect
import amf.core.client.common.validation.SeverityLevels
import amf.core.client.scala.AMFResult
import amf.core.client.scala.model.document.{BaseUnit, ExternalFragment}
import org.mulesoft.als.common.dtoTypes.{PositionRange, ReferenceOrigins, ReferenceStack}
import org.mulesoft.als.server.logger.Logger
import org.mulesoft.amfintegration.AmfImplicits.{AmfAnnotationsImp, BaseUnitImp}
import org.mulesoft.amfintegration.DiagnosticsBundle
import org.mulesoft.amfintegration.amfconfiguration.{AmfConfigurationWrapper, AmfParseResult}
import org.mulesoft.amfintegration.relationships.{AliasInfo, RelationshipLink}
import org.mulesoft.amfintegration.visitors.AmfElementVisitors
import org.mulesoft.lsp.feature.link.DocumentLink

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ParsedMainFileTree(main: AMFResult,
                         cachables: Set[String],
                         private val innerNodeRelationships: Seq[RelationshipLink],
                         private val innerDocumentLinks: Map[String, Seq[DocumentLink]],
                         private val innerAliases: Seq[AliasInfo],
                         logger: Logger,
                         definedBy: Dialect,
                         amfConfiguration: AmfConfigurationWrapper)
    extends MainFileTree {

  private val cache: mutable.Map[String, BaseUnit]  = mutable.Map.empty
  private val units: mutable.Map[String, AMFResult] = mutable.Map.empty
  private val innerRefs: mutable.Map[String, DiagnosticsBundle] =
    mutable.Map.empty

  private def index(result: AMFResult, stack: ReferenceStack): Future[Unit] = {
    val cachedF: Future[Unit]   = checkCache(result.baseUnit)
    val refs: Seq[Future[Unit]] = extractRefs(result, stack)

    Future.sequence(cachedF +: refs).map(_ => Unit)
  }

  private def extractRefs(result: AMFResult, stack: ReferenceStack): Seq[Future[Unit]] =
    if (!isRecursive(stack, result.baseUnit)) { // stop: recursion
      units.put(result.baseUnit.identifier, result)
      intoInners(result.baseUnit, stack)
      val targets = result.baseUnit.annotations.targets()
      targets
        .flatMap {
          case (targetLocation, ranges) =>
            result.baseUnit.references.find(_.identifier == targetLocation).map { r =>
              index(
                AMFResult(r, result.results),
                stack.through(
                  ranges.map(originRange => ReferenceOrigins(result.baseUnit.identifier, PositionRange(originRange))))
              )
            }
        }
    }.toSeq
    else Nil

  private def intoInners(bu: BaseUnit, stack: ReferenceStack) = {
    innerRefs.get(bu.identifier) match {
      case Some(db) => innerRefs.update(bu.identifier, db.and(stack))
      case _ =>
        innerRefs.put(bu.identifier, DiagnosticsBundle(bu.isInstanceOf[ExternalFragment], Set(stack)))
    }
  }

  private def isRecursive(stack: ReferenceStack, unit: BaseUnit): Boolean =
    stack.stack.exists(_.originUri == unit.identifier)

  private def checkCache(bu: BaseUnit): Future[Unit] =
    if (cache.isEmpty && cachables.contains(bu.identifier) && !hasErrors(bu))
      cache(bu)
    else Future.unit

  private def hasErrors(unit: BaseUnit): Boolean =
    main.results.exists(
      e =>
        e.location
          .contains(unit.identifier) && e.severityLevel == SeverityLevels.VIOLATION)

  private def cache(bu: BaseUnit): Future[Unit] = {
    val eventualUnit: Future[Unit] = Future {
      amfConfiguration.resolve(bu)
    }.flatMap(resolved => {
      if (resolved.conforms)
        amfConfiguration
          .report(resolved.baseUnit)
          .map { r =>
            if (r.conforms) cache.put(bu.identifier, resolved.baseUnit)
            Unit
          } else Future.unit
    })
    eventualUnit
      .recoverWith {
        case e: Throwable => // ignore
          logger.error(s"Error while resolving cachable unit: ${bu.identifier}. Message ${e.getMessage}",
                       "Repository",
                       "Cache unit")
          Future.successful(Unit)
      }
  }

  override def cleanCache(): Unit = cache.clear()

  override def getCache: Map[String, BaseUnit] = cache.toMap

  override def parsedUnits: Map[String, ParsedUnit] =
    units
      .map(t => t._1 -> ParsedUnit(new AmfParseResult(t._2, definedBy, amfConfiguration.branch), inTree = true, definedBy))
      .toMap

  override def references: Map[String, DiagnosticsBundle] = innerRefs.toMap

  def index(): Future[Unit] = index(main, ReferenceStack(Nil))

  override def contains(uri: String): Boolean = parsedUnits.contains(uri)

  override def cached(uri: String): Option[BaseUnit] = cache.get(uri)

  override def nodeRelationships: Seq[RelationshipLink] = innerNodeRelationships

  override def documentLinks: Map[String, Seq[DocumentLink]] = innerDocumentLinks

  override def aliases: Seq[AliasInfo] = innerAliases
}

object ParsedMainFileTree {
  def apply(main: AMFResult,
            cachables: Set[String],
            nodeRelationships: Seq[RelationshipLink],
            documentLinks: Map[String, Seq[DocumentLink]],
            aliases: Seq[AliasInfo],
            logger: Logger,
            definedBy: Dialect,
            amfConfiguration: AmfConfigurationWrapper): ParsedMainFileTree =
    new ParsedMainFileTree(main,
                           cachables,
                           nodeRelationships,
                           documentLinks,
                           aliases,
                           logger,
                           definedBy,
                           amfConfiguration)
}

object MainFileTreeBuilder {
  def build(amfParseResult: AmfParseResult,
            cachables: Set[String],
            visitors: AmfElementVisitors,
            amfConfiguration: AmfConfigurationWrapper,
            logger: Logger): Future[ParsedMainFileTree] = {

    handleVisit(visitors, logger, amfParseResult.result.baseUnit)
    val tree = ParsedMainFileTree(
      amfParseResult.result,
      cachables,
      visitors.getRelationshipsFromVisitors,
      visitors.getDocumentLinksFromVisitors,
      visitors.getAliasesFromVisitors,
      logger,
      amfParseResult.definedBy,
      amfConfiguration
    )
    tree.index().map(_ => tree)
  }

  private def handleVisit(visitors: AmfElementVisitors, logger: Logger, unit: BaseUnit): Unit = {
    try {
      visitors.applyAmfVisitors(unit)
    } catch {
      case e: Throwable =>
        logger.error(e.getMessage, "MainFileTreeBuilder", "Handle Visitors")
    }
  }
}
