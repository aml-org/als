import Dependencies.deps
import NpmOpsPlugin.autoImport.npmDependencies
import sbt.File
import sbt.Keys.{libraryDependencies, mainClass, packageOptions}
import sbtcrossproject.CrossPlugin.autoImport.crossProject
import org.scalajs.linker.interface.ESVersion

import scala.language.postfixOps
import scala.sys.process.Process

name := "api-language-server"

ThisBuild / scalaVersion := "2.12.13"

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
val scalaJSVersion                   = "1.1.0"

val commonNpmDependencies = List(
  ("ajv", "6.12.6"),
  ("@aml-org/amf-antlr-parsers", amfAntlrParsersVersion)
)

lazy val amfJVMRef = ProjectRef(workspaceDirectory / "amf", "graphqlJVM")
lazy val amfJSRef  = ProjectRef(workspaceDirectory / "amf", "graphqlJS")
lazy val amfLibJVM = "com.github.amlorg" %% "amf-graphql"        % amfVersion
lazy val amfLibJS  = "com.github.amlorg" %% "amf-graphql_sjs1" % amfVersion

lazy val customValidatorWebJVMRef =
  ProjectRef(workspaceDirectory / "amf-custom-validator-scalajs", "amfCustomValidatorWebJVM")
lazy val customValidatorWebJSRef =
  ProjectRef(workspaceDirectory / "amf-custom-validator-scalajs", "amfCustomValidatorWebJS")
lazy val customValidatorWebLibJVM = "com.github.amlorg" %% "amf-custom-validator-web" % amfCustomValidatorScalaJSVersion
lazy val customValidatorWebLibJS =
  "com.github.amlorg" %% "amf-custom-validator-web_sjs1" % amfCustomValidatorScalaJSVersion
lazy val customValidatorNodeJVMRef =
  ProjectRef(workspaceDirectory / "amf-custom-validator-scalajs", "amfCustomValidatorNodeJVM")
lazy val customValidatorNodeJSRef =
  ProjectRef(workspaceDirectory / "amf-custom-validator-scalajs", "amfCustomValidatorNodeJS")
lazy val customValidatorNodeLibJVM =
  "com.github.amlorg" %% "amf-custom-validator-node" % amfCustomValidatorScalaJSVersion
lazy val customValidatorNodeLibJS =
  "com.github.amlorg" %% "amf-custom-validator-node_sjs1" % amfCustomValidatorScalaJSVersion

lazy val npmDependencyAmfCustomValidatorWeb = s"@aml-org/amf-custom-validator-web@$amfCustomValidatorJSVersion"
lazy val npmDependencyAmfCustomValidator    = s"@aml-org/amf-custom-validator@$amfCustomValidatorJSVersion"
lazy val npmDependencyAmfAntlr              = s"@aml-org/amf-antlr-parsers@$amfAntlrParsersVersion"

////region SBT-Dependencies
lazy val scalaJS_DomDependency = ModuleID("org.scala-js", "scalajs-dom_sjs1_2.12" , "1.1.0")
lazy val upickle_Dependency = ModuleID("com.lihaoyi", "upickle_sjs1_2.12", "0.9.9")
lazy val scalaJS_NodeDependency = ModuleID("net.exoego", "scala-js-nodejs-v14_sjs1_2.12", "0.13.0")
////endregion

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
    "org.scalatest" %%% "scalatest"     % "3.2.0"        % Test,
    upickle_Dependency
  )
)

