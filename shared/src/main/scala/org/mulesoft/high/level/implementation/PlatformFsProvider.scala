// $COVERAGE-OFF$
package org.mulesoft.high.level.implementation

import amf.core.remote.{Context, Platform}
import org.mulesoft.high.level.interfaces.IFSProvider

import scala.language.postfixOps
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class PlatformFsProvider(platform:Platform) extends IFSProvider{

    override def content(fullPath: String): Future[String]
        = platform.resolve(fullPath).map(_.stream.toString)

    override def resolve(absBasePath: String, path: String): Option[String]
        = Option(Context(platform,absBasePath).resolve(path))

    override def dirName(fullPath: String): String = {
        val lastSeparatorIndex1 = fullPath.lastIndexOf(platform.fs.separatorChar)
        val lastSeparatorIndex2 = fullPath.lastIndexOf("/")
        val lastSeparatorIndex = Math.max(lastSeparatorIndex1,lastSeparatorIndex2)

        if (lastSeparatorIndex == -1 || lastSeparatorIndex == 0) {
            ""
        } else {
            fullPath.substring(0, lastSeparatorIndex)
        }
    }

//    override def exists(fullPath: String): Boolean = ???

    override def existsAsync(path: String): Future[Boolean] = {
        var p = path
        if(p.startsWith("file://")){
            p = p.substring("file://".length)
        }
        platform.fs.asyncFile(path).exists
    }

    override def isDirectory(fullPath: String): Boolean = {
        var p = fullPath
        if(p.startsWith("file://")){
            p = p.substring("file://".length)
        }
        platform.fs.syncFile(p).isDirectory
    }
//
//    override def readDir(fullPath: String): Seq[String] = ???

    override def readDirAsync(path: String): Future[Seq[String]] = {
        var p = path
        if(p.startsWith("file://")){
            p = p.substring("file://".length)
        }
        platform.fs.asyncFile(p).list.map(array=>array.toSeq)
    }

    override def isDirectoryAsync(path: String): Future[Boolean] = {
        var p = path
        if(p.startsWith("file://")){
            p = p.substring("file://".length)
        }
        platform.fs.asyncFile(p).isDirectory
    }
}

object PlatformFsProvider {
    def apply(platform: Platform):PlatformFsProvider = new PlatformFsProvider(platform)
}
// $COVERAGE-ON$