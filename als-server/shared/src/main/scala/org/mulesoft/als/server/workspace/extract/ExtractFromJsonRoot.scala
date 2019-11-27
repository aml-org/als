package org.mulesoft.als.server.workspace.extract

import amf.core.remote.Platform
import org.mulesoft.common.io.SyncFile
import org.yaml.model.{YDocument, YMap, YMapEntry}
import org.yaml.parser.JsonParser

class ExtractFromJsonRoot(cs: CharSequence) {

  val rootMap: Iterable[YMapEntry] = {
    JsonParser(cs).parse(false).headOption match {
      case Some(d: YDocument) =>
        d.node.value match {
          case y: YMap => y.entries
          case _       => Nil
        }
      case _ => Nil
    }
  }

  lazy val getMain: Option[String] =
    rootMap.find(_.key.asScalar.exists(_.text == "main")).flatMap(_.value.asScalar.map(_.text))
}

object ExchangeConfigReader extends ConfigReader {
  override val configFileName: String = "exchange.json"

  override protected def buildConfig(file: SyncFile, path: String, platform: Platform): Option[WorkspaceConf] = {
    val root = new ExtractFromJsonRoot(file.read())
    root.getMain.map { m =>
      val dependencies = getSubList(platform.fs.syncFile(path), platform)

      WorkspaceConf(file.path, m, dependencies)
    }
  }

  private def getSubList(dir: SyncFile, platform: Platform): Set[String] = {
    if (dir.list != null)
      findDependencies(dir.list.map(l => platform.fs.syncFile(dir.path + "/" + l)).filter(_.isDirectory), platform)
    else Set.empty
  }

  private def findDependencies(subDirs: Array[SyncFile], platform: Platform): Set[String] = {
    if (subDirs.nonEmpty) {
      val (dependencies, others) = subDirs.partition(_.list.contains(configFileName))
      val mains                  = dependencies.flatMap(d => new ExtractFromJsonRoot(d.read()).getMain.map(m => d.path + "/" + m))
      mains.toSet ++ findDependencies(
        others.flatMap(o => o.list.map(so => platform.fs.syncFile(o.path + "/" + so))).filter(_.isDirectory),
        platform)
    } else Set.empty
  }

}
