import java.io.{FileInputStream, FileOutputStream}
import java.util.Properties

import org.scalajs.core.tools.linker.ModuleKind
import Dependencies.deps
import sbt.Keys.{mainClass, packageOptions}
import sbtcrossproject.CrossPlugin.autoImport.crossProject


name := "api-language-server"

version := deps("version")

scalaVersion := "2.12.6"

publish := {}

jsEnv := new org.scalajs.jsenv.nodejs.NodeJSEnv()

val settings =  Common.settings ++ Common.publish ++ Seq(
    organization := "test",
    version := deps("version"),

      resolvers ++= List(
        Common.releases,
        Common.snapshots,
        Resolver.mavenLocal/*,
        Resolver.sonatypeRepo("releases"),
        Resolver.sonatypeRepo("snapshots")*/
    ),
    resolvers += "jitpack" at "https://jitpack.io",
    
    credentials ++= Common.credentials(),
    
    libraryDependencies ++= Seq(
        "com.github.amlorg" %%% "amf-webapi" % deps("amf"),
        "com.github.amlorg" %%% "amf-core" % deps("amf"),
        "com.github.amlorg" %%% "amf-client" % deps("amf"),
        "com.github.amlorg" %%% "amf-aml" % deps("amf"),
        "org.mule.common" %%% "scala-common" % deps("common"),
        "org.mule.syaml" %%% "syaml" % deps("syaml"),
        "org.scalatest"    %%% "scalatest" % "3.0.5" % Test,
        "com.chuusai" %% "shapeless" % "2.3.3",
        "com.lihaoyi" %%% "upickle" % "0.5.1" % Test
    )
)

lazy val hl = crossProject(JSPlatform, JVMPlatform).settings(
        Seq(
            name := "als-hl"
        ))
    .in(file("./als-hl"))
    .settings(settings: _*)
    .jsSettings(
        libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.9.2",
        libraryDependencies += "com.lihaoyi" %%% "upickle" % "0.5.1",
        scalaJSOutputMode := org.scalajs.core.tools.linker.backend.OutputMode.ECMAScript6,
        scalaJSModuleKind := ModuleKind.CommonJSModule
//        artifactPath in (Compile, fastOptJS) := baseDirectory.value / "target" / "artifact" /"high-level.js"
    )

lazy val hlJVM = hl.jvm.in(file("./als-hl/jvm"))
lazy val hlJS  = hl.js.in(file("./als-hl/js"))

//
lazy val suggestions = crossProject(JSPlatform, JVMPlatform).settings(
  Seq(
    name := "als-suggestions"
  ))
  .dependsOn(hl)
  .in(file("./als-suggestions"))
  .settings(settings: _*)
  .jsSettings(
    libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.9.2",
    libraryDependencies += "com.lihaoyi" %%% "upickle" % "0.5.1",
    scalaJSOutputMode := org.scalajs.core.tools.linker.backend.OutputMode.ECMAScript6,
    scalaJSModuleKind := ModuleKind.CommonJSModule
//    artifactPath in (Compile, fastOptJS) := baseDirectory.value / "target" / "artifact" /"als-suggestions.js"
  )

lazy val suggestionsJVM = suggestions.jvm.in(file("./als-suggestions/jvm"))
lazy val suggestionsJS  = suggestions.js.in(file("./als-suggestions/js"))

lazy val outline = crossProject(JSPlatform, JVMPlatform).settings(
  Seq(
    name := "als-outline"
  ))
  .dependsOn(hl)
  .in(file("./als-outline"))
  .settings(settings: _*)
  .jsSettings(
    libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.9.2",
    libraryDependencies += "com.lihaoyi" %%% "upickle" % "0.5.1",
    scalaJSOutputMode := org.scalajs.core.tools.linker.backend.OutputMode.ECMAScript6,
    scalaJSModuleKind := ModuleKind.CommonJSModule
    //    artifactPath in (Compile, fastOptJS) := baseDirectory.value / "target" / "artifact" /"als-suggestions.js"
  )

