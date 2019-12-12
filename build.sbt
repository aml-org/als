import Dependencies.deps
import org.scalajs.core.tools.linker.ModuleKind
import org.scalajs.core.tools.linker.backend.OutputMode
import org.scalajs.sbtplugin.ScalaJSPlugin.AutoImport.scalaJSOutputMode
import sbt.File
import sbt.Keys.{mainClass, packageOptions}
import sbtcrossproject.CrossPlugin.autoImport.crossProject

import scala.sys.process.Process
import scala.language.postfixOps
import scala.sys.process._

name := "api-language-server"

version := deps("version")

jsEnv := new org.scalajs.jsenv.nodejs.NodeJSEnv()

publish := {}

lazy val workspaceDirectory: File =
  sys.props.get("sbt.mulesoft") match {
    case Some(x) => file(x)
    case _       => Path.userHome / "mulesoft"
  }

val amfVersion = deps("amf")

lazy val amfJVMRef = ProjectRef(workspaceDirectory / "amf", "clientJVM")
lazy val amfJSRef = ProjectRef(workspaceDirectory / "amf", "clientJS")
lazy val amfLibJVM = "com.github.amlorg" %% "amf-client" % amfVersion
lazy val amfLibJS = "com.github.amlorg" %% "amf-client_sjs0.6" % amfVersion



val settings = Common.settings ++ Common.publish ++ Seq(
  organization := "org.mule.als",
  version := deps("version"),

  resolvers ++= List(
    Common.releases,
    Common.snapshots,
    Resolver.mavenLocal /*,
        Resolver.sonatypeRepo("releases"),
        Resolver.sonatypeRepo("snapshots")*/
  ),
  resolvers += "jitpack" at "https://jitpack.io",

  credentials ++= Common.credentials(),

  libraryDependencies ++= Seq(
    "com.chuusai" %% "shapeless" % "2.3.3",
    "org.scala-js" %% "scalajs-stubs" % scalaJSVersion % "provided",
    
    "org.scalatest" %%% "scalatest" % "3.0.5" % Test,
    "org.scalamock" %%% "scalamock" % "4.1.0" % Test,
    "com.lihaoyi" %%% "upickle" % "0.5.1" % Test,
    "com.github.google.guava" % "guava" % "28.1"
  )
)

lazy val common = crossProject(JSPlatform, JVMPlatform).settings(
  Seq(
    name := "als-common"
  ))
  .in(file("./als-common"))
  .settings(settings: _*)
  .jsSettings(
    scalaJSOutputMode := org.scalajs.core.tools.linker.backend.OutputMode.ECMAScript6,
    scalaJSModuleKind := ModuleKind.CommonJSModule
    //        artifactPath in (Compile, fastOptJS) := baseDirectory.value / "target" / "artifact" /"high-level.js"
  ).disablePlugins(SonarPlugin)

lazy val commonJVM = common.jvm.in(file("./als-common/jvm")).sourceDependency(amfJVMRef, amfLibJVM)
lazy val commonJS = common.js.in(file("./als-common/js")).sourceDependency(amfJSRef, amfLibJS).disablePlugins(SonarPlugin)

//
lazy val suggestions = crossProject(JSPlatform, JVMPlatform).settings(
  Seq(
    name := "als-suggestions"
  ))
  .dependsOn( common % "compile->compile;test->test")
  .in(file("./als-suggestions"))
  .settings(settings: _*)
  .jsSettings(
    skip in packageJSDependencies := false,
    scalaJSOutputMode := OutputMode.Defaults,
    scalaJSModuleKind := ModuleKind.CommonJSModule
  ).disablePlugins(SonarPlugin)

lazy val suggestionsJVM = suggestions.jvm.in(file("./als-suggestions/jvm"))
lazy val suggestionsJS = suggestions.js.in(file("./als-suggestions/js")).disablePlugins(SonarPlugin)

