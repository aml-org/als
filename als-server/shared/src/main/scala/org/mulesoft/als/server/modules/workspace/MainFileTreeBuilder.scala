package org.mulesoft.als.server.modules.workspace

import amf.aml.client.scala.model.document.Dialect
import amf.core.client.scala.AMFResult
import amf.core.client.scala.model.document.BaseUnit
import org.mulesoft.als.logger.Logger
import org.mulesoft.amfintegration.AmfImplicits.BaseUnitImp
import org.mulesoft.amfintegration.amfconfiguration.{AmfParseContext, AmfParseResult, ProfileMatcher}
import org.mulesoft.amfintegration.dialect.dialects.ExternalFragmentDialect
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
    private val parseContext: AmfParseContext
) extends MainFileTree {

  private val units: mutable.Map[String, AMFResult] = mutable.Map.empty

  override def parsedUnits: Map[String, ParsedUnit] =
    units
      .map(t =>
        t._1 -> {
          val definedBy = getDialectForBaseUnit(t._2.baseUnit)
          ParsedUnit(
            new AmfParseResult(
              t._2,
              definedBy,
              parseContext,
              t._1
            ),
            inTree = true,
            definedBy
          )
        }
      )
      .toMap

  private def getDialectForBaseUnit(baseUnit: BaseUnit): Dialect =
    baseUnit.sourceSpec.flatMap(ProfileMatcher.dialect).getOrElse(ExternalFragmentDialect.dialect)

  override def references: Map[String, Seq[DocumentLink]] = documentLinks

  def index(refs: Seq[BaseUnit]): Unit =
    (main.baseUnit +: refs)
      .map(AMFResult(_, main.results))
      .foreach(r => units.put(r.baseUnit.identifier, r))

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
      parseContext: AmfParseContext
  ): ParsedMainFileTree =
    new ParsedMainFileTree(main, nodeRelationships, documentLinks, aliases, definedBy, parseContext)
}

object MainFileTreeBuilder {
  def build(
      amfParseResult: AmfParseResult,
      visitors: AmfElementVisitors,
      refs: Seq[BaseUnit]
  ): Future[ParsedMainFileTree] = Future {
    handleVisit(visitors, amfParseResult.result.baseUnit, amfParseResult.context)
    val tree = ParsedMainFileTree(
      amfParseResult.result,
      visitors.getRelationshipsFromVisitors,
      visitors.getDocumentLinksFromVisitors,
      visitors.getAliasesFromVisitors,
      amfParseResult.definedBy,
      amfParseResult.context
    )
    tree.index(refs)
    tree
  }

  private def handleVisit(
      visitors: AmfElementVisitors,
      unit: BaseUnit,
      context: AmfParseContext
  ): Unit = {
    try {
      visitors.applyAmfVisitors(unit, context)
    } catch {
      case e: Throwable =>
        Logger.error(s"Exception: ${e.getMessage}", "MainFileTreeBuilder", "Handle Visitors")
    }
  }
}
