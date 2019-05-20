import Dependencies.deps
import org.scalajs.core.tools.linker.ModuleKind
import org.scalajs.core.tools.linker.backend.OutputMode
import org.scalajs.sbtplugin.ScalaJSPlugin.AutoImport.scalaJSOutputMode
import sbt.File
import sbt.Keys.{mainClass, packageOptions}
import sbtcrossproject.CrossPlugin.autoImport.crossProject


name := "api-language-server"

version := deps("version")

scalaVersion := "2.12.6"

jsEnv := new org.scalajs.jsenv.nodejs.NodeJSEnv()

publish := {}

lazy val workspaceDirectory: File =
  sys.props.get("sbt.mulesoft") match {
    case Some(x) => file(x)
    case _       => Path.userHome / "mulesoft"
  }

val amfVersion = "3.3.0-SNAPSHOT"

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
    "org.mule.common" %%% "scala-common" % deps("common"),
    "org.mule.syaml" %%% "syaml" % deps("syaml"),
    "com.chuusai" %% "shapeless" % "2.3.3",
    "org.scala-js" %% "scalajs-stubs" % scalaJSVersion % "provided",
    
    "org.scalatest" %%% "scalatest" % "3.0.5" % Test,
    "org.scalamock" %%% "scalamock" % "4.1.0" % Test,
    "com.lihaoyi" %%% "upickle" % "0.5.1" % Test
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
  )

lazy val commonJVM = common.jvm.in(file("./als-common/jvm")).sourceDependency(amfJVMRef, amfLibJVM)
lazy val commonJS = common.js.in(file("./als-common/js")).sourceDependency(amfJSRef, amfLibJS)

lazy val hl = crossProject(JSPlatform, JVMPlatform).settings(
  Seq(
    name := "als-hl"
  ))
  .dependsOn(common)
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
lazy val hlJS = hl.js.in(file("./als-hl/js"))

//
lazy val suggestions = crossProject(JSPlatform, JVMPlatform).settings(
  Seq(
    name := "als-suggestions"
  ))
  .dependsOn(hl)
  .in(file("./als-suggestions"))
  .settings(settings: _*)
  .jsSettings(
    skip in packageJSDependencies := false,
    scalaJSOutputMode := OutputMode.Defaults,
    scalaJSModuleKind := ModuleKind.CommonJSModule
  )

lazy val suggestionsJVM = suggestions.jvm.in(file("./als-suggestions/jvm"))
lazy val suggestionsJS = suggestions.js.in(file("./als-suggestions/js"))

lazy val structure = crossProject(JSPlatform, JVMPlatform).settings(
  Seq(
    name := "als-structure"
  ))
  .dependsOn(hl)
  .in(file("./als-structure"))
  .settings(settings: _*)
  .jsSettings(
    libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.9.2",
    libraryDependencies += "com.lihaoyi" %%% "upickle" % "0.5.1",
    scalaJSOutputMode := org.scalajs.core.tools.linker.backend.OutputMode.ECMAScript6,
    scalaJSModuleKind := ModuleKind.CommonJSModule
    //    artifactPath in (Compile, fastOptJS) := baseDirectory.value / "target" / "artifact" /"als-suggestions.js"
  )

lazy val structureJVM = structure.jvm.in(file("./als-structure/jvm"))
lazy val structureJS = structure.js.in(file("./als-structure/js"))

