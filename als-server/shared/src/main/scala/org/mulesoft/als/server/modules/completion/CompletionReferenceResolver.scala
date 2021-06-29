package org.mulesoft.als.server.modules.completion

import amf.core.client.platform.resource.ResourceNotFound
import amf.core.client.scala.config.{CachedReference, UnitCache}
import amf.core.client.scala.model.document.BaseUnit
import org.mulesoft.amfintegration.AmfImplicits.BaseUnitImp

import scala.concurrent.Future

case class CompletionReferenceResolver(unit: BaseUnit) extends UnitCache {

  val cache: Map[String, BaseUnit] = allReferences(unit).groupBy(_.id).mapValues(_.head)

  private def allReferences(baseUnit: BaseUnit): Seq[BaseUnit] = baseUnit.flatRefs

  override def fetch(url: String): Future[CachedReference] = {
    cache.get(url) match {
      case Some(p) => Future.successful(CachedReference(url, p))
      case None    => Future.failed(new ResourceNotFound("Uncached ref"))
    }
  }
}