lazy val structure = crossProject(JSPlatform, JVMPlatform).settings(
  Seq(
    name := "als-structure"
  ))
  .dependsOn(common % "compile->compile;test->test" )
  .in(file("./als-structure"))
  .settings(settings: _*)
  .jsSettings(
    libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.9.2",
    libraryDependencies += "com.lihaoyi" %%% "upickle" % "0.5.1",
    scalaJSOutputMode := org.scalajs.core.tools.linker.backend.OutputMode.ECMAScript6,
    scalaJSModuleKind := ModuleKind.CommonJSModule
    //    artifactPath in (Compile, fastOptJS) := baseDirectory.value / "target" / "artifact" /"als-suggestions.js"
  ).disablePlugins(SonarPlugin)

lazy val structureJVM = structure.jvm.in(file("./als-structure/jvm"))
lazy val structureJS = structure.js.in(file("./als-structure/js")).disablePlugins(SonarPlugin)

lazy val actions = crossProject(JSPlatform, JVMPlatform)
  .settings(name := "als-actions")
  .settings(libraryDependencies += "org.wvlet.airframe" %% "airframe" % "19.3.7")
  .dependsOn(common % "compile->compile;test->test" )
  .in(file("./als-actions"))
  .settings(settings: _*)
  .jsSettings(
    skip in packageJSDependencies := false,
    scalaJSOutputMode := OutputMode.Defaults,
    scalaJSModuleKind := ModuleKind.CommonJSModule
  ).disablePlugins(SonarPlugin)



lazy val actionsJVM = server.jvm.in(file("./als-actions/jvm"))
lazy val actionsJS = server.js.in(file("./als-actions/js")).disablePlugins(SonarPlugin)

lazy val server = crossProject(JSPlatform, JVMPlatform)
  .settings(name := "als-server")
  .settings(libraryDependencies += "org.wvlet.airframe" %% "airframe" % "19.3.7")
  .dependsOn(actions, suggestions, structure % "compile->compile;test->test")
  .in(file("./als-server"))
  .settings(settings: _*)
  .disablePlugins(SonarPlugin)
  .jvmSettings(
    // https://mvnrepository.com/artifact/org.eclipse.lsp4j/org.eclipse.lsp4j
    libraryDependencies += "org.eclipse.lsp4j" % "org.eclipse.lsp4j" % "0.7.2",
    packageOptions in(Compile, packageBin) += Package.ManifestAttributes("Automatic-Module-Name" → "org.mule.als"),
    aggregate in assembly := true,
    mainClass in assembly := Some("org.mulesoft.als.server.lsp4j.Main"),
    mainClass in Compile := Some("org.mulesoft.als.server.lsp4j.Main"),
    scalacOptions += "-Xmixin-force-forwarders:false",
    assemblyMergeStrategy in assembly := {
      case PathList("META-INF", xs@_*) => MergeStrategy.discard
      case x => MergeStrategy.first
    }
  )
  .jsSettings(
    libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.9.2",
    libraryDependencies += "com.lihaoyi" %%% "upickle" % "0.5.1",
    scalaJSModuleKind := ModuleKind.CommonJSModule,
    artifactPath in(Compile, fullOptJS) := baseDirectory.value / "target" / "artifact" / "als-server.js"
  )



lazy val serverJVM = server.jvm.in(file("./als-server/jvm"))
lazy val serverJS = server.js.in(file("./als-server/js")).disablePlugins(SonarPlugin)

// ******************** BuildJS ****************************************************************************************

val buildJS = TaskKey[Unit]("buildJS", "Build npm module")

buildJS := {
  val _ = (fullOptJS in Compile in serverJS).value
  "./als-server/js/build-scripts/buildJs.sh" !
}

mainClass in Compile := Some("org.mulesoft.als.server.lsp4j.Main")

val buildSuggestionsJS = TaskKey[Unit]("buildSuggestionsJS", "Build suggestions npm module")

