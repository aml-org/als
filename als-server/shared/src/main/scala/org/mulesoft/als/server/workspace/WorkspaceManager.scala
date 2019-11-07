package org.mulesoft.als.server.workspace

import org.mulesoft.lsp.workspace.WorkspaceService

class WorkspaceManager(workspaceRootHandler: WorkspaceRootHandler) extends WorkspaceService {

  /* If there was a change in a valid config file, I should update my info */
//  override def didChangeWatchedFiles(params: DidChangeWatchedFilesParams): Unit = {
//    params.changes
//      .filter(ce => {
//        configFileNames.exists(cfn => {
//          rootDirs.keySet.exists(rd => {
//            ce.uri == s"$rd${cfn._1}"
//          })
//        })
//      })
//      .foreach(fe => {
//        val uriDir = fe.uri.substring(0, fe.uri.lastIndexOf('/'))
//        addRootDir(uriDir)
//      })
//  }
}
