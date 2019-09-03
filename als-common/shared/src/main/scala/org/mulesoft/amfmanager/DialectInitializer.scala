package org.mulesoft.amfmanager

import amf.core.remote._
import amf.{ProfileName, Raml08Profile}
import org.mulesoft.amfmanager.dialect.DialectUniversesProvider

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object DialectInitializer {

  private val initialized
    : mutable.Map[ProfileName, Future[Unit]] = mutable.Map() // todo change to vendor when dialect instarface supports it

  def init(initOptions: InitOptions): Future[Unit] = {

    val futures: ListBuffer[Future[Unit]] = ListBuffer()
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

  def removeInitialized(profileName: ProfileName): Option[Future[Unit]] = initialized.remove(profileName)
}
