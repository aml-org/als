package org.mulesoft.als.suggestions.implementation

import org.mulesoft.high.level.implementation.AlsPlatform

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object PathCompletion {

  def complete(defaultPath: String, additionalPath: String, alsPlatform: AlsPlatform): Future[Seq[String]] = {

    alsPlatform.directoryResolver
      .isDirectory(defaultPath)
      .flatMap(isDir => {
        var directoryPath =
          if (!isDir)
            alsPlatform.directoryResolver.dirName(defaultPath)
          else
            defaultPath

        if (!directoryPath.endsWith("/")) directoryPath = directoryPath + "/"

        val additionalDirectoryPath: Option[String] =
          if (additionalPath.contains('/'))
            Some(additionalPath.substring(0, additionalPath.lastIndexOf('/') + 1))
          else
            None

        val modifiedDirectoryPath: String =
          if (additionalDirectoryPath.isDefined) {

            val resolved = alsPlatform.resolvePath(directoryPath, additionalDirectoryPath.get)

            if (resolved.isDefined) resolved.get else directoryPath
          } else {
            directoryPath
          }

        var finalDirectoryPath = directoryPath

        val filesFuture = alsPlatform.directoryResolver
          .isDirectory(modifiedDirectoryPath)
          .flatMap(modifiedIsDirectory => {

            if (modifiedIsDirectory) {
              finalDirectoryPath = modifiedDirectoryPath
            }

            alsPlatform.directoryResolver.readDir(finalDirectoryPath)
          })

        filesFuture.flatMap(shortFilePaths => {

          val fullFilePaths: Seq[String] =
            shortFilePaths
              .filter(
                shortFilePath => alsPlatform.resolvePath(finalDirectoryPath, shortFilePath).isDefined
              )
              .map(
                shortFilePath => alsPlatform.resolvePath(finalDirectoryPath, shortFilePath).get
              )

          val filter = alsPlatform.resolvePath(directoryPath, additionalPath)

          val futures: Seq[Future[String]] = fullFilePaths
            .filter(fullFilePath =>
              filter.isDefined && fullFilePath.startsWith(filter.get) && fullFilePath != defaultPath)
            .map(fullFilePath => {
              var result = fullFilePath.substring(directoryPath.length)
              alsPlatform.directoryResolver
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
