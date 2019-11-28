package org.mulesoft.als.server.modules.workspace

import amf.client.resource.ResourceNotFound
import amf.core.annotations.ReferenceTargets
import amf.core.model.document.{BaseUnit, ExternalFragment}
import amf.internal.reference.{CachedReference, ReferenceResolver}
import org.mulesoft.als.common.dtoTypes.PositionRange
import org.mulesoft.als.server.logger.Logger
import org.mulesoft.amfmanager.ParserHelper

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

case class ParsedUnit(bu: BaseUnit, inTree: Boolean) {
  def toCU(next: Option[Future[CompilableUnit]], mf: Option[String]): CompilableUnit =
    CompilableUnit(bu.id, bu, if (inTree) mf else None, next)
}

case class ReferenceOrigin(locationTarget: String, locationOrigin: String, range: PositionRange, isExternal: Boolean)

class Repository(cachables: Set[String], logger: Logger) {

  private val units: mutable.Map[String, ParsedUnit] = mutable.Map.empty

  private val cache: mutable.Map[String, ParsedUnit] = mutable.Map.empty

  private var references: Map[String, Set[ReferenceOrigin]] = Map()

  def getReferences: Map[String, Set[ReferenceOrigin]] = references

  private def referenceMap(): Map[String, Set[ReferenceOrigin]] =
    treeUnits()
      .flatMap(
        pu =>
          pu.bu.annotations
            .collect[ReferenceTargets] { case rt: ReferenceTargets => rt }
            .map(
              rt =>
                ReferenceOrigin(rt.targetLocation,
                                pu.bu.location().getOrElse(pu.bu.id),
                                PositionRange(rt.originRange),
                                units(rt.targetLocation).bu.isInstanceOf[ExternalFragment])))
      //          pu.bu.meta == ExternalFragmentModel)))
      .groupBy(o => o.locationTarget)
      .mapValues(v => v.toSet)

  def getParsed(uri: String): Option[ParsedUnit] = units.get(uri)

  def inTree(uri: String): Boolean = treeKeys.contains(uri)

  def treeUnits(): Iterable[ParsedUnit] = units.values.filter(_.inTree)

  def treeKeys: collection.Set[String] = units.filter(_._2.inTree).keySet

  def update(u: BaseUnit): Unit = {
    if (treeKeys.contains(u.id)) throw new Exception("Cannot update an unit from the tree")
    val unit = ParsedUnit(u, inTree = false)
    units.update(u.id, unit)
  }

  def cleanTree(): Unit = treeKeys.foreach(units.remove)

  def newTree(main: BaseUnit): Future[Unit] = synchronized {
    cleanTree()
    indexUnit(main)
      .map(_ => references = referenceMap())
  }

  private def indexUnit(unit: BaseUnit): Future[Unit] = indexParsedUnit(ParsedUnit(unit, inTree = true))

  private def indexParsedUnit(pu: ParsedUnit): Future[Unit] = {
    val cachedF = checkCache(pu)

    val refs = if (!units.contains(pu.bu.id)) { // stop: recursion
      units.put(pu.bu.id, pu)
      pu.bu.references.map(indexUnit)
    } else Nil
    Future.sequence(cachedF +: refs).map(_ => Unit)
  }

  private def checkCache(p: ParsedUnit): Future[Unit] =
    if (cache.isEmpty && cachables.contains(p.bu.id)) cache(p) else Future.unit

  private def cache(p: ParsedUnit): Future[Unit] = {
    val eventualUnit: Future[Unit] = Future({
      ParserHelper.resolve(p.bu.cloneUnit())
    }).flatMap(resolved => {
      ParserHelper
        .reportResolved(resolved)
        .map(r => {
          if (r.conforms) cache.put(p.bu.id, ParsedUnit(resolved, inTree = true))
          Unit
        })
    })
    eventualUnit
      .recoverWith {
        case e: Throwable => // ignore
          logger.error(s"Error while resolving cachable unit: ${p.bu.id}. Message ${e.getMessage}",
                       "Respotivory",
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
