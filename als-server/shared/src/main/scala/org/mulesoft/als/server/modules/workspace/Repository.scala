package org.mulesoft.als.server.modules.workspace

import amf.client.resource.ResourceNotFound
import amf.core.model.document.BaseUnit
import amf.internal.reference.{CachedReference, ReferenceResolver}
import org.mulesoft.als.actions.common.AliasInfo
import org.mulesoft.als.common.dtoTypes.ReferenceStack
import org.mulesoft.als.server.logger.Logger
import org.mulesoft.als.server.modules.workspace.references.visitors.AmfElementDefaultVisitors
import org.mulesoft.amfmanager.AmfParseResult
import org.mulesoft.amfmanager.BaseUnitImplicits._
import org.mulesoft.lsp.feature.common.Location
import org.mulesoft.lsp.feature.link.DocumentLink

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
case class ParsedUnit(bu: BaseUnit, inTree: Boolean) {
  def toCU(next: Option[Future[CompilableUnit]],
           mf: Option[String],
           stack: Seq[ReferenceStack],
           isDirty: Boolean = false): CompilableUnit =
    CompilableUnit(bu.location().getOrElse(bu.id), bu, if (inTree) mf else None, next, stack, isDirty)
}

case class DiagnosticsBundle(isExternal: Boolean, references: Set[ReferenceStack]) {
  def and(stack: ReferenceStack): DiagnosticsBundle = DiagnosticsBundle(isExternal, references + stack)
}

class Repository(logger: Logger) {
  var cachables: Set[String] = Set.empty
  private val visitors       = AmfElementDefaultVisitors.build()

  /**
    * replaces cachable list and removes cached units which are not on the new list
    * @param newCachables
    */
  def setCachables(newCachables: Set[String]): Unit = {
    // possible optimization: { innerCachables -- newCachables }.foreach(cache.remove)
    tree.cleanCache()
    cachables = newCachables
  }

  private var tree: MainFileTree = EmptyFileTree

  private val units: mutable.Map[String, ParsedUnit] = mutable.Map.empty

  def getAllFilesUris: List[String] = getIsolatedUris ++ getTreeUris

  def getTreeUris: List[String] = treeUnits().map(_.bu.identifier).toList

  def getIsolatedUris: List[String] = units.values.map(_.bu.identifier).toList

  def getParsed(uri: String): Option[ParsedUnit] = tree.parsedUnits.get(uri).orElse(units.get(uri))

  def references: Map[String, DiagnosticsBundle] = tree.references

  def inTree(uri: String): Boolean = tree.contains(uri)

  def treeUnits(): Iterable[ParsedUnit] = tree.parsedUnits.values

  def update(bu: BaseUnit): Unit = {
    if (tree.contains(bu.identifier)) throw new Exception("Cannot update an unit from the tree")
    val unit = ParsedUnit(bu, inTree = false)
    units.update(bu.identifier, unit)
  }

  def cleanTree(): Unit = tree = EmptyFileTree

  def newTree(result: AmfParseResult): Future[Unit] = synchronized {
    cleanTree()
    MainFileTreeBuilder
      .build(result.eh, result.baseUnit, cachables, visitors, logger)
      .map(nt => {
        tree = nt
        nt.parsedUnits.keys.foreach(units.remove)
      })
  }

  def getReferenceStack(uri: String): Seq[ReferenceStack] =
    tree.references.get(uri).map(db => db.references.toSeq).getOrElse(Nil)

  def resolverCache: ReferenceResolver = { url: String =>
    tree.cached(url) match {
      case Some(p) => Future.successful(CachedReference(url, p, resolved = true))
      case None    => Future.failed(new ResourceNotFound("Uncached ref"))
    }
  }

  def getDocumentLinks(): Map[String, Seq[DocumentLink]] =
    tree.documentLinks

  def getAliases(): Seq[AliasInfo] =
    tree.aliases

  def getRelationships(): Seq[(Location, Location)] =
    tree.nodeRelationships
}
