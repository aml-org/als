package org.mulesoft.high.level

import amf.core.model.document.BaseUnit
import amf.core.remote.Platform
import org.mulesoft.high.level.builder.{ASTFactoryRegistry, ProjectBuilder, UniverseProvider}
import org.mulesoft.high.level.implementation.PlatformFsProvider
import org.mulesoft.high.level.interfaces.{IFSProvider, IProject}

import scala.language.postfixOps
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Core {

  def init(initOptions: InitOptions = InitOptions.AllProfiles): Future[Unit] =
    UniverseProvider
      .init(initOptions)
      .flatMap(x => ASTFactoryRegistry.init())

  def buildModel(unit: BaseUnit, platform: Platform): Future[IProject] = buildModel(unit, PlatformFsProvider(platform))

  def buildModel(unit: BaseUnit, fsResolver: IFSProvider): Future[IProject] = Future {
    ProjectBuilder.buildProject(unit, fsResolver)
  }
}
