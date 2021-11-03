import sbt._
import java.util.Properties
import collection.JavaConverters._
import java.io.{File, FileInputStream}

object Dependencies {

  val deps: Map[String, String] = load()

  private def load(): Map[String, String] = {
    val props             = new Properties()
    val sourceModeEnabled = java.lang.Boolean.getBoolean("sbt.sourcemode")
    val versionsFile = if (sourceModeEnabled) {
      Common.workspaceDirectory / "als" / "dependencies.properties"
    } else {
      file("dependencies.properties")
    }
    props.load(new FileInputStream(versionsFile))
    props.entrySet().asScala.map(e => e.getKey.toString -> e.getValue.toString).toMap
  }
}
