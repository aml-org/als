package org.mulesoft.als.server.modules.workspace

import amf.core.client.scala.model.document.BaseUnit
import org.mulesoft.als.common.dtoTypes.ReferenceStack
import org.mulesoft.als.logger.Logger
import org.mulesoft.amfintegration.AmfImplicits.BaseUnitImp
import org.mulesoft.amfintegration.DiagnosticsBundle
import org.mulesoft.amfintegration.amfconfiguration.AmfParseResult
import org.mulesoft.amfintegration.relationships.{AliasInfo, RelationshipLink}
import org.mulesoft.amfintegration.visitors.AmfElementDefaultVisitors
import org.mulesoft.lsp.feature.link.DocumentLink

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class WorkspaceParserRepository(logger: Logger) extends Repository[ParsedUnit] {

  private def visitors(bu: BaseUnit) = AmfElementDefaultVisitors.build(bu)

  private var tree: MainFileTree = EmptyFileTree

  override def getAllFilesUris: List[String] = getIsolatedUris ++ getTreeUris // profiles not added on purpose?

  def getTreeUris: List[String] = treeUnits().map(_.parsedResult.result.baseUnit.identifier).toList

  def getIsolatedUris: List[String] = units.values.map(_.parsedResult.result.baseUnit.identifier).toList

  override def getUnit(uri: String): Option[ParsedUnit] =
    tree.parsedUnits.get(uri).orElse(units.get(uri)).orElse(tree.profiles.get(uri)).orElse(tree.dialects.get(uri))

  def references: Map[String, DiagnosticsBundle] = tree.references

  def inTree(uri: String): Boolean = tree.contains(uri)

  def treeUnits(): Iterable[ParsedUnit] = tree.parsedUnits.values

  def updateUnit(result: AmfParseResult): Unit = {
    logger.debug(s"updating ${result.result.baseUnit.location()}", "WorkspaceParserRepository", "updateUnit")
    if (tree.contains(result.result.baseUnit.identifier)) throw new Exception("Cannot update an unit from the tree")
    val unit = ParsedUnit(result, inTree = false, result.definedBy)
    updateUnit(result.result.baseUnit.identifier, unit)
  }

  def cleanTree(): Unit = tree = EmptyFileTree

  def newTree(result: AmfParseResult): Future[MainFileTree] = synchronized {
    cleanTree()
    MainFileTreeBuilder
      .build(result, visitors(result.result.baseUnit), logger)
      .map { nt =>
        tree = nt
        nt.parsedUnits.keys.foreach { removeUnit }
        tree
      }
  }

  def getReferenceStack(uri: String): Seq[ReferenceStack] =
    tree.references.get(uri).map(db => db.references.toSeq).getOrElse(Nil)

  def documentLinks(): Map[String, Seq[DocumentLink]] =
    tree.documentLinks

  def aliases(): Seq[AliasInfo] =
    tree.aliases

  def relationships(): Seq[RelationshipLink] =
    tree.nodeRelationships
}
