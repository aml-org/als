import Dependencies.deps
import org.scalajs.core.tools.linker.ModuleKind
import org.scalajs.core.tools.linker.backend.OutputMode
import org.scalajs.sbtplugin.ScalaJSPlugin.AutoImport.{fastOptJS, scalaJSOutputMode}
import sbt.File
import sbt.Keys.{libraryDependencies, mainClass, packageOptions}
import sbtcrossproject.CrossPlugin.autoImport.crossProject

import scala.sys.process.Process
import scala.language.postfixOps
import scala.sys.process.Process

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

val orgSettings = Seq(
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
  )
)

val settings = Common.settings ++ Common.publish ++ orgSettings

/** ALS common */

lazy val common = crossProject(JSPlatform, JVMPlatform).settings(
  Seq(
    name := "als-common"
  ))
  .in(file("./als-common"))
  .dependsOn(lsp)
  .settings(settings: _*)
  .jsSettings(
    scalaJSOutputMode := org.scalajs.core.tools.linker.backend.OutputMode.ECMAScript6,
    scalaJSModuleKind := ModuleKind.CommonJSModule
    //        artifactPath in (Compile, fastOptJS) := baseDirectory.value / "target" / "artifact" /"high-level.js"
  ).disablePlugins(SonarPlugin)

lazy val commonJVM = common.jvm.in(file("./als-common/jvm")).sourceDependency(amfJVMRef, amfLibJVM)
lazy val commonJS = common.js.in(file("./als-common/js")).sourceDependency(amfJSRef, amfLibJS).disablePlugins(SonarPlugin)

/** ALS LSP */

lazy val lsp = crossProject(JSPlatform, JVMPlatform).settings(
  Seq(
    name := "als-lsp"
  ))
  .in(file("./als-lsp"))
  .settings(settings: _*)
  .jvmSettings(
    libraryDependencies += "org.eclipse.lsp4j" % "org.eclipse.lsp4j" % "0.9.0",
    libraryDependencies += "org.scala-lang.modules" % "scala-java8-compat_2.12" % "0.8.0"
  )
  .jsSettings(
    scalaJSOutputMode := org.scalajs.core.tools.linker.backend.OutputMode.ECMAScript6,
    scalaJSModuleKind := ModuleKind.CommonJSModule
    //        artifactPath in (Compile, fastOptJS) := baseDirectory.value / "target" / "artifact" /"high-level.js"
  ).disablePlugins(SonarPlugin)

lazy val lspJVM = lsp.jvm.in(file("./als-lsp/jvm"))
lazy val lspJS = lsp.js.in(file("./als-lsp/js"))

/** ALS suggestions */

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

/** ALS structure */

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

/** ALS actions */

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

/** ALS server */

val installJsDependencies = TaskKey[Unit]("installJsDependencies", "Runs npm i")

lazy val server = crossProject(JSPlatform, JVMPlatform)
  .settings(name := "als-server")
  .settings(libraryDependencies += "org.wvlet.airframe" %% "airframe" % "19.3.7")
  .dependsOn(actions, suggestions, structure % "compile->compile;test->test")
  .in(file("./als-server"))
  .settings(settings: _*)
  .disablePlugins(SonarPlugin)
  .jvmSettings(
    // https://mvnrepository.com/artifact/org.eclipse.lsp4j/org.eclipse.lsp4j
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
    installJsDependencies := {
      Process("npm install",     new File("./als-server/js/node-package")) !
    },
    test in Test := ((test in Test) dependsOn installJsDependencies).value,
    artifactPath in(Test, fastOptJS) := baseDirectory.value / "node-package" / "tmp" / "als-server.js",

    scalaJSModuleKind := ModuleKind.CommonJSModule,
    libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.9.2",

    artifactPath in(Compile, fastOptJS) := baseDirectory.value / "target" / "artifact" / "als-server.js",
    artifactPath in(Compile, fullOptJS) := baseDirectory.value / "target" / "artifact" / "als-server.min.js"
  )

lazy val serverJVM = server.jvm.in(file("./als-server/jvm"))
lazy val serverJS = server.js.in(file("./als-server/js")).disablePlugins(SonarPlugin)

/** ALS node client */
val npmIClient = TaskKey[Unit]("npmIClient", "Install npm at node client")

lazy val nodeClient =  project
  .dependsOn(serverJS)
  .in(file("./als-node-client"))
  .enablePlugins(ScalaJSPlugin)
  .settings(settings: _*)
  .disablePlugins(SonarPlugin)
  .settings(settings ++ Seq(
    name := "als-node-client",

    scalaJSUseMainModuleInitializer := true,
    scalaJSModuleKind := ModuleKind.CommonJSModule,
    libraryDependencies += "io.scalajs" %%% "nodejs-core" % "0.4.2",
    mainClass in Compile := Some("org.mulesoft.als.nodeclient.Main"),

    npmIClient := {
      Process(
        "npm i",
        new File("./als-node-client/node-package/")
      ).!
    },

    test in Test := ((test in Test) dependsOn npmIClient).value,
    artifactPath in(Test, fastOptJS) := baseDirectory.value / "node-package" / "tmp" / "als-node-client.js",
    artifactPath in(Compile, fastOptJS) := baseDirectory.value / "target" / "artifact" / "als-node-client.js",
    artifactPath in(Compile, fullOptJS) := baseDirectory.value / "target" / "artifact" / "als-node-client.js"
  ))
/** ALS build tasks */
  
// Server library

val buildJsServerLibrary = TaskKey[Unit]("buildJsServerLibrary", "Build server library")

buildJsServerLibrary := {
  (fastOptJS in Compile in serverJS).value
  (fullOptJS in Compile in serverJS).value
  (installJsDependencies in serverJS).value
  val result = (Process(
    "./scripts/build.sh",
    new File("./als-server/js/node-package/")
  ).!)
  if(result != 0) throw new IllegalStateException("Node JS build.sh failed")
}

// Node client
val buildNodeJsClient = TaskKey[Unit]("buildNodeJsClient", "Build node client")

buildNodeJsClient := {
  (fastOptJS in Compile in nodeClient).value
  (fullOptJS in Compile in nodeClient).value
  (npmIClient in nodeClient).value
  val result = (Process("./scripts/build.sh",
    new File("./als-node-client/node-package/")
  ).!)
  if(result != 0) throw new IllegalStateException("Node JS build.sh failed")
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
  "; serverJVM/test; suggestionsJVM/test; structureJVM/test; commonJVM/test; actionsJVM/test; lspJVM/test; "
)

addCommandAlias(
  "testJS",
  "; serverJS/test; suggestionsJS/test; structureJS/test; commonJS/test; actionsJS/test; lspJS/test; "
)

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs@_*) => MergeStrategy.discard
  case x => MergeStrategy.first
}

//******* fat jar*****************************

lazy val fat = crossProject(JVMPlatform).settings(
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
)

lazy val coreJVM = fat.jvm.in(file("./als-fat/jvm")).disablePlugins(SonarPlugin)
