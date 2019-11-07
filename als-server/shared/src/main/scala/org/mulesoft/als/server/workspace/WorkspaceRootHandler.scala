package org.mulesoft.als.server.workspace

import amf.core.remote.Platform
import org.mulesoft.als.common.FileUtils

import scala.collection.mutable

class WorkspaceRootHandler(platform: Platform) {

  /**
    * @key: recognized config file
    * @value: mainFile extractor for this specific file
    */
  private val configFileNames: Map[String, MainFileExtractable] =
    Map("exchange.json" -> new ExtractFromJsonRoot("main"))

  /**
    * @key: root directory
    * @value: mainFile (if config found)
    */
  private val rootDirs: mutable.Map[String, Option[String]] = mutable.Map()

  private def checkDirForConfigFile(fileNames: Iterator[String], uri: String): Option[String] =
    if (fileNames.hasNext) {
      val head: String = fileNames.next()
      val path         = FileUtils.getPath(s"$uri/$head", platform)
      val file         = platform.fs.syncFile(path)
      if (file.exists)
        configFileNames
          .get(head)
          .flatMap(_.extractMainFile(file.read()))
          .orElse(checkDirForConfigFile(fileNames, uri))
      else
        checkDirForConfigFile(fileNames, uri)
    } else None

  def addRootDir(dir: String): Unit = synchronized {
    val uri = FileUtils.getEncodedUri(dir, platform)
    rootDirs += (uri -> checkDirForConfigFile(configFileNames.keysIterator, uri))
  }

  def removeRootDir(dir: String): Unit = synchronized {
    val uri = FileUtils.getEncodedUri(dir, platform)
    rootDirs.remove(uri)
  }

  def getRootDirs: collection.Set[String] = rootDirs.keySet

  def getMainFiles: Set[String] = rootDirs.values.flatten.toSet
}
