import Dependencies.deps
import NpmOpsPlugin.autoImport.npmDependencies
import org.scalajs.core.tools.linker.ModuleKind
import org.scalajs.core.tools.linker.backend.OutputMode
import org.scalajs.sbtplugin.ScalaJSPlugin.AutoImport.{fastOptJS, scalaJSOutputMode}
import sbt.File
import sbt.Keys.{libraryDependencies, mainClass, packageOptions}
import sbtcrossproject.CrossPlugin.autoImport.crossProject

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

val amfVersion                       = deps("amf")
val amfCustomValidatorJSVersion      = deps("amf.custom-validator.js")
val amfCustomValidatorScalaJSVersion = deps("amf.custom-validator-scalajs")
val amfAntlrParsersVersion           = deps("amf-antlr-parsers")

val commonNpmDependencies = List(
  ("ajv", "6.12.6"),
  ("@aml-org/amf-antlr-parsers", amfAntlrParsersVersion)
)

lazy val amfJVMRef = ProjectRef(workspaceDirectory / "amf", "graphqlJVM")
lazy val amfJSRef = ProjectRef(workspaceDirectory / "amf", "graphqlJS")
lazy val amfLibJVM = "com.github.amlorg" %% "amf-graphql" % amfVersion
lazy val amfLibJS = "com.github.amlorg" %% "amf-graphql_sjs0.6" % amfVersion

lazy val customValidatorWebJVMRef =
  ProjectRef(workspaceDirectory / "amf-custom-validator-scalajs", "amfCustomValidatorWebJVM")
lazy val customValidatorWebJSRef =
  ProjectRef(workspaceDirectory / "amf-custom-validator-scalajs", "amfCustomValidatorWebJS")
lazy val customValidatorWebLibJVM = "com.github.amlorg" %% "amf-custom-validator-web" % amfCustomValidatorScalaJSVersion
lazy val customValidatorWebLibJS =
  "com.github.amlorg" %% "amf-custom-validator-web_sjs0.6" % amfCustomValidatorScalaJSVersion
lazy val customValidatorNodeJVMRef =
  ProjectRef(workspaceDirectory / "amf-custom-validator-scalajs", "amfCustomValidatorNodeJVM")
lazy val customValidatorNodeJSRef =
  ProjectRef(workspaceDirectory / "amf-custom-validator-scalajs", "amfCustomValidatorNodeJS")
lazy val customValidatorNodeLibJVM =
  "com.github.amlorg" %% "amf-custom-validator-node" % amfCustomValidatorScalaJSVersion
lazy val customValidatorNodeLibJS =
  "com.github.amlorg" %% "amf-custom-validator-node_sjs0.6" % amfCustomValidatorScalaJSVersion

lazy val npmDependencyAmfCustomValidatorWeb = s"@aml-org/amf-custom-validator-web@$amfCustomValidatorJSVersion"
lazy val npmDependencyAmfCustomValidator = s"@aml-org/amf-custom-validator@$amfCustomValidatorJSVersion"
lazy val npmDependencyAmfAntlr = s"@aml-org/amf-antlr-parsers@$amfAntlrParsersVersion"

val orgSettings = Seq(
  organization := "org.mule.als",
  version      := deps("version"),
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
    "com.chuusai"    %% "shapeless"     % "2.3.3",
    "org.scala-js"   %% "scalajs-stubs" % scalaJSVersion % "provided",
    "org.scalatest" %%% "scalatest"     % "3.0.5"        % Test,
    "org.scalamock" %%% "scalamock"     % "4.1.0"        % Test,
    "com.lihaoyi"   %%% "upickle"       % "0.5.1"        % Test
  )
)

val settings = Common.settings ++ Common.publish ++ orgSettings

////region ALS-COMMON
/** ALS common */

