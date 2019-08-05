package org.mulesoft.high.level

import amf.ProfileName
import amf.core.model.document.BaseUnit
import amf.core.remote.Platform
import org.mulesoft.high.level.builder.{ASTFactoryRegistry, ProjectBuilder, UniverseProvider}
import org.mulesoft.high.level.interfaces.IProject

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.language.postfixOps

object Core {

  def init(initOptions: InitOptions = InitOptions.AllProfiles): Future[Unit] =
    UniverseProvider
      .init(initOptions)
      .flatMap(_ => ASTFactoryRegistry.init())

  def buildModel(unit: BaseUnit, platform: Platform): Future[IProject] = Future {
    ProjectBuilder.buildProject(unit, platform)
  }
}

case class CustomDialects(name: ProfileName,
                          url: String,
                          content: String,
                          customVocabulary: Option[CustomVocabulary] = None)

case class CustomVocabulary(url: String, content: String)
