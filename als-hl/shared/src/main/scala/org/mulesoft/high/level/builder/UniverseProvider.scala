package org.mulesoft.high.level.builder

import amf.{Oas20Profile, ProfileName, Raml08Profile, Raml10Profile}
import amf.core.AMF
import amf.core.client.ParserConfig
import amf.core.remote._
import amf.plugins.document.vocabularies.AMLPlugin
import amf.plugins.document.webapi.validation.PayloadValidatorPlugin
import amf.plugins.document.webapi.{Oas20Plugin, Oas30Plugin, Raml08Plugin, Raml10Plugin}
import amf.plugins.features.validation.AMFValidatorPlugin
import org.mulesoft.high.level.InitOptions
import org.mulesoft.high.level.dialect.DialectUniversesProvider
import org.mulesoft.typesystem.definition.system.RamlUniverseProvider
import org.mulesoft.typesystem.nominal_interfaces.IUniverse

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object UniverseProvider {

  private val universes: mutable.Map[Vendor, IUniverse] = mutable.Map()
  private val initialized
    : ListBuffer[ProfileName] = ListBuffer() // todo change to vendor when dialect instarface supports it

  def init(initOptions: InitOptions): Future[Unit] = {

    val futures: ListBuffer[Future[Unit]] = ListBuffer()
    if (initOptions.contains(Raml10Profile) && !initialized.contains(Raml10Profile)) {
      initialized += Raml10Profile
      futures += RamlUniverseProvider.raml10Universe().map(universes(Raml10) = _)
    }
    if (initOptions.contains(Raml08Profile) && !initialized.contains(Raml08Profile)) {
      initialized += Raml08Profile
      futures += RamlUniverseProvider.raml08Universe().map(universes(Raml08) = _)
    }
    if (initOptions.contains(Oas20Profile) && !initialized.contains(Oas20Profile)) {
      initialized += Oas20Profile
      futures += RamlUniverseProvider.oas20Universe().map(universes(Oas) = _)
    }
    futures += initDialects(initOptions)
    Future.sequence(futures).map(_ => Unit)
  }

  private def initDialects(initOptions: InitOptions): Future[Unit] = {
    val optionsCopy = initOptions.filterClone(initialized.toSet)
    optionsCopy.vendors.foreach { p =>
      initialized += p
    }
    DialectUniversesProvider.buildAndLoadDialects(optionsCopy)
  }

  def universe(format: Vendor): Option[IUniverse] = universes.get(format)
}
