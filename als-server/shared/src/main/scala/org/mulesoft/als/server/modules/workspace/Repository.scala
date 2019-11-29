package org.mulesoft.als.server.modules.workspace

import amf.client.resource.ResourceNotFound
import amf.core.annotations.ReferenceTargets
import amf.core.model.document.{BaseUnit, ExternalFragment}
import amf.internal.reference.{CachedReference, ReferenceResolver}
import org.mulesoft.als.server.logger.Logger
import org.mulesoft.amfmanager.ParserHelper

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class ParsedUnit(bu: BaseUnit, inTree: Boolean) {
  def toCU(next: Option[Future[CompilableUnit]], mf: Option[String]): CompilableUnit =
    CompilableUnit(bu.id, bu, if (inTree) mf else None, next)
}

case class ReferenceStack(stack: Seq[ReferenceTargets])

class Repository(cachables: Set[String], logger: Logger) {
  private val cache: mutable.Map[String, ParsedUnit] = mutable.Map.empty

  /**
    * Contains all tree units with the corresponding stack
    */
  private val unitsWithStack: mutable.Set[(ParsedUnit, ReferenceStack)] = mutable.Set()

  /**
    * contains all units not refered by the Project
    */
  private val isolatedUnits: mutable.Map[String, ParsedUnit] = mutable.Map.empty

  // todo: difference between units and cache?
  private def units: Map[String, ParsedUnit] =
    isolatedUnits.toMap ++ mappedUnits.map(t => t._1 -> t._2._1)

  /**
    *
    * @return a map indexed by BU id, containing each ParsedUnit and the corresponding Stack
    */
  private def mappedUnits: Map[String, (ParsedUnit, Seq[ReferenceStack])] =
    unitsWithStack.groupBy(_._1.bu.id).map(v => v._1 -> (v._2.head._1, v._2.map(_._2).toSeq))

  /**
    *
    * Map key = URI, Boolean = isExternal, Set = All stacks leading to this URI
    */
  def references: Map[String, (Boolean, Set[ReferenceStack])] =
    unitsWithStack
      .groupBy(_._1.bu.id)
      .map(v => v._1 -> (units.get(v._1).forall(_.bu.isInstanceOf[ExternalFragment]), v._2.map(_._2).toSet))

  def getParsed(uri: String): Option[ParsedUnit] = units.get(uri)

  def inTree(uri: String): Boolean = treeKeys.contains(uri)

  def treeUnits(): Iterable[ParsedUnit] = units.values.filter(_.inTree)

  def treeKeys: collection.Set[String] = units.filter(_._2.inTree).keySet

  def update(u: BaseUnit): Unit = {
    if (treeKeys.contains(u.id)) throw new Exception("Cannot update an unit from the tree")
    val unit = ParsedUnit(u, inTree = false)
    isolatedUnits.update(u.id, unit)
  }

  def cleanTree(): Unit = unitsWithStack.clear()

  //    treeKeys.foreach(isolatedUnits.remove)

  def newTree(main: BaseUnit): Future[Unit] = synchronized {
    cleanTree()
    indexUnit(main, Nil)
  }

  private def indexUnit(unit: BaseUnit, stack: Seq[ReferenceTargets]): Future[Unit] =
    indexParsedUnit(ParsedUnit(unit, inTree = true), stack: Seq[ReferenceTargets])

  private def indexParsedUnit(pu: ParsedUnit, stack: Seq[ReferenceTargets]): Future[Unit] = {
    val cachedF = checkCache(pu)

    val unitWithStack = (pu, ReferenceStack(stack))
    val refs = if (unitsWithStack.add(unitWithStack)) { // stop: recursion
      pu.bu.annotations
        .collect[ReferenceTargets] { case rt: ReferenceTargets => rt }
        .map(rt => indexUnit(pu.bu.references.find(_.id == rt.targetLocation).get, rt +: stack))
    } else Nil

    Future.sequence(cachedF +: refs).map(_ => Unit)
  }

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