buildSuggestionsJS := {
  (fastOptJS in Compile in suggestionsJS).value
  (fullOptJS in Compile in suggestionsJS).value
  Process(
    "./build-package.sh",
    new File("./als-suggestions/js/node-package/")
  ) !
}

// ************** SONAR *******************************
lazy val token = sys.env.getOrElse("SONAR_SERVER_TOKEN", "Not found token.")
lazy val branch = sys.env.getOrElse("BRANCH_NAME", "devel")

sonarProperties ++= Map(
  "sonar.login" -> token,
  "sonar.projectKey" -> "mulesoft.als",
  "sonar.projectName" -> "ALS",
  "sonar.projectVersion" -> "1.0.0",
  "sonar.sourceEncoding" -> "UTF-8",
  "sonar.github.repository" -> "mulesoft/als",

  "sonar.branch.name" -> branch,

  "sonar.scala.coverage.reportPaths" -> "als-server/jvm/target/scala-2.12/scoverage-report/scoverage.xml,als-structure/jvm/target/scala-2.12/scoverage-report/scoverage.xml,als-suggestions/jvm/target/scala-2.12/scoverage-report/scoverage.xml,als-common/jvm/target/scala-2.12/scoverage-report/scoverage.xml",
  "sonar.sources" -> "als-server/shared/src/main/scala,als-structure/shared/src/main/scala,als-suggestions/shared/src/main/scala",
  "sonar.tests" -> "als-server/shared/src/test/scala,als-structure/shared/src/test/scala,als-suggestions/shared/src/test/scala"
)


//**************** ALIASES *********************************************************************************************
// run only one?
addCommandAlias(
  "testJVM",
  "; serverJVM/test; suggestionsJVM/test; structureJVM/test; commonJVM/test; actionsJVM/test"
)

addCommandAlias(
  "testJS",
  "; serverJS/test; suggestionsJS/test; structureJS/test; commonJS/test; actionsJS/test"
)

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs@_*) => MergeStrategy.discard
  case x => MergeStrategy.first
}



//******* fat jar*****************************
//
lazy val fat = crossProject(JSPlatform, JVMPlatform).settings(
  Seq(
    name := "api-language-server"
  )
)
  .dependsOn(suggestions, structure  , server)
  .disablePlugins(SonarPlugin)
  .enablePlugins(AssemblyPlugin)
  .in(file("./als-fat")).settings(settings: _*).jvmSettings(
  packageOptions in (Compile, packageBin) += Package.ManifestAttributes("Automatic-Module-Name" → "org.mule.als"),
  aggregate in assembly := true,
  publishArtifact in (Compile, packageBin) := false,
  addArtifact(Artifact("api-language-server", ""), assembly),
  assemblyMergeStrategy in assembly := {
    case x if x.toString.contains("commons/logging") => MergeStrategy.discard
    case x if x.toString.endsWith("JS_DEPENDENCIES") => MergeStrategy.discard
    case PathList(ps@_*) if ps.last endsWith "JS_DEPENDENCIES" => MergeStrategy.discard
    case PathList("META-INF", "MANIFEST.MF") => MergeStrategy.discard
    case x => MergeStrategy.first
  },
).jsSettings(
  libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.9.2",
  libraryDependencies += "com.lihaoyi" %%% "upickle" % "0.5.1",
  scalaJSOutputMode := org.scalajs.core.tools.linker.backend.OutputMode.ECMAScript6,
  scalaJSModuleKind := ModuleKind.CommonJSModule,
  scalaJSUseMainModuleInitializer := true,
  mainClass in Compile := Some("org.mulesoft.language.client.js.ServerProcess"),
  artifactPath in(Compile, fastOptJS) := baseDirectory.value / "target" / "artifact" / "serverProcess.js"
)

lazy val coreJVM = fat.jvm.in(file("./als-fat/jvm")).disablePlugins(SonarPlugin)
