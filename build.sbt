import org.scalajs.core.tools.linker.ModuleKind
import sbt.Keys.{libraryDependencies, resolvers}

name := "api-language-server"

version := "0.3"

scalaVersion := "2.12.2"

publish := {}

jsEnv := new org.scalajs.jsenv.nodejs.NodeJSEnv()

val settings =  Common.settings ++ Common.publish ++ Seq(
    organization := "test",
    
    resolvers ++= List(
        Common.releases,
        Common.snapshots,
        Resolver.mavenLocal/*,
        Resolver.sonatypeRepo("releases"),
        Resolver.sonatypeRepo("snapshots")*/
    ),
    
    credentials ++= Common.credentials(),
    
    libraryDependencies ++= Seq(
        "com.github.amlorg" %%% "amf-webapi" % "1.5.0",
        "com.github.amlorg" %%% "amf-core" % "1.5.0",
        "com.github.amlorg" %%% "amf-client" % "1.5.0",
        "com.github.amlorg" %%% "amf-aml" % "1.5.0",
        "org.mule.common" %%% "scala-common" % "0.1.3",
        "org.mule.syaml" %%% "syaml" % "0.2.0",
        "org.scalatest"    %%% "scalatest" % "3.0.0" % Test,
        "com.chuusai" %% "shapeless" % "2.3.3",
        "org.mule.amf" %%% "typesystem-project" % "0.2.0-SNAPSHOT",
        "org.mule.amf" %%% "als-suggestions" % "0.4.0-SNAPSHOT",
        "org.mule.amf" %%% "als-outline" % "0.1.0-SNAPSHOT"
    )
)

lazy val sharedProject = (project in file("shared")).settings(settings: _*).settings(name := "api-language-server-shared-project")

lazy val core = crossProject.settings(
    Seq(
        name := "api-language-server"
    )
).in(file(".")).settings(settings: _*).jvmSettings(
    assemblyMergeStrategy in assembly := {
        case x if x.toString.endsWith("JS_DEPENDENCIES")             => MergeStrategy.discard
        case PathList(ps @ _*) if ps.last endsWith "JS_DEPENDENCIES" => MergeStrategy.discard
        case PathList("META-INF", "MANIFEST.MF") => MergeStrategy.discard
        case x => {
            MergeStrategy.first
        }
    },
    assemblyJarName in assembly := "server.jar"
).jsSettings(
    libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.9.2",
    libraryDependencies += "com.lihaoyi" %%% "upickle" % "0.5.1",
    scalaJSOutputMode := org.scalajs.core.tools.linker.backend.OutputMode.ECMAScript6,
    scalaJSModuleKind := ModuleKind.CommonJSModule,
    scalaJSUseMainModuleInitializer := true,
    mainClass in Compile := Some("org.mulesoft.language.client.js.ServerProcess"),
    artifactPath in (Compile, fastOptJS) := baseDirectory.value / "target" / "artifact" /"serverProcess.js"
)

//lazy val core_mslsp = crossProject.settings(
//    Seq(
//        name := "api-language-server-mslsp",
//
//        libraryDependencies += "org.mule.syaml" %%% "syaml" % "0.1.3"
//    )
//).in(file(".")).settings(settings: _*).jvmSettings(
//    libraryDependencies += "org.scala-js"           %% "scalajs-stubs"          % scalaJSVersion % "provided"
//).jsSettings(
//    libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.9.2",
//    libraryDependencies += "com.lihaoyi" %%% "upickle" % "0.5.1",
//    scalaJSOutputMode := org.scalajs.core.tools.linker.backend.OutputMode.ECMAScript6,
//    scalaJSModuleKind := ModuleKind.CommonJSModule,
//    scalaJSUseMainModuleInitializer := true,
//    mainClass in Compile := Some("org.mulesoft.language.server.js.Main"),
//    artifactPath in (Compile, fastOptJS) := baseDirectory.value / "target" / "artifact" /"serverProcess.js"
//)

//lazy val core_weblsp = crossProject.settings(
//    Seq(
//        name := "api-language-server-weblsp",
//
//        libraryDependencies += "org.mule.syaml" %%% "syaml" % "0.1.3"
//    )
//).in(file(".")).settings(settings: _*).jvmSettings(
//    libraryDependencies += "org.scala-js"           %% "scalajs-stubs"          % scalaJSVersion % "provided"
//).jsSettings(
//    libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.9.2",
//    libraryDependencies += "com.lihaoyi" %%% "upickle" % "0.5.1",
//    scalaJSOutputMode := org.scalajs.core.tools.linker.backend.OutputMode.ECMAScript6,
//    scalaJSModuleKind := ModuleKind.CommonJSModule,
//    scalaJSUseMainModuleInitializer := true,
//    mainClass in Compile := Some("org.mulesoft.language.server.js.Main"),
//    artifactPath in (Compile, fastOptJS) := baseDirectory.value / "target" / "artifact" /"serverProcess.js"
//)

lazy val coreJVM = core.jvm.in(file("./jvm")).dependsOn(sharedProject)
lazy val coreJS  = core.js.in(file("./js")).dependsOn(sharedProject)

//lazy val coreJVM_MSLSP = core_mslsp.jvm.in(file("./jvm_mslsp")).dependsOn(sharedProject)
//lazy val coreJS_MSLSP  = core_mslsp.js.in(file("./js_mslsp")).dependsOn(sharedProject)
//
//lazy val coreJVM_WEBLSP = core_weblsp.jvm.in(file("./jvm_weblsp")).dependsOn(sharedProject)
//lazy val coreJS_WEBLSP  = core_weblsp.js.in(file("./js_weblsp")).dependsOn(sharedProject)

val buildJS = TaskKey[Unit]("buildJS")
val buildJS_MSLSP = TaskKey[Unit]("buildJSMSLSP")
val buildJS_WEBLSP = TaskKey[Unit]("buildJSWEBLSP")

buildJS := {
    val _ = (fastOptJS in Compile in coreJS).value
}

//buildJS_MSLSP := {
//    val _ = (fastOptJS in Compile in coreJS_MSLSP).value
//}
//
//buildJS_WEBLSP := {
//    val _ = (fastOptJS in Compile in coreJS_WEBLSP).value
//}

mainClass in Compile := Some("org.mulesoft.language.client.js.Main")

