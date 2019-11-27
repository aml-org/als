package org.mulesoft.als.server.modules.workspace

import amf.client.resource.ResourceNotFound
import amf.core.model.document.BaseUnit
import amf.internal.reference.{CachedReference, ReferenceResolver}
import org.mulesoft.als.server.logger.Logger
import org.mulesoft.amfmanager.ParserHelper

import scala.collection.mutable
import scala.concurrent.Future

case class ParsedUnit(bu: BaseUnit, inTree: Boolean) {
  def toCU(next: Option[Future[CompilableUnit]], mf: Option[String]): CompilableUnit = {
    CompilableUnit(bu.id, bu, if (inTree) mf else None, next)
  }
}

class Repository(cachables: Set[String], logger: Logger) {

  private val units: mutable.Map[String, ParsedUnit] = mutable.Map.empty

  private val cache: mutable.Map[String, ParsedUnit] = mutable.Map.empty

  def getParsed(uri: String): Option[ParsedUnit] = units.get(uri)

  def inTree(uri: String): Boolean = treeKeys.contains(uri)

  def treeKeys: collection.Set[String] = units.filter(_._2.inTree).keySet

  def update(u: BaseUnit): Unit = {
    if (treeKeys.contains(u.id)) throw new Exception("Cannot update an unit from the tree")
    val unit = ParsedUnit(u, inTree = false)
    units.update(u.id, unit)
  }

  def cleanTree(): Unit = treeKeys.foreach(units.remove)

  def newTree(main: BaseUnit): Unit = synchronized {
    cleanTree()
    indexUnit(main)
  }

  private def indexUnit(unit: BaseUnit): Unit = indexParsedUnit(ParsedUnit(unit, inTree = true))

  private def indexParsedUnit(pu: ParsedUnit): Unit = {
    checkCach(pu)

    if (!units.contains(pu.bu.id)) { // stop: recursion
      units.put(pu.bu.id, pu)
      pu.bu.references.foreach(indexUnit)
    }
  }

  private def checkCach(p: ParsedUnit): Unit = if (cache.isEmpty && cachables.contains(p.bu.id)) cache(p)

  private def cache(p: ParsedUnit): Unit = {
    try {
      ParserHelper.resolve(p.bu.cloneUnit())
      cache.put(p.bu.id, p)
    } catch {
      case e: Throwable => // ignore
        logger.error(s"Error while resolving cachable unit: ${p.bu.id}. Message ${e.getMessage}",
                     "Respotivory",
                     "Cache unit")
    }
  }

  def resolverCache: ReferenceResolver = { url: String =>
    cache.get(url) match {
      case Some(p) => Future.successful(CachedReference(url, p.bu, resolved = true))
      case None    => Future.failed(new ResourceNotFound("Uncached ref"))
    }
  }
}
