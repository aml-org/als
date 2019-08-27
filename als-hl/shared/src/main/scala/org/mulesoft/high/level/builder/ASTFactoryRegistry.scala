package org.mulesoft.high.level.builder

import amf.core.remote._

import scala.language.postfixOps
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object ASTFactoryRegistry {

  def getFactory(format: Vendor): Option[IASTFactory] = format match {
    case Raml08 => Some(RAML08ASTFactory.instance)
    case _      => None
  }

  def init(): Future[Unit] = {
    Future.sequence(Seq(RAML08ASTFactory.init())).map(x => {})
  }
}