lazy val pathAlsCommon = "./als-common"
lazy val common = crossProject(JSPlatform, JVMPlatform)
  .settings(
    Seq(
      name := "als-common"
    )
  )
  .in(file(s"$pathAlsCommon"))
  .dependsOn(lsp)
  .settings(settings: _*)
    .jsSettings(installJsDependencies := {
      Process(
        s"npm install -E $npmDependencyAmfAntlr",
        new File(s"$pathAlsCommon/js/")
      ) #&&
        Process("npm install", new File(s"$pathAlsCommon/js/")) !
    },
    scalaJSOutputMode := org.scalajs.core.tools.linker.backend.OutputMode.ECMAScript6,
    scalaJSModuleKind := ModuleKind.CommonJSModule,
    npmDependencies ++= commonNpmDependencies
    //        artifactPath in (Compile, fastOptJS) := baseDirectory.value / "target" / "artifact" /"high-level.js"
  )
  .disablePlugins(SonarPlugin)

lazy val commonJVM = common.jvm
  .in(file(s"$pathAlsCommon/jvm"))
  .sourceDependency(amfJVMRef, amfLibJVM)
  .sourceDependency(customValidatorWebJVMRef, customValidatorWebLibJVM)
lazy val commonJS = common.js
  .in(file(s"$pathAlsCommon/js"))
  .sourceDependency(amfJSRef, amfLibJS)
  .sourceDependency(customValidatorWebJSRef, customValidatorWebLibJS)
  .disablePlugins(SonarPlugin)
////endregion

////region ALS-LSP
/** ALS LSP */

lazy val lsp = crossProject(JSPlatform, JVMPlatform)
  .settings(
    Seq(
      name := "als-lsp"
    )
  )
  .in(file("./als-lsp"))
  .settings(settings: _*)
  .jvmSettings(
    libraryDependencies += "org.eclipse.lsp4j"      % "org.eclipse.lsp4j"       % "0.12.0",
    libraryDependencies += "org.scala-lang.modules" % "scala-java8-compat_2.12" % "0.8.0"
  )
  .jsSettings(
    scalaJSOutputMode := org.scalajs.core.tools.linker.backend.OutputMode.ECMAScript6,
    scalaJSModuleKind := ModuleKind.CommonJSModule
    //        artifactPath in (Compile, fastOptJS) := baseDirectory.value / "target" / "artifact" /"high-level.js"
  )
  .disablePlugins(SonarPlugin)

lazy val lspJVM = lsp.jvm.in(file("./als-lsp/jvm"))
lazy val lspJS  = lsp.js.in(file("./als-lsp/js"))
////endregion

////region ALS-SUGGESTIONS
/** ALS suggestions */

lazy val suggestions = crossProject(JSPlatform, JVMPlatform)
  .settings(
    Seq(
      name := "als-suggestions"
    )
  )
  .dependsOn(common % "compile->compile;test->test")
  .in(file("./als-suggestions"))
  .settings(settings: _*)
  .jsSettings(
    packageJSDependencies / skip := false,
    scalaJSOutputMode            := OutputMode.Defaults,
    scalaJSModuleKind            := ModuleKind.CommonJSModule,
    npmDependencies ++= commonNpmDependencies
  )
  .disablePlugins(SonarPlugin)

lazy val suggestionsJVM = suggestions.jvm.in(file("./als-suggestions/jvm"))
lazy val suggestionsJS  = suggestions.js.in(file("./als-suggestions/js")).disablePlugins(SonarPlugin)
////endregion

////region ALS-STRUCTURE
/** ALS structure */

lazy val pathAlsStructure = "./als-structure"
lazy val structure = crossProject(JSPlatform, JVMPlatform)
  .settings(
    Seq(
      name := "als-structure"
    )
  )
  .dependsOn(common % "compile->compile;test->test")
  .in(file(s"$pathAlsStructure"))
  .settings(settings: _*)
  .jsSettings(
    libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.9.2",
    libraryDependencies += "com.lihaoyi"  %%% "upickle"     % "0.5.1",
    scalaJSOutputMode                      := org.scalajs.core.tools.linker.backend.OutputMode.ECMAScript6,
    scalaJSModuleKind                      := ModuleKind.CommonJSModule,
    npmDependencies ++= commonNpmDependencies
    //    artifactPath in (Compile, fastOptJS) := baseDirectory.value / "target" / "artifact" /"als-suggestions.js"
  )
  .disablePlugins(SonarPlugin)

