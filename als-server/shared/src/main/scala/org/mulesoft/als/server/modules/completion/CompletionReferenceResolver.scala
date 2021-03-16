package org.mulesoft.als.server.modules.completion

import amf.client.resource.ResourceNotFound
import amf.core.model.document.BaseUnit
import amf.internal.reference.{CachedReference, UnitCache}
import org.mulesoft.amfintegration.AmfImplicits._

import scala.concurrent.Future

case class CompletionReferenceResolver(unit: BaseUnit) extends UnitCache {

  val cache: Map[String, BaseUnit] = allReferences(unit).groupBy(_.id).mapValues(_.head)

  private def allReferences(baseUnit: BaseUnit): Seq[BaseUnit] = baseUnit.flatRefs

  override def fetch(url: String): Future[CachedReference] = {
    cache.get(url) match {
      case Some(p) => Future.successful(CachedReference(url, p, resolved = true))
      case None    => Future.failed(new ResourceNotFound("Uncached ref"))
    }
  }
}
