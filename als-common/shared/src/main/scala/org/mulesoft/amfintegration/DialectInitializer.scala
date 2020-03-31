package org.mulesoft.amfmanager

import amf.ProfileName
import amf.internal.environment.Environment
import amf.plugins.document.vocabularies.DialectsRegistry
import org.mulesoft.als.CompilerEnvironment
import org.mulesoft.amfmanager.dialect.DialectUniversesProvider

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object DialectInitializer {

  private val initialized
    : mutable.Map[ProfileName, Future[Unit]] = mutable.Map() // todo change to vendor when dialect instarface supports it

  def init(initOptions: InitOptions,
           compilerEnvironment: CompilerEnvironment[AmfParseResult, Environment]): Future[Unit] = {

    val futures: ListBuffer[Future[Unit]] = ListBuffer()
    futures += initDialects(initOptions, compilerEnvironment)
    Future.sequence(futures).map(_ => Unit)
  }

  private def initDialects(initOptions: InitOptions,
                           compilerEnvironment: CompilerEnvironment[AmfParseResult, Environment]): Future[Unit] = {
    val optionsCopy = initOptions.filterClone(initialized.keys.toSet)
    val f           = DialectUniversesProvider.buildAndLoadDialects(optionsCopy, compilerEnvironment)
    optionsCopy.vendors.foreach { p =>
      initialized.put(p, f)
    }
    f
  }

  def removeInitialized(profileName: ProfileName): Option[Future[Unit]] = initialized.remove(profileName)
}
