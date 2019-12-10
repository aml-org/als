package org.mulesoft.als.server.modules.workspace

import amf.client.resource.ResourceNotFound
import amf.core.annotations.ReferenceTargets
import amf.core.model.document.{BaseUnit, ExternalFragment}
import amf.internal.reference.{CachedReference, ReferenceResolver}
import org.mulesoft.als.common.dtoTypes.{PositionRange, ReferenceOrigins, ReferenceStack}
import org.mulesoft.als.server.logger.Logger
import org.mulesoft.amfmanager.ParserHelper

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class ParsedUnit(bu: BaseUnit, inTree: Boolean) {
  def toCU(next: Option[Future[CompilableUnit]], mf: Option[String], stack: Seq[ReferenceStack]): CompilableUnit =
    CompilableUnit(bu.id, bu, if (inTree) mf else None, next, stack)
}

case class DiagnosticsBundle(isExternal: Boolean, references: Set[ReferenceStack]) {
  def and(stack: ReferenceStack): DiagnosticsBundle = DiagnosticsBundle(isExternal, references + stack)
}

class Repository(logger: Logger) {
  var cachables: Set[String] = Set.empty

  /**
    * replaces cachable list and removes cached units which are not on the new list
    * @param newCachables
    */
  def setCachables(newCachables: Set[String]) = {
    // { innerCachables -- newCachables }.foreach(cache.remove)
    cache.clear()
    cachables = newCachables
  }

  private val cache: mutable.Map[String, ParsedUnit] = mutable.Map.empty

  private val units: mutable.Map[String, ParsedUnit] = mutable.Map.empty

  private val innerRefs: mutable.Map[String, DiagnosticsBundle] = mutable.Map.empty

  def references: Map[String, DiagnosticsBundle] = innerRefs.toMap

  def getParsed(uri: String): Option[ParsedUnit] = units.get(uri)

  def inTree(uri: String): Boolean = treeKeys.contains(uri)

  def treeUnits(): Iterable[ParsedUnit] = units.filterKeys(innerRefs.keySet).values

  def treeKeys: collection.Set[String] = innerRefs.keySet

  def update(u: BaseUnit): Unit = {
    if (treeKeys.contains(u.id)) throw new Exception("Cannot update an unit from the tree")
    val unit = ParsedUnit(u, inTree = false)
    units.update(u.id, unit)
  }

  def cleanTree(): Unit = {
    treeKeys.foreach { k =>
      units.remove(k)
    }
    innerRefs.clear()
  }

  def newTree(main: BaseUnit): Future[Unit] = synchronized {
    cleanTree()
    indexUnit(main, ReferenceStack(Nil))
  }

  private def indexUnit(unit: BaseUnit, stack: ReferenceStack): Future[Unit] =
    indexParsedUnit(ParsedUnit(unit, inTree = true), stack)

  private def indexParsedUnit(pu: ParsedUnit, stack: ReferenceStack): Future[Unit] = {
    def isRecursive(stack: ReferenceStack, unit: ParsedUnit): Boolean =
      stack.stack.exists(_.originUri == unit.bu.id)

    val cachedF = checkCache(pu)

    val refs = if (!isRecursive(stack, pu)) { // stop: recursion
      units.put(pu.bu.id, pu)
      innerRefs.get(pu.bu.id) match {
        case Some(db) => innerRefs.update(pu.bu.id, db.and(stack))
        case _        => innerRefs.put(pu.bu.id, DiagnosticsBundle(pu.bu.isInstanceOf[ExternalFragment], Set(stack)))
      }
      pu.bu.annotations
        .collect[ReferenceTargets] { case rt: ReferenceTargets => rt }
        .map(rt =>
          indexUnit(
            pu.bu.references.find(_.id == rt.targetLocation).get,
            stack.through(ReferenceOrigins(pu.bu.location().getOrElse(pu.bu.id), PositionRange(rt.originRange)))
        ))
    } else Nil

    Future.sequence(cachedF +: refs).map(_ => Unit)
  }

  def getReferenceStack(uri: String): Seq[ReferenceStack] =
    references.get(uri).map(db => db.references.toSeq).getOrElse(Nil)

  private def checkCache(p: ParsedUnit): Future[Unit] =
    if (cache.isEmpty && cachables.contains(p.bu.id)) cache(p) else Future.unit

  private def cache(p: ParsedUnit): Future[Unit] = {
    val eventualUnit: Future[Unit] = Future {
      ParserHelper.resolve(p.bu.cloneUnit())
    }.flatMap(
      resolved =>
        ParserHelper
          .reportResolved(resolved)
          .map(r => {
            if (r.conforms) cache.put(p.bu.id, ParsedUnit(resolved, inTree = true))
            Unit
          }))
    eventualUnit
      .recoverWith {
        case e: Throwable => // ignore
          logger.error(s"Error while resolving cachable unit: ${p.bu.id}. Message ${e.getMessage}",
                       "Repository",
                       "Cache unit")
          Future.successful(Unit)
      }
  }

  def resolverCache: ReferenceResolver = { url: String =>
    cache.get(url) match {
      case Some(p) => Future.successful(CachedReference(url, p.bu, resolved = true))
      case None    => Future.failed(new ResourceNotFound("Uncached ref"))
    }
  }
}