lazy val outlineJVM = outline.jvm.in(file("./als-outline/jvm"))
lazy val outlineJS  = outline.js.in(file("./als-outline/js"))

lazy val server = crossProject(JSPlatform, JVMPlatform).settings(
    Seq(
        name := "als-server"
    )
  )
  .dependsOn(suggestions, outline)
  .in(file("./als-server")).settings(settings: _*).jvmSettings(
    packageOptions in (Compile, packageBin) += Package.ManifestAttributes("Automatic-Module-Name" → "org.mule.als"),
    aggregate in assembly := true
).jsSettings(
    libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.9.2",
    libraryDependencies += "com.lihaoyi" %%% "upickle" % "0.5.1",
    scalaJSModuleKind := ModuleKind.CommonJSModule,
    scalaJSUseMainModuleInitializer := true,
    scalaJSOutputMode := org.scalajs.core.tools.linker.backend.OutputMode.ECMAScript6
)



lazy val serverJVM = server.jvm.in(file("./als-server/jvm"))
lazy val serverJS  = server.js.in(file("./als-server/js"))

val buildJS_MSLSP = TaskKey[Unit]("buildJSMSLSP")
val buildJS_WEBLSP = TaskKey[Unit]("buildJSWEBLSP") // todo: commented tasks?

val buildJS = TaskKey[Unit]("buildJS", "Build npm module")

buildJS := {
    val _ = (fullOptJS in Compile in serverJS).value
    "./als-server/js/build-scripts/buildjs.sh".!
}

mainClass in Compile := Some("org.mulesoft.language.client.js.Main")


// ************** SONAR *******************************

enablePlugins(SonarRunnerPlugin)

val setSonarProperties = TaskKey[Unit](
    "setSonarProperties",
    "Set sonar properties!"
)

setSonarProperties := {
    lazy val url = sys.env.getOrElse("SONAR_SERVER_URL", "Not found url.")
    lazy val token = sys.env.getOrElse("SONAR_SERVER_TOKEN", "Not found token.")

    val values = Map(
        "sonar.host.url" -> url,
        "sonar.login" -> token,
        "sonar.projectKey" -> "mulesoft.als",
        "sonar.projectName" -> "ALS",
        "sonar.projectVersion" -> "1.0.0",
        "sonar.sourceEncoding" -> "UTF-8",
        "sonar.github.repository" -> "mulesoft/als",
        "sonar.modules" -> "als-hl,als-server",
        "als-server.sonar.sources" -> "shared/src/main/scala",
        "als-server.sonar.scoverage.reportPath" -> "jvm/target/scala-2.12/scoverage-report/scoverage.xml",
        "als-hl.sonar.sources" -> "shared/src/main/scala",
        "als-hl.sonar.scoverage.reportPath" -> "jvm/target/scala-2.12/scoverage-report/scoverage.xml",
        "als-server.sonar.tests" -> "shared/src/test/scala",
        "als-hl.sonar.tests" -> "shared/src/test/scala"
    )
    sonarProperties := values

    val p = new Properties()
    values.foreach(v => p.put(v._1, v._2))
    val stream = new FileOutputStream(file("./sonar-project.properties"))
    p.store(stream, null)
    stream.close()
}

val runSonar = TaskKey[Unit](
    "runSonar",
    "Run sonar!")
runSonar := {

    //  sonarRunnerOptions := Seq(
    //    "-D",
    //    s"sonar.host.url=$url",
    //    "-D",
    //    s"sonar.login=$token"
    //  )

    //  val a = generateSonarConfiguration.value

    setSonarProperties.value
    sonar.value
}


//**************** ALIASES *********************************************************************************************
// run only one?
addCommandAlias(
  "testJVM",
  "; serverJVM/test; suggestionsJVM/test; outlineJVM/test; hlJVM/test"
)

addCommandAlias(
  "testJS",
  "; serverJS/test; suggestionsJS/test; outlineJVM/test; hlJS/test"
)

