import java.io.FileInputStream
import java.util.Properties

import collection.JavaConverters._
import sbt.Path

object Dependencies {

  val deps: Map[String, String] = load()

  private def load(): Map[String, String] = {
    val props = new Properties()
    props.load(new FileInputStream(Path("dependencies.properties").asFile))
    props.entrySet().asScala.map(e => e.getKey.toString -> e.getValue.toString).toMap
  }
}