lazy val structureJVM = structure.jvm.in(file(s"$pathAlsStructure/jvm"))
lazy val structureJS  = structure.js.in(file(s"$pathAlsStructure/js")).disablePlugins(SonarPlugin)
////endregion

////region ALS-ACTIONS
/** ALS actions */

lazy val pathAlsActions = "./als-actions"
lazy val actions = crossProject(JSPlatform, JVMPlatform)
  .settings(name := "als-actions")
  .settings(libraryDependencies += "org.wvlet.airframe" %% "airframe" % "19.3.7")
  .dependsOn(common % "compile->compile;test->test")
  .in(file(s"$pathAlsActions"))
  .settings(settings: _*)
  .jsSettings(
    packageJSDependencies / skip := false,
    scalaJSOutputMode            := OutputMode.Defaults,
    scalaJSModuleKind            := ModuleKind.CommonJSModule,
    npmDependencies ++= commonNpmDependencies
  )
  .disablePlugins(SonarPlugin)

lazy val actionsJVM = actions.jvm.in(file(s"$pathAlsActions/jvm"))
lazy val actionsJS  = actions.js.in(file(s"$pathAlsActions/js")).disablePlugins(SonarPlugin)

////endregion

////region ALS-SERVER
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
    Compile / packageBin / packageOptions += Package.ManifestAttributes("Automatic-Module-Name" → "org.mule.als"),
    assembly / aggregate := true,
    assembly / mainClass := Some("org.mulesoft.als.server.lsp4j.Main"),
    Compile / mainClass  := Some("org.mulesoft.als.server.lsp4j.Main"),
    scalacOptions += "-Xmixin-force-forwarders:false",
    assembly / assemblyMergeStrategy := {
      case PathList("META-INF", xs @ _*) => MergeStrategy.discard
      case x                             => MergeStrategy.first
    }
  )
  .jsSettings(
    installJsDependencies := {
      Process(
        s"npm install -E $npmDependencyAmfCustomValidatorWeb $npmDependencyAmfAntlr",
        new File("./als-server/js/node-package")
      ) #&&
        Process("npm install", new File("./als-server/js/node-package")) !
    },
    Test / test                            := ((Test / test) dependsOn installJsDependencies).value,
    Test / fastOptJS / artifactPath        := baseDirectory.value / "node-package" / "tmp" / "als-server.js",
    scalaJSModuleKind                      := ModuleKind.CommonJSModule,
    libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.9.2",
    Compile / fastOptJS / artifactPath     := baseDirectory.value / "node-package" / "lib" / "als-server.js",
    Compile / fullOptJS / artifactPath     := baseDirectory.value / "node-package" / "lib" / "als-server.min.js"
  )

lazy val serverJVM = server.jvm.in(file("./als-server/jvm"))
lazy val serverJS  = server.js.in(file("./als-server/js")).disablePlugins(SonarPlugin)
////endregion

////region ALS-NODE-CLIENT
/** ALS node client */
val npmIClient = TaskKey[Unit]("npmIClient", "Install npm at node client")

