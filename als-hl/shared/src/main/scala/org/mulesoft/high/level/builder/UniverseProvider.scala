package org.mulesoft.high.level.builder

import amf.core.remote._
import amf.{Oas20Profile, ProfileName, Raml08Profile, Raml10Profile}
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
    : mutable.Map[ProfileName, Future[Unit]] = mutable.Map() // todo change to vendor when dialect instarface supports it

  def init(initOptions: InitOptions): Future[Unit] = {

    val futures: ListBuffer[Future[Unit]] = ListBuffer()
    if (initOptions.contains(Raml10Profile) && !initialized.contains(Raml10Profile)) {
      val f = RamlUniverseProvider.raml10Universe().map(universes(Raml10) = _)
      futures += f
      initialized.put(Raml10Profile, f)
    } else futures += initialized(Raml10Profile)
    if (initOptions.contains(Raml08Profile) && !initialized.contains(Raml08Profile)) {
      val f = RamlUniverseProvider.raml08Universe().map(universes(Raml08) = _)
      futures += f
      initialized.put(Raml08Profile, f)
    } else futures += initialized(Raml08Profile)
    if (initOptions.contains(Oas20Profile) && !initialized.contains(Oas20Profile)) {
      val f = RamlUniverseProvider.oas20Universe().map(universes(Oas) = _)
      futures += f
      initialized.put(Oas20Profile, f)
    } else futures += initialized(Oas20Profile)

    futures += initDialects(initOptions)
    Future.sequence(futures).map(_ => Unit)
  }

  private def initDialects(initOptions: InitOptions): Future[Unit] = {
    val optionsCopy = initOptions.filterClone(initialized.keys.toSet)
    val f           = DialectUniversesProvider.buildAndLoadDialects(optionsCopy)
    optionsCopy.vendors.foreach { p =>
      initialized.put(p, f)
    }
    f
  }

  def universe(format: Vendor): Option[IUniverse] = universes.get(format)
}
