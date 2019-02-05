package org.mulesoft.high.level

import amf.core.model.document.BaseUnit
import org.mulesoft.high.level.builder.{ASTFactoryRegistry, ProjectBuilder, UniverseProvider}
import org.mulesoft.high.level.implementation.AlsPlatform
import org.mulesoft.high.level.interfaces.IProject

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.language.postfixOps

object Core {

  def init(initOptions: InitOptions = InitOptions.AllProfiles): Future[Unit] =
    UniverseProvider
      .init(initOptions)
      .flatMap(x => ASTFactoryRegistry.init())

  def buildModel(unit: BaseUnit): Future[IProject] = buildModel(unit, AlsPlatform.default)

  def buildModel(unit: BaseUnit, platform: AlsPlatform): Future[IProject] = Future {
    ProjectBuilder.buildProject(unit, platform)
  }
}
