package org.mulesoft.high.level.builder

import amf.core.remote._

import scala.language.postfixOps
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object ASTFactoryRegistry {

    def getFactory(format:Vendor):Option[IASTFactory] = format match {
        case Raml10 => Some(RAML10ASTFactory.instance)
        case Raml08 => Some(RAML08ASTFactory.instance)
        case Oas => Some(OAS20ASTFactory.instance)
        case Oas20 => Some(OAS20ASTFactory.instance)
        case _ => None
    }

    def init():Future[Unit] = {
        Future.sequence(Seq(RAML10ASTFactory.init() , RAML08ASTFactory.init() , OAS20ASTFactory.init() )).map(x=>{})
    }
}