lazy val nodeClient = project
  .dependsOn(serverJS % "compile->compile;test->test")
  .in(file("./als-node-client"))
  .enablePlugins(ScalaJSPlugin)
  .settings(settings: _*)
  .disablePlugins(SonarPlugin)
  .settings(
    settings ++ Seq(
      name                                 := "als-node-client",
      scalaJSUseMainModuleInitializer      := true,
      scalaJSModuleKind                    := ModuleKind.CommonJSModule,
      libraryDependencies += "io.scalajs" %%% "nodejs-core" % "0.4.2",
      Compile / mainClass                  := Some("org.mulesoft.als.nodeclient.Main"),
      npmIClient := {
        Process(
          s"npm install -E $npmDependencyAmfCustomValidator $npmDependencyAmfCustomValidatorWeb $npmDependencyAmfAntlr",
          new File("./als-node-client/node-package/")
        ) #&&
          Process(
            s"cp -r ../../als-server/js/node-package/typescript/als-server.d.ts ./typescript/als-node-client.d.ts",
            new File("./als-node-client/node-package/")
          ) #&&
          Process(
            s"sed -i.bk s/@aml-org\\/als-server/@aml-org\\/als-node-client/ als-node-client.d.ts",
            new File("./als-node-client/node-package/typescript/")
          ) #&&
          Process(s"rm als-node-client.d.ts.bk", new File("./als-node-client/node-package/typescript/")) #&&
          Process("npm i", new File("./als-node-client/node-package/")) !
      },
      Test / test                        := ((Test / test) dependsOn npmIClient).value,
      Test / fastOptJS / artifactPath    := baseDirectory.value / "node-package" / "tmp" / "als-node-client.js",
      Compile / fastOptJS / artifactPath := baseDirectory.value / "node-package" / "dist" / "als-node-client.js",
      Compile / fullOptJS / artifactPath := baseDirectory.value / "node-package" / "dist" / "als-node-client.min.js"
    )
  )
  .sourceDependency(customValidatorNodeJSRef, customValidatorNodeLibJS)
////endregion

/** ALS build tasks */

// Server library

val buildJsServerLibrary = TaskKey[Unit]("buildJsServerLibrary", "Build server library")

buildJsServerLibrary := {
  (serverJS / Compile / fastOptJS).value
  (serverJS / Compile / fullOptJS).value
  (serverJS / installJsDependencies).value
}

// Node client
val buildNodeJsClient = TaskKey[Unit]("buildNodeJsClient", "Build node client")

buildNodeJsClient := {
  (nodeClient / Compile / fastOptJS).value
  (nodeClient / Compile / fullOptJS).value
  (nodeClient / npmIClient).value
}

// ************** SONAR *******************************
lazy val token  = sys.env.getOrElse("SONAR_SERVER_TOKEN", "Not found token.")
lazy val branch = sys.env.getOrElse("BRANCH_NAME", "devel")

sonarProperties ++= Map(
  "sonar.login"             -> token,
  "sonar.projectKey"        -> "mulesoft.als",
  "sonar.projectName"       -> "ALS",
  "sonar.projectVersion"    -> "1.0.0",
  "sonar.sourceEncoding"    -> "UTF-8",
  "sonar.github.repository" -> "aml-org/als",
  "sonar.branch.name"       -> branch,
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

assembly / assemblyMergeStrategy := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x                             => MergeStrategy.first
}

//******* fat jar*****************************

lazy val fat = crossProject(JVMPlatform)
  .settings(
    Seq(
      name := "api-language-server"
    )
  )
  .dependsOn(suggestions, structure, server)
  .disablePlugins(SonarPlugin)
  .enablePlugins(AssemblyPlugin)
  .in(file("./als-fat"))
  .settings(settings: _*)
  .jvmSettings(
    Compile / packageBin / packageOptions += Package.ManifestAttributes("Automatic-Module-Name" → "org.mule.als"),
    assembly / aggregate                   := true,
    Compile / packageBin / publishArtifact := false,
    addArtifact(Artifact("api-language-server", ""), assembly),
    assembly / assemblyMergeStrategy := {
      case x if x.toString.contains("commons/logging")             => MergeStrategy.discard
      case x if x.toString.endsWith("JS_DEPENDENCIES")             => MergeStrategy.discard
      case PathList(ps @ _*) if ps.last endsWith "JS_DEPENDENCIES" => MergeStrategy.discard
      case PathList("META-INF", "MANIFEST.MF")                     => MergeStrategy.discard
      case x                                                       => MergeStrategy.first
    }
  )

lazy val coreJVM = fat.jvm.in(file("./als-fat/jvm")).disablePlugins(SonarPlugin)
