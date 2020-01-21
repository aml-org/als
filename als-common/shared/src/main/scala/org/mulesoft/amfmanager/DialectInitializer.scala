package org.mulesoft.amfmanager

import amf.ProfileName
import org.mulesoft.amfmanager.dialect.DialectUniversesProvider
import org.mulesoft.lsp.server.AmfEnvHandler

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object DialectInitializer {

  private val initialized
    : mutable.Map[ProfileName, Future[Unit]] = mutable.Map() // todo change to vendor when dialect instarface supports it

  def init(initOptions: InitOptions, amfEnvHandler: AmfEnvHandler): Future[Unit] = {

    val futures: ListBuffer[Future[Unit]] = ListBuffer()
    futures += initDialects(initOptions, amfEnvHandler: AmfEnvHandler)
    Future.sequence(futures).map(_ => Unit)
  }

  private def initDialects(initOptions: InitOptions, amfEnvHandler: AmfEnvHandler): Future[Unit] = {
    val optionsCopy = initOptions.filterClone(initialized.keys.toSet)
    val f           = DialectUniversesProvider.buildAndLoadDialects(optionsCopy, amfEnvHandler)
    optionsCopy.vendors.foreach { p =>
      initialized.put(p, f)
    }
    f
  }

  def removeInitialized(profileName: ProfileName): Option[Future[Unit]] = initialized.remove(profileName)
}
