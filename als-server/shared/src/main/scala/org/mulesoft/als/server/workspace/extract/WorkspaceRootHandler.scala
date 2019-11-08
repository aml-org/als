package org.mulesoft.als.server.workspace.extract

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

  def extractMainFile(dir: String): Option[ConfigFileMain] = {
    val mains = configFileNames.flatMap { k =>
      val path = FileUtils.getPath(s"$dir/${k._1}", platform)
      val file = platform.fs.syncFile(path)
      k._2.extractMainFile(file.read()).map(mf => ConfigFileMain(FileUtils.getEncodedUri(file.path, platform), mf))
    }
    mains.headOption
  }

  /**
    * @key: root directory
    * @value: if config found: (configFile, mainFile)
    */
  private val rootDirs: mutable.Map[String, Option[ConfigFileMain]] = mutable.Map()

  private def checkDirForConfigFile(fileNames: Iterator[String], uri: String): Option[ConfigFileMain] =
    if (fileNames.hasNext) {
      val head: String = fileNames.next()
      val path         = FileUtils.getPath(s"$uri/$head", platform)
      val file         = platform.fs.syncFile(path)
      if (file.exists)
        configFileNames
          .get(head)
          .flatMap(_.extractMainFile(file.read()))
          .map(mainFile => ConfigFileMain(FileUtils.getEncodedUri(file.path, platform), mainFile))
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

  def getMainFiles: Set[String] =
    rootDirs.values.flatMap {
      case Some(ConfigFileMain(_, mf)) => Some(mf)
      case _                           => None
    }.toSet

  def getUsedConfigFiles: Set[String] =
    rootDirs.values.flatMap {
      case Some(ConfigFileMain(cf, _)) => Some(cf)
      case _                           => None
    }.toSet
}
