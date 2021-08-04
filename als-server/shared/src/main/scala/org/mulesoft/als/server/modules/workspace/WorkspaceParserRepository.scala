package org.mulesoft.als.server.modules.workspace

import amf.client.resource.ResourceNotFound
import amf.core.model.document.BaseUnit
import amf.internal.reference.{CachedReference, UnitCache}
import org.mulesoft.amfintegration.relationships.{AliasInfo, RelationshipLink}
import org.mulesoft.als.common.dtoTypes.ReferenceStack
import org.mulesoft.als.server.logger.Logger
import org.mulesoft.amfintegration.visitors.AmfElementDefaultVisitors
import org.mulesoft.amfintegration.AmfImplicits._
import org.mulesoft.amfintegration.{AmfParseResult, DiagnosticsBundle}
import org.mulesoft.lsp.feature.link.DocumentLink

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class WorkspaceParserRepository(logger: Logger) extends Repository[ParsedUnit] {
  var cachables: Set[String]         = Set.empty
  private def visitors(bu: BaseUnit) = AmfElementDefaultVisitors.build(bu)

  /**
    * replaces cachable list and removes cached units which are not on the new list
    * @param newCachables
    */
  def setCachables(newCachables: Set[String]): Unit = {
    tree.cleanCache()
    cachables = newCachables
  }

  private var tree: MainFileTree = EmptyFileTree

  override def getAllFilesUris: List[String] = getIsolatedUris ++ getTreeUris

  def getTreeUris: List[String] = treeUnits().map(_.bu.identifier).toList

  def getIsolatedUris: List[String] = units.values.map(_.bu.identifier).toList

  override def getUnit(uri: String): Option[ParsedUnit] =
    tree.parsedUnits.get(uri).orElse(units.get(uri))

  def references: Map[String, DiagnosticsBundle] = tree.references

  def inTree(uri: String): Boolean = tree.contains(uri)

  def treeUnits(): Iterable[ParsedUnit] = tree.parsedUnits.values

  def updateUnit(result: AmfParseResult): Unit = {
    logger.debug(s"updating ${result.baseUnit.location()}", "WorkspaceParserRepository", "updateUnit")
    if (tree.contains(result.baseUnit.identifier)) throw new Exception("Cannot update an unit from the tree")
    val unit = ParsedUnit(result.baseUnit, inTree = false, result.definedBy, result.eh, result.workspaceConfiguration)
    updateUnit(result.baseUnit.identifier, unit)
  }

  def cleanTree(): Unit = tree = EmptyFileTree

  def newTree(result: AmfParseResult): Future[Unit] = synchronized {
    cleanTree()
    MainFileTreeBuilder
      .build(result, cachables, visitors(result.baseUnit), logger)
      .map { nt =>
        tree = nt
        nt.parsedUnits.keys.foreach { removeUnit }
      }
  }

  def getReferenceStack(uri: String): Seq[ReferenceStack] =
    tree.references.get(uri).map(db => db.references.toSeq).getOrElse(Nil)

  def resolverCache: UnitCache = { url: String =>
    tree.cached(url) match {
      case Some(p) => Future.successful(CachedReference(url, p, resolved = true))
      case None    => Future.failed(new ResourceNotFound("Uncached ref"))
    }
  }

  def documentLinks(): Map[String, Seq[DocumentLink]] =
    tree.documentLinks

  def aliases(): Seq[AliasInfo] =
    tree.aliases

  def relationships(): Seq[RelationshipLink] =
    tree.nodeRelationships
}
