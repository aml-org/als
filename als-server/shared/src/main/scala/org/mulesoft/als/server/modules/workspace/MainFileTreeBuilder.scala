package org.mulesoft.als.server.modules.workspace

import amf.core.errorhandling.ErrorCollector
import amf.core.model.document.{BaseUnit, ExternalFragment}
import amf.core.validation.SeverityLevels
import amf.plugins.document.vocabularies.model.document.Dialect
import org.mulesoft.als.common.dtoTypes.{PositionRange, ReferenceOrigins, ReferenceStack}
import org.mulesoft.als.configuration.WorkspaceConfiguration
import org.mulesoft.als.server.logger.Logger
import org.mulesoft.amfintegration.AmfImplicits._
import org.mulesoft.amfintegration.relationships.{AliasInfo, RelationshipLink}
import org.mulesoft.amfintegration.visitors.AmfElementVisitors
import org.mulesoft.amfintegration.{AmfParseResult, DiagnosticsBundle, ParserHelper}
import org.mulesoft.lsp.feature.link.DocumentLink

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ParsedMainFileTree(eh: ErrorCollector,
                         main: BaseUnit,
                         cachables: Set[String],
                         private val innerNodeRelationships: Seq[RelationshipLink],
                         private val innerDocumentLinks: Map[String, Seq[DocumentLink]],
                         private val innerAliases: Seq[AliasInfo],
                         logger: Logger,
                         definedBy: Dialect,
                         val workspaceConfiguration: Option[WorkspaceConfiguration])
    extends MainFileTree {

  private val errors                               = eh.getErrors
  private val cache: mutable.Map[String, BaseUnit] = mutable.Map.empty
  private val units: mutable.Map[String, BaseUnit] = mutable.Map.empty
  private val innerRefs: mutable.Map[String, DiagnosticsBundle] =
    mutable.Map.empty

  private def index(bu: BaseUnit, stack: ReferenceStack): Future[Unit] = {
    val cachedF: Future[Unit]   = checkCache(bu)
    val refs: Seq[Future[Unit]] = extractRefs(bu, stack)

    Future.sequence(cachedF +: refs).map(_ => Unit)
  }

  private def extractRefs(bu: BaseUnit, stack: ReferenceStack): Seq[Future[Unit]] =
    if (!isRecursive(stack, bu)) { // stop: recursion
      units.put(bu.identifier, bu)
      intoInners(bu, stack)
      val targets = bu.annotations.targets()
      targets
        .flatMap {
          case (targetLocation, ranges) =>
            bu.references.find(_.identifier == targetLocation).map { r =>
              index(
                r,
                stack.through(ranges.map(originRange => ReferenceOrigins(bu.identifier, PositionRange(originRange))))
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
    errors.exists(
      e =>
        e.location
          .contains(unit.identifier) && e.severityLevel == SeverityLevels.VIOLATION)

  private def cache(bu: BaseUnit): Future[Unit] = {
    val eventualUnit: Future[Unit] = Future {
      ParserHelper.resolve(bu.cloneUnit())
    }.flatMap(
      resolved =>
        ParserHelper
          .reportResolved(resolved)
          .map(r => {
            if (r.conforms) cache.put(bu.identifier, resolved)
            Unit
          }))
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
    units.map(t => t._1 -> ParsedUnit(t._2, inTree = true, definedBy, eh, workspaceConfiguration)).toMap

  override def references: Map[String, DiagnosticsBundle] = innerRefs.toMap

  def index(): Future[Unit] = index(main, ReferenceStack(Nil))

  override def contains(uri: String): Boolean = parsedUnits.contains(uri)

  override def cached(uri: String): Option[BaseUnit] = cache.get(uri)

  override def nodeRelationships: Seq[RelationshipLink] = innerNodeRelationships

  override def documentLinks: Map[String, Seq[DocumentLink]] = innerDocumentLinks

  override def aliases: Seq[AliasInfo] = innerAliases
}

object ParsedMainFileTree {
  def apply(eh: ErrorCollector,
            main: BaseUnit,
            cachables: Set[String],
            nodeRelationships: Seq[RelationshipLink],
            documentLinks: Map[String, Seq[DocumentLink]],
            aliases: Seq[AliasInfo],
            logger: Logger,
            definedBy: Dialect,
            workspaceConfiguration: Option[WorkspaceConfiguration]): ParsedMainFileTree =
    new ParsedMainFileTree(eh,
                           main,
                           cachables,
                           nodeRelationships,
                           documentLinks,
                           aliases,
                           logger,
                           definedBy,
                           workspaceConfiguration)
}

object MainFileTreeBuilder {
  def build(amfParseResult: AmfParseResult,
            cachables: Set[String],
            visitors: AmfElementVisitors,
            logger: Logger): Future[ParsedMainFileTree] = {

    handleVisit(visitors, logger, amfParseResult.baseUnit)
    val tree = ParsedMainFileTree(
      amfParseResult.eh,
      amfParseResult.baseUnit,
      cachables,
      visitors.getRelationshipsFromVisitors,
      visitors.getDocumentLinksFromVisitors,
      visitors.getAliasesFromVisitors,
      logger,
      amfParseResult.definedBy,
      amfParseResult.workspaceConfiguration
    )
    tree.index().map(_ => tree)
  }

  private def handleVisit(visitors: AmfElementVisitors, logger: Logger, unit: BaseUnit) = {
    try {
      visitors.applyAmfVisitors(unit)
    } catch {
      case e: Throwable =>
        logger.error(e.getMessage, "MainFileTreeBuilder", "Handle Visitors")
    }
  }
}
