package org.mulesoft.als.suggestions.implementation

import amf.core.remote.Platform
import org.mulesoft.als.common.DirectoryResolver

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object PathCompletion {

  private def resolvePath(platform: Platform, basePath: String, path: String): String =
    platform.decodeURI(platform.resolvePath(platform.encodeURI(basePath + path.stripPrefix("/"))))

  def complete(defaultPath: String,
               additionalPath: String,
               directoryResolver: DirectoryResolver,
               platform: Platform): Future[Seq[String]] = {

    directoryResolver
      .isDirectory(defaultPath)
      .flatMap(isDir => {
        var directoryPath =
          if (!isDir)
            directoryResolver.dirName(defaultPath)
          else
            defaultPath

        if (!directoryPath.endsWith("/")) directoryPath = directoryPath + "/"

        val additionalDirectoryPath: Option[String] =
          if (additionalPath.contains('/'))
            Some(additionalPath.substring(0, additionalPath.lastIndexOf('/') + 1))
          else
            None

        val modifiedDirectoryPath: String =
          additionalDirectoryPath
            .map(resolvePath(platform, directoryPath, _))
            .getOrElse(directoryPath)

        var finalDirectoryPath = directoryPath

        val filesFuture = directoryResolver
          .isDirectory(modifiedDirectoryPath)
          .flatMap(modifiedIsDirectory => {

            if (modifiedIsDirectory) {
              finalDirectoryPath = modifiedDirectoryPath
            }

            directoryResolver.readDir(finalDirectoryPath)
          })

        filesFuture.flatMap(shortFilePaths => {
          val fullFilePaths: Seq[String] =
            shortFilePaths
              .map(resolvePath(platform, finalDirectoryPath, _))

          val filter = resolvePath(platform, directoryPath, additionalPath)

          val futures: Seq[Future[String]] = fullFilePaths
            .filter(fullFilePath => fullFilePath.startsWith(filter) && fullFilePath != defaultPath)
            .map(fullFilePath => {
              val result = fullFilePath.stripPrefix(finalDirectoryPath)
              directoryResolver
                .isDirectory(fullFilePath)
                .map({
                  case true if !result.endsWith("/") => result + "/"
                  case _                             => result
                })
            })
          Future.sequence(futures)
        })
      })
  }
}