val settings = Common.settings ++ Common.publish ++ orgSettings
concurrentRestrictions in Global := Seq(Tags.limitAll(2))

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
  .jsSettings(
    installJsDependenciesWeb := {
      Process(
        s"npm install -E $npmDependencyAmfAntlr",
        new File(s"$pathAlsCommon/js/")
      ) #&&
        Process("npm install", new File(s"$pathAlsCommon/js/")) !
    },
    scalaJSLinkerConfig  ~= { _
      .withModuleKind(ModuleKind.CommonJSModule)
      .withESFeatures(_.withESVersion(ESVersion.ES2016))
    },
    npmDependencies      ++= commonNpmDependencies
//    ,scalaJSLinkerOutputDirectory in (Compile, fastOptJS) := baseDirectory.value / "target" / "artifact" /"high-level.js"
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
  .disablePlugins(SonarPlugin, ScoverageSbtPlugin)
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
    scalaJSLinkerConfig ~= { _
      .withModuleKind(ModuleKind.CommonJSModule)
      .withESFeatures(_.withESVersion(ESVersion.ES2016))
    }
    //        scalaJSLinkerOutputDirectory in (Compile, fastOptJS) := baseDirectory.value / "target" / "artifact" /"high-level.js"
  )
  .disablePlugins(SonarPlugin)

lazy val lspJVM = lsp.jvm.in(file("./als-lsp/jvm"))
lazy val lspJS  = lsp.js.in(file("./als-lsp/js")).disablePlugins(ScoverageSbtPlugin)
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
    libraryDependencies += upickle_Dependency,
//    packageJSDependencies / skip := false,
    scalaJSLinkerConfig          ~= { _
      .withModuleKind(ModuleKind.CommonJSModule)
      .withESFeatures(_.withESVersion(ESVersion.ES2016))
    },
    npmDependencies ++= commonNpmDependencies
  )
  .disablePlugins(SonarPlugin)

lazy val suggestionsJVM = suggestions.jvm.in(file("./als-suggestions/jvm"))
lazy val suggestionsJS = suggestions.js.in(file("./als-suggestions/js")).disablePlugins(SonarPlugin, ScoverageSbtPlugin)
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
    libraryDependencies ++= Seq(
      scalaJS_DomDependency,
      upickle_Dependency
    ),
    scalaJSLinkerConfig ~= { _
      .withModuleKind(ModuleKind.CommonJSModule)
      .withESFeatures(_.withESVersion(ESVersion.ES2016))
    },
    npmDependencies ++= commonNpmDependencies
  )
  .disablePlugins(SonarPlugin)

lazy val structureJVM = structure.jvm.in(file(s"$pathAlsStructure/jvm"))
lazy val structureJS  = structure.js.in(file(s"$pathAlsStructure/js")).disablePlugins(SonarPlugin, ScoverageSbtPlugin)
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
    scalaJSLinkerConfig ~= { _
      .withModuleKind(ModuleKind.CommonJSModule)
      .withESFeatures(_.withESVersion(ESVersion.ES2016))
    },
    npmDependencies ++= commonNpmDependencies
  )
  .disablePlugins(SonarPlugin)

lazy val actionsJVM = actions.jvm.in(file(s"$pathAlsActions/jvm"))
lazy val actionsJS  = actions.js.in(file(s"$pathAlsActions/js")).disablePlugins(SonarPlugin, ScoverageSbtPlugin)

////endregion

////region ALS-SERVER
/** ALS server */

val installJsDependenciesWeb = TaskKey[Unit]("installJsDependenciesWeb", "Runs npm i")
val installJsDependencies = TaskKey[Unit]("installJsDependencies", "Runs npm i but switches web for node dependency (tests only)")

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
    installJsDependenciesWeb := {
      Process(
        s"npm uninstall @aml-org/amf-custom-validator",
        new File("./als-server/js/node-package")
      ) #&&
      Process(
        s"npm install -E $npmDependencyAmfCustomValidatorWeb $npmDependencyAmfAntlr",
        new File("./als-server/js/node-package")
      ) #&&
        Process("npm install", new File("./als-server/js/node-package")) !
    },
    installJsDependencies := {
        Process(
          s"npm install -E $npmDependencyAmfCustomValidator $npmDependencyAmfCustomValidatorWeb $npmDependencyAmfAntlr",
          new File("./als-server/js/node-package")
        ) #&&
        Process("npm install", new File("./als-server/js/node-package")) !
    },
    Test / test                                       := ((Test / test) dependsOn installJsDependencies).value,
    Test / fastLinkJS / scalaJSLinkerOutputDirectory  := baseDirectory.value / "node-package" / "tmp" / "als-server.js",
    scalaJSLinkerConfig                    ~= { _
      .withModuleKind(ModuleKind.CommonJSModule)
      .withESFeatures(_.withESVersion(ESVersion.ES2016))
    },
    libraryDependencies ++= Seq(
      scalaJS_NodeDependency,
      scalaJS_DomDependency
    ),
    Compile / fastLinkJS / scalaJSLinkerOutputDirectory := baseDirectory.value / "node-package" / "tmp" / "als-server.js",
    Compile / fullLinkJS / scalaJSLinkerOutputDirectory := baseDirectory.value / "node-package" / "tmp" / "als-server.min.js"
  )

