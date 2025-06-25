package org.mulesoft.als.server.modules.workspace

import amf.aml.client.scala.model.document.Dialect
import amf.core.client.scala.AMFResult
import amf.core.client.scala.model.document.BaseUnit
import org.mulesoft.als.logger.Logger
import org.mulesoft.amfintegration.AmfImplicits.BaseUnitImp
import org.mulesoft.amfintegration.amfconfiguration.{AmfParseContext, AmfParseResult, DocumentDefinition, ProfileMatcher}
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
                          documentDefinition: DocumentDefinition,
                          private val parseContext: AmfParseContext
) extends MainFileTree {

  private val units: mutable.Map[String, AMFResult] = mutable.Map.empty

  override def parsedUnits: Map[String, ParsedUnit] =
    units
      .map(t =>
        t._1 -> {
          val documentDefinition = getDefinitionForBaseUnit(t._2.baseUnit)
          ParsedUnit(
            new AmfParseResult(
              t._2,
              documentDefinition,
              parseContext,
              t._1
            ),
            inTree = true,
            documentDefinition
          )
        }
      )
      .toMap

  private def getDefinitionForBaseUnit(baseUnit: BaseUnit): DocumentDefinition =
    baseUnit.sourceSpec.flatMap(ProfileMatcher.dialect).getOrElse(DocumentDefinition(ExternalFragmentDialect.dialect))

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
    .map(p => {
      val definition = DocumentDefinition(p.definedBy)
      p.path -> ParsedUnit(
        new AmfParseResult(AMFResult(p.model, Nil), definition, parseContext, p.path),
        false,
        definition
      )
    }
    )
    .toMap

  override val dialects: Map[String, ParsedUnit] = parseContext.state.dialects
    .map(d => {
      val definition = DocumentDefinition(d) // should this be the meta dialect?
      d.identifier -> ParsedUnit(
        new AmfParseResult(AMFResult(d, Nil), definition, parseContext, d.identifier),
        false,
        definition
      )
    }
    )
    .toMap
}

object ParsedMainFileTree {
  def apply(
             main: AMFResult,
             nodeRelationships: Seq[RelationshipLink],
             documentLinks: Map[String, Seq[DocumentLink]],
             aliases: Seq[AliasInfo],
             documentDefinition: DocumentDefinition,
             parseContext: AmfParseContext
  ): ParsedMainFileTree =
    new ParsedMainFileTree(main, nodeRelationships, documentLinks, aliases, documentDefinition, parseContext)
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
      amfParseResult.documentDefinition,
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
