package org.mulesoft.als.server.modules.workspace

import amf.aml.client.scala.model.document.Dialect
import amf.core.client.scala.AMFResult
import amf.core.client.scala.model.document.{BaseUnit, ExternalFragment}
import org.mulesoft.als.common.dtoTypes.{PositionRange, ReferenceOrigins, ReferenceStack}
import org.mulesoft.als.logger.Logger
import org.mulesoft.amfintegration.AmfImplicits.{AmfAnnotationsImp, BaseUnitImp}
import org.mulesoft.amfintegration.DiagnosticsBundle
import org.mulesoft.amfintegration.amfconfiguration.{AmfParseContext, AmfParseResult}
import org.mulesoft.amfintegration.relationships.{AliasInfo, RelationshipLink}
import org.mulesoft.amfintegration.visitors.AmfElementVisitors
import org.mulesoft.lsp.feature.link.DocumentLink

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ParsedMainFileTree(
    val main: AMFResult,
    private val innerNodeRelationships: Seq[RelationshipLink],
    private val innerDocumentLinks: Map[String, Seq[DocumentLink]],
    private val innerAliases: Seq[AliasInfo],
    definedBy: Dialect,
    private val parseContext: AmfParseContext,
    private val disableValidationAllTraces: Boolean
) extends MainFileTree {
  var indexCalls                                        = 0
  val cacherefs: mutable.Map[String, Seq[Future[Unit]]] = mutable.Map.empty[String, Seq[Future[Unit]]]

  private val units: mutable.Map[String, AMFResult] = mutable.Map.empty
  private val innerRefs: mutable.Map[String, DiagnosticsBundle] =
    mutable.Map.empty

  private def index(result: AMFResult, stack: ReferenceStack): Future[Unit] = {
    if (disableValidationAllTraces) {
      if (!cacherefs.contains(result.baseUnit.identifier)) {
        val refs: Seq[Future[Unit]] = extractRefs(result, stack)
        cacherefs += result.baseUnit.identifier -> refs
        Future.sequence(refs).map(_ => Unit)
      } else
        Future.sequence(cacherefs.getOrElse(result.baseUnit.identifier, Nil)).map(_ => Unit)
    } else {
      val refs: Seq[Future[Unit]] = extractRefs(result, stack)
      Future.sequence(refs).map(_ => Unit)
    }
  }

  private def extractRefs(result: AMFResult, stack: ReferenceStack): Seq[Future[Unit]] =
    if (!isRecursive(stack, result.baseUnit)) { // stop: recursion
      units.put(result.baseUnit.identifier, result)
      intoInners(result.baseUnit, stack)
      val targets = result.baseUnit.annotations.targets()
      targets
        .flatMap { case (targetLocation, ranges) =>
          result.baseUnit.references.find(_.identifier == targetLocation).map { r =>
            index(
              AMFResult(r, result.results),
              stack.through(
                ranges.map(originRange => ReferenceOrigins(result.baseUnit.identifier, PositionRange(originRange)))
              )
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

  override def parsedUnits: Map[String, ParsedUnit] =
    units
      .map(t => t._1 -> ParsedUnit(new AmfParseResult(t._2, definedBy, parseContext, t._1), inTree = true, definedBy))
      .toMap

  override def references: Map[String, DiagnosticsBundle] = innerRefs.toMap

  def index(): Future[Unit] = index(main, ReferenceStack(Nil))

  override def contains(uri: String): Boolean = (parsedUnits.keys ++ profiles.keys ++ dialects.keys).exists(_ == uri)

  override def nodeRelationships: Seq[RelationshipLink] = innerNodeRelationships

  override def documentLinks: Map[String, Seq[DocumentLink]] = innerDocumentLinks

  override def aliases: Seq[AliasInfo] = innerAliases

  override val profiles: Map[String, ParsedUnit] = parseContext.state.profiles
    .map(p =>
      p.path -> ParsedUnit(
        new AmfParseResult(AMFResult(p.model, Nil), p.definedBy, parseContext, p.path),
        false,
        p.definedBy
      )
    )
    .toMap

  override val dialects: Map[String, ParsedUnit] = parseContext.state.dialects
    .map(d =>
      d.identifier -> ParsedUnit(
        new AmfParseResult(AMFResult(d, Nil), d, parseContext, d.identifier),
        false,
        d
      )
    )
    .toMap
}

object ParsedMainFileTree {
  def apply(
      main: AMFResult,
      nodeRelationships: Seq[RelationshipLink],
      documentLinks: Map[String, Seq[DocumentLink]],
      aliases: Seq[AliasInfo],
      definedBy: Dialect,
      parseContext: AmfParseContext,
      disableValidationAllTraces: Boolean
  ): ParsedMainFileTree =
    new ParsedMainFileTree(
      main,
      nodeRelationships,
      documentLinks,
      aliases,
      definedBy,
      parseContext,
      disableValidationAllTraces
    )
}

object MainFileTreeBuilder {
  def build(
      amfParseResult: AmfParseResult,
      visitors: AmfElementVisitors,
      logger: Logger,
      disableValidationAllTraces: Boolean
  ): Future[ParsedMainFileTree] = {

    handleVisit(visitors, logger, amfParseResult.result.baseUnit, amfParseResult.context)
    val tree = ParsedMainFileTree(
      amfParseResult.result,
      visitors.getRelationshipsFromVisitors,
      visitors.getDocumentLinksFromVisitors,
      visitors.getAliasesFromVisitors,
      amfParseResult.definedBy,
      amfParseResult.context,
      disableValidationAllTraces
    )
    logger.debug(s"ValidationAllTraces - about to call root index for ${tree.main.baseUnit.id}", "", "")
    val startTime     = System.currentTimeMillis()
    val mainTreeIndex = tree.index()
    val endTime       = System.currentTimeMillis()
    logger.debug(
      s"ValidationAllTraces - disableValidationAllTraces = $disableValidationAllTraces - indexCalls ${tree.indexCalls}, took ${endTime - startTime} milliseconds to index for ${tree.main.baseUnit.id}",
      "",
      ""
    )
    val endResult = mainTreeIndex.map(_ => tree)
    endResult
  }

  private def handleVisit(
      visitors: AmfElementVisitors,
      logger: Logger,
      unit: BaseUnit,
      context: AmfParseContext
  ): Unit = {
    try {
      visitors.applyAmfVisitors(unit, context)
    } catch {
      case e: Throwable =>
        logger.error(e.getMessage, "MainFileTreeBuilder", "Handle Visitors")
    }
  }
}
