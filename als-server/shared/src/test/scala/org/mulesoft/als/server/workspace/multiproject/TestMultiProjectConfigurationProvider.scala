package org.mulesoft.als.server.workspace.multiproject

import org.mulesoft.als.common.DirectoryResolver
import org.mulesoft.als.server.modules.workspace.DefaultProjectConfigurationProvider
import org.mulesoft.als.server.textsync.TextDocumentContainer
import org.mulesoft.amfintegration.amfconfiguration.EditorConfiguration
import org.mulesoft.amfintegration.amfconfiguration.executioncontext.Implicits.global

import scala.concurrent.Future


case class TestMultiProjectConfigurationProvider(
                                                  container: TextDocumentContainer,
                                                  editorConfiguration: EditorConfiguration,
                                                  directoryResolver: DirectoryResolver
                                                ) extends DefaultProjectConfigurationProvider(container, editorConfiguration) {
  private def isProject(folder: String): Boolean = folder.split("/").last.startsWith("project")

  override def getProjectsFromFolder(folder: String): Future[Seq[String]] = {
    if(isProject(folder)) Future.successful(Seq(folder))
    else {
      directoryResolver.isDirectory(folder).flatMap{
        case true =>
          directoryResolver.readDir(folder)
        case false =>
          Future.successful(Seq.empty)
      }.flatMap(folders => Future.sequence(folders.map(trailSlash(folder).concat).map(getProjectsFromFolder)).map(_.flatten))
    }
  }
  private def trailSlash(f: String): String =
    if (f.endsWith("/")) f else s"$f/"
}
