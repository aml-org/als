package org.mulesoft.high.level.builder

import amf.core.remote.{Oas, Raml08, Raml10, Vendor}
import org.mulesoft.typesystem.definition.system.RamlUniverseProvider
import org.mulesoft.typesystem.nominal_interfaces.IUniverse

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object UniverseProvider {

    private val universes:mutable.Map[Vendor,IUniverse] = mutable.Map()

    def init():Future[Unit] = {
        if(universes.nonEmpty) Future.successful()
        else
            Future.sequence(
                Seq(RamlUniverseProvider.raml10Universe().map(universes(Raml10)=_) ,
                    RamlUniverseProvider.raml08Universe().map(universes(Raml08)=_) ,
                    RamlUniverseProvider.oas20Universe().map(universes(Oas)=_) ))
              .map( _ => Unit )
    }

    def universe(format:Vendor):Option[IUniverse] = universes.get(format)
}
