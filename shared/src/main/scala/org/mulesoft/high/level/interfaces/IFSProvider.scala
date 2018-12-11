package org.mulesoft.high.level.interfaces

import scala.language.postfixOps
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait IFSProvider {

    def content(fullPath: String): Future[String]
//
//    def contentDirName(content: IEditorStateProvider): String

    def dirName(fullPath: String): String

//    def exists(fullPath: String): Boolean

    def existsAsync(path: String): Future[Boolean]

    def resolve(contextPath: String, relativePath: String): Option[String]

    def isDirectory(fullPath: String): Boolean

//    def readDir(fullPath: String): Seq[String]

    def readDirAsync(path: String): Future[Seq[String]]

    def isDirectoryAsync(path: String): Future[Boolean]
}
