package org.mulesoft.als

import org.mulesoft.als.server.protocol.LanguageServer
import org.mulesoft.als.server.protocol.configuration.{AlsInitializeParams, AlsInitializeResult}
import org.mulesoft.als.server.workspace.WorkspaceManager

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

package object server {

  implicit class LanguageServerImplicit(ls: LanguageServer) {

    /** After initializing the LanguageServer, waits for every WorkspaceContentManager to initialize in order to avoid
      * staged merges in tests
      * @param params
      * @return
      */
    def testInitialize(params: AlsInitializeParams): Future[AlsInitializeResult] = {
      ls.initialize(params).flatMap { r =>
        ls.workspaceService match {
          case wf: WorkspaceManager =>
            Future
              .sequence(wf.allWorkspaces().map(_.initialized))
              .map(_ => r)
          case _ => Future.successful(r)
        }
      }
    }
  }
}
