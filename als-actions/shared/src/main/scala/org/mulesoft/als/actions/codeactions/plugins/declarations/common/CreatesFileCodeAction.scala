package org.mulesoft.als.actions.codeactions.plugins.declarations.common

import org.mulesoft.als.actions.codeactions.plugins.base.CodeActionRequestParams

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait CreatesFileCodeAction {
  protected val params: CodeActionRequestParams

  protected val extension: String
  protected val relativeUri: String       = params.uri.substring(0, params.uri.lastIndexOf('/') + 1)
  protected def completeUri(name: String) = s"$relativeUri$name.$extension"

  protected def createFileUri(baseName: String): Future[String] = finalName(baseName).map(completeUri)

  protected def finalName(baseName: String, c: Option[Int] = None): Future[String] = {
    val maybeName = s"$baseName${c.getOrElse("")}"
    params.directoryResolver
      .exists(completeUri(maybeName))
      .flatMap {
        case false => Future.successful(maybeName)
        case true  => finalName(baseName, Some(c.getOrElse(0) + 1))
      }
  }

}