lazy val server = crossProject(JSPlatform, JVMPlatform)
  .settings(Seq(
    name := "als-server",
    libraryDependencies += "org.wvlet.airframe" %% "airframe" % "19.3.7"
  ))
  .dependsOn(suggestions, structure % "compile->compile;test->test")
  .in(file("./als-server"))
  .settings(settings: _*)
  .jvmSettings(
    // https://mvnrepository.com/artifact/org.eclipse.lsp4j/org.eclipse.lsp4j
    libraryDependencies += "org.eclipse.lsp4j" % "org.eclipse.lsp4j" % "0.6.0",
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
lazy val serverJS = server.js.in(file("./als-server/js"))

// ******************** BuildJS ****************************************************************************************

val buildJS = TaskKey[Unit]("buildJS", "Build npm module")

buildJS := {
  val _ = (fullOptJS in Compile in serverJS).value
  "./als-server/js/build-scripts/buildJs.sh".!
}

mainClass in Compile := Some("org.mulesoft.als.server.lsp4j.Main")

val buildSuggestionsJS = TaskKey[Unit]("buildSuggestionsJS", "Build suggestions npm module")

buildSuggestionsJS := {
  (fastOptJS in Compile in suggestionsJS).value
  (fullOptJS in Compile in suggestionsJS).value
  Process(
    "./build-package.sh",
    new File("./als-suggestions/js/node-package/")
  ).!
}

// ************** SONAR *******************************

enablePlugins(SonarRunnerPlugin)

lazy val sonarUrl = sys.env.getOrElse("SONAR_SERVER_URL", "Not found url.")
lazy val token = sys.env.getOrElse("SONAR_SERVER_TOKEN", "Not found token.")
lazy val branch = sys.env.getOrElse("BRANCH_NAME", "devel")

sonarProperties ++= Map(
  "sonar.host.url" -> sonarUrl,
  "sonar.login" -> token,
  "sonar.projectKey" -> "mulesoft.als",
  "sonar.projectName" -> "ALS",
  "sonar.projectVersion" -> "1.0.0",
  "sonar.sourceEncoding" -> "UTF-8",
  "sonar.github.repository" -> "mulesoft/als",

  "sonar.branch.name" -> branch,

  "sonar.scala.coverage.reportPaths" -> "als-server/jvm/target/scala-2.12/scoverage-report/scoverage.xml,als-structure/jvm/target/scala-2.12/scoverage-report/scoverage.xml,als-suggestions/jvm/target/scala-2.12/scoverage-report/scoverage.xml,als-hl/jvm/target/scala-2.12/scoverage-report/scoverage.xml,als-common/jvm/target/scala-2.12/scoverage-report/scoverage.xml",
  "sonar.sources" -> "als-server/shared/src/main/scala,als-structure/shared/src/main/scala,als-suggestions/shared/src/main/scala,als-hl/shared/src/main/scala,als-common/shared/src/main/scala"
)

//**************** ALIASES *********************************************************************************************
// run only one?
addCommandAlias(
  "testJVM",
  "; serverJVM/test; suggestionsJVM/test; structureJVM/test; hlJVM/test"
)

addCommandAlias(
  "testJS",
  "; serverJS/test; suggestionsJS/test; structureJVM/test; hlJS/test"
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
  .dependsOn(suggestions, structure , hl , server)
  .in(file("./als-fat")).settings(settings: _*).jvmSettings(
  libraryDependencies += "com.github.amlorg" %%% "amf-aml" % amfVersion,
  //	packageOptions in (Compile, packageBin) += Package.ManifestAttributes("Automatic-Module-Name" → "org.mule.als"),
  //        aggregate in assembly := true,
  assemblyMergeStrategy in assembly := {
    case x if x.toString.endsWith("JS_DEPENDENCIES") => MergeStrategy.discard
    case PathList(ps@_*) if ps.last endsWith "JS_DEPENDENCIES" => MergeStrategy.discard
    case PathList("META-INF", "MANIFEST.MF") => MergeStrategy.discard
    case x => {
      MergeStrategy.first
    }
  },
  assemblyJarName in assembly := "server.jar",
  addArtifact(Artifact("api-language-server", ""), sbtassembly.AssemblyKeys.assembly)
).jsSettings(
  libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.9.2",
  libraryDependencies += "com.lihaoyi" %%% "upickle" % "0.5.1",
  scalaJSOutputMode := org.scalajs.core.tools.linker.backend.OutputMode.ECMAScript6,
  scalaJSModuleKind := ModuleKind.CommonJSModule,
  scalaJSUseMainModuleInitializer := true,
  mainClass in Compile := Some("org.mulesoft.language.client.js.ServerProcess"),
  artifactPath in(Compile, fastOptJS) := baseDirectory.value / "target" / "artifact" / "serverProcess.js"
)

lazy val coreJVM = fat.jvm.in(file("./als-fat/jvm"))