lazy val serverJVM = server.jvm.in(file("./als-server/jvm"))
lazy val serverJS  = server.js.in(file("./als-server/js"))
  .disablePlugins(SonarPlugin, ScoverageSbtPlugin)
  .sourceDependency(customValidatorNodeJSRef, customValidatorNodeLibJS)
////endregion

////region ALS-NODE-CLIENT
/** ALS node client */
val npmIClient = TaskKey[Unit]("npmIClient", "Install npm at node client")

lazy val nodeClient = project
  .dependsOn(serverJS % "compile->compile;test->test")
  .in(file("./als-node-client"))
  .enablePlugins(ScalaJSPlugin)
  .settings(settings: _*)
  .disablePlugins(SonarPlugin, ScoverageSbtPlugin)
  .settings(
    settings ++ Seq(
      name                                 := "als-node-client",
      scalaJSUseMainModuleInitializer      := true,
      scalaJSLinkerConfig                  ~= { _
        .withModuleKind(ModuleKind.CommonJSModule)
        .withESFeatures(_.withESVersion(ESVersion.ES2016))
      },
      libraryDependencies += scalaJS_NodeDependency,
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
      Test / fastLinkJS / scalaJSLinkerOutputDirectory := baseDirectory.value / "node-package" / "tmp" / "als-node-client.js",
      Compile / fullLinkJS / scalaJSLinkerOutputDirectory := baseDirectory.value / "node-package" / "tmp" / "als-node-client.min.js",
      Compile / fastLinkJS / scalaJSLinkerOutputDirectory := baseDirectory.value / "node-package" / "tmp" / "als-node-client.js"
    )
  )
  .sourceDependency(customValidatorNodeJSRef, customValidatorNodeLibJS)
////endregion

/** ALS build tasks */

// Server library


// in order to test using sbt (which runs on jsEnv node) we need to install the node package of custom-validator
// building the server library we package the -web version because it is meant to run in browser
// beware that this means the -web version is not tested and if there is any bug in this asset, it will be passed on runtime
val buildJsServerLibrary = TaskKey[Unit]("buildJsServerLibrary", "Build server library")

buildJsServerLibrary := {
  (serverJS / installJsDependenciesWeb).value
  (serverJS / Compile / fastLinkJS).value
  (serverJS / Compile / fullLinkJS).value
  val result = (Process(
    "./scripts/build.sh",
    new File("./als-server/js/node-package")
  ).!)
  if (result != 0) throw new IllegalStateException("Node JS build.sh failed")
}

// Node client
val buildNodeJsClient = TaskKey[Unit]("buildNodeJsClient", "Build node client")

buildNodeJsClient := {
  (nodeClient / Compile / fastLinkJS).value
  (nodeClient / Compile / fullLinkJS).value
  (nodeClient / npmIClient).value
  val result = (Process(
    "./scripts/build.sh",
    new File("./als-node-client/node-package")
  ).!)
  if (result != 0) throw new IllegalStateException("Node JS build.sh failed")
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
